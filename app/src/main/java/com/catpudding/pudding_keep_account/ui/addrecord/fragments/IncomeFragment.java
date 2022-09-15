package com.catpudding.pudding_keep_account.ui.addrecord.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.databinding.FragmentIncomeBinding;
import com.catpudding.pudding_keep_account.ui.addrecord.utils.GridViewAdapter;
import com.catpudding.pudding_keep_account.ui.login.LoginActivity;
import com.catpudding.pudding_keep_account.utils.BaseConfiguration;
import com.catpudding.pudding_keep_account.utils.IconList;
import com.catpudding.pudding_keep_account.utils.MyIcon;
import com.catpudding.pudding_keep_account.utils.RequestWithAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IncomeFragment extends Fragment {


    FragmentIncomeBinding binding;
    IncomeViewModel incomeViewModel;
    private List<MyIcon> incomeIconList;
    private RequestQueue requestQueue;
    private String addDate;

    public IncomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentIncomeBinding.inflate(inflater);
        incomeViewModel = new ViewModelProvider(getActivity()).get(IncomeViewModel.class);
        View root = binding.getRoot();
        incomeIconList = new ArrayList<>();
        for(int i = 0; i< IconList.MY_ICON_LIST.length; i++){
            if(IconList.MY_ICON_LIST[i].getType() == 2){
                incomeIconList.add(IconList.MY_ICON_LIST[i]);
            }
        }
        GridViewAdapter gridViewAdapter = new GridViewAdapter(getActivity(), incomeIconList);
        binding.icomGridView.setAdapter(gridViewAdapter);
        requestQueue = Volley.newRequestQueue(getContext());

        addListener();
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_income, container, false);
        return root;
    }

    private void addListener(){
        binding.icomGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Dialog dialog = new Dialog(getActivity());
                View root = LayoutInflater.from(getActivity()).inflate(R.layout.add_record_item,null);
                ImageView imageView = root.findViewById(R.id.icon_selected);
                imageView.setImageResource(incomeIconList.get(i).getSource());

                TextView textView = root.findViewById(R.id.add_item_title);
                textView.setText(incomeIconList.get(i).getName());

                TextView textView1 = root.findViewById(R.id.cancel_btn_add_item);
                textView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                CalendarView calendarView = root.findViewById(R.id.calendarView);
                EditText price_input = root.findViewById(R.id.input_price);
                EditText desc_input = root.findViewById(R.id.input_desc);
                TextView confirm_btn = root.findViewById(R.id.confirm_btn);
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                addDate = sf.format(new Date(calendarView.getDate()));
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                        Date date = new Date();
                        date.setYear(i-1900);
                        date.setMonth(i1);
                        date.setDate(i2);
                        addDate = sf.format(date);
                    }
                });
                confirm_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("describeBill",desc_input.getText());
                            jsonObject.put("price",Double.parseDouble(String.valueOf(price_input.getText())));
                            jsonObject.put("priceType",1);
                            int typeKey = 0;
                            for(;typeKey < IconList.MY_ICON_LIST.length;typeKey++){
                                if(IconList.MY_ICON_LIST[typeKey].getSource() == incomeIconList.get(i).getSource()){
                                    break;
                                }
                            }
                            jsonObject.put("type",typeKey);
                            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                            jsonObject.put("createDate",addDate);
                            addBill(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dialog.cancel();
                    }
                });

                Window window = dialog.getWindow();
                window.setContentView(root);
                window.setBackgroundDrawable(null);
                window.setBackgroundDrawableResource(R.color.white);
                window.setLayout(WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                window.setWindowAnimations(R.style.AnimBottom);
                dialog.show();
            }
        });
    }

    /**
     * 添加账单信息
     * @param data
     */
    private void addBill(JSONObject data){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String url = BaseConfiguration.BASE_URL + "bill/addBill";
        RequestWithAuth requestWithAuth = new RequestWithAuth(
                Request.Method.POST,
                url,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("code") == 200){
                                Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }else{
                                Toast.makeText(getActivity(), "添加失败", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = new String(error.networkResponse.data);
                        Log.d("requestTest",message);
                        try {
                            JSONObject jsonObject = new JSONObject(message);
                            String errorMsg = jsonObject.getString("message");
                            if(errorMsg.equals("Token失效，请重新登录")){
                                Intent intent = new Intent();
                                intent.setClass(getActivity(), LoginActivity.class);
                                getActivity().startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                sharedPreferences.getString("token","")
        );
        requestQueue.add(requestWithAuth);
    }
}
package com.catpudding.pudding_keep_account.ui.addrecord.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.catpudding.pudding_keep_account.databinding.FragmentExpenditureBinding;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ExpenditureFragment extends Fragment {

    private FragmentExpenditureBinding binding;
    private List<MyIcon> expenditureIconList;
    private RequestQueue requestQueue;
    private String addDate;

    public ExpenditureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentExpenditureBinding.inflate(inflater,container,false);
        View root = binding.getRoot();
        expenditureIconList = new ArrayList<>();
        for(int i = 0;i<IconList.MY_ICON_LIST.length;i++){
            if(IconList.MY_ICON_LIST[i].getType() == 1){
                expenditureIconList.add(IconList.MY_ICON_LIST[i]);
            }
        }
        GridViewAdapter gridViewAdapter = new GridViewAdapter(getActivity(), expenditureIconList);
        binding.addRecordGrid.setAdapter(gridViewAdapter);
        requestQueue = Volley.newRequestQueue(getContext());

        addListener();
        return root;
    }

    private void addListener(){
        binding.addRecordGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Dialog dialog = new Dialog(getActivity());
                View root = LayoutInflater.from(getActivity()).inflate(R.layout.add_record_item,null);
                ImageView imageView = root.findViewById(R.id.icon_selected);
                imageView.setImageResource(expenditureIconList.get(i).getSource());

                TextView textView = root.findViewById(R.id.add_item_title);
                textView.setText(expenditureIconList.get(i).getName());

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
                            jsonObject.put("priceType",0);
                            int typeKey = 0;
                            for(;typeKey < IconList.MY_ICON_LIST.length;typeKey++){
                                if(IconList.MY_ICON_LIST[typeKey].getSource() == expenditureIconList.get(i).getSource()){
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
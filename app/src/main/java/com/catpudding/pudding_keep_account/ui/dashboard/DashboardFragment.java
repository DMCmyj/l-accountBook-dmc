package com.catpudding.pudding_keep_account.ui.dashboard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.databinding.FragmentDashboardBinding;
import com.catpudding.pudding_keep_account.ui.dashboard.utils.BillSecondListAdapter;
import com.catpudding.pudding_keep_account.ui.dashboard.utils.MonthBill;
import com.catpudding.pudding_keep_account.ui.monthBill.MonthBillActivity;
import com.catpudding.pudding_keep_account.utils.BaseConfiguration;
import com.catpudding.pudding_keep_account.utils.RequestWithAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    DashboardViewModel dashboardViewModel;
    private RequestQueue requestQueue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.setData(dashboardViewModel);
        binding.setLifecycleOwner(getActivity());

        requestQueue = Volley.newRequestQueue(getContext());
        setListener();
        setObserve();
        return root;
    }

    private void setObserve(){
        dashboardViewModel.getCurrentYear().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.currentYear.setText(integer + "年");
                getComputeData();
            }
        });
        dashboardViewModel.getMonthBills().observe(getActivity(), new Observer<List<MonthBill>>() {
            @Override
            public void onChanged(List<MonthBill> monthBills) {
                BillSecondListAdapter billSecondListAdapter = new BillSecondListAdapter(getContext(),monthBills);
                binding.listSecondBill.setAdapter(billSecondListAdapter);
                binding.listSecondBill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent();
                        intent.setClass(getContext(), MonthBillActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("year",dashboardViewModel.getCurrentYear().getValue());
                        bundle.putInt("month",i+1);
                        bundle.putDouble("income",monthBills.get(i).getIncome());
                        bundle.putDouble("pay",monthBills.get(i).getPay());
                        bundle.putDouble("left",monthBills.get(i).getLeft());
                        float[] monthData = new float[12];
                        for(int item = 0;item<12;item++){
                            monthData[item] = (float) monthBills.get(item).getPay();
                        }
                        bundle.putFloatArray("monthData",monthData);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void setListener(){
        binding.currentYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyYearPicker();
            }
        });

        binding.selectYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyYearPicker();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * 获取一年的统计数据
     */
    public void getComputeData(){
        String url = BaseConfiguration.BASE_URL + "bill/getComputeData";
        JSONObject data = new JSONObject();
        try {
            data.put("num",dashboardViewModel.getCurrentYear().getValue());
            data.put("type",3);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);

        RequestWithAuth requestWithAuth = new RequestWithAuth(
                Request.Method.POST,
                url,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
//                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                handleYearData(response.getJSONArray("result"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
                sharedPreferences.getString("token","")
        );
        requestQueue.add(requestWithAuth);
    }

    private void handleYearData(JSONArray result){
        List<MonthBill> monthBills = new ArrayList<>();
        double totalIncome = 0;
        double totalPay = 0;
        for(int i = 1;i<=12;i++){
            MonthBill monthBill = new MonthBill();
            monthBill.setMonth(i);
            String date = dashboardViewModel.getCurrentYear().getValue() + "-" + (i<10?"0"+i:i+"");
            System.out.println(date);
            for(int j = 0;j < result.length();j++){
                try {
                    JSONObject item = result.getJSONObject(j);
                    String itemDate = item.getString("createDate");
                    if(itemDate.indexOf(date) != -1){
                        if(item.getInt("priceType") == 0){
                            monthBill.setPay(monthBill.getPay() + item.getDouble("price"));
                            totalPay += item.getDouble("price");
                        }else{
                            monthBill.setIncome(monthBill.getIncome() + item.getDouble("price"));
                            totalIncome += item.getDouble("price");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            monthBill.setLeft(monthBill.getIncome() - monthBill.getPay());
            monthBills.add(monthBill);
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        dashboardViewModel.getMonthBills().postValue(monthBills);
        System.out.println(totalIncome);
        System.out.println(totalPay);
        dashboardViewModel.getTotalIncome().postValue(decimalFormat.format(totalIncome));
        dashboardViewModel.getTotalPay().postValue(decimalFormat.format(totalPay));
        dashboardViewModel.getTotalLeft().postValue(decimalFormat.format(totalIncome - totalPay));
    }

    /**
     * 显示底部弹出框
     */
    private void showMyYearPicker(){
        Calendar calendar = Calendar.getInstance();
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.my_year_picker,null);
        NumberPicker yearPicker = root.findViewById(R.id.number_picker_year);
        ImageView okBtn = root.findViewById(R.id.change_month_ok);

        int year = dashboardViewModel.getCurrentYear().getValue();

        //设置年份范围
//        int yearNow = calendar.get(Calendar.YEAR);
        yearPicker.setMinValue(year-10);
        yearPicker.setMaxValue(year+10);
        yearPicker.setValue(year);
        yearPicker.setWrapSelectorWheel(false);//关闭循环


        Dialog dialog = new Dialog(getActivity());

        Window window = dialog.getWindow();
        window.setContentView(root);
        window.setBackgroundDrawable(null);
        window.setBackgroundDrawableResource(R.color.white);
        window.setLayout(WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setWindowAnimations(R.style.AnimBottom);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.y = 0;
        lp.x = 0;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.horizontalMargin = 0;
        lp.verticalMargin = 0;
        window.setAttributes(lp);
        dialog.show();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dashboardViewModel.getCurrentYear().setValue(yearPicker.getValue());
                dialog.cancel();
            }
        });
    }
}
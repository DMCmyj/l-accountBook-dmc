package com.catpudding.pudding_keep_account.ui.monthBill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.databinding.ActivityMonthBillBinding;
import com.catpudding.pudding_keep_account.ui.monthBill.utils.BillRankAdapter;
import com.catpudding.pudding_keep_account.ui.monthBill.utils.MyChartOneClient;
import com.catpudding.pudding_keep_account.ui.monthBill.utils.MyChartThreeClient;
import com.catpudding.pudding_keep_account.ui.monthBill.utils.model.ChartOneDataModel;
import com.catpudding.pudding_keep_account.ui.monthBill.utils.MyChartTwoClient;
import com.catpudding.pudding_keep_account.ui.monthBill.utils.model.MonthRankItem;
import com.catpudding.pudding_keep_account.utils.BaseConfiguration;
import com.catpudding.pudding_keep_account.utils.IconList;
import com.catpudding.pudding_keep_account.utils.RequestWithAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MonthBillActivity extends AppCompatActivity {

    ActivityMonthBillBinding binding;
    MonthBillViewModel monthBillViewModel;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        monthBillViewModel = new ViewModelProvider(this).get(MonthBillViewModel.class);
        binding = ActivityMonthBillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setData(monthBillViewModel);
        binding.setLifecycleOwner(this);
        binding.chartOne.getSettings().setJavaScriptEnabled(true);
        binding.chartTwo.getSettings().setJavaScriptEnabled(true);
        binding.chartThree.getSettings().setJavaScriptEnabled(true);
        binding.chartOne.loadUrl("file:///android_asset/month_bill_chart_1.html");
        binding.chartTwo.loadUrl("file:///android_asset/month_bill_chart_2.html");
        binding.chartThree.loadUrl("file:///android_asset/month_bill_chart_3.html");

        requestQueue = Volley.newRequestQueue(this);

        setListener();
        initView();
    }

    private void setListener(){
        //退出该页面
        binding.imageView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 初始化视图，展示标题头像等信息
     */
    private void initView(){
        handlePartOne();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int year = bundle.getInt("year");
        int month = bundle.getInt("month");
        binding.monthBillTitle.setText(year + "年" + month + "月账单");
        SharedPreferences sharedPreferences = getSharedPreferences("user_info",MODE_PRIVATE);
        Glide.with(this)
                .load(sharedPreferences.getString("avatar",""))
                .error(R.drawable.ic_user)
                .into(binding.imageView12);
        binding.textView64.setText(sharedPreferences.getString("username","用户名"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 初始化图表数据
     */
    private void initData(){
        getComputeData();
//        ArrayList<ChartOneDataModel> dataModels = new ArrayList<>();
//        for(int i = 1;i<15;i++){
//            ChartOneDataModel chartOneDataModel = new ChartOneDataModel();
//            chartOneDataModel.setName(IconList.MY_ICON_LIST[(int)((Math.random() * 10000) % IconList.MY_ICON_LIST.length)].getName());
//            chartOneDataModel.setValue(Math.random() * 1000);
//            dataModels.add(chartOneDataModel);
//        }
//        setChartOneData(dataModels);


        ArrayList<String> xData = new ArrayList<>();
        ArrayList<Double> yData = new ArrayList<>();
        for(int i = 0;i<30;i++){
            xData.add("" + (i+1));
            yData.add(Math.random()*1000);
        }

        setChartTwoData(yData,xData,1000);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        float[] floats = bundle.getFloatArray("monthData");
        ArrayList<Double> doubles = new ArrayList<>();
        for(int i = 0;i<floats.length;i++){
            doubles.add((double)floats[i]);
        }
        setChartThreeData(doubles);
    }

    /**
     * 显示表一数据
     * @param data
     */
    public void setChartOneData(ArrayList<ChartOneDataModel> data,double max) {
        binding.chartOne.getSettings().setJavaScriptEnabled(true);
        binding.chartOne.setWebViewClient(new MyChartOneClient(data,max));
        binding.chartOne.loadUrl("file:///android_asset/month_bill_chart_1.html");
    }

    /**
     * 显示表二数据
     * @param ydata
     * @param xdata
     * @param max
     */
    public void setChartTwoData(ArrayList<Double> ydata, ArrayList<String> xdata,double max) {
        binding.chartTwo.getSettings().setJavaScriptEnabled(true);
        binding.chartTwo.setWebViewClient(new MyChartTwoClient(ydata,xdata,max));
        binding.chartTwo.loadUrl("file:///android_asset/month_bill_chart_2.html");
    }

    /**
     * 显示表三数据
     * @param ydata
     */
    public void setChartThreeData(ArrayList<Double> ydata) {
        binding.chartThree.getSettings().setJavaScriptEnabled(true);
        binding.chartThree.setWebViewClient(new MyChartThreeClient(ydata));
        binding.chartThree.loadUrl("file:///android_asset/month_bill_chart_3.html");
    }

    /**
     * 处理第一部分数据显示
     */
    private void handlePartOne(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        double income = bundle.getDouble("income");
        double pay = bundle.getDouble("pay");
        double left = bundle.getDouble("left");
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        monthBillViewModel.getMonthLeft().setValue(decimalFormat.format(left));
        monthBillViewModel.getMonthPay().setValue(decimalFormat.format(pay));
        monthBillViewModel.getMonthIncome().setValue(decimalFormat.format(income));

        WindowManager windowManager = this.getWindowManager();
        double width = windowManager.getDefaultDisplay().getWidth() * 0.7;
        if(pay == 0){
            binding.viewPay.getLayoutParams().width = 5;
            if(income != 0){
                binding.viewIncome.getLayoutParams().width = (int)width;
            }else{
                binding.viewIncome.getLayoutParams().width = 5;
            }
        }
        if(income == 0){
            binding.viewIncome.getLayoutParams().width = 5;
            if(pay != 0){
                binding.viewPay.getLayoutParams().width = (int)width;
            }else{
                binding.viewPay.getLayoutParams().width = 5;
            }
        }

        if(income != 0 && pay != 0){
            binding.viewIncome.getLayoutParams().width = (int)(width * income/(pay + income));
            binding.viewPay.getLayoutParams().width = (int)(width * pay/(pay + income));
        }
    }

    /**
     * 获取月份的统计数据
     */
    public void getComputeData(){
        String url = BaseConfiguration.BASE_URL + "bill/get_month_compute_data";
        JSONObject data = new JSONObject();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int year = bundle.getInt("year");
        int month = bundle.getInt("month");
        try {
            data.put("year",year);
            data.put("month",month);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);

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
                                handleData(response.getJSONArray("result"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                },
                sharedPreferences.getString("token","")
        );
        requestQueue.add(requestWithAuth);
    }

    /**
     * 处理获取到的数据
     * @param result
     */
    public void handleData(JSONArray result){
        //处理表一数据
        ArrayList<ChartOneDataModel> dataChartOne = new ArrayList<>();
        double max = 0;
        List<Integer> existType = new ArrayList<>();
        for(int i = 0;i < result.length();i++){
            try {
                JSONObject item = result.getJSONObject(i);
                int type = item.getInt("type");
                if(item.getInt("priceType") == 0){
                    if(existType.contains(type)){
                        for(int modelIndex = 0;modelIndex < dataChartOne.size();modelIndex++){
                            ChartOneDataModel dataItem = dataChartOne.get(modelIndex);
                            if(dataItem.getName().equals(IconList.MY_ICON_LIST[type].getName())){
                                dataItem.setValue(dataItem.getValue() + item.getDouble("price"));
                                if(max < item.getDouble("price")){
                                    max = item.getDouble("price");
                                }
                            }
                        }
                    }else{
                        ChartOneDataModel dataItem = new ChartOneDataModel();
                        dataItem.setName(IconList.MY_ICON_LIST[type].getName());
                        existType.add(type);
                        dataItem.setValue(item.getDouble("price"));
                        if(max < item.getDouble("price")){
                            max = item.getDouble("price");
                        }
                        dataChartOne.add(dataItem);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        dataChartOne.sort(new Comparator<ChartOneDataModel>() {
            @Override
            public int compare(ChartOneDataModel chartOneDataModel, ChartOneDataModel t1) {
                return (int)(t1.getValue() - chartOneDataModel.getValue());
            }
        });
        setChartOneData(dataChartOne,max);

        ArrayList<String> xData = new ArrayList<>();
        ArrayList<Double> yData = new ArrayList<>();
        double maxPay = 0;
        Calendar calendar = Calendar.getInstance();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int year = bundle.getInt("year");
        int month = bundle.getInt("month");
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month-1);
        for(int i = 1;i<calendar.getActualMaximum(Calendar.DATE);i++){
            xData.add("" + i);
            String date = year + "-" + (month < 10?"0"+month:month + "") + "-" + (i < 10?"0"+i:i + "");
            double totalPrice = 0;
            for(int index = 0;index < result.length();index++){
                try {
                    JSONObject item = result.getJSONObject(index);
                    if(item.getInt("priceType") == 0){
                        String itemDate = item.getString("createDate");
                        if(itemDate.indexOf(date) != -1){
                            totalPrice += item.getDouble("price");

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(totalPrice>maxPay){
                maxPay = totalPrice;
            }
            yData.add(totalPrice);
        }

        setChartTwoData(yData,xData,maxPay);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        monthBillViewModel.getMaxPay().postValue(decimalFormat.format(maxPay));
        monthBillViewModel.getAveragePay().postValue(decimalFormat.format(Double.parseDouble(monthBillViewModel.getMonthPay().getValue())/calendar.getActualMaximum(Calendar.DATE)));

        List<MonthRankItem> data = new ArrayList<>();
        for(int i = 0;i < result.length();i++){
            try {
                JSONObject item = result.getJSONObject(i);
                if(item.getInt("priceType") == 0){
                    MonthRankItem monthRankItem = new MonthRankItem();
                    monthRankItem.setIconId(item.getInt("type"));
                    monthRankItem.setName(item.getString("describeBill"));
                    monthRankItem.setPrice(item.getDouble("price"));
                    monthRankItem.setDate(item.getString("createDate"));
                    data.add(monthRankItem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        data.sort(new Comparator<MonthRankItem>() {
            @Override
            public int compare(MonthRankItem monthRankItem, MonthRankItem t1) {
                return (int)(t1.getPrice() - monthRankItem.getPrice());
            }
        });
        if(data.size() > 0){
            binding.rankListPart.setVisibility(View.VISIBLE);
        }
        binding.rankList.setAdapter(new BillRankAdapter(this,data));
    }

}
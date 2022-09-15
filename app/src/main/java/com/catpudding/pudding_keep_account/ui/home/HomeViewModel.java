package com.catpudding.pudding_keep_account.ui.home;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.catpudding.pudding_keep_account.ui.login.LoginActivity;
import com.catpudding.pudding_keep_account.utils.BaseConfiguration;
import com.catpudding.pudding_keep_account.utils.RequestWithAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeViewModel extends AndroidViewModel {

    private RequestQueue requestQueue;
    private MutableLiveData<String> selectYear;
    private MutableLiveData<String> selectMonth;
    private MutableLiveData<String> monthTotalPay_front = new MutableLiveData<>();
    private MutableLiveData<String> monthTotalPay_after = new MutableLiveData<>();
    private MutableLiveData<String> monthTotalGet_front = new MutableLiveData<>();
    private MutableLiveData<String> monthTotalGet_after = new MutableLiveData<>();
    private MutableLiveData<JSONArray> monthData = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        requestQueue = Volley.newRequestQueue(application);
        if (selectYear == null || selectMonth == null) {
            selectYear = new MutableLiveData<>();
            selectMonth = new MutableLiveData<>();
            Calendar calendar = Calendar.getInstance();
            String year = calendar.get(Calendar.YEAR) + "年";
            selectYear.setValue(year);
            int month = calendar.get(Calendar.MONTH) + 1;
            selectMonth.setValue(month < 10 ? "0" + month : month + "");
            monthTotalGet_after.setValue(".00");
            monthTotalGet_front.setValue("0");
            monthTotalPay_after.setValue(".00");
            monthTotalPay_front.setValue("0");
            monthData.setValue(new JSONArray());
        }
    }

    public MutableLiveData<JSONArray> getMonthData() {
        return monthData;
    }

    public MutableLiveData<String> getMonthTotalGet_after() {
        return monthTotalGet_after;
    }

    public MutableLiveData<String> getMonthTotalGet_front() {
        return monthTotalGet_front;
    }

    public MutableLiveData<String> getMonthTotalPay_after() {
        return monthTotalPay_after;
    }

    public MutableLiveData<String> getMonthTotalPay_front() {
        return monthTotalPay_front;
    }

    public MutableLiveData<String> getSelectMonth() {
        return selectMonth;
    }

    public MutableLiveData<String> getSelectYear() {
        return selectYear;
    }

    public void getBillData(){
        //获取年份和月份
        Integer year = Integer.parseInt(selectYear.getValue().substring(0,selectYear.getValue().indexOf("年")));
        Integer month = Integer.parseInt(selectMonth.getValue());
        //组织URL
        String url = BaseConfiguration.BASE_URL + "/bill/getByMonth?year=" + year + "&month=" + month;
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        RequestWithAuth jsonObjectRequest = new RequestWithAuth(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = response.getJSONObject("result");
                            handleData(result);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = new String(error.networkResponse.data);
                        try {
//                            JSONObject jsonObject = new JSONObject(message.substring(1,message.length()-1).replace("\\\"","\""));
                            JSONObject jsonObject = new JSONObject(message);
                            String errorMsg = jsonObject.getString("message");
                            if(errorMsg.equals("Token失效，请重新登录")){
                                Intent intent = new Intent();
                                intent.setClass(getApplication(), LoginActivity.class);
                                getApplication().startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
//                注意请求头中加入token
                sharedPreferences.getString("token","")
        );
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * 处理请求到的数据
     * @param result
     */
    private void handleData(JSONObject result){
        try {
            double month_pay_money = result.getDouble("month_pay_money");
            double month_get_money = result.getDouble("month_get_money");
            monthTotalPay_front.postValue((int)month_pay_money + "");
            monthTotalGet_front.postValue((int)month_get_money + "");
            DecimalFormat df = new DecimalFormat("0.00");
            String pay_money = df.format(month_pay_money);
            String get_money = df.format(month_get_money);
            monthTotalGet_after.postValue(pay_money.substring(pay_money.indexOf(".")));
            monthTotalPay_after.postValue(get_money.substring(get_money.indexOf(".")));

            monthData.postValue(result.getJSONArray("data"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
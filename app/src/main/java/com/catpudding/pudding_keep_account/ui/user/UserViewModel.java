package com.catpudding.pudding_keep_account.ui.user;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.catpudding.pudding_keep_account.entity.User;
import com.catpudding.pudding_keep_account.ui.login.LoginActivity;
import com.catpudding.pudding_keep_account.utils.BaseConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class UserViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
//    private MutableLiveData<User> user;
    SavedStateHandle handle;
    final private static String SAVE_FILE_NAME = "key_user";
    final private static String IS_LOGIN = "is_login";
    final private static String USERNAME = "username";
    private MutableLiveData<Bitmap> avatar = new MutableLiveData<>();
    private MutableLiveData<String> total_day = new MutableLiveData<>();
    private MutableLiveData<String> total_bill = new MutableLiveData<>();
    private MutableLiveData<String> current_month = new MutableLiveData<>();
    private MutableLiveData<String> current_month_income = new MutableLiveData<>();
    private MutableLiveData<String> current_month_pay = new MutableLiveData<>();
    private MutableLiveData<String> current_month_left = new MutableLiveData<>();

    public MutableLiveData<Bitmap> getAvatar() {
        return avatar;
    }

    public UserViewModel(@NonNull Application application, SavedStateHandle handle) {
        super(application);
        if(!handle.contains(IS_LOGIN)){
            SharedPreferences sharedPreferences = getApplication().getSharedPreferences(SAVE_FILE_NAME, Context.MODE_PRIVATE);
            handle.set(IS_LOGIN,false);
            handle.set(USERNAME,sharedPreferences.getString(USERNAME,"未登录"));
            total_day.setValue("0");
            total_bill.setValue("0");
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);
            current_month.setValue((month+1<10)?("0" + (month + 1)):(month + 1) + "");
            current_month_income.setValue("0.00");
            current_month_pay.setValue("0.00");
            current_month_left.setValue("0.00");
        }
        this.handle = handle;
    }

    public MutableLiveData<Boolean> getLogin(){
        return handle.getLiveData(IS_LOGIN);
    }

    public MutableLiveData<String> getUsername(){
        return handle.getLiveData(USERNAME);
    }

    public MutableLiveData<String> getCurrent_month() {
        return current_month;
    }

    public MutableLiveData<String> getCurrent_month_income() {
        return current_month_income;
    }

    public MutableLiveData<String> getCurrent_month_left() {
        return current_month_left;
    }

    public MutableLiveData<String> getCurrent_month_pay() {
        return current_month_pay;
    }

    public MutableLiveData<String> getTotal_bill() {
        return total_bill;
    }

    public MutableLiveData<String> getTotal_day() {
        return total_day;
    }

    /**
     * 用于保存数据
     */
    public void save(){
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(SAVE_FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_LOGIN,getLogin().getValue());
        editor.putString(USERNAME,getUsername().getValue());
        editor.commit();
    }

    /**
     * 获取头像
     * @param avatar 头像路由
     */
    public void setAvatar(String avatar){
        Log.d("requestError",avatar);
        URL url = null;
        Bitmap bitmap = null;
        try {
            url = new URL(avatar);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
            getAvatar().postValue(bitmap);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
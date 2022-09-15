package com.catpudding.pudding_keep_account.ui.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.databinding.UserFragmentBinding;
import com.catpudding.pudding_keep_account.ui.login.LoginActivity;
import com.catpudding.pudding_keep_account.ui.notifications.utils.MyWebViewClient;
import com.catpudding.pudding_keep_account.ui.user_setting.UserSettingActivity;
import com.catpudding.pudding_keep_account.utils.BaseConfiguration;
import com.catpudding.pudding_keep_account.utils.RequestCode;
import com.catpudding.pudding_keep_account.utils.RequestWithAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;

public class UserFragment extends Fragment {

    private UserViewModel mViewModel;
    private UserFragmentBinding userFragmentBinding;
    private RequestQueue requestQueue;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        requestQueue = Volley.newRequestQueue(getActivity());
        userFragmentBinding = UserFragmentBinding.inflate(inflater,container,false);
        userFragmentBinding.setUser(mViewModel);
        userFragmentBinding.setLifecycleOwner(requireActivity());

        View root = userFragmentBinding.getRoot();
        setListener();

        //添加
        mViewModel.getAvatar().observe(requireActivity(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                if(userFragmentBinding != null){
                    userFragmentBinding.userAvatar.setImageBitmap(bitmap);
                }
            }
        });
//        userFragmentBinding.webView2.getSettings().setJavaScriptEnabled(true);
//        userFragmentBinding.webView2.loadUrl("file:///android_asset/user_sub_chart.html");
//        userFragmentBinding.progressBar2.setIndeterminate(true);
        return root;
    }

    private void setListener(){
        UserOnClickListener userOnClickListener = new UserOnClickListener();
        userFragmentBinding.cardView.setOnClickListener(userOnClickListener);
        userFragmentBinding.textView.setOnClickListener(userOnClickListener);
        userFragmentBinding.imageView8.setOnClickListener(userOnClickListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userFragmentBinding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        mViewModel.getUsername().setValue(sharedPreferences.getString("username","未登录"));
        String avatar = sharedPreferences.getString("avatar","");
        if(!"".equals(avatar)){
            Glide.with(getActivity())
                    .load(avatar)
                    .placeholder(R.drawable.ic_img_load)
                    .error(R.drawable.ic_img_load)
                    .centerCrop()
                    .into(userFragmentBinding.userAvatar);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    mViewModel.setAvatar(BaseConfiguration.BASE_URL + avatar);
//                }
//            }).start();
        }else{
            userFragmentBinding.userAvatar.setImageResource(R.drawable.ic_user);
        }
        getUserBillInfo();
        getBillInfo();
     }

    /**
     * 处理头像和用户名再被点击的时候的如何处理
     */
    class UserOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            //判断用户是否登录
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info",Context.MODE_PRIVATE);
            if(sharedPreferences.getString("token","").equals("")){
                Intent intent = new Intent();
                intent.setClass(getActivity(),LoginActivity.class);
                startActivity(intent);
            }else{
//                Intent intent = new Intent();
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserSettingActivity.class);
                startActivity(intent);
            }
        }
    }

    public void getUserBillInfo(){
        String url = BaseConfiguration.BASE_URL + "user/user_bill_info";
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        RequestWithAuth jsonObjectRequest = new RequestWithAuth(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = response.getJSONObject("result");
                            handleUserInfo(result);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
//                注意请求头中加入token
                sharedPreferences.getString("token","")
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void handleUserInfo(JSONObject result){
        try {
            mViewModel.getTotal_day().setValue(result.getInt("totalDay") + "");
            mViewModel.getTotal_bill().setValue(result.getInt("totalBill") + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getBillInfo(){
        String url = BaseConfiguration.BASE_URL + "bill/get_month_compute_data";
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        JSONObject data = new JSONObject();
        Date date = new Date();
        try {
            data.put("year",date.getYear() + 1900);
            data.put("month",Integer.parseInt(mViewModel.getCurrent_month().getValue()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestWithAuth jsonObjectRequest = new RequestWithAuth(
                Request.Method.POST,
                url,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            handleCurrentMonthBill(response.getJSONArray("result"));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                },
//                注意请求头中加入token
                sharedPreferences.getString("token","")
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void handleCurrentMonthBill(JSONArray result){
        double total_income = 0;
        double total_pay = 0;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        for(int i = 0; i < result.length();i++){
            try {
                JSONObject item = result.getJSONObject(i);
                if(item.getInt("priceType") == 0){
                    total_pay += item.getDouble("price");
                }else{
                    total_income += item.getDouble("price");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mViewModel.getCurrent_month_income().postValue(decimalFormat.format(total_income));
        mViewModel.getCurrent_month_pay().postValue(decimalFormat.format(total_pay));
        mViewModel.getCurrent_month_left().postValue(decimalFormat.format(total_income - total_pay));
    }

}
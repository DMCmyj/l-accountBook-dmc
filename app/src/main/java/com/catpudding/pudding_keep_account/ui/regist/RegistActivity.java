package com.catpudding.pudding_keep_account.ui.regist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.databinding.ActivityRegistBinding;
import com.catpudding.pudding_keep_account.utils.BaseConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistActivity extends AppCompatActivity {

    private RegistViewModel registViewModel;
    private ActivityRegistBinding activityRegistBinding;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        registViewModel = new ViewModelProvider(this).get(RegistViewModel.class);
        activityRegistBinding = ActivityRegistBinding.inflate(getLayoutInflater());
        setContentView(activityRegistBinding.getRoot());
        activityRegistBinding.setData(registViewModel);
        activityRegistBinding.setLifecycleOwner(this);
        requestQueue = Volley.newRequestQueue(this);

        setListener();
    }

    private void setListener(){
        activityRegistBinding.registBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activityRegistBinding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone_regist = activityRegistBinding.phoneRegiste.getText().toString().trim();
                String username_regist = activityRegistBinding.usernameRegiste.getText().toString().trim();
                String password_first = activityRegistBinding.passwordFirst.getText().toString().trim();
                String password_confirm = activityRegistBinding.passwordConfirm.getText().toString().trim();
                if(username_regist.equals("")){
                    Toast.makeText(RegistActivity.this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(phone_regist.equals("") || phone_regist.length() != 11){
                    Toast.makeText(RegistActivity.this, "手机为空或格式不正确！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password_first.equals("")){
                    Toast.makeText(RegistActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password_confirm.equals(password_first)){
                    registe(phone_regist,username_regist,password_confirm);
                }else{
                    Toast.makeText(RegistActivity.this, "用户名密码不同，请重新输入！", Toast.LENGTH_SHORT).show();
                    activityRegistBinding.passwordFirst.setText("");
                    activityRegistBinding.passwordConfirm.setText("");
                }
            }
        });
    }


    private void registe(String phone,String username,String password){
        String url = BaseConfiguration.BASE_URL + "sys/register";
        JSONObject data = new JSONObject();
        try {
            data.put("phone",phone);
            data.put("username",username);
            data.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(data);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean success = false;
                        try {
                            success = response.getBoolean("success");
                            if(success){
                                Toast.makeText(getApplication(), "注册成功！", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(getApplication(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(), "注册失败！请检查网络", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

}
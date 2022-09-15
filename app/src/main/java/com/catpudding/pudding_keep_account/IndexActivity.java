package com.catpudding.pudding_keep_account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.catpudding.pudding_keep_account.ui.login.LoginActivity;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("user_info",MODE_PRIVATE);
        String token = sharedPreferences.getString("token","");
        Intent intent = new Intent();
        if(token.equals("")){
            intent.setClass(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            intent.setClass(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
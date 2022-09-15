package com.catpudding.pudding_keep_account.ui.user_setting;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.catpudding.pudding_keep_account.utils.BaseConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UserSettingViewModel extends AndroidViewModel {

    private MutableLiveData<Bitmap> avatar = new MutableLiveData<>();
    private MutableLiveData<String> username = new MutableLiveData<>();

    public UserSettingViewModel(@NonNull Application application) {
        super(application);


    }

    public MutableLiveData<Bitmap> getAvatar() {
        return avatar;
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }

}

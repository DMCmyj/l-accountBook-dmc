package com.catpudding.pudding_keep_account.ui.regist;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.catpudding.pudding_keep_account.utils.BaseConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class RegistViewModel extends AndroidViewModel {

    private MutableLiveData<String> usernameRegiste = new MutableLiveData<>();
    private MutableLiveData<String> passwordFirst = new MutableLiveData<>();
    private MutableLiveData<String> passwordConfirm = new MutableLiveData<>();


    public RegistViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getUsernameRegiste() {
        return usernameRegiste;
    }

    public MutableLiveData<String> getPasswordFirst() {
        return passwordFirst;
    }

    public MutableLiveData<String> getPasswordConfirm() {
        return passwordConfirm;
    }


}

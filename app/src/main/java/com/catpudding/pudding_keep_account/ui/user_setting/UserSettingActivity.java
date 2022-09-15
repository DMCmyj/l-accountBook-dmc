package com.catpudding.pudding_keep_account.ui.user_setting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.databinding.ActivityUserSettingBinding;
import com.catpudding.pudding_keep_account.ui.login.LoginActivity;
import com.catpudding.pudding_keep_account.utils.BaseConfiguration;
import com.catpudding.pudding_keep_account.utils.RequestWithAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserSettingActivity extends AppCompatActivity {

    ActivityUserSettingBinding binding;
    UserSettingViewModel userSettingViewModel;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        requestQueue = Volley.newRequestQueue(this);

        userSettingViewModel = new ViewModelProvider(this).get(UserSettingViewModel.class);
        binding = ActivityUserSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setData(userSettingViewModel);
        binding.setLifecycleOwner(this);

        loadInfo();

        setListener();
        setObserve();
    }

    private void loadInfo(){
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        //当缓存中有头像数据的时候执行
        if(!sharedPreferences.getString("avatar","").equals("")){
            Glide.with(getApplication())
                    .load(sharedPreferences.getString("avatar",""))
                    .placeholder(R.drawable.ic_img_load)
                    .error(R.drawable.ic_img_load)
                    .centerCrop()
                    .into(binding.userAvatar);
//                    setAvatar(BaseConfiguration.BASE_URL + sharedPreferences.getString("avatar",""));
        }
        userSettingViewModel.getUsername().setValue(sharedPreferences.getString("username",""));
    }

    private void setObserve(){
        userSettingViewModel.getAvatar().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                binding.userAvatar.setImageBitmap(bitmap);
            }
        });
    }

    private void setListener(){
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.avatarLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(UserSettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(UserSettingActivity.this,
                            new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },1);
                }else{
                    openSystemAlbum();
                }
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(UserSettingActivity.this);
                builder.setTitle("提示");
                builder.setMessage("您确定要退出登录吗？");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = getSharedPreferences("user_info",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("username");
                        editor.remove("avatar");
                        editor.remove("token");
                        editor.remove("id");
                        editor.commit();
                        Intent intent = new Intent();
                        intent.setClass(UserSettingActivity.this,LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
            }
        });

        binding.usernameLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUsernameModifyPicker();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openSystemAlbum();
            }
        }
    }

    private void openSystemAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/**");
        startActivityForResult(intent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2){
            //判断结果状态
            if(resultCode == RESULT_OK && data != null){
                if(Build.VERSION.SDK_INT >= 19){
                    handleImage(data);
                }else{
                    handleImageVersionLow(data);
                }
            }
        }
    }

    private void handleImage(Intent data){
        String path = null;
        Uri uri = data.getData();

//        String[] arr = {
//                MediaStore.Images.Media.DATA
//        };
//        Cursor cursor = managedQuery(uri,arr,null,null,null);
//        int img_index = cursor.getColumnIndexOrThrow(arr[0]);
//        cursor.moveToFirst();
//        String image_path = cursor.getString(img_index);
////        获取实际文件
//        File file = new File(image_path);
        ContentResolver contentResolver = getContentResolver();
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));
            String img = bitmapToBase64(bitmap);
            if(img != null){
                Log.e("uploadError",img);
//                upload(img);
                HashMap<String,Object> params = new HashMap<>();
                params.put("uploadFile",bitmap);
                upload(params);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleImageVersionLow(Intent data){
        Uri uri = data.getData();

//        String[] arr = {
//                MediaStore.Images.Media.DATA
//        };
//        Cursor cursor = managedQuery(uri,arr,null,null,null);
//        int img_index = cursor.getColumnIndexOrThrow(arr[0]);
//        cursor.moveToFirst();
//        String image_path = cursor.getString(img_index);
////        获取实际文件
//        File file = new File(image_path);
        ContentResolver contentResolver = getContentResolver();
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri));
            String img = bitmapToBase64(bitmap);
            if(img != null){
                System.out.println(img);
                Log.e("uploadError",img);
                HashMap<String,Object> params = new HashMap<>();
                params.put("uploadFile",bitmap);
                upload(params);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String bitmapToBase64(Bitmap bitmap){
        String result = null;
        ByteArrayOutputStream baos = null;
        if(bitmap != null){
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            try {
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.getEncoder().encodeToString(bitmapBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void upload(HashMap<String,Object> params){
        binding.loading.setVisibility(View.VISIBLE);
        String url = BaseConfiguration.BASE_URL + "upload/user_avatar";

        SharedPreferences sharedPreferences = getSharedPreferences("user_info",MODE_PRIVATE);

        RequestWithAuth requestWithAuth = new RequestWithAuth(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        binding.loading.setVisibility(View.GONE);
                        try {
                            if(response.getBoolean("success")){
                                Toast.makeText(UserSettingActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("avatar",response.getString("result"));
                                editor.commit();
                                loadInfo();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        binding.loading.setVisibility(View.GONE);
                        Log.e("uploadError",error.toString());
                    }
                },
                sharedPreferences.getString("token","")
        ){
            private final String fixFormat = "--";
            private final String mFilename = System.currentTimeMillis()+".jpeg";
            private final String splitLine = "\r\n";
            private final String boundary = "joneSplitLineHere"; // 分隔符

            @Override
            public String getBodyContentType() {
                return "multipart/form-data;boundary=" + boundary;
            }
            @Override
            public byte[] getBody() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                // 构建请求体
                try{
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        Bitmap orgBit = (Bitmap) entry.getValue();
                        //压缩并转格式Start
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        if(orgBit.compress(Bitmap.CompressFormat.JPEG,100,out)) {
                            out.flush();
                            out.close();
                        }
                        //压缩并转格式End
                        dos.write(getBytes(fixFormat + boundary + splitLine));
                        dos.write(getBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\""+ mFilename + "\"" + splitLine));
                        dos.write(getBytes(splitLine));
                        dos.write(out.toByteArray());
                        dos.write(getBytes(splitLine));
                    }
                    dos.write(getBytes(fixFormat + boundary + fixFormat + splitLine));// 结束标记
                }catch (IOException e){
                    e.printStackTrace();
                }

                return baos.toByteArray();
            }
        };
        requestQueue.add(requestWithAuth);
    }

    public static byte[] getBytes(String str){
        return str.getBytes();
    }

    /**
     * 显示底部弹出框
     */
    private void showUsernameModifyPicker(){
        View root = LayoutInflater.from(this).inflate(R.layout.layout_modify_name,null);
//        NumberPicker yearPicker = root.findViewById(R.id.number_picker_year);
//        NumberPicker monthPicker = root.findViewById(R.id.number_picker_month);
//        ImageView okBtn = root.findViewById(R.id.change_month_ok);
        TextView tv_btn_cancel = root.findViewById(R.id.tv_btn_cancel);
        TextView tv_btn_ok = root.findViewById(R.id.tv_btn_ok);
        EditText editText = root.findViewById(R.id.modify_name_input);
        SharedPreferences sharedPreferences = getSharedPreferences("user_info",MODE_PRIVATE);
        editText.setText(sharedPreferences.getString("username",""));

        Dialog dialog = new Dialog(this);

        Window window = dialog.getWindow();
        window.setContentView(root);
        window.setBackgroundDrawable(null);
//        window.setBackgroundDrawableResource(R.color.white);
        window.setLayout(WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        window.setWindowAnimations(R.style.AnimBottom);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.y = 0;
        lp.x = 0;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.horizontalMargin = 0;
        lp.verticalMargin = 0;
        window.setAttributes(lp);
        dialog.show();

//        okBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                homeViewModel.getSelectYear().setValue(yearPicker.getValue() + "年");
//                homeViewModel.getSelectMonth().setValue(monthPicker.getValue() < 10 ? "0" + monthPicker.getValue():monthPicker.getValue() + "");
//                homeViewModel.getBillData();
//                dialog.cancel();
//            }
//        });
        tv_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        tv_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUsername(editText.getText().toString().trim());
                dialog.cancel();
            }
        });
    }

    private void changeUsername(String username){
        String url = BaseConfiguration.BASE_URL + "user/change_username";
        JSONObject data = new JSONObject();
        try {
            data.put("username",username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("user_info",MODE_PRIVATE);

        RequestWithAuth requestWithAuth = new RequestWithAuth(
                Request.Method.POST,
                url,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                JSONObject result = response.getJSONObject("result");
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username",result.getString("username"));
                                editor.putString("token",result.getString("token"));
                                editor.commit();
                                Toast.makeText(UserSettingActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                loadInfo();
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

}
package com.catpudding.pudding_keep_account.ui.home.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.ui.home.HomeViewModel;
import com.catpudding.pudding_keep_account.ui.login.LoginActivity;
import com.catpudding.pudding_keep_account.ui.user_setting.UserSettingActivity;
import com.catpudding.pudding_keep_account.utils.BaseConfiguration;
import com.catpudding.pudding_keep_account.utils.IconList;
import com.catpudding.pudding_keep_account.utils.RequestWithAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private JSONArray data;
    private Context context;
    private RequestQueue requestQueue;
    private HomeViewModel homeViewModel;

    public MyAdapter(JSONArray data, Context context,HomeViewModel homeViewModel) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
        this.homeViewModel = homeViewModel;
    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return data.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.home_list_item,null);
        TextView title = linearLayout.findViewById(R.id.list_item_title_left);
        TextView titleRight = linearLayout.findViewById(R.id.list_item_title_right);
        TextView titleSecond = linearLayout.findViewById(R.id.list_item_title_total_income);
        ListView listView = linearLayout.findViewById(R.id.sub_list);


        try {
            JSONObject item = data.getJSONObject(i);
            title.setText(item.getString("date"));
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            if(item.getDouble("total_get_price") != 0){
                titleSecond.setVisibility(View.VISIBLE);
                titleSecond.setText("收入：" + decimalFormat.format(item.getDouble("total_get_price")));
            }
            titleRight.setText("支出：" + decimalFormat.format(item.getDouble("total_pay_price")));
            MySecondBaseAdapter mySecondBaseAdapter = new MySecondBaseAdapter(item.getJSONArray("bills"),listView);
            listView.setAdapter(mySecondBaseAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }



        return linearLayout;
    }

    class MySecondBaseAdapter extends BaseAdapter{
        private JSONArray secondData;
        private ListView listView;

        public MySecondBaseAdapter(JSONArray secondData,ListView listView) {
            this.secondData = secondData;
            this.listView = listView;
        }

        @Override
        public int getCount() {
            return secondData.length();
        }

        @Override
        public Object getItem(int i) {
            try {
                return secondData.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LinearLayout root = (LinearLayout) inflater.inflate(R.layout.home_sub_list_item,null);

            ImageView imageView = root.findViewById(R.id.icon_type);
            TextView price = root.findViewById(R.id.sub_list_item_tv_price);
            TextView desc = root.findViewById(R.id.sub_list_item_tv);


            try {
                JSONObject item = secondData.getJSONObject(i);
//                int num = (int)(Math.random()*1000 % IconList.MY_ICON_LIST.length);
                imageView.setImageResource(IconList.MY_ICON_LIST[item.getInt("type")].getSource());
                desc.setText(item.getString("describeBill"));
                price.setText((item.getInt("priceType") == 0?"-":"+") + item.getDouble("price"));

                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            editItem(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

                root.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        PopupMenu popupMenu = new PopupMenu(context,root);
                        MenuInflater inflater = popupMenu.getMenuInflater();
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.delete:
                                        deleteItem(item);
                                        break;
                                }
                                return false;
                            }
                        });

                        inflater.inflate(R.menu.home_long_click_menu,popupMenu.getMenu());
                        popupMenu.show();
                        return false;
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return root;
        }

        private void deleteItem(JSONObject item){
            //创建对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提示");
            builder.setMessage("您确定要删除该记录吗?");

            builder.setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
                    String url = BaseConfiguration.BASE_URL + "bill/deleteBill";
                    RequestWithAuth requestWithAuth = new RequestWithAuth(
                            Request.Method.POST,
                            url,
                            item,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if(response.getInt("code") == 200){
                                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                            homeViewModel.getBillData();
                                        }else{
                                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                        }
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
                                            intent.setClass(context, LoginActivity.class);
                                            context.startActivity(intent);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            sharedPreferences.getString("token","")
                    );
                    requestQueue.add(requestWithAuth);
                }
            });

            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        }

        private String addDate;

        private void editItem(JSONObject item) throws JSONException, ParseException {
            Dialog dialog = new Dialog(context);
            View root = LayoutInflater.from(context).inflate(R.layout.add_record_item,null);
            ImageView imageView = root.findViewById(R.id.icon_selected);
            imageView.setImageResource(IconList.MY_ICON_LIST[item.getInt("type")].getSource());

            TextView textView = root.findViewById(R.id.add_item_title);
            textView.setText(IconList.MY_ICON_LIST[item.getInt("type")].getName());

            TextView textView1 = root.findViewById(R.id.cancel_btn_add_item);
            textView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });

            CalendarView calendarView = root.findViewById(R.id.calendarView);
            EditText price_input = root.findViewById(R.id.input_price);
            EditText desc_input = root.findViewById(R.id.input_desc);
            TextView confirm_btn = root.findViewById(R.id.confirm_btn);
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            addDate = item.getString("createDate");
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                    Date date = new Date();
                    date.setYear(i-1900);
                    date.setMonth(i1);
                    date.setDate(i2);
                    addDate = sf.format(date);
                }
            });
            confirm_btn.setText("确认修改");
            price_input.setText(item.getDouble("price") + "");
            desc_input.setText(item.getString("describeBill"));
            Date date = sf.parse(item.getString("createDate"));
            calendarView.setDate(date.getTime());
            confirm_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id",item.getInt("id"));
                        jsonObject.put("describeBill",desc_input.getText());
                        jsonObject.put("price",Double.parseDouble(String.valueOf(price_input.getText())));
                        jsonObject.put("priceType",item.getInt("priceType"));
                        jsonObject.put("type",item.getInt("type"));
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                        jsonObject.put("createDate",addDate);
                        editBill(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.cancel();
                }
            });

            Window window = dialog.getWindow();
            window.setContentView(root);
            window.setBackgroundDrawable(null);
            window.setBackgroundDrawableResource(R.color.white);
            window.setLayout(WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            window.setWindowAnimations(R.style.AnimBottom);
            dialog.show();
        }

        private void editBill(JSONObject data){
            SharedPreferences sharedPreferences = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
            String url = BaseConfiguration.BASE_URL + "bill/editBill";
            RequestWithAuth requestWithAuth = new RequestWithAuth(
                    Request.Method.POST,
                    url,
                    data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("code") == 200){
                                    Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
                                    homeViewModel.getBillData();
                                }else{
                                    Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
                                }
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
                                    intent.setClass(context, LoginActivity.class);
                                    context.startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    sharedPreferences.getString("token","")
            );
            requestQueue.add(requestWithAuth);
        }
    }
}

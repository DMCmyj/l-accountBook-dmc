package com.catpudding.pudding_keep_account.ui.notifications;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.databinding.FragmentNotificationsBinding;
import com.catpudding.pudding_keep_account.ui.notifications.utils.MyListAdapter;
import com.catpudding.pudding_keep_account.ui.notifications.utils.MyWebViewClient;
import com.catpudding.pudding_keep_account.ui.notifications.utils.RankItem;
import com.catpudding.pudding_keep_account.utils.BaseConfiguration;
import com.catpudding.pudding_keep_account.utils.RequestWithAuth;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private TabLayout.OnTabSelectedListener myListener;
    private RequestQueue requestQueue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        createSecondTab(0);
        binding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        binding.webView.setBackgroundColor(0);
        //开启脚本支持
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl("file:///android_asset/test.html");
        requestQueue = Volley.newRequestQueue(getContext());

        addListener();
        return root;
    }

    private void addListener(){

        //添加顶部tab的点击事件
        binding.topTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                createSecondTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.textView22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyPriceTypePicker();
            }
        });
        binding.imageView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyPriceTypePicker();
            }
        });

    }

    public void setData2(ArrayList<Double> ydata, ArrayList<String> xdata,double max) {
        if (binding.webView == null) {
            return;
        }
        binding.webView.setWebViewClient(new MyWebViewClient(ydata,xdata,max,binding.topTab.getSelectedTabPosition()));
        binding.webView.loadUrl("file:///android_asset/test.html");
    }

    private ArrayList<String> getWeekDate(int week){
        ArrayList<String> dates = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        //获得今天是一周的第几天
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.setWeekDate(calendar.get(Calendar.YEAR),week,2);
        Date dateStart = calendar.getTime();
        calendar.setWeekDate(calendar.get(Calendar.YEAR),week,1);
        Date dateEnd = calendar.getTime();
        List<Date> dateList = getDays(dateStart,dateEnd);

        SimpleDateFormat sf = new SimpleDateFormat("MM-dd");
        System.out.println(sf.format(dateStart));
        System.out.println(sf.format(dateEnd));
        for(Date date : dateList){
            dates.add(sf.format(date));
        }
        return dates;
    }

    /**
     * 获取两个日期之间的所有日期
     * @param
     * @param
     * @return
     */
    private static List<Date> getDays(Date start, Date end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        while (tempStart.before(tempEnd) || !tempStart.after(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    /**
     * 从网络获取数据
     * @param type
     * @param num
     */
    private void getData(int type,int num){
        JSONObject jsonObject = new JSONObject();
        String url = BaseConfiguration.BASE_URL + "bill/getComputeData";
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        try {
            jsonObject.put("type",type);
            jsonObject.put("num",num);
            RequestWithAuth requestWithAuth = new RequestWithAuth(
                    Request.Method.POST,
                    url,
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("code") == 200){
                                    JSONArray data = response.getJSONArray("result");
                                    for(int i = 0;i<data.length();i++){
                                        JSONObject item = data.getJSONObject(i);
                                        if(item.getInt("priceType") != notificationsViewModel.getPriceType().getValue()){
                                            data.remove(i);
                                            i--;
                                        }
                                    }
                                    switch (type){
                                        case 1:
                                            showWeekData(data,num);
                                            break;
                                        case 2:
                                            showMonthData(data,num);
                                            break;
                                        case 3:
                                            showYearData(data,num);
                                            break;
                                    }
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //展示周数据
    private void showWeekData(JSONArray data,int num){
        ArrayList<String> dates = getWeekDate(num);
        ArrayList<Double> ydata = new ArrayList<>();
        ArrayList<String> xdata = new ArrayList<>();
        double maxPrice = 0;
        double totalPrice = 0;
        try {
            for(int dayNum = 0;dayNum < dates.size();dayNum++){
                xdata.add(dates.get(dayNum));
                Double price = 0d;
                for(int i = 0;i<data.length();i++){
                    JSONObject bill = (JSONObject) data.get(i);
                    String createDate = bill.getString("createDate");
                    if(createDate.indexOf(dates.get(dayNum)) != -1){
                        price += bill.getDouble("price");
                    }
                }
                ydata.add(price);
                totalPrice += price;
                if(maxPrice < price){
                    maxPrice = price;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        if(notificationsViewModel.getPriceType().getValue() == 0){
            binding.totalPay.setText("总支出：" + decimalFormat.format(totalPrice));
        }else{
            binding.totalPay.setText("总收入：" + decimalFormat.format(totalPrice));
        }
        if(dates.size()!=0){
            binding.avgPay.setText("平均值：" + decimalFormat.format(totalPrice/dates.size()));
        }else{
            binding.avgPay.setText("平均值：0");
        }
        setData2(ydata,xdata,maxPrice);
        showRanking(data);
    }

    //展示月数据
    private void showMonthData(JSONArray data,int num){
        ArrayList<Double> ydata = new ArrayList<>();
        ArrayList<String> xdata = new ArrayList<>();
        double maxPrice = 0;
        double totalPrice = 0;
        int col = 0;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH,num-1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            col = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            for(int i = 1;i<=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
                xdata.add(i + "");
                double price = 0;
                for(int j = 0;j<data.length();j++){
                    JSONObject bill = (JSONObject) data.get(j);
                    Date date = dateFormat.parse(bill.getString("createDate"));
                    if(date.getDate() == i){
                        price += bill.getDouble("price");
                    }
                }
                ydata.add(price);
                totalPrice += price;
                if(maxPrice < price){
                    maxPrice = price;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        if(notificationsViewModel.getPriceType().getValue() == 0){
            binding.totalPay.setText("总支出：" + decimalFormat.format(totalPrice));
        }else{
            binding.totalPay.setText("总收入：" + decimalFormat.format(totalPrice));
        }
        if(col!=0){
            binding.avgPay.setText("平均值：" + decimalFormat.format(totalPrice/col));
        }else{
            binding.avgPay.setText("平均值：0");
        }
        setData2(ydata,xdata,maxPrice);
        showRanking(data);
    }

    //展示年数据
    private void showYearData(JSONArray data,int num){
        ArrayList<Double> ydata = new ArrayList<>();
        ArrayList<String> xdata = new ArrayList<>();
        double maxPrice = 0;
        double totalPrice = 0;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for(int i = 1;i<= 12;i++){
                xdata.add(i + "");
                double price = 0;
                for (int j = 0;j<data.length();j++){
                    JSONObject bill = (JSONObject) data.get(j);
                    Date date = simpleDateFormat.parse(bill.getString("createDate"));
                    if(date.getMonth() == i - 1){
                        price += bill.getDouble("price");
                    }
                }
                ydata.add(price);
                totalPrice += price;
                if(maxPrice < price){
                    maxPrice = price;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        if(notificationsViewModel.getPriceType().getValue() == 0){
            binding.totalPay.setText("总支出：" + decimalFormat.format(totalPrice));
        }else{
            binding.totalPay.setText("总收入：" + decimalFormat.format(totalPrice));
        }
        binding.avgPay.setText("平均值：" + decimalFormat.format(totalPrice/12));
        setData2(ydata,xdata,maxPrice);
        showRanking(data);
    }

    //展示排行榜
    private void showRanking(JSONArray data){
        try {
            List<RankItem> rankItems = new ArrayList<>();
            double totalPrice = 0;
            for(int i = 0;i<data.length();i++){
                JSONObject bill = (JSONObject) data.get(i);
                Integer source = bill.getInt("type");
                boolean flag = false;
                for(int j = 0;j<rankItems.size();j++){
                    if(rankItems.get(j).getSource() == source){
                        rankItems.get(j).setTotalPrice(rankItems.get(j).getTotalPrice() + bill.getDouble("price"));
                        totalPrice += bill.getDouble("price");
                        flag = true;
                    }
                }
                //没找到
                if(!flag){
                    RankItem rankItem = new RankItem();
                    rankItem.setSource(bill.getInt("type"));
                    rankItem.setTotalPrice(bill.getDouble("price"));
                    totalPrice += bill.getDouble("price");
                    rankItems.add(rankItem);
                }
            }
            int maxIndex = 0;
            for(int i = 1;i < rankItems.size();i++){
                if(rankItems.get(i).getTotalPrice() > rankItems.get(maxIndex).getTotalPrice()){
                    maxIndex = i;
                }
            }
            //            初始化最大值项
            if(rankItems.size() > 0){
                rankItems.get(maxIndex).setPercent(100);
            }
            //获取条百分比
            for(int i = 0;i < rankItems.size();i++){
                if(i != maxIndex){
                    double percent = rankItems.get(i).getTotalPrice()*100/rankItems.get(maxIndex).getTotalPrice();
                    rankItems.get(i).setPercent((int) percent);
                }
            }
            //获取数值百分比
            for(int i = 0;i < rankItems.size();i++){
                double percent = rankItems.get(i).getTotalPrice()*100/totalPrice;
                rankItems.get(i).setPercentTip(percent);
            }

            rankItems.sort(new Comparator<RankItem>() {
                @Override
                public int compare(RankItem rankItem, RankItem t1) {
                    return t1.getPercent() - rankItem.getPercent();
                }
            });

            if(rankItems.size() == 0){
                binding.noInfoTip.setVisibility(View.VISIBLE);
            }else{
                binding.noInfoTip.setVisibility(View.GONE);
            }

            MyListAdapter adapter = new MyListAdapter(getContext(),rankItems);
            binding.bangListview.startLayoutAnimation();
            binding.bangListview.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setWeekSecondTabLayoutClickListener(){
        myListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String name = (String) tab.getText();
                name = name.replace("周","");
                Integer num = Integer.parseInt(name);
                getData(1,num);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };

        binding.secondTab.setOnTabSelectedListener(myListener);
    }

    private void setMonthItemClickListener(){
        myListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String name = (String) tab.getText();
                name = name.replace("月","");
                Integer num = Integer.parseInt(name);
                getData(2,num);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
        binding.secondTab.setOnTabSelectedListener(myListener);
    }

    private void setYearItemClickListener(){
        myListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        Date date = new Date();
                        getData(3,date.getYear() + 1900 - 1);
                        break;
                    case 1:
                        Date date1 = new Date();
                        getData(3,date1.getYear() + 1900);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
        binding.secondTab.setOnTabSelectedListener(myListener);
    }


    /**
     * 创建二级tab
     * @param i
     */
    private void createSecondTab(int i){
        binding.secondTab.removeOnTabSelectedListener(myListener);
        binding.secondTab.removeAllTabs();
        switch (i){
            case 0:
                Calendar calendar = Calendar.getInstance();
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                int weekNum = calendar.getWeeksInWeekYear();
                int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                for(int num = 1;num <= weekNum;num++){
                    TabLayout.Tab tab = binding.secondTab.newTab();
                    tab.setText(num + "周");
                    binding.secondTab.addTab(tab,num - 1,num == currentWeek?true:false);
                }
                binding.secondTab.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.secondTab.getTabAt(currentWeek - 1).select();
                        getData(1,currentWeek);
                    }
                },100);
                setWeekSecondTabLayoutClickListener();
                break;
            case 1:
                Calendar calendar1 = Calendar.getInstance();
                int currentMonth = calendar1.get(Calendar.MONTH);
                for(int num = 1;num <= 12;num++){
                    TabLayout.Tab tab = binding.secondTab.newTab();
                    tab.setText(num + "月");
                    binding.secondTab.addTab(tab,num - 1,num == currentMonth + 1?true:false);
                }
                binding.secondTab.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.secondTab.getTabAt(currentMonth).select();
                        getData(2,currentMonth + 1);
                    }
                },100);
                setMonthItemClickListener();
                break;
            case 2:
                TabLayout.Tab tab1 = binding.secondTab.newTab();
                TabLayout.Tab tab2 = binding.secondTab.newTab();
                tab1.setText("去年");
                tab2.setText("今年");
                binding.secondTab.addTab(tab1,0,false);
                binding.secondTab.addTab(tab2,1,true);
                setYearItemClickListener();
                Date date = new Date();
                getData(3,date.getYear() + 1900);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * 显示底部弹出框
     */
    private void showMyPriceTypePicker(){
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.my_price_type_picker,null);
        ImageView img_check_income = root.findViewById(R.id.img_check_income);
        ImageView img_check_pay = root.findViewById(R.id.img_check_pay);
        ConstraintLayout line_pay = root.findViewById(R.id.select_line_pay);
        ConstraintLayout line_income = root.findViewById(R.id.select_line_income);

        System.out.println(notificationsViewModel.getPriceType().getValue() + "");
        if (notificationsViewModel.getPriceType().getValue() == 0){
            img_check_pay.setVisibility(View.VISIBLE);
            img_check_income.setVisibility(View.GONE);
        }else{
            img_check_pay.setVisibility(View.GONE);
            img_check_income.setVisibility(View.VISIBLE);
        }

        Dialog dialog = new Dialog(getActivity());

        Window window = dialog.getWindow();
        window.setContentView(root);
        window.setBackgroundDrawable(null);
        window.setBackgroundDrawableResource(R.color.white);
        window.setLayout(WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        window.setWindowAnimations(R.style.AnimBottom);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.y = 0;
        lp.x = 0;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.horizontalMargin = 0;
        lp.verticalMargin = 0;
        window.setAttributes(lp);
        dialog.show();

        line_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationsViewModel.getPriceType().setValue(0);
                binding.topTab.selectTab(binding.topTab.getTabAt(binding.topTab.getSelectedTabPosition()));
                createSecondTab(binding.topTab.getSelectedTabPosition());
                binding.textView22.setText("支出");
                dialog.cancel();
            }
        });
        line_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationsViewModel.getPriceType().setValue(1);
                binding.topTab.selectTab(binding.topTab.getTabAt(binding.topTab.getSelectedTabPosition()));
                createSecondTab(binding.topTab.getSelectedTabPosition());
                binding.textView22.setText("收入");
                dialog.cancel();
            }
        });
    }
}
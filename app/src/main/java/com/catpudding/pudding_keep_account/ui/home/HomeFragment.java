package com.catpudding.pudding_keep_account.ui.home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.databinding.FragmentHomeBinding;
import com.catpudding.pudding_keep_account.ui.addrecord.AddRecordActivity;
import com.catpudding.pudding_keep_account.ui.home.utils.MyAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.setData(homeViewModel);
        binding.setLifecycleOwner(getActivity());
        addListener();
        addObserve();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.getBillData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addObserve(){
        homeViewModel.getMonthData().observe(getActivity(), new Observer<JSONArray>() {
            @Override
            public void onChanged(JSONArray jsonArray) {
                if(jsonArray != null){
                    BaseAdapter baseAdapter = new MyAdapter(jsonArray,getActivity(),homeViewModel);
                    binding.myListView.startLayoutAnimation();
                    binding.myListView.setAdapter(baseAdapter);
                    if(jsonArray.length()>0){
                        binding.noInfoTip.setVisibility(View.GONE);
                    }else{
                        binding.noInfoTip.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void addListener(){
        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyMonthPicker();
            }
        });
        binding.textView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyMonthPicker();
            }
        });
        binding.textView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyMonthPicker();
            }
        });
        binding.textView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyMonthPicker();
            }
        });
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AddRecordActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_bottom, android.R.anim.fade_out);
            }
        });
    }

    /**
     * 显示底部弹出框
     */
    private void showMyMonthPicker(){
        Calendar calendar = Calendar.getInstance();
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.my_month_picker,null);
        NumberPicker yearPicker = root.findViewById(R.id.number_picker_year);
        NumberPicker monthPicker = root.findViewById(R.id.number_picker_month);
        ImageView okBtn = root.findViewById(R.id.change_month_ok);

        String year = homeViewModel.getSelectYear().getValue();
        int yearNow = Integer.parseInt(year.substring(0,year.indexOf("年")));
        int monthNow = Integer.parseInt(homeViewModel.getSelectMonth().getValue());

        //设置年份范围
//        int yearNow = calendar.get(Calendar.YEAR);
        yearPicker.setMinValue(yearNow-10);
        yearPicker.setMaxValue(yearNow+10);
        yearPicker.setValue(yearNow);
        yearPicker.setWrapSelectorWheel(false);//关闭循环

        //设置月份范围
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(monthNow);
        monthPicker.setWrapSelectorWheel(false);


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

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeViewModel.getSelectYear().setValue(yearPicker.getValue() + "年");
                homeViewModel.getSelectMonth().setValue(monthPicker.getValue() < 10 ? "0" + monthPicker.getValue():monthPicker.getValue() + "");
                homeViewModel.getBillData();
                dialog.cancel();
            }
        });
    }


}
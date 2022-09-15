package com.catpudding.pudding_keep_account.ui.addrecord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.databinding.ActivityAddRecordBinding;
import com.catpudding.pudding_keep_account.ui.addrecord.fragments.ExpenditureFragment;
import com.catpudding.pudding_keep_account.ui.addrecord.fragments.IncomeFragment;
import com.catpudding.pudding_keep_account.ui.addrecord.utils.MyAddRecordViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddRecordActivity extends AppCompatActivity {

    private MyAddRecordViewPagerAdapter myAddRecordViewPagerAdapter;
    private ActivityAddRecordBinding binding;
    private AddRecordViewModel addRecordViewModel;
    private List<String> titles = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_record);
        getSupportActionBar().hide();

        addRecordViewModel = new ViewModelProvider(this).get(AddRecordViewModel.class);

        binding = ActivityAddRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        addListener();
        initPage();
    }

    private void addListener(){
        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    /**
     * 初始化界面
     */
    private void initPage(){
        titles.add("支出");
        titles.add("收入");
        fragments.add(new ExpenditureFragment());
        fragments.add(new IncomeFragment());
        myAddRecordViewPagerAdapter = new MyAddRecordViewPagerAdapter(getSupportFragmentManager(),titles,fragments);
        binding.addRecordViewPager.setAdapter(myAddRecordViewPagerAdapter);
        binding.addRecordTab.setupWithViewPager(binding.addRecordViewPager);
    }

    @Override
    protected void onPause() {
        //设置返回页面动画
        overridePendingTransition(android.R.anim.fade_in,R.anim.slide_out_bottom);
        super.onPause();
    }
}
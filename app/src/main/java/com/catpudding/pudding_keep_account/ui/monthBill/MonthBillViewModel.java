package com.catpudding.pudding_keep_account.ui.monthBill;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MonthBillViewModel extends AndroidViewModel {

    private MutableLiveData<String> monthLeft = new MutableLiveData<>();
    private MutableLiveData<String> monthPay = new MutableLiveData<>();
    private MutableLiveData<String> monthIncome = new MutableLiveData<>();
    private MutableLiveData<String> maxPay = new MutableLiveData<>();
    private MutableLiveData<String> averagePay = new MutableLiveData<>();

    public MonthBillViewModel(@NonNull Application application) {
        super(application);
        if(monthIncome == null){
            monthIncome.setValue("0.00");
            monthPay.setValue("0.00");
            monthLeft.setValue("0.00");
            maxPay.setValue("0.00");
            averagePay.setValue("0.00");
        }
    }

    public MutableLiveData<String> getMonthIncome() {
        return monthIncome;
    }

    public MutableLiveData<String> getMonthLeft() {
        return monthLeft;
    }

    public MutableLiveData<String> getMonthPay() {
        return monthPay;
    }

    public MutableLiveData<String> getAveragePay() {
        return averagePay;
    }

    public MutableLiveData<String> getMaxPay() {
        return maxPay;
    }
}

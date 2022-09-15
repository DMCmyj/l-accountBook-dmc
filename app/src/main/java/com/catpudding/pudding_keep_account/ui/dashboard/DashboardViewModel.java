package com.catpudding.pudding_keep_account.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.catpudding.pudding_keep_account.ui.dashboard.utils.MonthBill;

import java.util.Calendar;
import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private MutableLiveData<String> totalLeft = new MutableLiveData<>();
    private MutableLiveData<String> totalIncome = new MutableLiveData<>();
    private MutableLiveData<String> totalPay = new MutableLiveData<>();
    private MutableLiveData<List<MonthBill>> monthBills = new MutableLiveData<>();
    private MutableLiveData<Integer> currentYear = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        if(totalLeft.getValue() == null){
            totalLeft.setValue("0.00");
            totalIncome.setValue("0.00");
            totalPay.setValue("0.00");
            Calendar calendar = Calendar.getInstance();
            currentYear.setValue(calendar.get(Calendar.YEAR));
        }
    }

    public MutableLiveData<Integer> getCurrentYear() {
        return currentYear;
    }

    public MutableLiveData<List<MonthBill>> getMonthBills() {
        return monthBills;
    }

    public MutableLiveData<String> getTotalPay() {
        return totalPay;
    }

    public MutableLiveData<String> getTotalIncome() {
        return totalIncome;
    }

    public MutableLiveData<String> getTotalLeft() {
        return totalLeft;
    }
}
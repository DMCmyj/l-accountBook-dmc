package com.catpudding.pudding_keep_account.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<Integer> priceType = new MutableLiveData<>();

    public NotificationsViewModel() {
        if(priceType.getValue() == null){
            priceType.setValue(0);
        }
    }

    public MutableLiveData<Integer> getPriceType() {
        return priceType;
    }
}
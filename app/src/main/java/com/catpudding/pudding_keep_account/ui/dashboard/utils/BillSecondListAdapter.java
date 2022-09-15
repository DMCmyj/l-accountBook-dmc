package com.catpudding.pudding_keep_account.ui.dashboard.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.catpudding.pudding_keep_account.R;

import java.text.DecimalFormat;
import java.util.List;

public class BillSecondListAdapter extends BaseAdapter {

    private List<MonthBill> data;
    private LayoutInflater layoutInflater;

    public BillSecondListAdapter(Context context, List<MonthBill> data) {
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View root = layoutInflater.inflate(R.layout.layout_second_bill_item,null);
        TextView month = root.findViewById(R.id.text_month);
        TextView income = root.findViewById(R.id.text_income);
        TextView pay = root.findViewById(R.id.text_pay);
        TextView left = root.findViewById(R.id.text_left);

        month.setText(data.get(i).getMonth() + "月");
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        income.setText(decimalFormat.format(data.get(i).getIncome()));
        pay.setText(decimalFormat.format(data.get(i).getPay()));
        left.setText(decimalFormat.format(data.get(i).getLeft()));
        return root;
    }
}

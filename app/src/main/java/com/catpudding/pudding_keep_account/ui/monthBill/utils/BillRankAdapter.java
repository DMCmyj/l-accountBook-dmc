package com.catpudding.pudding_keep_account.ui.monthBill.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.ui.monthBill.utils.model.MonthRankItem;
import com.catpudding.pudding_keep_account.utils.IconList;

import java.text.DecimalFormat;
import java.util.List;

public class BillRankAdapter extends BaseAdapter {

    List<MonthRankItem> data;
    LayoutInflater layoutInflater;
    Context context;

    public BillRankAdapter(Context context, List<MonthRankItem> data) {
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
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
        View root = layoutInflater.inflate(R.layout.layout_month_bill_rank_item,null);
        TextView tv_rank_number = root.findViewById(R.id.tv_rank_number);
        TextView tv_rank_name = root.findViewById(R.id.tv_rank_name);
        TextView tv_rank_date = root.findViewById(R.id.tv_rank_date);
        TextView tv_rank_price = root.findViewById(R.id.tv_rank_price);
        ImageView rank_img_icon = root.findViewById(R.id.img_rank_icon);

        rank_img_icon.setImageResource(IconList.MY_ICON_LIST[data.get(i).getIconId()].getSource());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        tv_rank_number.setText((i+1) + "");
        if(i+1 <= 3){
            Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
            tv_rank_number.setTypeface(boldTypeface);
            if(i+1 == 1){
                tv_rank_number.setTextColor(context.getResources().getColor(R.color.main_pink));
            }else if(i+1 == 2){
                tv_rank_number.setTextColor(context.getResources().getColor(R.color.main_pink_second));
            }else{
                tv_rank_number.setTextColor(context.getResources().getColor(R.color.main_pink_third));
            }
        }
        tv_rank_name.setText(data.get(i).getName());
        tv_rank_date.setText(data.get(i).getDate());
        tv_rank_price.setText("-" + decimalFormat.format(data.get(i).getPrice()));
        return root;
    }
}

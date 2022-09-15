package com.catpudding.pudding_keep_account.ui.notifications.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.utils.IconList;

import java.text.DecimalFormat;
import java.util.List;

public class MyListAdapter extends BaseAdapter {

    List<RankItem> data;
    Context context;
    LayoutInflater layoutInflater;

    public MyListAdapter(Context context, List<RankItem> data) {
        this.data = data;
        this.context = context;
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
        View root = layoutInflater.inflate(R.layout.bang_item,null);

        ImageView imageView = root.findViewById(R.id.item_type_image);
        TextView tv_name = root.findViewById(R.id.tv_name);
        TextView tv_percent = root.findViewById(R.id.tv_percent);
        TextView tv_price = root.findViewById(R.id.tv_price);
        ProgressBar progressBar = root.findViewById(R.id.progress);

        imageView.setImageResource(IconList.MY_ICON_LIST[data.get(i).getSource()].getSource());
        tv_name.setText(IconList.MY_ICON_LIST[data.get(i).getSource()].getName());
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        tv_percent.setText(decimalFormat.format(data.get(i).getPercentTip())+"%");
        tv_price.setText(decimalFormat.format(data.get(i).getTotalPrice()) + "");
        progressBar.setProgress(data.get(i).getPercent());
        return root;
    }
}

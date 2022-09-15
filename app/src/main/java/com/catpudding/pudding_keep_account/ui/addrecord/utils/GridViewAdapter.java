package com.catpudding.pudding_keep_account.ui.addrecord.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.catpudding.pudding_keep_account.R;
import com.catpudding.pudding_keep_account.utils.IconList;
import com.catpudding.pudding_keep_account.utils.MyIcon;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<MyIcon> list;

    public GridViewAdapter(Context context, List<MyIcon> list) {
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View root = layoutInflater.inflate(R.layout.add_record_grid_item,null);
        ImageView imageView = root.findViewById(R.id.grid_item_icon);
        TextView textView = root.findViewById(R.id.grid_item_title);
        imageView.setImageResource(list.get(i).getSource());
        textView.setText(list.get(i).getName());
        return root;
    }
}

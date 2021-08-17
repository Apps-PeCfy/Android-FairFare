package com.fairfareindia.ui.drawer.adapter;/*
 * Author : Kiran Poman.
 * Module : Navigation Module
 * Version : V 1.0
 * Comments : This class is used to set navigation drawer adapter.
 * Output : Navigation drawer adapter
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fairfareindia.R;
import com.fairfareindia.ui.drawer.pojo.DrawerPojo;

import java.util.ArrayList;


public class DrawerAdapter extends BaseAdapter {
    private ArrayList<DrawerPojo> drawerArrayList;
    private LayoutInflater mInflater;

    public DrawerAdapter(Context mContext, ArrayList<DrawerPojo> drawerArrayList) {
        this.drawerArrayList = drawerArrayList;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_left_drawer, null);
            holder = new ViewHolder();
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.ivIcon = convertView.findViewById(R.id.ivIcon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvTitle.setText(drawerArrayList.get(position).getName());
        holder.ivIcon.setImageResource(drawerArrayList.get(position).getIcon());


        return convertView;
    }

    @Override
    public int getCount() {
        return drawerArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return drawerArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView tvTitle;
        ImageView ivIcon;
        LinearLayout linearLayout;




    }

}
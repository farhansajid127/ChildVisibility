package com.example.jawad.childvisibility;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jawad.childvisibility.Model.ChilData;

import java.util.List;

public class DrawerListAdapter extends BaseAdapter {
    Context context;
    List<ChilData> childList;
    DrawerListAdapter(Context context, List<ChilData> list) {
        this.context = context;
        this.childList = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            int a=(position%6);
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
            final TextView Name = (TextView) convertView.findViewById(R.id.user_name);
            final TextView Email = (TextView) convertView.findViewById(R.id.email);
            final TextView Icon = (TextView) convertView.findViewById(R.id.icon_list_item);
            Name.setText(childList.get(position).getName());
            Email.setText(childList.get(position).getEmail());
            Icon.setText(childList.get(position).getName());
            switch (a){
                case 0:
                    Icon.setBackgroundResource(R.drawable.img_shape);
                    break;
                case 1:
                    Icon.setBackgroundResource(R.drawable.img_shape1);
                    break;

                case 2:
                    Icon.setBackgroundResource(R.drawable.img_shape5);
                    break;

                case 3:
                    Icon.setBackgroundResource(R.drawable.img_shape3);
                    break;

                case 4:
                    Icon.setBackgroundResource(R.drawable.img_shape4);
                    break;
                case 5:
                    Icon.setBackgroundResource(R.drawable.img_shape2);
                    break;

            }
        }
        return convertView;
    }
    @Override
    public int getCount() {
        return childList.size();
    }

    @Override
    public Object getItem(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
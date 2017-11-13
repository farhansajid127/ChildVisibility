package com.example.jawad.childvisibility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jawad.childvisibility.Model.ChilData;

import java.util.List;

public class ChildListAdapter extends BaseAdapter {
    Context context;
   List<ChilData> childList;

    ChildListAdapter(Context context, List<ChilData> list) {
        this.context = context;
        this.childList = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.child_list_item, null);
            final TextView Email = (TextView) convertView.findViewById(R.id.name_user);
            Email.setText(childList.get(position).getEmail());
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
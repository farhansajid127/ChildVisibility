package com.example.jawad.childvisibility.Model;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jawad.childvisibility.R;

public class ToastMessage {
    static Context context;
    public ToastMessage(Context context_){
        context=context_;
    }
    public  static void  ErrorToast(String  message){
        Toast toast = new Toast(context);
        TextView textView = new TextView(context);
        textView.setText(message);
        textView.setTextColor(Color.RED);
        textView.setTextSize(15);
        textView.setPadding(55, 20, 55, 20);
        textView.setBackgroundResource(R.drawable.toast_bg);
        toast.setView(textView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
    public  static void  ErrorToastWhiteBG(String  message){
        Toast toast = new Toast(context);
        TextView textView = new TextView(context);
        textView.setText(message);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(15);
        textView.setPadding(55, 20, 55, 20);
        textView.setBackgroundResource(R.drawable.toast_bg_white);
        toast.setView(textView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
    public  static void  SuccessToast(String  message){
        Toast toast = new Toast(context);
        TextView textView = new TextView(context);
        textView.setText(message);
        textView.setTextColor(Color.YELLOW);
        textView.setTextSize(15);
        textView.setPadding(55, 20, 55, 20);
        textView.setBackgroundResource(R.drawable.toast_bg);
        toast.setView(textView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}

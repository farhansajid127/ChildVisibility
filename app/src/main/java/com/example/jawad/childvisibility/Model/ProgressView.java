package com.example.jawad.childvisibility.Model;

import android.content.Context;
import android.graphics.Color;

import com.kaopiz.kprogresshud.KProgressHUD;

public class ProgressView {
    static  Context context;
    static KProgressHUD progress;
    public ProgressView(Context context_){
        context=context_;
    }
    public static void Show(){
        progress =  KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .setWindowColor(Color.parseColor("#990024"))
                .show();
    }
    public static void Show(String message){
        progress =  KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .setLabel(message)
                .setWindowColor(Color.parseColor("#990024"))
                .show();
    }
    public static void Show(String message,int AnimationSpeed){
        progress =  KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(AnimationSpeed)
                .setDimAmount(0.5f)
                .setWindowColor(Color.parseColor("#990024"))
                .show();
    }
    public static void Dismiss(){
        if(progress.isShowing()){
            progress.dismiss();
        }
    }
}


package com.example.jawad.childvisibility;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.example.jawad.jdmaterialdesign.SharedPreferencesMethod;
import com.example.jawad.jdmaterialdesign.StaticFunctions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.example.jawad.jdmaterialdesign.SharedPreferencesMethod.getDefaultBoolean;
import static com.example.jawad.jdmaterialdesign.StaticFunctions.GoTo;
public class Splash extends AppCompatActivity {
    SharedPreferencesMethod sharedPreferencesMethod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferencesMethod = new SharedPreferencesMethod(Splash.this);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPreferencesMethod.getDefaultBoolean("rb")){
                    GoTo(Splash.this, HomeActivity.class);
                }else {
                    GoTo(Splash.this, LoginActivity.class);
                }
                finish();
            }
        }, 3000);
    }
}

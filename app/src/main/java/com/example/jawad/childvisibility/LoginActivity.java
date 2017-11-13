package com.example.jawad.childvisibility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jawad.childvisibility.Model.ProgressView;
import com.example.jawad.childvisibility.Model.ToastMessage;
import com.example.jawad.jdmaterialdesign.MaterialProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.jawad.jdmaterialdesign.SharedPreferencesMethod.setDefaultBoolean;
import static com.example.jawad.jdmaterialdesign.SharedPreferencesMethod.setDefaultInt;
import static com.example.jawad.jdmaterialdesign.StaticFunctions.GoTo;
import static com.example.jawad.jdmaterialdesign.StaticFunctions.IsEmail;

public class LoginActivity extends AppCompatActivity implements OnClickListener {
    EditText Email;
    EditText Password;
    ToastMessage toast;
    RadioGroup STATUS;
    int PID;
    AppCompatCheckBox RB;
    int status_id = 0;
    boolean remmbrer_me_St= false;
    Button SignInButton, registrationButton;
    ProgressView pd;
    TextInputLayout Email_, Password_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Init();
        RB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                 remmbrer_me_St = b;
            }
        });
    }

    public void Init() {
        RB = (AppCompatCheckBox) findViewById(R.id.remember_me);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        SignInButton = (Button) findViewById(R.id.sign_in_button);
        SignInButton.setOnClickListener(this);
        registrationButton = (Button) findViewById(R.id.register_button);
        registrationButton.setOnClickListener(this);
        pd = new ProgressView(LoginActivity.this);
        toast = new ToastMessage(LoginActivity.this);
        STATUS = (RadioGroup) findViewById(R.id.status);
        STATUS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = radioGroup.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.parent:
                        status_id = 1;

                        break;
                    case R.id.child:
                        status_id = 2;
                        break;
                }
            }
        });
        Email_ = (TextInputLayout) findViewById(R.id.email_text);
        Password_ = (TextInputLayout) findViewById(R.id.password_text);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                if (Email.length() == 0 || Password.length() == 0 || status_id == 0) {
                    if (status_id == 0) {
                        toast.ErrorToast("Select Account Type to login.");
                    }
                    if (Email.length() == 0) {
                        Email_.setError("enter email address!");
                    }
                    if (Password.length() == 0) {
                        Password_.setError("enter password!!");
                    }
                } else {
                    if (IsEmail(Email.getText().toString())) {
                        pd.Show();
                        LoginVarification();
                    } else {
                        Email.requestFocus();
                        Email_.setError("Email is not correct!!");
                    }
                }

                break;
            case R.id.register_button:
                GoTo(LoginActivity.this, Registration.class);
                break;
        }
    }

    public void LoginVarification() {
        final String url = "http://wasifapps.com/login.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("response", jsonObject.getString("status"));
                            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("user_data");
                                JSONObject obj= jsonArray.getJSONObject(0);
                                PID =  obj.getInt("id");
                                setDefaultBoolean("rb",remmbrer_me_St);
                                setDefaultInt("status",status_id);
                                setDefaultInt("PID",PID);
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                pd.Dismiss();
                                toast.ErrorToast("username or password is not correct!!");
                                Password.setText("");
                                Password.requestFocus();
                                Password_.setError("password is not correct");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("response", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error + "");
                toast.ErrorToast("connection error, try again!!");
                pd.Dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parm = new HashMap<>();
                parm.put("username", Email.getText().toString().trim());
                parm.put("password", Password.getText().toString().trim());
                parm.put("status", status_id+"".trim());
                return parm;
            }
        };
        Volley.newRequestQueue(LoginActivity.this).add(stringRequest);
    }
}


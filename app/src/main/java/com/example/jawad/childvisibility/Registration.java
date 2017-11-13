package com.example.jawad.childvisibility;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jawad.childvisibility.Model.ChilData;
import com.example.jawad.childvisibility.Model.ToastMessage;
import com.example.jawad.jdmaterialdesign.MaterialProgress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.jawad.childvisibility.Model.ToastMessage.ErrorToast;
import static com.example.jawad.childvisibility.Model.ToastMessage.SuccessToast;
import static com.example.jawad.jdmaterialdesign.StaticFunctions.IsEmail;

public class Registration extends AppCompatActivity {
    Button Add_Child;
    ListView child_List;
    int x = 228;
    int Status_id = 0;
    ImageView BACK;
    LinearLayout CHILD_LAYOUT;
    RadioGroup STATUS;
    ToastMessage toast;
    DB db;
    MaterialProgress pd;
    List<ChilData> childListArray = new ArrayList<>();
    EditText FirstName, LastName, Username, Email, Password, CnfrmPassword,Child_Email;
    TextInputLayout FirstName_inputlayout, LastName_inputlayout, Child_Email_layout,Username_inputlayout, Email_inputlayout, Password_inputlayout, CnfrmPassword_inputlayout;
    Button Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        pd =  new MaterialProgress(Registration.this);
        db = new DB(Registration.this);
        toast = new ToastMessage(Registration.this);
        Add_Child = (Button) findViewById(R.id.add_child);
        child_List = (ListView) findViewById(R.id.child_list);
        CHILD_LAYOUT = (LinearLayout) findViewById(R.id.add_child_layout);
        STATUS = (RadioGroup) findViewById(R.id.status);
        FirstName = (EditText) findViewById(R.id.firstname);
        LastName = (EditText) findViewById(R.id.lastname);
        Username = (EditText) findViewById(R.id.username);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        Child_Email = (EditText) findViewById(R.id.child_email);
        CnfrmPassword = (EditText) findViewById(R.id.cnfrm_password);
        FirstName_inputlayout = (TextInputLayout) findViewById(R.id.first_name_inputlayout);
        LastName_inputlayout = (TextInputLayout) findViewById(R.id.laste_name_inputlayout);
        Username_inputlayout = (TextInputLayout) findViewById(R.id.username_inputlayout);
        Email_inputlayout = (TextInputLayout) findViewById(R.id.email_inputlayout);
        Password_inputlayout = (TextInputLayout) findViewById(R.id.password_inputlayout);
        CnfrmPassword_inputlayout = (TextInputLayout) findViewById(R.id.cnfrmpassword_inputlayout);
        Child_Email_layout = (TextInputLayout) findViewById(R.id.child_email_layout);
        Register = (Button) findViewById(R.id.register_btn);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirstName.length() == 0 || LastName.length() == 0 || Username.length() == 0 || Email.length() == 0 || Password.length() == 0 || CnfrmPassword.length() == 0 || Status_id==0) {
                    ErrorToast("Enter all field to countinue!!");
                } else {
                    if (IsEmail(Email.getText().toString())) {
                        if (Password.getText().toString().equalsIgnoreCase(CnfrmPassword.getText().toString())) {
                            pd.Show();
                            String url = "http://wasifapps.com/signUpApi.php";
                           StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                   new Response.Listener<String>() {
                                       @Override
                                       public void onResponse(String response) {
                                           try {
                                               JSONObject jsonObject = new JSONObject(response);
                                               Log.d("response", jsonObject.getString("status"));
                                               if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                                   SuccessToast("Registration Successfully!!!");
                                                   Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                   startActivity(intent);
                                                   finish();
                                               } else {
                                                   pd.Dismiss();
                                                   toast.ErrorToast("Connection Error Try Again!!!");
                                               }
                                           } catch (JSONException e) {
                                               e.printStackTrace();
                                               pd.Dismiss();
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
                                   parm.put("f_name", FirstName.getText().toString().trim());
                                   parm.put("l_name", LastName.getText().toString().trim());
                                   parm.put("username", Username.getText().toString().trim());
                                   parm.put("email", Email.getText().toString().trim());
                                   parm.put("password", Password.getText().toString().trim());
                                   parm.put("status", Status_id + "".trim());
                                   return parm;
                               }
                           };
                            Volley.newRequestQueue(Registration.this).add(stringRequest);
                        } else {
                            CnfrmPassword_inputlayout.setError("Password not match!!");
                        }
                    } else {
                        Email_inputlayout.setError("Email is not correcct!!");
                    }
                }
            }
        });
        Add_Child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Child_Email.length()==0){
                    Child_Email_layout.setError("Enter child email");
                    Child_Email.requestFocus();
                }else {
                   if(IsEmail(Child_Email.getText().toString())){
                       pd.Show();
                       String url = "http://wasifapps.com/searchChild.php";
                         StringRequest  stringRequest=  new StringRequest(Request.Method.POST, url,
                                 new Response.Listener<String>() {
                                     @Override
                                     public void onResponse(String response) {
                                         try {
                                             JSONObject jsonObject = new JSONObject(response);
                                             Log.d("response", jsonObject.getString("status"));
                                             if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                                 pd.Dismiss();
                                                 Log.d("height", x + "");
                                                 ViewGroup.LayoutParams params = child_List.getLayoutParams();
                                                 params.height = x;
                                                 child_List.setLayoutParams(params);
                                                 child_List.requestLayout();
                                                 childListArray.add(new ChilData(Child_Email.getText().toString()));
                                                 child_List.setVisibility(View.VISIBLE);
                                                 child_List.setAdapter(new ChildListAdapter(Registration.this, childListArray));
                                                 x = x + 228;
                                                 Log.d("List Size", child_List.getHeight() + "");
                                                 //add to db
                                             } else {
                                                 pd.Dismiss();
                                                 toast.ErrorToast("Child Email is not Exist!!!");
                                             }
                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                             pd.Dismiss();
                                         }
                                         Log.d("response", response);
                                     }
                                 }, new Response.ErrorListener() {
                             @Override
                             public void onErrorResponse(VolleyError error) {
                                 pd.Dismiss();
                                 toast.ErrorToast("Connection Error Try Again!!!");
                             }
                         }){
                             @Override
                             protected Map<String, String> getParams() throws AuthFailureError {
                                 HashMap<String, String> parm = new HashMap<>();
                                 parm.put("email", Child_Email.getText().toString().trim());
                                 return parm;
                             }
                         };
                       Volley.newRequestQueue(Registration.this).add(stringRequest);
                   }else {
                       Email_inputlayout.setError("Email is not correct!!!");
                   }
                }
            }
        });
        STATUS.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton parent = (RadioButton) STATUS.findViewById(R.id.parent);
                if (parent.isChecked()) {
                    Status_id=1;
                } else {
                    Status_id=2;
                }
            }
        });
        BACK = (ImageView) findViewById(R.id.back_btn);
        BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

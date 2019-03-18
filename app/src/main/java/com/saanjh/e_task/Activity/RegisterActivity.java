package com.saanjh.e_task.Activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.messaging.FirebaseMessaging;
import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Rest.HttpGetAsyncTask;
import com.saanjh.e_task.R;
import com.saanjh.e_task.Utils.ExceptionHandling;
import com.saanjh.e_task.Utils.Validation;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;

public class RegisterActivity extends AppCompatActivity implements HttpGetAsyncTask.AsyncResponse {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static String url = Config.MAIN_URL + Config.URL_INSERT_QUERY+"?";
    TextInputLayout txt_user, txt_email, txt_password, txt_cpassword, txt_mobile, txt_code;
    EditText user_name, email, pwd, c_password, mobile_no, company_code;
    Button save;
    private Context context;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String regId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        user_name= (EditText) findViewById(R.id.u_name);
        email = (EditText) findViewById(R.id.email);
        pwd = (EditText) findViewById(R.id.password);
        c_password = (EditText) findViewById(R.id.c_paswword);
        mobile_no = (EditText) findViewById(R.id.mobile);
        company_code = (EditText) findViewById(R.id.company_code);
        save = (Button) findViewById(R.id.save);
        txt_user = (TextInputLayout) findViewById(R.id.u_name_text);
        txt_email = (TextInputLayout) findViewById(R.id.email_text);
        txt_password = (TextInputLayout) findViewById(R.id.password_text);
        txt_cpassword = (TextInputLayout) findViewById(R.id.c_password_text);
        txt_mobile = (TextInputLayout) findViewById(R.id.mobile_text);
        txt_code = (TextInputLayout) findViewById(R.id.c_company_code);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Register");

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();
                }
            }
        };
        displayFirebaseRegId();
        user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (user_name.getText().length() > 0) {
                    user_name.setError(null);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        mobile_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (mobile_no.getText().length() != 0) {
                    mobile_no.setError(null);
                }
            }
        });


        company_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (company_code.getText().length() != 0) {

                    company_code.setError(null);
                }
            }
        });

        email.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                boolean e = Validation.email_Validation(email.getText().toString());
                if (email.getText().length() != 0) {
                    if (e == false) {
                        email.setError(Html.fromHtml("<font color='red'>Invalid Email Address!!</font>"));
                    }
                    if (e == true) {
                        email.setError(null);
                    }
                }
            }
        });

        pwd.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pwd.getText().length() != 0) {
                    pwd.setError(null);
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String companycode=company_code.getText().toString();
                byte[] CompanyCodeEncode = new byte[0];
                try {
                    CompanyCodeEncode = Base64.encode(companycode.getBytes("UTF-8"), Base64.DEFAULT);
                } catch (UnsupportedEncodingException e) {
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                    e.getStackTrace()[0].getLineNumber();
                }
                String encodeCompanyCode = new String(CompanyCodeEncode);
                String password=pwd.getText().toString();

                byte[] PasswordEncode = new byte[0];
                try {
                    PasswordEncode = Base64.encode(password.getBytes("UTF-8"), Base64.DEFAULT);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                    }
                String encodePassword = new String(PasswordEncode);

                String user=user_name.getText().toString();
                byte[] userEncode = new byte[0];
                try {
                    userEncode = Base64.encode(user.getBytes("UTF-8"), Base64.DEFAULT);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                }
                String encodeuser = new String(userEncode);
                String email1=email.getText().toString();
                byte[] emailEncode = new byte[0];
                try {
                    emailEncode = Base64.encode(email1.getBytes("UTF-8"), Base64.DEFAULT);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                }

                String encodeEmail = new String(emailEncode);
                String mobil_no=mobile_no.getText().toString();
                boolean mob_no = Validation.mobile_Validation(mobil_no);
                byte[] encodeValue = new byte[0];
                try {
                    encodeValue = Base64.encode(mobil_no.getBytes("UTF-8"), Base64.DEFAULT);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                }
                String encode = new String(encodeValue);
                if (user_name.getText().length() == 0) {
                    txt_user.setErrorEnabled(true);
                    txt_user.setError(Html.fromHtml("<font color='red'>Name is Required</font>"));
                    user_name.requestFocus();
                } else if (mobil_no.length() < 10) {
                    txt_mobile.setErrorEnabled(true);
                    txt_mobile.setError(Html.fromHtml("<font color='red'>Mobile Number is Required</font>"));
                    mobile_no.requestFocus();
                } else if (!mob_no) {

                    txt_mobile.setErrorEnabled(true);
                    txt_mobile.setError(Html.fromHtml("<font color='red'>Invalid Mobile Number</font>"));
                    mobile_no.requestFocus();
                } else if (email.getError() != null) {
                    txt_code.setErrorEnabled(true);
                    txt_email.setError(Html.fromHtml("<font color='red'>Invalid Email</font>"));
                    //Toast.makeText(getBaseContext(), "Clear Errors in Email Address!!", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                } else if (pwd.getText().length() == 0) {
                    txt_password.setErrorEnabled(true);
                    txt_password.setError(Html.fromHtml("<font color='red'>Password is Required!!</font>"));
                    pwd.requestFocus();
                } else if (pwd.getText().length() < 6) {
                    txt_password
                            .setError(Html.fromHtml("<font color='red'>Password at least six character long!!</font>"));
                    pwd.requestFocus();
                } else if (!pwd.getText().toString().equals(c_password.getText().toString())) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Your Confirm Password is not matched!!Please Try Again");
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            c_password.setText("");
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                } else {
                    try {
                        HttpGetAsyncTask http = new HttpGetAsyncTask(RegisterActivity.this, 3);
                        http.delegate = RegisterActivity.this;

                        // String str_query = "insert into task_manager_app (user_name,mobile_no,email,pwd,company_code,gcm_token)values('" + user_name.getText().toString() + "','" + mobile_no.getText().toString() + "','" + email.getText().toString() + "','" + pwd.getText().toString() + "','" + company_code.getText().toString() + "','"+regId+"')";
                      //  JSONObject jsonObject = new JSONObject();
                      //  jsonObject.put("str_query", str_query);
//                            jsonObject.put("user_name", user_name.getText().toString());
//                            jsonObject.put("email", encodeEmail);
//                            jsonObject.put("comapny_code", company_code.getText().toString());
//                            jsonObject.put("pwd", pwd.getText().toString());
//                            jsonObject.put("mobile_no", encode);
//                            jsonObject.put("gcm_token", regId);
//                            Log.d("user",jsonObject.toString());
                      // url= Uri.parse(url).buildUpon().appendQueryParameter(jsonObject.toString(), "UTF-8").build().toString();

                       // System.out.println(url + jsonObject.toString());
                        //url.replaceAll(" ","%20");
                            //http.execute(url + URLEncoder.encode(jsonObject.toString(), "UTF-8"));
                          http.execute(url.trim()+"user_name="+encodeuser.trim()+"&pwd="+encodePassword.trim()+"&email="+encodeEmail.trim()+"&mobile_no="+encode.trim()+"&company_code="+encodeCompanyCode.trim()+"&gcm_token="+regId);


                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        String ex=e.toString();
                        String line_number= String.valueOf(e.getStackTrace()[0]);
                        ExceptionHandling exceptionHandling=new ExceptionHandling();
                        exceptionHandling.handler(ex,line_number,context);
                    }
                }
            }
        });
    }
    public void displayExceptionMessage(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void processFinish(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            System.out.println(result);
            Log.d("data",result.toString());
            if (jsonObject.getString("status").equals("error")) {
                Toast.makeText(getApplicationContext(), "Value not Inserted", Toast.LENGTH_LONG).show();
            } else if (jsonObject.getString("status").equals("Exist")) {

                Toast.makeText(getApplicationContext(), "Mobile Number Already Exist", Toast.LENGTH_LONG).show();
            } else if(jsonObject.getString("status").equals("Success")){
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
        }
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Log.e(TAG, "Firebase Reg Id: " + regId);
        else
            Log.e(TAG, "Firebase Reg Id is not received yet!");
    }
    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
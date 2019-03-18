package com.saanjh.e_task.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.Rest.HttpGetAsyncTask;
import com.saanjh.e_task.R;
import com.saanjh.e_task.Utils.ExceptionHandling;
import com.saanjh.e_task.Utils.ServiceHandler;
import com.saanjh.e_task.Utils.Utilities;
import com.saanjh.e_task.Utils.Validation;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements HttpGetAsyncTask.AsyncResponse {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static String url = Config.MAIN_URL + Config.URL_LOGIN + "?", str_result = "Error";
    String str_mobno, str_name = "", str_empid = "", regId = "", str_pwd = "",str_company="";
    EditText MOBILE, PASSWORD;
    Button register, login;
    TextView forgotPassword;
    private Context context;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ProgressDialog progDialog;
    int progressBarStatus = 0;
    AppLockerPreference appLockerPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        context = this;
        MOBILE = (EditText) findViewById(R.id.l_id);
        PASSWORD = (EditText) findViewById(R.id.l_pwd);
        register = (Button) findViewById(R.id.reg);
        login = (Button) findViewById(R.id.login);
        forgotPassword = (TextView)findViewById(R.id.forgot_password);
        appLockerPreference=new AppLockerPreference(LoginActivity.this);

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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password=PASSWORD.getText().toString();

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


                String mobil_no=MOBILE.getText().toString();
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
                String encodeMobile = new String(encodeValue);


                boolean mob_no = Validation.mobile_Validation(MOBILE.getText().toString());

                if (MOBILE.getText().equals("")) {

                    MOBILE.setError(Html.fromHtml("<font color='red'>Mobile Number must be Required</font>"));
                    MOBILE.requestFocus();

                } else if (!mob_no) {
                    MOBILE.setError(Html.fromHtml("<font color='red'>Invalid Mobile Number</font>"));
                    MOBILE.requestFocus();
                } else if (PASSWORD.getText().equals("")) {

                    PASSWORD.setError(Html.fromHtml("<font color='red'>Password must be Required</font>"));
                    PASSWORD.requestFocus();
                } else if (PASSWORD.getText().length() < 6) {
                    PASSWORD.setError(Html.fromHtml("<font color='red'>Password at least six character long!!</font>"));
                    PASSWORD.requestFocus();
                } else {
                    HttpGetAsyncTask http = new HttpGetAsyncTask(LoginActivity.this, 3);
                    http.delegate = LoginActivity.this;

                    try {
                        str_pwd = PASSWORD.getText().toString();
//                        String str_query = "select user_name,mobile_no,email,pwd,company_code,sr_no from task_manager_app where mobile_no='"
//                                + MOBILE.getText().toString() + "' and pwd= '" + str_pwd
//                                + "'";
                        http.execute(url + "mobile_no="+encodeMobile.trim()+"&pwd="+encodePassword.trim());


                    } catch (Exception e) {
                        e.printStackTrace();
                        String ex=e.toString();
                        String line_number= String.valueOf(e.getStackTrace()[0]);
                        ExceptionHandling exceptionHandling=new ExceptionHandling();
                        exceptionHandling.handler(ex,line_number,context);
                    }
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void processFinish(String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);
            Log.d("response",result);

            if (jsonObject.getString("exist").equals("NO")) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                alertDialog.setCancelable(false);

                alertDialog.setMessage("Wrong User mobile or password!!");

                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PASSWORD.setText("");
                        PASSWORD.requestFocus();
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            } else {

                str_name = jsonObject.getString("user_name");
                str_mobno = jsonObject.getString("mobile_no");
                str_empid = jsonObject.getString("emp_id");
                str_company=jsonObject.getString("company_code");
                System.out.println("value="+str_name+"mobile"+str_mobno+"emp_id"+str_empid+"str_company="+str_company);
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        progDialog = new ProgressDialog(context);
                        progDialog.setMessage("Please wait.");
                        progDialog.setCancelable(false);
                        progDialog.setIndeterminate(true);
                        progDialog.show();
                        progressBarStatus = 0;
                    }

                    @Override
                    protected Void doInBackground(Void... arg0) {
                        try {
                            progressBarStatus = doSomeWork(str_empid, regId);
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        try {
                            if (progDialog != null) {
                                progDialog.dismiss();
                                if (str_result.equals("Success")) {
                                    appLockerPreference.getInstance(context).setM_username(str_name);
                                    appLockerPreference.getInstance(context).setstr_mobno(str_mobno);
                                    appLockerPreference.getInstance(context).setM_empid(str_empid);
                                    appLockerPreference.getInstance(context).setM_company(str_company);
                                    System.out.println("emp_id"+str_empid+"mobile_no"+str_mobno+"username"+str_name+"Comapany="+str_company);
                                    Log.e(TAG, "Account created");
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(i);
                                } else {
                                   // Toast.makeText(getApplicationContext(), "gcm token not inserted", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    finish();
                                    startActivity(i);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            String ex=e.toString();
                            String line_number= String.valueOf(e.getStackTrace()[0]);
                            ExceptionHandling exceptionHandling=new ExceptionHandling();
                            exceptionHandling.handler(ex,line_number,context);
                        }
                    }

                };
                task.execute((Void[]) null);

            }
        } catch (Exception e) {
            e.printStackTrace();
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
        }
    }

    private int doSomeWork(String empid, String token) {
        if (Utilities.hasConnection(getBaseContext())) {
            try {
                ServiceHandler sh = new ServiceHandler();
                String jsonStr = "";
                jsonStr = execute_query(empid, token,context);
                Log.d("Response1: ", "> " + jsonStr);
                if (jsonStr != null && !jsonStr.equals("Error")) {
                  //  System.out.println("jsonstr="+jsonStr);
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    System.out.println();
                    if (jsonObject.getString("status").equals("success")) {
                        str_result = "Success";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                String ex=e.toString();
                String line_number= String.valueOf(e.getStackTrace()[0]);
                ExceptionHandling exceptionHandling=new ExceptionHandling();
                exceptionHandling.handler(ex,line_number,context);
            }
        }
        return 100;
    }

    public static String execute_query(String str_empid, String token,Context context) {
        String str1 = "";

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Config.MAIN_URL + Config.URL_UPDATE_QUERY);
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 25000);
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), 25000);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            nameValuePairs.add(new BasicNameValuePair("str_empid", str_empid));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
            String responseBody = EntityUtils.toString(response.getEntity());
            str1 = responseBody.trim();
            Utilities.LogDebug(str1);
        } catch (Exception e) {
            Utilities.LogDebug("error " + e.getMessage());
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
        }
        return str1;
    }

    // Fetches reg id from shared preferences
    //    // and displays on the screen
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
}
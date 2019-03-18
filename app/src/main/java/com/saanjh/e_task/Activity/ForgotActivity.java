package com.saanjh.e_task.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.R;
import com.saanjh.e_task.Rest.JSONParser;
import com.saanjh.e_task.Utils.ExceptionHandling;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForgotActivity extends AppCompatActivity {


    // Progress Dialog
//    private ProgressDialog pDialog;
//    JSONParser jsonParser = new JSONParser();
   EditText mobile;
  Button submit;
    private String Mobile;
    Context context;
//    // JSON Node names
//    private static final String TAG_STATUS = "status";
//    private static final String TAG_OTP = "otp";
//    private static final String TAG_ERROR = "error_msg";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ActionBar actionbar = getSupportActionBar();
//        actionbar.setTitle("Forgot Password");
//         context=this;
//
        mobile = (EditText) findViewById(R.id.forgot_mobile);
        submit = (Button) findViewById(R.id.forgot_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mobile = mobile.getText().toString();
                if (Mobile.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill mobile number", Toast.LENGTH_LONG).show();
                } else if (Mobile.length() < 10) {
                    Toast.makeText(getApplicationContext(), "Please fill valid mobile number", Toast.LENGTH_LONG).show();
                } else {
                    // creating api call in background thread
                   // new ForgotPassword().execute();
                    Intent intent = new Intent(ForgotActivity.this, OtpActivity.class);
                    intent.putExtra("mobile", Mobile);
                    startActivity(intent);
                }
            }
        });
//    }
//
//    /**
//     * Background Async Task to Create new product
//     */
//    public class ForgotPassword extends AsyncTask<String, String, String> {
//
//        /**
//         * Before starting background thread Show Progress Dialog
//         */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(ForgotActivity.this);
//            pDialog.setMessage("Loading..");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//        }
//
//        /**
//         * Creating product
//         */
//        protected String doInBackground(String... args) {
//
//            // Building Parameters
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("str_mobile", Mobile));
//
//            // getting JSON Object
//            // Note that create product url accepts POST method
//            JSONObject json = jsonParser.makeHttpRequest(Config.MAIN_URL + Config.URL_FORGOT,
//                    "POST", params);
//
//            // check log cat fro response
//            Log.e("Create Response", json.toString());
//
//            // check for success tag
//            try {
//                String status = json.getString(TAG_STATUS);
//                if (status.equals("success")) {
//                    String otp = json.getString(TAG_OTP);
//                    // successfully go otp screen
//                    Intent i = new Intent(getApplicationContext(), OtpActivity.class);
//                    i.putExtra("otp", otp);
//                    i.putExtra("mobile", Mobile);
//                    startActivity(i);
//
//                } else {
//                    final String error = json.getString(TAG_ERROR);
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                String ex=e.toString();
//                String line_number= String.valueOf(e.getStackTrace()[0]);
//                ExceptionHandling exceptionHandling=new ExceptionHandling();
//                exceptionHandling.handler(ex,line_number,context);
//            }
//
//            return null;
//        }
//
//        /**
//         * After completing background task Dismiss the progress dialog
//         **/
//        protected void onPostExecute(String file_url) {
//            // dismiss the dialog once done
//            pDialog.dismiss();
//        }
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

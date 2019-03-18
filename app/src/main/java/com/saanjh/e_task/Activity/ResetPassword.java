package com.saanjh.e_task.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.R;
import com.saanjh.e_task.Rest.JSONParser;
import com.saanjh.e_task.Utils.ExceptionHandling;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ResetPassword extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    EditText password, confirmPassword;
    TextInputLayout txt_password;
	private static String Mobilee;
    Button submit;
    ResetPassword context;
    private String Password, ConfirmPassword, Mobile,encodePassword;
    // JSON Node names
    private static final String TAG_STATUS = "status";
    private static final String MSG = "tag_msg";
    private static final String TAG_ERROR = "error_msg";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  Context context = getApplicationContext();
        setContentView(R.layout.resetpassword);
        context=this;
		Mobilee = AppLockerPreference.getInstance(context).getstr_mobno();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Change Password");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Mobile = bundle.getString("mobile");
            System.out.println("Mobile"+Mobile);
        }
        else {
            Toast.makeText(ResetPassword.this,"null mobile number",Toast.LENGTH_LONG).show();
        }

        password = (EditText) findViewById(R.id.newPassword);
        txt_password=(TextInputLayout)findViewById(R.id.password_text);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        submit = (Button) findViewById(R.id.reset_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Password = password.getText().toString().trim();
                byte[] PasswordEncode = new byte[0];
                try {
                    PasswordEncode = Base64.encode(Password.getBytes("UTF-8"), Base64.DEFAULT);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                }
                 encodePassword = new String(PasswordEncode);
                ConfirmPassword = confirmPassword.getText().toString().trim();
                System.out.println(Password+"confirm=="+ConfirmPassword);
                if (Password.isEmpty() || ConfirmPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill mobile number.", Toast.LENGTH_LONG).show();
                } else if (!Password.equals(ConfirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Password not match.", Toast.LENGTH_LONG).show();
                } else if (password.getText().length() == 0) {
                    txt_password.setErrorEnabled(true);
                    txt_password.setError(Html.fromHtml("<font color='red'>Password is Required!!</font>"));
                    password.requestFocus();
                } else if (password.getText().length() < 6) {
                    txt_password.setError(Html.fromHtml("<font color='red'>Password at least six character long!!</font>"));
                    password.requestFocus();
                }
                else {
                    // creating api call in background thread
                    new ResetPass().execute();
                }
            }
        });
    }

    /***  Background Async Task to Create new product */
    public class ResetPass extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ResetPassword.this);
            pDialog.setMessage("Loading..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
//            pDialog.show();
        }

        /**
         * Creating product
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("str_password",encodePassword.trim()));
            System.out.println("pass_reset"+encodePassword.trim());
            params.add(new BasicNameValuePair("str_mobile", Mobile));
            System.out.println("mobile_Reset="+Mobile);

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(Config.MAIN_URL + Config.URL_RESET_PASSWORD,
                    "POST", params);

            System.out.println("json="+json.toString());
            // check log cat fro response
            Log.e("Reset Response", json.toString());

            // check for success tag
            try {
                String status = json.getString(TAG_STATUS);

                if (status.equals("success")) {
                    final String Message = json.getString(MSG);
                    // successfully created product
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    final String Error = json.getString(TAG_ERROR);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), Error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
                String ex=e.toString();
                String line_number= String.valueOf(e.getStackTrace()[0]);
                ExceptionHandling exceptionHandling=new ExceptionHandling();
                exceptionHandling.handler(ex,line_number,context);
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
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

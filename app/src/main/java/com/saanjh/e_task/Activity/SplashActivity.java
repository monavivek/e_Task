package com.saanjh.e_task.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.R;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    private Context context;
    private String m_reg = "";
    private String m_company = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        m_reg = AppLockerPreference.getInstance(this).getstr_mobno();
        m_company = AppLockerPreference.getInstance(this).getM_username();
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!m_reg.equals("") && !m_company.equals("")) {
                    // User is already logged in. Take him to main activity
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }


}











package com.saanjh.e_task.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class AppLockerPreference implements OnSharedPreferenceChangeListener {

    private static final String PREF_mreg = "str_mobno";
    private static final String PREF_COMPANY = "COMPANY";
    private static final String PREF_user = "str_name";
    private static final String PREF_empid="str_empid";
    private static AppLockerPreference mInstance;
    private SharedPreferences mPref;
    private String[] mApplicationList;
    private String str_mobno;
    private String m_company;
    private String str_name;
    private String str_empid;

    public AppLockerPreference(Context context) {
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
        mPref.registerOnSharedPreferenceChangeListener(this);
        reloadPreferences();
    }

    public static AppLockerPreference getInstance(Context context) {
        return mInstance == null ? (mInstance = new AppLockerPreference(context))
                : mInstance;
    }

    public String getM_empid() {
        return str_empid;
    }

    public String setM_empid(String str_empid) {
        this.str_empid = str_empid;
        mPref.edit().putString(PREF_empid, str_empid).commit();
        System.out.println(str_empid);
        return str_empid;
    }

    public String getM_username() {
        return str_name;
    }

    public String setM_username(String str_name) {
        this.str_name = str_name;
        System.out.println("prefuser="+PREF_user);
        mPref.edit().putString(PREF_user, str_name).commit();
        return str_name;
    }

    public String getM_company() {
        return m_company;
    }

    public String setM_company(String m_company) {
		this.m_company = m_company;
        System.out.println("PREF_COMPANY="+PREF_COMPANY);
        mPref.edit().putString(PREF_COMPANY, m_company).commit();
        return m_company;
    }

    public String getstr_mobno() {
        return str_mobno;


    }

    public String setstr_mobno(String str_mobno) {
        this.str_mobno = str_mobno;
        mPref.edit().putString(PREF_mreg, str_mobno).commit();
        return str_mobno;
    }

    private void reloadPreferences() {
        str_empid=mPref.getString(PREF_empid,"str_empid");
        System.out.println("str_empid"+ str_empid);
        str_name=mPref.getString(PREF_user,"");
        m_company = mPref.getString(PREF_COMPANY, "Taskmanager");
        str_mobno = mPref.getString(PREF_mreg, "");
        System.out.println("m_company="+ m_company);



    }

    public String[] getApplicationList() {
        return mApplicationList;
    }

    public void saveApplicationList(String[] applicationList) {
        mApplicationList = applicationList;
        String combined = "";
        for (int i = 0; i < mApplicationList.length; i++) {
            combined = combined + mApplicationList[i] + ";";
        }

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        reloadPreferences();
    }
}
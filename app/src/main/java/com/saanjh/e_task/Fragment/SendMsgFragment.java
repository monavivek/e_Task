package com.saanjh.e_task.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.Model.BeanParameter;
import com.saanjh.e_task.Adapter.EmployeMsgAdaptor;
import com.saanjh.e_task.Rest.HttpGetAsyncTaskFragment;
import com.saanjh.e_task.R;
import com.saanjh.e_task.Utils.ExceptionHandling;
import com.saanjh.e_task.Utils.ServiceHandler;
import com.saanjh.e_task.Utils.Utilities;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SendMsgFragment extends Fragment {

    public static EditText EMP_LIST, MSG;
    ListView employee_list;
    CheckBox SELECT_ALL;
    public String token = "";
    private static String url = Config.MAIN_URL + Config.URL_GCM_TOKEN_ALL+"?";
    public static String EMP_SRNO = "";
    EmployeMsgAdaptor adapter;
    public static String token_id = "", ASSIGN_BY, str_msg = "";
    String str_result = "";
    Button SEND;
    static String sr_no="";
    ProgressDialog progDialog;
    private static int progressBarStatus = 0;
    private static String url2 = Config.MAIN_URL + Config.URL_EMPLOYEE_LIST + "?";
    List<BeanParameter> list_array_task = new ArrayList<BeanParameter>();
    Context context;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.send_msg_fragment, container, false);
        context = getActivity();
        sr_no=AppLockerPreference.getInstance(context).getM_empid();
        ASSIGN_BY = AppLockerPreference.getInstance(context).getM_username();
        EMP_LIST = (EditText) rootView.findViewById(R.id.list_dilog_msg);
        SELECT_ALL = (CheckBox) rootView.findViewById(R.id.chk_all);
        MSG = (EditText) rootView.findViewById(R.id.notify_msgg);
        SEND = (Button) rootView.findViewById(R.id.asiign_sendmsg);
        EMP_LIST.setKeyListener(null);

        final HttpGetAsyncTaskFragment http = new HttpGetAsyncTaskFragment(getActivity(), new AssignTaskFragment.FragmentCallback() {

            public void onTaskDone(String result) {

                try {

                    JSONObject jsonObject = new JSONObject(result);
                    System.out.println(result);
                    if (jsonObject.getString("status").equals("Error")) {
                        Toast.makeText(getActivity(), "Nothing to Display", Toast.LENGTH_LONG).show();

                    } else {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            System.out.println(jsonObject1.getString("srno"));
                            BeanParameter beanParameter = new BeanParameter();
                            beanParameter.setStr_ename(jsonObject1.getString("employe_name"));
                            beanParameter.setStr_assign_to_id(jsonObject1.getString("srno"));
                            beanParameter.setStr_serial(jsonObject1.getString("sr_no"));
                            beanParameter.setStr_token_employee(jsonObject1.getString("gcm_token"));

                            list_array_task.add(beanParameter);
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
        }, 3);

        try {

           // String str_query = "select * from task_manager_app where coalesce(null,user_name,'')<>'admin' order by user_name desc";
            http.execute(url2 + URLEncoder.encode("UTF-8"));


        } catch (Exception e) {
            e.printStackTrace();
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
        }

        EMP_LIST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setCancelable(false);
                View dialogView = inflater.inflate(R.layout.employee_l, null);
                alertDialog.setView(dialogView);

                employee_list = (ListView) dialogView.findViewById(R.id.emp_list);
                adapter = new EmployeMsgAdaptor(getActivity(), R.layout.employee_list, list_array_task);
                employee_list.setAdapter(adapter);

                alertDialog.setPositiveButton("Select", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

        SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SELECT_ALL.isChecked()) {

                    HttpGetAsyncTaskFragment http = new HttpGetAsyncTaskFragment(getActivity(), new AssignTaskFragment.FragmentCallback() {

                        public void onTaskDone(String result) {

                            try {
                                JSONObject jsonObject = new JSONObject(result);

                                if (jsonObject.getString("status").equals("Error")) {

                                } else {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        token = jsonObject1.getString("gcm_token") + ",";


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
                    }, 3);

                    try {
                      //  String str_query = "select gcm_token from task_manager_app where gcm_token<>''";
                        http.execute(url + URLEncoder.encode("UTF-8"));

                    } catch (Exception e) {
                        e.printStackTrace();
                        String ex=e.toString();
                        String line_number= String.valueOf(e.getStackTrace()[0]);
                        ExceptionHandling exceptionHandling=new ExceptionHandling();
                        exceptionHandling.handler(ex,line_number,context);
                    }
                    try {
                        System.out.println("----->>>" + token);
                        str_msg = MSG.getText().toString();
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
                                    progressBarStatus = doSomeWork(ASSIGN_BY, str_msg, token);
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    String ex=e.toString();
                                    String line_number= String.valueOf(e.getStackTrace()[0]);
                                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                                    exceptionHandling.handler(ex,line_number,context);
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                try {
                                    System.out.println(progDialog);
                                    if (progDialog != null) {
                                        progDialog.dismiss();
                                        if (str_result.equals("Success")) {
                                            EMP_LIST.setText("");
                                            MSG.setText("");
                                            SELECT_ALL.setChecked(false);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        String ex=e.toString();
                        String line_number= String.valueOf(e.getStackTrace()[0]);
                        ExceptionHandling exceptionHandling=new ExceptionHandling();
                        exceptionHandling.handler(ex,line_number,context);
                    }

                } else {
                    try {
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
                                    progressBarStatus = doSomeWork(ASSIGN_BY, str_msg, token_id);
                                    System.out.println("-->>" + str_msg);
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    String ex=e.toString();
                                    String line_number= String.valueOf(e.getStackTrace()[0]);
                                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                                    exceptionHandling.handler(ex,line_number,context);
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                try {
                                    System.out.println(progDialog);
                                    if (progDialog != null) {
                                        progDialog.dismiss();
                                        if (str_result.equals("Success")) {

                                            EMP_LIST.setText("");
                                            MSG.setText("");


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

        return rootView;
    }

    private int doSomeWork(String str_assign, String str_msg, String str_token) {
        if (Utilities.hasConnection(getActivity())) {
            try {
                ServiceHandler sh = new ServiceHandler();
                String jsonStr = "";
                jsonStr = execute_query(str_assign, str_msg, str_token,context);
                Log.d("Response: ", "> " + jsonStr);
                if (jsonStr != null && !jsonStr.equals("error")) {
                    System.out.println(jsonStr);
                    JSONObject jsonObject = new JSONObject();
                    if (jsonObject.getString("status").equalsIgnoreCase("successs")) {
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

    public static String execute_query(String str_assign, String str_msg, String str_token,Context context) {
        String str1 = "";
        HttpClient httpclient = new DefaultHttpClient();
        String url=Config.MAIN_URL + Config.URL_GCM_ANDROID_SINGLE;
        HttpPost httppost = new HttpPost(url);
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 25000);
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), 25000);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
           // nameValuePairs.add(new BasicNameValuePair("employee_id",sr_no));

                nameValuePairs.add(new BasicNameValuePair("str_assign", str_assign));
            nameValuePairs.add(new BasicNameValuePair("subject", str_msg));
          //  nameValuePairs.add(new BasicNameValuePair("token", str_token));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            System.out.println("notiofication="+url);
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
}

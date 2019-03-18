package com.saanjh.e_task.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.Model.BeanParameter;
import com.saanjh.e_task.Activity.EditTaskActivity;
import com.saanjh.e_task.Fragment.MyAssignedTaskListFragment;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyAssignTaskAdapter extends ArrayAdapter<BeanParameter> {

    private Context context;
    private String str_result = "error";
    private ProgressDialog progDialog;
    private static String ASSIGN_BY;
    public static String token_id = "";
    int progressBarStatus = 0;
    private List<BeanParameter> list_array_task;
    public String name="";

    static String sr_no="";

    public MyAssignTaskAdapter(Context context, int textViewResourceId, List<BeanParameter> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.list_array_task = objects;
    }

    @Override
    public int getCount() {
        return this.list_array_task.size();
    }

    @Override
    public BeanParameter getItem(int index) {
        return list_array_task.get(index);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getViewTypeCount() {
        if (getCount() < 1) {
            return 1;
        } else {
            return getCount();
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        final Holder holder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.assign_task_list, null);
            holder = new Holder();

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        final BeanParameter bean = list_array_task.get(position);

        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        token_id =bean.getStr_token_employee();

        //token_id=Config.REGISTRATION_COMPLETE;
        ASSIGN_BY = AppLockerPreference.getInstance(context).getM_username();
        sr_no=bean.getStr_assign_task_srno();
        holder.txt_assignto = (TextView) row.findViewById(R.id.txt_assignto);
        holder.txt_status = (TextView) row.findViewById(R.id.txt_status);
        holder.txt_sub = (TextView) row.findViewById(R.id.txt_sub);
        holder.txt_activity_date = (TextView) row.findViewById(R.id.txt_activity_date);
        holder.seekBar = (ImageView) row.findViewById(R.id.seek);
        holder.txt_target_date = (TextView) row.findViewById(R.id.txt_target_date);
        holder.txt_edit = (TextView) row.findViewById(R.id.txt_edit);
        holder.txt_del = (TextView) row.findViewById(R.id.txt_del);
        holder.txt_send = (TextView) row.findViewById(R.id.txt_sent);
        holder.img_seen = (ImageView) row.findViewById(R.id.img_assigntask_seen);
        holder.btn_del = (ImageButton) row.findViewById(R.id.task_del);
        holder.btn_edit = (ImageButton) row.findViewById(R.id.task_edit);
        holder.btn_send = (ImageButton) row.findViewById(R.id.task_sent);


        if (bean.getStr_assign_task_chksent().equalsIgnoreCase("1")) {
            holder.btn_del.setVisibility(View.GONE);
            holder.btn_edit.setVisibility(View.GONE);
            holder.btn_send.setVisibility(View.GONE);
            holder.txt_del.setVisibility(View.GONE);
            holder.txt_edit.setVisibility(View.GONE);
            holder.txt_send.setVisibility(View.GONE);
        } else {
            holder.btn_del.setVisibility(View.VISIBLE);
            holder.btn_edit.setVisibility(View.VISIBLE);
            holder.btn_send.setVisibility(View.VISIBLE);
            holder.txt_del.setVisibility(View.VISIBLE);
            holder.txt_edit.setVisibility(View.VISIBLE);
            holder.txt_send.setVisibility(View.VISIBLE);
        }

        holder.btn_edit.setTag(position);
        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditTaskActivity.class);
                intent.putExtra("emp_name", (bean.getStr_assigner_name()));
                intent.putExtra("sub", (bean.getStr_assigntask_sub()));
                intent.putExtra("t_date", (bean.getStr_target_date()));
                intent.putExtra("c_name", bean.getStr_assign_task_c_name());
                intent.putExtra("msg", bean.getStr_assign_task_msg());
                intent.putExtra("image_path", bean.getStr_assign_task_image_path());
                intent.putExtra("attach", bean.getStr_assign_task_attach());
                intent.putExtra("priority", bean.getStr_assign_task_priority());
                intent.putExtra("sr_no", bean.getStr_assign_task_srno());
                name=bean.getStr_assigner_name();
                context.startActivity(intent);
            }
        });

        holder.btn_send.setTag(position);
        holder.btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                            progressBarStatus = doSomeWork(bean);
                            doSomeWork2(ASSIGN_BY, (bean.getStr_assigntask_sub()), bean);
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
                            if (progDialog != null) {
                                progDialog.dismiss();
                                if (str_result.equals("Success")) {
                                    MyAssignedTaskListFragment.listdata();
                                    MyAssignedTaskListFragment.refreshContent();
                                } else {
                                    Toast.makeText(context, "Task Send Successfully", Toast.LENGTH_LONG).show();
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
        });


        holder.btn_del.setTag(position);
        holder.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setCancelable(false);
                alertDialog.setMessage("Delete Task from Assigned Task");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                                    progressBarStatus = doSomeWork1(bean);
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
                                    if (progDialog != null) {
                                        progDialog.dismiss();
                                        if (str_result.equals("Success")) {
                                            list_array_task.remove(position);
                                            notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(context, "Task Not Deleted", Toast.LENGTH_LONG).show();
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
                });

                alertDialog.setNegativeButton("Cancel", null);

                AlertDialog dialog = alertDialog.create();
                dialog.show();

            }
        });

        holder.txt_assignto.setText(bean.getStr_assigner_name());
        //System.out.println("assign_name455="+bean.getStr_assigner_name());
        holder.txt_sub.setText(bean.getStr_assigntask_sub());
        holder.txt_activity_date.setText(bean.getStr_activitydate());
        holder.txt_status.setText(bean.getStr_status());
        holder.txt_target_date.setText(bean.getStr_target_date());

        switch (bean.getStr_task_percentage()) {
            case "0":
            case "null":
                holder.seekBar.setImageResource(R.drawable.zeronew);
                break;
            case "10":
                holder.seekBar.setImageResource(R.drawable.tennew);
                break;
            case "20":
                holder.seekBar.setImageResource(R.drawable.twentynew);
                break;
            case "30":
                holder.seekBar.setImageResource(R.drawable.thirtynew);
                break;
            case "40":
                holder.seekBar.setImageResource(R.drawable.fourtynew);
                break;
            case "50":
                holder.seekBar.setImageResource(R.drawable.fiftynew);
                break;
            case "75":
                holder.seekBar.setImageResource(R.drawable.eightynew);
                break;
            case "90":
                holder.seekBar.setImageResource(R.drawable.nintynew);
                break;
            case "100":
                holder.seekBar.setImageResource(R.drawable.hundrednew);
                break;
        }
        if (bean.getStr_assign_task_seen().equals("1")) {
            holder.img_seen.setImageResource(R.drawable.tick);
        }
        if (bean.getStr_status().equals("Complete")) {
            holder.seekBar.setImageResource(R.drawable.task_done);
        }
        return row;
    }


    private int doSomeWork(BeanParameter Parameter) {
        if (Utilities.hasConnection(context)) {
            try {
                String str1 = "";

               // str1 = "update task_assign set chk_sent='1' where sr_no=" + Parameter.getStr_assign_task_srno();
                String TaskSrNO = Parameter.getStr_assign_task_srno();
                    System.out.println("taskSrNo="+TaskSrNO);
                ServiceHandler sh = new ServiceHandler();
                String result = "";
                System.out.println(str1);
                result = execute_query( TaskSrNO,context);
                Log.d("Response: ", "> " + result);
                if (result != null && !result.equals("Error")) {
                    System.out.println(result);
                    JSONObject jsonObject = new JSONObject(result);
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

    private static String execute_query( String srNo,Context context) {
        String str1 = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Config.MAIN_URL + Config.URL_UPDATE_SEND_DATA);
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 25000);
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), 25000);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            JSONObject jsonObject = new JSONObject();
           // jsonObject.put("str_query", str_1);
            //jsonObject.put("sr_no", srNo);
            System.out.println("sr:"+srNo);
            nameValuePairs.add(new BasicNameValuePair("sr_no", srNo));
           // jsonObject.put("mobile", "null");

   //  nameValuePairs.add(new BasicNameValuePair("" ,jsonObject.toString()));
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

    private int doSomeWork1(BeanParameter Parameter) {
        if (Utilities.hasConnection(context)) {
            try {
                String str1 = "";

               // str1 = "delete from task_assign where sr_no='" + Parameter.getStr_assign_task_srno() + "'and chk_sent IS NULL";
                ServiceHandler sh = new ServiceHandler();
                String jsonStr = "";
                System.out.println(str1);
                jsonStr = execute_query1(Parameter,context);
                Log.d("Response: ", "> " + jsonStr);
                if (jsonStr != null && !jsonStr.equals("Error")) {
                    System.out.println(jsonStr);
                    JSONObject jsonObject = new JSONObject(jsonStr);
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
    private static String execute_query1(BeanParameter Parameter,Context context) {


        String str1 = "";
        String name="";

       // BeanParameter Parameter=new BeanParameter();
        name=Parameter.getStr_assigner_name();
        System.out.println("name"+name);
        String srno=Parameter.getStr_assign_task_srno();
        HttpClient httpclient = new DefaultHttpClient();
        String url=Config.MAIN_URL + Config.URL_DELETE_TASK+"?";
        HttpPost httppost = new HttpPost(url);
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 25000);
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), 25000);


        try {


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

          //  JSONObject jsonObject = new JSONObject();
//            jsonObject.put("str_query", str_1);

            System.out.println( "sr_no"+srno);
           // jsonObject.put("sr_no", beanParameter.getStr_assign_task_srno());

           nameValuePairs.add(new BasicNameValuePair("sr_no",srno));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);

            System.out.println("val"+httppost.toString());
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

    private void doSomeWork2(String assign, String subject,BeanParameter parameter) {
        if (Utilities.hasConnection(context)) {
            try {

                ServiceHandler sh = new ServiceHandler();
                String jsonStr = "";
                jsonStr = execute_query2(assign, subject, parameter,context);
                Log.d("Response: ", "> " + jsonStr);
                if (jsonStr != null && !jsonStr.equals("Error")) {
                    System.out.println(jsonStr);
                    JSONObject jsonObject = new JSONObject(jsonStr);
                   // System.out.println("jsonObject:"+jsonObject.getString("success"));
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
    }

    private static String execute_query2(String str_assign, String subject,BeanParameter parameter,Context context) {
        String str1 = "";
        String TaskSrNO = parameter.getStr_assign_task_srno();
        token_id= parameter.getStr_token_employee();
        HttpClient httpclient = new DefaultHttpClient();
        String url=Config.MAIN_URL + Config.URL_GCM_ANDROID_SINGLE+"?";
        HttpPost httppost = new HttpPost(url);
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 25000);
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), 25000);
        //sr_no=AppLockerPreference.getInstance(context).getM_empid();

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
          nameValuePairs.add(new BasicNameValuePair("sr_no",TaskSrNO));
            System.out.println("token="+TaskSrNO);
            nameValuePairs.add(new BasicNameValuePair("str_assign", str_assign));
            nameValuePairs.add(new BasicNameValuePair("subject", subject));
               // nameValuePairs.add(new BasicNameValuePair("token", token_id));
            System.out.println("token_id="+token_id);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("url="+httppost.toString());
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
    private class Holder {
        private TextView txt_assignto, txt_sub, txt_activity_date, txt_status, txt_target_date, txt_send, txt_del, txt_edit;
        private ImageButton btn_send, btn_edit, btn_del;
        private ImageView img_seen, seekBar;
    }
}
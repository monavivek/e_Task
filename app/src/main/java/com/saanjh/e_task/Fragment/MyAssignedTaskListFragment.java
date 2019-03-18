package com.saanjh.e_task.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.saanjh.e_task.Adapter.MyAssignTaskAdapter;
import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.Model.BeanParameter;
import com.saanjh.e_task.R;
import com.saanjh.e_task.Utils.ExceptionHandling;
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
import java.util.ArrayList;
import java.util.List;


public class MyAssignedTaskListFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {
    private static Context context;
    ImageButton txt_edit;
    private static String emp_id = "", LOG_TAG, str_result = "error";
    static SwipeRefreshLayout swp_layout;
    private static String url2 = Config.MAIN_URL + Config.URL_ASSIGN_TASK_LIST+"?";
    static List<BeanParameter> list_array_task;
    private static MyAssignTaskAdapter adapter;
    static ListView lv;
    private static int progressBarStatus = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_myassigned_task, container, false);
        lv = (ListView) rootView.findViewById(R.id.assign_task_listView);
        swp_layout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        txt_edit = (ImageButton) rootView.findViewById(R.id.task_edit);
        context = getActivity();
        LOG_TAG = "Test";

        DisplayImageOptions defaultOption = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(defaultOption).build();
        ImageLoader.getInstance().init(config);

        emp_id = AppLockerPreference.getInstance(context).getM_empid();


        list_array_task = new ArrayList<BeanParameter>();

        swp_layout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swp_layout.post(new Runnable() {
                            @Override
                            public void run() {
                                swp_layout.setRefreshing(true);
                                if (!list_array_task.isEmpty()) {
                                    list_array_task.clear();
                                }
                                listdata();
                            }
                        }
        );

        return rootView;
    }

    @Override
    public void onRefresh() {
        refreshContent();
    }

    public static void listdata() {
        try {
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {

                    progressBarStatus = 0;
                }

                @Override
                protected Void doInBackground(Void... arg0) {
                    try {
                        //str_query = "select sr_no,assign_to,subject,target_date,chk_sent,coalesce(null,task_status,'-')as task_status,task_seen,c_name,msg,attachment,image_path,priority,(select percentage from task_reply where task_assign.sr_no=task_id::int order by sr_no desc limit 1) percentage,coalesce(null,(select activity_date from task_reply where task_assign.sr_no=task_id::int order by sr_no desc limit 1),'-')activity_date from task_assign where employee_id = '" + emp_id + "' order by task_assign.sr_no desc";
                        progressBarStatus = doSomeWork1();
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

                        if (str_result.equals("Success")) {
                            // stopping swipe refresh
                            swp_layout.setRefreshing(false);
                            adapter = new MyAssignTaskAdapter(context, R.layout.assign_task_list, list_array_task);

                            lv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
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

    public static void refreshContent() {
        if (!list_array_task.isEmpty()) {
            list_array_task.clear();
            adapter.notifyDataSetChanged();
        }
        listdata();
    }

    private static int doSomeWork1() {
        if (Utilities.hasConnection(context)) {
            try {
                //  ServiceHandler sh = new ServiceHandler();
                String result = "";
                result = execute_query();
                Log.d("Response: ", "> " + result);
                if (result != null && !result.equals("error")) {
                    // System.out.println("str="+result);
                    JSONObject jsonObject = new JSONObject(result);
                    //   System.out.println("url2="+url2);
                    if (!jsonObject.getString("status").equals("Error")) {
                        str_result = "Success";
                        System.out.println("hlw=" +jsonObject.toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            BeanParameter beanParameter = new BeanParameter();
                            beanParameter.setStr_assigntask_sub(jsonObject1.getString("subject"));
                            beanParameter.setStr_activitydate(jsonObject1.getString("activity_date"));
                            System.out.println("assign_name="+jsonObject1.getString("assign_to"));
                            beanParameter.setStr_status(jsonObject1.getString("task_status"));
                            beanParameter.setStr_assigner_name(jsonObject1.getString("assign_to"));
                            beanParameter.setStr_target_date(jsonObject1.getString("target_date"));
                            beanParameter.setStr_task_percentage(jsonObject1.getString("percentage").trim());
                            beanParameter.setStr_assign_task_seen(jsonObject1.getString("task_seen"));
                            beanParameter.setStr_assign_task_msg(jsonObject1.getString("msg"));
                            beanParameter.setStr_assign_task_c_name(jsonObject1.getString("c_name"));
                            beanParameter.setStr_assign_task_priority(jsonObject1.getString("priority"));
                            beanParameter.setStr_assign_task_image_path(jsonObject1.getString("image_path"));
                            beanParameter.setStr_assign_task_attach(jsonObject1.getString("attachment"));
                            beanParameter.setStr_assign_task_srno(jsonObject1.getString("sr_no"));
                            beanParameter.setStr_assign_task_chksent(jsonObject1.getString("chk_sent"));

                            list_array_task.add(beanParameter);
                        }
                    } else {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Nothing to Display", Toast.LENGTH_LONG).show();
                            }
                        });
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
    public static String execute_query() {
        String str1 = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url2);
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 25000);
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), 25000);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

            nameValuePairs.add(new BasicNameValuePair("sr_no", emp_id));

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
}




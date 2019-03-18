package com.saanjh.e_task.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.Model.BeanParameter;
import com.saanjh.e_task.Adapter.CustomAdapter;
import com.saanjh.e_task.Rest.HttpGetAsyncTaskFragment;
import com.saanjh.e_task.R;
import com.saanjh.e_task.Utils.ExceptionHandling;
import com.saanjh.e_task.Utils.ServiceHandler;
import com.saanjh.e_task.Activity.TaskStatusUpdate;
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

public class MyTaskListFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private Context context;
    private String user_name, EMP_ID;
    SwipeRefreshLayout swp_layout;
    private static String url = Config.MAIN_URL + Config.URL_TASK_LIST+"?";
    List<BeanParameter> list_array_task;
    private CustomAdapter adapter;
    String task_id, LOG_TAG = "test";
    private static String str_result = "Error";
    ProgressDialog progDialog;
    int progressBarStatus = 0;
    ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_mytasklist, container, false);
        lv = (ListView) rootView.findViewById(R.id.task_listView);
        swp_layout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        context = getActivity();
        getActivity().setTitle("My Tasks");

        DisplayImageOptions defaultOption = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(defaultOption).build();
        ImageLoader.getInstance().init(config);

        user_name =AppLockerPreference.getInstance(context).getM_username();
        EMP_ID = AppLockerPreference.getInstance(context).getM_empid();
        //System.out.println(user_name);

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

                                task_list();
                            }
                        }
        );

        return rootView;
    }

    @Override
    public void onRefresh() {
        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
        refreshContent();
    }

    public void task_list() {
      final   HttpGetAsyncTaskFragment http = new HttpGetAsyncTaskFragment(getActivity(), new AssignTaskFragment.FragmentCallback() {

            public void onTaskDone(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                   // System.out.println(url+result);
                   // System.out.println("hello1" +jsonObject.toString());
                    // stopping swipe refresh
                    swp_layout.setRefreshing(false);
                    if (jsonObject.getString("status").equals("Error")) {
                        Toast.makeText(getActivity(), "Nothing to Display", Toast.LENGTH_LONG).show();
                      //  System.out.println("hello" +jsonObject.toString());

                        Log.d("j=",jsonObject.toString());

                    } else {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                      //  System.out.println("hlw" +jsonObject.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            BeanParameter beanParameter = new BeanParameter();
                            beanParameter.setStr_task_id(jsonObject1.getString("sr_no"));
                           // System.out.println("sr_no="+jsonObject1.getString("sr_no"));
                            beanParameter.setStr_assignby(jsonObject1.getString("assigned_by"));
                            beanParameter.setStr_employee_id(jsonObject1.getString("employee_id"));
                            beanParameter.setStr_assignto(jsonObject1.getString("assign_to"));
                            beanParameter.setStr_sub(jsonObject1.getString("subject"));
                            beanParameter.setStr_tdate(jsonObject1.getString("target_date"));
                            beanParameter.setStr_cname(jsonObject1.getString("c_name"));
                            beanParameter.setStr_photo(jsonObject1.getString("photo"));
                            beanParameter.setStr_mssg(jsonObject1.getString("msg"));
                            beanParameter.setStr_prior(jsonObject1.getString("prior"));
                            beanParameter.setStr_att(jsonObject1.getString("attachment"));
                            beanParameter.setStr_task_status(jsonObject1.getString("task_status"));
                            beanParameter.setStr_task_seen(jsonObject1.getString("task_seen"));
                            list_array_task.add(beanParameter);
                         //  System.out.println("array_list"+list_array_task.toString());
                        }

                        adapter = new CustomAdapter(getActivity(),android.R.layout.simple_list_item_1, list_array_task);
                        lv.setAdapter(adapter);
                       //System.out.println("lv="+lv.toString()+"ad="+adapter.toString());
                        adapter.notifyDataSetChanged();
                        if (user_name.equals("admin")) {

                        } else {
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                    //System.out.println("position"+position);
                                    final BeanParameter bean = adapter.getItem(position);
                                    task_id = bean.getStr_task_id();
                                    String sr_no=bean.getStr_assign_task_srno();
                                    System.out.println("Sr_no"+sr_no);
                                    Intent intent = new Intent(getActivity(), TaskStatusUpdate.class);
                                    intent.putExtra("taskid", task_id);
                                    intent.putExtra("tasksub", bean.getStr_sub());
                                    startActivity(intent);

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
                                                        Runnable run = new Runnable() {
                                                            public void run() {
                                                                adapter.notifyDataSetChanged();
                                                            }
                                                        };

                                                       // Toast.makeText(getActivity(), "Status Updated", Toast.LENGTH_LONG).show();

                                                    } else {
                                                       // Toast.makeText(getActivity(), "Status Not Updated", Toast.LENGTH_LONG).show();
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
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                    // stopping swipe refresh
                    swp_layout.setRefreshing(false);
                }
            }
        }, 3);

        try {

//            if (user_name.equals("admin")) {
//
//                String str_query = "select * from task_assign";
//                http.execute(url + URLEncoder.encode(str_query, "UTF-8"));
//            } else {
//
//                String str_query = "select * from task_assign where assign_to_id = '" + EMP_ID + "' and coalesce(null,task_status,'')<>'Complete' and chk_sent='1'\n";
                http.execute(url+"user_name="+user_name+"&sr_no="+EMP_ID);
               // System.out.println("list"+url);
           // http.execute(url2 +"user_name="+SELECT_EMPLOYEE.getText().toString());
            //}
        } catch (Exception e) {
            e.printStackTrace();
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
        }


    }

    private void refreshContent() {
        if (!list_array_task.isEmpty()) {
            list_array_task.clear();
            adapter.notifyDataSetChanged();
        }
        task_list();
    }

    private int doSomeWork(BeanParameter parameter) {
        if (Utilities.hasConnection(getActivity())) {
            try {
                String str1 = "";

               // str1 = "update task_assign set task_seen='1' where sr_no= '" +task_id+"'";

                ServiceHandler sh = new ServiceHandler();
                String jsonStr = "";
             //   System.out.println(str1);
                jsonStr = execute_query(parameter,context);
                Log.d("Response: ", ">" + jsonStr);
                if (jsonStr != null && !jsonStr.equals("Error")) {
                    //System.out.println(jsonStr);
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    if (jsonObject.getString("status").equals("Success")) {
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

    public static String execute_query(BeanParameter parameter,Context context) {
        String str1 = "";
        //String sr_no=parameter.getStr_assign_task_srno();

        String srno=parameter.getStr_task_id();
        System.out.println("Sr_no"+srno);
        HttpClient httpclient = new DefaultHttpClient();
        String url=Config.MAIN_URL + Config.URL_UPDATETASK_LIST+"?";
        HttpPost httppost = new HttpPost(url);
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 25000);
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), 25000);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
           // System.out.println(""+task_id);
          //  JSONObject jsonObject = new JSONObject();
          //  jsonObject.put("str_query", str_1);
            //jsonObject.put("sr_no", sr_no);

            nameValuePairs.add(new BasicNameValuePair("sr_no",srno));
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


package com.saanjh.e_task.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.Model.BeanParameter;
import com.saanjh.e_task.Rest.HttpGetAsyncTaskFragment;
import com.saanjh.e_task.Adapter.NotificationAdapter;
import com.saanjh.e_task.R;
import com.saanjh.e_task.Utils.ExceptionHandling;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = NotificationFragment.class.getSimpleName();
    public Context context;
    public String user_name, EMP_ID;
    public SwipeRefreshLayout swp_layout;
    private static String url = Config.MAIN_URL + Config.URL_NOTIFICATION_LIST + "?";
    private List<BeanParameter> list_array_notification;
    private NotificationAdapter adapter;
    String LOG_TAG = "test";
    ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_notificationlist, container, false);
        lv = (ListView) rootView.findViewById(R.id.notify_listView);

        swp_layout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        context = getActivity();

        user_name = AppLockerPreference.getInstance(context).getM_username();
        EMP_ID = AppLockerPreference.getInstance(context).getM_empid();

        list_array_notification = new ArrayList<BeanParameter>();

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
        HttpGetAsyncTaskFragment http = new HttpGetAsyncTaskFragment(getActivity(), new AssignTaskFragment.FragmentCallback() {

            public void onTaskDone(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    // stopping swipe refresh
                    swp_layout.setRefreshing(false);
                    Log.e(TAG, jsonObject.toString());
                    if (jsonObject.getString("status").equals("Error")) {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            BeanParameter beanParameter = new BeanParameter();
                            beanParameter.setStr_notify_msg(jsonObject1.getString("msg"));
                            beanParameter.setStr_notify_date(jsonObject1.getString("target_date"));
                            beanParameter.setStr_notify_by(jsonObject1.getString("assigned_by"));
                            beanParameter.setStr_notify_subject(jsonObject1.getString("subject"));
                            beanParameter.setStr_notify_image(jsonObject1.getString("image_path"));
                            beanParameter.setStr_notify_attachment(jsonObject1.getString("attachment"));
                            beanParameter.setStr_notify_priority(jsonObject1.getString("priority"));

                            list_array_notification.add(beanParameter);
                        }

                        adapter = new NotificationAdapter(getActivity(), R.layout.list_notify, list_array_notification);
                        lv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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
         //   String str_query = "select  coalesce(assigned_by,null,'') as  assigned_by, coalesce(target_date,null,'') as  target_date, coalesce(subject,null,'') as  subject, coalesce(msg,null,'') as  msg, coalesce(attachment,null,'') as  attachment, coalesce(priority,null,'') as  priority, coalesce(image_path,null,'') as  image_path from  task_assign where assign_to_id = '" + EMP_ID + "' order by target_date desc limit 10";
            http.execute(url +"assign_to_id="+EMP_ID);

        } catch (Exception e) {
            e.printStackTrace();
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
        }

    }
    private void refreshContent() {
        if (!list_array_notification.isEmpty()) {
            list_array_notification.clear();
            adapter.notifyDataSetChanged();
        }
        task_list();
    }




    public void displayExceptionMessage(String msg)
    {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}

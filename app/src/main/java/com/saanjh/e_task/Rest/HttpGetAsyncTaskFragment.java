package com.saanjh.e_task.Rest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.saanjh.e_task.Fragment.AssignTaskFragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@SuppressWarnings("deprecation")
public class HttpGetAsyncTaskFragment extends AsyncTask<String, Void, String> {
    public ProgressDialog dialog;
    private Context con;
    private int tpe;
    private AssignTaskFragment.FragmentCallback mFragmentCallback;


    public HttpGetAsyncTaskFragment(Context con, AssignTaskFragment.FragmentCallback fragmentCallback, int tp) {
        mFragmentCallback = fragmentCallback;
        this.con = con;
        this.tpe = tp;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.tpe == 1) {

            Activity activity = (Activity) con;
            activity.setProgressBarIndeterminateVisibility(true);
        } else if (this.tpe == 3) {
            dialog = new ProgressDialog(con);
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Loading...");
        }
    }

    @Override
    protected String doInBackground(String... params) {

        HttpClient httpClient = new DefaultHttpClient();

        System.out.println(params[0]);

        HttpPost httpGet = new HttpPost(params[0]);
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 25000);
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 25000);

        String res = "";
        try {

            HttpResponse httpResponse = httpClient.execute(httpGet);

            String responseBody = EntityUtils.toString(httpResponse.getEntity());

            res = responseBody.trim();
        } catch (ClientProtocolException cpe) {
            System.out.println("Exception generates coz of httpResponse :" + cpe);
            cpe.printStackTrace();
        } catch (IOException ioe) {
            System.out.println("Second exception generates coz of httpResponse :" + ioe);
            ioe.printStackTrace();
        }

        return res;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (this.tpe == 1) {
            Activity activity = (Activity) con;
            activity.setProgressBarIndeterminateVisibility(false);
        } else if (this.tpe == 3) {
            if (dialog.isShowing()) dialog.dismiss();
        }
        mFragmentCallback.onTaskDone(result);

    }
}

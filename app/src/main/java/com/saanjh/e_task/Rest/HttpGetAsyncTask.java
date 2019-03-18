package com.saanjh.e_task.Rest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.saanjh.e_task.Utils.ExceptionHandling;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

//@SuppressWarnings("deprecation")
public class HttpGetAsyncTask extends AsyncTask<String, Void, String> {
    public ProgressDialog dialog;
    public AsyncResponse delegate = null;
    private Context con;
    private int tpe;

    public HttpGetAsyncTask(Context c, int tp) {

        this.con = c;
        this.tpe = tp;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (this.tpe == 1) {

            Activity activity = (Activity) this.con;
            activity.setProgressBarIndeterminateVisibility(true);
        } else if (this.tpe == 3) {
            dialog = new ProgressDialog(this.con);
            dialog.dismiss();
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
             dialog.show();
            System.out.println("Exception generates coz of httpResponse :" + cpe);
            cpe.printStackTrace();
            String ex=cpe.toString();
            String line_number= String.valueOf(cpe.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,con);
        } catch (IOException ioe) {

            String ex=ioe.toString();
            String line_number= String.valueOf(ioe.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,con);
            System.out.println("Second exception generates coz of httpResponse :" + ioe);
            ioe.printStackTrace();
        }

        return res;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (this.tpe == 1) {
            Activity activity = (Activity) this.con;
            activity.setProgressBarIndeterminateVisibility(false);


        } else if (this.tpe == 3) {
            if (dialog.isShowing()) dialog.dismiss();
        }
        delegate.processFinish(result);

    }

    public interface AsyncResponse {

        void processFinish(String output);
    }


}

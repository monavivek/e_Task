package com.saanjh.e_task.Utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.saanjh.e_task.App.Config;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class ExceptionHandling extends Application {
    private static Context context;
    String exception,line_number;
    RequestQueue requestQueue;

    public void handler(String ex, final String line_number, final Context context)
    {
        this.exception=ex;
       this. context=context;
       this.line_number=line_number;
       System.out.println("re:"+exception);
        String url=Config.MAIN_URL+Config.URL_EXCEPTION_HANDLER;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            System.out.println("lkjjk"+obj);
                        }catch (Exception e)
                        {
                            String ex=e.toString();
                            String line_number= String.valueOf(e.getStackTrace()[0]);
                            ExceptionHandling exceptionHandling=new ExceptionHandling();
                            exceptionHandling.handler(ex,line_number,context);

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("exception", exception);
                params.put("line_number", line_number);


                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(ExceptionHandling.context);
        requestQueue.add(stringRequest);
    }
}

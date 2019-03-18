package com.saanjh.e_task.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.Rest.HttpGetAsyncTask;
import com.saanjh.e_task.R;
import com.saanjh.e_task.Utils.ExceptionHandling;
import com.saanjh.e_task.Utils.ServiceHandler;
import com.saanjh.e_task.Utils.UploadData;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class TaskStatusUpdate extends AppCompatActivity implements HttpGetAsyncTask.AsyncResponse {

    private static final int CAMERA_PIC_REQUEST = 1;
    private static final int RESULT_LOAD_IMAGE = 3;
    public static String imgFilename = "";
    EditText TASK_ID, ACTIVITY_DATE, DESC, TASK_SUB, UPLOAD;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static String url = Config.MAIN_URL + Config.URL_INSERT_QUERY_STATUS + "?", str_result = "Error";
    Button SAVE;
    private static String s;
    private String ASSIGN_BY, EMP_ID, Subject, TaskId, Date;
    private static String iemi = "", image_path = "";
    public String PER;
    String task_id = "", task_sub = "";
    private RadioGroup status_info;
    public RadioButton STATUS;
    Spinner spin;
    private static Context context;
    private ImageView activity_img;
    ProgressDialog progDialog;
    int progressBarStatus = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_status_update);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Task Update");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
        ACTIVITY_DATE = (EditText) findViewById(R.id.completed_date);
        TASK_ID = (EditText) findViewById(R.id.task_id);
        activity_img = (ImageView) findViewById(R.id.activity_image);
        TASK_SUB = (EditText) findViewById(R.id.task_sub);
        DESC = (EditText) findViewById(R.id.description);
        status_info = (RadioGroup) findViewById(R.id.task_status);
        UPLOAD = (EditText) findViewById(R.id.upload_img);
        SAVE = (Button) findViewById(R.id.save_status);
        spin = (Spinner) findViewById(R.id.pow);
        context = this;
        String[] percentage = {"0%", "10%", "20%", "30%", "40%", "50%", "75%", "90%", "100%"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, percentage);
        spin.setAdapter(adapter);
        Intent intent = getIntent();
        task_id = intent.getStringExtra("taskid");
        task_sub = intent.getStringExtra("tasksub");
        System.out.println("task_id"+task_id);
        TASK_SUB.setText(task_sub);
        TASK_SUB.setKeyListener(null);
        TASK_ID.setText(task_id);
        System.out.println( " TASK_ID"+TASK_ID);
        TASK_ID.setKeyListener(null);
        TelephonyManager tm = (TelephonyManager) TaskStatusUpdate.this.getSystemService(Context.TELEPHONY_SERVICE);
       // iemi = tm.getDeviceId();
        Calendar cal = Calendar.getInstance();
        s = dateFormat.format(cal.getTime());
        ACTIVITY_DATE.setText(s);
        ACTIVITY_DATE.setKeyListener(null);
        UPLOAD.setKeyListener(null);
        ASSIGN_BY = AppLockerPreference.getInstance(context).getM_username();
        EMP_ID = AppLockerPreference.getInstance(context).getM_empid();
        System.out.println("emp_id"+EMP_ID);
        System.out.println("Emp_name"+ASSIGN_BY);
        loadimage();
        UPLOAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(TaskStatusUpdate.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temptask.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            startActivityForResult(intent, CAMERA_PIC_REQUEST);
                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            File f = new File(android.os.Environment.getExternalStorageDirectory(),
                                    "temp_gallery.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            startActivityForResult(intent, RESULT_LOAD_IMAGE);

                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        SAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = status_info.getCheckedRadioButtonId();
                STATUS = (RadioButton) findViewById(selectedId);
                String Per = spin.getSelectedItem().toString().trim();
                PER=Per.replace('%', ' ');
                System.out.println("percentage="+PER);
                if (DESC.getText().toString().length() == 0) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Please add Description about task");
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                } else if (status_info.getCheckedRadioButtonId() == -1) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Please select the Status");
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                } else {
                    // Text get from EditText
                    Subject = TASK_SUB.getText().toString();
                    TaskId = TASK_ID.getText().toString();
                    Date = ACTIVITY_DATE.getText().toString();

                    // Http calling from server
                    HttpGetAsyncTask http = new HttpGetAsyncTask(TaskStatusUpdate.this, 3);
                    http.delegate = TaskStatusUpdate.this;
                    try {
                      //    String str_query = "insert into task_reply ( task_id,task_sub,activity_date,description,status,emp_id,emp_name,activity_image_path,percentage)values('" + TASK_ID.getText().toString() + "','" + TASK_SUB.getText().toString() + "','" + ACTIVITY_DATE.getText().toString() + "','" + DESC.getText().toString() + "','" + STATUS.getText().toString() + "','" + EMP_ID + "','" + ASSIGN_BY + "','" + image_path + "','" + PER + "')";
                        JSONObject jsonObject = new JSONObject();
                       // jsonObject.put("str_query", str_query);
                       // jsonObject.put("mobile", "null");
                        jsonObject.put("sr_no", TASK_ID.getText().toString());
                        jsonObject.put("subject", TASK_SUB.getText().toString());
                        jsonObject.put("activity_date",  ACTIVITY_DATE.getText().toString());
                        jsonObject.put("description",  DESC.getText().toString());
                        jsonObject.put("status",  STATUS.getText().toString());
                        jsonObject.put("emp_id", EMP_ID);
                        jsonObject.put("emp_name",  ASSIGN_BY);
                        jsonObject.put("percentage", PER);
                        jsonObject.put("activity_image_path", image_path);
                        System.out.println("srno"+task_id);
                        System.out.println("sub="+task_sub);
                        System.out.println("des="+DESC.toString());
                        System.out.println(url + jsonObject.toString());
                       // http.execute(url + URLEncoder.encode(jsonObject.toString(), "UTF-8").trim());
                      //  http.execute(url + URLDecoder.decode(jsonObject.toString(), "UTF-8").trim());
						http.execute(url + "sr_no="+TASK_ID.getText().toString()+"&subject="+TASK_SUB.getText().toString()+"&activity_date="+ACTIVITY_DATE.getText().toString()+"&description="+DESC.getText().toString()+"&status="+STATUS.getText().toString()+"&percentage="+PER.trim()+"&emp_id="+EMP_ID+"&emp_name="+ASSIGN_BY+"&activity_image_path="+image_path);
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
    }

    @Override
    public void processFinish(String res) {
        try {
            JSONObject jsonObject = new JSONObject(res);
           // System.out.println("result"+res);
            if (jsonObject.getString("status").equals("error")) {
                Toast.makeText(getApplicationContext(), "Value not Inserted", Toast.LENGTH_LONG).show();
            } else {
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
                            progressBarStatus = doSomeWork();
                            doSomeWork1(ASSIGN_BY, Subject, TaskId, Date);
                            UploadData.uploadfiles(context,image_path);
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
                                    ACTIVITY_DATE.setText("");
                                    TASK_ID.setText("");
                                    TASK_SUB.setText("");
                                    DESC.setText("");
                                    UPLOAD.setText("");
                                    STATUS.setChecked(false);
                                    activity_img.setImageResource(android.R.color.transparent);
                                    spin.setSelection(0);
                                    TASK_ID.requestFocus();



                                } else {
                                    Toast.makeText(getApplicationContext(), "Status Not Updated", Toast.LENGTH_LONG).show();
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

                ACTIVITY_DATE.setText("");
                TASK_ID.setText("");
                TASK_SUB.setText("");
                DESC.setText("");
                UPLOAD.setText("");
                TASK_ID.requestFocus();
                }
        } catch (Exception e) {
            e.printStackTrace();
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temptask.jpg")) {
                    f = temp;
                    break;
                }
            }
            try {
                Bitmap bitmap = null;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = 2;
                try {
                    InputStream inputStream = (InputStream) new URL("file://" + f.getAbsolutePath()).getContent();
                    bitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                } catch (IOException e) {
                    e.printStackTrace();
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                }

                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/taskmanagerimages/";
                File newdir = new File(path);
                newdir.mkdirs();
                f.delete();
                OutputStream outFile = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                String date = dateFormat.format(new Date());
                imgFilename = iemi + "_" + date + ".jpg";
                File file = new File(path + File.separator + imgFilename);
                try {
                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outFile);
                    outFile.flush();
                    outFile.close();
                    image_path = imgFilename;
                    UPLOAD.setText(image_path);
                    activity_img.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                } catch (IOException e) {
                    e.printStackTrace();
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                } catch (Exception e) {
                    e.printStackTrace();
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                }
            } catch (Exception e) {
                e.printStackTrace();
                String ex=e.toString();
                String line_number= String.valueOf(e.getStackTrace()[0]);
                ExceptionHandling exceptionHandling=new ExceptionHandling();
                exceptionHandling.handler(ex,line_number,context);
            }
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp_gallery.jpg")) {
                    f = temp;
                    break;
                }
            }
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = TaskStatusUpdate.this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            activity_img.setImageBitmap(thumbnail);
            // String fileNameSegments[] = picturePath.split("/");
            // image_path = fileNameSegments[fileNameSegments.length - 1];

            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/taskmanagerimages/";
            File newdir = new File(path);
            newdir.mkdirs();
            f.delete();
            OutputStream outFile = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
            String date = dateFormat.format(new Date());
            image_path = iemi + "_" + date + ".jpg";
            UPLOAD.setText(image_path);
            File file = new File(path + File.separator + image_path);
            try {
                outFile = new FileOutputStream(file);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 60, outFile);
                outFile.flush();
                outFile.close();
            } catch (Exception e) {
                e.printStackTrace();
                String ex=e.toString();
                String line_number= String.valueOf(e.getStackTrace()[0]);
                ExceptionHandling exceptionHandling=new ExceptionHandling();
                exceptionHandling.handler(ex,line_number,context);
            }
        }
     }
    private void loadimage() {
        if (!imgFilename.equals("")) {

            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/taskmanagerimages/";
            File newdir = new File(path);
            newdir.mkdirs();
            File file = new File(path + File.separator + imgFilename);
            try {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bMap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                activity_img.setImageBitmap(bMap);
            } catch (Exception e) {
                e.printStackTrace();
                String ex=e.toString();
                String line_number= String.valueOf(e.getStackTrace()[0]);
                ExceptionHandling exceptionHandling=new ExceptionHandling();
                exceptionHandling.handler(ex,line_number,context);
            }
        }
     }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int doSomeWork() {
        if (Utilities.hasConnection(getBaseContext())) {
            try {
                String str1 = "";

               //str1 = "update task_assign set task_status='" + STATUS.getText().toString() + "' where sr_no=" + task_id;

                ServiceHandler sh = new ServiceHandler();
                String jsonStr = "";
                //System.out.println(str1);
                jsonStr = execute_query(task_id);
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

    public String execute_query(String task_id) {
        String str1 = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Config.MAIN_URL + Config.URL_UPDATE_STATUS);
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 25000);
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), 25000);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            JSONObject jsonObject = new JSONObject();
////           jsonObject.put("str_query", str_1);
//           jsonObject.put("status",STATUS.getText().toString() );
//           jsonObject.put("sr_no",task_id);
           System.out.println("update_status"+task_id);
            System.out.println("status_id"+STATUS.getText().toString());
            nameValuePairs.add(new BasicNameValuePair("status",STATUS.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("sr_no",task_id));
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
    private void doSomeWork1(String assign, String subject, String str_taskid, String date) {
        if (Utilities.hasConnection(getBaseContext())) {
            try {
                ServiceHandler sh = new ServiceHandler();
                String jsonStr = "";
                jsonStr = execute_query(assign, subject, str_taskid, date);
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
    }
    public static String execute_query(String str_assign, String subject, String str_taskid, String date) {
        String str1 = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Config.MAIN_URL + Config.URL_GCM_TASK_REPLY);
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 25000);
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), 25000);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("str_assign", str_assign));
            nameValuePairs.add(new BasicNameValuePair("subject", subject));
            nameValuePairs.add(new BasicNameValuePair("taskid", str_taskid));
            nameValuePairs.add(new BasicNameValuePair("date", date));
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
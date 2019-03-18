package com.saanjh.e_task.Activity;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.saanjh.e_task.Adapter.EmployeAdaptorEditTask;
import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.Rest.HttpGetAsyncTask;
import com.saanjh.e_task.Model.BeanParameter;
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
import org.json.JSONArray;
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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class EditTaskActivity extends AppCompatActivity implements HttpGetAsyncTask.AsyncResponse {

    EditText SUBJECT, COMP_NAME, MSG, ATTACH, UPLOAD_IMG;
    Button SEND;
    private ImageView image_task;
    private static final int CAMERA_PIC_REQUEST = 1;
    private static final int RESULT_LOAD_IMAGE = 3;
    public static String imgFilename = "", iemi = "", image_path = "";
    public static String EMP_SRNO = "";
    public String EMP_LST = "";
    public String SUB ="";
    public String TARGET_D ="";
    public String C_NAMAE = "";
    public String MSSG = "";
    public String ATTACHMENT = "";
    public String PRIOR = "";
    public static EditText EMP_LIST, TARGET_DATE;
    private RadioGroup rg;
    private RadioButton PRIORITY;
    String emp_name, sub, t_date, c_name, msg, priority, attach, srno, currentDate = "", str_result = "error";
    EmployeAdaptorEditTask adapter;
    ProgressDialog progDialog;
    private static Calendar c = Calendar.getInstance();
    ListView employee_list;
    private String ASSIGN_BY, EMP_ID;
    private static SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
    List<BeanParameter> list_array_task = new ArrayList<BeanParameter>();
    private static String url2 = Config.MAIN_URL + Config.URL_EMPLOYEE_LIST+"?";
    private static Context context;
    private static int progressBarStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task_activity);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Edit Task");
        context = this;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        EMP_LIST = (EditText) findViewById(R.id.list_dilog);
        image_task = (ImageView) findViewById(R.id.task_image);
        SUBJECT = (EditText) findViewById(R.id.subject);
        TARGET_DATE = (EditText) findViewById(R.id.target_date);
        COMP_NAME = (EditText) findViewById(R.id.comp_name);
        MSG = (EditText) findViewById(R.id.msg);
        SEND = (Button) findViewById(R.id.asiign_send);
        ATTACH = (EditText) findViewById(R.id.attachment);
        UPLOAD_IMG = (EditText) findViewById(R.id.upload_img);
        rg = (RadioGroup) findViewById(R.id.r_1);

        Intent intent = getIntent();
        emp_name = intent.getStringExtra("emp_name");
        EMP_LIST.setText(emp_name);
        EMP_LIST.setKeyListener(null);
        sub = intent.getStringExtra("sub");
        SUBJECT.setText(sub);
        t_date = intent.getStringExtra("t_date");
        TARGET_DATE.setText(t_date);
        msg = intent.getStringExtra("msg");
        image_path = intent.getStringExtra("image_path");
        c_name = intent.getStringExtra("c_name");
        attach = intent.getStringExtra("attach");
        priority = intent.getStringExtra("priority");
        srno = intent.getStringExtra("sr_no");
        System.out.println("sr_no"+srno);
        MSG.setText(msg);
        COMP_NAME.setText(c_name);
        UPLOAD_IMG.setText(image_path);
        UPLOAD_IMG.setKeyListener(null);
        ATTACH.setText(attach);
        ASSIGN_BY=AppLockerPreference.getInstance(context).getM_username();
        EMP_ID=AppLockerPreference.getInstance(context).getM_empid();
        currentDate = dformat.format(c.getTime());
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        iemi = tm.getDeviceId();
//        ASSIGN_BY = AppLockerPreference.getInstance(context).getM_username();
//        EMP_ID = AppLockerPreference.getInstance(context).getM_empid();
        loadimage();

        switch (priority) {
            case "High":
                PRIORITY = (RadioButton) findViewById(R.id.high);
                PRIORITY.setChecked(true);
                break;
            case "Avg":
                PRIORITY = (RadioButton) findViewById(R.id.avg);
                PRIORITY.setChecked(true);
                break;
            case "low":
                PRIORITY = (RadioButton) findViewById(R.id.low);
                PRIORITY.setChecked(true);
                break;
        }

        HttpGetAsyncTask http = new HttpGetAsyncTask(EditTaskActivity.this, 3);
        http.delegate = EditTaskActivity.this;
        try {
          //  String str_query = "select * from task_manager_app where coalesce(null,user_name,'')<>'admin' order by user_name desc";
            http.execute(url2);
            System.out.println("empList="+url2);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
        }

        EMP_LIST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = getLayoutInflater();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setCancelable(false);
                View dialogView = inflater.inflate(R.layout.employee_l, null);
                alertDialog.setView(dialogView);

                employee_list = (ListView) dialogView.findViewById(R.id.emp_list);
                adapter = new EmployeAdaptorEditTask(getApplicationContext(), R.layout.employee_list, list_array_task);
                employee_list.setAdapter(adapter);

                alertDialog.setPositiveButton("Select", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
             }
         });
        TARGET_DATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TARGET_DATE.setError(null);
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getSupportFragmentManager(), "DatePicker");

            }

        });
        UPLOAD_IMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = rg.getCheckedRadioButtonId();
                PRIORITY = (RadioButton) findViewById(selectedId);
                PRIOR = PRIORITY.getText().toString();
                EMP_LST = EMP_LIST.getText().toString();
                SUB = SUBJECT.getText().toString();
                TARGET_D = TARGET_DATE.getText().toString();
                C_NAMAE = COMP_NAME.getText().toString();
                MSSG = MSG.getText().toString();
                ATTACHMENT = ATTACH.getText().toString();

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

                                progressBarStatus = doSomeWork1(EMP_LST, SUB, TARGET_D, C_NAMAE, MSSG, ATTACHMENT, PRIOR, image_path, ASSIGN_BY, EMP_ID, srno, EMP_SRNO);
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
                                System.out.println(progDialog);
                                if (progDialog != null) {
                                    progDialog.dismiss();
                                    if (str_result.equals("Success")) {
                                        EMP_LIST.setText("");
                                        SUBJECT.setText("");
                                        TARGET_DATE.setText("");
                                        COMP_NAME.setText("");
                                        MSG.setText("");
                                        ATTACH.setText("");
                                        PRIORITY.setChecked(false);
                                        UPLOAD_IMG.setText("");
                                        EMP_LIST.requestFocus();
                                        image_task.setImageResource(android.R.color.transparent);
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


        });
     }

    @Override
    public void processFinish(String result) {
        try {

            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getString("status").equals("Error")) {
                Toast.makeText(getApplicationContext(), "Nothing to Display", Toast.LENGTH_LONG).show();

            } else {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    BeanParameter beanParameter = new BeanParameter();
                    beanParameter.setStr_ename(jsonObject1.getString("employe_name"));
                    beanParameter.setStr_serial(jsonObject1.getString("sr_no"));
                    beanParameter.setStr_assign_to_id(jsonObject1.getString("srno"));
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
    private int doSomeWork1(String str_assign, String str_subject, String str_date, String str_cname, String str_msg, String str_attach, String str_priority, String str_imagepath, String str_assignby, String str_empid, String str_sr_no, String str_emp_srno) {
        if (Utilities.hasConnection(context)) {
            try {
                ServiceHandler sh = new ServiceHandler();
                String jsonStr = "";
                jsonStr = execute_query(str_assign, str_subject, str_date, str_cname, str_msg, str_attach, str_priority, str_imagepath, str_assignby, str_empid, str_sr_no, str_emp_srno);
                Log.d("Response: ", "> " + jsonStr);
                if (jsonStr != null && !jsonStr.equals("error")) {
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
    public static String execute_query(String str_assign, String str_subject, String str_date, String str_cname, String str_msg, String str_attach, String str_priority, String str_imagepath, String str_assignby, String str_empid, String str_sr_no, String str_emp_srno) {
        String str1 = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Config.MAIN_URL + Config.URL_UPDATE_ASSIGN_TASK);
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 25000);
        HttpConnectionParams.setSoTimeout(httpclient.getParams(), 25000);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("assign", str_assign));
            nameValuePairs.add(new BasicNameValuePair("subject", str_subject));
            nameValuePairs.add(new BasicNameValuePair("date", str_date));
            nameValuePairs.add(new BasicNameValuePair("name", str_cname));
            nameValuePairs.add(new BasicNameValuePair("msg", str_msg));
            nameValuePairs.add(new BasicNameValuePair("attach", str_attach));
            nameValuePairs.add(new BasicNameValuePair("priority", str_priority));
            nameValuePairs.add(new BasicNameValuePair("imagepath", str_imagepath));
            nameValuePairs.add(new BasicNameValuePair("assignby", str_assignby));
            nameValuePairs.add(new BasicNameValuePair("empid", str_empid));
            nameValuePairs.add(new BasicNameValuePair("taskid", str_sr_no));
            nameValuePairs.add(new BasicNameValuePair("emp_srno", str_emp_srno));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            String responseBody = EntityUtils.toString(response.getEntity());
            str1 = responseBody.trim();
            Utilities.LogDebug(str1);
            System.out.println("res="+str1);
            System.out.println("urlupdate="+httppost);
        } catch (Exception e) {
            Utilities.LogDebug("error " + e.getMessage());
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);

            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
        }
        return str1;
    }
    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }
        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
        }
        public void populateSetDate(int year, int month, int day) {
            if (year < c.get(Calendar.YEAR)) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                alertDialog.setCancelable(false);
                alertDialog.setMessage("Year should not be smaller then current date!!");
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TARGET_DATE.setText("");
                        TARGET_DATE.requestFocus();
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            } else if (month < c.get(Calendar.MONTH) + 1) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                alertDialog.setCancelable(false);

                alertDialog.setMessage("Month should not be smaller then current date!!");

                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TARGET_DATE.setText("");
                        TARGET_DATE.requestFocus();
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            } else if (day < c.get(Calendar.DAY_OF_MONTH)) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                alertDialog.setCancelable(false);

                alertDialog.setMessage("Date should not be smaller then current date!!");

                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TARGET_DATE.setText("");
                        TARGET_DATE.requestFocus();
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            } else {
                GregorianCalendar d = new GregorianCalendar(year, month - 1, day);
                String date = dformat.format(d.getTime());
                TARGET_DATE.setText(date);

            }
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
                    UPLOAD_IMG.setText(image_path);
                    image_task.setImageBitmap(bitmap);
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
            Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            image_task.setImageBitmap(thumbnail);
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
            UPLOAD_IMG.setText(image_path);
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
                image_task.setImageBitmap(bMap);
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
}

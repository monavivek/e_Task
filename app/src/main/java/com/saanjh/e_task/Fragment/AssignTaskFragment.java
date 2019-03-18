package com.saanjh.e_task.Fragment;

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
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.Model.BeanParameter;
import com.saanjh.e_task.Adapter.EmployeAdaptor;
import com.saanjh.e_task.Rest.HttpGetAsyncTaskFragment;
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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AssignTaskFragment extends Fragment {
    private static final int CAMERA_PIC_REQUEST = 1;
    private static final int RESULT_LOAD_IMAGE = 3;
    private static String com;
    public static String imgFilename = "";
    public static EditText SELECT_EMPLOYEE, T_DATE, UPLOAD, SUB, MSG, COMP_NAME, ATTACH;
    public static String ASSIGN_TO = "", SR_NO = "";
    private static String url2 = Config.MAIN_URL + Config.URL_EMPLOYEE_LIST + "?";
    private static Context context;
    private static Calendar c = Calendar.getInstance();
    private static SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
    private static String iemi = "", image_path = "";
    private static Button SEND;
    ListView employee_list;
    String currentDate = "", str_result = "";
    EmployeAdaptor adapter;
    List<BeanParameter> list_array_task = new ArrayList<BeanParameter>();
    ProgressDialog progDialog;
    private String ASSIGN_BY, EMP_ID;
    private RadioGroup rg;
    private RadioButton PRIORITY;
    private ImageView image_task;
    private static String progressBarStatus = String.valueOf(0);
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_assigntask, container, false);
        context = getActivity();
        requestMultiplePermissions();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
        image_task = (ImageView) rootView.findViewById(R.id.task_image);
        com = AppLockerPreference.getInstance(context).getM_empid();
        SUB = (EditText) rootView.findViewById(R.id.subject);
        SELECT_EMPLOYEE = (EditText) rootView.findViewById(R.id.list_dilog);
        MSG = (EditText) rootView.findViewById(R.id.msg);
        T_DATE = (EditText) rootView.findViewById(R.id.target_date);
        COMP_NAME = (EditText) rootView.findViewById(R.id.comp_name);
        ATTACH = (EditText) rootView.findViewById(R.id.attachment);
        UPLOAD = (EditText) rootView.findViewById(R.id.upload_img);
        SEND = (Button) rootView.findViewById(R.id.asiign_send);
        rg = (RadioGroup) rootView.findViewById(R.id.r_1);
        ASSIGN_BY = AppLockerPreference.getInstance(context).getM_username();
        EMP_ID = AppLockerPreference.getInstance(context).getM_empid();
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                //SEY SOMTHING LIKE YOU CANT ACCESS WITHOUT PERMISSION
            } else {
              //  doSomthing();
                iemi = tm.getDeviceId();
            }
        //iemi = tm.getDeviceId();
        SELECT_EMPLOYEE.setKeyListener(null);
        UPLOAD.setKeyListener(null);
        T_DATE.setKeyListener(null);
        currentDate = dformat.format(c.getTime());
        loadimage();
        final HttpGetAsyncTaskFragment http = new HttpGetAsyncTaskFragment(getActivity(), new FragmentCallback() {

            public void onTaskDone(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    System.out.println(url2 + jsonObject.toString());
                    System.out.println(result);
                    if (jsonObject.getString("status").equals("Error")) {
                        Toast.makeText(getActivity(), "Nothing to Display", Toast.LENGTH_LONG).show();
                    } else{
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            System.out.println(jsonObject1.getString("user_name"));
                            BeanParameter beanParameter = new BeanParameter();
                            beanParameter.setStr_ename(jsonObject1.getString("user_name"));
                            beanParameter.setStr_assign_to_id(jsonObject1.getString("sr_no"));
                            beanParameter.setStr_serial(jsonObject1.getString("srno"));
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

         //   String str_query = "select * from task_manager_app where coalesce(null,user_name,'')<>'admin' order by user_name desc";
         //   http.execute(url2 +"user_name="+SELECT_EMPLOYEE.getText().toString());
            System.out.println("company_code=" + com + "::" + AppLockerPreference.getInstance(context).getM_empid() + "::" + AppLockerPreference.getInstance(context).getM_username());
            //   String str_query = "select * from task_manager_app where coalesce(null,user_name,'')<>'admin' order by user_name desc";
            http.execute(url2.trim() + "empid=" + com.trim());
            System.out.println(url2.trim() + "empid=" + com.trim());


        } catch (Exception e) {
            e.printStackTrace();
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
        }

        UPLOAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(Environment.getExternalStorageDirectory(), "temptask.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            startActivityForResult(intent, CAMERA_PIC_REQUEST);
                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            File f = new File(Environment.getExternalStorageDirectory(),
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
        T_DATE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                T_DATE.setError(null);
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        SELECT_EMPLOYEE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!list_array_task.isEmpty()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setCancelable(false);
                    View dialogView = inflater.inflate(R.layout.employee_l, null);
                    alertDialog.setView(dialogView);
                    employee_list = (ListView) dialogView.findViewById(R.id.emp_list);
                    adapter = new EmployeAdaptor(getActivity(), R.layout.employee_list, list_array_task);
                    employee_list.setAdapter(adapter);
                    alertDialog.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                } else {
                    Toast.makeText(getActivity(), "Employee list is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        SEND.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        ASSIGN_TO = SELECT_EMPLOYEE.getText().toString();
                        int selectedId = rg.getCheckedRadioButtonId();
                        PRIORITY = (RadioButton) rootView.findViewById(selectedId);
                        if (T_DATE.getText().toString().length() == 0) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setCancelable(false);
                            alertDialog.setMessage("Please select the target date");
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    }
                            });
                            AlertDialog dialog = alertDialog.create();
                            dialog.show();
                      } else if (SUB.getText().length() == 0) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setCancelable(false);
                            alertDialog.setMessage("Please Enter Subject");
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog dialog = alertDialog.create();
                            dialog.show();
                        } else if (COMP_NAME.getText().toString().length() == 0) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setCancelable(false);
                            alertDialog.setMessage("Please enter company name");
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    }
                            });
                            AlertDialog dialog = alertDialog.create();
                            dialog.show();
                        } else if (MSG.getText().toString().length() == 0) {

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setCancelable(false);
                            alertDialog.setMessage("Please Enter Message");
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    }
                            });
                            AlertDialog dialog = alertDialog.create();
                            dialog.show();

                        } else if (ATTACH.getText().toString().length() == 0) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setCancelable(false);
                            alertDialog.setMessage("enter attachment or link or type null");
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    }
                            });
                            AlertDialog dialog = alertDialog.create();
                            dialog.show();

                        } else if (rg.getCheckedRadioButtonId() == -1) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setCancelable(false);
                            alertDialog.setMessage("Please select the priority of the Task");
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    }
                            });
                            AlertDialog dialog = alertDialog.create();
                            dialog.show();

                        } else {
                            try {
                               AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected void onPreExecute() {
                                        progDialog = new ProgressDialog(context);
                                        progDialog.setMessage("Please wait.");
                                        progDialog.setCancelable(false);
                                        progDialog.setIndeterminate(true);
                                        //progDialog.show();
                                        progressBarStatus = String.valueOf(0);
                                    }

                                    @Override
                                    protected Void doInBackground(Void... arg0) {
                                        try {
                                            progressBarStatus = doSomeWork1(ASSIGN_TO, SUB.getText().toString(), T_DATE.getText().toString(), COMP_NAME.getText().toString(), MSG.getText().toString(), ATTACH.getText().toString(), PRIORITY.getText().toString(), image_path, ASSIGN_BY, EMP_ID, SR_NO);
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
                                                    SELECT_EMPLOYEE.setText("");
                                                    SUB.setText("");
                                                    T_DATE.setText("");
                                                    COMP_NAME.setText("");
                                                    MSG.setText("");
                                                    ATTACH.setText("");
                                                    PRIORITY.setChecked(false);
                                                    UPLOAD.setText("");
                                                    SELECT_EMPLOYEE.requestFocus();
                                                    image_task.setImageResource(android.R.color.transparent);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            String ex=e.toString();
                                            String line_number= String.valueOf(e.getStackTrace()[0]);
                                            ExceptionHandling exceptionHandling=new ExceptionHandling();
                                            exceptionHandling.handler(ex,line_number,context);
//                                            System.out.print(Thread.currentThread().getStackTrace()[1].
//                                                    getLineNumber());
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
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
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
            UPLOAD.setText(image_path);
            File file = new File(path + File.separator + image_path);
            try {
                outFile = new FileOutputStream(file);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 60, outFile);
                outFile.flush();
                outFile.close();
            } catch (Exception e) {
                String ex=e.toString();
                String line_number= String.valueOf(e.getStackTrace()[0]);
                ExceptionHandling exceptionHandling=new ExceptionHandling();
                exceptionHandling.handler(ex,line_number,context);
                e.printStackTrace();
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

    public interface FragmentCallback {
        void onTaskDone(String result);
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
                if (month < c.get(Calendar.MONTH) + 1) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Year should not be smaller then current date!!");
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            T_DATE.setText("");
                            T_DATE.requestFocus();
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                }

            } else if (month < c.get(Calendar.MONTH) + 1) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setCancelable(false);
                alertDialog.setMessage("Month should not be smaller then current date!!");
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        T_DATE.setText("");
                        T_DATE.requestFocus();
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
                        T_DATE.setText("");
                        T_DATE.requestFocus();
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            } else {
                GregorianCalendar d = new GregorianCalendar(year, month - 1, day);
                String date = dformat.format(d.getTime());
                T_DATE.setText(date);
                }
        }
    }
    private String doSomeWork1(String str_assign, String str_subject, String str_date, String str_cname, String str_msg, String str_attach, String str_priority, String str_imagepath, String str_assignby, String str_empid, String str_assign_id) {
        String jsonStr = null;
        if (Utilities.hasConnection(getActivity())) {
            try {
                ServiceHandler sh = new ServiceHandler();
                jsonStr = "";
                jsonStr = execute_query(str_assign, str_subject, str_date, str_cname, str_msg, str_attach, str_priority, str_imagepath, str_assignby, str_empid, str_assign_id);
                Log.d("Response: ", "> " + jsonStr);
                if (jsonStr != null && !jsonStr.equals("error")) {
                    System.out.println("json=  " + jsonStr);
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    System.out.println(url2 + jsonObject.toString());
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
        return jsonStr;
    }
    public static String execute_query(String str_assign, String str_subject, String str_date, String str_cname, String str_msg, String str_attach, String str_priority, String str_imagepath, String str_assignby, String str_empid, String str_assign_id) {
        String str1 = "";
        byte[] encodeValue = new byte[0];
        try {
            encodeValue = Base64.encode(str_assign.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
        }
        String encode = new String(encodeValue);
        String url=Config.MAIN_URL+Config.URL_TASK_ASSIGN;
        HttpPost post= new HttpPost(url);
        HttpClient httpclient = new DefaultHttpClient();
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
            nameValuePairs.add(new BasicNameValuePair("assign_id", str_assign_id));
//            System.out.println("username"+nameValuePairs);
//            System.out.println("url="+post+httpclient.toString());
            UrlEncodedFormEntity entity=new UrlEncodedFormEntity(nameValuePairs,"UTF-8");
            post.setEntity(entity);
            HttpResponse response = httpclient.execute(post);
            System.out.println("hle="+url+nameValuePairs.toString());
            String responseBody = EntityUtils.toString(response.getEntity());
            str1 = responseBody.trim();
            System.out.println("res" +responseBody);
            Utilities.LogDebug(str1);
        } catch (Exception e) {
            Utilities.LogDebug("error " + e.getMessage());
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
            System.out.println("line_number"+line_number);
            }
        return str1;
    }
    private void  requestMultiplePermissions(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			Dexter.withActivity(getActivity())
					.withPermissions(
							Manifest.permission.CAMERA,
							Manifest.permission.WRITE_EXTERNAL_STORAGE,
							Manifest.permission.READ_EXTERNAL_STORAGE)
					.withListener(new MultiplePermissionsListener() {
						@Override
						public void onPermissionsChecked(MultiplePermissionsReport report) {
							// check if all permissions are granted
							if (report.areAllPermissionsGranted()) {
								Toast.makeText(getContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
							}

							// check for permanent denial of any permission
							if (report.isAnyPermissionPermanentlyDenied()) {
								// show alert dialog navigating to Settings
								//openSettingsDialog();
							}
						}
						@Override
						public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
							token.continuePermissionRequest();
						}
					}).
					withErrorListener(new PermissionRequestErrorListener() {
						@Override
						public void onError(DexterError error) {
							Toast.makeText(getContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
						}
					})
					.onSameThread()
					.check();
		}
	}
}


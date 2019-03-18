package com.saanjh.e_task.Utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.saanjh.e_task.App.Config;

public class UploadData extends Activity {

    public static String IMEINumber = "";
    public static int serverResponseCode=0;
    private static String format = "MM/dd/yyyy HH:mm:ss";
    public static String uploadFilePath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/taskmanagerimages/";

    private static String server_ip = "10.0.0.116:5444";//"115.112.58.54:7070";
    private static String urlString = Config.MAIN_URL + Config.URL_UPLOAD_IMAGE_FILE;

    private String uid, pwd;
    private static String return_str;

    public static void uploadfiles(Context context,String image_path) {
//        final String dir = Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/taskmanagerimages/";
       // System.out.println("First_Dirctory"+dir);
        //File newdir = new File(dir);

        IMEINumber=image_path;
       // newdir.mkdirs();

//        try {
//            if (newdir.mkdirs()) {
//                System.out.println("Directory created");
//            } else {
//                System.out.println("Directory is not created");
//            }
//        }
//            catch(Exception e){
//            e.printStackTrace();
//
//            }

        File fileDirectory = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/taskmanagerimages/");

        File[] dirFiles = fileDirectory.listFiles();

        System.out.println("fileDirectory"+fileDirectory);

        if (dirFiles.length != 0) {
            for (int ii = 0; ii < dirFiles.length; ii++) {
                String fileOutput = dirFiles[ii].getName().toString();
                //doFileUpload(fileOutput, IMEINumber, context);
                doFileUpload( uploadFilePath+ "" + IMEINumber,context);
                System.out.println("IMEINumber"+IMEINumber);
            }
        }
    }

    @SuppressWarnings({"deprecation"})
    private static void doFileUpload(String sourceFileUri,Context context) {
        String fileName = sourceFileUri;
        // System.out.println("Im"+IMEI);
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;

//        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                + "/taskmanagerimages/";
        // String exsistingFileName = dir + str_file;// + "Images";
        //System.out.println("Exsisting_Dirctory"+exsistingFileName);

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;

        byte[] buffer;

        int maxBufferSize = 64 * 1024;
        File sourceFile = new File(sourceFileUri);
        //server_ip = context.getResources().getString(R.string.ipaddress);
        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :"
                    + urlString + "" + IMEINumber);
        } else {

            try {

                Log.e("MediaPlayer", "Inside second Method");

           FileInputStream fileInputStream = new FileInputStream(new File(String.valueOf(sourceFile)));
//            System.out.println("fileInputStream="+fileInputStream.toString());

                URL url = new URL(urlString);

                conn = (HttpURLConnection) url.openConnection();

                conn.setDoInput(true);

                // Allow Outputs
                conn.setDoOutput(true);

                // Don't use a cached copy.
                conn.setUseCaches(false);

                // Use a post method.
                conn.setRequestMethod("POST");


                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.setRequestProperty("ENCTYPE", "multipart/form-data");


                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes(
                        "Content-Disposition: form-data; name=uploaded_file;filename="
                                + fileName + "" + lineEnd);
                dos.writeBytes(lineEnd);

                Log.e("MediaPlayer", "Headers are written");

                bytesAvailable = fileInputStream.available();
                System.out.println("bytesAvailable=" + bytesAvailable);
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                System.out.println("bytesRead=" + bytesRead);


                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();
                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                if(serverResponseCode == 200){
                   Log.e("File Upload Complete.",serverResponseMessage);
                }


//                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String inputLine;
//                System.out.println("in" + in);
//
//                while ((inputLine = in.readLine()) != null)
//                    Log.e("MediaPlayer", inputLine);
//                // tv.append(inputLine);

                // close streams
                Log.e("MediaPlayer", "File is written");
                fileInputStream.close();
                dos.flush();
                dos.close();

               // File file = new File(exsistingFileName);
              //  file.delete();
            } catch (MalformedURLException e) {
                String ex = e.toString();
                String line_number = String.valueOf(e.getStackTrace()[0]);
                ExceptionHandling exceptionHandling = new ExceptionHandling();
                exceptionHandling.handler(ex, line_number, context);
                Log.e("MediaPlayer", "error: " + e.getMessage(), e);
            } catch (IOException ioe) {
                String ex = ioe.toString();
                String line_number = String.valueOf(ioe.getStackTrace()[0]);
                ExceptionHandling exceptionHandling = new ExceptionHandling();
                exceptionHandling.handler(ex, line_number, context);
                Log.e("MediaPlayer", "error: " + ioe.getMessage(), ioe);
            }

            try {
                inStream = new DataInputStream(conn.getInputStream());
                String str;
                while ((str = inStream.readLine()) != null) {
                    Log.e("MediaPlayer", "Server Response" + str);
                }

                inStream.close();

            } catch (IOException ioex) {
                String ex = ioex.toString();
                String line_number = String.valueOf(ioex.getStackTrace()[0]);
                ExceptionHandling exceptionHandling = new ExceptionHandling();
                exceptionHandling.handler(ex, line_number, context);
                Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
            }

        }

    }

}
package com.saanjh.e_task.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class Utilities {
	private static Context context;
	private static final int LOGLEVEL = 5;
	private static long latestTimeStamp;

	public static long getLatestTimeStamp() {
		return latestTimeStamp;
	}

	public static void setLatestTimeStamp(long latestTimeStamp) {
		Utilities.latestTimeStamp = latestTimeStamp;
	}

	private static void LogToDebugFile(String message) {
		// DebugLogger.Write(message);
	}

	public static void LogInfo(String message) {
		if (LOGLEVEL >= 3) {
			Log.i("GPSLogger", message);
		}

		LogToDebugFile(message);

	}

	public static void LogError(String methodName, Exception ex) {
		try {
			LogError(methodName + ":" + ex.getMessage());
		} catch (Exception e) {
			String exe=e.toString();
			String line_number= String.valueOf(e.getStackTrace()[0]);
			ExceptionHandling exceptionHandling=new ExceptionHandling();
			exceptionHandling.handler(exe,line_number,context);
			/**/
		}
	}

	private static void LogError(String message) {
		Log.e("GPSLogger", message);
		LogToDebugFile(message);
	}

	public static void LogDebug(String message) {
		if (LOGLEVEL >= 4) {
			Log.d("GPSLogger", message);
		}
		LogToDebugFile(message);
	}

	public static void LogWarning(String message) {
		if (LOGLEVEL >= 2) {
			Log.w("GPSLogger", message);
		}
		LogToDebugFile(message);
	}

	public static void LogVerbose(String message) {
		if (LOGLEVEL >= 5) {
			Log.v("GPSLogger", message);
		}
		LogToDebugFile(message);
	}

	public Utilities(Context _context) {
		context = _context;

	}

	public static String GetIsoDateTime(Date dateToFormat) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		return sdf.format(dateToFormat);
	}

	public static String GetReadableDateTime(Date dateToFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
		return sdf.format(dateToFormat);
	}

	public static int MetersToFeet(int m) {
		return (int) Math.round(m * 3.2808399);
	}

	public static int FeetToMeters(int f) {
		return (int) Math.round(f / 3.2808399);
	}

	public static int MetersToFeet(double m) {
		return MetersToFeet((int) m);
	}

	public static String imei_get() {
		String IMEINumber = "";
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		//IMEINumber = tm.getDeviceId();
		return IMEINumber;
	}

	public static boolean hasConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetwork != null && wifiNetwork.isConnected()) {
				return true;
			}

			NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mobileNetwork != null && mobileNetwork.isConnected()) {
				return true;
			}
		} else {
			return false;
		}

		return false;
	}
}

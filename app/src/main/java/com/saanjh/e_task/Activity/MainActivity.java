package com.saanjh.e_task.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.messaging.FirebaseMessaging;
import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Fragment.TabsFragment;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.Fragment.DrawerFragment;
import com.saanjh.e_task.Fragment.AssignTaskFragment;
import com.saanjh.e_task.Fragment.NotificationFragment;
import com.saanjh.e_task.Fragment.SendMsgFragment;
import com.saanjh.e_task.Fragment.MyTaskListFragment;
import com.saanjh.e_task.R;
import com.saanjh.e_task.Utils.ExceptionHandling;
import com.saanjh.e_task.Utils.NotificationUtils;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DrawerFragment.FragmentDrawerListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Context context;
    private static String com;
    private Toolbar mToolbar;
    private DrawerFragment drawerFragment;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    private String user_name;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
         setSupportActionBar(mToolbar);
        com = AppLockerPreference.getInstance(context).getM_company();
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        user_name = AppLockerPreference.getInstance(context).getM_username();
        System.out.println(user_name);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            	// checking for type intent filter
				if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                  FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                    } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    try {
                        String message = intent.getStringExtra("message");
                       // String message = intent.getStringExtra("message");
                        Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                        Log.e(TAG, message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String ex=e.toString();
                        String line_number= String.valueOf(e.getStackTrace()[1]);
                        ExceptionHandling exceptionHandling=new ExceptionHandling();
                        exceptionHandling.handler(ex,line_number,context);
                    }
                }
            }
        };
        displayFirebaseRegId();
        drawerFragment = (DrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
       displayView(0);
    }
    /*
    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            //noinspection SimplifiableIfStatement
            if(id== R.id.action_logout){
                try {
                    AppLockerPreference.getInstance(context).setM_reg("");
                    AppLockerPreference.getInstance(context).setM_username("");
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    finish();
                    startActivity(i);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                return true;
            }
            return super.onOptionsItemSelected(item);
        }*/
    @Override
    public void onDrawerItemSelected(View view, int position) {

        displayView(position);
    }
    private void displayView(int position) {

        Fragment fragment = null;
        if (user_name.equals("admin")) {

            switch (position) {
                case 0:
                    fragment = new TabsFragment();
                    break;
                case 1:
                    fragment = new MyTaskListFragment();
                    break;
                case 2:
                    fragment = new AssignTaskFragment();
                    break;
                case 3:
                    fragment = new NotificationFragment();
                    break;
                case 4:
                    Intent j = new Intent(MainActivity.this, AboutUs.class);
                    startActivity(j);
                    break;
                case 5:
                    fragment=new SendMsgFragment();
                    break;
                case 6:
                    AppLockerPreference.getInstance(context).setstr_mobno("");
                    AppLockerPreference.getInstance(context).setM_username("");
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    finish();
                    startActivity(i);
                    break;
                 default:
                    break;
            }
        } else {
            switch (position) {
                case 0:
                    fragment = new TabsFragment();
                    break;
                case 1:
                    fragment = new MyTaskListFragment();
                    break;
                case 2:
                    fragment = new AssignTaskFragment();
                    break;
                case 3:
                    fragment = new NotificationFragment();
                    break;

                case 4:
                    Intent j = new Intent(MainActivity.this, AboutUs.class);
                    startActivity(j);
                    break;
                case 5:
                    AppLockerPreference.getInstance(context).setstr_mobno("");
                    AppLockerPreference.getInstance(context).setM_username("");
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    finish();
                    startActivity(i);
                    break;
                default:
                    break;
            }
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            System.out.println(fragment.toString());
            fragmentTransaction.replace(R.id.container_body, fragment, fragment.toString()).addToBackStack(fragment.getTag()).commit();
            // set the toolbar title
            DrawerFragment.adapter.setSelected(position);
            getSupportActionBar().setTitle(DrawerFragment.titles[position]);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = null;
        if (fragmentManager.getBackStackEntryCount() > 1) {
            String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            currentFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        } else {
            finish();
        }
        return currentFragment;
    }
    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e(TAG, "Firebase reg id: " + regId);
        if (!TextUtils.isEmpty(regId))
            Log.e(TAG, "Firebase Reg Id: " + regId);
        else
            Log.e(TAG, "Firebase Reg Id is not received yet!");
    }
    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        Fragment fragmentBeforeBachPress = getCurrentFragment();
        if(fragmentBeforeBachPress != null){
            if(fragmentBeforeBachPress.getTag().contains("TabsFragment"))
            {
                finish();
            }
            else{
                TabsFragment frag = new TabsFragment();//TestHomeLayout{41cab5b0}
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container_body, frag, "TabsFragment").addToBackStack(frag.getTag()).commit();
                setTitle(DrawerFragment.titles[0]);
                DrawerFragment.adapter.setSelected(0);
            }
        }else{
            finish();
        }
    }
}
package com.saanjh.e_task.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.R;

public class NotificationDetail extends AppCompatActivity {

    private static final String TAG = NotificationDetail.class.getSimpleName();
    private String url = Config.MAIN_URL + Config.URL_IMAGES, FullUrl = "";
    TextView assigned, subject, priority, message, reference, imageText;
    ImageView image;
    Context context;
    String Msg, AssignedBy, Date, Subject, Image, Attachment, Priority;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_detail);
        context=this;

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Description");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Msg = bundle.getString("msg");
            AssignedBy = bundle.getString("assign");
            Date = bundle.getString("date");
            Subject = bundle.getString("subject");
            Image = bundle.getString("image");
            Attachment = bundle.getString("attachment");
            Priority = bundle.getString("priority");
            System.out.println("hlw"+Attachment+"image"+image);
        }

        assigned = (TextView)findViewById(R.id.assigned_by);
        assigned.setText(AssignedBy);
        subject = (TextView)findViewById(R.id.subject);
        subject.setText(Subject);
        priority = (TextView)findViewById(R.id.priority);
        priority.setText(Priority);
        message = (TextView)findViewById(R.id.message);
        message.setText(Msg);
        reference = (TextView)findViewById(R.id.reference);
        reference.setText(Attachment);
        image = (ImageView) findViewById(R.id.notification_image);
        imageText = (TextView)findViewById(R.id.notification_text);

        FullUrl =url+Image;
        System.out.println("urlImage="+FullUrl);
        if(!Image.equals("")){
            if (image.getVisibility()== View.GONE){
                imageText.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);

                ImageLoader.getInstance().displayImage(FullUrl,image);
            }
        }else {
            imageText.setText("Image Not Available");
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

package com.saanjh.e_task.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.saanjh.e_task.App.Config;
import com.saanjh.e_task.Model.BeanParameter;
import com.saanjh.e_task.R;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<BeanParameter> {

    public Context context;
    private List<BeanParameter> list_array_task;
    private String url = Config.MAIN_URL + Config.URL_IMAGES, FullUrl = "";
    public CustomAdapter(Context context, int textViewResourceId, List<BeanParameter> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.list_array_task = objects;
    }
    @Override
    public int getCount() {
        return this.list_array_task.size();
    }
    @Override
    public BeanParameter getItem(int index) {
        return list_array_task.get(index);
    }
    @Override
    public boolean hasStableIds() {
        return true;
    }
    @Override
    public int getViewTypeCount() {
        if (getCount() < 1) {
            return 1;
        } else {
            return getCount();
        }
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        Holder holder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_task, null);
            holder = new Holder();

            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }
        final BeanParameter bean = list_array_task.get(position);
        holder.txt_assign = (TextView) row.findViewById(R.id.txt_assign);
        holder.txt_subject = (TextView) row.findViewById(R.id.txt_subject);
        holder.txt_cname = (TextView) row.findViewById(R.id.txt_company_name);
        holder.txt_msg = (TextView) row.findViewById(R.id.txt_message);
        holder.txt_prior = (TextView) row.findViewById(R.id.txt_priority);
        holder.txt_url = (TextView) row.findViewById(R.id.txt_refrenceLinks);
        holder.txt_deadline = (TextView) row.findViewById(R.id.txt_deadline);
        holder.img_task = (ImageView) row.findViewById(R.id.img_task);
        holder.img_seen = (ImageView) row.findViewById(R.id.img_seen);
        holder.txt_task_id = (TextView) row.findViewById(R.id.txt_task_id);
        holder.txt_assign.setText(bean.getStr_assignby());
        holder.txt_subject.setText(bean.getStr_sub());
        holder.txt_cname.setText(bean.getStr_cname());
        holder.txt_msg.setText(bean.getStr_mssg());
        holder.txt_url.setText(bean.getStr_att());
        holder.txt_deadline.setText(bean.getStr_tdate());
        holder.txt_task_id.setText(bean.getStr_task_id());
        Log.d("task_id=",bean.getStr_task_id());
        Log.d("holder=", String.valueOf(holder.txt_task_id));

        if (bean.getStr_photo().equals("")) {
            holder.img_task.setImageResource(R.drawable.no_img);
        } else {
            FullUrl = url + bean.getStr_photo();
            ImageLoader.getInstance().displayImage(FullUrl, holder.img_task);
        }

        if (bean.getStr_task_seen().equals("1")) {
            holder.img_seen.setImageResource(R.drawable.tick);
        }

        switch (bean.getStr_prior()) {
            case "High":
                holder.txt_prior.setText((Html.fromHtml("<font color='red'><b>High</b></font>")));
                break;
            case "Avg":
                holder.txt_prior.setText((Html.fromHtml("<font color='blue'><b>Avg</b></font>")));
                break;
            case "Low":
                holder.txt_prior.setText((Html.fromHtml("<font color='yellow'><b>Low</b></font>")));
                break;
        }


        holder.img_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        getContext());
                LayoutInflater factory = LayoutInflater.from(getContext());
                final View view1 = factory.inflate(R.layout.taskimg_layout, null);

                ImageView img_task = (ImageView) view1.findViewById(R.id.img_task);

                if (bean.getStr_photo().equals("")) {
                    img_task.setImageResource(R.drawable.no_img);
                } else {
                    FullUrl = url + bean.getStr_photo();
                    ImageLoader.getInstance().displayImage(FullUrl, img_task);
                }
                alert.setView(view1);
                alert.show();
            }
        });

        return row;
    }
    public class Holder {
        TextView txt_assign, txt_subject, txt_cname, txt_msg, txt_url, txt_deadline, txt_task_id, txt_prior;
        ImageView img_task, img_seen;
    }
}
package com.saanjh.e_task.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.saanjh.e_task.Activity.NotificationDetail;
import com.saanjh.e_task.Model.BeanParameter;
import com.saanjh.e_task.R;
import java.util.List;

public class NotificationAdapter extends ArrayAdapter<BeanParameter> {

    public Context context;
    private List<BeanParameter> list_array_notification;
    public NotificationAdapter(Context context, int textViewResourceId, List<BeanParameter> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.list_array_notification = objects;
    }

    @Override
    public int getCount() {
        return this.list_array_notification.size();
    }

    @Override
    public BeanParameter getItem(int index) {
        return list_array_notification.get(index);
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
            row = inflater.inflate(R.layout.list_notify, null);
            holder = new Holder();
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }
        final BeanParameter bean = list_array_notification.get(position);
        holder.txt_msg = (TextView) row.findViewById(R.id.txt_notificationmsg);
        holder.txt_msg.setText(bean.getStr_notify_msg());
        holder.txt_msg_assignby = (TextView) row.findViewById(R.id.txt_assignby);
        holder.txt_msg_assignby.setText(bean.getStr_notify_by());
        holder.txt_msg_date = (TextView) row.findViewById(R.id.txt_notify_date);
        holder.txt_msg_date.setText(bean.getStr_notify_date());
        holder.notifyLinear = (LinearLayout)row.findViewById(R.id.notify_linear);
        holder.notifyLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NotificationDetail.class);
                intent.putExtra("msg", bean.getStr_notify_msg());
                intent.putExtra("assign", bean.getStr_notify_by());
                intent.putExtra("date", bean.getStr_notify_date());
                intent.putExtra("subject", bean.getStr_notify_subject());
                intent.putExtra("image", bean.getStr_notify_image());
                intent.putExtra("attachment", bean.getStr_notify_attachment());
                intent.putExtra("priority", bean.getStr_notify_priority());
                context.startActivity(intent);
                System.out.println("image "+bean.getStr_notify_image());
            }
        });
        return row;
    }
    public class Holder {
        TextView txt_msg, txt_msg_date, txt_msg_assignby;
        LinearLayout notifyLinear;
    }
}
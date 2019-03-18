package com.saanjh.e_task.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.saanjh.e_task.Helper.AppLockerPreference;
import com.saanjh.e_task.Model.NavDrawerItem;
import com.saanjh.e_task.R;
import com.saanjh.e_task.Utils.ExceptionHandling;
import java.util.Collections;
import java.util.List;

public class NavDrawerListAdapter extends RecyclerView.Adapter<NavDrawerListAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    public NavDrawerListAdapter(Context context, List<NavDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
       this.data = data;
        mPref = context.getSharedPreferences("person", Context.MODE_PRIVATE);
        mEditor = mPref.edit();
    }
    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.img_title_icon.setImageResource(current.getIcon());
        Log.e("selection", "" + data.get(position).isSelected());
        if (data.get(position).isSelected()) {
            holder.list_row.setBackgroundColor(Color.parseColor("#46B06F"));
        } else {
            holder.list_row.setBackgroundColor(Color.TRANSPARENT);
        }
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        String value;
        ImageView img_title_icon;
        RelativeLayout list_row;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            img_title_icon = (ImageView) itemView.findViewById(R.id.icon);
            list_row = (RelativeLayout) itemView.findViewById(R.id.listrow);
            value=AppLockerPreference.getInstance(context).getM_company();
        }
    }
    public void setSelected(int pos) {
        try {
            if (data.size() > 1) {
                data.get(mPref.getInt("position", 0)).setSelected(false);
                mEditor.putInt("position", pos);
                mEditor.commit();
            }
            data.get(pos).setSelected(true);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            String ex=e.toString();
            String line_number= String.valueOf(e.getStackTrace()[0]);
            ExceptionHandling exceptionHandling=new ExceptionHandling();
            exceptionHandling.handler(ex,line_number,context);
        }
    }
}
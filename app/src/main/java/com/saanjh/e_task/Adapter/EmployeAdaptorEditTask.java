package com.saanjh.e_task.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.saanjh.e_task.Model.BeanParameter;
import com.saanjh.e_task.Activity.EditTaskActivity;
import com.saanjh.e_task.Fragment.AssignTaskFragment;
import com.saanjh.e_task.R;
import com.saanjh.e_task.Utils.ExceptionHandling;

import java.util.ArrayList;
import java.util.List;

public class EmployeAdaptorEditTask extends ArrayAdapter<BeanParameter> {

    public BeanParameter dev1 = null;
    ArrayList<Boolean> positionArray;
    String result ="",token="",sr_no="";
    private Context context;
    private List<BeanParameter> employee = new ArrayList<BeanParameter>();

    public EmployeAdaptorEditTask(Context context, int resource, List<BeanParameter> objects) {
        super(context, resource, objects);
        this.context = context;
        this.employee = objects;
        positionArray = new ArrayList<Boolean>(employee.size());
        for (int i = 0; i < employee.size(); i++) {
            positionArray.add(false);
        }
    }
    @Override
    public int getCount() {
        return this.employee.size();
    }

    @Override
    public BeanParameter getItem(int index) {
        return employee.get(index);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint({"DefaultLocale", "InflateParams"})
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.employee_list, null);
            holder = new ViewHolder();

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
            holder.chk_ofemp.setOnCheckedChangeListener(null);
        }
        dev1 = getItem(position);
        holder.srno = (TextView) row.findViewById(R.id.sr_no);
        holder.empname = (TextView) row.findViewById(R.id.emp_name);
        holder.chk_ofemp = (CheckBox) row.findViewById(R.id.chkbox);
        String str1 = dev1.getStr_serial();
        String str2 = dev1.getStr_ename();
        holder.chk_ofemp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    result = "";
                    token = "";
                    sr_no="";
                    AssignTaskFragment.SELECT_EMPLOYEE.setText("");

                    for (int i = 0; i < positionArray.size(); i++) {

                        if (positionArray.get(i) == true) {
                            dev1 = getItem(i);
                            sr_no=sr_no+dev1.getStr_assign_to_id()+",";
                            token = token + dev1.getStr_token_employee() +",";
                            result = result + dev1.getStr_ename() + ",";
                            MyAssignTaskAdapter.token_id = token;
                            EditTaskActivity.EMP_SRNO=sr_no;
                            EditTaskActivity.EMP_LIST.setText(result);
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
        });
        holder.srno.setText(str1);
        holder.empname.setText(str2);
        holder.chk_ofemp.setTag(position);
        holder.chk_ofemp.setChecked(positionArray.get(position));
        System.out.println(position);
        holder.chk_ofemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        positionArray.set(position, true);
                    } else {
                        positionArray.set(position, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String ex=e.toString();
                    String line_number= String.valueOf(e.getStackTrace()[0]);
                    ExceptionHandling exceptionHandling=new ExceptionHandling();
                    exceptionHandling.handler(ex,line_number,context);
                }
            }
        });
        return row;
    }
    private static class ViewHolder {
        TextView srno;
        TextView empname;
        CheckBox chk_ofemp;

    }
}
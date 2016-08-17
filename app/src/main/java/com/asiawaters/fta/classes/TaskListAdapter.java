package com.asiawaters.fta.classes;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asiawaters.fta.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TaskListAdapter extends ArrayAdapter<Model_ListMembers> implements View.OnClickListener{

    private ArrayList<Model_ListMembers> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView TaskName;
        TextView OutletName;
        TextView AgentName;
        TextView DeadLine;
        TextView OutletAddress;
        ImageView info;
    }



    public TaskListAdapter(ArrayList<Model_ListMembers> data, Context context) {
        super(context, R.layout.list_task_row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }


    @Override
    public void onClick(View v) {


        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Model_ListMembers dataModel=(Model_ListMembers)object;




//        switch (v.getId())
//        {
//
//            case R.id.item_info:
//
//                Snackbar.make(v, "Release date " +dataModel.getAppointmentDateOfTask(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//
//                break;
//
//
//        }


    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Model_ListMembers dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_task_row_item, parent, false);
            viewHolder.TaskName = (TextView) convertView.findViewById(R.id.taskname);
            viewHolder.OutletName = (TextView) convertView.findViewById(R.id.OutletName);
            viewHolder.AgentName = (TextView) convertView.findViewById(R.id.AgentName);
            viewHolder.DeadLine = (TextView) convertView.findViewById(R.id.DeadLine);
            viewHolder.OutletAddress = (TextView) convertView.findViewById(R.id.OutletAddress);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;


        viewHolder.TaskName.setText(dataModel.getTextProblem());
        viewHolder.OutletName.setText(dataModel.getOutletName());
        viewHolder.AgentName.setText(dataModel.getOutletAgent());
        viewHolder.OutletAddress.setText(dataModel.getOutletAddress());

        SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy");
        viewHolder.DeadLine.setText(date_format.format(dataModel.getDeadline()));
//        viewHolder.info.setOnClickListener(this);
//        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}

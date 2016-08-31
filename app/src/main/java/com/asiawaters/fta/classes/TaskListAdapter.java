package com.asiawaters.fta.classes;

import android.content.Context;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asiawaters.fta.FTA;
import com.asiawaters.fta.MainActivity;
import com.asiawaters.fta.R;

import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TaskListAdapter extends ArrayAdapter<Model_ListMembers> implements View.OnClickListener {

    Context mContext;
    MainActivity MA;

    // View lookup cache
    private static class ViewHolder {
        TextView TaskName;
        TextView OutletName;
        TextView AgentName;
        TextView DeadLine;
        TextView OutletAddress;
        LinearLayout list_row;
        ImageView black_state;
        ImageView color_state;

    }


    public TaskListAdapter(ArrayList<Model_ListMembers> data, Context context, MainActivity mainactivity) {
        super(context, R.layout.list_task_row_item, data);
        this.mContext = context;
        this.MA = mainactivity;
    }


    @Override
    public void onClick(View v) {
        String open = getContext().getResources().getString(R.string.label_open);

        v.setSelected(true);

        int position = (Integer) v.findViewById(R.id.taskname).getTag();
        Object object = getItem(position);
        final Model_ListMembers dataModel = (Model_ListMembers) object;
        ((FTA)getContext().getApplicationContext()).setTaskGuid(dataModel);
        ((FTA)getContext().getApplicationContext()).setFormStatus(2);
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FTA)getContext().getApplicationContext()).setFormStatus(0);
                open_task();
            }
        };
        Snackbar.make(v,dataModel.getTextProblem(), Snackbar.LENGTH_LONG)
                .setAction(open, mOnClickListener)
                .show();
    }

    public void open_task(){
        MA.startNextActivity();
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
            viewHolder.black_state = (ImageView) convertView.findViewById(R.id.state);
            viewHolder.color_state = (ImageView) convertView.findViewById(R.id.calendar);
            viewHolder.list_row = (LinearLayout) convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;
        viewHolder.TaskName.setText(dataModel.getTextProblem());
        viewHolder.OutletName.setText(dataModel.getOutletName());
        viewHolder.AgentName.setText(dataModel.getOutletAgent());
        viewHolder.OutletAddress.setText(dataModel.getOutletAddress());

        Calendar c = Calendar.getInstance();
        long millisec = c.getTimeInMillis();
        if (dataModel.isDone()) {
            viewHolder.black_state.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.state_done));
            viewHolder.color_state.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.calendar_green));
        }
        else {
            if (dataModel.getDeadline().getTime()<millisec){
                viewHolder.black_state.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.state_cloud));
                viewHolder.color_state.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.calendar_red));
            }
            else
            {
                viewHolder.black_state.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.state_cloud_white));
                viewHolder.color_state.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.calendar_yellow));

            }
        }

        SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy");

        viewHolder.DeadLine.setText(date_format.format(dataModel.getDeadline()));
        viewHolder.list_row.setOnClickListener(this);
        viewHolder.TaskName.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}

package com.asiawaters.fta.classes;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asiawaters.fta.FTA;
import com.asiawaters.fta.MainActivity;
import com.asiawaters.fta.R;
import com.asiawaters.fta.SearchActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class OutletListAdapter extends ArrayAdapter<ModelSearchedOutlets> implements View.OnClickListener {

    Context mContext;
    SearchActivity MA;

    // View lookup cache
    private static class ViewHolder {
        TextView Name;
        TextView Address;
        TextView ListAgentName;
        LinearLayout list_row;
    }


    public OutletListAdapter(ArrayList<ModelSearchedOutlets> data, Context context, SearchActivity SearchActivity) {
        super(context, R.layout.list_outlets_row_item, data);
        this.mContext = context;
        this.MA = SearchActivity;
    }


    @Override
    public void onClick(View v) {
        String open = getContext().getResources().getString(R.string.CreateNewTask);


        int position = (Integer) v.findViewById(R.id.listOutletName).getTag();
        Object object = getItem(position);
        final ModelSearchedOutlets dataModel = (ModelSearchedOutlets) object;

        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallNewTaskCration(dataModel.getGUID());
            }
        };
        Snackbar.make(v,dataModel.getName()+"/"+dataModel.getAddress(), Snackbar.LENGTH_LONG)
                .setAction(open, mOnClickListener)
                .show();
    }

    public void CallNewTaskCration(String Guid){
        ((FTA)getContext().getApplicationContext()).setTaskGuid(Guid);
        MA.startNextActivity();
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ModelSearchedOutlets dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_outlets_row_item, parent, false);
            viewHolder.Name = (TextView) convertView.findViewById(R.id.listOutletName);
            viewHolder.Address = (TextView) convertView.findViewById(R.id.listOutletAdress);
            viewHolder.ListAgentName =(TextView) convertView.findViewById(R.id.AgentNameList);
            viewHolder.list_row = (LinearLayout) convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;
        viewHolder.Name.setText(dataModel.getName());
        viewHolder.Address.setText(dataModel.getAddress());
        viewHolder.ListAgentName.setText(dataModel.getAgentName());
        viewHolder.Name.setTag(position);

        //Обработка на клик тут
        viewHolder.list_row.setOnClickListener(this);
        // Return the completed view to render on screen
        return convertView;
    }
}

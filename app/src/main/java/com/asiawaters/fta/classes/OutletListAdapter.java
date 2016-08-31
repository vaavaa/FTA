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
import java.util.Date;

public class OutletListAdapter extends ArrayAdapter<ModelSearchedOutlets> implements View.OnClickListener {

    Context mContext;
    SearchActivity MA;
    FTA fta;

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
        fta = ((com.asiawaters.fta.FTA)MA.getApplication());
    }


    @Override
    public void onClick(View v) {
        String open = getContext().getResources().getString(R.string.CreateNewTask);

        v.setSelected(true);
        int position = (Integer) v.findViewById(R.id.listOutletName).getTag();
        Object object = getItem(position);
        final ModelSearchedOutlets dataModel = (ModelSearchedOutlets) object;

        Model_TaskMember taskMembers = new Model_TaskMember();
        taskMembers.setDateOfExecutionFact(new Date());
        taskMembers.setDateOfCommencementFact(new Date());
        taskMembers.setInitiatorBP(fta.getUser());
        String initialStatusBP = MA.getResources().getString(R.string.InitialStatusBP);
        taskMembers.setStateTask(initialStatusBP);
        Model_TaskListFields[] MTLF = new Model_TaskListFields[3];
        MTLF[0] = new Model_TaskListFields();

        MTLF[0].setKey("Торговая точка");
        MTLF[0].setValue(dataModel.getName());

        MTLF[1] = new Model_TaskListFields();
        MTLF[1].setKey("OutletGUID");
        MTLF[1].setValue(dataModel.getGUID());

        MTLF[2] = new Model_TaskListFields();
        MTLF[2].setKey("Торговый агент");
        MTLF[2].setValue(dataModel.getAgentName());

        taskMembers.setmTaskListFields(MTLF);

        fta.setTaskMember(taskMembers);

        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallNewTaskCration();
            }
        };
        Snackbar.make(v, dataModel.getName() + "/" + dataModel.getAddress(), Snackbar.LENGTH_LONG)
                .setAction(open, mOnClickListener)
                .show();
    }

    public void CallNewTaskCration() {
        ((FTA) getContext().getApplicationContext()).setFormStatus(1);
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
            viewHolder.ListAgentName = (TextView) convertView.findViewById(R.id.AgentNameList);
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

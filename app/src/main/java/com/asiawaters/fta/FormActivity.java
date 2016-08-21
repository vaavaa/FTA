package com.asiawaters.fta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asiawaters.fta.classes.DatePickerFragment;
import com.asiawaters.fta.classes.Model_NewTask;
import com.asiawaters.fta.classes.Model_TaskListFields;
import com.asiawaters.fta.classes.Model_TaskMember;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class FormActivity extends AppCompatActivity {
    private FTA FTA;
    private String WDSLPath;
    Model_TaskMember taskMembers;
    String[] items = new String[]{""};
    private DatePickerFragment newFragment;
    private View clickedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_form);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousActivity();
            }
        });

        FTA = ((com.asiawaters.fta.FTA) getApplication());
        WDSLPath = FTA.getPath_url();
        taskMembers = FTA.getTaskMember();
        if (taskMembers != null) {
            setUILock(false);
            fillCommoninfo();
        } else {
            if (FTA.getTaskGuid() != null) {
                setUILock(true);
                new LoginTask().execute();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_send) {
            if (SendToServerCheck()) {
                Model_NewTask MNT = new Model_NewTask();
                MNT.setComment(((EditText) findViewById(R.id.task_description)).getText().toString());
                MNT.setIDAuthor(FTA.getPerson().getPerson_guid());
                MNT.setIDTradePoint(getValueByKey("OutletGUID"));
                SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy");
                SimpleDateFormat date_format_SOAP = new SimpleDateFormat("yyyy-MM-dd");
                String datesource = ((EditText) findViewById(R.id.deadline_field)).getText().toString();
                String newDate_format="";
                try {
                    Date dta = date_format.parse(datesource);
                    newDate_format = date_format_SOAP.format(dta);
                }
                catch (ParseException ex) {}
                MNT.setTheTermOfTheTask(newDate_format);
                FTA.setMNT(MNT);
                new CreateTask().execute();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        previousActivity();
    }

    public Boolean SendToServerCheck() {
        if (((EditText) findViewById(R.id.task_description)).getText() == null) {
            Toast.makeText(FormActivity.this, getResources().getString(R.string.NoTask), Toast.LENGTH_SHORT).show();
            return false;
        } else if (((EditText) findViewById(R.id.task_description)).getText().length() == 0) {
            Toast.makeText(FormActivity.this, getResources().getString(R.string.NoTask), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (((EditText) findViewById(R.id.deadline_field)).getText() == null) {
            Toast.makeText(FormActivity.this, getResources().getString(R.string.NoCutOfDate), Toast.LENGTH_SHORT).show();
            return false;
        } else if (((EditText) findViewById(R.id.deadline_field)).getText().length() == 0) {
            Toast.makeText(FormActivity.this, getResources().getString(R.string.NoCutOfDate), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy");
            String d_vle = ((EditText) findViewById(R.id.deadline_field)).getText().toString();
            Date Commitment_date = new Date();
            Date curr_date = new Date();
            try {
                Commitment_date = date_format.parse(d_vle);
            } catch (ParseException ex) {
            }
            if (Commitment_date.getTime() < curr_date.getTime()) {
                Toast.makeText(FormActivity.this, getResources().getString(R.string.InvalidDate), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    public void previousActivity() {
        this.finish();
        Intent intent;
        intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private String getValueByKey(String Key) {
        String value = "";
        for (Model_TaskListFields MTF : taskMembers.getmTaskListFields()) {
            if (MTF.getKey().equals(Key)) value = MTF.getValue();
        }
        return value;
    }

    public void runDatePickerCompliting() {
        Date Result = newFragment.getResultDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        EditText et = (EditText) findViewById(R.id.deadline_field);
        et.setText(sdf.format(Result));
    }

    private boolean doLogin() {

        String NAMESPACE = "Mobile";
        String URL = WDSLPath;

        boolean result = false;
        final String SOAP_ACTION = "Mobile/MobilePortType/GetStatusRequest";
        final String METHOD_NAME = "GetTask";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("GUIDTask", FTA.getTaskGuid());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.implicitTypes = true;
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        System.out.println(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.debug = true;

        ArrayList headerProperty = new ArrayList();
        headerProperty.add(new HeaderProperty("Authorization", "Basic " +
                org.kobjects.base64.Base64.encode((FTA.getUser() + ":" + FTA.getPassword()).getBytes())));


        try {
            androidHttpTransport.call(SOAP_ACTION, envelope, headerProperty);
            Log.d("dump Request: ", androidHttpTransport.requestDump);
            Log.d("dump response: ", androidHttpTransport.responseDump);
            SoapObject response = (SoapObject) envelope.getResponse();
            System.out.println("response" + response);

            if (response.getProperty("List").toString().length() > 0) {
                taskMembers = RetrieveFromSoap(response);
                result = true;
            }

        } catch (SocketException ex) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception e) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + e.getMessage());
            e.printStackTrace();
        }
        return result;

    }

    private boolean doNewTask() {

        String NAMESPACE = "Mobile";
        String NAMESPACE1 = "http://www.asiawaters.com/wsdl/taskDealer";
        String URL = WDSLPath;

        boolean result = false;
        final String SOAP_ACTION = "Mobile/MobilePortType/GetStatusRequest";
        final String METHOD_NAME = "TaskDealer";
        final String METHOD_NAME_M = "Object";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapObject requestObj = new SoapObject(NAMESPACE, METHOD_NAME_M);
        requestObj.addProperty(NAMESPACE1, "IDTradePoint", FTA.getMNT().getIDTradePoint());
        requestObj.addProperty(NAMESPACE1, "Comment", FTA.getMNT().getComment());
        requestObj.addProperty(NAMESPACE1, "IDAuthor", FTA.getMNT().getIDAuthor());
        requestObj.addProperty(NAMESPACE1, "TheTermOfTheTask", FTA.getMNT().getTheTermOfTheTask());
        request.addSoapObject(requestObj);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.implicitTypes = true;
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        System.out.println(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.debug = true;

        ArrayList headerProperty = new ArrayList();
        headerProperty.add(new HeaderProperty("Authorization", "Basic " +
                org.kobjects.base64.Base64.encode((FTA.getUser() + ":" + FTA.getPassword()).getBytes())));


        try {
            androidHttpTransport.call(SOAP_ACTION, envelope, headerProperty);
            Log.d("dump Request: ", androidHttpTransport.requestDump);
            Log.d("dump response: ", androidHttpTransport.responseDump);
            String response = envelope.getResponse().toString();
            System.out.println("response" + response);
            if (response.length() > 0) {
                result = true;
            }
        } catch (SocketException ex) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception e) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + e.getMessage());
            e.printStackTrace();
        }
        return result;

    }


    public Model_TaskMember RetrieveFromSoap(SoapObject soap) {
        int ii = 0;
        int i0 = 0;
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Model_TaskMember[] tms = new Model_TaskMember[soap.getPropertyCount()];
        Model_TaskMember taskMembers = new Model_TaskMember();
        String Vle = "";
        for (int i = 0; i < tms.length; i++) {
            switch (soap.getPropertyInfo(i).getName()) {
                case "List":
                    ii++;
                    break;
                case "Events":
                    i0++;
                    break;
            }
        }
        Model_TaskListFields[] tfls = new Model_TaskListFields[ii];
        items = new String[i0];
        ii = 0;
        i0 = 0;

        for (int i = 0; i < tms.length; i++) {
            switch (soap.getPropertyInfo(i).getName()) {
                case "DateOfExecutionPlan":
                    try {
                        taskMembers.setDateOfExecutionPlan(date_format.parse(soap.getProperty(i).toString()));
                    } catch (ParseException pr) {
                    }
                    break;
                case "DateOfExecutionFact":
                    try {
                        taskMembers.setDateOfExecutionFact(date_format.parse(soap.getProperty(i).toString()));
                    } catch (ParseException pr) {
                    }
                    break;
                case "DateOfCommencementPlan":
                    try {
                        taskMembers.setDateOfCommencementPlan(date_format.parse(soap.getProperty(i).toString()));
                    } catch (ParseException pr) {
                    }
                    break;
                case "DateOfCommencementFact":
                    try {
                        taskMembers.setDateOfCommencementFact(date_format.parse(soap.getProperty(i).toString()));
                    } catch (ParseException pr) {
                    }
                    break;
                case "InitiatorBP":
                    Vle = soap.getProperty(i).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    taskMembers.setInitiatorBP(Vle);
                    break;
                case "mComment":
                    Vle = soap.getProperty(i).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    taskMembers.setmComment(Vle);
                    break;
                case "Director":
                    Vle = soap.getProperty(i).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    taskMembers.setDirector(Vle);
                    break;
                case "Event":
                    Vle = soap.getProperty(i).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    taskMembers.setEvent(Vle);
                    break;
                case "StateTask":
                    Vle = soap.getProperty(i).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    taskMembers.setStateTask(Vle);
                    break;
                case "Status":
                    Vle = soap.getProperty(i).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    taskMembers.setStatus(Vle);
                    break;
                case "Events":
                    SoapObject l0 = (SoapObject) (soap.getProperty(i));
                    items[i0] = l0.getProperty(0).toString();
                    i0++;
                    break;
                case "List":
                    SoapObject l2 = (SoapObject) (soap.getProperty(i));
                    tfls[ii] = new Model_TaskListFields();
                    tfls[ii].setKey(l2.getProperty(0).toString());
                    Vle = l2.getProperty(1).toString();
                    if (Vle.equals("anyType{}")) Vle = "";
                    tfls[ii].setValue(Vle);
                    ii++;
                    break;
            }
        }
        taskMembers.setmTaskListFields(tfls);
        return taskMembers;
    }

    public void showDatePickerDialog(View v) {
        newFragment = new DatePickerFragment();
        clickedView = v;
        EditText et = (EditText) findViewById(R.id.deadline_field);
        Date dte = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        if (et.getText().length() > 0) {
            try {
                dte = sdf.parse(et.getText().toString());
            } catch (ParseException ex) {
            }

        }
        switch (clickedView.getId()) {
            case R.id.dtaPikerF:
                newFragment.setInitialDate(dte);
                newFragment.setmActivity(this);
                break;
        }

        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void setUILock(boolean lock) {
        if (lock) {
            ((ImageView) findViewById(R.id.dtaPikerF)).setEnabled(false);
        } else {
            ((ImageView) findViewById(R.id.dtaPikerF)).setEnabled(true);
            ((EditText) findViewById(R.id.task_description)).setFocusable(true);
            ((EditText) findViewById(R.id.task_description)).setClickable(true);
            ((EditText) findViewById(R.id.task_description)).setCursorVisible(true);
            ((EditText) findViewById(R.id.task_description)).setFocusableInTouchMode(true);
        }
    }

    public void fillCommoninfo() {

        EditText et;
        SimpleDateFormat date_format = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat initial_date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        et = (EditText) findViewById(R.id.outlet);
        et.setText(getValueByKey("Торговая точка"));

        et = (EditText) findViewById(R.id.agent);
        et.setText(getValueByKey("Торговый агент").toString());

        et = (EditText) findViewById(R.id.deadline_field);
        String fnl_vle = "";
        Date dte;
        try {
            dte = initial_date_format.parse(getValueByKey("Срок выполнения задачи").toString());
            fnl_vle = date_format.format(dte);
        } catch (ParseException ex) {
        }
        et.setText(fnl_vle);

        et = (EditText) findViewById(R.id.task_description);
        et.setText(getValueByKey("Текст задачи").toString());

        et = (EditText) findViewById(R.id.status);
        et.setText(taskMembers.getStateTask());

        et = (EditText) findViewById(R.id.AgentComments);
        et.setText(getValueByKey("Комментарий").toString());

        et = (EditText) findViewById(R.id.AgentComments);
        et.setText(getValueByKey("Комментарий").toString());

        et = (EditText) findViewById(R.id.initialdate);
        et.setText(date_format.format(taskMembers.getDateOfCommencementFact()));

    }

    private class LoginTask extends AsyncTask<Void, Void, Void> {

        private final ProgressDialog dialog = new ProgressDialog(
                FormActivity.this);

        protected void onPreExecute() {

            this.dialog.setMessage(getBaseContext().getResources().getString(R.string.GetingTask));
            this.dialog.show();

        }


        protected Void doInBackground(final Void... unused) {

            boolean auth = doLogin();
            System.out.println(auth);

            return null;// don't interact with the ui!
        }


        protected void onPostExecute(Void result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                if (taskMembers != null) {
                    fillCommoninfo();
                }
            }
        }

    }

    private class CreateTask extends AsyncTask<Void, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(
                FormActivity.this);

        protected void onPreExecute() {

            this.dialog.setMessage(getBaseContext().getResources().getString(R.string.GetingTask));
            this.dialog.show();

        }


        protected Boolean doInBackground(final Void... unused) {

            boolean auth = doNewTask();

            return auth;
        }


        protected void onPostExecute(Void result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
                Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.TasksSentOk), Toast.LENGTH_SHORT).show();
            }
        }

    }
}

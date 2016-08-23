package com.asiawaters.fta;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.asiawaters.fta.classes.Model_ListMembers;
import com.asiawaters.fta.classes.Model_Person;
import com.asiawaters.fta.classes.TaskListAdapter;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Model_Person mp;
    private Model_ListMembers[] lst;
    private ListAdapter listAdapter;
    private ListView expListView;
    private ArrayList<Model_ListMembers> listData;
    private FTA FTA;
    private AsyncTask AT;
    private android.os.Handler TimerHandler = new android.os.Handler();
    private boolean TaskIsRunning;
    private String WDSLPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FTA = ((com.asiawaters.fta.FTA) getApplication());

        //prepare tool bar
        mp = FTA.getPerson();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeApp();
            }
        });

        WDSLPath = FTA.getPath_url();
        lst = FTA.getList_values();

        // get the listview
        expListView = (ListView) findViewById(R.id.initial_list);
        if (lst == null) {
            AT = new LoginTask().execute();
        } else {
            if (FTA.getUpdateList()) {
                AT = new LoginTask().execute();
            } else {
                FTA.setUpdateList(false);
                runUpdateView();
            }
        }
        TimerHandler.postDelayed(TimerResult, 300000);
    }

    public void searchVoid(View v) {
        //Hide Keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        if (listAdapter != null) {
            EditText set = (EditText) findViewById(R.id.search);
//            if (set.getText().toString().length() > 0)
//              //  listAdapter.getFilter().filter(set.getText().toString());
//            else listAdapter.getFilter().filter("");
        }
    }

    public void allShow(View v) {
        v.setSelected(true);
        findViewById(R.id.IV3).setSelected(false);
        if (lst != null) {
//            prepareListData();
//            listAdapter = new FAExpandableListAdapter(getBaseContext(), listDataHeader, listDataChild);
//            // setting list adapter
//            expListView.setAdapter(listAdapter);
//            expListView.setOnScrollListener((AbsListView.OnScrollListener) listAdapter);
        }

    }

    public void actionNeed(View v) {
        v.setSelected(true);
        findViewById(R.id.IV2).setSelected(false);
        if (lst != null) {
            prepareListData_();
            listAdapter = new TaskListAdapter(listData, getBaseContext(), this);
            // setting list adapter
            expListView.setAdapter(listAdapter);
        }

    }

    public void new_task(View v) {
        this.finish();
        Intent intent;
        intent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(intent);
    }

    public void prepareListData_() {
        if (lst == null) return;
        listData = new ArrayList<>();
        for (int i = 0; i < lst.length; i++) {
            listData.add(lst[i]);
        }

    }

    public void startLogingActivity() {

        this.finish();
        Intent intent;
        intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void runUpdateView() {
        FTA.setList_values(lst);
        actionNeed(findViewById(R.id.IV3));
    }

    public void GetNextStep(int groupPosition, int position) {

        //String GUIDObject = listDataChild.get(listDataHeader.get(groupPosition)).get(position);
        //  FTA.setListMembers(findMember(GUIDObject));
        startNextActivity();
    }

    private Model_ListMembers findMember(String codeIsIn) {
        for (Model_ListMembers Model : lst) {
            if (Model.getIDTask().equals(codeIsIn)) {
                return Model;
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_desc) {
            sortList(1);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_asc) {
            sortList(-1);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void closeApp() {
        if (lst != null) {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else startLogingActivity();
    }

    public void sortList(int order) {
        if (listAdapter != null) {
            //listDataChild = sortMap(listDataChild, order);
//            listAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onBackPressed() {
        closeApp();
    }

    public void startNextActivity() {
        this.finish();
        Intent intent = new Intent(getApplicationContext(), FormActivity.class);
        startActivity(intent);
    }

    private boolean doLogin(String user_id, String password, String guid) {

        String NAMESPACE = "Mobile";
        String URL = WDSLPath;
        int timeout = FTA.getTimeOut();

        boolean result = false;
        final String SOAP_ACTION = "Mobile/MobilePortType/GetStatusRequest";
        final String METHOD_NAME = "SpisokZadachTAPoPostanovshiku";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("GuidUser", guid);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        request.addProperty("BeginDate", sdf.format(FTA.getDateTo()));
        request.addProperty("EndDate", sdf.format(FTA.getDateFrom()));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.implicitTypes = true;
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        System.out.println(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, timeout);
        androidHttpTransport.debug = true;


        ArrayList headerProperty = new ArrayList();
        headerProperty.add(new HeaderProperty("Authorization", "Basic " +
                org.kobjects.base64.Base64.encode((FTA.getUser() + ":" + FTA.getPassword()).getBytes())));


        try {
            androidHttpTransport.call(SOAP_ACTION, envelope, headerProperty);
            Log.d("dump Request: ", androidHttpTransport.requestDump);
            Log.d("dump response: ", androidHttpTransport.responseDump);
            SoapObject response = (SoapObject) envelope.getResponse();
            Log.i("myApp", response.toString());
            System.out.println("response" + response);
            if (response.getPropertyCount() > 0) {
                if (response.getProperty("Line").toString().length() > 0) {
                    Model_ListMembers[] lms = RetrieveFromSoap(response);
                    if (lms != null) {
                        lst = lms;
                        result = true;
                    } else return false;

                } else return false;
            } else result = true;

        } catch (SocketException ex) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception e) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + e.getMessage());
            e.printStackTrace();
        }
        return result;

    }

    public static Model_ListMembers[] RetrieveFromSoap(SoapObject soap) {
        Model_ListMembers[] lms = null;
        if (soap.getPropertyCount() > 0) {
            lms = new Model_ListMembers[soap.getPropertyCount()];
            for (int i = 0; i < lms.length; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);
                Model_ListMembers listMembers = new Model_ListMembers();
                //Название точки
                listMembers.setOutletName(pii.getProperty("TT").toString());
                //Описание задачи
                listMembers.setTextProblem(pii.getProperty("TextProblem").toString());
                //Ид задачи
                listMembers.setIDTask(pii.getProperty("IDTask").toString());

                listMembers.setOutletAgent(pii.getProperty("Agent").toString());
                //Время когда завершить
                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                try {
                    listMembers.setAppointmentDateOfTask(date_format.parse(pii.getProperty("StagingDate").toString()));
                    listMembers.setDeadline(date_format.parse(pii.getProperty("Deadline").toString()));
                } catch (ParseException pr) {
                }
                listMembers.setOutletAddress(pii.getProperty("Address").toString());
                listMembers.setDone(Boolean.valueOf(pii.getProperty("done").toString()));
                lms[i] = listMembers;
            }
        }

        return lms;
    }

    private class LoginTask extends AsyncTask<Void, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(
                MainActivity.this);


        protected void onPreExecute() {
//            this.dialog.setMessage(getBaseContext().getResources().getString(R.string.Updating));
//            this.dialog.show();
//            this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    AT.cancel(true);
//                    closeApp();
//                }
//            });
        }


        protected Boolean doInBackground(final Void... unused) {

            boolean auth = doLogin("", "", mp.getPerson_guid());
            System.out.println(auth);

            return auth;
        }


        protected void onPostExecute(Boolean result) {


            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (!result) {
                Toast.makeText(getBaseContext(), R.string.timeout, Toast.LENGTH_SHORT).show();
                startLogingActivity();
            } else if (lst != null) {
                FTA.setUpdateList(false);
                runUpdateView();
                sortList(-1);
            }
        }

    }

    private Runnable TimerResult = new Runnable() {
        @Override
        public void run() {
            TaskIsRunning = true;

            AT = new LoginTask().execute();
            TimerHandler.postDelayed(this, 300000);
        }
    };
}

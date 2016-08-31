package com.asiawaters.fta;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asiawaters.fta.classes.ModelRegions;
import com.asiawaters.fta.classes.ModelSearchOutletRequest;
import com.asiawaters.fta.classes.ModelSearchedOutlets;
import com.asiawaters.fta.classes.Model_ListMembers;
import com.asiawaters.fta.classes.Model_TaskListFields;
import com.asiawaters.fta.classes.Model_TaskMember;
import com.asiawaters.fta.classes.OutletListAdapter;
import com.asiawaters.fta.classes.TaskListAdapter;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class SearchActivity extends AppCompatActivity {

    private FTA FTA;
    private String WDSLPath;
    private Spinner spnr;
    private ModelRegions[] ModelRegionsArr;
    private ModelSearchedOutlets[] ModelSearchedOutletsArr;
    private AutoCompleteTextView actv;
    private ImageView IV1;
    private long millisecond = 0;
    private ListAdapter listAdapter;
    private ListView expListView;
    private ArrayList<ModelSearchedOutlets> listData;
    private Handler TimerHandler = new Handler();
    private String[] InputValues = {"", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        FTA = ((com.asiawaters.fta.FTA) getApplication());

        //prepare tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        WDSLPath = FTA.getPath_url();
        spnr = (Spinner) findViewById(R.id.spinner);

        //disable search elements at start
        actv = (AutoCompleteTextView) findViewById(R.id.search_search);
        // specify the minimum type of characters before drop-down list is shown
        //needs 10 for cancel automotive show
        actv.setThreshold(250);
        IV1 = (ImageView) findViewById(R.id.IV1M);
        actv.setEnabled(false);
        IV1.setEnabled(false);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ModelSearchedOutlets MSO = (ModelSearchedOutlets) adapterView.getItemAtPosition(position);
                actv.setText(MSO.getName());
                ((TextView) findViewById(R.id.search_search_address)).setText(MSO.getAddress());
                ((TextView) findViewById(R.id.search_search_agent)).setText(MSO.getAgentName());


                prepareListData_(MSO);
                SetAdapterForListOutlets();
            }
        });

        expListView = (ListView) findViewById(R.id.search_list);

        actv.addTextChangedListener(new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                InputValues[1] = s.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                InputValues[0] = s.toString();
                Calendar c = Calendar.getInstance();
                millisecond = c.getTimeInMillis();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        if (FTA.getModelRegionsArray() != null) {
            ModelRegionsArr = FTA.getModelRegionsArray();
            SetAdapterForRegions();
            if (FTA.getRegionId() == -1) {
                FTA.setRegionId(0);
            }else spnr.setSelection(Integer.parseInt(Long.toString(FTA.getRegionId())));
        } else {
            new UpdateRegionTask().execute();
        }
        spnr.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        FTA.setRegionId(arg0.getSelectedItemId());
                        actv.setEnabled(true);
                        IV1.setEnabled(true);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        actv.setEnabled(false);
                        IV1.setEnabled(false);
                    }

                }
        );

        TimerHandler.postDelayed(TimerResult, 1500);
    }

    @Override
    public void onBackPressed() {
        previousActivity();
    }

    public String getGuidofRegion(String value) {
        String return_value = "";
        for (ModelRegions mr : ModelRegionsArr) {
            if (mr.getName().equals(value)) {
                return_value = mr.getGUID();
                break;
            }
        }
        return return_value;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_desc_search) {
            sortList(1);
            return true;
        }
        if (id == R.id.menu_asc_search) {
            sortList(-1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sortList(int order) {
        if (listAdapter != null) {
            Collections.sort(listData, new TaskNameComparator(order));
            listAdapter = new OutletListAdapter(listData, getBaseContext(), this);
            expListView.setAdapter(listAdapter);
        }
    }

    public class TaskNameComparator implements Comparator<ModelSearchedOutlets> {
        private int mOrder;

        public TaskNameComparator(int order) {
            mOrder = order;
        }

        public int compare(ModelSearchedOutlets fst1, ModelSearchedOutlets scnd2) {
            if (mOrder < 0) {
                return fst1.getName().compareTo(scnd2.getName());
            } else return scnd2.getName().compareTo(fst1.getName());
        }
    }

    public void search_void_Outlets(View v) {
        if (actv.getText().length() > 0) {
            InputValues[0] = InputValues[1];
            ModelSearchOutletRequest MSOR = new ModelSearchOutletRequest();
            MSOR.setCount("0");
            MSOR.setWord("%" + actv.getText().toString() + "%");
            String guid = getGuidofRegion(((ModelRegions) spnr.getSelectedItem()).getName());
            MSOR.setGUIDOrganization(guid);
            MSOR.setListCode(2);
            new GetAutoSuggestionsTask().execute(MSOR);
        } else {

        }
    }

    public void previousActivity() {
        this.finish();
        Intent intent;
        intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public static ModelRegions[] RetrieveFromSoapRegions(SoapObject soap) {
        ModelRegions[] lms = null;
        if (soap.getPropertyCount() > 0) {
            lms = new ModelRegions[soap.getPropertyCount()];
            for (int i = 0; i < lms.length; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);
                ModelRegions MR = new ModelRegions();
                //Guid региона
                MR.setGUID(pii.getProperty("GUID").toString());
                //Имя Региона
                MR.setName(pii.getProperty("Name").toString());
                lms[i] = MR;
            }
        }

        return lms;
    }

    public static ModelSearchedOutlets[] RetrieveFromSoapOutlets(SoapObject soap) {
        ModelSearchedOutlets[] lms = null;
        if (soap.getPropertyCount() > 0) {
            lms = new ModelSearchedOutlets[soap.getPropertyCount()];
            for (int i = 0; i < lms.length; i++) {
                SoapObject pii = (SoapObject) soap.getProperty(i);
                ModelSearchedOutlets MO = new ModelSearchedOutlets();
                //Guid точки
                MO.setGUID(pii.getProperty("GUID").toString());
                //Имя точки
                if (pii.getProperty("Name").toString().equals("anyType{}")) MO.setName("");
                else MO.setName(pii.getProperty("Name").toString());
                //Адрес точки
                if (pii.getProperty("Address").toString().equals("anyType{}")) MO.setAddress("");
                else MO.setAddress(pii.getProperty("Address").toString());
                //Имя Агента
                if (pii.getProperty("Agent").toString().equals("anyType{}")) MO.setAgentName("");
                else MO.setAgentName(pii.getProperty("Agent").toString());
                lms[i] = MO;
            }
        }

        return lms;
    }

    private Integer doOutletListUpdate(ModelSearchOutletRequest MSR) {

        String NAMESPACE = "Mobile";
        String URL = WDSLPath;
        int timeout = FTA.getTimeOut();

        int result = -1;
        final String SOAP_ACTION = "Mobile/MobilePortType/GetStatusRequest";
        final String METHOD_NAME = "GetListOfOutlets";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("Word", MSR.getWord());
        request.addProperty("Count", MSR.getCount());
        request.addProperty("GUIDOrganization", MSR.getGUIDOrganization());

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
            if (response.getPropertyCount() > 0) {
                if (response.getProperty("List").toString().length() > 0) {
                    ModelSearchedOutlets[] Mo = RetrieveFromSoapOutlets(response);
                    if (Mo != null) {
                        ModelSearchedOutletsArr = Mo;
                        result = MSR.getListCode();
                    } else return -1;
                } else return -1;
            } else result = MSR.getListCode();

        } catch (SocketException ex) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception e) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + e.getMessage());
            e.printStackTrace();
        }
        return result;

    }

    private class UpdateRegionTask extends AsyncTask<Void, Void, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(
                SearchActivity.this);


        protected void onPreExecute() {
            this.dialog.setMessage(getBaseContext().getResources().getString(R.string.GetingRegions));
            this.dialog.setCancelable(false);
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }


        protected Boolean doInBackground(final Void... unused) {
            boolean auth = doRegionUpdate();
            return auth;
        }


        protected void onPostExecute(Boolean result) {


            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (!result) {
                Toast.makeText(getBaseContext(), R.string.timeout, Toast.LENGTH_SHORT).show();
            } else {
                //Загрузили полученные регионы в общую переменную
                FTA.setModelRegionsArray(ModelRegionsArr);
                SetAdapterForRegions();
            }
        }

    }

    public void SetAdapterForRegions() {
        ArrayAdapter<ModelRegions> adapter = new ArrayAdapter<ModelRegions>(
                this, android.R.layout.simple_spinner_item, ModelRegionsArr);
        spnr.setAdapter(adapter);
    }


    private boolean doRegionUpdate() {

        String NAMESPACE = "Mobile";
        String URL = WDSLPath;
        int timeout = FTA.getTimeOut();

        boolean result = false;
        final String SOAP_ACTION = "Mobile/MobilePortType/GetStatusRequest";
        final String METHOD_NAME = "GetRegions";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

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
            if (response.getPropertyCount() > 0) {
                if (response.getProperty("List").toString().length() > 0) {
                    ModelRegions[] ModelRegionsA = RetrieveFromSoapRegions(response);
                    if (ModelRegionsA != null) {
                        ModelRegionsArr = ModelRegionsA;
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


    private class GetAutoSuggestionsTask extends AsyncTask<ModelSearchOutletRequest, Void, Integer> {

        private final ProgressDialog dialog = new ProgressDialog(
                SearchActivity.this);


        protected void onPreExecute() {
            this.dialog.setMessage(getBaseContext().getResources().getString(R.string.GetingOutlets));
            this.dialog.setCancelable(false);
            this.dialog.setCanceledOnTouchOutside(false);
            this.dialog.show();
        }

        protected Integer doInBackground(ModelSearchOutletRequest... passing) {
            ModelSearchOutletRequest MSR = passing[0];
            int auth = doOutletListUpdate(MSR);
            return auth;
        }


        protected void onPostExecute(Integer result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (result < 0) {
                Toast.makeText(getBaseContext(), R.string.timeout, Toast.LENGTH_SHORT).show();
            } else {
                //Hide Keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(actv.getWindowToken(), 0);
                if (result == 1) SetAdapterForAutoCompliteOutlets();
                if (result == 2) SetAdapterForListOutlets();
            }
        }

    }

    public void SetAdapterForAutoCompliteOutlets() {
        if (ModelSearchedOutletsArr!=null) {
            ArrayAdapter<ModelSearchedOutlets> adapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ModelSearchedOutletsArr);
            actv.setAdapter(adapter);
            actv.showDropDown();
            Calendar c = Calendar.getInstance();
            millisecond = c.getTimeInMillis() + 1200;
        }
    }



    public void SetAdapterForListOutlets() {
        if (ModelSearchedOutletsArr == null) return;
        prepareListData_();
        listAdapter = new OutletListAdapter(listData, getBaseContext(), this);
        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    public void prepareListData_() {
        if (ModelSearchedOutletsArr == null) return;
        listData = new ArrayList<>();
        for (int i = 0; i < ModelSearchedOutletsArr.length; i++) {
            listData.add(ModelSearchedOutletsArr[i]);
        }
    }

    public void prepareListData_(ModelSearchedOutlets mso) {
        ModelSearchedOutletsArr = new ModelSearchedOutlets[1];
        ModelSearchedOutletsArr[0] = mso;
        InputValues[0] = InputValues[1];
    }

    public void startNextActivity() {
        this.finish();
        Intent intent = new Intent(getApplicationContext(), FormActivity.class);
        startActivity(intent);
    }

    private Runnable TimerResult = new Runnable() {
        @Override
        public void run() {
            Calendar c = Calendar.getInstance();
            long millisecond_updated = c.getTimeInMillis();
            if (!InputValues[1].equals(InputValues[0])) {
                if (millisecond_updated > (millisecond + 1500)) {
                    ModelSearchOutletRequest MSOR = new ModelSearchOutletRequest();
                    MSOR.setCount("10");
                    MSOR.setWord("%" + actv.getText().toString() + "%");
                    String guid = getGuidofRegion(((ModelRegions) spnr.getSelectedItem()).getName());
                    MSOR.setGUIDOrganization(guid);
                    MSOR.setListCode(1);
                    new GetAutoSuggestionsTask().execute(MSOR);
                    millisecond = millisecond_updated;
                    InputValues[0] = InputValues[1];
                }
            } else millisecond = millisecond_updated;

            TimerHandler.postDelayed(this, 1000);
        }
    };
}

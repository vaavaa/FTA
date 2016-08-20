package com.asiawaters.fta;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.asiawaters.fta.classes.OutletListAdapter;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
        IV1 = (ImageView) findViewById(R.id.IV1);
        actv.setEnabled(false);
        IV1.setEnabled(false);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ModelSearchedOutlets MSO = (ModelSearchedOutlets) adapterView.getItemAtPosition(position);
                actv.setText(MSO.getName());
                ((TextView)findViewById(R.id.search_search_address)).setText(MSO.getAddress());
            }
        });

        expListView = (ListView) findViewById(R.id.search_list);

        actv.addTextChangedListener(new TextWatcher() {

            private boolean shouldAutoComplete = true;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Определяем когда запускать обновления списка подсказок
                Calendar c = Calendar.getInstance();
                c.get(Calendar.DATE);
                long millisecond_now = c.getTimeInMillis();
                if ((millisecond + 2500) < millisecond_now) {
                    millisecond = c.getTimeInMillis();
                    shouldAutoComplete = true;
                } else shouldAutoComplete = false;


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Calendar c = Calendar.getInstance();
                if (millisecond==0) millisecond = c.getTimeInMillis();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (shouldAutoComplete) {
                    ModelSearchOutletRequest MSOR = new ModelSearchOutletRequest();
                    MSOR.setCount("10");
                    MSOR.setWord("%" + actv.getText().toString() + "%");
                    String guid = getGuidofRegion(((ModelRegions) spnr.getSelectedItem()).getName());
                    MSOR.setGUIDOrganization(guid);
                    MSOR.setListCode(1);
                    new GetAutoSuggestionsTask().execute(MSOR);
                }
            }

        });

        if (FTA.getModelRegionsArray() != null) {
            ModelRegionsArr = FTA.getModelRegionsArray();
            SetAdapterForRegions();
        } else {
            new UpdateRegionTask().execute();
        }
        spnr.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
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


    public void search_void_Outlets(View v) {
        if (actv.getText().length() > 0) {
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
                MO.setName(pii.getProperty("Name").toString());
                //Адресс точки
                MO.setAddress(pii.getProperty("Address").toString());
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
            this.dialog.setMessage(getBaseContext().getResources().getString(R.string.Updating));
            this.dialog.show();
            this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //In case of progress cancel we exit from activity
                    previousActivity();
                }
            });


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
            this.dialog.show();
            this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //In case of progress cancel we exit from activity
                    actv.setText("");
                }
            });


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
        ArrayAdapter<ModelSearchedOutlets> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ModelSearchedOutletsArr);
        actv.setAdapter(adapter);
        actv.showDropDown();
        Calendar c = Calendar.getInstance();
        millisecond = c.getTimeInMillis() + 1500;
    }

    public void SetAdapterForListOutlets() {
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

    public void startNextActivity() {
    }
}

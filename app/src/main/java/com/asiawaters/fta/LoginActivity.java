package com.asiawaters.fta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asiawaters.fta.classes.DBController;
import com.asiawaters.fta.classes.DatePickerFragment;
import com.asiawaters.fta.classes.Model_NetState;
import com.asiawaters.fta.classes.Model_Person;
import com.asiawaters.fta.classes.NetListener;
import com.asiawaters.fta.classes.m_settings;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

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
import java.util.Date;


public class LoginActivity extends AppCompatActivity {

    private Model_Person person = null;

    // UI references.
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private Model_NetState model_netState;
    private NetListener mnetListener;
    private String WDSLPath;
    private DatePickerFragment newFragment;
    private View clickedView;

    private ExpandableRelativeLayout mExpandLayout;

    // Database Helper
    private DBController db;
    private com.asiawaters.fta.FTA FA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FA = ((com.asiawaters.fta.FTA) getApplication());

        db = new DBController(this);
        db.openDB();
        FA.setDb(db);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    new LoginTask().execute();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et;
                et = (EditText) findViewById(R.id.URLPath);
                if (et.getText() == null) {
                    Toast.makeText(LoginActivity.this, R.string.login, Toast.LENGTH_SHORT).show();
                    return;
                }
                m_settings mset = new m_settings();
                mset.setSettingsName("WSDL_path");
                mset.setSettingsValue(et.getText().toString());
                db.createNewSettings(mset);
                FA.setPath_url(et.getText().toString());
                // Set up the login form.
                WDSLPath = FA.getPath_url();

                if ((((TextView) findViewById(R.id.email)).getText() != null)) {
                    if (isLoginValid(((TextView) findViewById(R.id.email)).getText().toString())) {
                        if ((((TextView) findViewById(R.id.password)).getText() != null)) {
                            if (isPasswordValid(((TextView) findViewById(R.id.password)).getText().toString())) {
                                FA.setUser(((TextView) findViewById(R.id.email)).getText().toString());
                                FA.setPassword(((TextView) findViewById(R.id.password)).getText().toString());
                                //Hide Keyboard
                                InputMethodManager imm = (InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                new LoginTask().execute();
                            } else
                                Toast.makeText(LoginActivity.this, R.string.password, Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(LoginActivity.this, R.string.password, Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(LoginActivity.this, R.string.login, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(LoginActivity.this, R.string.login, Toast.LENGTH_SHORT).show();
            }
        });

        String[] credentials = db.getCredentials();

        if (credentials[0] != null) {
            if (credentials[0].length() > 0) {
                ((TextView) findViewById(R.id.email)).setText(credentials[0].toString());
                ((TextView) findViewById(R.id.password)).setText(credentials[1].toString());
            }
        }

        mExpandLayout
                = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout);

        Button mSettingsButton = (Button) findViewById(R.id.showSettings);
        mSettingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide Keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (!mExpandLayout.isExpanded()) mExpandLayout.expand();
                else mExpandLayout.collapse();
            }
        });

        model_netState = FA.getModel_netState();
        mnetListener = FA.getMnetListener();

        EditText et;
        et = (EditText) findViewById(R.id.URLPath);
        if (db.getSettings("WSDL_path")!=null) {
            et.setText(db.getSettings("WSDL_path").getSettingsValue());
            FA.setPath_url(db.getSettings("WSDL_path").getSettingsValue());
        }else et.setText(FA.getPath_url());

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, +1);
        FA.setDateFrom(new Date(c.getTimeInMillis()));
        et = (EditText) findViewById(R.id.DataFrom);
        et.setText(sdf.format(FA.getDateFrom()));

        EditText et1;
        c.add(Calendar.DATE, -51);
        FA.setDateTo(new Date(c.getTimeInMillis()));
        et1 = (EditText) findViewById(R.id.DateTo);
        et1.setText(sdf.format(FA.getDateTo()));
        RunStatListener();
    }


    public void startNextActivity() {
        this.finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void runDatePickerCompliting() {
        Date Result = newFragment.getResultDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        EditText et = (EditText) findViewById(R.id.DataFrom);
        switch (clickedView.getId()) {
            case R.id.dtaPiker:
                et = (EditText) findViewById(R.id.DataFrom);
                break;
            case R.id.dtaPiker1:
                et = (EditText) findViewById(R.id.DateTo);
                break;
        }
        et.setText(sdf.format(Result));
        IsDateCorrect();
    }

    public boolean IsDateCorrect() {
        boolean result = false;
        Date from;
        Date to;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        EditText et = (EditText) findViewById(R.id.DataFrom);
        if (et.getText().length() > 0) {
            try {
                from = sdf.parse(et.getText().toString());
                FA.setDateFrom(from);
                et = (EditText) findViewById(R.id.DateTo);
                if (et.getText().length() > 0) {
                    try {
                        to = sdf.parse(et.getText().toString());
                        FA.setDateTo(to);
                        findViewById(R.id.email_sign_in_button).setEnabled(true);
                    } catch (ParseException ex) {
                        findViewById(R.id.email_sign_in_button).setEnabled(false);
                        Toast.makeText(LoginActivity.this, R.string.WrongDate, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (ParseException ex) {
                findViewById(R.id.email_sign_in_button).setEnabled(false);
                Toast.makeText(LoginActivity.this, R.string.WrongDate, Toast.LENGTH_SHORT).show();
            }
        }
        return result;
    }

    public void showDatePickerDialog(View v) {
        newFragment = new DatePickerFragment();
        clickedView = v;
        switch (clickedView.getId()) {
            case R.id.dtaPiker:
                newFragment.setInitialDate(FA.getDateFrom());
                break;
            case R.id.dtaPiker1:
                newFragment.setInitialDate(FA.getDateTo());
                break;
        }

        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void CheckState() {
        TextView textView = (TextView) findViewById(R.id.HelpText);

        if (mnetListener.get_isOnline()) {
            mEmailSignInButton.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.welcome_box_yellow));
            if (mnetListener.get_isConnected()) {
                mEmailSignInButton.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.welcome_box_green));
                textView.setText("");
            } else {
                textView.setText(R.string.Internet_is_not_available);
            }
        } else {
            mEmailSignInButton.setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.welcome_box_red));
            textView.setText(R.string.Connection_is_switched_off);
        }
    }


    private boolean isLoginValid(String email) {
        return email.length() > 0;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    private String getStringResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return getString(resId);
    }

    public void RunStatListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new CheckTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new CheckTask().execute();
        }
    }

    private String doLogin() {
        String NAMESPACE = "Mobile";
        int timeout = FA.getTimeOut();

        String result = "false";
        final String SOAP_ACTION = "Mobile";
        final String METHOD_NAME = "Autorization";
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("Login", ((TextView) findViewById(R.id.email)).getText().toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.implicitTypes = true;
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        System.out.println(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(WDSLPath, timeout);
        androidHttpTransport.debug = true;

        ArrayList headerProperty = new ArrayList();
        headerProperty.add(new HeaderProperty("Authorization", "Basic " +
                org.kobjects.base64.Base64.encode((FA.getUser() + ":" + FA.getPassword()).getBytes())));


        try {
            androidHttpTransport.call(SOAP_ACTION, envelope, headerProperty);
            Log.d("dump Request: ", androidHttpTransport.requestDump);
            Log.d("dump response: ", androidHttpTransport.responseDump);
            SoapObject response = (SoapObject) envelope.getResponse();
            System.out.println("response" + response);

            if (response.getProperty("GUIDUser").toString().length() > 0) {
                //Заполнили данные о человеке
                Model_Person person = new Model_Person();
                person.setPerson_guid(response.getProperty("GUIDUser").toString());
                person.setPerson_name(response.getProperty("Name").toString());
                person.setPerson_surname(response.getProperty("Surname").toString());
                person.setPerson_midname(response.getProperty("MiddleName").toString());
                person.setPerson_organisation(response.getProperty("Organization").toString());
                person.setEmail(response.getProperty("email").toString());
                person.setPerson_position(response.getProperty("Position").toString());
                FA.setPerson(person);
                result = "true";
            }

        } catch (SocketException ex) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + ex.getMessage());
            ex.printStackTrace();
            result = ex.getMessage();
        } catch (Exception e) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + e.getMessage());
            e.printStackTrace();
            result = e.getMessage();
        }
            return result;
        }


        private class LoginTask extends AsyncTask<Void, Void, String> {

            private final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);

            protected void onPreExecute() {
                this.dialog.setMessage(getBaseContext().getResources().getString(R.string.LoggingIn));
                this.dialog.show();
            }

            protected String doInBackground(final Void... unused) {

                String auth = doLogin();
                return auth;// doesn't interact with the ui!
            }

            protected void onPostExecute(String result) {
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
                if (result != null) {
                    if (result.equals("true")) {
                        if (((CheckBox) findViewById(R.id.chSave)).isChecked())
                            db.setCredentials(((TextView) findViewById(R.id.email)).getText().toString(),
                                    ((TextView) findViewById(R.id.password)).getText().toString());
                        else db.setCredentials("", "");

                        startNextActivity();
                    } else {
                        if (result.indexOf("Пользователь не найден") > 0)
                            Toast.makeText(getBaseContext(), R.string.UserNotFind, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getBaseContext(), getStringResourceByName("ServerIsNotResponding") + " "+ result, Toast.LENGTH_SHORT).show();
                    }
                }

            }

        }

        private class CheckTask extends AsyncTask<Void, Void, Boolean> {

            protected void onPreExecute() {
            }

            protected void onProgressUpdate(Void... progress) {
                CheckState();
            }

            protected Boolean doInBackground(final Void... unused) {
                do {
                    try {
                        Thread.sleep(2000);
                        publishProgress();
                    } catch (InterruptedException Ex) {
                    }
                } while (!this.isCancelled());
                return true;
            }

            protected void onPostExecute() {
            }

        }
    }




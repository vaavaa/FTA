package com.asiawaters.fta;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;

import com.asiawaters.fta.classes.Model_NetState;
import com.asiawaters.fta.classes.Model_Person;
import com.asiawaters.fta.classes.NetListener;
import com.asiawaters.fta.classes.Network_Helper;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    private int mSplashTime = 3500;
    private Timer mTimer;
    private Model_NetState model_netState = new Model_NetState();
    private NetListener mnetListener = new NetListener();
    private Model_Person person = null;
    private FTA FTA;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        model_netState.setConnected(Network_Helper.isOnline(getBaseContext()));
        model_netState.setOnline(Network_Helper.isConnectedbyPing(""));
        model_netState.setContext(this);
        model_netState.setUrl("");

        FTA = ((com.asiawaters.fta.FTA) getApplication());

        mTimer = new Timer();
        mTimer.schedule(
                new TimerTask() {
                    public void run() {
                        stopSplash();
                    }
                },
                mSplashTime);
    }
    @Override
    public void onStart(){
        super.onStart();
        RunStatListener();
    }
    private void stopSplash() {
        FTA.setModel_netState(model_netState);
        FTA.setMnetListener(mnetListener);

        startMainActivity();
    }


    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            mTimer.cancel();
            stopSplash();
        }
        return true;
    }

    public void RunStatListener() {
        if (mnetListener.getStatus() != AsyncTask.Status.RUNNING) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mnetListener.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, model_netState, model_netState);
            } else {
                mnetListener.execute(model_netState, model_netState);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void startMainActivity() {
        this.finish();
        Intent intent ;
        if (FTA.getList_values() == null) intent = new Intent(getApplicationContext(), LoginActivity.class);
        else intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}

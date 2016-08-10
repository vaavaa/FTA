package com.asiawaters.fta.classes;

import android.os.AsyncTask;

public class NetListener extends AsyncTask<Model_NetState, Model_NetState, Boolean> {

    boolean isOnline = false;
    boolean isConnected = false;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Model_NetState... progress) {
        isOnline = progress[0].isOnline();
        isConnected = progress[0].isConnected();
    }

    protected void onPostExecute(String... result) {

    }

    //Выполняем бесконечный цикл опроса среды о подключении
    protected Boolean doInBackground(Model_NetState... parameter) {
        Model_NetState context_plus = parameter[0];
        do {
            context_plus.setConnected(Network_Helper.doCheck());
            context_plus.setOnline(Network_Helper.isOnline(context_plus.getContext()));

            publishProgress(context_plus);
            try {
                Thread.sleep(2500);
            } catch (InterruptedException Ex) {
            }

        } while (!this.isCancelled());
        return true;
    }


    public boolean get_isOnline() {
        return isOnline;
    }

    public boolean get_isConnected() {
        return isConnected;
    }

}

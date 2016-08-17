package com.asiawaters.fta.classes;


import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketException;
import java.util.ArrayList;

public class Network_Helper {
    private com.asiawaters.fta.FTA FTA;

    public static boolean isOnline(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static Boolean isConnectedbyPing(String host) {
        if (host.length() ==0) host="www.google.com";
        try {
            Process p1 = Runtime.getRuntime().exec("ping -c 1 "+host);
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean doCheck() {

        String NAMESPACE = "Mobile";

        Boolean result = false;
        final String SOAP_ACTION = "Mobile";
        final String METHOD_NAME = "GetStatus";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.implicitTypes = true;
        envelope.dotNet = false;

        envelope.setOutputSoapObject(request);
        System.out.println(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE("http://ws.asiawaters.com/ast2/ws/Mobile", 60000);
        androidHttpTransport.debug = true;

        ArrayList headerProperty = new ArrayList();
        headerProperty.add(new HeaderProperty("Authorization", "Basic " +
                org.kobjects.base64.Base64.encode(("ws_connection:kmwaqua").getBytes())));


        try {
            androidHttpTransport.call(SOAP_ACTION, envelope, headerProperty);
            String response = envelope.getResponse().toString();
            System.out.println("response" + response);
            if (response.equals("Working")) result = true;

        } catch (SocketException ex) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception e) {
            Log.e("Error : ", "Error on soapPrimitiveData() " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


}

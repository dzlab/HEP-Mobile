package com.orange.labs.hep.android.task;

import com.orange.labs.hep.android.common.Periods;
import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.lang.Thread;

public class PhoneMonitoringTask extends AsyncTask<Object, Object, Object> {

	public static final String TAG = PhoneMonitoringTask.class.getSimpleName();
	public static final long PERIOD= Periods.PHONE_MONITORING_PERIOD;
	private int oldCallState = -1;
	/**
	 * @param a TelephonyManager instance
	 * @return information about the user device: phone number & state (ringing, idle, offhook)
	 * */
	
	@Override
	protected Object doInBackground(Object... params) {
		// TODO Auto-generated method stub	
		Context ctxtApp = (Context)params[0];
		try {
    		while(true) {
    			
    			TelephonyManager phone = (TelephonyManager) ctxtApp.getSystemService(Context.TELEPHONY_SERVICE);
    			String phoneNumber = phone.getLine1Number();
    			Log.v(TAG, "Phone number: " + phoneNumber);
    	    	int newCallState = phone.getCallState();
    	    	if(oldCallState != newCallState) {
    	    		oldCallState = newCallState;
        	    	switch(newCallState) {
        	    	case TelephonyManager.CALL_STATE_IDLE:
        	    		Log.v(TAG, "Call state is IDLE");
        	    		break;
        	    	case TelephonyManager.CALL_STATE_OFFHOOK:
        	    		Log.v(TAG, "Call state is OFFHOOK");
        	    		break;
        	    	case TelephonyManager.CALL_STATE_RINGING:
        	    		Log.v(TAG, "Call state is RINGING");
        	    		break;
        	    	default:
        	    		Log.v(TAG, "Call state (" + newCallState + ") not recognized");
        	    	}
    	    	}
    	    	Thread.sleep(PERIOD);
    		}			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}			
		return null;
	}

	public int getCurrentPhoneState() {
		return oldCallState;
	}
}

package com.orange.labs.hep.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

	public static final String TAG = CallReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		Log.v(TAG, callState);		
	}

}

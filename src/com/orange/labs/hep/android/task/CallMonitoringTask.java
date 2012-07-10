package com.orange.labs.hep.android.task;

import com.orange.labs.hep.android.common.Periods;
import java.util.Date;
import java.lang.Thread;
import java.lang.InterruptedException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.content.Context;

public class CallMonitoringTask extends AsyncTask<Object, Object, Object> {
	
	public static final String TAG = CallMonitoringTask.class.getSimpleName();
	public static final long PERIOD = Periods.CALL_MONITORING_PERIOD;
	protected int nbIncomingCalls = 0;
	protected int nbOutgoingCalls = 0;
	protected int nbMissedCalls = 0;
	
	/**
	 * Method called when the task is executed
	 * It continuously check the state of calls with a frequency of {@link PERIOD}
	 * */
	@Override
	protected Object doInBackground(Object... params) {
		Context ctxtApp = (Context) params[0];
		Uri uriCallLogProvider = Uri.parse("content://call_log/calls");
		try {
			while(true) {
		        Cursor cursor = ctxtApp.getContentResolver().query(uriCallLogProvider, null, null, null, null);             
		        
		        if(cursor.moveToFirst()) {
		        	int newNbIncomingCalls = 0;
		        	int newNbOutgoingCalls = 0;
		        	int newNbMissedCalls = 0;
		        	do {
		        		String id = cursor.getString(cursor.getColumnIndex(Calls._ID));
		        		String num = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
		        		int type = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Calls.TYPE)));
		        		 
		        		Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Calls.DATE))));
		        		
		        		// how to detect rejected calls
		        		switch(type) {
		        		case Calls.INCOMING_TYPE:
		        			newNbIncomingCalls += 1;
		        			Log.v(TAG, date+": "+id+", "+num+" INCOMING");
		        			break;
		        		case Calls.OUTGOING_TYPE:
		        			newNbOutgoingCalls += 1;
		        			Log.v(TAG, date+": "+id+", "+num+" OUTGOING");
		        			break;
		        		case Calls.MISSED_TYPE:
		        			newNbMissedCalls += 1;
		        			Log.v(TAG, date+": "+id+", "+num+" MISSED");
		        			break;
		        		// what about rejected called how to find them?
		        		default:
		        			Log.v(TAG, date+": "+id+", "+num+": DEFAULT");
		        		}
		        	}while(cursor.moveToNext());
		        	nbIncomingCalls = newNbIncomingCalls;
		        	nbOutgoingCalls = newNbOutgoingCalls;
		        	nbMissedCalls = newNbMissedCalls;
		        }
		        cursor.close();
		        Thread.sleep(PERIOD);
			}
		}catch(InterruptedException ie) {
			ie.printStackTrace();
		}

		return null;
	}
	
	/**
	 * Get the number of incoming calls
	 * */
	public int getNbIncomingCalls() {
		return nbIncomingCalls;
	}
	/**
	 * Get the number of outgoing calls
	 * */
	public int getNbOutgoingCalls() {
		return nbOutgoingCalls;
	}
	/**
	 * Get the number of missed calls
	 * */
	public int getNbMissedCalls() {
		return nbMissedCalls;
	}

}

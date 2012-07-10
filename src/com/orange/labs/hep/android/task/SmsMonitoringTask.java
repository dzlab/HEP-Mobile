package com.orange.labs.hep.android.task;

import com.orange.labs.hep.android.common.Periods;
import java.util.Date;
import java.lang.InterruptedException;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class SmsMonitoringTask extends AsyncTask<Object, Object, Object> {

	public static final String TAG = SmsMonitoringTask.class.getSimpleName();
	protected static final long PERIOD = Periods.SMS_MONITORING_PERIOD;
	protected int nbReadSms = 0;
	protected int nbUnReadSms = 0;
	protected int nbAllSms = 0;
	
	/**
	 * Method called when the task is executed
	 * It continuously check the state of SMS with a frequency of {@link PERIOD}
	 * */
	@Override
	protected Object doInBackground(Object... params) {
		
		try{
			Context ctxtApp = (Context)params[0];						
	    	Uri uriSmsInbox = Uri.parse("content://sms/inbox");	    	
	    	
			while(true) {
				Cursor cursor = ctxtApp.getContentResolver().query(uriSmsInbox, null, null, null, null);
		    	
		    	/**
		    	 * The SMS charge indicator are reset every time we check the inbox
		    	 * All received SMS are checked, we should consider another way (e.g. consider those received this morning, day)
		    	 * For the unread messages, consider only those received after the last read message     
		    	 * */
		    	
		    	int newNbAllSms = 0;
		    	int newNbReadSms = 0;
		    	int newNbUnReadSms = 0;
	        	
		    	if(cursor.moveToFirst()) {		        	
		        	do {
		        		String person = cursor.getString(cursor.getColumnIndex("person"));
		        		String address = cursor.getString(cursor.getColumnIndex("address"));
		        		String body = cursor.getString(cursor.getColumnIndex("body"));
		        		String status = cursor.getString(cursor.getColumnIndex("status"));            		
		        		String type = cursor.getString(cursor.getColumnIndex("type"));   
		        		boolean seen = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex("seen")));
		        		boolean read = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex("read")));
		        		newNbAllSms += 1;
		        		newNbReadSms += (read==true ? 1:0);
		        		newNbUnReadSms += (read==false ? 1:0);
		        		Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex("date"))));
		        		Log.v(TAG, date+": "+person+", "+address+", "+body+", "+ status+ ", "+type+", "+seen+", "+read);
		        	}while(cursor.moveToNext()); 
		        	nbAllSms = nbAllSms==newNbAllSms ? nbAllSms:newNbAllSms;
		        	nbReadSms = nbReadSms==newNbReadSms ? nbReadSms:newNbReadSms;
		        	nbUnReadSms = nbUnReadSms==newNbUnReadSms ? nbUnReadSms:newNbUnReadSms;
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
	 * Get the total number of messages found in the inbox
	 * */
	public int getNbAllSms() {
		return nbAllSms;
	}

	/**
	 * Get the number of read messages
	 * */
	public int getNbReadSms() {
		return nbReadSms;
	}
	/**
	 * Get the number of unread messages
	 * */
	public int getUnReadSms() {
		return nbUnReadSms;
	}
}

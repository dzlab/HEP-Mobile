package com.orange.labs.hep.android;


import com.orange.labs.hep.android.task.PhoneMonitoringTask;


import android.app.Activity;
import android.os.Bundle;
import android.net.Uri;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.provider.CallLog.Calls;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import java.util.Date;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.LinearLayout;

public class HepActivity extends Activity {
	
	public static final String TAG = HepActivity.class.getSimpleName();
	public static final int PICK_CONTACT_REQUEST = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);        
        //LinearLayout view = (LinearLayout) getCurrentFocus();
        
        LinearLayout lstContacts = new LinearLayout(getApplicationContext());
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lstContacts.setOrientation(LinearLayout.VERTICAL);	
		lstContacts.setLayoutParams(params);
		
        Uri uriCallLogProvider = Uri.parse("content://call_log/calls");
        Cursor cursor = managedQuery(uriCallLogProvider, null, null, null, null);
        
        for(String colName: cursor.getColumnNames()) {
        	Log.v(TAG, "Column name: " + colName);
        }
        
        if(cursor.moveToFirst()) {
        	int idx_ID = cursor.getColumnIndex(Calls._ID);
        	int idx_NUM = cursor.getColumnIndex(Calls.NUMBER);
        	int idx_TYPE = cursor.getColumnIndex(Calls.TYPE);
        	int idx_DATE = cursor.getColumnIndex(Calls.DATE);
        	do {
        		String id = cursor.getString(idx_ID);
        		String num = cursor.getString(idx_NUM);
        		int type = Integer.parseInt(cursor.getString(idx_TYPE));
        		 
        		Date date = new Date(Long.parseLong(cursor.getString(idx_DATE)));
        		
        		// how deetct rejected calls
        		switch(type) {
        		case Calls.INCOMING_TYPE: 
        			Log.v(TAG, date+": "+id+", "+num+" INCOMING");
        			break;
        		case Calls.OUTGOING_TYPE:
        			Log.v(TAG, date+": "+id+", "+num+" OUTGOING");
        			break;
        		case Calls.MISSED_TYPE:
        			Log.v(TAG, date+": "+id+", "+num+" MISSED");
        			break;
        		// what about rejected called how to find them?
        		default:
        			Log.v(TAG, date+": "+id+", "+num+": DEFAULT");
        		}
        	}while(cursor.moveToNext());
        }
        cursor.close();
    	
    	new PhoneMonitoringTask().execute(getApplicationContext()); 
    	SmsManager smsMgr = SmsManager.getDefault();
    	//sms.
    	//SmsMessage smsMsg =  SmsMessage()
    	String smsSent = "content://sms/sent";
    	String smsInbox = "content://sms/inbox";
    	Uri uriSMSProvider = Uri.parse(smsInbox);
    	Cursor cursor2 = managedQuery(uriSMSProvider, null, null, null, null);
    	for(String colName: cursor2.getColumnNames()) {
        	Log.v(TAG, "SMS Inbox column name: " + colName);
        }
    	if(cursor2.moveToFirst()) {
        	int idx_person = cursor2.getColumnIndex("person");
        	int idx_address = cursor2.getColumnIndex("address");
        	int idx_body = cursor2.getColumnIndex("body");
        	int idx_date = cursor2.getColumnIndex("date");
        	int idx_status = cursor2.getColumnIndex("status");
        	int idx_type = cursor2.getColumnIndex("type");
        	int idx_seen = cursor2.getColumnIndex("seen");
        	int idx_read = cursor2.getColumnIndex("read");
        	do {
        		String person = cursor2.getString(idx_person);
        		String address = cursor2.getString(idx_address);
        		String body = cursor2.getString(idx_body);
        		String status = cursor2.getString(idx_status);            		
        		String type = cursor2.getString(idx_type);   
        		String seen = cursor2.getString(idx_seen);
        		String read = cursor2.getString(idx_read);
        		Date date = new Date(Long.parseLong(cursor2.getString(idx_date)));
        		Log.v(TAG, date+": "+person+", "+address+", "+body+", "+ status+ ", "+type+", "+seen+", "+read);
        	}while(cursor2.moveToNext());            	
    	}
	
        cursor2.close();
        
        
        /**
         * retrieving the contact's list
         * */
        //ListView lstView = new ListView(getApplicationContext());
        //setContentView(lstView);        
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.info_contact);
        //lstView.setAdapter(arrayAdapter);    
        
        Cursor cContacts = managedQuery(Contacts.CONTENT_URI, null, null, null, null);
        if(cContacts.moveToFirst()) {
        	do{
				String _id = cContacts.getString(cContacts.getColumnIndex(Contacts._ID));
				String name = cContacts.getString(cContacts.getColumnIndex(Contacts.DISPLAY_NAME));
				//arrayAdapter.add(name);
				//View child = getLayoutInflater().inflate(R.layout.info_contact, (ViewGroup) findViewById(R.id.informations));
				//TextView text = (TextView) child.findViewById(R.id.contact_name);
				//text.setText(name);
				//lstContacts.addView(child);
				//view.addView(txtView);
				Log.v(TAG, "contact ("+_id + "): "+ name);
			}while(cContacts.moveToNext());
        }else {
        	Log.v(TAG, "Contact list empty");
        }
        //startActivityForResult(new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI), PICK_CONTACT_REQUEST);   
        setContentView(lstContacts);
    }
        
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == PICK_CONTACT_REQUEST) {
    		if(resultCode == RESULT_OK) {
    			Uri uriContacts = data.getData();
    			Cursor curContacts = managedQuery(uriContacts, null, null, null, null);
    			if(curContacts.moveToFirst()) {
    				do{
    					String _id = curContacts.getString(curContacts.getColumnIndex(Contacts._ID));
    					String name = curContacts.getString(curContacts.getColumnIndex(Contacts.DISPLAY_NAME));
    					Log.v(TAG, "contact ("+_id + "): "+ name);
    				}while(curContacts.moveToNext());
    			}
    		}else {
    			Log.v(TAG, "Contact list empty");
    		}
    	}
    }
}
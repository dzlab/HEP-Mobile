package com.orange.labs.hep.android;

import com.orange.labs.hep.android.layout.ContactInfoListAdapter;
import com.orange.labs.hep.android.layout.CustomizationActivity;
import com.orange.labs.hep.android.task.CallMonitoringTask;
import com.orange.labs.hep.android.task.ExplicitContextMonitoringTask;
import com.orange.labs.hep.android.task.PublichContextTask;
import com.orange.labs.hep.android.task.SmsMonitoringTask;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ContactListActivity extends ListActivity {

	public static final String TAG = ContactListActivity.class.getSimpleName();
	public static final String ACTION_DISPLAY_CONTACT = "display_contact";
	protected ContactInfoListAdapter lstContacts;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lstContacts = new ContactInfoListAdapter(this);
        setListAdapter(lstContacts);
        
        /**
         * Initiate the different async-tasks
         * */
        ExplicitContextMonitoringTask explicitTask = new ExplicitContextMonitoringTask();
        explicitTask.execute(this);
        CallMonitoringTask callTask = new CallMonitoringTask();
        callTask.execute(this);
        SmsMonitoringTask smsTask = new SmsMonitoringTask();
        smsTask.execute(this);
        PublichContextTask publishTask = new PublichContextTask();
        publishTask.execute(this, explicitTask, callTask, smsTask);
	}
	
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//Toast.makeText(this, "list item clicked", Toast.LENGTH_SHORT).show();
		long selectedContactID =  lstContacts.getContactId(position).longValue();
		String selectedContact = lstContacts.getContactName(position);
		Intent intent = new Intent(this, ContactInfoActivity.class);
		intent.putExtra("ContactID", selectedContactID);
		intent.putExtra("ContactName", selectedContact);
		startActivity(intent);	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings, menu);
		return true;
	}
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_customize:
			// start activity
			Intent intent = new Intent(this, CustomizationActivity.class);			
			startActivity(intent);
			break;
		default:
			break;
		}
		return true;
	}
}

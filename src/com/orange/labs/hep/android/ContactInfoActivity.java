package com.orange.labs.hep.android;


import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.TextView;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.content.Intent;
import android.database.Cursor;
import org.restlet.resource.ClientResource;
import org.restlet.representation.Representation;
import android.util.Log;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.orange.labs.hep.android.layout.CustomizationActivity;


public class ContactInfoActivity extends Activity {

	public static final String TAG = ContactInfoActivity.class.getSimpleName();
	protected final String broker = "http://c-albarquel.rd.francetelecom.fr:8080/RB1/restlet/"; 
	protected String channel = "/telephony/";
	
	protected void request(String uri) {
		
		Log.v(TAG, uri);
		try{
			ClientResource client = new ClientResource(uri);
			//Representation response = client.get();
			String response = client.get().getText();
			Log.v(TAG, "Broker reseponse: " + response);
			
			Document doc = new SAXBuilder().build(new StringReader(response));
			Element context = doc.getRootElement();
			Element entity = context.getChild("entity");
			List<Element> params = (List<Element>) entity.getChildren("param");
			List<Element> structs= (List<Element>) entity.getChildren("struct");
			String activity = "";
			for (Element param : params) {
				String name = param.getAttribute("name").getValue();
				String text = param.getText();
				activity += name + ": " + text + "\n";				
			}
			((TextView) findViewById(R.id.contact_activity)).setText(activity);
			
			for (int i=0; i<2; i++) {
				Element struct = structs.get(i);
				String text = "";
				for(Element structParam : (List<Element>) struct.getChildren("param")) {
					String name = structParam.getAttributeValue("name");
					int number = Integer.parseInt(structParam.getText());
					text += name + ": " + number + "\n";					
				}
				if(i==0) {
					((TextView) findViewById(R.id.contact_calls)).setText(text);
				}else if(i==1){
					((TextView) findViewById(R.id.contact_sms)).setText(text);
				}
			}
			
		}catch(IOException ioe) {
			ioe.printStackTrace();
			
		}catch(JDOMException jdome) {
			jdome.printStackTrace();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long contact_id = getIntent().getLongExtra("ContactID", -1);
		String contact_name = getIntent().getStringExtra("ContactName");
		String contact_phone = "";
		// request information from Broker about the contact whose ID is been sent as parameter (use async-task to manage connection with broker)
		
		Cursor cursor = managedQuery(Phone.CONTENT_URI, new String[]{Phone.NUMBER}, Phone.CONTACT_ID + "=" + contact_id, null, null);
		if(cursor.moveToFirst() == true) {
			contact_phone = cursor.getString(0).replace("-", "");
		}
				
		setContentView(R.layout.contact_workload);
		
		TextView contact = (TextView) findViewById(R.id.contact_name);
		contact.setText(contact_name);
		
		request(broker + "synreq" + channel + contact_phone);
		
		//txt.setText("coucou, contact ID is " + contact_id + "\n" + "contact phone is " + contact_phone);		
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

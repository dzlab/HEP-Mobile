/**
 * 
 */
package com.orange.labs.hep.android.common;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import java.io.StringReader;
import java.util.List;

import org.restlet.resource.ClientResource;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class ContactInfo {

	protected long _id = 0;
	protected String _phone = "";
	protected String _activity = "";
	protected String _location = "";
	protected int _incoming_calls = 0;
	protected double _incoming_percentage = 0.0;
	protected int _outgoing_calls = 0;
	protected double _outgoing_percentage = 0.0;
	protected int _missed_calls = 0;
	protected double _missed_percentage = 0.0;
	protected int _read_sms = 0;
	protected double _read_percentage = 0.0;
	protected int _unread_sms = 0;
	protected double _unread_percentage = 0.0;
	
	public ContactInfo() {
		
	}
	public ContactInfo(long id) {
		_id = id;
	}
	public ContactInfo(long id, String phone) {
		_id = id;
		_phone = phone;
	}
	
	public boolean init() {
		boolean initialized = false;
		if(! _phone.equals("")) {
			
		}
		try{
			String uri = Constants.BROKER + Constants.SYNC_REQUEST + Constants.TELEPHONY_CHANNEL + _phone;
			String response = new ClientResource(uri).get().getText();
			Document xml = new SAXBuilder().build(new StringReader(response));
			Element context = xml.getRootElement();			
			Element entity = context.getChild("entity");
			List<Element> params = (List<Element>) entity.getChildren("param");
			List<Element> structs= (List<Element>) entity.getChildren("struct");
			
			for (Element param : params) {
				String name = param.getAttribute("name").getValue();
				String text = param.getText();
				if(name.equals("activity")) {
					_activity = text;				
				}else if(name.equals("location")) {
					_location = text;
				}
			}
			
			for (int i=0; i<2; i++) {
				Element struct = structs.get(i);
				for(Element structParam : (List<Element>) struct.getChildren("param")) {
					String name = structParam.getAttributeValue("name");
					int number = Integer.parseInt(structParam.getText());
					if(name.equals("incoming")) {
						_incoming_calls = number;
					}else if(name.equals("outgoing")) {
						_outgoing_calls = number;
					}else if(name.equals("missed")) {
						_missed_calls = number;
					}else if(name.equals("read")) {
						_read_sms = number;
					}else if(name.equals("unread")) {
						_unread_sms = number;
					}
				}										
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		initWorkloadParams();
		
		return initialized;
	}
	
	public void initWorkloadParams() {
		try {
			String uri = Constants.BROKER + Constants.SYNC_REQUEST + Constants.WORKLOAD_CHANNEL + _phone;
			String response = new ClientResource(uri).get().getText();
			Document xml = new SAXBuilder().build(new StringReader(response));
			Element context = xml.getRootElement();			
			Element entity = context.getChild("entity");

			List<Element> structs= (List<Element>) entity.getChildren("struct");
			
			for (int i=0; i<2; i++) {
				Element struct = structs.get(i);
				for(Element structParam : (List<Element>) struct.getChildren("param")) {
					String name = structParam.getAttributeValue("name");
					int progress = Integer.parseInt(structParam.getText());
					if(name.equals("incoming")) {
						_incoming_percentage = 0.01 * progress;
						
					}else if(name.equals("outgoing")) {
						_outgoing_percentage = 0.01 * progress;
						
					}else if(name.equals("missed")) {
						_missed_percentage = 0.01 * progress;
						
					}else if(name.equals("read")) {
						_read_percentage = 0.01 * progress;
						
					}else if(name.equals("unread")) {
						_unread_percentage = 0.01 * progress;
					}
				}						
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean retrieveLocalInfo(Context context) {
		boolean done = false;
		
		Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI, new String[]{Phone.NUMBER}, Phone.CONTACT_ID + "=" + _id, null, null);
		if(cursor.moveToFirst() == true) {
			_phone = cursor.getString(0).replace("-", "");
			done = true;
		}
		
		return done;
	}
	
	public void setPhone(String phone) {
		_phone = phone;
	}
	public String getActivity() {
		return _activity;
	}
	public String getLocation() {
		return _location;
	}
	public int getIncomingCalls() {
		return _incoming_calls;
	}
	public int getOutgoingCalls() {
		return _outgoing_calls;
	}
	public int getMissedCalls() {
		return _missed_calls;
	}
	public int getReadSms() {
		return _read_sms;
	}
	public int getUnReadSms() {
		return _unread_sms;
	}
	/**
	 * Compute the workload of this contact based on his  received calls & SMS
	 * */
	public int getWorkload() {
		int workload = -1;
		double up = _incoming_calls * _incoming_percentage
				  + _outgoing_calls * _outgoing_percentage
				  + _missed_calls * _outgoing_percentage
				  + _read_sms * _read_percentage
				  + _unread_sms * _unread_percentage;
		
		double down = _incoming_calls + _outgoing_calls + _missed_calls + _read_sms + _unread_sms;
		
		if(down != 0.0) {
			double load = up / down;
			if(load < 0.25) 
				workload = 0;
			else if(load < 0.5) 
				workload = 1;
			else if(load < 0.75) 
				workload = 2;
			else  
				workload = 3;
		}
		return workload;
	}
}

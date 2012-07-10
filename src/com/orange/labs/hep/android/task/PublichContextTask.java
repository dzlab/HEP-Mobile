package com.orange.labs.hep.android.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.restlet.data.MediaType;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ClientResource;

import java.io.IOException;
import android.telephony.TelephonyManager;
import android.content.Context;

public class PublichContextTask extends AsyncTask<Object, Object, Object> {

	public static final String TAG = PublichContextTask.class.getSimpleName();
	public static final long PERIOD = 60 * 1000;
	//protected final String channel = "/telephony/0654983212";
	protected final String broker = "http://c-albarquel.rd.francetelecom.fr:8080/RB1/restlet/"; 
	//protected final String broker = "http://10.193.204.83:8080/RB1/restlet/";
	protected String channel = "/telephony/";
	
	protected void postSchema() {
		String schema = "<?xml version='1.0' ?><channel>"
			+ "<description>" + "bla bla" + "</description>"
			+ "<context name='telephony'>" 
			+ "<entity type='person' identifier='int'>"
			+ "<param name='activity' type='String'/>"
			+ "<param name='location' type='String'/>"
			+ "<struct name='calls_context'>"
			+ "<param name='incoming' type='int'/>"
			+ "<param name='outgoing' type='int'/>"
			+ "<param name='missed' type='int'/>"
			+ "</struct>"
			+ "<struct name='sms_context'>"
			+ "<param name='read' type='int'/>"
			+ "<param name='unread' type='int'/>"			
			+ "</struct>"
			+ "</entity>"
			+ "</context>"
			+ "</channel>";		

		publish(broker + "meta" + channel, schema);
	}
	
	/**
	 * Publish contextual information to the context broker
	 * @param uri of the context broker
	 * @param content to be sent to the broker
	 * */
	protected void publish(String uri, String content) {		
		try {
			ClientResource client = new ClientResource(uri);
			IContextResource resource = client.wrap(IContextResource.class);
			client.post(content, MediaType.APPLICATION_XML);
			if( client.getStatus().isSuccess() ) {
				client.getResponseEntity().write(System.out);
			}else {
				Log.v(TAG, "POST - something wrong: "+ client.getStatus());
			}
		}catch (IOException ioe) {
			ioe.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		try{
			Thread.sleep(PERIOD);
		}catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		
		Activity main = (Activity) params[0];	
		String phone_number = ((TelephonyManager) main.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
		if(phone_number != null)
			phone_number.replace("-", "");
		Log.v(TAG, "phone number is " + phone_number);
		channel += phone_number;
		ExplicitContextMonitoringTask explicitTask = (ExplicitContextMonitoringTask) params[1];
		CallMonitoringTask callTask = (CallMonitoringTask) params[2];
		SmsMonitoringTask smsTask = (SmsMonitoringTask) params[3];
		
		String context = "<context name='telephony'>"
			+ "<entity type='person' identifier='" + phone_number + "'>"
			+ "<param name='activity' type='String'>" + explicitTask.getActivity() + "</param>"
			+ "<param name='location' type='String'>" + explicitTask.getLocation() + "</param>"
			+ "<struct name='calls_context'>"
			+ "<param name='incoming' type='int'>" + callTask.getNbIncomingCalls() + "</param>"
			+ "<param name='outgoing' type='int'>" + callTask.getNbOutgoingCalls() + "</param>"
			+ "<param name='missed' type='int'>" + callTask.getNbMissedCalls() + "</param>"
			+ "</struct>"
			+ "<struct name='sms_context'>"
			+ "<param name='read' type='int'>" + smsTask.getNbReadSms() + "</param>"
			+ "<param name='unread' type='int'>" + smsTask.getUnReadSms() + "</param>"			
			+ "</struct>"
			+ "</entity>"
			+ "</context>";
				
		postSchema();
		publish(broker + "pub" + channel, context);
		
		return null;
	}
	
	/**
	 * Interface used to wrap the Get and Post method of RESTlet server
	 * */
	public interface IContextResource {
		
		@Get
		public String get();
		
		@Post
		public void post(String context);
	}

}

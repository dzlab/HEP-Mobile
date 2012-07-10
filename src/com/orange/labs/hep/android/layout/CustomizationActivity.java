/**
 * 
 */
package com.orange.labs.hep.android.layout;

import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;

import com.orange.labs.hep.android.R;
import com.orange.labs.hep.android.common.Constants;
import com.orange.labs.hep.android.task.PublichContextTask.IContextResource;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * @author hjwk9387
 *
 */
public class CustomizationActivity extends Activity implements OnSeekBarChangeListener {

	public static final String TAG = CustomizationActivity.class.getSimpleName();
	protected String phone_number;
	protected SeekBar _sms_read;
	protected SeekBar _sms_unread;
	protected SeekBar _phone_incoming;
	protected SeekBar _phone_outgoing;
	protected SeekBar _phone_missed;
	protected boolean anyChangesToBeSaved = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customization);
		
		phone_number = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
		
		_sms_read = (SeekBar) findViewById(R.id.sms_read_slide);
		_sms_unread = (SeekBar) findViewById(R.id.sms_unread_slide);
		_phone_incoming = (SeekBar) findViewById(R.id.phone_incoming_slide);
		_phone_outgoing = (SeekBar) findViewById(R.id.phone_outgoing_slide);
		_phone_missed = (SeekBar) findViewById(R.id.phone_missed_slide);
		
		_sms_read.setOnSeekBarChangeListener(this);
		_sms_unread.setOnSeekBarChangeListener(this);
		_phone_incoming.setOnSeekBarChangeListener(this);
		_phone_outgoing.setOnSeekBarChangeListener(this);
		_phone_missed.setOnSeekBarChangeListener(this);
		
		initSeekBars();
	}
	
	protected void initSeekBars() {
		new Thread(new Runnable() {
			public void run() {
				
				try {
					String uri = Constants.BROKER + Constants.SYNC_REQUEST + Constants.WORKLOAD_CHANNEL + phone_number;
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
								_phone_incoming.setProgress(progress);
							}else if(name.equals("outgoing")) {
								_phone_outgoing.setProgress(progress);
							}else if(name.equals("missed")) {
								_phone_missed.setProgress(progress);
							}else if(name.equals("read")) {
								_sms_read.setProgress(progress);
							}else if(name.equals("unread")) {
								_sms_unread.setProgress(progress);
							}
						}						
					}
					
				}catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		
	}
	
	/**
	 * When the current activity goes onPause, i.e. another one comes on front, save the current progress values 
	 * of the different {@link SeekBar} components 
	 * */
	protected void onPause() {
		super.onPause();
		if(anyChangesToBeSaved)
		new Thread(new Runnable() {
			
			public void run() {
				try{
					
					String uri = Constants.BROKER + Constants.PUB_REQUEST + Constants.WORKLOAD_CHANNEL + phone_number;					
					
					String content = "<context name='telephony_workload'>"
						+ "<entity type='person' identifier='" + phone_number + "'>"
						+ "<struct name='calls_context'>"
						+ "<param name='incoming' type='int'>" + _phone_incoming.getProgress() + "</param>"
						+ "<param name='outgoing' type='int'>" + _phone_outgoing.getProgress() + "</param>"
						+ "<param name='missed' type='int'>" + _phone_missed.getProgress() + "</param>"
						+ "</struct>"
						+ "<struct name='sms_context'>"
						+ "<param name='read' type='String'>" + _sms_read.getProgress() + "</param>"
						+ "<param name='unread' type='String'>" + _sms_unread.getProgress() + "</param>"			
						+ "</struct>"
						+ "</entity>"
						+ "</context>";
					
					ClientResource client = new ClientResource(uri);
					IContextResource resource = client.wrap(IContextResource.class);
					client.post(content, MediaType.APPLICATION_XML);
					if( client.getStatus().isSuccess() ) {
						client.getResponseEntity().write(System.out);
					}else {
						Log.v(TAG, "POST - something wrong: "+ client.getStatus());
					}
					
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
				
	}

	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		anyChangesToBeSaved = true;
		if(seekBar == _sms_read) {
			((TextView) findViewById(R.id.sms_read_text)).setText("Read " + progress + "%");
		
		}else if(seekBar == _sms_unread) {
			((TextView) findViewById(R.id.sms_unread_text)).setText("Unread " + progress + "%");
		
		}else if(seekBar == _phone_incoming) {
			((TextView) findViewById(R.id.phone_incoming_text)).setText("Incoming " + progress + "%");
		
		}else if(seekBar == _phone_outgoing) {
			((TextView) findViewById(R.id.phone_outgoing_text)).setText("Outgoing " + progress + "%");
		
		}else if(seekBar == _phone_missed) {
			((TextView) findViewById(R.id.phone_missed_text)).setText("Missed " + progress + "%");
		
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
}

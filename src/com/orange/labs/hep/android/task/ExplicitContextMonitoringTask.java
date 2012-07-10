package com.orange.labs.hep.android.task;

import com.orange.labs.hep.android.R;
import com.orange.labs.hep.android.common.Periods;

import android.os.AsyncTask;
import android.os.Looper;
import android.os.Bundle;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.text.SpannableStringBuilder;

/**
 * TODO the method show display a view, each {@link PERIOD}, in which the user enter his activity and location
 * This AsyncTask monitor the context user (e.g. activity, location) by explicitly asking the user 
 * */
public class ExplicitContextMonitoringTask extends AsyncTask<Object, Object, Object> {

	public static final String TAG = ExplicitContextMonitoringTask.class.getSimpleName();
	public static final long PERIOD = Periods.EXPLICIT_MONITORING_PERIOD;
	
	protected String activity = "";
	protected String location = "";
	
	@Override
	protected Object doInBackground(Object... params) {
		Looper.prepare();
		Context context = (Context) params[0];
		try{
			while(true) {
				Thread.sleep(PERIOD);
				ActivityDialog dialog = new ActivityDialog(context, this);		
				dialog.setTitle("What are you doing!");
				dialog.show();
				
				Looper.loop();				
			}
			
		}catch(InterruptedException ie) {
			ie.printStackTrace();
		}
		return null;
	}
	
	public void setActivity(String currentActivity) {
		activity = currentActivity;
	}
	public String getActivity() {
		return activity;
	}
	
	public void setLocation(String currentLocation) {
		location = currentLocation;
	}
	public String getLocation() {
		return location;
	}

	/**
	 * Class used to customize the displayed dialog
	 * */
	public class ActivityDialog extends Dialog implements OnClickListener {

		protected ExplicitContextMonitoringTask callerTask;
		
		public ActivityDialog(Context context) {
			super(context);
		}
		public ActivityDialog(Context context, ExplicitContextMonitoringTask task) {
			super(context);
			this.callerTask = task;
		}

		public ActivityDialog(Context context, String activity, String location) {
			super(context);
		}
		
		/**
		 * Called when the dialog is created
		 * */
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_dialog);			

			Button btnOK = (Button) findViewById(R.id.btnActOK);					
			btnOK.setOnClickListener(this);
			
			Button btnCancel = (Button) findViewById(R.id.btnActCancel);
			btnCancel.setOnClickListener(this);				
		}
		
		/**
		 * Called when the user click on one of the dialog buttons
		 * */
		@Override
		public void onClick(View view) {
			
			switch(view.getId()) {
			case R.id.btnActOK:
				SpannableStringBuilder currentActivity = new SpannableStringBuilder(((TextView) findViewById(R.id.edit_activity)).getText());
				SpannableStringBuilder currentLocation = new SpannableStringBuilder(((TextView) findViewById(R.id.edit_location)).getText());	
				callerTask.setActivity(currentActivity.toString());
				callerTask.setLocation(currentLocation.toString());
				break;
				
			case R.id.btnActCancel:
				
				break;
				
			default:
				
			}
			dismiss();
		}
		
	}
}

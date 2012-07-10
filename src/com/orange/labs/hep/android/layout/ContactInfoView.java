package com.orange.labs.hep.android.layout;

import com.orange.labs.hep.android.common.Workload;
import com.orange.labs.hep.android.R;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log;
import android.view.Gravity;
import java.lang.NullPointerException;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ContactInfoView extends LinearLayout {

	public static final String TAG = ContactInfoView.class.getSimpleName();
	protected TextView txtName;
	protected TextView txtContext; 
	protected int _workload;
	
	public ContactInfoView(Context context, String contactName, String contactContext, int workload) {
		super(context);

		//http://android-france.fr/2009/06/07/developper-sa-listview-personnalisee-sous-android/
		this.setOrientation(VERTICAL);
		/**
		 * create the view to display the contact full name and entity 
		 * */
		txtName = new TextView(context);
		txtName.setText(contactName);
		txtName.setTextSize(18);
		txtName.setPadding(5, 0, 0, 0);
		addView(txtName, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		/**
		 * create the view to display the contextual information (activity + location) of the contact
		 * */
		TableLayout table = new TableLayout(context);
		TableRow row = new TableRow(context);
		table.addView(row);
		
		//table.setOrientation(HORIZONTAL);		
		
		_workload = workload;
		ImageView imgWorkload = new ImageView(context);
		switch (_workload) {
		case Workload.VERY_AVAILABLE:
			imgWorkload.setImageResource(R.drawable.ic_workload_very_available);
			break;
		case Workload.AVAILABLE:
			imgWorkload.setImageResource(R.drawable.ic_workload_availabe);
			break;
		case Workload.BUSY:
			imgWorkload.setImageResource(R.drawable.ic_workload_busy);
			break;
		case Workload.DO_NOT_DISTURB:
			imgWorkload.setImageResource(R.drawable.ic_workload_do_not_disturb);
			break;
		default:
			imgWorkload.setImageResource(R.drawable.ic_workload_off_line);
		}
		//imgWorkload.set
		TableLayout.LayoutParams imgParams = new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		imgParams.gravity = Gravity.RIGHT;
		row.addView(imgWorkload);
		
		txtContext = new TextView(context);
		txtContext.setText(contactContext);
		txtContext.setTextSize(10);
		txtContext.setPadding(15, 0, 0, 0);
		txtContext.setGravity(Gravity.LEFT);
		//addView(txtContext, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		//row.addView(txtContext, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		row.addView(txtContext);
		
	
		addView(table, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		//addView(table);
	}

	public void setContactName(String contactName) {
		if(txtName != null) {
			txtName.setText(contactName);
		}else {
			Log.e(TAG, "trying to set name of null object");
			throw new NullPointerException();
		}
	}
	
	public void setContactContext(String contactContext) {
		if(txtContext != null) {
			txtContext.setText(contactContext);
		}else {
			Log.e(TAG, "trying to set name of null object");
			throw new NullPointerException();
		}
	}
	
	public void setContactWorkload(int contactWorkload) {
		_workload = contactWorkload;
	}
	
}

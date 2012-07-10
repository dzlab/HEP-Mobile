package com.orange.labs.hep.android.layout;

import com.orange.labs.hep.android.common.ContactInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import java.util.ArrayList;

public class ContactInfoListAdapter extends BaseAdapter {

	public static final String TAG = ContactInfoListAdapter.class.getSimpleName();
	protected Context _context;
	protected ArrayList<Long> _ids;
	protected ArrayList<String> _names;
	protected ArrayList<String> _contexts;
	protected ArrayList<Integer> _workload;
	protected ArrayList<ContactInfo> _contacts;
	
	/**
	 * 
	 * */
	public ContactInfoListAdapter(Context context) {
		_context = context;
				
		_ids = new ArrayList<Long>();
		_names = new ArrayList<String>();
		_contexts = new ArrayList<String>();
		_workload = new ArrayList<Integer>();
		_contacts = new ArrayList<ContactInfo>();

        /**
         * retrieving the contact's list
         * */
        ContentResolver db = _context.getContentResolver();
		Cursor cContacts = db.query(Contacts.CONTENT_URI, null, null, null, null);
        if(cContacts.moveToFirst()) {
        	do{
				Long _id = cContacts.getLong(cContacts.getColumnIndex(Contacts._ID));
				ContactInfo contact = new ContactInfo(_id);
				contact.retrieveLocalInfo(context);
				contact.init();
				String name = cContacts.getString(cContacts.getColumnIndex(Contacts.DISPLAY_NAME));
				
				_ids.add(_id);
				_names.add(name);
				_contexts.add(contact.getActivity()+"\n"+contact.getLocation());
				// need to generate a workload
				_workload.add(contact.getWorkload());
				
				Log.v(TAG, "contact ("+_id + "): "+ name);
			}while(cContacts.moveToNext());
        }else {
        	Log.v(TAG, "Contact list empty");
        }
        cContacts.close();

	}
	
	/**
	 * */
	@Override
	public int getCount() {
		return _names.size();
	}

	@Override
	public Object getItem(int position) {		
		return _names.get(position);
	}

	@Override
	public long getItemId(int position) { 
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactInfoView view;
		if(convertView == null) {
			view = new ContactInfoView(_context, _names.get(position), _contexts.get(position), _workload.get(position));
		}else {
			view = (ContactInfoView) convertView;
			view.setContactName(_names.get(position));
			view.setContactContext(_contexts.get(position));
			view.setContactWorkload(_workload.get(position));
		}		
		
		return view;
	}

	/**
	 * 
	 * */
	public Long getContactId(int position) {
		return _ids.get(position);
	}
	
	public String getContactName(int position) {
		return _names.get(position);
	}
	
	public int getContactWorkload(int position) {
		return _workload.get(position);
	}
}

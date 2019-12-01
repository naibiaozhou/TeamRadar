package com.nut.teamradar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nut.teamradar.R;
import com.nut.teamradar.model.Contact;

public class ContactsListAdapter extends ArrayAdapter<Contact> {
	
	private ArrayList<Contact> mItems = null;
	private LayoutInflater mInflater;

	public ContactsListAdapter(Context context,
			int textViewResourceId, ArrayList<Contact> objects) {
		super(context, textViewResourceId,objects);
		mInflater = LayoutInflater.from(context);
		mItems = objects;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}
	public Contact getItem(int arg0) {
		// TODO Auto-generated method stub
		return mItems.get(arg0);
	}
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView Caption;
		TextView Subscription;
		Contact aContact=null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.contactitems, null);
		} 

		
		Caption = (TextView) convertView.findViewById(R.id.txtCaption);
		Subscription = (TextView) convertView.findViewById(R.id.txtSubscriptions);
		aContact = mItems.get(position);
		//if(aContact != null)
		{
			Caption.setText(aContact.getContactName());
			Subscription.setText(aContact.getContactSubscription());
		}
		
		

		if (position == selectItem) {
			convertView.setBackgroundColor(Color.DKGRAY);
		}
		else {
			convertView.setBackgroundColor(Color.GRAY);
		}	
		
		return convertView;
	}
	public  void setSelectItem(int selectItem) {
		 this.selectItem = selectItem;
	}
	private int  selectItem=-1;
}
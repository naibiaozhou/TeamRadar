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
import com.nut.teamradar.model.ActivityHistory;
import com.nut.teamradar.model.Contact;

public class ActivityHistoryAdapter extends ArrayAdapter<ActivityHistory> {
	
	private ArrayList<ActivityHistory> mItems = null;
	private LayoutInflater mInflater;

	public ActivityHistoryAdapter(Context context,
			int textViewResourceId, ArrayList<ActivityHistory> objects) {
		super(context, textViewResourceId,objects);
		mInflater = LayoutInflater.from(context);
		mItems = objects;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}
	public ActivityHistory getItem(int arg0) {
		// TODO Auto-generated method stub
		return mItems.get(arg0);
	}
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView Caption;
		TextView HistoryDate;
		ActivityHistory aRecord=null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.contactitems, null);
		} 

		
		Caption = (TextView) convertView.findViewById(R.id.txtCaption);
		HistoryDate = (TextView) convertView.findViewById(R.id.txtSubscriptions);
		aRecord = mItems.get(position);
		//if(aContact != null)
		{
			Caption.setText("»î¶¯");
			HistoryDate.setText(aRecord.getDatetime());
		}	
		return convertView;
	}
	public  void setSelectItem(int selectItem) {
		 this.selectItem = selectItem;
	}
	private int  selectItem=-1;
}
package com.nut.teamradar.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nut.teamradar.R;
import com.nut.teamradar.model.Group;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class ActivityViewAdapter extends ArrayAdapter<Group> {
	
	private ArrayList<Group> mItems = null;
	private LayoutInflater mInflater;
	private int ActiveIndex=-1;

	public ActivityViewAdapter(Context context,
			int textViewResourceId, ArrayList<Group> objects) {
		super(context, textViewResourceId, objects);
		mInflater = LayoutInflater.from(context);
		mItems = objects;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}
	public Group getItem(int arg0) {
		// TODO Auto-generated method stub
		return mItems.get(arg0);
	}
	public void SetActiveItem(int Index)
	{
		ActiveIndex = Index;
	}
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView Caption;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.colorlistitem, null);
		} 

		
		Caption = (TextView) convertView.findViewById(R.id.itemText);
		Caption.setText(mItems.get(position).getName());
		
		

		if(ActiveIndex == position)
		{
			convertView.setBackgroundColor(Color.GREEN);
		}
		else if (position == selectItem) {
			convertView.setBackgroundColor(Color.DKGRAY);
		}
		else {
			convertView.setBackgroundColor(Color.GRAY);
		}	
		
		return convertView;
	}
	public  void setSelectItem(int selectItem) {
		if(selectItem < mItems.size())
		{
			this.selectItem = selectItem;
		}
	}
	private int  selectItem=-1;
}



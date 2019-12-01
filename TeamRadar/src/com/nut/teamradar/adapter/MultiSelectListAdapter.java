package com.nut.teamradar.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.nut.teamradar.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MultiSelectListAdapter extends BaseAdapter {  
 
	private ArrayList<String> list;  

	private static HashMap<Integer, Boolean> isSelected;  
  
	private Context context;  

	private LayoutInflater inflater = null;  

	public MultiSelectListAdapter(ArrayList<String> list, Context context) {  
		this.context = context;  
		this.list = list;  
		inflater = LayoutInflater.from(context);  
		isSelected = new HashMap<Integer, Boolean>();  

		initDate();  
	}  

	private void initDate() {  
		for (int i = 0; i < list.size(); i++) {  
			getIsSelected().put(i, false);  
        }  
    }  

   @Override  
   public int getCount() {  
        return list.size();  
   }  

   @Override  
   public Object getItem(int position) {  
        return list.get(position);  
   }  

   @Override  
   public long getItemId(int position) {  
	     return position;  
   }  

  @Override  
   public View getView(int position, View convertView, ViewGroup parent) {  
        ViewHolder holder = null;  
        if (convertView == null) {  

        	holder = new ViewHolder();  
        	convertView = inflater.inflate(R.layout.contactitemlayout, null);  
        	holder.tv = (TextView) convertView.findViewById(R.id.item_tv);  
        	holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);  

        	convertView.setTag(holder);  
         } else {  
 
             holder = (ViewHolder) convertView.getTag();  
        }  

        holder.tv.setText(list.get(position));  

        holder.cb.setChecked(getIsSelected().get(position));  
        return convertView;  
   }  
 
   public static HashMap<Integer, Boolean> getIsSelected() {  
       return isSelected;  
   }  

   public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {  
	   MultiSelectListAdapter.isSelected = isSelected;  
    }  

   public static class ViewHolder {  
       TextView tv;  
       CheckBox cb;  
   }  
}  


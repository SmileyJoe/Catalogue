package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import com.smileyjoedev.genLibrary.Debug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LocationListAdapter extends BaseAdapter {

	private ArrayList<Location> locations;
	private Context context;
	
	public LocationListAdapter(Context context, ArrayList<Location> locations){
		this.locations = locations;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return this.locations.size();
	}

	@Override
	public Object getItem(int position) {
		return this.locations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Location location = this.locations.get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.xml.location_list_row, null);
		
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
		TextView tvNumChildren = (TextView) convertView.findViewById(R.id.tv_num_loc_children);
		TextView tvNumItems = (TextView) convertView.findViewById(R.id.tv_num_items);
		
		tvTitle.setText(location.getTitle());
		Debug.d(location.toString());
		tvNumChildren.setText(Long.toString(location.getNumChildren()));
		tvNumItems.setText(Long.toString(location.getNumItems()));
		
		return convertView;
	}
	
	public void setData(ArrayList<Location> data){
		this.locations = data;
	}
}

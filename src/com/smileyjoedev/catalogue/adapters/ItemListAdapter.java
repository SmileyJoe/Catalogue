package com.smileyjoedev.catalogue.adapters;

import java.util.ArrayList;

import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.R.id;
import com.smileyjoedev.catalogue.R.xml;
import com.smileyjoedev.catalogue.objects.Item;
import com.smileyjoedev.genLibrary.Camera;
import com.smileyjoedev.genLibrary.Debug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemListAdapter extends BaseAdapter {

	private ArrayList<Item> items;
	private Context context;
	private Camera camera;
	
	public ItemListAdapter(Context context, ArrayList<Item> items){
		this.items = items;
		this.context = context;
		this.camera = new Camera(Constants.PHOTO_PATH);
	}
	
	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public Object getItem(int position) {
		return this.items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Item item = this.items.get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.xml.item_list_row, null);
		
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
		TextView tvDescription = (TextView) convertView.findViewById(R.id.tv_description);
		ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo);
		
		tvTitle.setText(item.getTitle());
		tvDescription.setText(item.getDesc());
		this.camera.setName(Long.toString(item.getId()));
		this.camera.showPhoto(ivPhoto);
		
		return convertView;
	}
	
	public void setData(ArrayList<Item> data){
		this.items = data;
	}
	
}

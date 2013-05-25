package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryListAdapter extends BaseAdapter {

	private ArrayList<Category> categories;
	private Context context;
	
	public CategoryListAdapter(Context context, ArrayList<Category> categories){
		this.categories = categories;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return this.categories.size();
	}

	@Override
	public Object getItem(int position) {
		return this.categories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Category category = this.categories.get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.xml.category_list_row, null);
		
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
		TextView tvNumChildren = (TextView) convertView.findViewById(R.id.tv_num_cat_children);
		TextView tvNumItems = (TextView) convertView.findViewById(R.id.tv_num_items);
		
		tvTitle.setText(category.getTitle());
		tvNumChildren.setText(Long.toString(category.getNumChildren()));
		tvNumItems.setText(Long.toString(category.getNumItems()));
		
		return convertView;
	}
	
	public void setData(ArrayList<Category> data){
		this.categories = data;
	}
	
}

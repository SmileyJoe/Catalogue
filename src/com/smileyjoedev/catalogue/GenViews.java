package com.smileyjoedev.catalogue;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.GeneralViews;

public class GenViews extends GeneralViews {

	public GenViews(Context context) {
		super(context);
	}
	
	public static void itemViewLocation(Context context, LinearLayout wrapper, Item item){
    	wrapper.removeAllViews();
    	
    	for(int i = 0; i < item.getLocations().size(); i++){
    		if(!item.getLocations().get(i).getLocation().getTitle().equals("")){
    			final int position = i;
				
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.xml.item_view_location_row, null);
				
				TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
				TextView tvQuan = (TextView) view.findViewById(R.id.tv_quantity);
				final HorizontalScrollView hsvBreadcrumb = (HorizontalScrollView) view.findViewById(R.id.hsv_breadcrumb);
				
				tvTitle.setText(item.getLocations().get(i).getLocation().getBreadCrumb(context));
				tvQuan.setText(Long.toString(item.getLocations().get(i).getQuantity()));
				
				Handler handler = new Handler();
		    	Runnable r = new Runnable(){
		    	    public void run(){
		    	    	hsvBreadcrumb.fullScroll(HorizontalScrollView.FOCUS_RIGHT);                      
		    	    }
		    	};
		    	handler.postDelayed(r, 100L);
				wrapper.addView(view);
    		}
		}
	}
	
	public static void itemViewCategory(Context context, LinearLayout wrapper, Item item){
    	wrapper.removeAllViews();
    	
    	for(int i = 0; i < item.getCategories().size(); i++){
    		if(!item.getCategories().get(i).getTitle().equals("")){
    			final int position = i;
				
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.xml.item_view_category_row, null);
				
				TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
				final HorizontalScrollView hsvBreadcrumb = (HorizontalScrollView) view.findViewById(R.id.hsv_breadcrumb);
				
				tvTitle.setText(item.getCategories().get(i).getBreadCrumb(context));
				
				Handler handler = new Handler();
		    	Runnable r = new Runnable(){
		    	    public void run(){
		    	    	hsvBreadcrumb.fullScroll(HorizontalScrollView.FOCUS_RIGHT);                      
		    	    }
		    	};
		    	handler.postDelayed(r, 100L);
				wrapper.addView(view);
    		}
		}
    	
	}
	
	public static void itemEditCategory(Context context, LinearLayout wrapper, Item item){
		GenViews.itemViewCategory(context, wrapper, item);
	}

}

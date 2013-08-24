package com.smileyjoedev.catalogue;

import android.app.ActionBar;
import android.content.Context;
import android.os.Handler;
import android.support.v13.app.FragmentTabHost;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.smileyjoedev.catalogue.fragments.CategoryListFragment;
import com.smileyjoedev.catalogue.fragments.ItemListFragment;
import com.smileyjoedev.catalogue.fragments.LocationListFragment;
import com.smileyjoedev.catalogue.objects.Item;
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
	
	public static void createBreadCrumbActionBar(Context context, ActionBar bar){
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowCustomEnabled(true);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.xml.actionbar_horizontal_scroll, null);
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL;
		view.setLayoutParams(lp);
		bar.setDisplayShowTitleEnabled(false);
		bar.setCustomView(view);
	}
	
	public static void addItemListTab(FragmentTabHost host){
	  	TabSpec spec = host.newTabSpec(Integer.toString(Constants.TAB_ITEM));
	  	spec.setIndicator("Items");
	  	host.addTab(spec, ItemListFragment.class, null);
	}
	
	public static void addCategoryListTab(FragmentTabHost host){
		GenViews.addCategoryListTab(host, -1);
	}
	
	public static void addCategoryListTab(FragmentTabHost host, long categoryId){
	  	TabSpec spec = host.newTabSpec(Integer.toString(Constants.TAB_CATEGORY));
	  	if(categoryId <= 0){
	  		spec.setIndicator("Categories");
	  	} else {
	  		spec.setIndicator("Sub-Categories");
	  	}
	  	
	  	host.addTab(spec, CategoryListFragment.class, null);
	}
	
	public static void addLocationListTab(FragmentTabHost host){
		GenViews.addLocationListTab(host, -1);
	}
	
	public static void addLocationListTab(FragmentTabHost host, long locationId){
	  	TabSpec spec = host.newTabSpec(Integer.toString(Constants.TAB_LOCATION));
	  	if(locationId <= 0){
	  		spec.setIndicator("Locations");
	  	} else {
	  		spec.setIndicator("Sub-Locations");
	  	}
	  	
	  	host.addTab(spec, LocationListFragment.class, null);
	}

}

package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

public class CategoryListFragment extends SherlockListFragment{

	private ArrayList<Category> categories;
	private ArrayList<Category> breadCrumb;
	private DbCategoryAdapter categoryAdapter;
	private long categoryId;
	private Category category;
	private Context context;
	private CategoryListAdapter adapter;
	private ArrayList<Long> categoryIdTrail;
	private LinearLayout llBreadCrumb;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.getCategories();
		this.adapter = new CategoryListAdapter(this.context, this.categories);
		
		this.populateBreadCrumb();
		setListAdapter(adapter);
	}
	
	public void getCategories(){
		this.categories = this.categoryAdapter.get(this.categoryId);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(getActivity(),getListView().getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
		this.categoryIdTrail.add(this.categoryId);
		this.categoryId = this.categories.get(position).getId();
		this.category = this.categoryAdapter.getDetails(this.categoryId);
		this.addToBreadCrumb();
		this.updateView();
		Broadcast.categoryChanged(this.context, this.categoryId);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		CategoryDataInterface hostInterface;
		try {
	        hostInterface = (CategoryDataInterface) activity;
	    } catch(ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement CategoryData");
	    }
	    
	    this.categoryId = hostInterface.getCategoryId();
	    this.context = activity.getApplicationContext();
	    this.categoryAdapter = new DbCategoryAdapter(this.context);
	    this.categoryIdTrail = new ArrayList<Long>();
	    this.category = this.categoryAdapter.getDetails(this.categoryId);
	    this.breadCrumb = this.categoryAdapter.getBreadCrumb(this.categoryId);
	    this.llBreadCrumb = new LinearLayout(this.context);
	}
	
	public void updateView(){
		this.categories.clear();
		this.getCategories();
		this.adapter.setData(this.categories);
		this.adapter.notifyDataSetChanged();
	}
	
	public long getCategoryId(){
		return this.categoryId;
	}
	
	public boolean showPreviousCategory(){
		return showPreviousCategory(true);
	}
	
	public boolean showPreviousCategory(boolean updateView){
		if(this.categoryIdTrail.size() == 0){
			return false;
		} else {
			int lastPosition = this.categoryIdTrail.size()-1;
			this.categoryId = this.categoryIdTrail.get(lastPosition);
			this.categoryIdTrail.remove(lastPosition);
			this.removeFromBreadCrumb();
			if(updateView){
				this.updateView();
				Broadcast.categoryChanged(this.context, this.categoryId);
			}
			return true;
		}
		
	}
	
	public void addToBreadCrumb(){
		this.addBreadCrumbItem(this.category);
		this.breadCrumb.add(this.category);
		
	}
	
	private void removeFromBreadCrumb(){
		this.llBreadCrumb.removeViewAt(llBreadCrumb.getChildCount()-1);
		this.breadCrumb.remove(this.breadCrumb.size()-1);
	}
	
    private void populateBreadCrumb(){
    	TextView tv;
    	int textSize = 20;
    	
    	this.llBreadCrumb.removeAllViews();
    	
    	this.addBreadCrumbItem(new Category());
		
    	for(int i = this.breadCrumb.size(); i > 0; i--){
    		this.addBreadCrumbItem(this.breadCrumb.get(i-1));
    	}
    }
    
    private void addBreadCrumbItem(Category category){
    	TextView tv  = new TextView(this.context);
		tv.setTag(category.getId());
		tv.setOnClickListener(this.breadCrumbClickListener);
    	tv.setTextSize(20);		
    	if(category.getId() == 0){
    		tv.setText(this.getText(R.string.root_category_title).toString());
    	} else {
    		tv.setText("/" + category.getTitle());
    	}
		
    	this.llBreadCrumb.addView(tv);
    }
    
    private OnClickListener breadCrumbClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			if(llBreadCrumb.getChildCount() > 1){
				categoryId = Long.parseLong(v.getTag().toString());
				if(categoryId != Long.parseLong(llBreadCrumb.getChildAt(llBreadCrumb.getChildCount()-1).getTag().toString())){
					removeAfterId(categoryId);
				}
			}
		}
	};
	
	public void removeAfterId(long categoryId){
		
		boolean found = false;
		boolean moreCats = true;
		int pos = this.categoryIdTrail.size()-1;
		
		while(!found && moreCats){
			if(this.categoryIdTrail.get(pos) != categoryId){
				moreCats = showPreviousCategory(false);
			} else {
				moreCats = showPreviousCategory(true);
				found = true;
			}
			pos--;
		}
	}
    
    public void updateBreadCrumbView(final HorizontalScrollView wrapper){
    	wrapper.removeAllViews();
    	wrapper.addView(this.llBreadCrumb);
    	
    	Handler handler = new Handler();
    	Runnable r = new Runnable(){
    	    public void run(){
    	    	wrapper.fullScroll(HorizontalScrollView.FOCUS_RIGHT);                      
    	    }
    	};
    	handler.postDelayed(r, 100L);
    }
}


package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.smileyjoedev.genLibrary.Debug;

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
	private String searchTerm;
	private boolean onSearch;
	private Category selectedCategory;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.restoreSavedState(savedInstanceState);
		this.init();
		this.getCategories();
		this.adapter = new CategoryListAdapter(this.context, this.categories);
		
		this.populateBreadCrumb();
		setListAdapter(adapter);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putBoolean(Constants.EXTRA_ON_SEARCH, this.onSearch);
		
		outState.putString(Constants.EXTRA_SEARCH_TERM, this.searchTerm);
		outState.putLong(Constants.EXTRA_CATEGORY_ID, this.categoryId);
		
		outState.putSerializable(Constants.EXTRA_CATEGORY_ID_TRAIL, this.categoryIdTrail);
	}
	
	private void restoreSavedState(Bundle savedInstanceState){
		if(savedInstanceState != null){
			this.onSearch = savedInstanceState.getBoolean(Constants.EXTRA_ON_SEARCH);
			
			this.categoryId = savedInstanceState.getLong(Constants.EXTRA_CATEGORY_ID);
			this.searchTerm = savedInstanceState.getString(Constants.EXTRA_SEARCH_TERM);
			
			this.categoryIdTrail = (ArrayList<Long>) savedInstanceState.getSerializable(Constants.EXTRA_CATEGORY_ID_TRAIL);
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.restoreSavedState(savedInstanceState);
	    this.registerForContextMenu(this.getListView());
	}
	
	public void getCategories(){
		if(this.onSearch){
			this.categories = this.categoryAdapter.search(this.searchTerm);
		} else {
			this.categories = this.categoryAdapter.get(this.categoryId);
		}
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(this.onSearch){
			startActivity(Intents.categoryList(this.context, this.categories.get(position).getId()));
		} else {
			this.categoryIdTrail.add(this.categoryId);
			this.categoryId = this.categories.get(position).getId();
			this.category = this.categoryAdapter.getDetails(this.categoryId);
			this.addToBreadCrumb();
			this.updateView();
		}
		
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			CategoryDataInterface hostInterface;
	        hostInterface = (CategoryDataInterface) activity;
	        this.categoryId = hostInterface.getCategoryId();
	    } catch(ClassCastException e) {
	        this.categoryId = 0;
	    }
	    
	    try {
			SearchDataInterface hostInterface;
	        hostInterface = (SearchDataInterface) activity;
	        this.searchTerm = hostInterface.getSearchTerm();
	        this.onSearch = true;
	    } catch(ClassCastException e) {
	        this.searchTerm = "";
	        this.onSearch = false;
	    }
	    
	    
	    this.context = activity.getApplicationContext();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		
		this.selectedCategory = this.categories.get(info.position);
		Debug.d(this.selectedCategory);
		
		menu.setHeaderTitle(this.getString(R.string.context_heading));
		
		menu.add(Menu.NONE, Constants.CONTEXT_CATEGORY_EDIT, Constants.CONTEXT_CATEGORY_EDIT, this.getString(R.string.context_edit));
		menu.add(Menu.NONE, Constants.CONTEXT_CATEGORY_DELETE, Constants.CONTEXT_CATEGORY_DELETE, this.getString(R.string.context_delete));
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int menuItemIndex = item.getItemId();

		switch(menuItemIndex){
			case Constants.CONTEXT_CATEGORY_EDIT:
				startActivityForResult(Intents.categoryEdit(this.context, this.selectedCategory.getId()), Constants.ACTIVITY_CATEGORY_EDIT);
				break;
			case Constants.CONTEXT_CATEGORY_DELETE:
				startActivityForResult(Intents.popupDelete(this.context, Constants.CATEGORY), Constants.ACTIVITY_CATEGORY_POPUP_DELETE);
				break;
		}
		return super.onContextItemSelected(item);
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case Constants.ACTIVITY_CATEGORY_EDIT:
				this.updateView();
				break;
			case Constants.ACTIVITY_CATEGORY_POPUP_DELETE:
				if(resultCode == Activity.RESULT_OK){
					if(data.getBooleanExtra("result", false)){
						this.categoryAdapter.delete(this.selectedCategory);
						this.updateView();
					}
				}
				break;
		}
		
	}
	
	public void init(){
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
		Broadcast.categoryChanged(this.context, this.categoryId);
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
    
	public void updateSearchResults(String searchTerm){
		this.searchTerm = searchTerm;
		this.updateView();
	}
}


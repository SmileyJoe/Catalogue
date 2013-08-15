package com.smileyjoedev.catalogue.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;

import com.smileyjoedev.catalogue.Broadcast;
import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.GenViews;
import com.smileyjoedev.catalogue.Intents;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.TabListener;
import com.smileyjoedev.catalogue.fragments.CategoryListFragment;
import com.smileyjoedev.catalogue.fragments.ItemListFragment;
import com.smileyjoedev.catalogue.interfaces.CategoryDataInterface;
import com.smileyjoedev.genLibrary.Debug;

public class CategoryList extends Activity implements CategoryDataInterface {

	private long categoryId = 0;
	
    private BroadcastReceiver categoryChanged = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	categoryId = intent.getLongExtra(Constants.EXTRA_CATEGORY_ID, 0);
        	if(categoryId == 0){
        		getActionBar().getTabAt(0).setText("Categories");
        	} else {
        		getActionBar().getTabAt(0).setText("Sub-Categories");
        	}
        	
        	try{
        		getItemFragment().changeCategory(categoryId);
        	} catch(NullPointerException e){
        		e.printStackTrace();
        	}
        
        	updateBreadCrumb();
        	
        	Debug.d(categoryId);
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);
        GenViews.createBreadCrumbActionBar(this, getActionBar());
        
        try{
        	Bundle extras = getIntent().getExtras();
        	if(extras.containsKey(Constants.EXTRA_CATEGORY_ID)){
        		this.categoryId = extras.getLong(Constants.EXTRA_CATEGORY_ID);
        	} else {
        		this.categoryId = 0;
        	}
        } catch(NullPointerException e){
        	this.categoryId = 0;
        }
        
        this.init();
        this.setTabs();
	}
	
	private void init(){
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        
       	inflater.inflate(R.menu.category_list, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.menu_add_category:
        		startActivityForResult(Intents.categoryNew(this, this.categoryId), Constants.ACTIVITY_CATEGORY_NEW);
				return true;
			case android.R.id.home:
				this.onBackPressed();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }

    }
	
	public void setTabs(){
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
  	
	  	ActionBar.Tab newTab0 = getActionBar().newTab();
	  	if(this.categoryId == 0){
	  		newTab0.setText("Categories");
	  	} else {
	  		newTab0.setText("Sub-Categories");
	  	}
//	  	newTab0.setTabListener(this);
	  	newTab0.setTabListener(new TabListener<CategoryListFragment>(
                this, Integer.toString(Constants.TAB_CATEGORY), CategoryListFragment.class, null));
	  	newTab0.setTag(Constants.TAB_CATEGORY);
	  	ActionBar.Tab newTab1 = getActionBar().newTab();
	  	newTab1.setText("Items");
//	  	newTab1.setTabListener(this);
	  	newTab1.setTabListener(new TabListener<ItemListFragment>(
                this, Integer.toString(Constants.TAB_ITEM), ItemListFragment.class, null));
	  	newTab1.setTag(Constants.TAB_ITEM);
	  	getActionBar().addTab(newTab0);
	  	getActionBar().addTab(newTab1);
	  	
	}

	@Override
	public long getCategoryId() {
		return this.categoryId;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case Constants.ACTIVITY_CATEGORY_NEW:
				if(resultCode == Activity.RESULT_OK){
					ActionBar.Tab currentTab = getActionBar().getSelectedTab();
					Debug.d();
					Debug.d(currentTab.getText().toString());
					
					switch(Integer.parseInt(currentTab.getTag().toString())){
						case Constants.TAB_CATEGORY:
							CategoryListFragment categoryFragment = getCategoryFragment();
							categoryFragment.updateView();
							break;
					}
				} else {
				}
				break;
		}
	}
	
    @Override
    protected void onResume() {
        IntentFilter categoryChangedFilter = new IntentFilter();
        categoryChangedFilter.addAction(Broadcast.CATEGORY_CHANGED);
        registerReceiver(this.categoryChanged, categoryChangedFilter);
        this.updateBreadCrumb();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(this.categoryChanged);
        super.onPause();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putLong(Constants.EXTRA_CATEGORY_ID, this.categoryId);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	
    	if(savedInstanceState != null){
    		this.categoryId = savedInstanceState.getLong(Constants.EXTRA_CATEGORY_ID);
    	}
    }
    
    private CategoryListFragment getCategoryFragment(){
    	return (CategoryListFragment) getFragmentManager().findFragmentByTag(Integer.toString(Constants.TAB_CATEGORY));
    }
    
    private ItemListFragment getItemFragment() throws NullPointerException{
    	try{
    		return (ItemListFragment) getFragmentManager().findFragmentByTag(Integer.toString(Constants.TAB_ITEM));
    	} catch(NullPointerException e){
    		throw e;
    	}
    	
    }
    
    @Override
    public void onBackPressed() {
    	if(!getCategoryFragment().showPreviousCategory()){
    		super.onBackPressed();
    	}
    }
    
    private void updateBreadCrumb(){
    	try{
    		getCategoryFragment().updateBreadCrumbView((HorizontalScrollView) findViewById(R.id.hsv_actionbar_breadcrumb));
    	} catch(NullPointerException e) {
    		e.printStackTrace();
    	}    	
    }

}

package com.smileyjoedev.catalogue.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;

import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.Intents;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.Views;
import com.smileyjoedev.catalogue.db.DbCategoryAdapter;
import com.smileyjoedev.catalogue.fragments.CategoryListFragment;
import com.smileyjoedev.catalogue.interfaces.CategoryDataInterface;
import com.smileyjoedev.catalogue.objects.Category;
import com.smileyjoedev.genLibrary.KeyBoard;

public class CategorySelector extends Base implements OnClickListener, CategoryDataInterface {
	
	private HorizontalScrollView hsvBreadCrumb;
	private Button btSave;
	private Button btCancel;
	private DbCategoryAdapter categoryAdapter;
	private ArrayList<Category> categories;
	private Views views;
	private long categoryId;
	
	private BroadcastReceiver categoryChanged = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	categoryId = intent.getLongExtra(Constants.EXTRA_CATEGORY_ID, 0);
        	if(categoryId == 0){
        		showSave(false);
        	} else {
        		showSave(true);
        	}

        	
        	getCategoryListFragment().updateBreadCrumbView(hsvBreadCrumb);
        	
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_selector);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        KeyBoard.forceClose(getWindow());

        try{
        	Bundle extras = getIntent().getExtras();
    		this.categoryId = extras.getLong("cat_id");
        } catch(NullPointerException e){
        	this.categoryId = 0;
        }
        
        this.initialize();
        this.populateView();
    }
    
    @Override
    protected void onResume() {
    	Intents.filterCategoryChanged(this, this.categoryChanged);
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
    
    public void initialize(){
    	this.hsvBreadCrumb = (HorizontalScrollView) findViewById(R.id.hsv_bread_crumb);
    	
    	this.btSave = (Button) findViewById(R.id.bt_save);
    	this.btSave.setOnClickListener(this);
    	this.btCancel = (Button) findViewById(R.id.bt_cancel);
    	this.btCancel.setOnClickListener(this);
    	
    	this.categoryAdapter = new DbCategoryAdapter(this);
    	
    	this.categories = new ArrayList<Category>();
    	
    	this.views = new Views(this);
    	
    }
    
    public void populateView(){
    	this.categories = this.categoryAdapter.get(this.categoryId);
    	
    	if(this.categoryId == 0){
    		this.btSave.setVisibility(View.GONE);
    	}
    	
    	this.getCategoryListFragment().updateBreadCrumbView(this.hsvBreadCrumb);
    }

	@Override
	public void onClick(View v) {
		Intent resultIntent = new Intent();
		switch(v.getId()){
			case R.id.bt_save:
				resultIntent.putExtra("cat_id", this.categoryId);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
				break;
			case R.id.bt_cancel:
				setResult(Activity.RESULT_CANCELED, resultIntent);
				finish();
				break;
		}
		
	}
	
	public void showSave(boolean show){
		if(show){
			this.btSave.setVisibility(View.VISIBLE);
		} else {
			this.btSave.setVisibility(View.GONE);
		}
	}
	
	@Override
	public long getCategoryId() {
		return this.categoryId;
	}
	
	@Override
	public void onBackPressed() {
		if(!getCategoryListFragment().showPreviousCategory()){
			super.onBackPressed();
		}
	}
	
	private CategoryListFragment getCategoryListFragment(){
		return (CategoryListFragment) getFragmentManager().findFragmentById(R.id.frag_category_list);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case Constants.ACTIVITY_CATEGORY_NEW:
				if(resultCode == Activity.RESULT_OK){
					getCategoryListFragment().updateView();
				} else {
				}
				break;
		}
	}
	
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.menu_add_category:
        		startActivityForResult(Intents.categoryNew(this, this.categoryId), Constants.ACTIVITY_CATEGORY_NEW);
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        
       	inflater.inflate(R.menu.category_selector, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
	
}

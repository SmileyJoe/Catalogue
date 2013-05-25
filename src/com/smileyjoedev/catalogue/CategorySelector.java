package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.KeyBoard;

public class CategorySelector extends SherlockFragmentActivity implements OnClickListener, CategoryDataInterface {
	
	private HorizontalScrollView hsvBreadCrumb;
	private Button btSave;
	private Button btCancel;
	private DbCategoryAdapter categoryAdapter;
	private ArrayList<Category> categories;
	private Views views;
	private long catId;
	
	private BroadcastReceiver categoryChanged = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	catId = intent.getLongExtra(Constants.EXTRA_CATEGORY_ID, 0);
        	if(catId == 0){
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        KeyBoard.forceClose(getWindow());

        try{
        	Bundle extras = getIntent().getExtras();
    		this.catId = extras.getLong("cat_id");
        } catch(NullPointerException e){
        	this.catId = 0;
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
    	this.categories = this.categoryAdapter.get(this.catId);
    	
    	if(this.catId == 0){
    		this.btSave.setVisibility(View.GONE);
    	}
    	
    	this.getCategoryListFragment().updateBreadCrumbView(this.hsvBreadCrumb);
    }

	@Override
	public void onClick(View v) {
		Intent resultIntent = new Intent();
		switch(v.getId()){
			case R.id.bt_save:
				resultIntent.putExtra("cat_id", this.catId);
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
		return this.catId;
	}
	
	@Override
	public void onBackPressed() {
		if(!getCategoryListFragment().showPreviousCategory()){
			super.onBackPressed();
		}
	}
	
	private CategoryListFragment getCategoryListFragment(){
		return (CategoryListFragment) getSupportFragmentManager().findFragmentById(R.id.frag_category_list);
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
        		startActivityForResult(Intents.categoryNew(this, this.catId), Constants.ACTIVITY_CATEGORY_NEW);
				return true;
			case android.R.id.home:
				this.onBackPressed();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        
       	inflater.inflate(R.menu.category_selector, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
	
}

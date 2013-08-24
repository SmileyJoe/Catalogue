package com.smileyjoedev.catalogue.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.Intents;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.fragments.ItemViewFragment;
import com.smileyjoedev.catalogue.interfaces.ItemDataInterface;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.KeyBoard;

public class ItemView extends Base implements ItemDataInterface {

	private long itemId;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Debug.d("OnCreate");
        super.onCreate(savedInstanceState);
        this.initialize();
        setContentView(R.layout.item_view);
        KeyBoard.forceClose(getWindow());
        this.restoreSavedInstance(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        this.populateView();
    }
    
    @Override
    protected void onResume() {
    	Debug.d("OnResume");
    	this.initialize();
    	super.onResume();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	Debug.d("onRestoreInstanceState");
    	super.onRestoreInstanceState(savedInstanceState);
    	
    	this.restoreSavedInstance(savedInstanceState);
    }
    
    public void restoreSavedInstance(Bundle savedInstanceState){
    	if(savedInstanceState != null){
    		
    	}
    }
    
    private void initialize(){
    	Debug.d("Initialize");
    	try{
        	Bundle extras = getIntent().getExtras();
        	if(extras.containsKey(Constants.EXTRA_ITEM_ID)){
        		this.itemId = extras.getLong(Constants.EXTRA_ITEM_ID);
        		Debug.d("Found", itemId);
        	} else {
        		Debug.d("not found");
        		this.itemId = 0;
        	}
        } catch(NullPointerException e){
        	Debug.d("Caught");
        	this.itemId = 0;
        }
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        
       	inflater.inflate(R.menu.item_view, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.menu_edit_item:
	        	startActivityForResult(Intents.itemEdit(this, this.itemId), Constants.ACTIVITY_ITEM_EDIT);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }

    }
    
    private void populateView(){
    }
	
	@Override
	public long getItemId() {
		Debug.d("Get id");
		return this.itemId;
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		switch(arg0){
			case Constants.ACTIVITY_ITEM_EDIT:
				Debug.d("On result item edit");
				getItemFragment().updateView();
				break;
		}
	}
	
    private ItemViewFragment getItemFragment() throws NullPointerException{
    	try{
    		return (ItemViewFragment) getFragmentManager().findFragmentByTag(Integer.toString(Constants.TAB_ITEM));
    	} catch(NullPointerException e){
    		throw e;
    	}
    	
    }
	
}

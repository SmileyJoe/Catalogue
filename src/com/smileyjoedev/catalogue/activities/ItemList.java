package com.smileyjoedev.catalogue.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.Intents;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.R.id;
import com.smileyjoedev.catalogue.R.layout;
import com.smileyjoedev.catalogue.R.menu;
import com.smileyjoedev.catalogue.fragments.ItemListFragment;
import com.smileyjoedev.genLibrary.Debug;

public class ItemList extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.init();
	}
	
	private void init(){
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        
       	inflater.inflate(R.menu.item_list, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.menu_add_item:
        		startActivityForResult(Intents.itemAdd(this), Constants.ACTIVITY_ITEM_NEW);
				return true;
			case android.R.id.home:
				this.onBackPressed();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }

    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
			case Constants.ACTIVITY_ITEM_NEW:
				if(resultCode == Activity.RESULT_OK){
					getItemListFragment().updateView();
				} else {
				}
				break;
		}
	}
	
	private ItemListFragment getItemListFragment(){
		return (ItemListFragment) getSupportFragmentManager().findFragmentById(R.id.frag_item_list);
	}
	
}

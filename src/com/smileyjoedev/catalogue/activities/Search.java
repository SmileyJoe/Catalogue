package com.smileyjoedev.catalogue.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.TabListener;
import com.smileyjoedev.catalogue.fragments.CategoryListFragment;
import com.smileyjoedev.catalogue.fragments.ItemListFragment;
import com.smileyjoedev.catalogue.fragments.LocationListFragment;
import com.smileyjoedev.catalogue.interfaces.SearchDataInterface;

public class Search extends Activity implements SearchDataInterface{

	private String searchTerm;
	private SearchView etSearch;
	
	private TextWatcher searchTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,int count) {
        	setSearchTerm();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        this.init();
        this.setTabs();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putString(Constants.EXTRA_SEARCH_TERM, this.searchTerm);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		if(savedInstanceState != null){
			this.searchTerm = savedInstanceState.getString(Constants.EXTRA_SEARCH_TERM);
		}
	}
	
	private void init(){
		this.searchTerm = "";
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        
       	inflater.inflate(R.menu.search, menu);
       	
       	this.etSearch = (SearchView) menu.findItem(R.id.menu_search).getActionView();
       	this.etSearch.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				searchTerm = newText;
				try{
					getItemFragment().updateSearchResults(searchTerm);
				} catch (NullPointerException e){
					e.printStackTrace();
				}
				
				try{
					getCategoryFragment().updateSearchResults(searchTerm);
				} catch (NullPointerException e){
					e.printStackTrace();
				}
				
				try{
					getLocationFragment().updateSearchResults(searchTerm);
				} catch (NullPointerException e){
					e.printStackTrace();
				}
				return false;
			}
		});

        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setOnActionExpandListener(new OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
//            	setSearchTerm();
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                etSearch.clearFocus();
                return true; // Return true to expand action view
            }
        });
        
        return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//        	case R.id.menu_add_category:
//        		startActivityForResult(Intents.categoryNew(this, this.categoryId), Constants.ACTIVITY_CATEGORY_NEW);
//				return true;
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
  		newTab0.setText("Items");
//	  	newTab0.setTabListener(this);
	  	newTab0.setTabListener(new TabListener<ItemListFragment>(
                this, Integer.toString(Constants.TAB_ITEM), ItemListFragment.class, null));
	  	newTab0.setTag(Constants.TAB_ITEM);
	  	
	  	ActionBar.Tab newTab1 = getActionBar().newTab();
	  	newTab1.setText("Categories");
//	  	newTab1.setTabListener(this);
	  	newTab1.setTabListener(new TabListener<CategoryListFragment>(
                this, Integer.toString(Constants.TAB_CATEGORY), CategoryListFragment.class, null));
	  	newTab1.setTag(Constants.TAB_ITEM);
	  	
	  	ActionBar.Tab newTab2 = getActionBar().newTab();
	  	newTab2.setText("Locations");
//	  	newTab1.setTabListener(this);
	  	newTab2.setTabListener(new TabListener<LocationListFragment>(
                this, Integer.toString(Constants.TAB_LOCATION), LocationListFragment.class, null));
	  	newTab2.setTag(Constants.TAB_LOCATION);
	  	
	  	getActionBar().addTab(newTab0);
	  	getActionBar().addTab(newTab1);
	  	getActionBar().addTab(newTab2);
	}
	
	@Override
	public String getSearchTerm() {
		return this.searchTerm;
	}
	
	public void setSearchTerm(){
//		this.searchTerm = this.etSearch.getText().toString();
//		Debug.d("SearchTerm: " + this.searchTerm);
	}
	
    private ItemListFragment getItemFragment() throws NullPointerException{
    	try{
    		return (ItemListFragment) getFragmentManager().findFragmentByTag(Integer.toString(Constants.TAB_ITEM));
    	} catch(NullPointerException e){
    		throw e;
    	}
    	
    }
    
    private CategoryListFragment getCategoryFragment() throws NullPointerException{
    	try{
    		return (CategoryListFragment) getFragmentManager().findFragmentByTag(Integer.toString(Constants.TAB_CATEGORY));
    	} catch(NullPointerException e){
    		throw e;
    	}
    	
    }
    
    private LocationListFragment getLocationFragment() throws NullPointerException{
    	try{
    		return (LocationListFragment) getFragmentManager().findFragmentByTag(Integer.toString(Constants.TAB_LOCATION));
    	} catch(NullPointerException e){
    		throw e;
    	}
    	
    }

}
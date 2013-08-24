package com.smileyjoedev.catalogue;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public class BaseNav extends Activity {

	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;

	@Override
	public void setContentView(int layoutResId) {
		super.setContentView(R.layout.base_nav);

		View content = getLayoutInflater().inflate(layoutResId, null);

		((FrameLayout) findViewById(R.id.fl_content)).addView(content);

		initNav();
	}

	private void initNav() {
		this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.drawerList = (ListView) findViewById(R.id.lv_nav_drawer);

		this.drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

			private String title;
			
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(this.title);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				this.title = getActionBar().getTitle().toString();
				getActionBar().setTitle("Menu");
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// Set the drawer toggle as the DrawerListener
		this.drawerLayout.setDrawerListener(this.drawerToggle);
		this.drawerToggle.setDrawerIndicatorEnabled(false);

	}
	
	public void enableDrawer(){
		this.drawerToggle.setDrawerIndicatorEnabled(true);
	}
	
	public void setNavAdapter(ListAdapter adapter){
		drawerList.setAdapter(adapter);
	}
	
	public void setNavClickListener(ListView.OnItemClickListener listener){
		drawerList.setOnItemClickListener(listener);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		this.drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (this.drawerToggle.onOptionsItemSelected(item)) {
			return true;
		} else {
			if(!this.drawerToggle.isDrawerIndicatorEnabled()){
				switch (item.getItemId()) {
					case android.R.id.home:
						finish();
						return true;
					default:
						return super.onOptionsItemSelected(item);
		        }
			}
			
		}
		
		return super.onOptionsItemSelected(item);

		
	}
	
	public void hideNav(){
		this.drawerLayout.closeDrawer(this.drawerList);
	}
	
	public void showNav(){
		this.drawerLayout.openDrawer(this.drawerList);
	}

}

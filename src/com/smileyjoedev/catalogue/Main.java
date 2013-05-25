package com.smileyjoedev.catalogue;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends SherlockActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.initialize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
	}
	
	private void initialize(){
		Button btCategoryList = (Button) findViewById(R.id.bt_category_list);
		btCategoryList.setOnClickListener(this);
		
		Button btLocationList = (Button) findViewById(R.id.bt_location_list);
		btLocationList.setOnClickListener(this);
		
		Button btItemList = (Button) findViewById(R.id.bt_item_list);
		btItemList.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.bt_category_list:
				startActivity(Intents.categoryList(this));
				break;
			case R.id.bt_location_list:
				startActivity(Intents.locationList(this));
				break;
			case R.id.bt_item_list:
				startActivity(Intents.itemList(this));
				break;
		}
		
	}

}

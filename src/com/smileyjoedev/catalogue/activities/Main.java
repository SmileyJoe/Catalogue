package com.smileyjoedev.catalogue.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.Intents;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.R.id;
import com.smileyjoedev.catalogue.R.layout;
import com.smileyjoedev.catalogue.R.menu;
import com.smileyjoedev.catalogue.db.DbBarcodeAdapter;
import com.smileyjoedev.catalogue.db.DbCategoryAdapter;
import com.smileyjoedev.catalogue.db.DbItemAdapter;
import com.smileyjoedev.catalogue.db.DbLocationAdapter;
import com.smileyjoedev.catalogue.objects.Barcode;
import com.smileyjoedev.catalogue.objects.Category;
import com.smileyjoedev.catalogue.objects.Item;
import com.smileyjoedev.catalogue.objects.Location;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.ZXing.IntentIntegrator;
import com.smileyjoedev.genLibrary.ZXing.IntentResult;

public class Main extends Base implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.enableDrawer();
		this.initialize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void initialize() {
		Button btCategoryList = (Button) findViewById(R.id.bt_category_list);
		btCategoryList.setOnClickListener(this);

		Button btLocationList = (Button) findViewById(R.id.bt_location_list);
		btLocationList.setOnClickListener(this);

		Button btItemList = (Button) findViewById(R.id.bt_item_list);
		btItemList.setOnClickListener(this);

		Button btSearch = (Button) findViewById(R.id.bt_search);
		btSearch.setOnClickListener(this);

		Button btScan = (Button) findViewById(R.id.bt_scan);
		btScan.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_category_list:
				startActivity(Intents.categoryList(this));
				break;
			case R.id.bt_location_list:
				startActivity(Intents.locationList(this));
				break;
			case R.id.bt_item_list:
				startActivity(Intents.itemList(this));
				break;
			case R.id.bt_search:
				startActivity(Intents.search(this));
				break;
			case R.id.bt_scan:
				Debug.d("Scan clicked");
				Intents.scanBarcode(Main.this);
				break;
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			DbBarcodeAdapter barcodeAdapter = new DbBarcodeAdapter(this);
			String barcodeId = scanResult.getContents();
			
			Barcode barcode = barcodeAdapter.getDetailsByBarcodeId(barcodeId);
			
			switch(barcode.getRelTypeId()){
				case Constants.ITEM:
					DbItemAdapter itemAdapter = new DbItemAdapter(this);
					Item item = itemAdapter.getDetails(barcode.getRelId());
					startActivity(Intents.itemView(this, item.getId()));
					break;
				case Constants.CATEGORY:
					DbCategoryAdapter categoryAdapter = new DbCategoryAdapter(this);
					Category category = categoryAdapter.getDetails(barcode.getRelId());
					startActivity(Intents.categoryList(this, category.getId()));
					break;
				case Constants.LOCATION:
					DbLocationAdapter locationAdapter = new DbLocationAdapter(this);
					Location location = locationAdapter.getDetails(barcode.getRelId());
					startActivity(Intents.locationList(this, location.getId()));
					break;
			}
			
			Debug.d("Scan code", barcodeId);
		} else {
			Debug.d("Scan failed");
		}
	}

}

package com.smileyjoedev.catalogue.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.smileyjoedev.catalogue.Broadcast;
import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.db.DbCategoryAdapter;
import com.smileyjoedev.catalogue.db.DbItemAdapter;
import com.smileyjoedev.catalogue.db.DbLocationAdapter;
import com.smileyjoedev.catalogue.db.DbNfcAdapter;
import com.smileyjoedev.catalogue.objects.Category;
import com.smileyjoedev.catalogue.objects.Item;
import com.smileyjoedev.catalogue.objects.Location;
import com.smileyjoedev.catalogue.objects.Nfc;
import com.smileyjoedev.genLibrary.Debug;

public class ReadNfcActivity extends Base implements OnClickListener {

	private Button btSave;
	private Button btCancel;
	private TextView tvNfcId;
	private Nfc nfc;
	private DbNfcAdapter nfcAdapter;
	private boolean isFree;
	
    private BroadcastReceiver nfcTagRead = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        	String nfcId = intent.getStringExtra(Constants.EXTRA_NFC_ID);
        	nfc = nfcAdapter.getDetailsByTagId(nfcId);
        	
        	if(nfc.getTagId().equals("")){
        		nfc.setTagId(nfcId);
        		isFree = true;
        	} else {
        		isFree = false;
        	}
        	
        	populateView();
        	Debug.d("Received ID", nfcId);
        	
        }
    };
    
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_nfc);
		
		this.initialize();
		
		this.populateView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Debug.d("onResume");
		
		this.registerNfcReceiver();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Debug.d("onPause");
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		Debug.d("onDestroy");
		
		this.unregisterNfcReceiver();
	}
	
	private void initialize(){
		this.tvNfcId = (TextView) findViewById(R.id.tv_nfc_id);
		this.btCancel = (Button) findViewById(R.id.bt_cancel);
		this.btSave = (Button) findViewById(R.id.bt_save);
		
		this.btCancel.setOnClickListener(this);
		this.btSave.setOnClickListener(this);
		
		this.nfc = new Nfc();
		this.nfcAdapter = new DbNfcAdapter(this);
		
		this.isFree = true;
	}
	
	public void populateView(){
		if(!this.isFree){
			TextView tvRelTypeTitle = (TextView) findViewById(R.id.tv_rel_type_title);
			TextView tvRelTitle = (TextView) findViewById(R.id.tv_rel_title);
			final HorizontalScrollView hsvBreadcrumb = (HorizontalScrollView) findViewById(R.id.hsv_breadcrumb);
			TextView tvBreadcrumb = (TextView) findViewById(R.id.tv_breadcrumb);
			
			Handler handler = new Handler();
	    	Runnable r = new Runnable(){
	    	    public void run(){
	    	    	hsvBreadcrumb.fullScroll(HorizontalScrollView.FOCUS_RIGHT);                      
	    	    }
	    	};
			
			findViewById(R.id.ll_tag_db_details).setVisibility(View.VISIBLE);
			
			switch(this.nfc.getRelTypeId()){
				case Constants.ITEM:
					DbItemAdapter itemAdapter = new DbItemAdapter(this);
					Item item = itemAdapter.getDetails(this.nfc.getRelId());
					hsvBreadcrumb.setVisibility(View.GONE);
					tvRelTitle.setVisibility(View.VISIBLE);
					tvRelTitle.setText(item.getTitle());
					tvRelTypeTitle.setText(getResources().getString(R.string.tv_type_title_item));
					break;
				case Constants.CATEGORY:
					DbCategoryAdapter categoryAdapter = new DbCategoryAdapter(this);
					Category category = categoryAdapter.getDetails(this.nfc.getRelId());
					tvRelTitle.setVisibility(View.GONE);
					hsvBreadcrumb.setVisibility(View.VISIBLE);
					tvBreadcrumb.setText(category.getBreadCrumb(this));
					tvRelTypeTitle.setText(getResources().getString(R.string.tv_type_title_category));
			    	handler.postDelayed(r, 100L);
					break;
				case Constants.LOCATION:
					DbLocationAdapter locationAdapter = new DbLocationAdapter(this);
					Location location = locationAdapter.getDetails(this.nfc.getRelId());
					tvRelTitle.setVisibility(View.GONE);
					hsvBreadcrumb.setVisibility(View.VISIBLE);
					tvBreadcrumb.setText(location.getBreadCrumb(this));
					tvRelTypeTitle.setText(getResources().getString(R.string.tv_type_title_location));
			    	handler.postDelayed(r, 100L);
					break;
			}
		} else {
			findViewById(R.id.ll_tag_db_details).setVisibility(View.GONE);
		}
		
		this.tvNfcId.setText(nfc.getTagId());
	}

	public void onClick(View v) {
		Intent resultIntent = new Intent();
		if(v.getId() == R.id.bt_save){
			if(!this.nfc.getTagId().equals("")){
				resultIntent.putExtra("result", true);
				resultIntent.putExtra(Constants.EXTRA_NFC_ID, this.nfc.getTagId());
				resultIntent.putExtra(Constants.EXTRA_NFC_IS_FREE, this.isFree);
			} else {
				resultIntent.putExtra("result", false);
			}
			
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}else{
			if(v.getId() == R.id.bt_cancel){
				resultIntent.putExtra("result", false);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		}
	}
    
    public void registerNfcReceiver(){
    	Debug.d("this is registered");
    	IntentFilter filter = new IntentFilter();
        filter.addAction(Broadcast.NFC_SCANNED);
        registerReceiver(this.nfcTagRead, filter);
    }
    
    public void unregisterNfcReceiver(){
    	Debug.d("this is not registered");
    	unregisterReceiver(this.nfcTagRead);
    }
	
}

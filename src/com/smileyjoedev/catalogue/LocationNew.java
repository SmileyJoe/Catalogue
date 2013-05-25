package com.smileyjoedev.catalogue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.smileyjoedev.genLibrary.Notify;

public class LocationNew extends SherlockActivity implements OnClickListener {

	private Button btSave;
	private Button btCancel;
	private EditText etLocationTitle;
	private DbLocationAdapter locationAdapter;
	private long locationId;
	private Location location;
	private boolean isEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.location_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.init();
        this.populateView();
	}
	
	private void init(){
		this.btSave = (Button) findViewById(R.id.bt_save);
		this.btSave.setOnClickListener(this);
		this.btCancel = (Button) findViewById(R.id.bt_cancel);
		this.btCancel.setOnClickListener(this);
		this.etLocationTitle = (EditText) findViewById(R.id.et_new_location);
		this.locationAdapter = new DbLocationAdapter(this);
		
		try{
        	Bundle extras = getIntent().getExtras();
    		this.locationId = extras.getLong(Constants.EXTRA_LOCATION_ID);
    		this.isEdit = extras.getBoolean(Constants.EXTRA_IS_EDIT);
    		if(this.isEdit){
    			this.location = this.locationAdapter.getDetails(this.locationId);
    		} else {
    			this.location = new Location();
    		}
        } catch(NullPointerException e){
        	this.locationId = 0;
        	this.isEdit = false;
        	this.location = new Location();
        }
	}
	
	private void populateView(){
		this.etLocationTitle.setText(this.location.getTitle());
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bt_save:
			String title = this.etLocationTitle.getText().toString().trim();
			if(!title.equals("")){
				this.location.setTitle(this.etLocationTitle.getText().toString().trim());
				if(!this.isEdit){
					this.location.setParentId(this.locationId);
				}
				this.locationAdapter.save(this.location);
				Intent resultIntent = new Intent();
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			} else {
				Notify.toast(this, R.string.toast_invalid_location_title);
			}
			break;
		case R.id.bt_cancel:
			finish();
			break;
		}
	}
	
}

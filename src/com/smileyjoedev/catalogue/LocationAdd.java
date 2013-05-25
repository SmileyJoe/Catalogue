package com.smileyjoedev.catalogue;

import com.smileyjoedev.genLibrary.KeyBoard;
import com.smileyjoedev.genLibrary.Notify;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LocationAdd extends Activity implements OnClickListener {
	
	private long locId;
	private Location location;
	private DbLocationAdapter locationAdapter;
	private EditText etLocationTitle;
	private Button btSave;
	private Button btCancel;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_add);
        KeyBoard.forceClose(getWindow());
        
        try{
        	Bundle extras = getIntent().getExtras();
    		this.locId = extras.getLong("loc_id");
        } catch(NullPointerException e){
        	this.locId = 0;
        	Notify.toast(this, R.string.toast_generic_error);
        	this.btCancel.performClick();
        }
        
        this.initialize();
        this.populateView();
    }
    
    private void initialize(){
    	this.location = new Location();
    	this.locationAdapter = new DbLocationAdapter(this);
    	
    	this.etLocationTitle = (EditText) findViewById(R.id.et_location_title);
    	
    	this.btCancel = (Button) findViewById(R.id.bt_cancel);
    	this.btCancel.setOnClickListener(this);
    	this.btSave = (Button) findViewById(R.id.bt_save);
    	this.btSave.setOnClickListener(this);
    }
    
    private void populateView(){
    	this.location = this.locationAdapter.getDetails(this.locId);
    	this.etLocationTitle.setText(this.location.getTitle());
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.bt_cancel:
				finish();
				break;
			case R.id.bt_save:
				this.location.setTitle(this.etLocationTitle.getText().toString());
				this.locationAdapter.update(this.location);
				finish();
				break;
		}
		
	}
	
}
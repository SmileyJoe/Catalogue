package com.smileyjoedev.catalogue.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.Intents;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.db.DbBarcodeAdapter;
import com.smileyjoedev.catalogue.db.DbLocationAdapter;
import com.smileyjoedev.catalogue.db.DbNfcAdapter;
import com.smileyjoedev.catalogue.objects.Barcode;
import com.smileyjoedev.catalogue.objects.Location;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.Notify;
import com.smileyjoedev.genLibrary.ZXing.IntentIntegrator;
import com.smileyjoedev.genLibrary.ZXing.IntentResult;

public class LocationNew extends Activity implements OnClickListener {

	private Button btSave;
	private Button btCancel;
	private EditText etLocationTitle;
	private DbLocationAdapter locationAdapter;
	private long locationId;
	private Location location;
	private boolean isEdit;
	private TextView tvTagExistsWarning;
	private Button btAddNfcTag;
	private TextView tvNfcId;
	private Button btAddBarcode;
	private TextView tvBarcodeId;
	private TextView tvBarcodeExistsWarning;
	private TextView tvBarcodeNotFoundWarning;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.location_new);
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
        
        this.btAddNfcTag = (Button) findViewById(R.id.bt_add_nfc_id);
    	this.btAddNfcTag.setOnClickListener(this);
    	
    	this.tvNfcId = (TextView) findViewById(R.id.tv_nfc_id);
    	
    	this.tvTagExistsWarning = (TextView) findViewById(R.id.tv_nfc_tag_exists_warning);
    	
    	this.btAddBarcode = (Button) findViewById(R.id.bt_add_barcode_id);
    	this.btAddBarcode.setOnClickListener(this);
    	this.tvBarcodeId = (TextView) findViewById(R.id.tv_barcode_id);
    	this.tvBarcodeExistsWarning = (TextView) findViewById(R.id.tv_barcode_exists_warning);
    	this.tvBarcodeNotFoundWarning = (TextView) findViewById(R.id.tv_barcode_not_found_warning);
	}
	
	private void populateView(){
		this.etLocationTitle.setText(this.location.getTitle());
		this.tvNfcId.setText(this.location.getNfc().getTagId());
		this.tvBarcodeId.setText(this.location.getBarcode().getBarcodeId());
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
		case R.id.bt_add_nfc_id:
			startActivityForResult(Intents.readNfc(this), Constants.ACTIVITY_READ_NFC);
			break;
		case R.id.bt_cancel:
			finish();
			break;
		case R.id.bt_add_barcode_id:
			Intents.scanBarcode(LocationNew.this);
			break;
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Debug.d("onActivityResult");
		switch(requestCode){
//			case Constants.ACTIVITY_CATEGORY_SELECTOR:
//				if(resultCode == Activity.RESULT_OK){
//					if(data.hasExtra("cat_id")){
//						Category cat = new Category();
//						cat = this.categoryAdapter.getDetails(data.getLongExtra("cat_id", 0));
//						this.item.addCategory(cat);
//						this.populateCategories();
//					}
//				}
//				break;
			case Constants.ACTIVITY_READ_NFC:
				if(resultCode == Activity.RESULT_OK){
					if(data.hasExtra(Constants.EXTRA_NFC_ID)){
						String nfcId = data.getStringExtra(Constants.EXTRA_NFC_ID);
						boolean isFree = data.getBooleanExtra(Constants.EXTRA_NFC_IS_FREE, true);
						this.tvNfcId.setText(nfcId);
						
						if(!isFree){
							DbNfcAdapter nfcAdapter = new DbNfcAdapter(this);
							this.location.setNfc(nfcAdapter.getDetailsByTagId(nfcId));
							this.tvTagExistsWarning.setVisibility(View.VISIBLE);
						} else {
							this.location.getNfc().setTagId(nfcId);
							this.tvTagExistsWarning.setVisibility(View.GONE);
						}
						Debug.d("Activity result nfc id", nfcId);
					}
				}
				break;
			case IntentIntegrator.REQUEST_CODE:
				IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
				if (scanResult != null) {
					String barcodeId = scanResult.getContents();
					DbBarcodeAdapter barcodeAdapter = new DbBarcodeAdapter(this);
					Barcode barcode = barcodeAdapter.getDetailsByBarcodeId(barcodeId);
					
					if(barcode.exists()){
						this.location.setBarcode(barcode);
						this.tvBarcodeExistsWarning.setVisibility(View.VISIBLE);
					} else {
						this.location.getBarcode().setBarcodeId(barcodeId);
						this.tvBarcodeExistsWarning.setVisibility(View.GONE);
					}
					
					this.tvBarcodeId.setText(barcodeId);
					this.tvBarcodeNotFoundWarning.setVisibility(View.GONE);
					Debug.d("Scan found: ", barcodeId);
				} else {
					this.tvBarcodeNotFoundWarning.setVisibility(View.VISIBLE);
					Debug.d("Scan failed");
				}
				
				break;
		}
	}
	
}

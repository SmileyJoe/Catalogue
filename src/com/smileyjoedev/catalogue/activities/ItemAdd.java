package com.smileyjoedev.catalogue.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.smileyjoedev.catalogue.Constants;
import com.smileyjoedev.catalogue.Intents;
import com.smileyjoedev.catalogue.R;
import com.smileyjoedev.catalogue.R.id;
import com.smileyjoedev.catalogue.R.layout;
import com.smileyjoedev.catalogue.R.menu;
import com.smileyjoedev.catalogue.R.xml;
import com.smileyjoedev.catalogue.db.DbBarcodeAdapter;
import com.smileyjoedev.catalogue.db.DbCategoryAdapter;
import com.smileyjoedev.catalogue.db.DbItemAdapter;
import com.smileyjoedev.catalogue.db.DbLocationAdapter;
import com.smileyjoedev.catalogue.db.DbNfcAdapter;
import com.smileyjoedev.catalogue.objects.Barcode;
import com.smileyjoedev.catalogue.objects.Category;
import com.smileyjoedev.catalogue.objects.Item;
import com.smileyjoedev.catalogue.objects.Location;
import com.smileyjoedev.genLibrary.Camera;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.GeneralViews;
import com.smileyjoedev.genLibrary.KeyBoard;
import com.smileyjoedev.genLibrary.TimeStamp;
import com.smileyjoedev.genLibrary.ZXing.IntentIntegrator;
import com.smileyjoedev.genLibrary.ZXing.IntentResult;

public class ItemAdd extends SherlockActivity implements OnClickListener {
	
	private Item item;
	private DbItemAdapter itemAdapter;
	private DbCategoryAdapter categoryAdapter;
	private DbLocationAdapter locationAdapter;
	private DbBarcodeAdapter barcodeAdapter;
	private boolean edit;
	private EditText etTitle;
	private EditText etDescription;
	private Button btAddCategory;
	private Button btAddLocation;
	private Button btSave;
	private Button btCancel;
	private Button btTakePhoto;
	private Button btAddNfcTag;
	private Button btAddBarcode;
	private TextView tvNfcId;
	private TextView tvBarcodeId;
	private ImageView ivItemPhoto;
	private LinearLayout llItemCategories;
	private LinearLayout llItemLocations;
	private TextView tvItemQuantity;
	private long totalQuantity;
	private GeneralViews genViews;
//	private ArrayList<EditText> etsLocQuan;
	private Camera camera;
	private TextView tvTagExistsWarning;
	private TextView tvBarcodeExistsWarning;
	private TextView tvBarcodeNotFoundWarning;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Debug.d("OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_add);
        KeyBoard.forceClose(getWindow());
        this.restoreSavedInstance(savedInstanceState);
        this.initialize();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try{
        	Bundle extras = getIntent().getExtras();
        	if(extras.containsKey("item_id")){
        		this.item = this.itemAdapter.getDetails(extras.getLong("item_id"));
        		this.edit = true;
        		this.camera.setName(Long.toString(this.item.getId()));
        		Debug.i(this.item);
        	} else {
        		
        	}
        } catch(NullPointerException e){
        	
        }
        
        if(this.camera.isTempExists()){
			this.camera.deleteTempPhoto();
		}
        
        this.populateView();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	Debug.d("onSaveInstanceState");
    	super.onSaveInstanceState(outState);
    	
    	Gson gson = new Gson();
    	
    	Debug.d(gson.toJson(item));
    	
    	outState.putString(Constants.EXTRA_ITEM, gson.toJson(item));
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	Debug.d("onRestoreInstanceState");
    	super.onRestoreInstanceState(savedInstanceState);
    	
    	this.restoreSavedInstance(savedInstanceState);
    }
    
    public void restoreSavedInstance(Bundle savedInstanceState){
    	if(savedInstanceState != null){
    		Gson gson = new Gson();
    		
    		if(this.item == null){
    			this.item = (Item) gson.fromJson(savedInstanceState.getString(Constants.EXTRA_ITEM), Item.class);
    			Debug.d(this.item.toString());
    		}
    		
    	}
    }
    
    private void initialize(){
    	Debug.d("initialize");
    	if(this.item == null){
    		this.item = new Item();
    	}
    	
		this.edit = false;
    	this.itemAdapter = new DbItemAdapter(this);
    	this.categoryAdapter = new DbCategoryAdapter(this);
    	this.locationAdapter = new DbLocationAdapter(this);
    	this.barcodeAdapter = new DbBarcodeAdapter(this);
    	this.edit = false;
    	
    	this.etTitle = (EditText) findViewById(R.id.et_item_title);
    	this.etDescription = (EditText) findViewById(R.id.et_item_description);
    	
    	this.btAddCategory = (Button) findViewById(R.id.bt_add_category);
    	this.btAddCategory.setOnClickListener(this);
    	this.btAddLocation = (Button) findViewById(R.id.bt_add_location);
    	this.btAddLocation.setOnClickListener(this);
    	this.btSave = (Button) findViewById(R.id.bt_save);
    	this.btSave.setOnClickListener(this);
    	this.btCancel = (Button) findViewById(R.id.bt_cancel);
    	this.btCancel.setOnClickListener(this);
    	this.btTakePhoto = (Button) findViewById(R.id.bt_take_photo);
    	this.btTakePhoto.setOnClickListener(this);
    	this.btAddNfcTag = (Button) findViewById(R.id.bt_add_nfc_id);
    	this.btAddNfcTag.setOnClickListener(this);
    	this.btAddBarcode = (Button) findViewById(R.id.bt_add_barcode_id);
    	this.btAddBarcode.setOnClickListener(this);
    	
    	this.ivItemPhoto = (ImageView) findViewById(R.id.iv_photo);
    	
    	this.llItemCategories = (LinearLayout) findViewById(R.id.ll_item_categories);
    	this.llItemLocations = (LinearLayout) findViewById(R.id.ll_item_locations);
    	
    	this.tvItemQuantity = (TextView) findViewById(R.id.tv_item_quantity);
    	
    	this.tvNfcId = (TextView) findViewById(R.id.tv_nfc_id);
    	this.tvBarcodeId = (TextView) findViewById(R.id.tv_barcode_id);
    	
    	this.genViews = new GeneralViews(this);
    	
//    	this.etsLocQuan = new ArrayList<EditText>();
    	
    	this.camera = new Camera(Constants.PHOTO_PATH);
    	
    	this.totalQuantity = 0;
    	
    	this.tvTagExistsWarning = (TextView) findViewById(R.id.tv_nfc_tag_exists_warning);
    	this.tvBarcodeExistsWarning = (TextView) findViewById(R.id.tv_barcode_exists_warning);
    	this.tvBarcodeNotFoundWarning = (TextView) findViewById(R.id.tv_barcode_not_found_warning);
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	Debug.d("onCreateOptionsMenu");
        MenuInflater inflater = getSupportMenuInflater();
        
       	inflater.inflate(R.menu.item_add, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	Debug.d("onOptionsItemSelected");
        switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }

    }
    
    private void populateView(){
    	Debug.d("populateView");
    	this.etTitle.setText(this.item.getTitle());
    	this.etDescription.setText(this.item.getDesc());
    	this.tvNfcId.setText(this.item.getNfc().getTagId());
    	this.tvBarcodeId.setText(this.item.getBarcode().getBarcodeId());
    	
    	if(this.camera.isPhotoExists()){
    		this.camera.showPhoto(this.ivItemPhoto);
    	}
    	
    	this.populateCategories();
    	this.populateLocations();
    }
    
    public void populateCategories(){
    	Debug.d("populateCategories");
    	
    	this.llItemCategories.removeAllViews();
    	
    	for(int i = 0; i < this.item.getCategories().size(); i++){
    		if(!this.item.getCategories().get(i).getTitle().equals("")){
    			final int position = i;
				
				LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.xml.item_edit_category_row, null);
				
				TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
				final HorizontalScrollView hsvBreadcrumb = (HorizontalScrollView) view.findViewById(R.id.hsv_breadcrumb);
				ImageView ivCategoryDelete = (ImageView) view.findViewById(R.id.iv_category_delete);
				
				tvTitle.setText(this.item.getCategories().get(i).getBreadCrumb(this));
				
				Handler handler = new Handler();
		    	Runnable r = new Runnable(){
		    	    public void run(){
		    	    	hsvBreadcrumb.fullScroll(HorizontalScrollView.FOCUS_RIGHT);                      
		    	    }
		    	};
		    	handler.postDelayed(r, 100L);
		    	
	    		ivCategoryDelete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						item.getCategories().remove(position);
						populateCategories();
					}
					
				});
		    	
	    		this.llItemCategories.addView(view);
    		}
		}
    	
    }
    
    public void populateLocations(){
    	Debug.d("populateLocations");
    	this.totalQuantity = 0;
    	this.llItemLocations.removeAllViews();
//    	this.etsLocQuan = new ArrayList<EditText>();
    	
    	for(int i = 0; i < this.item.getLocations().size(); i++){
    		if(!this.item.getLocations().get(i).getLocation().getTitle().equals("")){
    			final int position = i;
				totalQuantity += this.item.getLocations().get(i).getQuantity();
				Debug.d("Total Quantity", totalQuantity);
				
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.xml.item_add_location_row, null);
				
				TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
				EditText etQuan = (EditText) view.findViewById(R.id.et_quantity);
				ImageView ivCategoryDelete = (ImageView) view.findViewById(R.id.iv_category_delete);
				final HorizontalScrollView hsvBreadcrumb = (HorizontalScrollView) view.findViewById(R.id.hsv_breadcrumb);
				
				tvTitle.setText(this.item.getLocations().get(i).getLocation().getBreadCrumb(this));
				Debug.d("Quantity", this.item.getLocations().get(i).getQuantity());
				etQuan.setText(Long.toString(this.item.getLocations().get(i).getQuantity()));
				
				ivCategoryDelete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						item.getLocations().remove(position);
						populateLocations();
					}
					
				});
				
				Handler handler = new Handler();
		    	Runnable r = new Runnable(){
		    	    public void run(){
		    	    	hsvBreadcrumb.fullScroll(HorizontalScrollView.FOCUS_RIGHT);                      
		    	    }
		    	};
		    	handler.postDelayed(r, 100L);
				
				etQuan.addTextChangedListener(new TextWatcher() {
					
//					boolean first = true;
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						long quantity = 0;
						
//						if(!first){
							try{
								quantity = Long.parseLong(s.toString());
							} catch(NumberFormatException e){
								e.printStackTrace();
							}
							
							totalQuantity -= quantity;
//						}
						
					}
					
					@Override
					public void afterTextChanged(Editable s) {
						long quantity = 0;
						
//						if(!first){
							try{
								quantity = Long.parseLong(s.toString());
							} catch(NumberFormatException e){
								e.printStackTrace();
							}
							
							item.getLocations().get(position).setQuantity(quantity);
							totalQuantity += quantity;
							Debug.d("Things have changed", quantity);
							tvItemQuantity.setText(Long.toString(totalQuantity));
//						} else {
//							first = false;
//						}
					}
				});
				
				etQuan.setTag(i);
				
//				this.etsLocQuan.add(etQuan);
				
				this.llItemLocations.addView(view);
    		}
		}
    	Debug.d("Total Quantity", totalQuantity);
    	this.tvItemQuantity.setText(Long.toString(this.totalQuantity));
		
    }
    
	@Override
	public void onClick(View v) {
		Debug.d("onClick");
		long pdt = 0;
		switch(v.getId()){
			case R.id.bt_add_category:
				startActivityForResult(Intents.categorySelector(this,0), Constants.ACTIVITY_CATEGORY_SELECTOR);
				break;
			case R.id.bt_add_location:
				startActivityForResult(Intents.locationSelector(this,0), Constants.ACTIVITY_LOCATION_SELECTOR);
				break;
			case R.id.bt_save:
				this.item.setTitle(this.etTitle.getText().toString());
				this.item.setDesc(this.etDescription.getText().toString());
				pdt = TimeStamp.getCurrentUt();
				
//				for(int i = 0; i < this.etsLocQuan.size(); i++){
//					this.item.getLocations().get(Integer.parseInt(this.etsLocQuan.get(i).getTag().toString())).setQuantity(Long.parseLong(this.etsLocQuan.get(i).getText().toString()));
//				}
				
				if(!this.edit){
					this.item.setPdtCreate(pdt);
					this.item.setPdtUpdate(pdt);
					this.itemAdapter.save(this.item);
				} else {
					this.item.setPdtUpdate(pdt);
					this.itemAdapter.update(this.item);
				}
				// TODO: Check if save was ok //
				Intent resultIntent = new Intent();
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
				break;
			case R.id.bt_cancel:
				finish();
				break;
			case R.id.bt_take_photo:
				startActivityForResult(this.camera.startCamera(), Constants.ACTIVITY_TAKE_PHOTO);
				break;
			case R.id.bt_add_nfc_id:
				startActivityForResult(Intents.readNfc(this), Constants.ACTIVITY_READ_NFC);
				break;
			case R.id.bt_add_barcode_id:
				Intents.scanBarcode(ItemAdd.this);
				break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Debug.d("onActivityResult");
		switch(requestCode){
			case Constants.ACTIVITY_CATEGORY_SELECTOR:
				if(resultCode == Activity.RESULT_OK){
					if(data.hasExtra("cat_id")){
						Category cat = new Category();
						cat = this.categoryAdapter.getDetails(data.getLongExtra("cat_id", 0));
						this.item.addCategory(cat);
						this.populateCategories();
					}
				}
				break;
			case Constants.ACTIVITY_LOCATION_SELECTOR:
				if(resultCode == Activity.RESULT_OK){
					if(data.hasExtra("loc_id")){
						Location loc = new Location();
						loc = this.locationAdapter.getDetails(data.getLongExtra("loc_id", 0));
						this.item.addLocation(loc, 1);
						this.populateLocations();
					}
				}
				break;
			case Constants.ACTIVITY_TAKE_PHOTO:
				this.camera.savePhoto(data.getExtras());
				this.camera.showTempPhoto(this.ivItemPhoto);
				break;
			case Constants.ACTIVITY_READ_NFC:
				if(resultCode == Activity.RESULT_OK){
					if(data.hasExtra(Constants.EXTRA_NFC_ID)){
						String nfcId = data.getStringExtra(Constants.EXTRA_NFC_ID);
						boolean isFree = data.getBooleanExtra(Constants.EXTRA_NFC_IS_FREE, true);
						this.tvNfcId.setText(nfcId);
						
						if(!isFree){
							DbNfcAdapter nfcAdapter = new DbNfcAdapter(this);
							this.item.setNfc(nfcAdapter.getDetailsByTagId(nfcId));
							this.tvTagExistsWarning.setVisibility(View.VISIBLE);
						} else {
							this.item.getNfc().setTagId(nfcId);
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
					
					Barcode barcode = this.barcodeAdapter.getDetailsByBarcodeId(barcodeId);
					
					if(barcode.exists()){
						this.item.setBarcode(barcode);
						this.tvBarcodeExistsWarning.setVisibility(View.VISIBLE);
					} else {
						this.item.getBarcode().setBarcodeId(barcodeId);
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

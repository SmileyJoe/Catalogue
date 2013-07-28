package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.smileyjoedev.genLibrary.Camera;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.GeneralViews;
import com.smileyjoedev.genLibrary.KeyBoard;
import com.smileyjoedev.genLibrary.TimeStamp;

public class ItemAdd extends SherlockActivity implements OnClickListener {
	
	private Item item;
	private DbItemAdapter itemAdapter;
	private DbCategoryAdapter categoryAdapter;
	private DbLocationAdapter locationAdapter;
	private boolean edit;
	private EditText etTitle;
	private EditText etDescription;
	private Button btAddCategory;
	private Button btAddLocation;
	private Button btSave;
	private Button btCancel;
	private Button btTakePhoto;
	private ImageView ivItemPhoto;
	private LinearLayout llItemCategories;
	private LinearLayout llItemLocations;
	private TextView tvItemQuantity;
	private long totalQuantity;
	private GeneralViews genViews;
//	private ArrayList<EditText> etsLocQuan;
	private Camera camera;
	
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
    	
    	this.ivItemPhoto = (ImageView) findViewById(R.id.iv_photo);
    	
    	this.llItemCategories = (LinearLayout) findViewById(R.id.ll_item_categories);
    	this.llItemLocations = (LinearLayout) findViewById(R.id.ll_item_locations);
    	
    	this.tvItemQuantity = (TextView) findViewById(R.id.tv_item_quantity);
    	
    	this.genViews = new GeneralViews(this);
    	
//    	this.etsLocQuan = new ArrayList<EditText>();
    	
    	this.camera = new Camera(Constants.PHOTO_PATH);
    	
    	this.totalQuantity = 0;
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
    	
    	if(this.camera.isPhotoExists()){
    		this.camera.showPhoto(this.ivItemPhoto);
    	}
    	
    	this.populateCategories();
    	this.populateLocations();
    }
    
    public void populateCategories(){
    	Debug.d("populateCategories");
    	this.llItemCategories.removeAllViews();
    	Debug.d("Categories size", this.item.getCategories().size());
    	for(int i = 0; i < this.item.getCategories().size(); i++){
    		if(!this.item.getCategories().get(i).getTitle().equals("")){
    			this.llItemCategories.addView(this.genViews.addField(this.item.getCategories().get(i).getTitle()));
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
				
				tvTitle.setText(this.item.getLocations().get(i).getLocation().getTitle());
				Debug.d("Quantity", this.item.getLocations().get(i).getQuantity());
				etQuan.setText(Long.toString(this.item.getLocations().get(i).getQuantity()));
				
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
		}
	}
	
}

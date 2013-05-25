package com.smileyjoedev.catalogue;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.smileyjoedev.genLibrary.Camera;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.GeneralViews;
import com.smileyjoedev.genLibrary.KeyBoard;
import com.smileyjoedev.genLibrary.TimeStamp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
	private GeneralViews genViews;
	private ArrayList<EditText> etsLocQuan;
	private Camera camera;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_add);
        KeyBoard.forceClose(getWindow());

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
    
    private void initialize(){
    	this.item = new Item();
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
    	
    	this.etsLocQuan = new ArrayList<EditText>();
    	
    	this.camera = new Camera(Constants.PHOTO_PATH);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        
       	inflater.inflate(R.menu.item_add, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }

    }
    
    private void populateView(){
    	
    	this.etTitle.setText(this.item.getTitle());
    	this.etDescription.setText(this.item.getDesc());
    	
    	if(this.camera.isPhotoExists()){
    		this.camera.showPhoto(this.ivItemPhoto);
    	}
    	
    	this.populateCategories();
    	this.populateLocations();
    }
    
    public void populateCategories(){
    	this.llItemCategories.removeAllViews();
    	
    	for(int i = 0; i < this.item.getCategories().size(); i++){
    		if(!this.item.getCategories().get(i).getTitle().equals("")){
    			this.llItemCategories.addView(this.genViews.addField(this.item.getCategories().get(i).getTitle()));
    		}
    	}
    }
    
    public void populateLocations(){
    	long totQuan = 0;
    	this.llItemLocations.removeAllViews();
    	this.etsLocQuan = new ArrayList<EditText>();
    	
    	for(int i = 0; i < this.item.getLocations().size(); i++){
    		if(!this.item.getLocations().get(i).getLocation().getTitle().equals("")){
				totQuan += this.item.getLocations().get(i).getQuantity();
				
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.xml.item_add_location_row, null);
				
				TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
				EditText etQuan = (EditText) view.findViewById(R.id.et_quantity);
				
				tvTitle.setText(this.item.getLocations().get(i).getLocation().getTitle());
				etQuan.setText(Long.toString(this.item.getLocations().get(i).getQuantity()));
				
				etQuan.setTag(i);
				
				this.etsLocQuan.add(etQuan);
				
				this.llItemLocations.addView(view);
    		}
		}
    	
    	this.tvItemQuantity.setText(Long.toString(totQuan));
		
    }

	@Override
	public void onClick(View v) {
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
				
				for(int i = 0; i < this.etsLocQuan.size(); i++){
					this.item.getLocations().get(Integer.parseInt(this.etsLocQuan.get(i).getTag().toString())).setQuantity(Long.parseLong(this.etsLocQuan.get(i).getText().toString()));
				}
				
				if(!this.edit){
					this.item.setPdtCreate(pdt);
					this.item.setPdtUpdate(pdt);
					this.itemAdapter.save(this.item);
				} else {
					this.item.setPdtUpdate(pdt);
					this.itemAdapter.update(this.item);
				}
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

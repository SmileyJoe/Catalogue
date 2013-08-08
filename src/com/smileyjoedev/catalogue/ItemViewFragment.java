package com.smileyjoedev.catalogue;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.smileyjoedev.genLibrary.Camera;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.GeneralViews;

public class ItemViewFragment extends SherlockFragment {

	private Item item;
	private DbItemAdapter itemAdapter;
	private Context context;
	private long itemId;
	private Camera camera;
	private View view;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Debug.d("OnCreate fragment");
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Debug.d("Create view fragment");
    	this.view = inflater.inflate(R.layout.item_view_fragment, container, false);
    	
    	this.populateView();
		
	    return this.view;
    }
    
    private void initialize(){
    	Debug.d("Initialize fragment");
    	if(this.item == null){
    		this.item = new Item();
    	}
    	
    	this.itemAdapter = new DbItemAdapter(this.context);
    	this.camera = new Camera(Constants.PHOTO_PATH);
    	
    	this.item = this.itemAdapter.getDetails(this.itemId);
    	
    	Debug.d(this.item);
    }
    
    @Override
	public void onAttach(Activity activity) {
		Debug.d("onAttach fragment");
		super.onAttach(activity);
		
	    try {
	    	ItemDataInterface hostInterface;
	        hostInterface = (ItemDataInterface) activity;
	        this.itemId = hostInterface.getItemId();
	    } catch(ClassCastException e) {
	    	this.itemId = 0;
	    }
	    
	    this.context = activity.getApplicationContext();
	    
	    this.initialize();
	}
    
    private void populateView(){
    	ImageView ivPhoto = (ImageView) this.view.findViewById(R.id.iv_photo);
    	TextView tvTitle = (TextView) this.view.findViewById(R.id.tv_item_title);
    	TextView tvDescription = (TextView) this.view.findViewById(R.id.tv_item_description);
    	TextView tvCreated = (TextView) this.view.findViewById(R.id.tv_item_created);
    	TextView tvUpdated = (TextView) this.view.findViewById(R.id.tv_item_updated);
    	TextView tvQuantity = (TextView) this.view.findViewById(R.id.tv_item_quantity);
    	
		this.camera.setName(Long.toString(this.item.getId()));
		this.camera.showPhoto(ivPhoto);
		tvTitle.setText(this.item.getTitle());
		tvDescription.setText(this.item.getDesc());
		tvCreated.setText(this.item.getCreateFormatted());
		tvUpdated.setText(this.item.getUpdateFormatted());
		tvQuantity.setText(Long.toString(this.item.getTotalQuantity()));
		
		this.populateCategories();
		this.populateLocations();
    }
	
    public void updateView(){
    	this.item = this.itemAdapter.getDetails(this.itemId);
    	this.populateView();
    }
    
    public void populateCategories(){
    	Debug.d("populateCategories");
    	GenViews.itemViewCategory(this.context, (LinearLayout) this.view.findViewById(R.id.ll_item_categories), this.item);
    }
    
    public void populateLocations(){
    	GenViews.itemViewLocation(this.context, (LinearLayout) this.view.findViewById(R.id.ll_item_locations), this.item);
    }
}

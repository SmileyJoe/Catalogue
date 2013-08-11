package com.smileyjoedev.catalogue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.smileyjoedev.genLibrary.Debug;
import com.smileyjoedev.genLibrary.Notify;

public class CategoryNew extends SherlockActivity implements OnClickListener {

	private Button btSave;
	private Button btCancel;
	private EditText etCategoryTitle;
	private DbCategoryAdapter categoryAdapter;
	private long categoryId;
	private Category category;
	private boolean isEdit;
	private TextView tvTagExistsWarning;
	private Button btAddNfcTag;
	private TextView tvNfcId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.category_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.init();
        this.populateView();
	}
	
	private void init(){
		this.btSave = (Button) findViewById(R.id.bt_save);
		this.btSave.setOnClickListener(this);
		this.btCancel = (Button) findViewById(R.id.bt_cancel);
		this.btCancel.setOnClickListener(this);
		this.etCategoryTitle = (EditText) findViewById(R.id.et_new_category);
		this.categoryAdapter = new DbCategoryAdapter(this);
		
		try{
        	Bundle extras = getIntent().getExtras();
    		this.categoryId = extras.getLong(Constants.EXTRA_CATEGORY_ID);
    		this.isEdit = extras.getBoolean(Constants.EXTRA_IS_EDIT);
    		if(this.isEdit){
    			this.category = this.categoryAdapter.getDetails(this.categoryId);
    		} else {
    			this.category = new Category();
    		}
        } catch(NullPointerException e){
        	this.categoryId = 0;
        	this.isEdit = false;
        	this.category = new Category();
        }
        
    	this.btAddNfcTag = (Button) findViewById(R.id.bt_add_nfc_id);
    	this.btAddNfcTag.setOnClickListener(this);
    	
    	this.tvNfcId = (TextView) findViewById(R.id.tv_nfc_id);
    	
    	this.tvTagExistsWarning = (TextView) findViewById(R.id.tv_nfc_tag_exists_warning);
	}
	
	private void populateView(){
		this.etCategoryTitle.setText(this.category.getTitle());
		this.tvNfcId.setText(this.category.getNfc().getTagId());
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bt_save:
			String title = this.etCategoryTitle.getText().toString().trim();
			if(!title.equals("")){
				this.category.setTitle(this.etCategoryTitle.getText().toString().trim());
				if(!this.isEdit){
					this.category.setParentId(this.categoryId);
				}
				this.categoryAdapter.save(this.category);
				Intent resultIntent = new Intent();
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			} else {
				Notify.toast(this, R.string.toast_invalid_category_title);
			}
			break;
		case R.id.bt_add_nfc_id:
			startActivityForResult(Intents.readNfc(this), Constants.ACTIVITY_READ_NFC);
			break;
		case R.id.bt_cancel:
			finish();
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
							this.category.setNfc(nfcAdapter.getDetailsByTagId(nfcId));
							this.tvTagExistsWarning.setVisibility(View.VISIBLE);
						} else {
							this.category.getNfc().setTagId(nfcId);
							this.tvTagExistsWarning.setVisibility(View.GONE);
						}
						Debug.d("Activity result nfc id", nfcId);
					}
				}
				break;
		}
	}
	
}

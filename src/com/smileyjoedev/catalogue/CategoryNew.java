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

public class CategoryNew extends SherlockActivity implements OnClickListener {

	private Button btSave;
	private Button btCancel;
	private EditText etCategoryTitle;
	private DbCategoryAdapter categoryAdapter;
	private long categoryId;
	private Category category;
	private boolean isEdit;
	
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
	}
	
	private void populateView(){
		this.etCategoryTitle.setText(this.category.getTitle());
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
		case R.id.bt_cancel:
			finish();
			break;
		}
	}
	
}

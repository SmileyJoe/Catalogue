package com.smileyjoedev.catalogue;

import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.smileyjoedev.genLibrary.Debug;

public class ReadNFC extends SherlockActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Debug.d("Read nfc");
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	Tag myTag = (Tag) getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
    	String tagId = bytesToHexString(myTag.getId());
    	Debug.i("tag ID", tagId);
    	
    	DbNfcAdapter nfcAdapter = new DbNfcAdapter(this);
    	Nfc nfc = nfcAdapter.getDetailsByTagId(tagId);
    	
    	this.showResult();
    	
    	if(nfc.exists() && this.showResult()){
    		startActivity(Intents.fromNfc(this, nfc));
    	} else {
    		Broadcast.nfcFound(this, tagId);
    	}
    	
    	finish();
    }
    
    private boolean showResult(){
    	ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        // get the info from the currently running task
        List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(2); 

        String className = taskInfo.get(1).topActivity.getClassName(); 
        
        if(!className.equals("com.smileyjoedev.catalogue.ReadNfcActivity")){
        	return true;
        } else {
        	return false;
        }
    }
    
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);  
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);  
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString().toUpperCase();
    }

}

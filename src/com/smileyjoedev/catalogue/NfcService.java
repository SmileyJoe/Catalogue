package com.smileyjoedev.catalogue;

import com.smileyjoedev.genLibrary.Debug;

import android.app.Service;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.IBinder;

public class NfcService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO do something useful
		Debug.d("Service on start");
		Tag myTag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    	Debug.i("tag ID", bytesToHexString(myTag.getId()));
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO for communication return IBinder implementation
		Debug.d("Service on bind");
		return null;
	}
	
	private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
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

        return stringBuilder.toString();
    }
	
}

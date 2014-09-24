package com.cod3scr1b3r.wol;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class WOLActivity extends ActionBarActivity {

	private static final String WOL_SHARED_PREFS_FILE = "wol_prefs";
	private static final String MAC_ADDRESS_KEY = "mac_addr";
	
	private TextView mTextViewIpAddress;
	private TextView mTextViewMacAddress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wol);
		mTextViewMacAddress = (TextView)findViewById(R.id.text_mac_address);
		Button wolButton = (Button)findViewById(R.id.btn_wake_now);
		wolButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						WakeOnLanGenerator.WakeNow(mTextViewIpAddress.getText().toString(), 
								mTextViewMacAddress.getText().toString());
					}
				}).start();
				
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = getSharedPreferences(WOL_SHARED_PREFS_FILE, MODE_PRIVATE);
		if(prefs.contains(MAC_ADDRESS_KEY)){
			if( mTextViewMacAddress.getText().length() == 0 ){
				mTextViewMacAddress.setText(prefs.getString(MAC_ADDRESS_KEY, null));
			}
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Editor prefsEditor = getSharedPreferences(WOL_SHARED_PREFS_FILE, MODE_PRIVATE).edit();
		if( mTextViewMacAddress.getText().length() != 0 ){
			prefsEditor.putString(MAC_ADDRESS_KEY, mTextViewMacAddress.getText().toString());
		}
		prefsEditor.commit();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.wol, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
}


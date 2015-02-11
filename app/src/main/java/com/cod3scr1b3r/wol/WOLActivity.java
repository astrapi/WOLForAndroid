package com.cod3scr1b3r.wol;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cod3scr1b3r.wol.googleenhanced.MutableInt;
import com.cod3scr1b3r.wol.googleenhanced.SerializableSparseArray;

import java.net.InetAddress;
import java.util.regex.Pattern;



public class WOLActivity extends ActionBarActivity {

	enum TextFieldValidationStatus {
        valid, empty, bad_string
    }

    private SerializableSparseArray<TextFieldValidationStatus> mFieldsStat;
    private MutableInt mNumOfBadFields;
    private static final String BUNDLE_FIELD_STAT = "field_stat";
    private static final String BUNDLE_BAD_FIELDS_COUNT = "bad_fields_count";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wol);
		final EditText textViewMacAddress = (EditText)findViewById(R.id.text_mac_address);
        final EditText textViewNetAddress = (EditText)findViewById(R.id.text_net_address);
        initFieldsStat(new EditText[]{textViewMacAddress, textViewNetAddress}, savedInstanceState);
        final Button wolButton = (Button)findViewById(R.id.btn_wake_now);
        wolButton.setEnabled(false);

        textViewMacAddress.addTextChangedListener(new ValidationTextWatcher(textViewMacAddress.getId(),
				NetworkUtils.MAC_ADDRESS_REG_PATTERN, wolButton));
		textViewNetAddress.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        textViewNetAddress.addTextChangedListener(new ValidationTextWatcher(textViewNetAddress.getId(),
                NetworkUtils.IP4_REG_PATTERN, wolButton));
		wolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				String macStr = textViewMacAddress.getText().toString();
				String ipStr = textViewNetAddress.getText().toString();
				InetAddress ip = NetworkUtils.ip4String2Inet(ipStr);
				WOLSendService.DoWakeUp(getBaseContext(), new String[]{ macStr },ip);
				((WOLApp)getApplication()).getDataStrore().setLastUsedMac(macStr);
				((WOLApp)getApplication()).getDataStrore().setLastUsedNetAddress(ip);
            }
        });

		InetAddress ip = ((WOLApp)getApplication()).getDataStrore().getLastUsedNetAddress();
		if( ip != null ){
			textViewNetAddress.setText(ip.toString());
		}
		textViewMacAddress.setText( ((WOLApp)getApplication()).getDataStrore().getLastUsedMac());
	}


	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_FIELD_STAT, mFieldsStat);
        outState.putInt(BUNDLE_BAD_FIELDS_COUNT, mNumOfBadFields.value);
    }

    private void initFieldsStat(EditText[] editTexts, Bundle savedInstanceState){
        if( savedInstanceState == null ) {
			mNumOfBadFields = new MutableInt(editTexts.length);
            mFieldsStat = new SerializableSparseArray<>(editTexts.length);
            for (int i = 0; i < editTexts.length; i++) {
                mFieldsStat.put(editTexts[i].getId(), TextFieldValidationStatus.empty);
            }
        }else{
            mFieldsStat = (SerializableSparseArray<TextFieldValidationStatus>)savedInstanceState.getSerializable(BUNDLE_FIELD_STAT);
			int temp = savedInstanceState.getInt(BUNDLE_BAD_FIELDS_COUNT, editTexts.length);
			mNumOfBadFields = new MutableInt(temp);
        }
    }

    private TextFieldValidationStatus validateText(String textFromField, String regexp){
        TextFieldValidationStatus status = TextFieldValidationStatus.valid;
        String text = textFromField != null ? textFromField : "";
        if( TextUtils.isEmpty(text) ){
            status = TextFieldValidationStatus.empty;
        }else {
            if (!Pattern.matches(regexp, text)) {
                status = TextFieldValidationStatus.bad_string;
            }
        }
        return status;
    }

    private class ValidationTextWatcher implements TextWatcher {

        private int mMyId;
        private String mRegExpForValidation;
        private Button mButtonToChange;

        public ValidationTextWatcher(int myEditorId, String regExpValidation,Button buttonToChange){
            mMyId = myEditorId;
            mRegExpForValidation = regExpValidation;
            mButtonToChange = buttonToChange;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String check = s == null ? "" : s.subSequence(start, start + count).toString();
            TextFieldValidationStatus myPrevStat = mFieldsStat.get(mMyId);
            TextFieldValidationStatus myCurrentStat = validateText(check, mRegExpForValidation);
            if( myCurrentStat != myPrevStat){
                mFieldsStat.put(mMyId, myCurrentStat);
                if( myPrevStat != TextFieldValidationStatus.valid && myCurrentStat == TextFieldValidationStatus.valid ){
                    mNumOfBadFields.value--;
                }else if( myPrevStat == TextFieldValidationStatus.valid && myCurrentStat != TextFieldValidationStatus.valid){
                    mNumOfBadFields.value++;
                }
            }
            if( mNumOfBadFields.value == 0 ){
                mButtonToChange.setEnabled(true);
            }else{
                mButtonToChange.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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


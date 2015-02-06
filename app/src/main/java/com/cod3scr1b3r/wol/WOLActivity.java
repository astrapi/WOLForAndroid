package com.cod3scr1b3r.wol;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.regex.Pattern;



public class WOLActivity extends ActionBarActivity {

	enum TextFieldValidationStatus {
        valid, empty, bad_string
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wol);
		final EditText textViewMacAddress = (EditText)findViewById(R.id.text_mac_address);
        final EditText textViewNetAddress = (EditText)findViewById(R.id.text_net_address);
        final Button wolButton = (Button)findViewById(R.id.btn_wake_now);
        wolButton.setEnabled(false);

        TextWatcher validationTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextFieldValidationStatus fieldA = validateNetIpField(textViewMacAddress, NetworkUtils.MAC_ADDRESS_REG_PATTERN);
                TextFieldValidationStatus fieldB = validateNetIpField(textViewNetAddress, NetworkUtils.IP4_REG_PATTERN);
                if( fieldA == TextFieldValidationStatus.valid && fieldB == TextFieldValidationStatus.valid){
                    wolButton.setEnabled(true);
                }else{
                    wolButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        textViewMacAddress.addTextChangedListener(validationTextWatcher);
        textViewNetAddress.addTextChangedListener(validationTextWatcher);

		wolButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

			}
		});
	}

    private TextFieldValidationStatus validateNetIpField(EditText field, String regexp){
        TextFieldValidationStatus status = TextFieldValidationStatus.valid;
        String text = field != null ? field.getText().toString() : "";
        if( TextUtils.isEmpty(text) ){
            status = TextFieldValidationStatus.empty;
        }else {
            if (!Pattern.matches(regexp, text)) {
                status = TextFieldValidationStatus.bad_string;
            }
        }
        return status;
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


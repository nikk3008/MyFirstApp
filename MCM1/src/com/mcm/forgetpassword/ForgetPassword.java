package com.mcm.forgetpassword;

import com.mcm.R;
import com.mcm.login.LoginActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ForgetPassword extends Activity{
	
	TextView header;
	EditText login;
	Spinner spinnerChurchMember;
	Button submitButton;
	TextView tv_message ;
    ProgressDialog mProgressDialog;
	String clientid;
	boolean is_Table_Empty = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password_activity_layout);
		header();
		init();
	}

	private void init() {
		mProgressDialog = new ProgressDialog(ForgetPassword.this);
		tv_message = (TextView) findViewById(R.id.la_tvErrorMsg);
		login = (EditText) findViewById(R.id.la_editEmail);
		submitButton = (Button) findViewById(R.id.la_btnLogin);
		spinnerChurchMember = (Spinner) findViewById(R.id.la_spChurchCentre);
		
	}

	private void header() {
		// TODO Auto-generated method stub
		header = (TextView)findViewById(R.id.headerTextView);
		header.setText("Forgot Password");
	}

}

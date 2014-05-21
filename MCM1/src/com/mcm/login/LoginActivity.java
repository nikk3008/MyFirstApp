package com.mcm.login;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mcm.R;
import com.mcm.SplashActivity;
import com.mcm.database.AppConstant;
import com.mcm.forgetpassword.ForgetPassword;
import com.mcm.listener.LoginClickListner;
import com.mcm.menuandnotification.Menu;
import com.mcm.registration.InterfaceSPinnerId;

public class LoginActivity extends Activity implements InterfaceSPinnerId,
		PopulateChurchListOnValidating {

	EditText login, passWord;
	Spinner spinnerChurchMember;
	Button logiButton;
	TextView tv_message , tv_forgotPassword;
	TextView tv_header;
	private ProgressDialog mProgressDialog;
	String clientid;
	boolean is_Table_Empty = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
		header();
		addItemsOnChurchMemberShipTypeSpinner();
		addListenerOnSpinnerItemSelection();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
	}

	private void init() {
		is_Table_Empty = getIntent().getBooleanExtra(AppConstant.CHECK_TABLE, false);
		Log.e("I AM IN MAIN LOGIN", "boolean value " + is_Table_Empty);
		mProgressDialog = new ProgressDialog(LoginActivity.this);
		tv_message = (TextView) findViewById(R.id.la_tvErrorMsg);
		tv_forgotPassword = (TextView)findViewById(R.id.la_forfotpaswword);
		login = (EditText) findViewById(R.id.la_editEmail);
		passWord = (EditText) findViewById(R.id.la_editPass);
		logiButton = (Button) findViewById(R.id.la_btnLogin);
		spinnerChurchMember = (Spinner) findViewById(R.id.la_spChurchCentre);
		logiButton.setOnClickListener(loginClkListener);
		tv_forgotPassword.setOnClickListener(loginClkListener);
		spinnerChurchMember.setOnTouchListener(Spinner_OnTouch);
		tv_message.setVisibility(View.INVISIBLE);
	}

	private void header() {
		tv_header = (TextView) findViewById(R.id.headerTextView);
		tv_header.setText("Login");
	}

	public void addItemsOnChurchMemberShipTypeSpinner() {
		Log.e("Approved Member list", "" + SplashActivity.approvedMemeberList);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < SplashActivity.approvedMemeberList.size(); i++) {
			list.add(SplashActivity.approvedMemeberList.get(i).get(1));
		}
		Log.e("MY CHURCH LIST ", "" + list);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerChurchMember.setAdapter(dataAdapter);
		spinnerChurchMember.setPrompt("Church Center");
	}

	public void addListenerOnSpinnerItemSelection() {
		spinnerChurchMember
				.setOnItemSelectedListener(new ActiveMemeberShipSpinner(
						LoginActivity.this, LoginActivity.this,
						spinnerChurchMember));
	}

	@Override
	public void getSpinnerId(int pos) {
		clientid = SplashActivity.approvedMemeberList.get(pos).get(0)
				.toString();
		Log.e("I AM IN ID VALUE", "" + clientid);
	}

	LoginClickListner loginClkListener = new LoginClickListner() {

		@Override
		public void onLogInBtnClk(View view) {
			// TODO Auto-generated method stub
			validationForLoginButton();
		}

		@Override
		public void onforgotPasswordClk(View view) {
			// TODO Auto-generated method stub
			startActivity(new Intent(LoginActivity.this,ForgetPassword.class));
		}
	};

	private void validationForLoginButton() {
		if (!checkEmail())
			return;
		if (!checkEmailPattern())
			return;
		if (!checkpassWord())
			return;
		else {
			authorizedUser();
		}
	}

	private boolean checkEmailPattern() {
		Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9+._%-+]{1,100}"
				+ "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,10}" + "(" + "."
				+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,20}" + ")+");
		if (!EMAIL_PATTERN.matcher(login.getText().toString()).matches()) {
			setErrMsg("Please fill valid Email Address.");
			return false;
		}
		return true;
	}

	private boolean checkEmail() {
		if (login.length() == 0) {
			setErrMsg("Please fill Email field.");
			return false;
		}
		return true;
	}

	private boolean checkpassWord() {
		if (passWord.length() == 0) {
			setErrMsg("Please fill password field.");
			return false;
		}
		return true;
	}

	View.OnTouchListener Spinner_OnTouch = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				showToast();
			}
			return false;
		}

		private void showToast() {
			Log.e("SPINNER COUNT", ""
					+ spinnerChurchMember.getAdapter().getCount());
			if (spinnerChurchMember.getAdapter().getCount() == 1) {
				spinnerChurchMember.setEnabled(false);
//				Toast.makeText(LoginActivity.this, "NO Authorized Member",
//						Toast.LENGTH_LONG).show();
			}
			if (!is_Table_Empty) {
				Log.e("Spinner GEtCount", "" + spinnerChurchMember.getAdapter().getCount());
				spinnerChurchMember.setEnabled(false);
				tv_message.setText("Enter Your Email And Password");
			} else {
				spinnerChurchMember.setEnabled(true);
			}
		}
	};

	public void setErrMsg(String msg) {
		tv_message.setVisibility(View.VISIBLE);
		tv_message.setText(msg);
		tv_message.setTextColor(Color.RED);
	}

	private void authorizedUser() {

		if (!is_Table_Empty) {
			Log.e("Calling False one", "" + is_Table_Empty);
			validateFromApi();
		} else {
			Log.e("Calling True one", "" + is_Table_Empty);
			validatefromDatabase();
		}

	}

	@Override
	public void populateChurchList(ArrayList<ArrayList<String>> churchList) {
		is_Table_Empty = true;
	}

	private void validateFromApi() {
		SQLiteDatabase database = SplashActivity.databaseHelper
				.getWritableDatabase();
		String url = "http://mcmwebapi.victoriatechnologies.com/api/Member/GetRegisteredMemberByEmailPwd?EmailId="
				+ login.getText().toString().trim()
				+ "&"
				+ "Password="
				+ passWord.getText().toString().trim();
		new LogInAsync(LoginActivity.this, mProgressDialog, url, login
				.getText().toString().trim(), passWord.getText().toString()
				.trim(), LoginActivity.this, database, spinnerChurchMember,
				is_Table_Empty).execute("");
	}

	private void validatefromDatabase() {
		if (clientid == null) {
			showOKAleart("Error", "Invalid Credential");
			return;
		}
		GetDataFromDatabase getDataFromDatabase = new GetDataFromDatabase();
//		if (!checkForClientID(getDataFromDatabase)) {
//			showOKAleart("Error", "Invalid Credential");
//			return;
//		}
		if (!checkForEmail(getDataFromDatabase)) {
			showOKAleart("Error", "Invalid Credential");
			return;
		}
		if (!checkForPassword(getDataFromDatabase)) {
			showOKAleart("Error", "Invalid Credential");
			return;
		} else {
			startActivity(new Intent(LoginActivity.this, Menu.class));
		}
	}

	private boolean checkForClientID(GetDataFromDatabase getDataFromDatabase) {
		if (Integer.parseInt(clientid) == getDataFromDatabase
				.getClientIdField()) {
			return true;
		}
		return false;
	}

	private boolean checkForEmail(GetDataFromDatabase getDataFromDatabase) {
		if (login.getText().toString().trim().equals(getDataFromDatabase
				.getEmailField().toString().trim())) {
			return true;
		}
		return false;
	}

	private boolean checkForPassword(GetDataFromDatabase getDataFromDatabase) {
		if (passWord.getText().toString().trim().equals(getDataFromDatabase
				.getPasswordField().toString().trim())) {
			return true;
		}
		return false;
	}

	public void showOKAleart(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				LoginActivity.this);
		builder.setTitle(title).setMessage(message)
				.setNegativeButton("OK", null).show();
	}
}

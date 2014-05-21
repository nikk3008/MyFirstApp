package com.mcm.login;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mcm.SplashActivity;
import com.mcm.appconstant.RegistrationAppConstant;
import com.mcm.database.InsertTable;
import com.mcm.home.HomeActivity;

public class LogInAsync extends AsyncTask<String, String, String> {

	ProgressDialog mProgressDialog;
	Context context;
	String url;
	PopulateChurchListOnValidating populateChurchListOnValidating;
	int loginValue = 5;
	boolean isTableFalse;
	SQLiteDatabase database;
	String email;
	String password;
	Spinner spinnerChurchMember;

	public LogInAsync(Context context, ProgressDialog mProgressDialog,
			String url, String email, String password,
			PopulateChurchListOnValidating populateChurchListOnValidating,
			SQLiteDatabase database, Spinner spinnerChurchMember,
			boolean isTableFalse) {
		this.context = context;
		this.mProgressDialog = mProgressDialog;
		this.url = url;
		this.email = email;
		this.password = password;
		this.database = database;
		this.spinnerChurchMember = spinnerChurchMember;
		this.isTableFalse = isTableFalse;
		this.populateChurchListOnValidating = populateChurchListOnValidating;
		Log.e("MERA URL DEKH LO", "" + url);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		showDialog();
	}

	@Override
	protected String doInBackground(String... aurl) {
		Log.e("MAY AYA", "" + "do in background in sigin");
		callLogin();
		return null;
	}

	@Override
	protected void onPostExecute(String unused) {
		closeDialog();
		onSuccesFull();
	}

	void showDialog() {
		mProgressDialog.setMessage("Please Wait...For Some Time.");
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	void closeDialog() {
		if (mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private void callLogin() {
		GetDataFromApi getDataFromApi = new GetDataFromApi(url);
		if (!isTableFalse) {
			Log.e("MAI A GAAYA SYNCRONISE DATA BASE MAI", "HA MAI AYA");
			String message = getDataFromApi.postSignIn().toString().trim();
			syncroniseDatabase(message);
			populateChurchListOnValidating
					.populateChurchList(parseJsonToArrayList());
		} else {
			try {
				loginValue = Integer.parseInt(getDataFromApi.postSignIn());
				Log.e("Login VAlue", "" + loginValue);
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		}
	}

	public ArrayList<ArrayList<String>> parseJsonToArrayList() {
		GetDataFromDatabase getDataFromDatabase = new GetDataFromDatabase();
		String emailField = getDataFromDatabase.getEmailField().toString()
				.trim();
		String urlForChuchList = "http://mcmwebapi.victoriatechnologies.com/api/Client?EmailId="
				+ emailField;
		GetDataFromApi getDataFromApi = new GetDataFromApi(urlForChuchList);
		InsertTable insertTable = new InsertTable(database);
		try {
			JSONArray jArray = new JSONArray(getDataFromApi.postSignIn()
					.toString().trim());
			JSONObject jsonObject = null;
			for (int i = 0; i < jArray.length(); i++) {
				ArrayList<String> memeberFeild = new ArrayList<String>();
				jsonObject = jArray.getJSONObject(i);
				memeberFeild
						.add(""
								+ jsonObject
										.getInt(com.mcm.appconstant.AppConstant.CHURCH_MEMEBER_TAG_ID));
				memeberFeild
						.add(jsonObject
								.getString(com.mcm.appconstant.AppConstant.CHURCH_MEMEBER_TAG_NAME));
//				SplashActivity.approvedMemeberList.add(memeberFeild);
				insertTable.addRowforClientTable(jsonObject
										.getInt(com.mcm.appconstant.AppConstant.CHURCH_MEMEBER_TAG_ID), jsonObject
								.getString(com.mcm.appconstant.AppConstant.CHURCH_MEMEBER_TAG_NAME));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return SplashActivity.approvedMemeberList;
	}

	private void syncroniseDatabase(String mesString) {
		InsertTable insertTable = new InsertTable(database);
		try {
			JSONArray jsonArray = new JSONArray(mesString);
			JSONObject jsonObject = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				insertTable
						.addRowforMemberTable(
								jsonObject
										.getString(RegistrationAppConstant.MEMBER_ID),
								jsonObject
										.getString(RegistrationAppConstant.CLIENT_ID),
								jsonObject
										.getString(RegistrationAppConstant.FIRSTNAME),
								jsonObject
										.getString(RegistrationAppConstant.SURNAME),
								jsonObject
										.getString(RegistrationAppConstant.EMAIL_ID),
								jsonObject
										.getString(RegistrationAppConstant.PASSWORD));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void statusLogin() {
		if (loginValue == 1) {
			Toast.makeText(context, "You are Succesfully Login",
					Toast.LENGTH_LONG).show();
			context.startActivity(new Intent(context, HomeActivity.class));
		} else if (loginValue == 0) {
			Log.e("I S ZERO", "YES");
			Toast.makeText(context, "Invalid Credential", Toast.LENGTH_LONG)
					.show();
		} else if (loginValue == -1) {
			Log.e("I S ONE", "YES");
			Toast.makeText(context, "Bad Request", Toast.LENGTH_LONG).show();
		}
	}

	public void addItemsOnChurchMemberShipTypeSpinner() {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < SplashActivity.approvedMemeberList.size(); i++) {
			list.add(SplashActivity.approvedMemeberList.get(i).get(1));
		}
		Log.e("MY CHURCH LIST ", "" + list);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerChurchMember.setAdapter(dataAdapter);
		spinnerChurchMember.setPrompt("Church Center");
		spinnerChurchMember.setEnabled(true);
	}
	
	private void getChurchListFromDatabase() {
		GetDataFromDatabase getDataFromDatabase = new GetDataFromDatabase();
		ArrayList<ArrayList<String>> churchList = getDataFromDatabase
				.getClientChurch();
		for (int i = 0; i < churchList.size(); i++) {
			ArrayList<String> memeberFeild = new ArrayList<String>();
			memeberFeild.add("" + churchList.get(i).get(0));
			memeberFeild.add("" + churchList.get(i).get(1));
			SplashActivity.approvedMemeberList.add(memeberFeild);
		}
		Log.e("memberList", "" + SplashActivity.churchMemeberList);
	}

	private void onSuccesFull() {
		if (!isTableFalse) {
			getChurchListFromDatabase();
			addItemsOnChurchMemberShipTypeSpinner();
		} else {
			statusLogin();
		}
	}
}

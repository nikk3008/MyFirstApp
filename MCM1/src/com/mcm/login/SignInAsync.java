package com.mcm.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.mcm.Constants;
import com.mcm.SplashActivity;
import com.mcm.database.AppConstant;
import com.mcm.database.InsertTable;
import com.mcm.home.HomeActivity;
import com.mcm.registration.RegisterActivity;

public class SignInAsync extends AsyncTask<String, String, String> {

	ProgressDialog mProgressDialog;
	Context context;
	ArrayList<String> myStrings;
	String url;
	boolean isTable;

	public SignInAsync(Context context, ProgressDialog mProgressDialog,
			String url, boolean isTable) {
		this.context = context;
		this.mProgressDialog = mProgressDialog;
		this.url = url;
		this.isTable = isTable;
		Log.e("MAY AYA", "" + "do in context");
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		showDialog();
	}

	@Override
	protected String doInBackground(String... aurl) {
		Log.e("MAY AYA", "" + "do in background in sigin");
		if (!checkForMEmberTables()) {
         Log.e("IF CONDITION", "YES");
		} else {
			if (!checkForClientTable()) {
				 Log.e("ELSE IF CONDITION", "YES");
				saveVlaueInInserTable();
				getChurchListFromDatabase();
			} else {
				Log.e("ELSE ELSE CONDITION", "YES");
				getChurchListFromDatabase();
			}
			
		}
		return null;
	}

	@Override
	protected void onPostExecute(String unused) {
		closeDialog();
		signInCondition();

	}

	void showDialog() {
		mProgressDialog.setMessage("Please Wait...updating form.");
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	void closeDialog() {
		if (mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	public void saveVlaueInInserTable() {
		try {
			SQLiteDatabase database = SplashActivity.databaseHelper
					.getReadableDatabase();
			InsertTable insertTable = new InsertTable(database);
			JSONArray jArray = new JSONArray(postSignIn().toString().trim());
			Log.e("JSON LENGHT ", "" + jArray.length());
			for (int i = 0; i < jArray.length(); i++) {
				ArrayList<String> memeberFeild = new ArrayList<String>();
				JSONObject jsonObject = jArray.getJSONObject(i);
				memeberFeild.add("" + jsonObject.getInt(com.mcm.appconstant.AppConstant.CHURCH_MEMEBER_TAG_ID));
				memeberFeild.add(jsonObject.getString(com.mcm.appconstant.AppConstant.CHURCH_MEMEBER_TAG_NAME));
				// SplashActivity.approvedMemeberList.add(memeberFeild);
				insertTable.addRowforClientTable(jsonObject.getInt(com.mcm.appconstant.AppConstant.CHURCH_MEMEBER_TAG_ID),
							jsonObject.getString(com.mcm.appconstant.AppConstant.CHURCH_MEMEBER_TAG_NAME));
				
				
			}
			Log.e("memberList", "" + SplashActivity.churchMemeberList); // update

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private String postSignIn() {
		InputStream inputStream = null;
		String result = "";
		String mainUrl = url + getEmailField();
		try {
			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// 2. make POST request to the given URL
			HttpGet httpGet = new HttpGet(mainUrl);
			// 7. Set some headers to inform server about the type of the
			// content
			httpGet.setHeader("Accept", "application/json");
			httpGet.setHeader("Content-type", "application/json");

			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpGet);
			Log.e("RESPONSE FROM SERVER", ""
					+ httpResponse.getStatusLine().getStatusCode());

			// 9. receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// 10. convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		// 11. return result
		Log.e("mY CHURST LIST AND CLIENT ID", "" + result);
		return result;
	}

	private String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}

	 public ArrayList<ArrayList<String>> getAllRowsAsArrays() {
	 ArrayList<ArrayList<String>> dataArrays = new
	 ArrayList<ArrayList<String>>();
	 SQLiteDatabase database = SplashActivity.databaseHelper
	 .getReadableDatabase();
	 Cursor cursor;
	 try {
	 cursor = database.query(AppConstant.MEMBER_TABLE_NAME,
	 new String[] { AppConstant.MCM_MEMBER_MEMEBER_ID,
	 AppConstant.MCM_MEMBER_CLIENT_ID,
	 AppConstant.MCM_MEMBER_FIRST_NAME,
	 AppConstant.MCM_MEMBER_LAST_NAME,
	 AppConstant.MCM_MEMBER_EMAIL_ID,
	 AppConstant.MCM_MEMBER_PASSWORD }, null, null,
	 null, null, null);
	 cursor.moveToFirst();
	 if (!cursor.isAfterLast()) {
	 do {
	 ArrayList<String> dataList = new ArrayList<String>();
	 dataList.add("" + cursor.getInt(0));
	 dataList.add("" + cursor.getInt(1));
	 dataList.add(cursor.getString(2));
	 dataList.add(cursor.getString(3));
	 dataList.add(cursor.getString(4));
	 dataList.add(cursor.getString(5));
	 dataArrays.add(dataList);
	
	 } while (cursor.moveToNext());
	 }
	 } catch (SQLException e) {
	 Log.e("DB Error", e.toString());
	 e.printStackTrace();
	 }
	 Log.w("--Result-----", "" + dataArrays);
	 return dataArrays;
	 }

	private String getEmailField() {
		String email = "";
		ArrayList<ArrayList<String>> emailList = getAllRowsAsArrays();
		if (emailList.size() == 0) {
			email = "abc@gmail.com";
		} else {
			email = emailList.get(getAllRowsAsArrays().size() - 1).get(4)
					.toString();
		}
		Log.e("EMAIL SIZE", "" + getAllRowsAsArrays().size());
		Log.e("EMAIL ID", "" + email);
		return email;
	}

	public boolean checkForMEmberTables() {
		GetDataFromDatabase getDataFromDatabase = new GetDataFromDatabase();
		return getDataFromDatabase.checkForTables();
	}
	
	private boolean checkForClientTable()
	{
		GetDataFromDatabase getDataFromDatabase = new GetDataFromDatabase();
		return getDataFromDatabase.checkForClientTables();
	}

	private void signInCondition() {
			Log.e("BOOLEAN CONDITION", "" + checkForMEmberTables());
			Intent i = new Intent(context, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra(AppConstant.CHECK_TABLE, checkForMEmberTables());
			context.startActivity(new Intent(context,
					com.mcm.login.LoginActivity.class));
	}

}

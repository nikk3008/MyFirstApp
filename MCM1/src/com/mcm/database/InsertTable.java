package com.mcm.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mcm.SplashActivity;

public class InsertTable {

	SQLiteDatabase database;

	public InsertTable(SQLiteDatabase database) {
		this.database = database;
	}

	public void addRowforMemberTable(String memberID, String clientID,
			String firstName, String surName, String emailIDString,
			String passWordString) {
		Log.e("NAME", "" + memberID + "  " + clientID + " " + firstName + " "
				+ surName + " " + emailIDString + " " + passWordString);

		ContentValues contentValue = new ContentValues();

		contentValue.put(AppConstant.MCM_MEMBER_MEMEBER_ID, memberID);
		contentValue.put(AppConstant.MCM_MEMBER_CLIENT_ID, clientID);
		contentValue.put(AppConstant.MCM_MEMBER_FIRST_NAME, firstName);
		contentValue.put(AppConstant.MCM_MEMBER_LAST_NAME, surName);
		contentValue.put(AppConstant.MCM_MEMBER_EMAIL_ID, emailIDString);
		contentValue.put(AppConstant.MCM_MEMBER_PASSWORD, passWordString);
		// Log.e("Cotent value", "" + contentValue);

		try {
			SplashActivity.databaseHelper.insertInToTable(database,
					AppConstant.MEMBER_TABLE_NAME, contentValue);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addRowforClientTable(int clientID, String churhList) {
		Log.e("clientID", "" + clientID + "  " + churhList + "  " + churhList);
		ContentValues contentValue = new ContentValues();
		contentValue.put(AppConstant.MCM_CLIENT_CLIENT_ID, "" + clientID);
		contentValue.put(AppConstant.MCM_CLIENT_CLIENT_CHURCH, churhList);
		Log.e("Cotent value", "" + contentValue);
		try {
			SplashActivity.databaseHelper.insertInToTable(database,
					AppConstant.CLIENT_TABLE_NAME, contentValue);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

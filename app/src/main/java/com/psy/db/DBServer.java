package com.psy.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.psy.model.UserPose;


/**
 *
 * @author ppssyyy
 *
 */
public class DBServer {

	private DBHelper dbHelper;

	public DBServer(Context context)

	{

		this.dbHelper = new DBHelper(context);
	}

	// //////////////USER/////////////////////
	public void addUser(UserPose userPose) {
		SQLiteDatabase localSQLiteDatabase = this.dbHelper
				.getWritableDatabase();

		Object[] arrayOfObject = new Object[3];
		arrayOfObject[0] = userPose.getUserID();
		arrayOfObject[1] = userPose.getLoginName();
		arrayOfObject[2] = userPose.getPassword();

		localSQLiteDatabase.execSQL("insert into userPose(user_id,login_name,password) values(?,?,?)",
				arrayOfObject);
		localSQLiteDatabase.close();
	}

	public ArrayList<UserPose> findUser() {
		ArrayList<UserPose> userPose = new ArrayList<>();
		SQLiteDatabase localSQLiteDatabase = this.dbHelper
				.getWritableDatabase();
		Cursor localCursor = localSQLiteDatabase.rawQuery("select *  from userPose", null);

		while (localCursor.moveToNext()) {
			UserPose temp = new UserPose();
			temp.setUserID(localCursor.getInt(localCursor
					.getColumnIndex("user_id")));
			temp.setLoginName(localCursor.getString(localCursor
					.getColumnIndex("login_name")));
			temp.setPassword(localCursor.getString(localCursor
					.getColumnIndex("password")));
			userPose.add(temp);
		}
		localSQLiteDatabase.close();
		return userPose;
	}

	public void updateUser(String username, String password) {
		SQLiteDatabase localSQLiteDatabase = this.dbHelper
				.getWritableDatabase();
		localSQLiteDatabase.execSQL("update userPose set login_name='" + username + "',password='" + password+"'");
		localSQLiteDatabase.close();
	}

	public void updateUser(int userId,String username, String password) {
		SQLiteDatabase localSQLiteDatabase = this.dbHelper
				.getWritableDatabase();
		localSQLiteDatabase.execSQL("update userPose set user_id="+userId+" ,login_name='" + username + "',password='" + password+"'");
		localSQLiteDatabase.close();
	}


	/**
	 * delTable
	 */
	public void delTable() {
		SQLiteDatabase localSQLiteDatabase = this.dbHelper
				.getWritableDatabase();
		localSQLiteDatabase.execSQL("delete from  userPose");
		localSQLiteDatabase.close();
	}

}

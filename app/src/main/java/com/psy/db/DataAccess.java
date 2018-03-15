package com.psy.db;

import com.psy.model.UserPose;


import android.content.Context;

public class DataAccess {
	public void initDatabase(Context context) {
		DBServer dbServer = new DBServer(context);
		UserPose userPose = new UserPose();
		userPose.setUserID(-1);
		userPose.setLoginName("");
		userPose.setPassword("");
		dbServer.addUser(userPose);
	}
}

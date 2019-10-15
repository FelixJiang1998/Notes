package com.note.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.note.ActivityManager;

public class TestActivityManage extends AndroidTestCase {
	private static final String TAG = "XU";
	ActivityManager manager = new ActivityManager();
	
	public void testReturnTIme()
	{
		String s = manager.returnTime();
		Log.i(TAG, s);
	}
}

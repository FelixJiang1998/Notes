package com.note.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharePreferences extends Activity {

    SharedPreferences sp;
    Context ctx;
    Editor editor;

    //得到实例
    public static SharePreferences getIntance() {
        SharePreferences preferences = new SharePreferences();
        return preferences;
    }

    //判断是否首次安装登录
    public boolean isFirstLogin(Context context) {
        ctx = context;
        sp = ctx.getSharedPreferences("myfile", MODE_PRIVATE);

        //如果调用该方法检测到键isFirst的值为yes,则判定为第一次安装进入应用
        if ("yes".equals(sp.getString("isFirst", "no"))) {
            editor = sp.edit();
            editor.putString("isFirst", "no");
            editor.commit();
            return true;
        }

        //反之则相反
        else if ("no".equals(sp.getString("isFirst", "no"))) {
            editor = sp.edit();
            editor.putString("isFirst", "yes");
            editor.commit();
            return false;
        }

        return false;

    }


    //写入的方法
    public void writeToPreferences(boolean value, Context context) {
        ctx = context;
        sp = ctx.getSharedPreferences("myfile", MODE_PRIVATE);
        //存入数据getSharedPreferences
        editor = sp.edit();
        editor.putBoolean("message_send", value);//消息推送
        editor.commit();
    }


    //获取班级ID
    public int getClassID(Context context) {
        sp = context.getSharedPreferences("myfile", MODE_PRIVATE);
        int ClassID = Integer.valueOf(sp.getString("classID", "null"));
        return ClassID;
    }


}

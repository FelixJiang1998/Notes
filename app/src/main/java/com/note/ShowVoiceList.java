package com.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.note.dao.SqliteDBConnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowVoiceList extends Activity {

    private static final String TAG = "XU";
    int count = 0;
    int category = 2;
    TextView image;
    private ListView listview;
    private EditText txtBlur = null;
    private Button btnAddNewText, btnAddNewVoice, btnAddNewVideo, btnAllNote, btnSearch, btnReturn;
    private SimpleAdapter adapter;
    private ActivityManager manage;
    private SQLiteDatabase db;
    private SqliteDBConnect connect;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_list);

        Toast.makeText(ShowVoiceList.this, "您进入了语音记录列表", Toast.LENGTH_SHORT).show();

        connect = new SqliteDBConnect(getApplicationContext());
        db = connect.getReadableDatabase();


        manage = ActivityManager.getInstance();
        manage.addActivity(this);

        ButtonClick click = new ButtonClick();
        btnAddNewVoice = (Button) this.findViewById(R.id.btnAddNewVoice2);
        btnSearch = (Button) this.findViewById(R.id.btnSearch2);
        btnAllNote = (Button) this.findViewById(R.id.btnAll2);
        btnAddNewText = (Button) this.findViewById(R.id.btnAddNewVoice2);
        btnAddNewVideo = (Button) this.findViewById(R.id.btnAddNewVideo2);
        btnReturn = (Button) this.findViewById(R.id.btnReturn2);

        btnSearch.setOnClickListener(click);
        btnAllNote.setOnClickListener(click);
        btnAddNewVoice.setOnClickListener(click);
        btnAddNewVideo.setOnClickListener(click);
        btnAddNewText.setOnClickListener(click);
        btnReturn.setOnClickListener(click);
        //关键字查询搜索的文本输入框
        txtBlur = (EditText) this.findViewById(R.id.search_content2);

        listview = (ListView) findViewById(R.id.listview2);

        connect = new SqliteDBConnect(ShowVoiceList.this);

        disPlay();//从数据库提取数据并显示

        listview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) arg0.getItemAtPosition(arg2);
                Intent intent = new Intent();
                intent.putExtra("noteId", map.get("noteId").toString());
                intent.putExtra("category", "voice");
                intent.setClass(ShowVoiceList.this, DisPlay.class);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                startActivity(intent);
                finish();
            }
        });
        listview.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                @SuppressWarnings("unchecked") final Map<String, Object> map = (Map<String, Object>) arg0
                        .getItemAtPosition(arg2);
                AlertDialog.Builder adb = new Builder(ShowVoiceList.this);
                adb.setTitle(map.get("noteName").toString());
                adb.setItems(new String[]{"删 除", "修 改", "添加", "设置闹钟"},
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        SQLiteDatabase sdb = connect.getReadableDatabase();
                                        sdb.delete("note", "noteId=?", new String[]{map.get("noteId").toString()});
                                        Toast.makeText(ShowVoiceList.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        sdb.close();
                                        disPlay();
                                        break;
                                    case 1:
                                        Intent intent = new Intent();
                                        intent.putExtra("noteId", map.get("noteId").toString());
                                        intent.putExtra("category", "voice");
                                        intent.setClass(ShowVoiceList.this, DisPlay.class);
                                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                        startActivity(intent);
                                        finish();
                                        break;
                                }
                            }
                        });
                adb.show();
                return false;
            }
        });

    }


    public void disPlay() {
        SQLiteDatabase sdb = connect.getReadableDatabase();
        count = 0;
        Cursor c1 = sdb.query("note", new String[]{"noteId", "noteName",
                "noteTime"}, null, null, null, null, "noteId asc");
        while (c1.moveToNext()) {
            int noteid = c1.getInt(c1.getColumnIndex("noteId"));
            if (noteid > count)
                count = noteid;
        }
        c1.close();

        Cursor c = sdb.query("note", new String[]{"noteId", "noteName",
                        "noteTime", "category"}, "category = ?", new String[]{String.valueOf(category)},
                null, null, "noteId asc");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        while (c.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            String strName = c.getString(c.getColumnIndex("noteName"));

            if (strName.length() > 6) {
                map.put("noteName", strName.substring(0, 6) + "…");
            } else {
                map.put("noteName", strName);
            }

            map.put("noteTime", c.getString(c.getColumnIndex("noteTime")));
            map.put("noteId", c.getInt(c.getColumnIndex("noteId")));
            list.add(map);
        }
        c.close();
        sdb.close();
        if (count > 0) {
            adapter = new SimpleAdapter(ShowVoiceList.this, list, R.layout.items,
                    new String[]{"noteName", "noteTime"}, new int[]{
                    R.id.noteName, R.id.noteTime});
            listview.setAdapter(adapter);
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setClass(ShowVoiceList.this, MainUIActivity.class);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
            startActivity(intent);
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    class ButtonClick implements View.OnClickListener {

        private static final String TAG = "XU";

        public void onClick(View v) {
            if (v.getId() == R.id.btnAddNewVoice2) {
                Intent intent = new Intent();
                intent.setClass(ShowVoiceList.this, AddNewVoice.class);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                startActivity(intent);
                finish();
            } else if (v.getId() == R.id.btnAddNewText2) {
                Intent intent = new Intent();
                intent.setClass(ShowVoiceList.this, AddNewText.class);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                startActivity(intent);
                finish();
            } else if (v.getId() == R.id.btnAddNewVideo2) {
                Toast.makeText(ShowVoiceList.this, "由于时间有限暂未完成该模块", Toast.LENGTH_LONG).show();
            }

            //搜索按钮的事件处理
            else if (v.getId() == R.id.btnSearch2) {
                int count = 0;
                String keyStr = txtBlur.getText().toString().trim();
                //模糊查询的语句
                String sql = "select * from note where noteName like '%" + keyStr + "%'";
                Cursor cursor = db.rawQuery(sql, null);
                List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                while (cursor.moveToNext()) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    String name = cursor.getString(cursor.getColumnIndex("noteName"));
                    String time = cursor.getString(cursor.getColumnIndex("noteTime"));
                    map.put("noteName", name);
                    map.put("noteTime", time);
                    list.add(map);
                    count++;
                }

                if (count == 0) {
                    Toast.makeText(ShowVoiceList.this, "没有搜索到相关记录", Toast.LENGTH_LONG).show();
                }
                adapter = new SimpleAdapter(ShowVoiceList.this, list, R.layout.items,
                        new String[]{"noteName", "noteTime"}, new int[]{
                        R.id.noteName, R.id.noteTime});
                listview.setAdapter(adapter);
                txtBlur.setText("");
            } else if (v.getId() == R.id.btnAll2) {
                disPlay();
                txtBlur.setText("");
            } else if (v.getId() == R.id.btnReturn2) {
                Intent intent = new Intent(ShowVoiceList.this, MainUIActivity.class);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                startActivity(intent);
                finish();
            }
        }

    }

}

package com.YT.memo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class SubListActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_list);

		//ListViewの生成
		ListView listview;

		Intent getIntent = getIntent();

		String query = getIntent.getStringExtra("query");

		try{

			Cursor c = DatabaseHelper.getInstance(SubListActivity.this).searchRow(query);

			if(c.getCount() == 0){
				Toast.makeText(SubListActivity.this, "nothing!!", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(SubListActivity.this, MainActivity.class);
				SubListActivity.this.startActivity(intent);
				finish();
			}

			c.moveToFirst();

			//SQLiteの中身の有無で分岐
			if(c.getCount() != 0){
				//レコードの数だけループ
				final List<Map<String, String>> DataList = new ArrayList<Map<String, String>>();
				for(int i=0; i<c.getCount(); i++){
					Map<String, String> data = new HashMap<String, String>();
					data.put("id", c.getString(0));
					data.put("title", c.getString(2));
					data.put("time", Tool.getInstance().formDate(Long.parseLong(c.getString(1))));
					DataList.add(data);
					c.moveToNext();
					}
				final SimpleAdapter adapter = new SimpleAdapter(
					this,
					DataList,
					android.R.layout.simple_list_item_2,
					new String[] { "title","time" },
					new int[] { android.R.id.text1,android.R.id.text2 }
				);

				//listviewにadapter実装
				listview = (ListView) findViewById(R.id.listView2);
				listview.setAdapter(adapter);

				listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					//各行をタッチした時の動き
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id){
						ListView listView = (ListView) parent;
						String item = listView.getItemAtPosition(position).toString();
						String key = item.substring(4, item.indexOf(","));
						Cursor row = DatabaseHelper.getInstance(SubListActivity.this).getData(key);
						row.moveToFirst();

						//textactivityに値を保持して遷移する処理
						Intent intent = new Intent(SubListActivity.this, TextActivity.class);
						intent.putExtra("id", row.getString(0));
						intent.putExtra("date", row.getString(1));
						intent.putExtra("title", row.getString(2));
						intent.putExtra("contents", row.getString(3));
						SubListActivity.this.startActivity(intent);
						SubListActivity.this.finish();
						row.close();
					}
				});
			}else{
				Toast.makeText(SubListActivity.this, "Note is nothing", Toast.LENGTH_LONG).show();
				return;
			}
		}catch(Exception e){
			Toast.makeText(SubListActivity.this, "error", Toast.LENGTH_LONG).show();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main2, menu);
		return true;
	}

	//backButton押下時の処理
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent(SubListActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			return true;
		}
		return false;
	}
}

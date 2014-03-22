package com.YT.memo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class TextActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text);

		//ListActivityから受け取った値
		Intent getIntent = getIntent();
		final String id = getIntent.getStringExtra("id");
		final String date = Tool.getInstance().formDate(Long.parseLong(getIntent.getStringExtra("date")));
		String title = getIntent.getStringExtra("title");
		String contents = getIntent.getStringExtra("contents");

		TextView textView1 = (TextView) findViewById(R.id.textView1);
		textView1.setText(date);

		EditText editText3 = (EditText) findViewById(R.id.editText3);
		editText3.setText(contents);

		EditText editText4 = (EditText) findViewById(R.id.editText4);
		editText4.setText(title);

		//データベースの呼び出し
		final SQLiteDatabase db = DatabaseHelper.getInstance(TextActivity.this).getWritableDatabase();

		//OKボタンのクリックイベント
		Button button3 = (Button)findViewById(R.id.button3);
		button3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					//テキストの内容を取得
					EditText text3 = (EditText)findViewById(R.id.editText3);
					EditText text4 = (EditText)findViewById(R.id.editText4);
					String editText3 = text3.getText().toString();
					String editText4 = text4.getText().toString();

					//titleが未入力のときは処理しない(java1.6ではisEmpty()でエラーが出る模様...)
					if(editText4.length() == 0){
						Toast.makeText(TextActivity.this, "Title is required", Toast.LENGTH_LONG).show();
					}else{

						//update文作成
						ContentValues val = new ContentValues();
						val.put("date", Tool.getInstance().getMillis());
						val.put("title", editText4);
						val.put("contents", editText3);
						db.update("Memo", val, "id = '" + id + "'", null);

						//ダイアログ表示
						Toast.makeText(TextActivity.this, "Registration", Toast.LENGTH_LONG).show();
						ListFragment.DataList.clear();

						//ListActivityに遷移する
						Intent intent = new Intent(TextActivity.this, MainActivity.class);
						startActivity(intent);

						//Activityを終了する
						finish();
					}
				}catch(Exception e){
					Log.e("log", "error");
					Toast.makeText(TextActivity.this, "error", Toast.LENGTH_LONG).show();
				}

			}
		});
	}

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
			Intent intent = new Intent(TextActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			return true;
		}
		return false;
	}
}
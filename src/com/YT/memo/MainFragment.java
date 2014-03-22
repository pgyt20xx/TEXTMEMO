package com.YT.memo;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.main, container, false);

        final Activity act = getActivity();

		//addボタンのタッチイベント
		Button button1 = (Button) v.findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					//テキストの内容を取得
					EditText text1 = (EditText) getView().findViewById(R.id.editText1);
					EditText text2 = (EditText) getView().findViewById(R.id.editText2);
					String editText1 = text1.getText().toString();
					String editText2 = text2.getText().toString();

					//titleが未入力のときは処理しない(java1.6ではisEmpty()でエラーが出る模様...)
					if(editText2.length() == 0){
						Toast.makeText(act, "Title is required", Toast.LENGTH_LONG).show();
					}else{
						//データベースの呼び出し
						SQLiteDatabase db = DatabaseHelper.getInstance(act).getWritableDatabase();

						//インサート文作成
						ContentValues val = new ContentValues();
						val.put("date", Tool.getInstance().getMillis());
						val.put("title", editText2);
						val.put("contents", editText1);
						db.insert("Memo", null, val);

						//テキストを初期化
						text1.setText("");
						text2.setText("");

	            		new Thread(new Runnable(){

	            			//Handlerの作成(androidではHandlerにThread処理を渡さなければ例外が発生)
	            			Handler handler = new Handler();

							@Override
							public void run() {
								handler.post(new Runnable(){

									@Override
									public void run() {

										//15件の取得 Adapterに追加
										Cursor c2 = DatabaseHelper.getInstance(getActivity()).getList();
										c2.moveToFirst();
										ListFragment.DataList.clear();

										//List<Map<String, String>> DataList = new ArrayList<Map<String, String>>();
										for(int i=0; i<c2.getCount(); i++){
											//adapter.add(c2.getString(1) + "\r\n" + c2.getString(2));
											//adapter.add(c2.getString(2) + "\r\n" + "　　　　　　　　" + c2.getString(1));
											Map<String, String> data = new HashMap<String, String>();
											data.put("id", c2.getString(0));
											data.put("title", c2.getString(2));
											data.put("time", Tool.getInstance().formDate(c2.getLong(1)));
											ListFragment.DataList.add(data);
											if(i == (c2.getCount() - 1)){
												try {
													ListFragment.lastDate = Long.parseLong(c2.getString(1));
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
//											adapter.notifyDataSetChanged();

//											SimpleAdapter adapter2 = new SimpleAdapter(
//											    ListActivity.this,
//											    DataList,
//											    android.R.layout.simple_list_item_2,
//											    new String[] { "title","time" },
//											    new int[] { android.R.id.text1,android.R.id.text2 }
//											);
//											listview.setAdapter(adapter2);
											c2.moveToNext();
										}
										ListFragment.adapter.notifyDataSetChanged();
										c2.close();

									}

								});
							}

	            		}).start();

						//ダイアログ表示
						Toast.makeText(act, "Registration", Toast.LENGTH_LONG).show();
					}
				}catch(Exception e){
					Log.e("log", "error");
					Toast.makeText(act, "error", Toast.LENGTH_LONG).show();
				}

			}
		});

		EditText editText1 = (EditText)v.findViewById(R.id.editText1);
		editText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        // EditTextのフォーカスが外れた場合
		        if (hasFocus == false) {
		        	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		        }
		    }
		});

		EditText editText2 = (EditText)v.findViewById(R.id.editText2);
		editText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        // EditTextのフォーカスが外れた場合
		        if (hasFocus == false) {
		        	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		        }
		    }
		});
        return v;
    }


}




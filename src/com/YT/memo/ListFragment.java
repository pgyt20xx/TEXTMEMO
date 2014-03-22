package com.YT.memo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ListFragment extends Fragment implements OnQueryTextListener {

	//listview最下部の日付
	static long lastDate = 0L;

	//Adapterの生成
	static SimpleAdapter adapter;

	//スレッドが連発するのを防ぐためのフラグ
	static int flag = 0;

	//ListViewの生成
	static ListView listview;

	static List<Map<String, String>> DataList = new ArrayList<Map<String, String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	DataList.clear();

    	//データベースの呼び出し
    	DatabaseHelper.getInstance(getActivity());

        View v = inflater.inflate(R.layout.list, container, false);


		try{

			//全てのレコードを取得するメソッドの呼び出し
			Cursor c = DatabaseHelper.getInstance(getActivity()).getList();

			//SQLiteの中身の有無で分岐




			//listview生成
			updateList(v, c, DataList);
			c.close();

			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				//各行をタッチした時の動き
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id){
					ListView listView = (ListView) parent;
					String item = listView.getItemAtPosition(position).toString();
					String key = item.substring(4, item.indexOf(","));
					Cursor row = DatabaseHelper.getInstance(getActivity()).getData(key);
					row.moveToFirst();

					//textactivityに値を保持して遷移する処理
					Intent intent = new Intent(getActivity(), TextActivity.class);
					intent.putExtra("id", row.getString(0));
					intent.putExtra("date", row.getString(1));
					intent.putExtra("title", row.getString(2));
					intent.putExtra("contents", row.getString(3));
					getActivity().startActivity(intent);
					getActivity().finish();
					row.close();
				}
			});

			//ListView最下部までスクロールしたら次の15件を取得する
	        listview.setOnScrollListener(new OnScrollListener() {

	            @Override
	            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	            	int judgement = firstVisibleItem + visibleItemCount;


	            	//最下部までスクロールしたことを判定
	            	if(judgement == totalItemCount && flag != firstVisibleItem){
	            		new Thread(new Runnable(){

	            			//Handlerの作成(androidではHandlerにThread処理を渡さなければ例外が発生)
	            			Handler handler = new Handler();

	            			Cursor c2 = addRow(lastDate);

							@Override
							public void run() {
								handler.post(new Runnable(){

									@Override
									public void run() {
										//次の15件の取得 Adapterに追加
										if(c2.getCount() == 0) return;
										c2.moveToLast();
										if(Long.parseLong(c2.getString(1)) == lastDate) return;

										c2.moveToFirst();

										//List<Map<String, String>> DataList = new ArrayList<Map<String, String>>();
										for(int i=0; i<c2.getCount(); i++){
											//adapter.add(c2.getString(1) + "\r\n" + c2.getString(2));
											//adapter.add(c2.getString(2) + "\r\n" + "　　　　　　　　" + c2.getString(1));
											Map<String, String> data = new HashMap<String, String>();
											data.put("id", c2.getString(0));
											data.put("title", c2.getString(2));
											data.put("time", Tool.getInstance().formDate(Long.parseLong(c2.getString(1))));
											DataList.add(data);
											if(i == (c2.getCount() - 1)){
												try {
													lastDate = Long.parseLong(c2.getString(1));
												} catch (Exception e) {
													e.printStackTrace();
												}
											}

//												adapter.notifyDataSetChanged();

//												SimpleAdapter adapter2 = new SimpleAdapter(
//												    ListActivity.this,
//												    DataList,
//												    android.R.layout.simple_list_item_2,
//												    new String[] { "title","time" },
//												    new int[] { android.R.id.text1,android.R.id.text2 }
//												);
//												listview.setAdapter(adapter2);
											c2.moveToNext();
										}
										adapter.notifyDataSetChanged();
										c2.close();
									}
								});
							}
	            		}).start();
	            	}
	            	flag = firstVisibleItem;
	            }

	            @Override
	            public void onScrollStateChanged(AbsListView arg0, int arg1) {
	            }
	        });
		}catch(Exception e){
			Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
		}
		return v;
	}




    private Cursor addRow(long lastDate2){
    	return DatabaseHelper.getInstance(getActivity()).moreList(lastDate2);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            listview.clearTextFilter();
        } else {
            listview.setFilterText(newText.toString());
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public void updateList(View v, Cursor c, List<Map<String, String>> DataList) throws ParseException{
    	if(DataList.size() != 0) return;
		c.moveToFirst();
		//レコードの数だけループ
		for(int i=0; i<c.getCount(); i++){
			Map<String, String> data = new HashMap<String, String>();
			data.put("title", c.getString(2));
			data.put("time", Tool.getInstance().formDate(c.getLong(1)));
			data.put("id", c.getString(0));
			DataList.add(data);
			if(i == (c.getCount() - 1)){
				lastDate = Long.parseLong(c.getString(1));
			}
			c.moveToNext();
		}

		adapter = new SimpleAdapter(
			getActivity(),
			DataList,
			android.R.layout.simple_list_item_2,
			new String[] { "title","time" },
			new int[] { android.R.id.text1,android.R.id.text2 }
		);

		//List<Map<String, String>> dataList = retDataList;

		//listviewにadapter実装
		listview = (ListView) v.findViewById(R.id.listView1);
		listview.setAdapter(adapter);

		//listviewにtextfilterを実装
		listview.setTextFilterEnabled(true);
		c.close();
    }
}
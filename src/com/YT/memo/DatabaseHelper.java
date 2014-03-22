package com.YT.memo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

	private static final String DB_NAME = "Memo.db";
	private static final String TABLE = "Memo";
	private static final int DB_VARSION = 1;
	private static final String CREATE_TABLE = "create table "
							+ TABLE + "(" +"id integer primary key autoincrement,"
							+ "date integer not null,"
							+ "title varchar(30) not null,"
							+ "contents varchar(30) not null);";

	//singletonパターンを作成
	private static DatabaseHelper instance = null;

	//コンストラクタ
	private DatabaseHelper(Context context){
		super(context, DB_NAME, null, DB_VARSION);
	}

	//instanceがnullなら作成する
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

	}

	//最新のデータを15件取得するメソッド
	public Cursor getList(){
		String sql = "select * from Memo order by date desc limit 15";
		Cursor c = this.getWritableDatabase().rawQuery(sql, null);
		return c;
	}

	//指定の日付以降のデータを15件取得するメソッド
	public Cursor moreList(long lastDate2){
		String sql = "select * from Memo where date < '" + lastDate2 + "' order by id desc limit 15";
		Cursor c = this.getWritableDatabase().rawQuery(sql, null);
		return c;
	}

	//idからレコードを取得するメソッド
	public Cursor getData(String key){
		String sql = "select * from Memo where id = " + "'" + key + "'";
		Cursor c = this.getWritableDatabase().rawQuery(sql, null);
		return c;
	}

	//キーワードによる検索をするメソッド
	public Cursor searchRow(String word){
		String sql = "select * from Memo where title Like '" + word + "%' or contents Like '" + word + "%' order by date desc";
		Cursor c = this.getWritableDatabase().rawQuery(sql, null);
		return c;
	}
}
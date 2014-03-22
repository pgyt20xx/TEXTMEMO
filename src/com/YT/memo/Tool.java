package com.YT.memo;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

//共通関数を定義
public class Tool{

	//singletonパターンを作成
	private static Tool instance = new Tool();
	private Tool(){}
	public static Tool getInstance(){
		return instance;
	}

/**
 * 現在日時を取得するメソッド */
	public String today(){

		//現在の日時取得
		Date date = new Date();

		//ロケール取得
		Locale locale = new Locale("ja", "JP");

		//フォーマット取得
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);

		//現在の日時を取得したフォーマットに適応
		String today = df.format(date);

		return today;
	}

/**
* 現在のミリ秒を取得するメソッド
 * @throws ParseException */
	public long getMillis() throws ParseException{
		Date date = new Date();
		Long todayMilli = date.getTime();
		return todayMilli;
	}

/**
* ミリ秒を日付に変換するメソッド */
	public String formDate(long dateMilli){
		//ロケール取得
		Locale locale = new Locale("ja", "JP");

		//フォーマット取得
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);

		//フォーマットの適用
		String date = df.format(dateMilli);

		return date;
	}

/**
* 日付をミリ秒に変換するメソッド */
	public long formMillis(String date) throws ParseException{
		//StringをDate型に変換
		Locale locale = new Locale("ja", "JP");
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
		Date format = df.parse(date);


		//変換したdateをミリ秒に変換
		long millis = format.getTime();
		return millis;
	}
}
package com.dumpit.ffff;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table IF NOT EXISTS incheon_medicine  ("
                + "_id integer primary key autoincrement,"
                + "번호 INTEGER," + "약국 TEXT," + "읍면 TEXT," + "주소 TEXT," + "전화번호 INTEGER);";

//        String fs = "create table IF NOT EXISTS firstsemester  ("
//                + "_id integer primary key autoincrement,"
//                + "순번 TEXT, " + "개설학과전공 TEXT," + "학수번호 TEXT," + "교과목명 TEXT," + "분반 TEXT," + "이수구분 TEXT," + "영역 TEXT," + "학점 REAL," + "수강정원 INTEGER," +"시간표 TEXT," + "강의실 TEXT," + "담당교수 TEXT," +"캠퍼스 TEXT," + "수업유형 TEXT," + "평가등급유형 TEXT," + "수강안내사항 TEXT," + "대상자지정내용 TEXT);";
//
//
//
//        String ss = "create table IF NOT EXISTS secondsemester  ("
//                + "_id integer primary key autoincrement,"
//                + "순번 TEXT, " + "개설학과전공 TEXT," + "학수번호 TEXT," + "교과목명 TEXT," + "분반 TEXT," + "이수구분 TEXT," + "영역 TEXT," + "학점 REAL," + "수강정원 INTEGER," +"시간표 TEXT," + "강의실 TEXT," + "담당교수 TEXT," +"캠퍼스 TEXT," + "수업유형 TEXT," + "평가등급유형 TEXT," + "수강안내사항 TEXT," + "대상자지정내용 TEXT);";
//
//        String summer = "create table IF NOT EXISTS summersemester  ("
//                + "_id integer primary key autoincrement,"
//                + "순번 TEXT, " + "개설학과전공 TEXT," + "학수번호 TEXT," + "교과목명 TEXT," + "분반 TEXT," + "이수구분 TEXT," + "영역 TEXT," + "학점 REAL," + "수강정원 INTEGER," +"시간표 TEXT," + "강의실 TEXT," + "담당교수 TEXT," +"캠퍼스 TEXT," + "수업유형 TEXT," + "평가등급유형 TEXT," + "수강안내사항 TEXT," + "대상자지정내용 TEXT);";

//        db.execSQL(fs);
//        db.execSQL(ss);
//        db.execSQL(summer);

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS incheon_medicine";
//        String fs = "DROP TABLE IF EXISTS firstsemester";
//        String ss = "DROP TABLE IF EXISTS secondsemester";
//        String summer = "DROP TABLE IF EXISTS summersemester";


//        db.execSQL(fs);
//        db.execSQL(ss);
//        db.execSQL(summer);
        db.execSQL(sql);
        onCreate(db);
    }


}
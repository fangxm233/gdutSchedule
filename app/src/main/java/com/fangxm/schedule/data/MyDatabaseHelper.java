package com.fangxm.schedule.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {
//表1 课程信息
    public static final String CREATE_Course = "create table Course(" +
            "type text ," +
            "title text," +
            "classes text," +
            "startNum integer,"+
            "length integer,"+
            "weekNum integer,"+
            "weekDate integer,"+
            "classroom text,"+
            "teacherName text,"+
            "color text)";
//表2 学期课程
    public static final String CREATE_TermCourse = "create table TermCourse(" +
            "course text,"+
            "termId text)";
//表3 登录信息
    public static final String CREATE_Account = "create table Account(" +
            "accountNumber text,"+
            "password text,"+
            "cookie text)";
//表4 学期时间
    public static final String CREATE_TermTime = "create table TermTime(" +
            "termId text,"+
            "startTime text)";

    private Context mContext;
    //构造方法：第一个参数Context，第二个参数数据库名，第三个参数cursor允许我们在查询数据的时候返回一个自定义的光标位置，一般传入的都是null，第四个参数表示目前库的版本号（用于对库进行升级）
    public MyDatabaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_Course);
        db.execSQL(CREATE_TermCourse);
        db.execSQL(CREATE_Account);
        db.execSQL(CREATE_TermTime);
        ContentValues values = new ContentValues();//创建临时变量用于存放数据
        String Null=null;
        values.put("accountNumber",Null);
        values.put("password",Null);
        values.put("cookie",Null);
        db.insert("Account",null,values);// 插入数据
        values.clear();
        Toast.makeText(mContext, "创建成功", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

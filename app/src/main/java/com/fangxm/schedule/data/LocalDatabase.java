package com.fangxm.schedule.data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class LocalDatabase{

    private MyDatabaseHelper dbHelper;

    LocalDatabase(Context context) {
        dbHelper = new MyDatabaseHelper(context,"Course.db",null,1);
    }
//存数据
    public void SaveTermData(String termId, TermContent data) {
//先存入学期课程表 表2
        SQLiteDatabase db1=dbHelper.getWritableDatabase();
        db1.execSQL("delete from TermCourse");
        db1.execSQL("delete from Course");
        ContentValues values1 = new ContentValues();//创建临时变量用于存放数据
        for(String key:data.getCourses().keySet()){
            values1.put("course",key);
            values1.put("termId",termId);
            db1.insert("TermCourse",null,values1);// 插入数据
        }
        values1.clear();
//存入课程信息 表1
        ContentValues values2 = new ContentValues();//创建临时变量用于存放数据
        for(CourseContent values:data.getCourses().values()){
            for(ClassContent class_content:values.getClasses()) {
                values2.put("type", class_content.getType());
                values2.put("title", class_content.getTitle());
                values2.put("classes",class_content.getClasses());
                values2.put("startNum", class_content.getStartNum());
                values2.put("length", class_content.getLength());
                values2.put("weekNum", class_content.getWeekNum());
                values2.put("weekDate", class_content.getWeekDate());
                values2.put("classroom", class_content.getClassroom());
                values2.put("teacherName", class_content.getTeacherName());
                values2.put("color", class_content.getColor());
                db1.insert("Course",null,values2);// 插入数据
            }
        }
        values2.clear();
        //存入学期时间 表4
        ContentValues values3 = new ContentValues();//创建临时变量用于存放数据
                values3.put("termId", data.getTermId());
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");//将时间转化为字符串以便存入
                values3.put("startTime", sdf.format(data.getStartDate().getTime()));
                db1.insert("termTime",null,values3);// 插入数据
    }



    //取数据
    public TermContent GetTermData(String termId) {
        //读取学期课程内容 表2
        ArrayList<String> termCourse=new ArrayList<String>();//暂存获取到的该学期课程
        String selection1="termId=?";
        String[] selectionArgs1 = new  String[]{ termId };
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        Cursor cursor1 = db1.query("TermCourse",null,selection1,selectionArgs1,null,null,null);
        if(cursor1.moveToFirst()){
            do{
                // 遍历Cursor对象，取出数据
                @SuppressLint("Range") String course = cursor1.getString(cursor1.getColumnIndex("course"));

                termCourse.add(course);
            }while(cursor1.moveToNext());
        }
//读取课程内容 表1
        TermContent term_content=new TermContent(termId);
        term_content.setCourses(new HashMap<>());
        for(String courseTitle:termCourse) {
            System.out.println(courseTitle);
            ArrayList<ClassContent> classList=new ArrayList<>();
            SQLiteDatabase db2 = dbHelper.getWritableDatabase();
            String selection2 = "title=?";
            String[] selectionArgs2 = new  String[]{ courseTitle };
            // 查询Course表中所有的数据
            Cursor cursor2 = db2.query("Course", null, selection2, selectionArgs2, null, null, null);
            System.out.println(cursor2.getCount());
            if (cursor2.moveToFirst()) {
                String title = null;
                do {
                    // 遍历Cursor对象，取出数据
                    @SuppressLint("Range") String type = cursor2.getString(cursor2.getColumnIndex("type"));
                    title = cursor2.getString(cursor2.getColumnIndex("title"));
                    @SuppressLint("Range") String classes = cursor2.getString(cursor2.getColumnIndex("classes"));
                    @SuppressLint("Range") int startNum = cursor2.getInt(cursor2.getColumnIndex("startNum"));
                    @SuppressLint("Range") int length = cursor2.getInt(cursor2.getColumnIndex("length"));
                    @SuppressLint("Range") int weekNum = cursor2.getInt(cursor2.getColumnIndex("weekNum"));
                    @SuppressLint("Range") int weekDate = cursor2.getInt(cursor2.getColumnIndex("weekDate"));
                    @SuppressLint("Range") String classroom = cursor2.getString(cursor2.getColumnIndex("classroom"));
                    @SuppressLint("Range") String teacherName = cursor2.getString(cursor2.getColumnIndex("teacherName"));
                    @SuppressLint("Range") String color = cursor2.getString(cursor2.getColumnIndex("color"));
                    ClassContent A_Class=new ClassContent(type,title,classes,startNum,length,weekNum,weekDate,classroom,teacherName,color);
                    classList.add(A_Class);
                } while (cursor2.moveToNext());
                CourseContent A_Course=new CourseContent(title, classList);
                term_content.getCourses().put(title,A_Course);
            }
            cursor2.close();
        }
        System.out.println(term_content.getCourses().size());
        //读取学期时间内容 表4
        String selection3 = "termId=?";
        String[] selectionArgs3 = new  String[]{ termId };
        Cursor cursor3 = db1.query("TermTime",null,selection3,selectionArgs3,null,null,null);
        if(cursor3.moveToFirst()){
            do{
                // 遍历Cursor对象，取出数据
                @SuppressLint("Range") String starttime = cursor3.getString(cursor3.getColumnIndex("startTime"));
                SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date =sdf.parse(starttime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                term_content.setStartDate(calendar);
            }while(cursor3.moveToNext());
        }
        return term_content;
    }

    public boolean HasTermData(String termId) {
        SQLiteDatabase db3 = dbHelper.getWritableDatabase();
        String selection3="termId=?";
        String[] selectionArgs3 = new  String[]{ termId };
        Cursor cursor3 = db3.query("TermCourse",null,selection3,selectionArgs3,null,null,null);
        Cursor cursor4 = db3.query("TermCourse",null,null,null,null,null,null);
        if(cursor3.getCount()>0)
            return true;
        else
            return false;
    }

//存入账户数据 表4
    public void SaveAccount(String account, String password) {
        SQLiteDatabase db1=dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("accountNumber",account);
        values.put("password",password);
        db1.update("Account", values,null,null);
    }
//读取账户数据 表3
    public Pair<String, String> getAccount() {
        String[] selectionArgs = new  String[]{ "accountNumber", "password"};
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        Cursor cursor1 = db1.query("Account",selectionArgs,null,null,null,null,null);
        if(cursor1.moveToFirst()) {
            do {
                @SuppressLint("Range") String accountNumber = cursor1.getString(cursor1.getColumnIndex("accountNumber"));
                @SuppressLint("Range") String password = cursor1.getString(cursor1.getColumnIndex("password"));
                return new Pair(accountNumber,password);
            } while (cursor1.moveToNext());
        }
        return null;
    }
//存入账户cookie 表3
    public void setCookie(String cookie) {
        SQLiteDatabase db1=dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cookie",cookie);
        db1.update("Account", values,null,null);
    }
//读取账户cookie 表3
    public String getCookie() {
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        Cursor cursor1 = db1.query("Account",null,null,null,null,null,null);
        if(cursor1.moveToFirst()) {
            do {
                @SuppressLint("Range") String cookie = cursor1.getString(cursor1.getColumnIndex("cookie"));
                return cookie;
            } while (cursor1.moveToNext());
        }
        return null;
    }

    public void ClearAccount() {
        SQLiteDatabase db1=dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String Null=null;
        values.put("accountNumber",Null);
        values.put("password",Null);
        values.put("cookie",Null);
        db1.update("Account", values,null,null);
    }

    public Boolean IsSavedAccount() {
        String[] selectionArgs = new  String[]{ "accountNumber"};
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        Cursor cursor1 = db1.query("Account",selectionArgs,null,null,null,null,null);
        if(cursor1.moveToFirst()) {
            do {
                @SuppressLint("Range") String accountNumber = cursor1.getString(cursor1.getColumnIndex("accountNumber"));
                if(accountNumber==null)
                    return false;
                else
                    return true;
            } while (cursor1.moveToNext());
        }
        return false;
    }
}

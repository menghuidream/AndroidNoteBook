package com.menghuidream.notebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseOperation {
    SQLiteOpenHelper helper;
    SQLiteDatabase db;

    private static final String[] columns = {
        NoteDatabase.ID,
        NoteDatabase.TITLE,
        NoteDatabase.CONTENT,
        NoteDatabase.TIME,
        NoteDatabase.TAG
    };

    public DatabaseOperation(Context context) {
        helper = new NoteDatabase(context);
    }

    public void open() {
        db = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }

    // 添加笔记数据到数据库
    public NoteBean addNoteBean(NoteBean noteBean) {
        ContentValues values = new ContentValues();
        values.put(NoteDatabase.TITLE, noteBean.getTitle());
        values.put(NoteDatabase.CONTENT, noteBean.getContent());
        values.put(NoteDatabase.TIME, noteBean.getTime());
        values.put(NoteDatabase.TAG, noteBean.getTag());
        long insertId = db.insert(NoteDatabase.TABLE_NAME, null, values);
        noteBean.setId(insertId);
        return noteBean;
    }

    //根据id获取笔记数据
    public NoteBean getNoteBean(long id) {
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME, columns, NoteDatabase.ID + " =? " ,
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        NoteBean noteBean = new NoteBean(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4));
        cursor.close();
        return noteBean;
    }

    //获取所有笔记数据
    public List<NoteBean> getAllNoteBeans() {
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME, columns, null, null, null, null, null);
        List<NoteBean> noteBeans = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                NoteBean noteBean = new NoteBean();
                noteBean.setId(cursor.getLong(cursor.getColumnIndex(NoteDatabase.ID)));
                noteBean.setTitle(cursor.getString(cursor.getColumnIndex(NoteDatabase.TITLE)));
                noteBean.setContent(cursor.getString(cursor.getColumnIndex(NoteDatabase.CONTENT)));
                noteBean.setTime(cursor.getString(cursor.getColumnIndex(NoteDatabase.TIME)));
                noteBean.setTag(cursor.getInt(cursor.getColumnIndex(NoteDatabase.TAG)));
                noteBeans.add(noteBean);
            }
        }
        return noteBeans;
    }

    //更新笔记数据
    public int updateNoteBean(NoteBean noteBean) {
        ContentValues values = new ContentValues();
        values.put(NoteDatabase.TITLE, noteBean.getTitle());
        values.put(NoteDatabase.CONTENT, noteBean.getContent());
        values.put(NoteDatabase.TIME, noteBean.getTime());
        values.put(NoteDatabase.TAG, noteBean.getTag());
        return db.update(NoteDatabase.TABLE_NAME, values, NoteDatabase.ID + " =? ", new String[]{String.valueOf(noteBean.getId())});
    }

    //删除笔记数据
    public void removeNoteBean(NoteBean noteBean) {
        db.delete(NoteDatabase.TABLE_NAME, NoteDatabase.ID + " =? ", new String[]{String.valueOf(noteBean.getId())});
    }

}

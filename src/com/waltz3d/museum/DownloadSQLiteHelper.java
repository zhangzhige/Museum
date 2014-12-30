package com.waltz3d.museum;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;

public class DownloadSQLiteHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;

	private static final String DB_NAME = "downzipinfo.db";

	public static class DownloadNotes {
		public static final String TABLE_NAME = "installInfo";

		public static final String KEY_ID = "_id";
		public static final String HASH_KEY = "hashkey";
		public static final String ZIP_PATH = "originZipPath";
		public static final String IMG_PATH = "imgPath";
	}

	public DownloadSQLiteHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE " + DownloadNotes.TABLE_NAME + " (" + 
				DownloadNotes.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + 
				DownloadNotes.HASH_KEY + " TEXT,"+ 
				DownloadNotes.ZIP_PATH + " TEXT," + 
				DownloadNotes.IMG_PATH + " TEXT" + " )";
		try {
			db.beginTransaction();
			db.execSQL(CREATE_TABLE);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	public long insertData(DownloadInfo data) {
		SQLiteDatabase db = null;
		try {
			db = this.getReadableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(DownloadNotes.HASH_KEY, data.urlHashKey);
			cv.put(DownloadNotes.ZIP_PATH, data.originZipPath);
			cv.put(DownloadNotes.IMG_PATH, data.imgPath);
			db.insert(DownloadNotes.TABLE_NAME, null, cv);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return -1;
	}

	public void clear(String hashkey) {
		SQLiteDatabase db = null;
		try {
			db = this.getReadableDatabase();
			db.delete(DownloadNotes.TABLE_NAME, DownloadNotes.HASH_KEY + "=?", new String[] { hashkey });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
		}

	}

	public List<DownloadInfo> getZipFileList(String hashkey) {
		if (hashkey == null) {
			return null;
		}
		ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.getReadableDatabase();
			cursor = db.query(DownloadNotes.TABLE_NAME, null, DownloadNotes.HASH_KEY + "=?", new String[] { hashkey }, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					DownloadInfo mDownloadInfo = new DownloadInfo();
					mDownloadInfo.urlHashKey = cursor.getString(cursor.getColumnIndex(DownloadNotes.HASH_KEY));
					mDownloadInfo.imgPath = cursor.getString(cursor.getColumnIndex(DownloadNotes.IMG_PATH));
					mDownloadInfo.originZipPath = cursor.getString(cursor.getColumnIndex(DownloadNotes.ZIP_PATH));

					list.add(mDownloadInfo);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.close();
			}
			if (cursor != null) {
				cursor.close();
			}
		}

		return list;
	}
}

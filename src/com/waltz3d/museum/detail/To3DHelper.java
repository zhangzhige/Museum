package com.waltz3d.museum.detail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.waltz3d.museum.DiskDataCache;
import com.waltz3d.museum.DownloadInfo;
import com.waltz3d.museum.DownloadSQLiteHelper;
import com.waltz3d.museum.R;
import com.waltz3d.museum.Util;
import com.waltz3d.museum.XL_Log;
import com.waltz3d.museum.R.string;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class To3DHelper {
	
	private static final int START_LOADING = 1;//开始加载数据
	
	private static final int ZIP_FILE_SUCCESS = 2; // 解压完成
	
	private static final int MSG_UPDATE_DOWNLOAD_PROGRESS = 3; // 缓存任务interval

	private XL_Log log = new XL_Log(To3DHelper.class);

	private String Product3D;

	private ProgressDialog mProgressDialog;

	private Context mContext;

	// 更新缓存进度
	private Timer mTimer;

	private int readLen = 0;

	private long fileSize = 0;// 原始文件大小

	private String mFileSaveDir;
	
	private DownloadSQLiteHelper mDownloadSQLiteHelper;
	
	private OnloadSuccessListenr mOnloadSuccessListenr;
	
	private String mUrlHashKey;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START_LOADING:
				Util.showDialog(mProgressDialog, "正在加载中...");
				break;
			case ZIP_FILE_SUCCESS:
				stopTimer();
				dismissProgressBar();
				break;
			case MSG_UPDATE_DOWNLOAD_PROGRESS:// 显示进度条
				Bundle b = msg.getData();
				int progress = b.getInt("progress");
				log.debug("progress=" + progress);
				String upFormat = mContext.getString(R.string.update_loading);
				String upStr = MessageFormat.format(upFormat, progress);
				mProgressDialog.setMessage(upStr);
				break;
			default:
				break;
			}
		}
	};

	public To3DHelper(Context context, String url,OnloadSuccessListenr onloadSuccessListenr) {
		this.mContext = context;
		this.Product3D = url;
		mProgressDialog = new ProgressDialog(mContext);
		this.mOnloadSuccessListenr = onloadSuccessListenr;
		mDownloadSQLiteHelper = new DownloadSQLiteHelper(mContext);
		
		mFileSaveDir = Util.getSDCardDir(mContext) + "Mesume/3D";
		File f = new File(mFileSaveDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		mUrlHashKey = DiskDataCache.hashKeyForDisk(Product3D);
		tryToLoad();
	}
	
	private void tryToLoad(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				List<DownloadInfo> mImgList = mDownloadSQLiteHelper.getZipFileList(mUrlHashKey);
				if(mImgList == null || mImgList.size() == 0){
					handler.obtainMessage(START_LOADING).sendToTarget();
					
					String mFilePath = mFileSaveDir + File.separator + mUrlHashKey;
					File file = new File(mFilePath);
					if(file.exists()){//如果文件存在
						boolean isSuccess = readByZipInputStream(mFilePath, mFileSaveDir);//尝试解压缩
						if(isSuccess){//解压缩成功
							mImgList = mDownloadSQLiteHelper.getZipFileList(mUrlHashKey);
						}
					}
				}
				if(!selectList(mImgList)){//没有加载成功
					downFile();
				}
			}
		}).start();
	}

	private boolean selectList(List<DownloadInfo> mImgList){
		if(mImgList != null && mImgList.size() > 0){//数据为空
			List<DownloadInfo> mCallBackList = new ArrayList<DownloadInfo>();
			for(DownloadInfo mDownloadInfo : mImgList){
				File f = new File(mDownloadInfo.imgPath);
				if(f.exists()){
					mCallBackList.add(mDownloadInfo);
				}
			}
			if(mCallBackList.size() > 0){
				mOnloadSuccessListenr.onLoadSuccess(mCallBackList);
				handler.obtainMessage(ZIP_FILE_SUCCESS).sendToTarget();
				return true;
			}
		}
		return false;
	}
	private void startTimer() {
		if (null == mTimer) {
			mTimer = new Timer();
			mTimer.schedule(new PlayTimerTask(), 0, 1000);
		}
	}

	private void stopTimer() {
		if (null != mTimer) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	private class PlayTimerTask extends TimerTask {
		@Override
		public void run() {
			Message msg = handler.obtainMessage(MSG_UPDATE_DOWNLOAD_PROGRESS);
			Bundle b = new Bundle();
			int progress = (int) (((float) readLen / fileSize) * 100);
			b.putInt("progress", progress);
			msg.setData(b);
			handler.sendMessage(msg);
		}
	}

	private void downFile() {
		String mFilePath = mFileSaveDir + File.separator + mUrlHashKey;
		File file = new File(mFilePath);
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(Product3D);
		HttpResponse response;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			fileSize = entity.getContentLength();
			startTimer();
			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {
				fileOutputStream = new FileOutputStream(file);
				byte[] buf = new byte[8 * 1024];
				int ch = -1;
				while ((ch = is.read(buf)) != -1) {
					fileOutputStream.write(buf, 0, ch);
					readLen = readLen + ch;
				}
			}
			fileOutputStream.flush();
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
			boolean isSuccess = readByZipInputStream(mFilePath, mFileSaveDir);
			if(isSuccess){
				List<DownloadInfo> mImgList = mDownloadSQLiteHelper.getZipFileList(mUrlHashKey);
				if(selectList(mImgList)){
					return;
				};
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		handler.obtainMessage(ZIP_FILE_SUCCESS).sendToTarget();
		mOnloadSuccessListenr.onLoadSuccess(null);
	}

	private boolean readByZipInputStream(String archive, String decompressDir){
		BufferedInputStream mBufferedInputStream = null;
		try {
			FileInputStream fi = new FileInputStream(archive);
			CheckedInputStream csumi = new CheckedInputStream(fi, new CRC32());
			ZipInputStream in2 = new ZipInputStream(csumi);
			mBufferedInputStream = new BufferedInputStream(in2);
			java.util.zip.ZipEntry ze;
			while ((ze = in2.getNextEntry()) != null) {
				String entryName = ze.getName();
				if (ze.isDirectory()) {
					System.out.println("正在创建解压目录 - " + entryName);
					File decompressDirFile = new File(decompressDir + "/" + entryName);
					if (!decompressDirFile.exists()) {
						decompressDirFile.mkdirs();
					}
				} else {
					System.out.println("正在创建解压文件 - " + entryName);
					String mItemFileName = decompressDir + "/" + entryName;
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(mItemFileName));
					byte[] buffer = new byte[1024];
					int readCount = mBufferedInputStream.read(buffer);
					while (readCount != -1) {
						bos.write(buffer, 0, readCount);
						readCount = mBufferedInputStream.read(buffer);
					}
					bos.close();
					if(mItemFileName.endsWith("png") || mItemFileName.endsWith("jpg")){
						DownloadInfo mDownloadInfo = new DownloadInfo();
						mDownloadInfo.imgPath = mItemFileName;
						mDownloadInfo.originZipPath = archive;
						mDownloadInfo.urlHashKey = mUrlHashKey;
						mDownloadSQLiteHelper.insertData(mDownloadInfo);
					}
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(mBufferedInputStream != null){
				try {
					mBufferedInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 取消进度条
	 */
	private void dismissProgressBar() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
	
	public interface OnloadSuccessListenr{
		void onLoadSuccess(List<DownloadInfo> mList);
	}
}

package com.waltz3d.museum;

import java.util.Formatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.waltz3d.museum.ScreenObserver.ScreenStateListener;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.ViewGroup.LayoutParams;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class VideoPlayerActivity extends Activity {
	
	private XL_Log log = new XL_Log(VideoPlayerActivity.class);
	
	private MediaPlayer mediaPlayer;
	private SurfaceView mSurfaceView;
	private SeekBar mSeekBar;
	private TextView mEndTime, mCurrentTime;
	private LinearLayout control_layout;
	private LinearLayout loading_layout;

	private boolean mShowing;
	private boolean mDragging;
	private static final int sDefaultTimeout = 3000;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;
	StringBuilder mFormatBuilder;
	Formatter mFormatter;
	private ImageButton mPauseButton;
	private ImageButton mFfwdButton;
	private ImageButton mRewButton;
	
	ScreenObserver mScreenObserver;

	private String url;

	int currentPosition;

	private int mCurrentBufferPercentage;

	private int mScreenWidth;
	private int mScreenHeight;
	private Timer mTimer; //刷新播放进度定时器

	private Callback callback = new Callback() {
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			currentPosition = mediaPlayer.getCurrentPosition();
			pausePlayer();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			log.debug("surfaceCreated");
			play(currentPosition);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		}

	};

	protected void play(final int msec) {
		try {
			if (mediaPlayer != null) {
				try {
					mediaPlayer.stop();
					mediaPlayer.release();
					mediaPlayer = null;
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
			}
			loading_layout.setVisibility(View.VISIBLE);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					onResumePlayer();
					mSeekBar.setMax(mediaPlayer.getDuration());
					mediaPlayer.seekTo(msec);
				}
			});

			mediaPlayer.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {

				@Override
				public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
					if (mScreenWidth * height > mScreenHeight * width) {
						width = (int) (((float) mScreenHeight * width) / height + 0.5);
						height = mScreenHeight;
					} else {
						height = (int) (((float) mScreenWidth * height) / width + 0.5);
						width = mScreenWidth;
					}

					LayoutParams params = mSurfaceView.getLayoutParams();
					params.width = width;
					params.height = height;
					mSurfaceView.setLayoutParams(params);
				}
			});

			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					log.debug("onCompletion");
					Util.showToast(VideoPlayerActivity.this, "播放完成", Toast.LENGTH_LONG);
					finish();
				}
			});
			mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					mCurrentBufferPercentage = percent;
				}
			});
			mediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					log.debug("onError="+what);
					if (what != -38) {
						Util.showToast(VideoPlayerActivity.this, "播放出错，错误码："+what, Toast.LENGTH_LONG);
						finish();
					}
					return false;
				}
			});

			mediaPlayer.setOnInfoListener(new OnInfoListener() {

				@Override
				public boolean onInfo(MediaPlayer mp, int what, int extra) {
					switch (what) {
					case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:// 开始播放
						loading_layout.setVisibility(View.GONE);
						updatePausePlay();
						break;
					case MediaPlayer.MEDIA_INFO_BUFFERING_START:// 开始缓冲
						loading_layout.setVisibility(View.VISIBLE);
						break;
					case MediaPlayer.MEDIA_INFO_BUFFERING_END:// 缓冲结束
						loading_layout.setVisibility(View.GONE);
						updatePausePlay();
						break;
					}
					return false;
				}
			});

			mediaPlayer.setDataSource(url);
			mediaPlayer.setDisplay(mSurfaceView.getHolder());
			mediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}

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
            if (mediaPlayer == null) {
                return;
            }
            try {
                if (mediaPlayer.isPlaying()) {
                	mHandler.obtainMessage(SHOW_PROGRESS).sendToTarget();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_controller);
		initControllerView();
		url = getIntent().getStringExtra("url");
		if (url == null) {
			finish();
			return;
		}
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;

		mSurfaceView = (SurfaceView) findViewById(R.id.play_surface_view);
		mSurfaceView.getHolder().addCallback(callback);
		
        mScreenObserver = new ScreenObserver(this);
        mScreenObserver.requestScreenStateUpdate(new ScreenStateListener() {
            @Override
            public void onScreenOn() {

            }

            @Override
            public void onScreenOff() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                	doPauseResume();
                }
            }
        });
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		log.debug("onResume");
		mScreenObserver.tryAndWaitWhileScreenLocked(this);
	    mScreenObserver.keepScreenLightOn(this); //保持屏幕一直高亮
	}

	@Override
	protected void onPause() {
		super.onPause();
		mScreenObserver.keepScreenLightOff();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mScreenObserver != null) {
            mScreenObserver.stopScreenStateUpdate();
            mScreenObserver = null;
        }
		stopTimer();
		if (mediaPlayer != null) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						mediaPlayer.stop();
						mediaPlayer.release();
						mediaPlayer = null;
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	
	}
	
	private void initControllerView() {
		control_layout = (LinearLayout) findViewById(R.id.control_layout);
		loading_layout = (LinearLayout) findViewById(R.id.loading_layout);

		mPauseButton = (ImageButton) findViewById(R.id.pause);
		mPauseButton.setOnClickListener(mPauseListener);

		mFfwdButton = (ImageButton) findViewById(R.id.ffwd);
		mFfwdButton.setOnClickListener(mFfwdListener);

		mRewButton = (ImageButton) findViewById(R.id.rew);
		mRewButton.setOnClickListener(mRewListener);

		mSeekBar = (SeekBar) findViewById(R.id.mediacontroller_progress);
		SeekBar seeker = (SeekBar) mSeekBar;
		seeker.setOnSeekBarChangeListener(mSeekListener);
		mSeekBar.setMax(1000);

		mEndTime = (TextView) findViewById(R.id.time);
		mCurrentTime = (TextView) findViewById(R.id.time_current);
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
	}

	public void show(int timeout) {
		if (!mShowing) {
			control_layout.setVisibility(View.VISIBLE);
			setProgress();
			mShowing = true;
		}
		updatePausePlay();
		mHandler.sendEmptyMessage(SHOW_PROGRESS);
		Message msg = mHandler.obtainMessage(FADE_OUT);
		if (timeout != 0) {
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendMessageDelayed(msg, timeout);
		}
	}

	public void hide() {
		if (mShowing) {
			mHandler.removeMessages(SHOW_PROGRESS);
			control_layout.setVisibility(View.INVISIBLE);
			mShowing = false;
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int pos;
			switch (msg.what) {
			case FADE_OUT:
				hide();
				break;
			case SHOW_PROGRESS:
				pos = setProgress();
				if (!mDragging && mShowing && mediaPlayer.isPlaying()) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
				}
				break;
			}
		}
	};

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	private int setProgress() {
		if (mediaPlayer == null || mDragging) {
			return 0;
		}
		int position = mediaPlayer.getCurrentPosition();
		int duration = mediaPlayer.getDuration();
		
		mSeekBar.setProgress(position);
		int percent = mCurrentBufferPercentage;
		mSeekBar.setSecondaryProgress(percent * 10);

		mEndTime.setText(stringForTime(duration));
		mCurrentTime.setText(stringForTime(position));
		return position;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			show(0);
			break;
		case MotionEvent.ACTION_UP:
			show(sDefaultTimeout);
			break;
		case MotionEvent.ACTION_CANCEL:
			hide();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		show(sDefaultTimeout);
		return false;
	}

	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		public void onClick(View v) {
			doPauseResume();
			show(sDefaultTimeout);
		}
	};

	private void updatePausePlay() {
		if (mediaPlayer.isPlaying()) {
			mPauseButton.setImageResource(android.R.drawable.ic_media_pause);
		} else {
			mPauseButton.setImageResource(android.R.drawable.ic_media_play);
		}
	}

	private void doPauseResume() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			pausePlayer();
		} else {
			onResumePlayer();
		}
		updatePausePlay();
	}
	
	private void pausePlayer(){
		try {
			if(mediaPlayer != null && mediaPlayer.isPlaying()){
				mediaPlayer.pause();
			}
			stopTimer();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private void onResumePlayer(){
		log.debug("onResumePlayer");
		mediaPlayer.start();
		startTimer();
	}

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		
		public void onStartTrackingTouch(SeekBar bar) {
			show(3600000);
			mDragging = true;
			mHandler.removeMessages(SHOW_PROGRESS);
		}

		public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
			if (!fromuser) {
				return;
			}
			mediaPlayer.seekTo(progress);
			mCurrentTime.setText(stringForTime((int) progress));
		}

		public void onStopTrackingTouch(SeekBar bar) {
			mDragging = false;
			setProgress();
			updatePausePlay();
			show(sDefaultTimeout);
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
		}
	};

	private View.OnClickListener mRewListener = new View.OnClickListener() {
		public void onClick(View v) {
			int pos = mediaPlayer.getCurrentPosition();
			pos -= 5000;
			mediaPlayer.seekTo(pos);
			setProgress();
			show(sDefaultTimeout);
		}
	};

	private View.OnClickListener mFfwdListener = new View.OnClickListener() {
		public void onClick(View v) {
			int pos = mediaPlayer.getCurrentPosition();
			pos += 15000;
			mediaPlayer.seekTo(pos);
			setProgress();
			show(sDefaultTimeout);
		}
	};
}

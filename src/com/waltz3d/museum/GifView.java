package com.waltz3d.museum;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * GifView<br>
 * 本类可以显示一个gif动画，其使用方法和android的其它view（如imageview)一样。<br>
 * 如果要显示的gif太大，会出现OOM的问题。
 * @author liao
 *
 */
public class GifView extends View{
	
	XL_Log log=new XL_Log(GifView.class);
	
	/**gif解码器*/
	private GifDecoder gifDecoder = null;
	/**当前要画的帧的图*/
	private Bitmap currentImage = null;
	
	private boolean isRun = true;
	
	private DrawThread drawThread = null;
	
	private float rotateAngel = 0;
	
	private Bitmap mWallDown;
	
	private Bitmap mWallUp;
	
	private Matrix matrix;
	
	private float density;
	
	public GifView(Context context) {
		this(context,null);
        
    }
    
    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        mWallDown = BitmapFactory.decodeResource(context.getResources(), R.drawable.walk_down);
        mWallUp = BitmapFactory.decodeResource(getResources(), R.drawable.walk_up);
        matrix = new Matrix();
        
        density = getResources().getDisplayMetrics().density;
    }
    
    public void start(){
    	gifDecoder = new GifDecoder();
    	drawThread = new DrawThread();
    	drawThread.start();
    }
    
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(gifDecoder == null)
        	return;
        if(currentImage == null){
        	currentImage = gifDecoder.getImage(this.getContext());
        }
        if(currentImage == null || currentImage.isRecycled()){
        	return;
        }
        int saveCount = canvas.getSaveCount();
        
        canvas.save();
        matrix.reset();
        rotateAngel -= 0.5f;
        
        matrix.postRotate(rotateAngel, mWallDown.getWidth()/2 - 25 * density/2, mWallDown.getHeight()/2);
        matrix.postTranslate(25 * density/2, 170 *density/2);
        
        canvas.drawBitmap(mWallDown, matrix, null);
        canvas.drawBitmap(currentImage, (getMeasuredWidth() - currentImage.getWidth())/2, 150 * density/2, null);
        canvas.drawBitmap(mWallUp, matrix, null);
        canvas.restoreToCount(saveCount);
    }
    
    public int getCurrentWidth(){
    	return gifDecoder.width;
    }
    
    public int getCurrentheight(){
    	return gifDecoder.height;
    }
    
    private Handler redrawHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		log.debug("getTop="+getTop());
    		invalidate(0, getTop(), getWidth(), getHeight());
    	}
    };
    
    public void onStop(){
    	isRun = false;
    }
    
    public void onResume(){
    	if(!isRun){
    		isRun = true;
        	drawThread = new DrawThread();
        	drawThread.start();
    	}
    }
    
    /**
     * 动画线程
     *
     */
    private class DrawThread extends Thread{	
    	public void run(){
    		if(gifDecoder == null){
    			return;
    		}
    		while(isRun){
				currentImage = gifDecoder.next(getContext());
				Message msg = redrawHandler.obtainMessage();
				redrawHandler.sendMessage(msg);
				
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
}

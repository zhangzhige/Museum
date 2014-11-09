package com.example.museum;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
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
	
	private boolean pause = false;
	
	private int showWidth = -1;
	private int showHeight = -1;
	private Rect rect = null;
	
	private DrawThread drawThread = null;
	
	private GifImageType animationType = GifImageType.SYNC_DECODER;
	
	private int resId;
	/**
	 * 解码过程中，Gif动画显示的方式<br>
	 * 如果图片较大，那么解码过程会比较长，这个解码过程中，gif如何显示
	 * @author liao
	 *
	 */
	public enum GifImageType{
		/**
		 * 在解码过程中，不显示图片，直到解码全部成功后，再显示
		 */
		WAIT_FINISH (0),
		/**
		 * 和解码过程同步，解码进行到哪里，图片显示到哪里
		 */
		SYNC_DECODER (1),
		/**
		 * 在解码过程中，只显示第一帧图片
		 */
		COVER (2);
		
		GifImageType(int i){
			nativeInt = i;
		}
		final int nativeInt;
	}
	
	
	public GifView(Context context) {
        super(context);
        
    }
    
    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.drawBitmap(currentImage, 0, 0, null);
        canvas.restoreToCount(saveCount);
    }
    
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int pleft = getPaddingLeft();
        int pright = getPaddingRight();
        int ptop = getPaddingTop();
        int pbottom = getPaddingBottom();

        int widthSize;
        int heightSize;
        
        int w;
        int h;
       
        if(gifDecoder == null){
        	w = showWidth;
        	h = showHeight;
        }else{
        	w = gifDecoder.width;
        	h = gifDecoder.height;
        }
        
        w += pleft + pright;
        h += ptop + pbottom;

        w = Math.max(w, getSuggestedMinimumWidth());
        h = Math.max(h, getSuggestedMinimumHeight());

        widthSize = resolveSize(w, widthMeasureSpec);
        heightSize = resolveSize(h, heightMeasureSpec);
        showWidth=widthSize;
        showHeight=heightSize;
        setMeasuredDimension(widthSize, heightSize);
    }
    
    public int getCurrentWidth(){
    	return gifDecoder.width;
    }
    
    public int getCurrentheight(){
    	return gifDecoder.height;
    }
    
    /**
     * 只显示第一帧图片<br>
     * 调用本方法后，gif不会显示动画，只会显示gif的第一帧图
     */
    public void showCover(){
    	if(gifDecoder == null){
    		return;
    	}
    	pause = true;
    	currentImage = gifDecoder.getImage(this.getContext());
    	invalidate();
    	onStop();
    	try {
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 继续显示动画<br>
     * 本方法在调用showCover后，会让动画继续显示，如果没有调用showCover方法，则没有任何效果
     */
    public void showAnimation(){
    	if(pause){
    		pause = false;
        	isRun=true;
    	}
    }
    
    /**
     * 设置gif在解码过程中的显示方式<br>
     * <strong>本方法只能在setGifImage方法之前设置，否则设置无效</strong>
     * @param type 显示方式
     */
    public void setGifImageType(GifImageType type){
    	if(gifDecoder == null)
    		animationType = type;
    }
    
    
    
    private void reDraw(){
    	if(redrawHandler != null){
			Message msg = redrawHandler.obtainMessage();
			redrawHandler.sendMessage(msg);
    	}
    }
    
    private Handler redrawHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		invalidate();
    	}
    };
    
    /**
     * 动画线程
     *
     */
    private class DrawThread extends Thread{	
    	public void run(){
    		if(gifDecoder == null){
    			return;
    		}
    		log.debug("run="+isRun+",isPause="+pause);
    		while(isRun){
    			if(!pause){
	    				currentImage = gifDecoder.next(getContext());
	    				long sp = 30;	    				
    					Message msg = redrawHandler.obtainMessage();
    					redrawHandler.sendMessage(msg);
    					SystemClock.sleep(sp); 
    			}else{
    				SystemClock.sleep(10);
    			}
    		}
    	}
    }
    
    /**
     * 释放资源
     */
    public void onStop(){
    	isRun=false;
    	if(gifDecoder != null){  		
    		gifDecoder.onStop();
    	}
    }
    
    /**
     * 和onStop的区别是destroy后图片完全不显示。stop会显示第一张图
     * 方法的一句话概述
     * <p>方法详述（简单方法可不必详述）</p>
     */
    public void onDestroy(){
    	if(gifDecoder != null){  		
    		gifDecoder.onDestroy();
    	}
    }
}

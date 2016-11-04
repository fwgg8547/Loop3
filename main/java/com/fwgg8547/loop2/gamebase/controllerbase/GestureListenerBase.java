package com.fwgg8547.loop2.gamebase.controllerbase;

import com.fwgg8547.loop2.gamebase.util.*;
import com.fwgg8547.loop2.gamebase.layerbase.*;
import com.fwgg8547.loop2.gamebase.sequencerbase.*;
import com.fwgg8547.loop2.gamebase.modelbase.*;

import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.graphics.RectF;
import android.graphics.*;

abstract public class GestureListenerBase implements GestureDetector.OnGestureListener
{
	public interface notifyEventListener {
		public void onDown(PointF pos);
		public void onFling(PointF pos1, PointF pos2, float vx, float v2);
		public void onScroll(PointF pos1, PointF pos2, float x, float y);
		public void onSingleTapUp(PointF pos);
	}
	
	protected notifyEventListener mNE;
	protected ReadersWriterLock mLock;
	
	public GestureListenerBase(ReadersWriterLock lock){
		mLock = lock;
	}
	
	public void clear(){
		mLock = null;
		mNE = null;
	}

	public void setListener(notifyEventListener ne){
		mNE = ne;
	}
	
}

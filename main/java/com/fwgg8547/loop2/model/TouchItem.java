package com.fwgg8547.loop2.model;
import com.fwgg8547.loop2.gamebase.modelbase.*;
import android.graphics.*;

public class TouchItem extends CollidableItem
{
	private static final String TAG = TouchItem.class.getSimpleName();
	private FlickType mType;
  
	public enum FlickType{
    TOP,
    BOTTOM,
		RIGHT,
		LEFT,
    CENTER
	}
  
	public TouchItem(){
    mType = FlickType.CENTER;
  }

  public void setFlickType(FlickType t) {
    mType = t;
  }

  public int getFlick(){
    return mType.ordinal();
  }
	
	
}


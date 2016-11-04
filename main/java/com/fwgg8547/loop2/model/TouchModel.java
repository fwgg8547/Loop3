package com.fwgg8547.loop2.model;
import com.fwgg8547.loop2.gamebase.util.Lg;
import com.fwgg8547.loop2.gamebase.util.ReadersWriterLock;
import com.fwgg8547.loop2.gamebase.modelbase.CollisionModel;
import com.fwgg8547.loop2.gamebase.modelbase.ItemBase;
import com.fwgg8547.loop2.gamebase.modelbase.CollidableItem;
import com.fwgg8547.loop2.gamebase.modelbase.ModelGroup;
import com.fwgg8547.loop2.gamebase.modelbase.Sprite;
import com.fwgg8547.loop2.generater.ResourceFileReader;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.PointF;
import android.graphics.RectF;

import com.fwgg8547.loop2.*;
import com.fwgg8547.loop2.gamebase.sequencerbase.*;
import com.fwgg8547.loop2.gamebase.controllerbase.*;
import com.fwgg8547.loop2.gamebase.util.*;
import com.fwgg8547.loop2.gamebase.modelbase.*;

public class TouchModel extends CollisionModel
{
	private static final String TAG = TouchModel.class.getSimpleName();
	private static final int OBJ_NUM = 10;
	private int mIdOffset;
	private int mIdCurr;
	private boolean mFirst;
	private boolean mDeleting;
	private PendingRequest mPending;
	
	public class PendingRequest {
		public RectF mRect;
		
		public PendingRequest(RectF r){
			mRect = r;
		}
	}

	public TouchModel(){
		super();
	}

	@Override
	public void onUpdate()
	{
		try{
			mLock.writeLock();

			for(int i=0; i<mItemList.size();i++){
				ItemBase itm = mItemList.get(i);
				if(itm.mIsDeleted){
					Lg.i(TAG, "touch was deleted id =" +itm.getId());
					freeItem(i);
					i--; // mblock was reduced
				}
<<<<<<< HEAD
=======

>>>>>>> aa490f0a0ff645b1d4409f76d3391b850b176882
				itm.mIsDeleted = true;
			}

		}catch(Exception e){
			Lg.e(TAG,e.toString());
		}finally{
			mLock.writeUnlock();
		}
		
		if(mPending != null){
			createItem(mPending.mRect);
			mPending = null;
		}
	}

	@Override
	public void moveScroll()
	{
		// TODO: Implement this method
	}
	
	@Override
	public int getTextureCount()
	{
		// TODO: Implement this method
		return 1;
	}

	@Override
	public int getTextureId()
	{
		return  R.drawable.wall_image;
	}

	public void initialize(ReadersWriterLock lock, int offset, ModelGroup mg, int p){
		super.initialize(lock, offset, OBJ_NUM, mg, p);
		mIdOffset = offset;
		mIdCurr = 0;
		mIndexCount =0;
		mFirst = false;
		mIsScrollable = false;
		mPending =null;
	}

	//====
	public boolean isDeleting(){
		return mDeleting;
	}

	public void createItemRequest(RectF rect)
	{
		// TODO: Implement this method
		if(mPending == null){
			mPending = new PendingRequest(rect);
		}
		return ;
	}

	@Override
	public ItemBase createItem(int pattern)
	{
		return createItem(pattern, new RectF());
	}

	public ItemBase createItem(RectF rect)
	{
		// TODO: Implement this method
		return createItem(0,rect);
	}

	public ItemBase createItem(int pattern, RectF rect)
	{		
		CollidableItem it = null;
		try{
			mLock.writeLock();
      
			it = (CollidableItem)super.createItem();
			if(it == null){
				return null;
			}
			
			float l = rect.left, t = rect.top;
			float w = rect.width(), h =rect.height();
			
			it.setType(GLEngine.TOUCHMODELINDX);
			Sprite s = new Sprite(mIdOffset + mIdCurr);
			s.setTextureUv(ResourceFileReader.getUv(0));
			it.setId(mIdOffset+mIdCurr);
      Lg.i(TAG, "touch item created id= " + it.getId());
			mIdCurr++;
			it.setSprite(s);
			it.setPosition(l, t, 0.0f, 0.0f);
			it.setRect(new RectF(-1*w/2, -1*h/2, w/2, h/2));
			it.setColor(new float[]{0,1,1,1});
			it.mIsDeleted = false;
			return it;

		} catch (Exception e){

		} finally {
			mLock.writeUnlock();
		}

		return it;
	}


	public void deleteItem(CollidableItem it, float r){
		ItemPattern p = ResourceFileReader.getPattern(ResourceFileReader.Type.Batt, 1);
		float rad = (float)Math.toRadians(r);
		Vec2 v = new Vec2((float)Math.cos(rad), (float)Math.sin(rad));
		p.mMotionPattern = new MotionSequnce[]{
			new MotionSequnce( 180, 1.0f, v),
			new MotionSequnce(-1, 0f, null)
		};
		mDeleting = true;
		Lg.i(TAG, "batt item delete");
		it.delete(p);
	}
}

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
	private static final int OBJ_NUM = 190;
	private int mIdOffset;
	private int mIdCurr;
	private boolean mFirst;
	private boolean mDeleting;
	private PendingRequest mPending;

  
	public class PendingRequest {
		public RectF mRect;
		public PointF mPos1;
		public PointF mPos2;
		public TouchItem.FlickType mType;
    
		public PendingRequest(RectF r, TouchItem.FlickType t){
			mRect = r;
      mType = t;
		}
		
		public PendingRequest(PointF p1, PointF p2, TouchItem.FlickType t){
			mPos1 = p1;
			mPos2 = p2;
      mType = t;
		}
	}

	public TouchModel(){
		super();
	}

	public void initialize(ReadersWriterLock lock, int offset, ModelGroup mg, int p){
		super.initialize(lock, offset, OBJ_NUM, mg, p);

		// repleace own iremlists
		TouchItem[] items = new TouchItem[OBJ_NUM];
		for(int i=0;i<OBJ_NUM; i++){
			items[i] = new TouchItem();
			items[i].mIsDeleted = true;
			items[i].mIndex = i;
		}
		mItemList.initialize(items);
		
		mIdOffset = offset;
		mIdCurr = 0;
		mIndexCount =0;
		mFirst = false;
		mIsScrollable = false;
		mPending =null;
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
				//itm.mIsDeleted = true;
			}

		}catch(Exception e){
			Lg.e(TAG,e.toString());
		}finally{
			mLock.writeUnlock();
		}
		
		if(mPending != null){
			createItem(mPending.mPos1, mPending.mPos2, mPending.mType);
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

	//====
	public boolean isDeleting(){
		return mDeleting;
	}

	public void createItemRequest(PointF p1, PointF p2, TouchItem.FlickType t)
	{
		// TODO: Implement this method
		if(mPending == null){
			mPending = new PendingRequest(p1,p2, t);
		}
		return ;
	}

	@Override
	public ItemBase createItem(int pattern)
	{
		return createItem(pattern, new PointF(), new PointF(), TouchItem.FlickType.CENTER);
	}

	public ItemBase createItem(PointF p1, PointF p2, TouchItem.FlickType t)
	{
		// TODO: Implement this method
		return createItem(0,p1,p2,t);
	}

	//public ItemBase createItem(int pattern, RectF rect, TouchItem.FlickType type)
	public ItemBase createItem(int pattern, PointF p1, PointF p2, TouchItem.FlickType type)
	{		
		TouchItem it = null;
		try{
			mLock.writeLock();
      
			it = (TouchItem)super.createItem();
			if(it == null){
				return null;
			}
			
			Quadrilateral q ;
			switch(type){
				case TOP:
					Lg.i(TAG, "Top");
					q = new Quadrilateral(
						new PointF(p2.x+50, p2.y), // tl
						new PointF(p2.x-50, p2.y), // tr
						new PointF(p1.x-50, p1.y), // br
						new PointF(p1.x+50, p1.y)  // bl
					);
					break;
				case BOTTOM:
					Lg.i(TAG, "Bottom");
					q = new Quadrilateral(
						new PointF(p1.x+50, p1.y), // tl
						new PointF(p1.x-50, p1.y), // tr
						new PointF(p2.x-50, p2.y), // br
						new PointF(p2.x+50, p2.y)  // bl
					);
					break;
				case LEFT:
					Lg.i(TAG, "Left");
					q = new Quadrilateral(
						new PointF(p1.x, p1.y+50), // tl
						new PointF(p2.x, p2.y+50), // tr
						new PointF(p2.x, p2.y-50), // bl
						new PointF(p1.x, p1.y-50)  // br
						
						
					);
					break;
				case RIGHT:
				default:
				Lg.i(TAG,"Right");
					q = new Quadrilateral(
						new PointF(p2.x, p2.y+50), // tl
						new PointF(p1.x, p1.y+50), // tr
						new PointF(p1.x, p1.y-50), // bl
						new PointF(p2.x, p2.y-50)  // br
					);
					break;
				
			}
			
			it.setType(GLEngine.TOUCHMODELINDX);
			WallSprite s = new WallSprite(mIdOffset + mIdCurr);
			s.setTextureUv(ResourceFileReader.getUv(0));
			it.setId(mIdOffset+mIdCurr);
      		Lg.i(TAG, "touch item created id= " + it.getId());
			mIdCurr++;
			it.setSprite(s);
			//it.setPosition(0, 0, 0.0f, 0.0f);
			//it.setRect(new RectF(-1*w/2, -1*h/2, w/2, h/2));
			it.setQuadrilateral(q);
			it.setColor(new float[]{0,1,1,1});
      it.setFlickType(type);
			it.mIsDeleted = false;
			return it;

		} catch (Exception e){
			Lg.w(TAG,e.toString());
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

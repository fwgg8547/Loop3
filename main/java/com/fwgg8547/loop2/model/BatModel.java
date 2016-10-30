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
import android.graphics.PointF;
import android.graphics.RectF;
import com.fwgg8547.loop2.*;
import com.fwgg8547.loop2.gamebase.sequencerbase.*;
import com.fwgg8547.loop2.gamebase.controllerbase.*;
import com.fwgg8547.loop2.gamebase.util.*;
import com.fwgg8547.loop2.gamebase.modelbase.*;

public class BatModel extends CollisionModel
{
	private static final String TAG = BatModel.class.getSimpleName();
	private static final int OBJ_NUM = 1;
	private int mIdOffset;
	private int mIdCurr;
	private int mTextureId;
	private boolean mFirst;
	private int mClockWise; // -1: ClockWise, 1: CounterClockWise
	private DirectionDetectListener mListener;
	private boolean mDeleting;
	private int mCurrentCw;
	private AngleTime mLastAngle;
	private UpdatePositionRequest mPending;
	private DeleteRequest mPendingDelete;
	
	public class AngleTime{
		public float mAngle;
		public long mLastUpdated;
	}
	
	public class DeleteRequest {
		public ItemPattern mPattern;
		
		public DeleteRequest(ItemPattern p){
			mPattern = p;
		}
	}
	
	public class UpdatePositionRequest{
		public ScrollManager.Direct mDirect;
		public int mCw;
		
		public UpdatePositionRequest(ScrollManager.Direct d, int cw){
			mDirect = d;
			mCw = cw;
		}
	}
	
	public interface DirectionDetectListener{
		public void notifyDirect(ScrollManager.Direct d);
	}
	
	public BatModel(){
		super();
	}
	
	@Override
	public void onUpdate()
	{
		
		ScrollManager.Direct direct= ScrollManager.Direct.NONE;
		try{
			mLock.writeLock();

			for(int i=0; i<mItemList.size();i++){
				ItemBase itm = mItemList.get(i);
				if(itm.mIsDeleted){
					Lg.i(TAG, "batt was deleted id =" +itm.getId());
					freeItem(i);
					i--; // mblock was reduced
					new InnerEvent().notifyEvent(InnerEvent.InnerMessage.Event.GameOver);
				}
				
				if(itm.getPosition().y < GameConfig.DELETEMERGIN ||
				itm.getPosition().x < GameConfig.DELETEMERGIN) {
					itm.mIsDeleted = true;
				}
			}
	
			for(int i=0,n=mItemList.size();i<n;i++){
				CollidableItem itm = (CollidableItem)mItemList.get(i);
				
				if(mPending != null){
					updatePosition(mPending.mDirect, mPending.mCw);
					mPending = null;
				} else {
					if(GameConfig.AUTOMOVE==1){
						float r = itm.getAngle();
						direct = autoMove(r);				
					}
				}
				itm.moveAnimation();
				mLastAngle.mAngle = itm.getAngle();
				mLastAngle.mLastUpdated  = System.nanoTime();
			}
		}catch(Exception e){
			Lg.e(TAG,e.toString());
		}finally{
			mLock.writeUnlock();
		}
		
		if(GameConfig.AUTOMOVE==1 && 
		direct != ScrollManager.Direct.NONE &&
		mListener != null){
	 		mListener.notifyDirect(direct);
		}
	}

	@Override
	public void moveScroll()
	{
		try{
			mLock.writeLock();
			for(int i=0,n=mItemList.size();i<n;i++){
				ScrollableItem itm = (ScrollableItem)mItemList.get(i);
				itm.doScroll();
			}			
		} catch (Exception e) {
			Lg.e(TAG, e.toString());
		} finally {
			mLock.writeUnlock();
		}
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
		return  R.drawable.circle2;
	}

	public void initialize(ReadersWriterLock lock, int offset, ModelGroup mg, int p){
		super.initialize(lock, offset, OBJ_NUM, mg, p);
		mIdOffset = offset;
		mIdCurr = 0;
		mIndexCount =0;
		mFirst = false;
		mClockWise = 1;
		mCurrentCw = 1;
		mIsScrollable = true;
		mListener = null;
		mLastAngle = new AngleTime();
		mPending = null;
	}
	
	public AngleTime getLastUpdated(){
		return mLastAngle;
	}
	
	public void setCw(int cw){
		mCurrentCw = cw;
	}
	
	public void setDirectionDetectListener(DirectionDetectListener l){
		mListener = l;
	}
	
	private void setAngleDelta(float a){
		for(int i=0,n=mItemList.size();i<n;i++){
			CollidableItem itm = (CollidableItem)mItemList.get(i);
			itm.setRotatePattern(new RotateSequence[]{
				new RotateSequence(60, GameConfig.RVELOCITY*mClockWise)
			}, null);
			Lg.i(TAG,"angle deelta "+a*mClockWise);
		}
	}
	
	public void setAngle(float a){
		for(int i=0,n=mItemList.size();i<n;i++){
			CollidableItem itm = (CollidableItem)mItemList.get(i);
			itm.setAngle(a);
		}
	}
	
	public PointF getAngleCenter(){
		return getAngleCenter(0);
	}

	public float getAngle(){
		return ((CollidableItem)mItemList.get(0)).getAngle();
	}
	
	//====
	
	public void setAngleTggle(){
		mClockWise *= -1;
	}

	public int getAngleTggle(){
		return mClockWise;
	}
	
	public boolean isDeleting(){
		return mDeleting;
	}
	
	private static ScrollManager.Direct autoMove(float r){
		ScrollManager.Direct d = ScrollManager.Direct.NONE;
		switch( getHitDirect(r)){
			case UP:
				d = ScrollManager.Direct.UP;
				break;
			case RIGHT:
				d = ScrollManager.Direct.RIGHT;
				break;
			case LEFT:
				d = ScrollManager.Direct.LEFT;
				break;
			case DOWN:
				d = ScrollManager.Direct.DOWN;
				break;
			default:
		}
		return d;
	}
	
	private static ScrollManager.Direct getHitDirect(float r){
		if(r >= 90-GameConfig.AUTOHITRANGE && r < 90+GameConfig.AUTOHITRANGE){
			return ScrollManager.Direct.UP;
		} else if(r >= 180-GameConfig.AUTOHITRANGE && r < 180+GameConfig.AUTOHITRANGE){
			return ScrollManager.Direct.LEFT;
		} else if(r >= 270-GameConfig.AUTOHITRANGE && r < 270+GameConfig.AUTOHITRANGE){
			return ScrollManager.Direct.DOWN;
		} else if((r >= 0 && r < GameConfig.AUTOHITRANGE) || (r > 360-GameConfig.AUTOHITRANGE)){
			return ScrollManager.Direct.RIGHT;
		} else {
			return ScrollManager.Direct.NONE;
		}
	}
	
	public void updatePosition(ScrollManager.Direct di){
		updatePosition(di, mCurrentCw);
	}
	
	public void updatePosition(ScrollManager.Direct di, int cw){
		
		try{
			//mLock.writeLock();

			if(cw == mCurrentCw){
				Lg.i(TAG, "update pos rotate");
				rotatePosition(di);
				//setAngleTggle();
				
			} else {
				Lg.i(TAG, "update pos counter");
				counterRotatePosition2(di);		
			}
			mCurrentCw = cw;

		}catch(Exception e){
			Lg.e(TAG, e.toString());
		} finally{
			//mLock.writeUnlock();
			//mScore.add(1);
		}
		
	}
	
	public void updatePositionRequest(ScrollManager.Direct di, int cw){
		mPending = new UpdatePositionRequest(di, cw);
	}

	private void rotatePosition(ScrollManager.Direct di){

		for(int i=0,n=mItemList.size();i<n;i++){
			CollidableItem itm = (CollidableItem)mItemList.get(i);
			Vec2 p = new Vec2(0,0);
			float a = itm.getAngle();
			itm.setAngle(a+180);
			
			
			Lg.d(TAG, "Item Center " +"x="+p.x+":y="+p.y+":Angle="+itm.getAngle());
			switch(di){
				case UP:
					Lg.d(TAG, "UP");
					p.y+=(GameConfig.WIDTH*2);
					break;
				case DOWN:
					Lg.d(TAG, "DOWN");
					p.y-=(GameConfig.WIDTH*2);
					break;
				case LEFT:
					Lg.d(TAG, "LEFT");
					p.x-=(GameConfig.WIDTH*2);
					break;
				case RIGHT:
					Lg.d(TAG, "RIGHT");
					p.x+=(GameConfig.WIDTH*2);
					break;
			}
			Lg.i(TAG,"Before Item Position "+"x=" +itm.getPosition().x+":y="+itm.getPosition().y);
			Lg.i(TAG,"new position vect " +"x="+p.x+":y="+p.y);
			itm.setPositionDelta(p.x,p.y);
			PointF newPos = itm.getPosition();
			itm.setAngleCenter(newPos);
		}
	}
	
	private void counterRotatePosition2(ScrollManager.Direct di){
		rotatePosition(di);
		setAngleTggle();
		setAngleDelta(GameConfig.RVELOCITY);
	}
	
	@Override
	public ItemBase createItem(int pattern)
	{
		return createItem(pattern, 0, 0);
	}

	public ItemBase createItem(int pattern, float x, float y)
	{		
		CollidableItem it = null;
		try{
			mLock.writeLock();
			
		ItemPattern p = ResourceFileReader.getPattern(ResourceFileReader.Type.Batt, pattern);
		it = (CollidableItem)super.createItem();
		if(it == null){
			return null;
		}
		it.setType(GLEngine.BATTMODELINDX);
		Sprite s = new Sprite(mIdOffset + mIdCurr);
		it.setId(mIdOffset+mIdCurr);
		mIdCurr++;
		it.setSprite(s);

		it.setPosition(GameConfig.BATTINITPOSX, GameConfig.BATTINITPOSY, 0.0f, 0.0f);
		it.setRect(new RectF(-1*GameConfig.WIDTH, -1*GameConfig.HEIGHT, GameConfig.WIDTH, GameConfig.HEIGHT));

		if(p.mMotionPattern != null){
			it.setMotionPattern(p.mMotionPattern, null);
		}
		if(p.mRotatePattern != null){
			it.setRotatePattern(p.mRotatePattern, null);
		}
		if(p.mScalePattern != null){
			it.setScalePattern(p.mScalePattern, null);
		}
		if(p.mTexturePattern != null){
			it.setTexturePattern(p.mTexturePattern, null);
		}
		
		it.setCenterOffset(new Vec2(GameConfig.CENTEROFFSET,0));
		it.setAngleCenter(it.getPosition());
		//it.setAngle(270);
		it.setColor(new float[]{1,1,1,1});
		it.moveAnimation();
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

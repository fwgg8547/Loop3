package com.fwgg8547.loop2;

import com.fwgg8547.loop2.gamebase.util.*;
import com.fwgg8547.loop2.gamebase.controllerbase.GLControllerBase;
import com.fwgg8547.loop2.gamebase.enginebase.*;
import com.fwgg8547.loop2.gamebase.layerbase.*;
import com.fwgg8547.loop2.gamebase.sequencerbase.*;
import com.fwgg8547.loop2.gamebase.modelbase.*;
import com.fwgg8547.loop2.gamebase.controllerbase.*;
import com.fwgg8547.loop2.gamebase.scorebase.*;
import com.fwgg8547.loop2.gamebase.controllerbase.GameOverPatternBase;
import com.fwgg8547.loop2.model.*;
import com.fwgg8547.loop2.generater.*;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import android.view.MotionEvent;
import android.graphics.RectF;
import android.graphics.PointF;
import android.content.Context;
import android.view.GestureDetector;

import org.dyn4j.dynamics.*;
import org.dyn4j.geometry.*;
import com.fwgg8547.loop2.ScrollManager.*;

public class GLController 
extends GLControllerBase 
implements GestureListenerBase.notifyEventListener, 
ScoreBase.NotifyScore,
SequencerBase.Callback,
BatModel.DirectionDetectListener
{
	private final static String TAG = GLController.class.getSimpleName();
	
	private BlockModel mBlock;
	private TouchModel mTouch;
	private BlockGenerater mBlockGenerater;
	private ScrollManager mScrollManager;
	private BatModel mBatt;
	private WallModel2 mWall;
	private ScrollSequencer mScrollSequencer;
	private GestureListener mGl;
	private List<ItemBase> mCollisionList;
	private ScrollManager.Direct mAutoDirect;
	private boolean mIsFirstUpdate;
	private boolean mIsReady;
	
	public GLController(Context ctx, GestureListener gl, CollisionManager cmg)
	{
		super(ctx, gl, cmg);
		mGl = gl;
		mIsFirstUpdate = true;
		mIsReady = false;
	}

	public void initialize(){
		mGl.setListener(this);	
		mIsFirstUpdate = true;
		mCollisionList = new ArrayList<ItemBase>();
		mScrollSequencer = new ScrollSequencer();
		mScrollSequencer.initilaize();
		mScrollManager = new ScrollManager();
		mIsReady = false;
		mAutoDirect = ScrollManager.Direct.NONE;
	}

	@Override
	public void levelChanged(int newLevel)
	{
		mScrollSequencer.changeDirectRequest();
	}

	@Override
	public void onDown(PointF pos)
	{
		return ;
		/*
		if(mBatt.isDeleting()){
			return;
		}
		
		long diff = System.nanoTime() - mBatt.getLastUpdated().mLastUpdated;
		float r =GameConfig.RDEGPERNANO*diff+mBatt.getLastUpdated().mAngle;
		//Lg.w(TAG,"onDown angle = " + r + "|" + r2);
		
		DebugText.set("Touch : " + r);
		ScrollManager.Direct d = getHitDirect(r);
		Lg.i(TAG, "Hit DIRECT = " + d);
		if(d == ScrollManager.Direct.NONE){
			Lg.i(TAG, "Delete Batt");
			mBatt.deleteItem((CollidableItem)mBatt.getItemArray().get(0), r);
			return;
		}else{
			mAutoDirect = d;
		}
		
		List<CollidableItem>cl =  mCollisitionManager.getCollisionItem((CollidableItem)mBatt.getItemArray().get(0));
		if(cl.size() >= 2){
			Lg.d(TAG, "hit block when touch " +cl.size());
			int bcw = (pos.x < ScreenModel.getInstance().getWidth()/2)? -1:1;
			mBatt.updatePositionRequest(d, bcw);
			scroll(d);
		} else {
			Lg.i(TAG, "block < 2");
			mBatt.deleteItem((CollidableItem)mBatt.getItemArray().get(0), r);
			mAutoDirect = ScrollManager.Direct.NONE;
		}
		
		Lg.i(TAG, "ondown end");
		*/
	}

	@Override
	public void onSingleTapUp(PointF pos)
	{
		// TODO: Implement this method
		Lg.i(TAG, "onsingletapup end");
	}
	
	private ScrollManager.Direct getHitDirect(float r){
		if(r >= 90-GameConfig.HITRANGE && r < 90+GameConfig.HITRANGE){
			return ScrollManager.Direct.UP;
		} else if(r >= 180-GameConfig.HITRANGE && r < 180+GameConfig.HITRANGE){
			return ScrollManager.Direct.LEFT;
		} else if(r >= 270-GameConfig.HITRANGE && r < 270+GameConfig.HITRANGE){
			return ScrollManager.Direct.DOWN;
		} else if((r >= 0 && r < GameConfig.HITRANGE) || (r > 360-GameConfig.HITRANGE)){
			return ScrollManager.Direct.RIGHT;
		} else {
			return ScrollManager.Direct.NONE;
		}
	}
	
	@Override
	public void onFling(PointF event1,PointF event2, float vx, float vy)
	{
		
	}

	@Override
	public void onScroll(PointF pos1, PointF pos2, float x, float y)
	{
		// TODO: Implement this method
		Lg.i(TAG, "scroll x y " + pos2.x + " ; " + pos2.y +";" +x+";"+y);
		float l,t,r,b;
		if(pos2.x < pos2.x + x) {
			l=pos2.x;
			r=pos2.x+x+1;
		} else{
			l=pos2.x+x;
			r=pos2.x+1;
		}
		if(pos2.y < pos2.y+y){
			t=pos2.y;
			b=pos2.y+y+1;
		} else{
			t=pos2.y+y;
			b=pos2.y+1;
		}
		
		RectF rect = new RectF(l,t,r,b);
		
		if(mTouch == null){
			return;
		}
		
		mTouch.createItem(0, rect);
		/*
		List<CollidableItem> cl = mCollisitionManager.getCollisionItem(rect);
		Lg.i(TAG, "get col");
		Iterator<CollidableItem> ite = cl.iterator();
		while(ite.hasNext()){
			CollidableItem i = ite.next();
			if(i instanceof BlockItem){
				((BlockItem)i).select();
			}
			Lg.d(TAG,"hit " + i.mIndex);
		}
		Lg.i(TAG, "finish");
		
	*/
	}
	
	private BlockItem getCollisionItem(List<CollidableItem> cl, ScrollManager.Direct d){
		Iterator ite = cl.iterator();
		BlockItem most = null;
		switch(d){
			case UP:
				while(ite.hasNext()){
					BlockItem bi = (BlockItem)ite.next();
					most = (most == null)? bi : most;
					if(most.getPosition().y < bi.getPosition().y){
						most = bi;
					}
				}
			break;
				
			case DOWN:
				while(ite.hasNext()){
					BlockItem bi = (BlockItem)ite.next();
					most = (most == null)? bi : most;
					if(most.getPosition().y > bi.getPosition().y){
						most = bi;
					}
				}
			break;
			
			case RIGHT:
				while(ite.hasNext()){
					BlockItem bi = (BlockItem)ite.next();
					most = (most == null)? bi : most;
					if(most.getPosition().x < bi.getPosition().x){
						most = bi;
					}
				}
			break;
			
			case LEFT:
				while(ite.hasNext()){
					BlockItem bi = (BlockItem)ite.next();
					most = (most == null)? bi : most;
					if(most.getPosition().x > bi.getPosition().x){
						most = bi;
					}
				}
			break;
		}
		
		return most;
	}
	
	@Override
	public void addModel(ModelBase m) {
		super.addModel(m);
	}
	
	@Override
	public void addCollisionModel(CollisionModel m) {
		super.addCollisionModel(m);
		mScrollSequencer.addModel(m);
		if(m instanceof BlockModel) {
			mBlock = (BlockModel)m;
		} else if (m instanceof WallModel2){
			mWall = (WallModel2)m;
		} else if (m instanceof BatModel){
			mBatt = (BatModel)m;
			mBatt.setDirectionDetectListener(this);
		} else if(m instanceof TouchModel){
			mTouch = (TouchModel)m;
		}else {
			Lg.e(TAG, "unknown collidable model is added");
		}
	}

	@Override
	public void addGenerater(ItemGeneraterBase g)
	{
		super.addGenerater(g);
		if(g instanceof BlockGenerater){
			mBlockGenerater = (BlockGenerater)g;
		}
	}

	@Override
	public boolean broadCollision(ItemBase item1, ItemBase item2)
	{
		return true;
	}

	@Override
	public boolean narrowCollision(ItemBase item1, ItemBase item2)
	{
    Lg.i(TAG, "item type " + item1.getType() + " : " + item2.getType());
    if((item1.getType() == GLEngine.TOUCHMODELINDX) &&
       (item2.getType() == GLEngine.BLOCKMODELINDX)){
      mBlock.select((BlockItem)item2);
    } else if((item2.getType() == GLEngine.TOUCHMODELINDX) &&
              (item1.getType() == GLEngine.BLOCKMODELINDX)){
      mBlock.select((BlockItem)item1);
    }
    
		return false;
	}

	@Override
	public void notifyDirect(ScrollManager.Direct d)
	{
		boolean move = false;

		if(d != mAutoDirect || mBatt.isDeleting()){
			return;
		}
		
		PointF center = mBatt.getItemArray().get(0).getPosition();
    RectF rect = new RectF(center.x - GameConfig.WIDTH*2, center.y-GameConfig.WIDTH*2, center.x+GameConfig.WIDTH*2, center.y+GameConfig.WIDTH*2);
    List<CollidableItem> cl = mCollisitionManager.getCollisionItem(rect);
		Iterator<CollidableItem> ite = cl.iterator();
		CollidableItem item = null;
		Lg.w(TAG, "auto move " +d);
		Lg.d(TAG, "BatModel angle center x=" + mBatt.getAngleCenter().x + "|y="+mBatt.getAngleCenter().y);
		Lg.i(TAG, "BatModel position x=" + mBatt.getItemArray().get(0).getPosition().x + "|y="+mBatt.getItemArray().get(0).getPosition().y);
		Lg.i(TAG, "collision test rect left= " + rect.left + " top "+ rect.top + " right " + rect.right + " bottom " + rect.bottom);
		Lg.i(TAG, "detected block size is " + cl.size());
		
    while(ite.hasNext()){
			item = ite.next();
			PointF counter = item.getPosition();
			Lg.d(TAG, "collision item " + counter.x +" | "+ counter.y);
			
      if(d == ScrollManager.Direct.LEFT && 
			counter.x < center.x && 
			(counter.y < center.y + GameConfig.WIDTH &&
			counter.y > center.y - GameConfig.WIDTH)) {
				move = true;
				break;
			} 
			if(d == ScrollManager.Direct.RIGHT && 
			counter.x > center.x && 
			(counter.y < center.y + GameConfig.WIDTH &&
			counter.y > center.y - GameConfig.WIDTH)) {
				move = true;
				break;
			}
			if(d == ScrollManager.Direct.UP && 
			counter.y > center.y && 
			(counter.x < center.x + GameConfig.WIDTH &&
			counter.x > center.x - GameConfig.WIDTH)) {
				move = true;
				break;
			}
			if(d == ScrollManager.Direct.DOWN && 
			counter.y < center.y && 
			(counter.x < center.x + GameConfig.WIDTH &&
			counter.x > center.x - GameConfig.WIDTH)){
				move = true;
				break;
			}
    }
		
		if(move){
			Lg.i(TAG, "automove " +d);
			//int cw = (((BlockItem)item).getBlockType() == BlockItem.Type.RIGHT)? 0:1;
			mBatt.updatePosition(d);
			scroll(d);
		}
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		return super.onTouchEvent(event);
	}

	@Override
	public void notifyFinish(ItemBase i, int type)
	{
		// TODO: Implement this method
	}

	@Override
	public void notifyNext()
	{
		scroll(ScrollManager.Direct.NONE);
	}
	
	private void scroll(ScrollManager.Direct d){
		
		switch(d){
			case UP:
				if(GameConfig.BLOCKSCROLL != 0) {
					doScroll(ScrollSequenceItem.UP);					
				}
				break;
				
			case DOWN:
				if(GameConfig.BLOCKSCROLL != 0){
					doScroll(ScrollSequenceItem.DOWN);
				}
				break;
				
			case LEFT:
				if(GameConfig.HORIZSCROLL != 0){
					doScroll(ScrollSequenceItem.LEFT);
				}
				break;
				
			case RIGHT:
				if(GameConfig.HORIZSCROLL != 0){
					doScroll(ScrollSequenceItem.RIGHT);
				}
				break;
				
			case NONE:
				doScroll(ScrollSequenceItem.NONE);
				break;
		}
	}
	
	private void doScroll(ScrollSequenceItem si){
		Iterator ite = mCollisionModels.iterator();
		while(ite.hasNext()){
			SpriteModel m = (SpriteModel)ite.next();
			if(m.getScrollable()){
				m.changeMotion(
					//todo
					new MotionSequnce[]{
						new MotionSequnce(si.mTick, si.mPpf, si.mDirect),
						new MotionSequnce(-1,0,new Vec2(0,0))
					}
				);
			}
		}
	}
	
	@Override
	public boolean onUpdate()
	{
		if(FpsController.mTestCounrt == 0){
			Lg.w(TAG, "alive");
		}
		FpsController.mTestCounrt++;
		
		if(mIsFirstUpdate){
			if(mBatt != null){
				ItemBase i =  mBatt.createItem(0);
				i.setType(GLEngine.BATTMODELINDX);
			}
			mBlockGenerater.createInitialItem();
			mIsFirstUpdate = false;
			mIsReady = true;
		} else if (GameConfig.COMMONSCROLL != 0) {
	
			boolean b = mScrollSequencer.onUpdate();
			if(b){
				// create block when scroll break event
				mBlockGenerater.createRequest();
			}
			
			
		}
		super.onUpdate();
		
		return mIsReady;
	}
	
	
	
	// collision position 
	/*
	 CollidableItem high=null, low=null, left=null, right=null;
	 Iterator ite = cl.iterator();
	 while(ite.hasNext()){
	 CollidableItem itm = (CollidableItem)ite.next();
	 if(high != null){
	 if(high.getPosition().y < itm.getPosition().y){
	 high = itm;
	 }
	 } else {
	 high = itm;
	 }

	 if(low != null){
	 if(low.getPosition().y > itm.getPosition().y){
	 low = itm;
	 }
	 } else {
	 low = itm;
	 }

	 if(left != null){
	 if(left.getPosition().x > itm.getPosition().x){
	 left = itm;
	 }
	 } else {
	 left = itm;
	 }

	 if(right != null){
	 if(right.getPosition().x < itm.getPosition().x){
	 right = itm;
	 }
	 } else {
	 right = itm;
	 }
	 }
	*/
}

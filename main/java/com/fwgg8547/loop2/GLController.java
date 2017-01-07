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
BatModel.DirectionDetectListener,
TouchModel.CreateCallback
{
	private final static String TAG = GLController.class.getSimpleName();
	
	private BlockModel mBlock;
	private TouchModel mTouch;
	private BlockGenerater mBlockGenerater;
 	//private BlockGenerater2 mBlockGenerater;
	private ScrollManager mScrollManager;
	private BatModel mBatt;
	private WallModel2 mWall;
	private ScrollSequencer mScrollSequencer;
	private GestureListener mGl;
	private List<ItemBase> mCollisionList;
	private ScrollManager.Direct mAutoDirect;
	private boolean mIsFirstUpdate;
	private boolean mIsReady;
	private TouchItem mTouchItem;
	private BlockItem mSelectedBlock;
	
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
		mTouchItem = null;
	}

	@Override
	public void created(TouchItem item)
	{
		Lg.i(TAG,"Created !");
		mTouchItem = item;
	}
	
	@Override
	public void levelChanged(int newLevel)
	{
		mScrollSequencer.changeDirectRequest();
	}

	@Override
	public void onDown(PointF pos)
	{
		Lg.i(TAG, "onDown " + pos.x + ":" + pos.y);
		
		RectF rect = new RectF(0, 0, 10, 10);
		
		mTouch.createItemRequest(rect, pos,this);
		return;
	}

  @Override
  public void onUp(PointF pos) {
    Lg.i(TAG, "onDown " + pos.x + ":" + pos.y);
    releaseSelected();
    return;
  }
  
	@Override
	public void onSingleTapUp(PointF pos)
	{
		Lg.i(TAG, "onSingleTapUp " + pos.x + ":" + pos.y);
		RectF rect = new RectF(pos.x, pos.y,
                           pos.x+1, pos.y+1);
    releaseSelected();
    return;
	}
	
	@Override
	public void onFling(PointF pos1,PointF pos2, float vx, float vy)
	{
		Lg.i(TAG, "onFling " + "pos1=" +pos1.x + ":" + pos1.y + " pos2= " + pos2.x + ":" + pos2.y);
		releaseSelected();
		/*
		RectF rect;
		PointF p1,p2;
    TouchItem.FlickType t;
    float dx = pos2.x - pos1.x;
    float dy = pos2.y - pos1.y;
    if (dx >= 0) {
      // pos2.x > pos1.x
      if (dx*dx > dy*dy) {
        t = TouchItem.FlickType.RIGHT;
        if (pos1.y > pos2.y) {
          rect = new RectF(pos1.x, pos2.y, pos2.x, pos1.y);
        } else {
          rect = new RectF(pos1.x, pos1.y, pos2.x, pos2.y);
        }
      } else {
        if (dy >= 0) {
          // pos2.y > pos1.y
          t = TouchItem.FlickType.TOP;
          rect = new RectF(pos1.x, pos1.y, pos2.x, pos2.y);
        } else {
          // pos1.y > pos2.y
          t = TouchItem.FlickType.BOTTOM;
          rect = new RectF(pos1.x, pos2.y, pos2.x, pos1.y);
        }
      }
    } else {
      // pos1.x > pos2.x
      if (dx*dx > dy*dy) {
        t = TouchItem.FlickType.LEFT;
        if (pos1.y > pos2.y) {
          rect = new RectF(pos2.x, pos2.y, pos1.x, pos1.y);
        } else {
          rect = new RectF(pos2.x, pos1.y, pos1.x, pos2.y);
        }
        
      } else {
        if (dy >= 0) {
          t = TouchItem.FlickType.TOP;
          rect = new RectF(pos2.x, pos1.y, pos1.x, pos2.y);
        } else {
          t = TouchItem.FlickType.BOTTOM;
          rect = new RectF(pos2.x, pos2.y, pos1.x, pos1.y);
        }
      }
    }
		if(mTouch == null){
			return;
		}
		
		mTouch.createItemRequest(pos1, pos2, t);
		*/
    return;
	}
  
	@Override
	public void onScroll(PointF pos1, PointF pos2, float x, float y)
	{
		Lg.i(TAG, "scroll x y " + pos2.x + " ; " + pos2.y +";" +x+";"+y);
		if(mTouchItem != null){
			mTouchItem.setPosition(pos2.x, pos2.y, 0, 0);
		}
		if(mSelectedBlock != null){
			mSelectedBlock.setNextTop(mTouchItem.getPosition());
		}
	}
	
	private void releaseSelected(){
		if(mTouch != null){
			mTouch.deleteAll();
			mTouchItem = null;
		}

		if(mSelectedBlock != null){
			mSelectedBlock.select(false);
			mSelectedBlock = null;
		}
		
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
		/*
		if(g instanceof BlockGenerater2){
			mBlockGenerater = (BlockGenerater2)g;
		}
		*/
		if(g instanceof BlockGenerater){
			mBlockGenerater = (BlockGenerater)g;
		}
	}

	@Override
	public boolean broadCollision(ItemBase item1, ItemBase item2)
	{
		if((item1.getType() == item2.getType())){
			return false;
		}
		return true;
	}

	@Override
	public boolean narrowCollision(ItemBase item1, ItemBase item2)
	{
    Lg.d(TAG, "item type " + item1.getType() + " : " + item2.getType());
    if((item1.getType() == GLEngine.TOUCHMODELINDX) &&
       (item2.getType() == GLEngine.BLOCKMODELINDX)){
      
			mSelectedBlock = (BlockItem)item2;
			mSelectedBlock.select(true);
    } else if((item2.getType() == GLEngine.TOUCHMODELINDX) &&
              (item1.getType() == GLEngine.BLOCKMODELINDX)){
				mSelectedBlock = (BlockItem)item1;
				mSelectedBlock.select(true);
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

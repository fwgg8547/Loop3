package com.fwgg8547.loop2.model;

import com.fwgg8547.loop2.gamebase.util.Lg;
import com.fwgg8547.loop2.gamebase.util.ReadersWriterLock;
import com.fwgg8547.loop2.gamebase.modelbase.ItemBase;
import com.fwgg8547.loop2.gamebase.modelbase.Sprite;
import com.fwgg8547.loop2.gamebase.modelbase.ModelGroup;
import com.fwgg8547.loop2.gamebase.modelbase.CollisionModel;
import com.fwgg8547.loop2.gamebase.modelbase.CollidableItem;
import com.fwgg8547.loop2.gamebase.sequencerbase.ItemPattern;
import com.fwgg8547.loop2.gamebase.util.*;
import com.fwgg8547.loop2.gamebase.controllerbase.*;
import com.fwgg8547.loop2.R;
import com.fwgg8547.loop2.generater.ResourceFileReader;

import java.util.List;
import java.util.ArrayList;
import android.graphics.PointF;
import android.graphics.RectF;

import com.fwgg8547.loop2.GLEngine;
import com.fwgg8547.loop2.gamebase.sequencerbase.*;
import com.fwgg8547.loop2.*;
import java.util.*;
import com.fwgg8547.loop2.gamebase.modelbase.*;

public class BlockModel extends CollisionModel
{
	
	private static final String TAG = BlockModel.class.getSimpleName();
	public static final int MAX_BLOCK = GameConfig.MAPWIDTH*GameConfig.MAPHEIGHT+GameConfig.MAPHEIGHT*3;
	private static boolean mIsFirstUpdate;
	private static MoveChecker mMoveChecker;
	private Generater mGenerater;
	private RectF mScreenInfo;
	private boolean mIsSelect;
	
	public class MoveChecker {
		private boolean mIsMoving;
		
		public MoveChecker(){}
		
		synchronized public void setMoving(boolean move){
			Lg.d(TAG, "setMoving " + move);
			mIsMoving = move;
		}
		
		synchronized public boolean isMoving(){
			return mIsMoving;
		}
	}
	
	public BlockModel(){
		mIsFirstUpdate = true;
		mIsSelect = false;
		mGenerater = new Generater();
	}
	
	public void initialize(ReadersWriterLock lock, int offset, ModelGroup mg, int p){
		super.initialize(lock, offset, MAX_BLOCK, mg, p);
		
		// repleace own iremlists
		BlockItem[] items = new BlockItem[MAX_BLOCK];
		for(int i=0;i<MAX_BLOCK; i++){
			items[i] = new BlockItem();
			items[i].mIsDeleted = true;
			items[i].mIndex = i;
		}
		mItemList.initialize(items);
		mIsFirstUpdate = true;
		mGenerater.init(offset, MAX_BLOCK);
		mIsScrollable = true;
		mMoveChecker = new MoveChecker();
		mScreenInfo = ScreenModel.getInstance().getScreenInfo();
	}

	@Override
	public void onUpdate()
	{
		Lg.a(TAG, "update");
		boolean bIsAllItemSelected = true;
		try{
			mLock.writeLock();
			for(int i=0; i<mItemList.size();i++){
				BlockItem itm = (BlockItem)mItemList.get(i);
				if(itm.mIsDeleted){
					Lg.d(TAG, "block was deleted id =" +itm.getId());
					
					freeItem(i);
					i--; // mblock was reduced
				}
				
				if(itm.getPosition().y < GameConfig.DELETEMERGIN ||
						itm.getPosition().x < GameConfig.DELETEMERGIN) {
					itm.mIsDeleted = true;
				}
				
				if(itm.isSelect()){
					itm.changeColor();
				} else {
					bIsAllItemSelected = false;
				}
				
				// animation check
				if(!itm.mIsDeleted) {
					PointF pos = itm.moveAnimation();
				}
			}
			
			if(bIsAllItemSelected){
				for(int i=0; i<mItemList.size();i++){
					BlockItem itm = (BlockItem)mItemList.get(i);
					itm.select(false);
					itm.changeColor();
				}
			}
		} catch(Exception e){
			Lg.e(TAG, "a "+e.toString());
			Lg.stack(TAG, e.getStackTrace());
		} finally{
			mLock.writeUnlock();
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
	
	public boolean isMoving() {
		return mMoveChecker.isMoving();
	}
	
	public void select(List<BlockItem> items){
		Iterator<BlockItem> ite = items.iterator();
		while(ite.hasNext()){
			BlockItem item = ite.next();
			item.select(true);
		}
	}

  public void attack(BlockItem item, int t){
		Lg.i(TAG,"attack " + t);
    if(item != null){
      if(item.attack(t)){
				deleteItem(item);
			}
    }
  }
  
  public void select(BlockItem item){
    if(item != null) {
      item.select(true);
    }
  }
	
	public ItemBase createItem(int pattern, float y){
		BlockItem ib = null;
		try{
			mLock.writeLock();
			ib = (BlockItem)super.createItem();
			ib.setType(GLEngine.BLOCKMODELINDX);
			ItemPattern p = ResourceFileReader.getPattern(ResourceFileReader.Type.Block,pattern);
			if(p.mInitPos != null){
				p.mInitPos.y = y;
			}			
			mGenerater.createItem(ib, p);
		} catch (Exception e){
			Lg.e(TAG, e.toString());
		} finally {
			mLock.writeUnlock();
		}
		return ib;		
	}
	
	public ItemBase createItem(float x, float y){
		BlockItem ib = null;
		try{
			mLock.writeLock();
			ib = (BlockItem)super.createItem();
			ib.setType(GLEngine.BLOCKMODELINDX);
			ItemPattern p = ResourceFileReader.getPattern(ResourceFileReader.Type.Block, 0);
			p.mInitPos.y = y;
			p.mInitPos.x = x;
			mGenerater.createItem(ib, p);
		} catch (Exception e){
			Lg.e(TAG, e.toString());
		} finally {
			mLock.writeUnlock();
		}
		return ib;		
	}

  // Call from ItemGenerater
	@Override
	public ItemBase createItem(int pattern)
	{
		BlockItem ib = null;
		try{
			mLock.writeLock();
			ib = (BlockItem)super.createItem();
			ib.setType(GLEngine.BLOCKMODELINDX);
			//ib.setType(pattern);
			ItemPattern p = ResourceFileReader.getPattern(ResourceFileReader.Type.Block,pattern);
			mGenerater.createItem(ib, p);
      ib.setBlockType(pattern);
		} catch (Exception e){
			Lg.e(TAG, e.toString());
		} finally {
			mLock.writeUnlock();
		}
		return ib;
	}

	public void deleteItem(BlockItem itm){
		try{
			Lg.i(TAG, "delete items");
			mLock.writeLock();
			itm.mIsDeleted = true;
		} catch(Exception e){
			Lg.e(TAG, e.toString());
		} finally{
			mLock.writeUnlock();
		}
	}
	
	public void deleteItem(List<BlockItem> list){
		try{
			Lg.i(TAG, "delete items");
			mLock.writeLock();
			Iterator ite = list.iterator();
			while(ite.hasNext()){
				BlockItem b = (BlockItem)ite.next();
				b.mIsDeleted = true;
			}
		} catch(Exception e){
			Lg.e(TAG, e.toString());
		} finally{
			mLock.writeUnlock();
		}
	}
	
	@Override
	public int getTextureId()
	{
		return R.drawable.ic_launcher;
	}

	@Override
	public int getTextureCount()
	{
		// TODO: Implement this method
		return 1;
	}

	@Override
	public void changeMotion(MotionSequnce[] ms)
	{
		if(mMoveChecker.isMoving()){
			Lg.w(TAG, "changeMotion failed. now moving");
			return;
		}
		
		try{
			mLock.writeLock();
			mMoveChecker.setMoving(true);
			MotionSequnce[] next = ms;
			ItemBase itm = null;
			for(int i=0; i<mItemList.size();i++){
				itm = mItemList.get(i);
				itm.setMotionPattern(next,
				
				new AnimationSequencer.Callback(){
						public void notify(ItemBase i, int type){
							if(type == 0) {
								Lg.d(TAG, "change motion callback");
								mMoveChecker.setMoving(false);
							}
						}
					}
				);
			}
		} catch(Exception e){
			Lg.e(TAG,e.toString());
		} finally {
			mLock.writeUnlock();
		}
	}

	public class Generater {
		
		private int mOffset;
		private int mCurr;
		private int mMax;
		
		public Generater (){
		}
		
		public void init(int offset, int max){
			mMax = max;
			mOffset = offset;
			mCurr =0;
		}
		
		public void addTopLine(){
			
		}

		
		public BlockItem createItem(BlockItem i, ItemPattern p){
			BlockItem it = i;
			try {
				Sprite s = new Sprite(mOffset + mCurr);
				it.setId(mOffset+mCurr);
				it.setSprite(s);
				
				if(p.mInitPos != null){
					it.setPosition(p.mInitPos.x, p.mInitPos.y, 0.0f, 0.0f);
					it.setAngleCenter(p.mInitPos.x, p.mInitPos.y);
				}
				if(p.mRect != null){
					it.setRect(p.mRect);
				}
				if(p.mMotionPattern != null){
					it.setMotionPattern(p.mMotionPattern, null);
				}
				if(p.mScalePattern != null){
					it.setScalePattern(p.mScalePattern, null);
				}
				if(p.mTexturePattern != null){
					it.setTexturePattern(p.mTexturePattern, 0,  null);
				}
				it.setAnimationValid(true);
				it.moveAnimation(); // init 
				it.updateVertix();
				it.mIsDeleted = false;
				Lg.i(TAG, "create block = "+it.getId());
				mCurr++;
				
			}catch(Exception e){
				Lg.stack(TAG, e.getStackTrace());
			}finally{
			}
			return it;
		}
	}

	public void deleteItem(CollidableItem it){
		ItemPattern p = ResourceFileReader.getPattern(ResourceFileReader.Type.Block, 0);
		it.delete(p);
	}
}

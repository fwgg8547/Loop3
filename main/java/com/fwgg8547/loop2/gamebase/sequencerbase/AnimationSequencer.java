package com.fwgg8547.loop2.gamebase.sequencerbase;

import com.fwgg8547.loop2.gamebase.util.*;
import com.fwgg8547.loop2.gamebase.modelbase.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class AnimationSequencer
{
	private final static  String TAG = AnimationSequencer.class.getSimpleName();
	private final int MAX_TICK_COUNT = 60000;
	private int mTick;
	private MotionSequnce[]  mMotionSequence;
	private RotateSequence[] mRotateSequence;
	private ScaleSequence[]  mScaleSequence;
	private TextureSequence[] mTextureSequence;
  private ManualSequence [] mManualSequece;
	private Vec2 mCurrentVect;
	private List<Callback> mCallbacks;
	private ItemBase mOwnerItem;
	private CurrentFrame mAnimFrame;
	
	public interface ScaleFunc  {
		abstract public Vec2 nextScale();
	}
	
	public interface Callback{
		abstract public void notify(ItemBase i, int type);
	}
	
	
	public static class CurrentFrame {
		public int mtick; // motion
		public int rtick; // rotate
		public int stick; // scale
		public int ttick; // texture
    public int ftick; // func
    
		public int mindex;
		public int rindex;
		public int sindex;
		public int tindex;
		public int findex;
    
		public boolean mvalid;
		public boolean rvalid;
		public boolean svalid;
		public boolean tvalid;
    public boolean fvalid;
	}
	
	public AnimationSequencer(){
		mCurrentVect = new Vec2(0,0);
		mAnimFrame = new CurrentFrame();
		mCallbacks = new ArrayList<Callback>();
	}

	public void Initialize(MotionSequnce[] ms, ItemBase i, Callback cb){
		Lg.d(TAG, "initialize");
		mAnimFrame.mvalid = false;
		mAnimFrame.mindex = 0;
		mMotionSequence = ms;
		mTick = 0;
		mAnimFrame.mtick = mMotionSequence[0].frame;
		
		if(cb != null){
			mCallbacks.add(cb);
			mOwnerItem = i;
		}
		mAnimFrame.mvalid = true;
	}

	public void Initialize(RotateSequence[] rs, ItemBase i, Callback cb){
		mAnimFrame.rvalid = false;
		mAnimFrame.rindex = 0;
		mRotateSequence = rs;
		mTick = 0;
		mAnimFrame.rtick = mRotateSequence[0].frame;

		if(cb != null){
			mCallbacks.add(cb);
			mOwnerItem = i;
		}
		mAnimFrame.rvalid = true;
	}
	
	public void clear(){
		
	}
	
	public void Initialize(ScaleSequence[] ms, ItemBase i, Callback cb){
		mAnimFrame.svalid = false;
		mAnimFrame.sindex = 0;
		mScaleSequence = ms;
		mTick = 0;
		mAnimFrame.stick = mScaleSequence[0].frame;
		if(cb != null){
			mCallbacks.add(cb);
			mOwnerItem = i;
		}
		mAnimFrame.svalid = true;
	}
	
	public void Initialize(TextureSequence[] ms, ItemBase i, Callback cb){
		mAnimFrame.tvalid = false;
		mAnimFrame.tindex = 0;
		mTextureSequence = ms;
		mTick = 0;
		mAnimFrame.ttick = mTextureSequence[0].frame;
		if(cb != null){
			mCallbacks.add(cb);
			mOwnerItem = i;
		}
		mAnimFrame.tvalid = true;
	}

	public void Initialize(TextureSequence[] ms, int init, ItemBase i, Callback cb){
		mAnimFrame.tvalid = false;
		mAnimFrame.tindex = init;
		mTextureSequence = ms;
		mTick = 0;
		mAnimFrame.ttick = mTextureSequence[0].frame;
		if(cb != null){
			mCallbacks.add(cb);
			mOwnerItem = i;
		}
		mAnimFrame.tvalid = true;
	}

  public void Initialize(ManualSequence[] ms, ItemBase i, Callback cb) {
    
    mTick = 0;
    mAnimFrame.fvalid = false;
    mAnimFrame.ftick = ms[0].frame;
    mAnimFrame.findex = 0;
    mManualSequece = ms;
    
    if(cb != null) {
      mCallbacks.add(cb);
      mOwnerItem = i;
    }
    
    mAnimFrame.fvalid = true;
  }
  
	public void Initialize(MotionSequnce[] ms, RotateSequence[] rs ,ScaleSequence[] as, TextureSequence[] ts, ItemBase i, Callback cb){
		mAnimFrame.mvalid = false;
		mAnimFrame.rvalid = false;
		mAnimFrame.svalid = false;
		mAnimFrame.tvalid = false;
		
		mScaleSequence = as;
		mMotionSequence = ms;
		mTextureSequence = ts;
		mRotateSequence = rs;
		
		mCallbacks.add(cb);
		
		mTick = 0;
		mAnimFrame.sindex = 0;
		mAnimFrame.stick = mScaleSequence[0].frame;
		mAnimFrame.svalid = true;
		
		mAnimFrame.mindex = 0;
		mAnimFrame.mtick = mMotionSequence[0].frame;
		mAnimFrame.mvalid = true;
		
		mAnimFrame.rindex = 0;
		mAnimFrame.rtick = mRotateSequence[0].frame;
		mAnimFrame.rvalid = true;
		
		mAnimFrame.tindex = 0;
		mAnimFrame.ttick = mTextureSequence[0].frame;
		mAnimFrame.tvalid = true;
		
		if(cb != null){
			mCallbacks.add(cb);
			mOwnerItem = i;
		}
	}

  public void stopManualFunc() {
    mAnimFrame.fvalid = false;
  }
  
	public void stopMotion(){
		mAnimFrame.mvalid = false;
	}
	
	public void stopRotate(){
		mAnimFrame.rvalid = false;
	}
	
	public void stopScale(){
		mAnimFrame.svalid = false;
		
	}
	
	public void stopTexture(){
		mAnimFrame.tvalid = false;

	}
	
	public void valid(boolean v) {
		if(v){
			mAnimFrame.mvalid = true;
			mAnimFrame.svalid = true;
			mAnimFrame.tvalid = true;
			mAnimFrame.rvalid = true;
      		mAnimFrame.fvalid = true;
		} else {
			mAnimFrame.mvalid = false;
			mAnimFrame.svalid = false;
			mAnimFrame.tvalid = false;
			mAnimFrame.rvalid = false;
      		mAnimFrame.fvalid = false;
		}
	}

	public boolean getValid() {
		return (mAnimFrame.mvalid ||mAnimFrame.rvalid || mAnimFrame.svalid || mAnimFrame.tvalid || mAnimFrame.fvalid);
	}

	public boolean getMotionValid() {
		return mAnimFrame.mvalid;
	}
	
	public boolean getRotateValid() {
		return mAnimFrame.rvalid;
	}

  public boolean getManualFuncValid() {
    return mAnimFrame.fvalid;
  }

	public void tick(){
		if(!(mAnimFrame.mvalid || mAnimFrame.rvalid || mAnimFrame.svalid || mAnimFrame.tvalid || mAnimFrame.fvalid)){
			return;
		}
		
		if (mMotionSequence != null 
		&& mAnimFrame.mvalid 
		&& mAnimFrame.mtick != 0
		&& mTick > 0
		&& (mTick % mAnimFrame.mtick) == 0){
			mAnimFrame.mindex++;
			if (mAnimFrame.mindex >= mMotionSequence.length) {
				mAnimFrame.mindex = 0;
			}
			mAnimFrame.mtick = mMotionSequence[mAnimFrame.mindex].frame;
			
			if (mAnimFrame.mtick < 0){
				mAnimFrame.mvalid = false;
				for(int i=0, n=mCallbacks.size();i<n;i++){
					mCallbacks.remove(i).notify(mOwnerItem,0);
				}
			}
		}
		
		if (mRotateSequence != null 
				&& mAnimFrame.rvalid 
				&& mAnimFrame.rtick != 0
				&& mTick > 0
				&& (mTick % mAnimFrame.rtick) == 0){
			mAnimFrame.rindex++;
			if (mAnimFrame.rindex >= mRotateSequence.length) {
				mAnimFrame.rindex = 0;
			}
			mAnimFrame.rtick = mRotateSequence[mAnimFrame.rindex].frame;
			if (mAnimFrame.rtick < 0){
				mAnimFrame.rvalid = false;
				for(int i=0, n=mCallbacks.size();i<n;i++){
					mCallbacks.remove(i).notify(mOwnerItem,1);
				}
			}
		}
		
		if (mScaleSequence != null 
		&& mAnimFrame.svalid
		&& mAnimFrame.stick != 0
		&& mTick > 0
		&&(mTick % mAnimFrame.stick) == 0){
			mAnimFrame.sindex++;
			if (mAnimFrame.sindex >= mScaleSequence.length) {
				mAnimFrame.sindex = 0;
			}
			
			mAnimFrame.stick = mScaleSequence[mAnimFrame.sindex].frame;
			if (mAnimFrame.stick < 0){
				mAnimFrame.svalid = false;
				for(int i=0, n=mCallbacks.size();i<n;i++){
					mCallbacks.remove(i).notify(mOwnerItem,2);
				}
			}
		}

		if (mTextureSequence != null 
		&& mAnimFrame.tvalid
		&& mAnimFrame.ttick != 0
		&& mTick > 0
		&&(mTick % mAnimFrame.ttick) == 0){
			mAnimFrame.tindex++;
			if (mAnimFrame.tindex >= mTextureSequence.length) {
				mAnimFrame.tindex = 0;
			}

			mAnimFrame.ttick = mTextureSequence[mAnimFrame.tindex].frame;
			if (mAnimFrame.ttick < 0){
				mAnimFrame.tvalid = false;
				for(int i=0, n=mCallbacks.size();i<n;i++){
					mCallbacks.remove(i).notify(mOwnerItem,3);
				}
			}
		}

    	if (mManualSequece != null
    	&& mAnimFrame.fvalid
    	&& mAnimFrame.ftick != 0
    	&& mTick > 0
    	&& (mTick % mAnimFrame.ftick) == 0) {
			mAnimFrame.findex++;

			if (mAnimFrame.findex >= mManualSequece.length) {
				mAnimFrame.findex = 0;
			}

			mAnimFrame.ftick = mManualSequece[mAnimFrame.findex].frame;

			if (mAnimFrame.ftick < 0) {
				mAnimFrame.fvalid = false;
				for (int i = 0, n = mCallbacks.size(); i < n; i++) {
					mCallbacks.remove(i).notify(mOwnerItem, 4);
				}
			}
		}

		mTick++;
		if(mTick >= MAX_TICK_COUNT){
			mTick = 0;
		}
	}

	public Vec2 getVector() {
		if(mAnimFrame.mvalid && (mMotionSequence != null)){
			float ppf = mMotionSequence[mAnimFrame.mindex].ppf;
			mCurrentVect.x = mMotionSequence[mAnimFrame.mindex].direct.x * ppf;
			mCurrentVect.y = mMotionSequence[mAnimFrame.mindex].direct.y * ppf;
			return mCurrentVect;
		} else {
			return null;
		}
	}

	public float getRotate() {
		if(mAnimFrame.rvalid && (mRotateSequence != null)){
			float dpf = mRotateSequence[mAnimFrame.rindex].dpf;
			return dpf;
		} else {
			return 0;
		}
	}
	
	public Vec2 getScale(){
		try{
			if(!mAnimFrame.svalid) return null;
			return new Vec2(
				mScaleSequence[mAnimFrame.sindex].scalex,
				mScaleSequence[mAnimFrame.sindex].scaley
			);
		}catch(Exception e){
			return null;
		}
	}
	
	public float[] getTextureUv(){
		try{
			if(!mAnimFrame.tvalid) return null;
			return mTextureSequence[mAnimFrame.tindex].uv;
		}catch(Exception e){
			return null;
		}
	}

  public Vec2 getFunc() {
		try{
			if(!mAnimFrame.fvalid) return null;
			return mManualSequece[mAnimFrame.findex].func.doFunc();
		} catch(Exception e){
			return null;
		}
  }
}

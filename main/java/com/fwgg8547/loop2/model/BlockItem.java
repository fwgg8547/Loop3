package com.fwgg8547.loop2.model;

import com.fwgg8547.loop2.gamebase.modelbase.CollidableItem;
import com.fwgg8547.loop2.gamebase.sequencerbase.*;
import com.fwgg8547.loop2.gamebase.modelbase.*;
import com.fwgg8547.loop2.gamebase.util.*;
import com.fwgg8547.loop2.anim.*;

import android.graphics.*;

public class BlockItem extends CollidableItem
{
	private static final String TAG = BlockItem.class.getSimpleName();
	private static final float[] SELECTED = new float[]{1,1,0,1};
	private static final float[] UNSELECTED = new float[]{1,1,1,1};

	private static final RotateSequence[] RIGHT = new RotateSequence[]{
		new RotateSequence(360, 10)
	};

	private Type mType;
	private boolean mIsSelect;
	private Quadrilateral mNewQuad;
  private Quadrilateral mInitQuad;
  private SpringAnim mSpringAnim;

	public enum Type{
    TOP,
    BOTTOM,
		RIGHT,
		LEFT,
    CENTER
	}
	
	public BlockItem(){
		super();
		mIsSelect = false;
	}

	@Override
	public void setQuadrilateral(Quadrilateral q)
	{
		// TODO: Implement this method
		super.setQuadrilateral(q);
		if(mInitQuad == null){
			mInitQuad = new Quadrilateral(q);
		}
	}
	
	public void setNextTop(PointF pos){
		Quadrilateral o = mSprite.getQuad();
		PointF opos = getPosition();
		mNewQuad = new Quadrilateral(
		new PointF(o.topleft.x, o.topleft.y),
		new PointF(o.topright.x, o.topright.y),
			new PointF(o.bottomright.x, pos.y-opos.y),
			new PointF(o.bottomleft.x, pos.y-opos.y)
		);
	}
	
	public void setNextRight(PointF pos){
		Quadrilateral o = mSprite.getQuad();
		PointF opos = getPosition();
		mNewQuad = new Quadrilateral(
			new PointF(o.topleft.x, o.topleft.y),
			new PointF(pos.x-opos.x, o.topright.y),
			new PointF(pos.x-opos.x, o.bottomright.y),
			new PointF(o.bottomleft.x, o.bottomleft.y)
		);
	}
	
	public void setNextTop(Vec2 pos){
		Quadrilateral o = mSprite.getQuad();
		PointF opos = getPosition();
		mNewQuad = new Quadrilateral(
			new PointF(o.topleft.x, o.topleft.y),
			new PointF(o.topright.x, o.topright.y),
			new PointF(o.bottomright.x, pos.y-opos.y),
			new PointF(o.bottomleft.x, pos.y-opos.y)
		);
	}
	
	public void  setNextBottom(PointF bottomleft, PointF bottomright){
		
	}
	
	public void execConv(){
	 setQuadrilateral(mNewQuad);
	}
	
	public void setBlockType(int t){
		switch(t){
			case 1:
				mType = Type.TOP;
				mSprite.setColor(new float[]{1,1,1,1 });
				break;
			case 2:
				mType = Type.BOTTOM;
				mSprite.setColor(new float[]{1,0,1,1 });
				break;
			case 3:
				mType = Type.RIGHT;
				mSprite.setColor(new float[]{1,1,0,1 });
				break;
			case 4:
				mType = Type.LEFT;
				mSprite.setColor(new float[]{0,1,1,1 });
				break;
			case 5:
				mType = Type.CENTER;
				mSprite.setColor(new float[]{0,0,1,1 });
				break;
			default :
				mType = Type.LEFT;
				mSprite.setColor(new float[]{1,0,0,1 });
		}
	}
  
	public void setBlockType(Type t){
		switch(t){
			case RIGHT:
				mType = Type.RIGHT;
				mSprite.setColor(new float[]{1,1,1,1 });
				break;
			default :
				mType = Type.LEFT;
				mSprite.setColor(new float[]{1,0,0,1 });
		}
	}
	
	public Type getBlockType(){
		return mType;
	}
	
	public void changeFigure(){
		if(mNewQuad != null){
			setQuadrilateral(mNewQuad);
			mNewQuad = null;
		}
	}
	
	public void changeColor(){
		mSprite.setColor(SELECTED);
		mIsSelect = false;
	}

	public void select(boolean b){
    if (b != mIsSelect) {
      mIsSelect = b;
      if (b) {
        // select
				mAnimSequencer.stopManualFunc();
        //mInitQuad = mSprite.getQuad();
      } else {
        // release
				Vec2 init = new Vec2(mInitQuad.bottomleft.x, mInitQuad.bottomleft.y);
				Quadrilateral q = mSprite.getQuad();
				Vec2 now = new Vec2(q.bottomleft.x, q.bottomleft.y);
				mSpringAnim = new SpringAnim(init, now);
				ManualSequence ms[] = {new ManualSequence(120, mSpringAnim),
				new ManualSequence(-1,null)};
				mAnimSequencer.Initialize(ms,this,null);
				/*
				new AnimationSequencer.Callback(){
					public void notify(ItemBase i, int type){
						
					}
				}
				);
				*/
        //mNewQuad = mInitQuad;
      }
    }
	}
	
	public boolean isSelect(){
		return mIsSelect;
	}

  public boolean attack(int i){
		boolean res = false;
    Type t = convInt2Type(i);
    if (mType == t){
      select(true);
			res = true;
    }
		return res;
  }
  
  private Type convInt2Type(int i){
    Type t;
    switch(i) {
    case 0:
      t = Type.TOP;
      break;
    case 1:
      t = Type.BOTTOM;
      break;
    case 2:
      t = Type.RIGHT;
      break;
    case 3:
      t = Type.LEFT;
      break;
    default:
      t = Type.CENTER;
    }
    return t;
  }
}

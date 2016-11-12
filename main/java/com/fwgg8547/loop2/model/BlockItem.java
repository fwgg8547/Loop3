package com.fwgg8547.loop2.model;

import com.fwgg8547.loop2.gamebase.modelbase.CollidableItem;

public class BlockItem extends CollidableItem
{
	private static final String TAG = BlockItem.class.getSimpleName();
	private static final float[] SELECTED = new float[]{1,1,0,1};
	private static final float[] UNSELECTED = new float[]{1,1,1,1};
	private Type mType;
	private boolean mIsSelect;
	
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
	
	public void changeColor(){
		mSprite.setColor(SELECTED);
		mIsSelect = false;
	}
	
	public void select(){
		mIsSelect = true;
		if(mIsSelect){
			mSprite.setColor(SELECTED);
		} else{
			mSprite.setColor(UNSELECTED);
		}
		
	}
	
	public void select(boolean b){
		mIsSelect = b;
		//mSprite.setColor(SELECTED);
	}
	
	public boolean isSelect(){
		return mIsSelect;
	}

  public boolean attack(int i){
		boolean res = false;
    Type t = convInt2Type(i);
    if (mType == t){
      select();
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

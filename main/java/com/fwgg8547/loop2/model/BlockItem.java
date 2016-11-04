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
		RIGHT,
		LEFT,
	}
	
	public BlockItem(){
		super();
		mIsSelect = false;
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
}

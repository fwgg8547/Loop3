package com.fwgg8547.loop2.model;

import com.fwgg8547.loop2.GameConfig;

public class BlockMap3
{
	private static final String TAG = BlockMap2.class.getSimpleName();

	private BlockItem[][] mBoardArray;
	
	public static final BlockMap3 INSTANCE = new BlockMap3();

	private BlockMap3(){}

	public void initialize(){
		mBoardArray = new BlockItem[GameConfig.MAPHEIGHT][GameConfig.MAPWIDTH];
	}

	public void setTop(BlockItem item){
		System.arraycopy(top, 0, mTopItems, 0, mTopItems.length);

		//
		for(int i=1; i < GameConfig.MAPHEIGHT; i++){
			mRightItems[i-1] = mRightItems[i];
		}
		mRightItems[GameConfig.MAPHEIGHT-1] = mTopItems[GameConfig.MAPINITIALW-1];
	}

	public BlockItem[] getTop(){
		return mTopItems;
	}

	public void setRight(BlockItem[] right){
		System.arraycopy(right, 0, mRightItems, 0, mRightItems.length);

	}

	public BlockItem[] getRight(){
		return mRightItems;
	}

	
}

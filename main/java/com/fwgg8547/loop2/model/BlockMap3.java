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

  public int setItem2EmptySpace(BlockItem item){
  }

  public BlockItem getItem(int x, int y){
    return null;
  }

  private Point getEmptyPosition() {
    return null;
  }
}

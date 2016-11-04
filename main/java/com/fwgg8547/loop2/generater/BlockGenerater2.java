package com.fwgg8547.loop2.generater;

import com.fwgg8547.loop2.gamebase.util.Lg;
import com.fwgg8547.loop2.gamebase.sequencerbase.ItemGeneraterBase;
import com.fwgg8547.loop2.gamebase.modelbase.SpriteModel;
import com.fwgg8547.loop2.model.BlockModel;
import com.fwgg8547.loop2.R;

import android.content.Context;

import java.util.*;
import com.fwgg8547.loop2.model.*;
import android.text.style.*;
import android.text.*;
import com.fwgg8547.loop2.*;
import android.graphics.PointF;

public class BlockGenerater2
extends ItemGeneraterBase
{
	private final static String TAG = BlockGenerater2.class.getSimpleName();
	private BlockModel mBlockModel;
  
  public BlockGenerater2(BlockModel m){
    super(m);
    mBlockModel = m;
    mSequenceEnd = false;
  }
  
	@Override
	public void loadSequence(Context ctx, int level){
    mSq = ResourceFileReader.getSequence(level);
    mCurrent = mSq[0];
  }

 	@Override
	public void createInitialItem()
	{
    return;
  }

 	@Override
	public void loadLevel(Context ctx){
		mLevel = ResourceFileReader.getLevelThreash();
	}
	

  public void createRequest(){
  }
}

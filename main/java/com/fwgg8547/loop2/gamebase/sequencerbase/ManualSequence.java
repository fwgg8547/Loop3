package com.fwgg8547.loop2.gamebase.sequencerbase;

import com.fwgg8547.loop2.gamebase.util.*;

public class ManualSequence
{
	public int frame;
  public AnimationFunc func;
  
	public ManualSequence(){}
	
	public ManualSequence( int frame, AnimationFunc func){
		this.frame = frame;
    this.func = func;
	}
}

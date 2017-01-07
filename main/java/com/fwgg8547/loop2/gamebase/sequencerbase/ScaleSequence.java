package com.fwgg8547.loop2.gamebase.sequencerbase;

public class ScaleSequence
{
	public int frame;
	public float scalex;
	public float scaley;

	public enum Type {
		Fixed,
		Func
	}
	
	public ScaleSequence(){}
	
	public ScaleSequence(Type type, int frame, float scalex, float scaley){
		this.frame = frame;
		this.scalex = scalex;
		this.scaley = scaley;
	}
	
	
}

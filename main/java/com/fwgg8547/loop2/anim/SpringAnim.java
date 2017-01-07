package com.fwgg8547.loop2.anim;

import com.fwgg8547.loop2.gamebase.sequencerbase.*;
import com.fwgg8547.loop2.gamebase.util.Vec2;

public class SpringAnim extends AnimationFunc {

  Vec2 mInitSpringLength;
  Vec2 mSpringLength;
  float mVelocity;
  float mMass = 5;
  float mStrength = 0.15f;
  float mDamping = 0.98f;
  
  public SpringAnim(Vec2 initSpringLength, Vec2 springLength) {
    mInitSpringLength = springLength;
    mSpringLength = springLength;
  }

  public void initialize() {
    mVelocity = 0;
  }
  
  public Vec2 doFunc() {
    float forceY = (mInitSpringLength.y -mSpringLength.y)*mStrength;
    float ay = forceY / mMass;
    mVelocity = mDamping * (mVelocity + ay);
    mSpringLength.y = mSpringLength.y + mVelocity;
    return mSpringLength;
  }
}

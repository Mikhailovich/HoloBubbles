package com.example.bubblestest;

import java.util.Random;

class Boids {
    int mTotal = 0;
    int mWidth = 0;
    int mHeight = 0;
    public static float mBoidDist = 30.0f;
    public static float mGravity = 100.0f;
    public static float mMaxBoidSpeed = 18.0f;
    Boid[] mBoidList = null;
    int mCount = 5;
    int mMaxCount = 5;
    Position mPlace = new Position();
    float mPlaceFactor = 1.0f;
    float mBoxMinX = 1000, mBoxMaxX = 0, mBoxMinY = 1000,
            mBoxMaxY = 0;
    int mStates = 4;

    public Boids(int n, int w, int h, int st) {
        mTotal = n;
        mWidth = w;
        mHeight = h;
        mStates = st;

        init();
    }

    public void init() {
        mBoidList = null;
        mBoidList = new Boid[mTotal];

        Random rand = new Random();

        for (int i = 0; i < mTotal; i++) {
            mBoidList[i] = new Boid();

            mBoidList[i].mPosition.x = (int)(mWidth * Math.random());
            mBoidList[i].mPosition.y = (int)(mHeight * Math.random());

            mBoidList[i].mVelocity.x = (int)(30 * Math.random());
            mBoidList[i].mVelocity.y = (int)(30 * Math.random());

            mBoidList[i].mSize = (rand.nextInt(150) + 10);

            mBoidList[i].mState =  10 + rand.nextInt(150);

//            while (0 == mBoidList[i].mVelocity.x)
//                mBoidList[i].mVelocity.x = (int)(mMaxBoidSpeed *
//                        Math.random()) - mMaxBoidSpeed;
//
//            while (0 == mBoidList[i].mVelocity.y)
//                mBoidList[i].mVelocity.y = (int)(mMaxBoidSpeed *
//                        Math.random()) - mMaxBoidSpeed;
        }
    }

    public void changeDimensions(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void moveToNext() {
        for (int i = 0; i < mTotal; i++) {
            Velocity v1;
            Velocity v2;
            if (mCount <= mMaxCount && mPlace != null) {
                v1 = ruleTendToPlace(i, mPlaceFactor);
                v2 = new Velocity();
            } else {
                v1 = new Velocity();
                v2 = ruleKeepSmallDistance(i, mBoidDist, mGravity);
            }

            float w1 = 1.0f;
            float w2 = 1.0f;

            //v2 = ruleKeepSmallDistance(i, mBoidDist, mGravity);
            Velocity v3 = v2;

            float w3 = 1.0f;


            //TODO Make multiplier out of values for use as setting
            mBoidList[i].mVelocity.x += (w1 * v1.x + w2 * v2.x + w3 *
                    v3.x) * 0.05;
            mBoidList[i].mVelocity.y += ((w1 * v1.y + w2 * v2.y + w3 *
                    v3.y) * 0.05);

            ruleLimitVelocity(i, mMaxBoidSpeed);

            mBoidList[i].mPosition.x += mBoidList[i].mVelocity.x;
            mBoidList[i].mPosition.y += mBoidList[i].mVelocity.y;

            ruleBoundPosition(i, (float)(0.1f * mMaxBoidSpeed * 0.1f * mMaxBoidSpeed));

            //Below is responsible for sprite cycling
            //mBoidList[i].changeState();
        }

        if (mCount <= mMaxCount)
            mCount ++;
    }

    private void getBoundingBox() {
        mBoxMinX = mWidth; mBoxMaxX = 0;
        mBoxMinY = mHeight; mBoxMaxY = 0;
        for (int i = 0; i < mTotal; i++) {
            if (mBoidList[i].mPosition.x < mBoxMinX) mBoxMinX =
                    mBoidList[i].mPosition.x / 2;
            if (mBoidList[i].mPosition.x > mBoxMaxX) mBoxMaxX =
                    mBoidList[i].mPosition.x / 2;
            if (mBoidList[i].mPosition.y < mBoxMinY) mBoxMinY =
                    mBoidList[i].mPosition.y / 2;
            if (mBoidList[i].mPosition.y > mBoxMaxY) mBoxMaxY =
                    mBoidList[i].mPosition.y / 2;
        }
    }

    // Set a target place
    public void setTargetPlace(float x, float y, float f) {
        mPlace.x = x;
        mPlace.y = y;

        // If the touched point is within the boids, disperse
        // them away.
        // If it is outside the boids, make them follow it.
        //getBoundingBox();
        mPlaceFactor = f;
        mCount  = 0;
        mMaxCount = (int)(Math.abs((mBoxMinX + mBoxMaxX) /
                2.0f - x) + Math.abs((mBoxMinY + mBoxMaxY) /
                2.0f - y));

        //        if (x >= mBoxMinX && x <= mBoxMaxX && y >= mBoxMinY
        //                && y <= mBoxMaxY) {
        //            mPlaceFactor = -f;
        //            mCount  = 0;
        //            mMaxCount = 50;
        //        } else {
        //            mPlaceFactor = f;
        //            mCount  = 0;
        //            mMaxCount = (int)(Math.abs((mBoxMinX + mBoxMaxX) /
        //                    2.0f - x) + Math.abs((mBoxMinY + mBoxMaxY) /
        //                    2.0f - y));
        //        }
    }
    public void setTargetRepulsePlace(float x, float y, float f) {
        mPlace.x = x;
        mPlace.y = y;

        // If the touched point is within the boids, disperse
        // them away.
        // If it is outside the boids, make them follow it.
        //getBoundingBox();
        mPlaceFactor = -f;
        mCount  = 0;
        mMaxCount = 1;

        //        if (x >= mBoxMinX && x <= mBoxMaxX && y >= mBoxMinY
        //                && y <= mBoxMaxY) {
        //            mPlaceFactor = -f;
        //            mCount  = 0;
        //            mMaxCount = 50;
        //        } else {
        //            mPlaceFactor = f;
        //            mCount  = 0;
        //            mMaxCount = (int)(Math.abs((mBoxMinX + mBoxMaxX) /
        //                    2.0f - x) + Math.abs((mBoxMinY + mBoxMaxY) /
        //                    2.0f - y));
        //        }
    }
    public void setTargetNone() {
        // If direction is "following", stop it.
        if (mPlaceFactor > 0) {
            mCount = mMaxCount - 10;
        }
    }

    public int getTotal() {
        return mTotal;
    }

    public Boid[] getBoids() {
        return mBoidList;
    }

    // RULE: fly to center of other boids
    private Velocity ruleFlyToCentroid(int id, float factor) {
        Velocity v = new Velocity();
        Position p = new Position();

        for (int i = 0; i < mTotal; i++) {
            if (i != id ) {
                p.x += mBoidList[i].mPosition.x;
                p.y += mBoidList[i].mPosition.y;
            }
        }

        if (mTotal > 2) {
            p.x /= (mTotal - 1);
            p.y /= (mTotal - 1);
        }

        v.x = (p.x -  mBoidList[id].mPosition.x) * factor;
        v.y = (p.y -  mBoidList[id].mPosition.y) * factor;

        return v;
    }

    // RULE: keep a small distance from other boids
    private Velocity ruleKeepSmallDistance(int id, float dist, float gravity) {
        Velocity v = new Velocity();

        for (int i = 0; i < mTotal; i++) {
            if (i != id) {
                if ((Math.abs(mBoidList[id].mPosition.x -
                        mBoidList[i].mPosition.x) +
                        Math.abs(mBoidList[id].mPosition.y -
                                mBoidList[i].mPosition.y) < (mBoidList[id].mSize * .5f)) && id != mBoidList[i].mFollowedBy) {
                    v.x -= (mBoidList[i].mPosition.x -
                            mBoidList[id].mPosition.x) * .45f;
                    v.y -= (mBoidList[i].mPosition.y -
                            mBoidList[id].mPosition.y) * .45f;
                }
                else if (Math.abs(mBoidList[id].mPosition.x -
                        mBoidList[i].mPosition.x) +
                        Math.abs(mBoidList[id].mPosition.y -
                                mBoidList[i].mPosition.y) < gravity){
                    if (mBoidList[id].mSize < mBoidList[i].mSize && mBoidList[i].mFollowedBy == 0 || mBoidList[i].mFollowedBy == id){
                        mBoidList[i].mFollowedBy = id;
                        v = ruleTendToPlaceBoid(id, i, 0.1f);
                        ruleLimitVelocity(id, mMaxBoidSpeed / 2);
                        //ruleLimitFollowVelocity(id, i);
                    }
                }

            }
        }

        return v;
    }

    // RULE: tend to a place
    private Velocity ruleTendToPlace(int id, float factor) {
        Velocity v = new Velocity();

        v.x = (mPlace.x - mBoidList[id].mPosition.x) * factor;
        v.y = (mPlace.y - mBoidList[id].mPosition.y) * factor;

        return v;
    }

    private Velocity ruleTendToPlaceBoid(int id, int targetId, float factor) {
        Velocity v = new Velocity();
        Position p = new Position();

        p.x += mBoidList[id].mPosition.x;
        p.y += mBoidList[id].mPosition.y;

        v.x = (((mBoidList[targetId].mPosition.x - mBoidList[targetId].mVelocity.x) - p.x)  - (mBoidList[targetId].mVelocity.x / 2));
        v.y = (((mBoidList[targetId].mPosition.y - mBoidList[targetId].mVelocity.y) - p.y)  - (mBoidList[targetId].mVelocity.y / 2));

        return v;
    }
    // RULE: limit the velocity
    private void ruleLimitVelocity(int id, float vmax) {

        float vv = (float)Math.sqrt(mBoidList[id].mVelocity.x *
                mBoidList[id].mVelocity.x +
                mBoidList[id].mVelocity.y * mBoidList[id].mVelocity.y) ;

        if (vv > vmax) {
            mBoidList[id].mVelocity.x = (mBoidList[id].mVelocity.x /
                    vv) * vmax;
            mBoidList[id].mVelocity.y = (mBoidList[id].mVelocity.y /
                    vv) * vmax;
        }
    }

    private void ruleLimitFollowVelocity(int id, int targetID) {

        float vv = (float)Math.sqrt(mBoidList[id].mVelocity.x *
                mBoidList[id].mVelocity.x +
                mBoidList[id].mVelocity.y * mBoidList[id].mVelocity.y) ;

        float vvTarget = (float)Math.sqrt(mBoidList[targetID].mVelocity.x *
                mBoidList[targetID].mVelocity.x +
                mBoidList[targetID].mVelocity.y * mBoidList[targetID].mVelocity.y);

        if (vv > vvTarget) {
            mBoidList[id].mVelocity.x = (mBoidList[id].mVelocity.x /
                    vv) * vvTarget;
            mBoidList[id].mVelocity.y = (mBoidList[id].mVelocity.y /
                    vv) * vvTarget;
        }
    }
    // RULE: bound the position
    private void ruleBoundPosition(int id, float initv) {
        if (mBoidList[id].mPosition.x < 1) {
            mBoidList[id].mVelocity.x = initv * 0.75f;
        } else if (mBoidList[id].mPosition.x + mBoidList[id].mSize > mWidth) {
            mBoidList[id].mVelocity.x = -initv * 0.75f;
        }

        if (mBoidList[id].mPosition.y < 1) {
            mBoidList[id].mVelocity.y = initv * 0.75f;
        } else if (mBoidList[id].mPosition.y + mBoidList[id].mSize > mHeight) {
            mBoidList[id].mVelocity.y = -initv * 0.75f;
        }
    }

}

class Boid {
    public Position mPosition = null;
    public Velocity mVelocity = null;
    public int mFollowedBy = 0;
    public int mState = 0;
    public int mSize = 0;

    public Boid() {
        mPosition = new Position();
        mVelocity = new Velocity();
    }
}

class Position {
    public float x = 0;
    public float y = 0;
}

class Velocity {
    public float x = 0;
    public float y = 0;
}
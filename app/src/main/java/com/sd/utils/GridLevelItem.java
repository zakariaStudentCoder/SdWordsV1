package com.sd.utils;

/**
 * Created by Admin on 31.03.2016.
 */
public class GridLevelItem {

    private int mId;
    private boolean mIsDone;

    public GridLevelItem(int mId, boolean mIsDone) {
        this.mId = mId;
        this.mIsDone = mIsDone;
    }

    public int getId() {
        return mId;
    }

    public boolean IsDone() {
        return mIsDone;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public void setIsDone(boolean mIsDone) {
        this.mIsDone = mIsDone;
    }
}

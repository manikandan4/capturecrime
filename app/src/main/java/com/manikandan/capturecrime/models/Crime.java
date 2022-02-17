package com.manikandan.capturecrime.models;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mID;
    private String mTitle;
    private Date mDate;
    private boolean msolved;

    public Crime() {
        mID = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getmID() {
        return mID;
    }

    public void setmID(UUID mID) {
        this.mID = mID;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean isMsolved() {
        return msolved;
    }

    public void setMsolved(boolean msolved) {
        this.msolved = msolved;
    }
}

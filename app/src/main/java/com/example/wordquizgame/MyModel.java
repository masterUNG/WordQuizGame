package com.example.wordquizgame;

/**
 * Created by masterUNG on 2/2/16 AD.
 */
public class MyModel {

    private int timesAnInt;

    public interface OnMyModelChangeListener {
        void onMyModelChangeListener(MyModel myModel);
    }   // class Interface

    private OnMyModelChangeListener objOnMyModelChangeListener;

    public void setObjOnMyModelChangeListener(OnMyModelChangeListener objOnMyModelChangeListener) {
        this.objOnMyModelChangeListener = objOnMyModelChangeListener;
    }

    public int getTimesAnInt() {
        return timesAnInt;
    }

    public void setTimesAnInt(int timesAnInt) {
        this.timesAnInt = timesAnInt;

        if (this.objOnMyModelChangeListener != null) {
            this.objOnMyModelChangeListener.onMyModelChangeListener(this);
        }   // if

    }
}   // Main Class

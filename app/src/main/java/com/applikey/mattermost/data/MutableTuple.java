package com.applikey.mattermost.data;

public class MutableTuple<X, Y> {
    private X x;
    private Y y;

    // This constructor helps my purpose: for now I will instantiate tuple having only one parameter
    public MutableTuple(X x) {
        this.x = x;
    }

    public X getX() {
        return x;
    }

    public void setX(X x) {
        this.x = x;
    }

    public Y getY() {
        return y;
    }

    public void setY(Y y) {
        this.y = y;
    }
}

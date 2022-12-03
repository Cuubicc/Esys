package org.cubic.esys;

public class PostInfo {

    private final boolean superListener;

    public PostInfo(boolean superListener){
        this.superListener = superListener;
    }

    public boolean superListener() {
        return superListener;
    }
}

package com.example.facesample.engine.imgscan;

/**
 * Created by YGX on 2018/4/26.
 */

public interface Function<Param,Returner> {

    Returner applyAs(Param p);
}

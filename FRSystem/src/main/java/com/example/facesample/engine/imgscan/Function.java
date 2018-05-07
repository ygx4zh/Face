package com.example.facesample.engine.imgscan;



public interface Function<Param,Returner> {

    Returner applyAs(Param p);
}

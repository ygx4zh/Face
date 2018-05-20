package com.example.facesample.engine.imgscan;

import java.io.File;


public interface Filter<Param, Returner> {
    Returner filte(Param p);
}

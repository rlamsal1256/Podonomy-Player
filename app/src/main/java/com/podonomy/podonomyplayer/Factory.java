package com.podonomy.podonomyplayer;

/**
 * Created by lchen on 5/12/2016.
 */

import com.podonomy.podonomyplayer.service.search.SearchTask;

/** factory for prodcution */
public class Factory<T> {
    public <T> T getInstance(Class<T> klass){
        if (klass == SearchTask.class)
            return (T) new SearchTask();
        return null;
    }
}

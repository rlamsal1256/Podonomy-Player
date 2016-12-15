package com.podonomy.podonomyplayer.UITest.SubscriptionsActivityTest;

/**
 * Created by lchen on 5/12/2016.
 */

import com.podonomy.podonomyplayer.Factory;
import com.podonomy.podonomyplayer.service.search.SearchTask;

/** factory for teh test framework */
public class TestFactory <T> extends Factory<T> {
    @Override
    public <T> T getInstance(Class<T> klass) {
        if (klass == SearchTask.class)
            return (T) new SearchTaskTest();
        return null;
    }
}


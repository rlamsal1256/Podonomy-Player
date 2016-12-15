package com.podonomy.podonomyplayer.event;

/**
 * Created by rlamsal on 7/25/2016.
 */
public class SpeedRateIncreasedEvent extends UserEventBase {

    private float speedRate;

    public SpeedRateIncreasedEvent(float speedRate) {
        this.speedRate = speedRate;
    }

    public float getSpeedRate() {
        return speedRate;
    }
}


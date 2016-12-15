package com.podonomy.podonomyplayer.event;

public class IncreasePlaySpeedEvent extends UserEventBase {

    private float speedRate;

    public IncreasePlaySpeedEvent(float speedRate) {
        this.speedRate = speedRate;
    }

    public float getSpeedRate() {
        return speedRate;
    }
}

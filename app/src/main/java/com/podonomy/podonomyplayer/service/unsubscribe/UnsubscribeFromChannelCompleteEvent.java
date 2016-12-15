package com.podonomy.podonomyplayer.service.unsubscribe;

import com.podonomy.podonomyplayer.event.EventBase;

/**
 * Created by rlamsal on 6/16/2016.
 */
public class UnsubscribeFromChannelCompleteEvent extends EventBase {
    protected long originalUnsubscribeEventID = 0L;

    public UnsubscribeFromChannelCompleteEvent(){
        setName("UnsubscribeFromChannelCompleteEvent");
    }

    public long getOriginalSearchEventID() {
        return originalUnsubscribeEventID;
    }
    public void setOriginalSearchEventID(long originalUnsubscribeEventID) {
        this.originalUnsubscribeEventID = originalUnsubscribeEventID;
    }

}

package com.podonomy.podonomyplayer.service.search;

import com.podonomy.podonomyplayer.dao.SearchQuery;
import com.podonomy.podonomyplayer.event.EventBase;

public class SearchCompleteEvent extends EventBase{
  protected long originalSearchEventID = 0L;
  protected SearchQuery query = null;

  public SearchCompleteEvent(){
    setName("SearchCompleteEvent");
  }

  public long getOriginalSearchEventID() {
    return originalSearchEventID;
  }
  public void setOriginalSearchEventID(long originalSearchEventID) {
    this.originalSearchEventID = originalSearchEventID;
  }
  public SearchQuery getQuery() {
    return query;
  }
  public void setQuery(SearchQuery query) {
    this.query = query;
  }
}

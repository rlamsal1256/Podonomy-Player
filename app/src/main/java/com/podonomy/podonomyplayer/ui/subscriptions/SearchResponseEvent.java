package com.podonomy.podonomyplayer.ui.subscriptions;

import com.podonomy.podonomyplayer.event.EventBase;
import com.podonomy.podonomyplayer.dao.SearchQuery;

/**
 * This event will be generated once the search is complete. The query contains the original query
 * criteria and the results.
 */
public class SearchResponseEvent extends EventBase{
  protected SearchQuery query;

  public SearchResponseEvent() {}
  public SearchResponseEvent(SearchQuery query) {
    this();
    this.query = query;
  }
  public SearchQuery getQuery() {
    return query;
  }
  public void setQuery(SearchQuery query) {
    this.query = query;
  }
}

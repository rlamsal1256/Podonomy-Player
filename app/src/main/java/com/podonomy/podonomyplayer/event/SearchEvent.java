package com.podonomy.podonomyplayer.event;

import com.podonomy.podonomyplayer.dao.SearchQuery;
import com.podonomy.podonomyplayer.event.UserEventBase;

/**
 * Represents a search event, the user searching for podcasts/channels.
 */
public class SearchEvent extends UserEventBase {
  protected SearchQuery query = null;

  public SearchEvent(){
    setName("SearchEvent");
  }
  public SearchEvent(SearchQuery query){
    this();
    this.query = query;
  }
  @Override
  public Object[] getArguments() {
    if (query == null)
      query = new SearchQuery();

    return new Object[]{
      "keywords:", query.getKeywords(),
      "audio:", query.getAudio(),
      "video:", query.getVideo(),
      "nbWeeks:", query.getNumberWeeks(),
      "language:", query.getLanguage(),
      "local:", query.getLocal()
    };
  }
}

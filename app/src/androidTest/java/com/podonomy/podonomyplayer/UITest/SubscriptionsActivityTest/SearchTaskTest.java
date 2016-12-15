package com.podonomy.podonomyplayer.UITest.SubscriptionsActivityTest;

import com.podonomy.podonomyplayer.dao.Category;
import com.podonomy.podonomyplayer.dao.CategoryDAO;
import com.podonomy.podonomyplayer.dao.Channel;
import com.podonomy.podonomyplayer.dao.ChannelDAO;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.dao.Keyword;
import com.podonomy.podonomyplayer.dao.KeywordDAO;
import com.podonomy.podonomyplayer.dao.SearchQuery;
import com.podonomy.podonomyplayer.dao.Subscriber;
import com.podonomy.podonomyplayer.dao.SubscriberDAO;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.feedparser.Feed;
import com.podonomy.podonomyplayer.feedparser.FeedParser;
import com.podonomy.podonomyplayer.service.HttpClient;
import com.podonomy.podonomyplayer.service.cache.DownloadAssetEvent;
import com.podonomy.podonomyplayer.service.search.SearchCompleteEvent;
import com.podonomy.podonomyplayer.service.search.SearchTask;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lchen on 5/10/2016.
 */
public class SearchTaskTest extends SearchTask{
    private final String TEST_RESPONSE_STRING = "{\"results\":[{\"id\":\"1637633\",\"iTunesId\":null,\"name\":\"KPFA - Guns and Butter\",\"url\":\"https:\\/\\/kpfa.org\\/program\\/guns-and-butter\\/feed\\/\",\"author\":\"Guns and Butter\",\"thumbnail\":\"https:\\/\\/kpfa.org\\/wp-content\\/themes\\/kpfa-v2\\/dist\\/images\\/itunesbanner.jpg\",\"description\":\"A program that investigates the relationships among capitalism, militarism and politics. Maintaining a radical perspective in the aftermath of the September 11th attacks, \\u201cGuns & Butter: The Economics of Politics\\u201d reports on who wins and who loses when the economic resources of civil society are diverted toward global corporatization, war, and the furtherance of a national security state. Produced and hosted by Bonnie Faulkner. Visit Guns and Butter online at: http:\\/\\/www.gunsandbutter.org Follow Guns and Butter on Twitter at: https:\\/\\/twitter.com\\/gandbradio Listen to Guns and Butter on Sound Cloud at: https:\\/\\/soundcloud.com\\/guns-and-butter-1\",\"language\":\"en\",\"type\":\"AUDIO\",\"keywords\":\"\",\"lastpublicationdate\":\"1461157200000\",\"episodeNb\":\"15\",\"categories\":[{\"name_en\":\"News & Politics\",\"name_fr\":\"Actualit\\u00e9s et politique\"}]},{\"id\":\"498188\",\"iTunesId\":null,\"name\":\"Guns of Hollywood\",\"url\":\"http:\\/\\/feeds.feedburner.com\\/GunsOfHollywood\",\"author\":\"Firearms Radio Network\",\"thumbnail\":\"http:\\/\\/www.firearmsradio.net\\/wp-content\\/uploads\\/powerpress\\/GOH_itunes.jpg\",\"description\":\"Guns Of Hollywood talks about just that, guns in films and tv. From your favorite western to that run and gun action film you can't stop watching, we look at all the guns that make them go \\\"bang\\\". We strive to bring to the show a diverse knowledge base, variety of interests and special guests as we examine both the firearms and films that we all love so much.\",\"language\":\"en\",\"type\":\"AUDIO\",\"keywords\":\"\",\"lastpublicationdate\":\"1461293164000\",\"episodeNb\":\"102\",\"categories\":[{\"name_en\":\"Sports & Recreation\\/Outdoor\",\"name_fr\":\"Sports et loisirs\\/Plein air\"},{\"name_en\":\"TV & Film\",\"name_fr\":\"T\\u00e9l\\u00e9vision et cin\\u00e9ma\"}]},{\"id\":\"426384\",\"iTunesId\":null,\"name\":\"Kate Krueger's Talking Guns\",\"url\":\"http:\\/\\/katekruegertalkingguns.com\\/blog\\/?feed=podcast\",\"author\":\"Kate Krueger\",\"thumbnail\":\"http:\\/\\/katekruegertalkingguns.com\\/blog\\/wp-content\\/uploads\\/2012\\/11\\/Kate_Glock-300x225.jpg\",\"description\":\"Kate Krueger is the top female gun radio expert and hosts Talking Guns on Sundays at Noon \\u2013 2PM on 1100 AM KFNX Independent Talk Radio. Named \\u201cAnnie Oakley of the airwaves\\u201d by one of her guests she addresses all things related to firearms including products, books, politics, shooting sports & events, training, hunting and more. She also talks about survival and preparedness, accessories, and other peripheral topics related to the outdoors and the 2nd amendment. Kate is a patriot, mom, 2nd Amendment advocate and owner of a local Arizona gun store. She is also an instructor and shooter and has been engaged in the shooting sports for over 30 years. Kate has a strong firearms and martial arts background. Although she was not brought up around firearms her fighting spirit eventually brought her to that next logical step. Her thirst for knowledge has led her to training facilities around the country, Smith & Wesson, Lethal Force Institute (now Massad Ayoob Group), Chapman Academy, Gunsite to name a few. Isshin Ryu was her style of choice in martial arts but she has continued to add to that background with ground fighting, hand to hand, weapon retention and other varients of weapon and empty hand techniques. Through martial arts and shooting competitions over the years she honed her skills.\",\"language\":\"en\",\"type\":\"AUDIO\",\"keywords\":\"\",\"lastpublicationdate\":\"1409538467000\",\"episodeNb\":\"10\",\"categories\":[{\"name_en\":\"News & Politics\",\"name_fr\":\"Actualit\\u00e9s et politique\"}]}]}";

    @Override
    protected String fetchQueryResult(SearchQuery q) throws IOException {
        return TEST_RESPONSE_STRING;
    }
}

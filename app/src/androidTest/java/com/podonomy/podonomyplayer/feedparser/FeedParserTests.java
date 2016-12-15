package com.podonomy.podonomyplayer.feedparser;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Francois on 4/14/2016.
 */
@RunWith(AndroidJUnit4.class)
public class FeedParserTests {
  @Test
  public void testGunsOfHollywood(){
    Feed feed = FeedParser.parse(new ByteArrayInputStream(DataFiles.guns_of_hollywood.getBytes()));
    assertNotNull(feed);

    /////
    // Make sure that we parsed the correct data.
    assertEquals("Guns of Hollywood", feed.getTitle());
    assertEquals("http://www.firearmsradio.net/category/guns-of-hollywood/", feed.getLink());
    assertTrue(feed.getDescription().contains("We talk about guns in films and tv, from your favorite western to"));
    assertNull(feed.getPubDate());
    assertEquals("en-US", feed.getLanguage());
    assertTrue(feed.getSubtitle().startsWith("Guns Of Hollywood talks about just that, guns in films and tv"));
    assertEquals(100, feed.getFeedList().size());

    FeedItem item = feed.getFeedList().get(0);
    assertEquals("Guns of Hollywood 100 – Silverado", item.getTitle());
    assertEquals("http://www.firearmsradio.net/guns-of-hollywood-100-silverado/", item.getLink());
    assertEquals("http://traffic.libsyn.com/gunguyradio/GOH_100_Silverado.mp3", item.getMediaLink());
    assertEquals("Guns of Hollywood looks at the western gunling in Silverado (1985). And For this our 100th show we’re joined by Jake Challand, the president of the Firearms Radio Network.", item.getDescription());
    assertEquals("http://www.firearmsradio.net/wp-content/uploads/powerpress/GOH_itunes.jpg", item.getImageURL());
    assertEquals("1:00:44", item.getDuration());

    Calendar referenceDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    referenceDate.set(2016, Calendar.APRIL, 9, 1, 50, 17);
    referenceDate.set(Calendar.MILLISECOND, 0);
    Calendar itemDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    itemDate.setTimeInMillis((item.getDate().getTime() / 1000) * 1000);
    itemDate.set(Calendar.MILLISECOND, 0);
    assertEquals(referenceDate.getTime(), itemDate.getTime());
  }

  @Test
  public void testKPFA() {
    Feed feed = FeedParser.parse(new ByteArrayInputStream(DataFiles.kpfa.getBytes()));
    assertNotNull(feed);
    /////
    // Make sure that we parsed the correct data.
    assertEquals("KPFA - Guns and Butter", feed.getTitle());
    assertEquals("https://kpfa.org/program/guns-and-butter/", feed.getLink());
    assertTrue(feed.getDescription().contains("A program that investigates the relationships among capitalism, militarism and politics"));
    assertNull(feed.getPubDate());
    assertEquals("en-us", feed.getLanguage().toLowerCase());
    assertNull(feed.getSubtitle());
    assertEquals(15, feed.getFeedList().size());

    FeedItem item = feed.getFeedList().get(0);
    assertEquals("Guns and Butter - April 13, 2016", item.getTitle());

    assertEquals("http://archives.kpfa.org/data/20160413-Wed1300.mp3", item.getMediaLink());
    assertEquals("audio/mpeg", item.getMediaType());
    assertEquals("223", item.getMediaSize());
    assertTrue(item.getDescription().startsWith("A program that investigates the relationships among capitalism, militarism"));
    assertEquals("https://kpfa.org/wp-content/themes/kpfa-v2/library/images/itunesbanner.jpg", item.getImageURL());
    assertEquals("0:00", item.getDuration());

    Calendar referenceDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    referenceDate.set(2016, Calendar.APRIL, 13, 20, 00, 00);
    referenceDate.set(Calendar.MILLISECOND, 0);
    Calendar itemDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    itemDate.setTimeInMillis((item.getDate().getTime() / 1000) * 1000);
    itemDate.set(Calendar.MILLISECOND, 0);
    assertEquals(referenceDate.getTime(), itemDate.getTime());
  }

  @Test
  public void testMathDude() {
    Feed feed = FeedParser.parse(new ByteArrayInputStream(DataFiles.math_dude.getBytes()));
    assertNotNull(feed);
    /////
    // Make sure that we parsed the correct data.
    assertEquals("The Math Dude Quick and Dirty Tips to Make Math Easier", feed.getTitle());
    assertEquals("http://www.quickanddirtytips.com/math-dude", feed.getLink());
    assertEquals("en-us", feed.getLanguage());
    assertEquals("Quick and Dirty Tips to Make Math Easier", feed.getSubtitle());
    assertEquals("QuickAndDirtyTips.com", feed.getAuthor());
    assertTrue(feed.getDescription().startsWith("The Math Dude makes understanding math easier and more fun than"));
    assertEquals("Macmillan Holdings, LLC", feed.getOwnerName());
    assertEquals("contact@quickanddirtytips.com", feed.getOwnerEmail());
    assertEquals("http://www.quickanddirtytips.com/sites/default/files/avataritunes/14/MathDude_pod.jpg", feed.getImage().getUrl());
    //assertTrue(feed.getCategories().contains("K-12"));
  }

  @Test
  public void testScienceFriday() {
    Feed feed = FeedParser.parse(new ByteArrayInputStream(DataFiles.science_friday.getBytes()));
    assertNotNull(feed);
    /////
    // Make sure that we parsed the correct data.
    assertEquals("Science Friday", feed.getTitle());
    assertEquals("http://www.sciencefriday.com/", feed.getLink());
    assertTrue(feed.getDescription().startsWith("Covering everything about science and technology"));
    assertEquals("en-US", feed.getLanguage());
    assertEquals("It's brain fun, for curious people", feed.getSubtitle());
    assertEquals("Science Friday", feed.getAuthor());
    assertEquals("Science Friday", feed.getOwnerName());
    assertEquals("scifri@sciencefriday.com", feed.getOwnerEmail());
    assertEquals("clean", feed.getAdvisory());
    assertEquals("http://live-sciencefriday.pantheon.io/wp-content/uploads/2015/10/SciFri_avatar_1400x.png", feed.getImage().getUrl());
    assertTrue(feed.getCategories().contains("Science & Medicine"));

    FeedItem item = feed.getFeedList().get(0);
    assertEquals("Hr1: News Roundup, Climate and the Collapse of Ancient Civilizations, Climate Fiction", item.getTitle());
    assertEquals("http://www.sciencefriday.com/podcast/hr1-news-roundup-climate-and-the-collapse-of-ancient-civilizations-climate-fiction/", item.getLink());
    assertTrue(item.getDescription().startsWith("Writer Paolo Bacigalupi is using fiction to"));
    assertTrue(item.getSubtitle().startsWith("Writer Paolo Bacigalupi is using fiction to"));
    assertEquals("00:46:34", item.getDuration());
    assertEquals("Science Friday", item.getAuthor());

    Calendar referenceDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    referenceDate.set(2016, Calendar.APRIL, 8, 19, 00, 00);
    referenceDate.set(Calendar.MILLISECOND, 0);
    Calendar itemDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    itemDate.setTimeInMillis((item.getDate().getTime() / 1000) * 1000);
    itemDate.set(Calendar.MILLISECOND, 0);
    assertEquals(referenceDate.getTime(), itemDate.getTime());
  }

  @Test
  public void test60SecondsCivic(){
    Feed feed = FeedParser.parse(new ByteArrayInputStream(DataFiles.sixty_seconds_civic.getBytes()));
    assertNotNull(feed);
    /////
    // Make sure that we parsed the correct data.
    assertEquals("60-Second Civics Podcast", feed.getTitle());
    assertEquals("Center for Civic Education", feed.getAuthor());
    assertEquals("http://www.civiced.org/60-second-civics", feed.getLink());
    assertEquals("en-us",feed.getLanguage());
    assertTrue(feed.getDescription().startsWith("60-Second Civics is a daily podcast that provides a quick "));
    assertEquals("http://www.civiced.org/images/logos/0906logo60.png", feed.getImage().getUrl());

    FeedItem item = feed.getFeedList().elementAt(0);
    assertEquals("60-Second Civics: Episode 2510, Civic virtue cannot be relied upon", item.getTitle());
    assertEquals("http://gresyn0.100webspace.net/podcasts/60SecondCivics/60SecondCivics-Episode2510.mp3", item.getMediaLink());
  }

}

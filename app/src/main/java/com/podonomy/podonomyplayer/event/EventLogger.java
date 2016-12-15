package com.podonomy.podonomyplayer.event;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.dao.Configuration;
import com.podonomy.podonomyplayer.dao.ConfigurationDAO;
import com.podonomy.podonomyplayer.dao.DAO;

import org.greenrobot.eventbus.Subscribe;


/**
 Event log format:
 INFO |date|time|thread id|source/device|version|user id|event name|arguments....\n
 ERROR|date|time|thread id|source/device|version|error type|message              \n
 DEBUG|date|time|thread id|source/device|version|message                         \n
 TRACE|date|time|thread id|source/device|version|message                         \n

 Note that we don't actually use pipes (|) but tabs (\t) instead.
 */

/**
 * Event logger which stores the events into a text file to be uploaded later to the server.  It is
 * based on Logback and it thread safe.  It will in fact create 1 file per thread so they don't
 * interfere with each other.
 */
public class EventLogger {
  private final String TAG = ">>EventLogger<<";
  private final int EVENT_LOGGER_PRIORITY = 500;
  private final int VERSION            = 1;
  private Logger defaultLogger;
  private static final String SEPERATOR_s = "\t";
  private static final char SEPERATOR_c = '\t';
  private static final char TEMPSEPERATOR = '\01';
  private static volatile EventLogger loggerSingleton = null;

  /**
   * Returns a new logger with set with the given configuration.  The logger is a singleton so once
   * this method has been called once, it will no longer create a new logger but return the singleton
   * instead.
   */
  public static EventLogger getLogger(Configuration config) {
    if (loggerSingleton == null){
      synchronized (EventLogger.class) {
        if (loggerSingleton == null)
           loggerSingleton = new EventLogger(config);
      }
    }
    return loggerSingleton;
  }

  /**
   * Utility method which returns the logger singleton in use. See {@link EventLogger#getLogger(Configuration)} for more details.
   */
  public static EventLogger getLogger(){
    if (loggerSingleton == null){
      Configuration config = ConfigurationDAO.getConfig(DAO.getRealm(PlayerApplication.getInstance().getAppContext()));
      return getLogger(config);
    }
    return loggerSingleton;
  }

  private EventLogger(Configuration config) {
    LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
    lc.reset();

    /////
    // The event file is created using the process ID and thread id.  this way the log file shoudl not
    // be contended for if there are many thread/processes running simultaneously.
    //int pid = Process.myPid();
    //long threadID = Thread.currentThread().getId();
    //String filename = String.format("%s-%d-%d.log", config.getEventLogFileName(), pid, threadID );
    String filename = String.format("%s.log", config.getEventLogFileName());

    RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
    fileAppender.setContext(lc);
    fileAppender.setFile(PlayerApplication.getInstance().getAppContext().getFilesDir() + "/" + filename);

    TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
    rollingPolicy.setFileNamePattern(filename + "-%d{yyyy-MM-dd}.gz");
    rollingPolicy.setMaxHistory(3650);
    rollingPolicy.setContext(lc);
    rollingPolicy.setParent(fileAppender);
    rollingPolicy.start();

    PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
    encoder1.setContext(lc);
    encoder1.setPattern("%level" + SEPERATOR_s + "%date{yyyyMMdd}" + SEPERATOR_s + "%date{HH:mm:ss.SSS}" + SEPERATOR_s + "%thread" + SEPERATOR_s + "%message %n");
    encoder1.start();

    fileAppender.setEncoder(encoder1);
    fileAppender.setRollingPolicy(rollingPolicy);
    fileAppender.start();

    defaultLogger = (Logger) LoggerFactory.getLogger("EventLogger");
    defaultLogger.addAppender(fileAppender);
    defaultLogger.setLevel(Level.INFO);
    Bus.registerForEvents(this);
  }

  /**
   * Store the given event to our log file.
   */
  @Subscribe(priority = EVENT_LOGGER_PRIORITY, threadMode = ThreadMode.POSTING)
  public void Log(UserEvent event) {
    StringBuilder msg = new StringBuilder();

    if (event == null || event.getName() == null)
      throw new Error("Events must have a name.");

    msg.append(PlayerApplication.getInstance().getDeviceID());
    msg.append(TEMPSEPERATOR);
    msg.append(VERSION);
    msg.append(TEMPSEPERATOR);
    if (event.getUserID() != null)
      msg.append(event.getUserID());
    msg.append(TEMPSEPERATOR);
    msg.append(event.getName());
    msg.append(TEMPSEPERATOR);
    if (event.getArguments() != null) {
      for (Object s : event.getArguments()) {
        if (s != null)
          msg.append(s.toString()).append(TEMPSEPERATOR);
      }
    }
    removeUnwantedCharacters(msg);

    if (PlayerApplication.getInstance().isDebug()) //also send the log to the Android log file.
      android.util.Log.d(TAG, msg.toString());

    defaultLogger.info(msg.toString());
  }

  /**
   * clean up the string removing all carriage returns and other characters clashing with our format.
   */
  private void removeUnwantedCharacters(StringBuilder msg) {
    if (msg == null)
      return;
    //remove any carriage returns
    for (int i = 0; i < msg.length(); i++) {
      char c = msg.charAt(i);
      switch (c) {
        case '\r':
        case '\n':
        case SEPERATOR_c:
          msg.replace(i, i + 1, " ");
          break;
        case TEMPSEPERATOR:
          msg.replace(i, i + 1, SEPERATOR_s);
      }
    }
  }

  /**
   * Sends the given exception and message to the event log marked as an ERROR
   */
  public void error(String reporter, Throwable exception, Object... message) {
    log('E', PlayerApplication.getInstance().getDeviceID(), "Exception", exception, reporter, message);
  }

  /**
   * Sends the given message to the event log marked as an ERROR
   */
  public void error(String reporter, Object ... message) {
    log('E', PlayerApplication.getInstance().getDeviceID(), "Err Msg", null, reporter, message);
  }

  /**
   * Sends the given message to the event log marked as an DEBUG
   */
  public void debug(String reporter, Object...  message) {
    log('D', PlayerApplication.getInstance().getDeviceID(), null, null, reporter, message);
  }

  /**
   * Sends the given exception and message to the event log marked as an DEBUG
   */
  public void debug(String reporter, Throwable exception, Object... message) {
    log('D', PlayerApplication.getInstance().getDeviceID(), "Exception", exception, reporter, message);
  }

  /**
   * Sends the given message to the event log marked as an TRACE
   */
  public void trace(String reporter, String message) {
    log('T', PlayerApplication.getInstance().getDeviceID(), null, null, reporter, message);
  }

  private void log(char method,String deviceID, String msgType, Throwable exception, String reporter, Object ... message){
    StringBuilder msg = new StringBuilder();
    msg.append(deviceID == null ? "null" : deviceID);
    msg.append(TEMPSEPERATOR);
    msg.append(VERSION);
    msg.append(TEMPSEPERATOR);
    msg.append(msgType);
    msg.append(TEMPSEPERATOR);

    if (reporter != null)
      msg.append(reporter).append(' ');

    if (message != null) {
      for (Object o : message) {
        if (o != null)
          msg.append(o.toString());
      }
    }

    if (exception != null) {
      msg.append(' ');
      CharArrayWriter stream = new CharArrayWriter();
      exception.printStackTrace(new PrintWriter(stream));
      msg.append(stream.toCharArray());
      stream.close();
    }

    if (PlayerApplication.getInstance().isDebug()) //also send the log to the Android log file.
      android.util.Log.d(TAG, msg.toString());

    removeUnwantedCharacters(msg);
    switch (method){
      case 'D':
        defaultLogger.debug(msg.toString());
        break;
      case 'T':
        defaultLogger.trace(msg.toString());
        break;
      default:
        defaultLogger.error(msg.toString());
        break;
    }
  }
}
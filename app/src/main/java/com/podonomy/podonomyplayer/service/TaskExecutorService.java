package com.podonomy.podonomyplayer.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.CancelEvent;
import com.podonomy.podonomyplayer.event.DownloadChannelEpisodesEvent;
import com.podonomy.podonomyplayer.event.DownloadEpisodeMediaCompleteEvent;
import com.podonomy.podonomyplayer.event.Event;
import com.podonomy.podonomyplayer.event.EventLogger;
import com.podonomy.podonomyplayer.event.SearchEvent;
import com.podonomy.podonomyplayer.event.SubscribeToChannelEvent;
import com.podonomy.podonomyplayer.event.UnsubscribeFromChannelEvent;
import com.podonomy.podonomyplayer.service.cache.DownloadAssetEvent;
import com.podonomy.podonomyplayer.service.cache.DownloadAssetTask;
import com.podonomy.podonomyplayer.service.downloader.DownloadChannelEpisodesTask;
import com.podonomy.podonomyplayer.service.search.SearchTask;
import com.podonomy.podonomyplayer.service.unsubscribe.UnsubscribeFromChannelTask;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This is the gateway to our services.  You access a service by posting the proper event.  This
 * dispatcher will received the event and send it to the appropriate task.
 * The purpose of this class is to manage the thread that are spawned so that
 *  1) not too many threads are spawned.
 *  2) a spawned thread can be cancelled. That is, you can post an @{Event event} and later post a
 *     cancellation (@{CancelEvent}) for that event thus telling the {@link TaskExecutorService}
 *     that whatever was spawned for that event ought to be terminated.
 */
public class TaskExecutorService extends ServiceBase {
  private static final String TAG = "TaskExecutorService";
  private static final int MAX_NB_THREADS = 10;
  private ExecutorService taskExecutor = null;
  private Hashtable<Long, Future> runningTasks = new Hashtable<>();

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Subscribe(sticky = true)
  public void onSearch(SearchEvent event){
    //SearchTask search = new SearchTask();
    SearchTask search = (SearchTask) PlayerApplication.getFactory().getInstance(SearchTask.class);
    runTask(event, search);
  }

  @Subscribe(sticky = true)
  public void onCancel(CancelEvent event){
    Future task = runningTasks.get(event.getEventToCancel());
    if (task != null) {
      Bus.consume(event);
      runningTasks.remove(event.getEventToCancel());
      task.cancel(true);
      EventLogger.getLogger().debug("Event ", event.getEventID(), " cancelled.");
    }
  }

  @Subscribe(sticky = true)
  public void onDownloadAsset(DownloadAssetEvent event){
    DownloadAssetTask task = new DownloadAssetTask(event);
    runTask(event, task);
  }

  /**
   * This method handles the subscription to a channel.
   * @param event
   */
  @Subscribe(sticky = true, threadMode = ThreadMode.POSTING)
  public void onSubscribeToChannel(SubscribeToChannelEvent event){
    SubscribeToChannelTask task = new SubscribeToChannelTask();
    runTask(event, task);
  }

  @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
  public void onUnSubscribeFromChannel(UnsubscribeFromChannelEvent event){
    UnsubscribeFromChannelTask task = new UnsubscribeFromChannelTask();
    runTask(event, task);
  }

  @Subscribe(sticky = true)
  public void onDownloadChannelEpisodesEvent(DownloadChannelEpisodesEvent event){
    DownloadChannelEpisodesTask task = new DownloadChannelEpisodesTask();
    runTask(event, task);
  }

  @Subscribe(threadMode = ThreadMode.POSTING)
  public void onDownloadMediaComplete(DownloadEpisodeMediaCompleteEvent evant) {
    AddToDownloadedPlaylistTask task = new AddToDownloadedPlaylistTask();
    runTask(evant, task, false);


  }

  @Override
  public void onCreate() {
    super.onCreate();
    taskExecutor = Executors.newFixedThreadPool(MAX_NB_THREADS);
    Bus.registerForEvents(this);
  }

  @Override
  public void onDestroy() {
    Bus.unregister(this);
    taskExecutor.shutdownNow();
    super.onDestroy();
  }

  /**
   * Puts the given task on the queue to be run. Also "link" the task with the event (eventID)
   * so that the task (for that event) can be cancelled.
   */
  private void runTask(Event event, TaskBase task){
    runTask(event,task, true);
  }
  private void runTask(Event event, TaskBase task, boolean consumeEvent){
    if (consumeEvent)
      Bus.consume(event);
    task.setExecutorService(this.taskExecutor);
    task.setEvent(event);
    task.setContext(getBaseContext());
    EventLogger.getLogger().debug(TAG, ">>>>>", "submiting task begin" );
    Future f = taskExecutor.submit(task);
    EventLogger.getLogger().debug(TAG, ">>>>>", "submiting task done" );
    EventLogger.getLogger().debug(TAG, "Running task ", task.getClass().getSimpleName(),
                                  " for event ", event.getClass().getSimpleName());
    EventLogger.getLogger().debug(TAG, ">>>>>", "submiting event begin" );
    runningTasks.put(event.getEventID(), f);
    EventLogger.getLogger().debug(TAG, ">>>>>", "submiting event done" );
  }
}

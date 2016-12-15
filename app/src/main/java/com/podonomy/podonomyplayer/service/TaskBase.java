package com.podonomy.podonomyplayer.service;

import android.content.Context;

import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.event.Event;
import com.podonomy.podonomyplayer.event.EventLogger;
import java.util.concurrent.ExecutorService;

import io.realm.Realm;

public abstract class TaskBase<E extends Event> implements Runnable{
  protected ExecutorService executorService = null;
  protected EventLogger     logger;
  protected E               event;
  protected Realm           realm;
  protected Context         context;

  protected TaskBase(){
    this.logger = EventLogger.getLogger();
  }

  void setExecutorService(ExecutorService executorService) {
    this.executorService = executorService;
  }
  void setEvent(E e){
    this.event = e;
  }
  void setContext(Context c){ context = c;}

  public abstract void _run();

  @Override
  public void run() {
    realm = DAO.getRealm(PlayerApplication.getInstance().getAppContext());
    try{
      this._run();
    }
    catch(Exception e){
      logger.error("TaskBase", e, "Uncaught exception.");
      throw e;
    }
    finally {
      DAO.returnRealm(realm);
      realm  = null;
    }
  }
}

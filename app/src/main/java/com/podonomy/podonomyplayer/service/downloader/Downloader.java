package com.podonomy.podonomyplayer.service.downloader;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.podonomy.podonomyplayer.PlayerApplication;
import com.podonomy.podonomyplayer.Utils;
import com.podonomy.podonomyplayer.dao.DAO;
import com.podonomy.podonomyplayer.dao.Episode;
import com.podonomy.podonomyplayer.dao.EpisodeDAO;
import com.podonomy.podonomyplayer.dao.Subscriber;
import com.podonomy.podonomyplayer.dao.SubscriberDAO;
import com.podonomy.podonomyplayer.event.Bus;
import com.podonomy.podonomyplayer.event.CancelEvent;
import com.podonomy.podonomyplayer.event.DownloadEpisodeMediaCancelEvent;
import com.podonomy.podonomyplayer.event.DownloadEpisodeMediaCompleteEvent;
import com.podonomy.podonomyplayer.event.DownloadEpisodeMediaEvent;
import com.podonomy.podonomyplayer.event.DownloadEpisodeMediaProgressEvent;
import com.podonomy.podonomyplayer.event.EventLogger;
import com.podonomy.podonomyplayer.service.ServiceBase;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * This class is responsible for downloading the media file associated with an
 * episode.  That is, download the mpg/mp3.... files and save them to the local
 * file system.
 */
public class Downloader extends ServiceBase implements DownloadStatusListenerV1 {
    private static final String TAG = "Downloader";
    private static final String INCOMPLETE_FLAG = "incomplete-";
    //todo: DOWNLOAD_FOLDER_NAME  should be a configration parameter which the user can change.
    private static final String DOWNLOAD_FOLDER_NAME = "episodes";

    private List<CurrentDownload> currentDownloads = new ArrayList<CurrentDownload>();



    public DownloadEpisodeMediaProgressEvent downloadEpisodeMediaProgressEvent = null;

    @Nullable
    private static File downloadFolder = null;
    @Nullable
    private static ThinDownloadManager downloadManager = null;



    @Nullable
    public static ThinDownloadManager getDownloadManager() {
        return downloadManager;
    }

    /**
     * Static initializer.
     */
    public static void onStart() {
        if (downloadManager == null) {
            downloadManager = new ThinDownloadManager();
        }

        ///////
        //Initialize our download folder location.
        Context context = PlayerApplication.getInstance().getAppContext();
        downloadFolder = new File(context.getFilesDir(), DOWNLOAD_FOLDER_NAME);
        if (!downloadFolder.exists()) {
            EventLogger.getLogger().debug("Creating download folder ", downloadFolder.toString());
            if (downloadFolder.mkdirs()) {
                EventLogger.getLogger().error(TAG, "Failed to create download folder ", downloadFolder.toString());
            }
        }
    }

    /**
     * Returns the download folder where media files are stored.
     */
    public static File getDownloadFolder() {

        return downloadFolder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Bus.registerForEvents(this);

    }

    @Override
    public void onDestroy() {
        Bus.unregister(this);
        super.onDestroy();

    }

    public static void onStop() {
        if (downloadManager != null) {
            downloadManager.release();
            downloadManager = null;
        }
    }

    /**
     * This methods gets invoked when the {@link DownloadEpisodeMediaEvent} is raised (ie. an episode
     * file needs to be downloaded.
     */
    @Subscribe
    public void onDonwnloadEpisodeMediaEvent(DownloadEpisodeMediaEvent de) {


        Bus.consume(de);
        if (de == null || de.getEpisodeID() == null) {
            EventLogger.getLogger().error(TAG, "No episode ID specified to download...");
            return;
        }
        if (downloadManager == null) {
            EventLogger.getLogger().error(TAG, "downloadManager does not appear to having been initialized.");
            return;
        }

        Episode episode = EpisodeDAO.find(realm, de.getEpisodeID());
        if (episode == null || isBlank(episode.getMediaFileURL()))
            return;

        try {
            Uri url = Uri.parse(episode.getMediaFileURL());
            String fileName = getDownloadFileName(INCOMPLETE_FLAG, de.getEventID());
            DownloadRequest request = new DownloadRequest(url)
                    .setRetryPolicy(new DefaultRetryPolicy())
                    .setDestinationURI(Uri.parse(fileName))
                    .setStatusListener(this);

            CurrentDownload currentDownload = new CurrentDownload();
            currentDownload.episode = episode;
            currentDownload.event = de;
            currentDownload.request = request;

            currentDownloads.add(currentDownload);

            Subscriber subscriber = SubscriberDAO.getCurrentSubscriber(realm);

            DAO.beginTransaction(realm);
            subscriber.getCurrentDownloadEpisodes().add(currentDownload.episode);
            DAO.commitTransaction(realm);

            if (downloadManager.query(currentDownload.downloadId) == DownloadManager.STATUS_NOT_FOUND) {
                currentDownload.downloadId = downloadManager.add(request);
            }
            EventLogger.getLogger().debug(TAG, "Enqueued request to download ", episode.getMediaFileURL());


            for (CurrentDownload cd: currentDownloads){
                EventLogger.getLogger().debug(TAG, ">>>>>>>!!!!!!!!!!!********** download status ", downloadManager.query(cd.downloadId));
            }
        } catch (Throwable t) {
            EventLogger.getLogger().error(TAG, t, "Failed to request the download of ", episode.getMediaFileURL());
        }


    }

    /**
     * This methods gets invoked when the {@link DownloadEpisodeMediaCancelEvent} is raised (ie. a downloading episode
     * file needs to be cancel.
     */
    @Subscribe
    public void onDownloadEpisodeMediaCancelEvent(DownloadEpisodeMediaCancelEvent de) {

        Bus.consume(de);

        for (CurrentDownload currentDownload: currentDownloads){
            if (currentDownload.episode.equals(de.getEpisode())){
                //cancel download request
                downloadManager.cancel(currentDownload.downloadId);

                //remove from current downloads list
                currentDownloads.remove(currentDownload);

                //remove from memory
                getSubscriberAndRemove(currentDownload);
            }
        }

         //can be used to remove selected episodes from downloads activity
        Subscriber subscriber = SubscriberDAO.getCurrentSubscriber(realm);
        DAO.beginTransaction(realm);
        subscriber.getCurrentDownloadEpisodes().remove(de.getEpisode());
        DAO.commitTransaction(realm);


    }

    /**
     * Use the {@link CancelEvent} event to cancel a previous request submitted
     * to this {@Downloaded}.
     */
    @Subscribe(sticky = true)
    public void onCancel(CancelEvent cancel) {
        if (cancel == null)
            return;

        //////
        // try to find the event being cancelled....
        int idx = 0;
        for (CurrentDownload currentDownload : currentDownloads) {
            if (currentDownload.event.getEventID() == cancel.getEventToCancel())
                break;
            idx++;
        }

        ////////
        // If the event was found...
        if (idx > 0 && idx < currentDownloads.size()) {
            CurrentDownload currentDownload = currentDownloads.remove(idx);
            //this was our event, so no need for this cancel event to propagate further.
            Bus.consume(cancel);
            currentDownload.request.cancel();
            File fileToDelete = new File(currentDownload.request.getDestinationURI().toString());
            if (fileToDelete.exists() && !fileToDelete.delete()) {
                getLogger().error(TAG, "Unable to delete incomplete file ", fileToDelete);
            }
        }
    }





  /* =========================================================================
          Listener Methods for the DownloadStatusListenerV1 interface
     ========================================================================= */

    /**
     * This method is called by the ThinDownloadManager once the download is complete.  Though it is public
     * do not consider this method as such and consider it protected.
     */
    @Override
    public void onDownloadComplete(DownloadRequest downloadRequest) {
        for (CurrentDownload cd: currentDownloads){
            EventLogger.getLogger().debug(TAG, ">>>>>>>!!!!!!!!!!!********** download status at begining ", downloadManager.query(cd.downloadId));
        }
        for (CurrentDownload currentDownload : currentDownloads) {


            if (currentDownload.request != null && currentDownload.request.equals(downloadRequest)) {


                getLogger().debug(TAG, "URL ", currentDownload.request.getUri(), " was downloaded into ", currentDownload.request.getDestinationURI());

                //Now that the download is complete, let's unmark the file from incomplete.
                File newFile = new File(getDownloadFileName(null, currentDownload.event.getEventID()));
                File currentFile = new File(currentDownload.request.getDestinationURI().toString());
                if (!currentFile.exists())
                    getLogger().error(TAG, "Completed file ", currentDownload.request.getDestinationURI(), " does not exists and can't be renamed");
                if (!currentFile.renameTo(newFile))
                    getLogger().error(TAG, "Unable to make file ", currentDownload.request.getDestinationURI(), " as complete.");

                Episode e = currentDownload.episode;
                if (e != null) {
                    DAO.beginTransaction(realm);
                    e.setFileLocation(newFile.toString());
                    DAO.commitTransaction(realm);
                } else {
                    newFile.delete();
                }

                currentDownloads.remove(currentDownload);

                getSubscriberAndRemove(currentDownload);


                //Bus.post(currentDownload.event.getResponseEvent());
                DownloadEpisodeMediaCompleteEvent downloadEpisodeMediaCompleteEvent = new DownloadEpisodeMediaCompleteEvent(e.getId());
                Bus.post(downloadEpisodeMediaCompleteEvent);

                break;
            }
        }

        for (CurrentDownload cd: currentDownloads){
            EventLogger.getLogger().debug(TAG, ">>>>>>>!!!!!!!!!!!********** download status at end ", downloadManager.query(cd.downloadId));
        }
    }

    /**
     * This method is called by the ThinDownloadManager once the download fails.  Though it is public
     * do not consider this method as such and consider it protected.
     */
    @Override
    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {

        Iterator<CurrentDownload> iter = currentDownloads.iterator();

        while(iter.hasNext()){
            CurrentDownload currentDownload = iter.next();

            if (currentDownload.request != null && currentDownload.request.equals(downloadRequest)) {
                iter.remove();

                getSubscriberAndRemove(currentDownload);

                getLogger().error(TAG, "Error (", errorCode, ") occured downloading ", currentDownload.request.getUri(), ":", errorMessage);
                new File(currentDownload.request.getDestinationURI().toString()).delete();

                Bus.post(currentDownload.event.getFailureResponseEvent(errorMessage));

            }
        }
//        for (CurrentDownload currentDownload : currentDownloads) {
//            if (currentDownload.request != null && currentDownload.request.equals(downloadRequest)) {
//                currentDownloads.remove(currentDownload);
//
//                getSubscriberAndRemove(currentDownload);
//
//                getLogger().error(TAG, "Error (", errorCode, ") occured downloading ", currentDownload.request.getUri(), ":", errorMessage);
//                new File(currentDownload.request.getDestinationURI().toString()).delete();
//
//                Bus.post(currentDownload.event.getFailureResponseEvent(errorMessage));
//
//            }
//        }
    }

    private void getSubscriberAndRemove(CurrentDownload currentDownload) {
        Subscriber subscriber = SubscriberDAO.getCurrentSubscriber(realm);
        DAO.beginTransaction(realm);
        subscriber.getCurrentDownloadEpisodes().remove(currentDownload.episode);
        DAO.commitTransaction(realm);
    }

    /**
     * This method is called by the ThinDownloadManager to report download progress.  Though it is public
     * do not consider this method as such and consider it protected.
     */
    @Override
    public void onProgress(DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {

        //find out which current download is this onProgress for..
        for (CurrentDownload cd : currentDownloads) {
            if (cd.request.equals(request)) {
                if (cd.isInit == false) {

                    updateProgressAndPostEvent(cd, progress);
                    cd.isInit = true;

                } else if (cd.isInit == true) {
                    //.. check to make sure progress change is more than 1
                    if ((progress - cd.episode.getProgress()) > 1) {
                        //..post the event
                        updateProgressAndPostEvent(cd, progress);
                    }
                }
            }
        }
    }

    private void updateProgressAndPostEvent(CurrentDownload cd, int progress) {

        DAO.beginTransaction(realm);
        cd.episode.setProgress(progress);
        DAO.commitTransaction(realm);

        downloadEpisodeMediaProgressEvent = new DownloadEpisodeMediaProgressEvent(cd.episode);
        Bus.post(downloadEpisodeMediaProgressEvent);
    }


    /**
     * given an event ID and an optional prefix, return a download file name which is made
     * of the downloadFolder and the eventID.
     */
    private String getDownloadFileName(String prefix, Long eventID) {
        if (prefix != null)
            return Utils.mkString(downloadFolder, File.separator, prefix, eventID);
        else
            return Utils.mkString(downloadFolder, File.separator, eventID);
    }


}

package io.github.nightwolf.restapi.util.manager;

import io.github.nightwolf.restapi.dto.DownloadDTO;
import io.github.nightwolf.restapi.util.PriorityComparator;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author oshan
 */
public class DownloadManager {

    private ThreadPoolExecutor downloader = null;
    private Queue<DownloadDTO> downloadsQueue = null;

    private boolean isStarted = false;

    public DownloadManager() {
        downloadsQueue = new PriorityQueue<>(new PriorityComparator());
    }

    public void addDownload(DownloadDTO downloadDTO) {
        downloadsQueue.add(downloadDTO);
        if(isStarted) {
            downloader.execute(downloadDTO);
        }
    }

    public boolean removeDownload(DownloadDTO downloadDTO) {
        downloadsQueue.remove(downloadDTO);
        if(isStarted) {
            return downloader.remove(downloadDTO);
        }

        return false;
    }

    public DownloadDTO getDownload(DownloadDTO downloadDTO) {
        return downloadsQueue
                .stream()
                .filter(d->d.equals(downloadDTO))
                .collect(Collectors.toList())
                .get(0);
    }

    public Queue<DownloadDTO> getDownloadsQueue() {
        return downloadsQueue;
    }

    private void init() {
        downloader = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        downloader.prestartAllCoreThreads();        //start all the threads
    }

    private void setDownloads() {
        downloader.getQueue().addAll(downloadsQueue);
    }

    private void updateUnfinished(List<DownloadDTO> unfinishedDownloads) {
        //update the database
    }

    public void start() {
        init();
        setDownloads();
    }

    public void stop() {
        List<DownloadDTO> unfinishedDownloads;

        if(downloader != null) {
            try{
                downloader.shutdown();
                downloader.awaitTermination(5,TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                System.out.println("Interrupted");
            }finally {
                unfinishedDownloads = downloader.shutdownNow()
                        .stream()
                        .map(runnable -> (DownloadDTO)runnable)
                        .collect(Collectors.toList());

                updateUnfinished(unfinishedDownloads);

                downloader = null;      //remove the downloader

                System.out.println("Downloads Shut Down Successfully!");
            }

        }
    }
}

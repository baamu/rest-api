package io.github.nightwolf.restapi.util.manager;

import io.github.nightwolf.restapi.dto.DownloadDTO;
import io.github.nightwolf.restapi.entity.DownloadType;
import io.github.nightwolf.restapi.entity.TempDownload;
import io.github.nightwolf.restapi.entity.User;
import io.github.nightwolf.restapi.repository.DownloadTypeRepository;
import io.github.nightwolf.restapi.repository.TempDownloadRepository;
import io.github.nightwolf.restapi.repository.UserRepository;
import io.github.nightwolf.restapi.util.PriorityComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
@Component("downloadManager")
public class DownloadManager {

    private volatile ThreadPoolExecutor downloader = null;
    private Queue<DownloadDTO> downloadsQueue = null;

    private SimpleDateFormat format;

    private volatile boolean isStarted = false;

    private TempDownloadRepository tempDownloadRepository;
    private DownloadTypeRepository downloadTypeRepository;
    private UserRepository userRepository;

    public DownloadManager(@Qualifier("tempDownloadRepository") @Autowired TempDownloadRepository tempDownloadRepository, @Qualifier("downloadTypeRepository") @Autowired DownloadTypeRepository downloadTypeRepository, @Qualifier("userRepository") @Autowired UserRepository userRepository) {
        this.tempDownloadRepository = tempDownloadRepository;
        this.downloadTypeRepository = downloadTypeRepository;
        this.userRepository = userRepository;

        downloadsQueue = new PriorityQueue<>(new PriorityComparator());
        format = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void addToQueue(DownloadDTO downloadDTO) {
        downloadsQueue.add(downloadDTO);

        if(isStarted) {
            downloader.execute(downloadDTO);
        }
    }

    public void addDownload(DownloadDTO downloadDTO) {
        TempDownload download = saveDownload(downloadDTO);
        downloadDTO.setId(Long.toString(download.getId()));

        addToQueue(downloadDTO);

    }

    private TempDownload saveDownload(DownloadDTO downloadDTO) {
        TempDownload tempDownload = new TempDownload();

        DownloadType type = downloadTypeRepository.findByFileType(getType(downloadDTO.getContentType()));
        User user = userRepository.findById(downloadDTO.getUserId()).orElse(null);

        try {
            tempDownload.setAddedDate(format.parse(downloadDTO.getAddedDate()));
        } catch (ParseException e) {
            tempDownload.setAddedDate(new Date());
        }

        tempDownload.setLastModified(downloadDTO.getAddedDate());
        tempDownload.setName(downloadDTO.getFileName());
        tempDownload.setSize(downloadDTO.getFileSize());
        tempDownload.setType(type);
        tempDownload.setUrl(downloadDTO.getUrl().toString());
        tempDownload.setUser(user);

        return tempDownloadRepository.save(tempDownload);
    }

    public boolean removeDownload(DownloadDTO downloadDTO) {

        downloadsQueue.remove(downloadDTO);
        tempDownloadRepository.deleteByUrl(downloadDTO.getUrl().toString());

        if(isStarted) {
            return downloader.remove(downloadDTO);
        }

        return true;
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
        for(DownloadDTO download : downloadsQueue) {
            downloader.getQueue().add(download);
        }
    }

    public void start() {
        if(!isStarted) {
            System.out.println("Downloading starting");
            init();
            setDownloads();
            System.out.println("Downloading started at : " + new Date());
            isStarted = true;
        }
    }

    public void stop() {
        List<DownloadDTO> unfinishedDownloads = null;

        if(downloader != null) {
            try{

                downloader.shutdown();
                downloader.awaitTermination(1,TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

                for(DownloadDTO downloadDTO : downloadsQueue) {
                    downloadDTO.exit();         //set isExit true so that downloading will stop
                }

                downloader = null;      //remove the downloader
                isStarted = false;

                System.out.println("Downloads Shut Down Successfully!");
            }

        }
    }

    public String getType(String contentType) {
        if (contentType.contains("image/")) {
            return "images";
        } else if (contentType.contains("audio/") || contentType.contains("music/")) {
            return "audios";
        } else if (contentType.contains("video/")) {
            return "videos";
        } else if(contentType.contains("text/")) {
            return "documents";
        } else if(contentType.contains("application/")) {
            if(contentType.contains("pdf") || contentType.contains("msword") || contentType.contains("excel")) {
                return "documents";
            } else if(contentType.contains("x-gzip") || contentType.contains("x-compressed") || contentType.contains("zip") || contentType.contains("x-zip")) {
                return "other";
            } else {
                return "programs";
            }
        } else {
            return "other";
        }
    }

}

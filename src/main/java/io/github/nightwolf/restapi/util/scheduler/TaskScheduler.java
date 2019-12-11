package io.github.nightwolf.restapi.util.scheduler;

import io.github.nightwolf.restapi.dto.DownloadDTO;
import io.github.nightwolf.restapi.entity.Download;
import io.github.nightwolf.restapi.entity.DownloadType;
import io.github.nightwolf.restapi.entity.User;
import io.github.nightwolf.restapi.repository.DownloadRepository;
import io.github.nightwolf.restapi.repository.DownloadTypeRepository;
import io.github.nightwolf.restapi.repository.TempDownloadRepository;
import io.github.nightwolf.restapi.repository.UserRepository;
import io.github.nightwolf.restapi.util.manager.DownloadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author oshan
 */
public class TaskScheduler {

    private static final DownloadManager DOWNLOAD_MANAGER = new DownloadManager();

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(2);

    private static final int DOWNLOAD_START_HOUR = 0;
    private static final int DOWNLOAD_START_MIN = 0;
    private static final int DOWNLOAD_START_SEC = 0;

    private static final int DOWNLOAD_END_HOUR = 8;
    private static final int DOWNLOAD_END_MIN = 0;
    private static final int DOWNLOAD_END_SEC = 0;

    private static final String ZONE_ID = "Asia/Colombo";

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy/MM/dd");

    @Autowired
    @Qualifier(value = "tempDownloadRepository")
    private static TempDownloadRepository tempDownloadRepository;

    @Autowired
    @Qualifier(value = "downloadRepository")
    private static DownloadRepository downloadRepository;

    @Autowired
    @Qualifier(value = "userRepository")
    private static UserRepository userRepository;

    @Autowired
    @Qualifier(value = "downloadTypeRepository")
    private static DownloadTypeRepository downloadTypeRepository;

    public TaskScheduler() {
        initialize();
//        populateUncompletedDownloads();
    }

    private void initialize() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(ZONE_ID));
        ZonedDateTime nextDownloadStart = now.withHour(DOWNLOAD_START_HOUR)
                .withMinute(DOWNLOAD_START_MIN)
                .withSecond(DOWNLOAD_START_SEC);

        ZonedDateTime nextDownloadEnd = now.withHour(DOWNLOAD_END_HOUR)
                .withMinute(DOWNLOAD_END_MIN)
                .withSecond(DOWNLOAD_END_SEC);

        if(now.compareTo(nextDownloadStart) > 0) {
            nextDownloadStart = nextDownloadStart.plusDays(1);
        }

        if(now.compareTo(nextDownloadEnd) > 0) {
            nextDownloadEnd = nextDownloadEnd.plusDays(1);
        }

        Duration startDuration = Duration.between(now, nextDownloadStart);
        Duration endDuration = Duration.between(now, nextDownloadEnd);

        long startDelay = startDuration.getSeconds();
        long endDelay = endDuration.getSeconds();

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                startDownloads();
            }
        }, startDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                terminateDownloads();
            }
        }, endDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

    }

    private void populateUncompletedDownloads() {

        tempDownloadRepository.findAll().forEach(tempDownload -> {
            try {
                DOWNLOAD_MANAGER.addDownload(
                        new DownloadDTO(
                                tempDownload.getId()+"",
                                tempDownload.getUser().getEmail(),
                                new URL(tempDownload.getUrl()),
                                tempDownload.getSize(),
                                FORMATTER.format(tempDownload.getAddedDate()),
                                tempDownload.getLastModified(),
                                tempDownload.getName()
                        )
                );
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
    }

    public void startDownloads() {
        DOWNLOAD_MANAGER.start();
    }

    public void terminateDownloads() {
        DOWNLOAD_MANAGER.stop();
    }

    public void addDownload(DownloadDTO downloadDTO) {
        DOWNLOAD_MANAGER.addDownload(downloadDTO);
    }

    public boolean removeDownload(DownloadDTO downloadDTO) {
        return DOWNLOAD_MANAGER.removeDownload(downloadDTO);
    }

    public Queue<DownloadDTO> getDownloadsQueue() {
        return DOWNLOAD_MANAGER.getDownloadsQueue();
    }

    //remove downloadDTO from queue and temp_download table and add to download table
    public static void notifyDownloadFinish(DownloadDTO downloadDTO) {
        Download download = new Download();
        try {
            download.setAddedDate(FORMATTER.parse(downloadDTO.getAddedDate()));
        } catch (ParseException e) {
            download.setAddedDate(new Date());
        }

        DOWNLOAD_MANAGER.removeDownload(downloadDTO);

        User user = userRepository.findById(downloadDTO.getUserId()).orElse(null);
        DownloadType type = downloadTypeRepository.findByFileType(downloadDTO.getFileType());

        download.setDownloadedDate(new Date());
        download.setFileSize(downloadDTO.getFileSize());
        download.setName(downloadDTO.getFileName());
        download.setUrl(downloadDTO.getUrl().toString());
        download.setUsedTimes(1);
        download.setUser(user);
        download.setType(type);

        downloadRepository.save(download);

        tempDownloadRepository.deleteByUrl(downloadDTO.getUrl().toString());
    }

    //remove downloadDTO from queue and add back
    public static void notifyInterruptedDownload(DownloadDTO downloadDTO) {
        DOWNLOAD_MANAGER.removeDownload(downloadDTO);
        DOWNLOAD_MANAGER.addDownload(downloadDTO);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        SCHEDULED_EXECUTOR_SERVICE.shutdown();

        try {
            SCHEDULED_EXECUTOR_SERVICE.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            System.out.println("TaskScheduler Interrupted");
            SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
        }
    }
}

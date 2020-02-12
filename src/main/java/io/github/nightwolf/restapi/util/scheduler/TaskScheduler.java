package io.github.nightwolf.restapi.util.scheduler;

import io.github.nightwolf.restapi.dto.DownloadDTO;
import io.github.nightwolf.restapi.entity.Download;
import io.github.nightwolf.restapi.entity.DownloadType;
import io.github.nightwolf.restapi.entity.User;
import io.github.nightwolf.restapi.repository.DownloadRepository;
import io.github.nightwolf.restapi.repository.DownloadTypeRepository;
import io.github.nightwolf.restapi.repository.TempDownloadRepository;
import io.github.nightwolf.restapi.repository.UserRepository;
import io.github.nightwolf.restapi.service.EmailSenderService;
import io.github.nightwolf.restapi.util.manager.DownloadManager;
import io.github.nightwolf.restapi.util.manager.RepositoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
@Component(value = "taskScheduler")
public class TaskScheduler {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(2);

    private static final int DOWNLOAD_START_HOUR = 0;
    private static final int DOWNLOAD_START_MIN = 0;
    private static final int DOWNLOAD_START_SEC = 0;

    private static final int DOWNLOAD_END_HOUR = 8;
    private static final int DOWNLOAD_END_MIN = 0;
    private static final int DOWNLOAD_END_SEC = 0;

    private static final int CLEANING_START_HOUR = 22;
    private static final int CLEANING_START_MIN = 0;
    private static final int CLEANING_START_SEC = 0;

    private static final int CLEANING_END_HOUR = 23;
    private static final int CLEANING_END_MIN = 0;
    private static final int CLEANING_END_SEC = 0;

    private static final String ZONE_ID = "Asia/Colombo";

    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy/MM/dd");

    private final DownloadManager downloadManager;
    private final RepositoryManager repositoryManager;


    @Autowired
    @Qualifier(value = "userRepository")
    private UserRepository userRepository;

    private DownloadTypeRepository downloadTypeRepository;
    private TempDownloadRepository tempDownloadRepository;
    private DownloadRepository downloadRepository;

    private EmailSenderService emailSenderService;

    private String documentRepoPath;
    private String imgRepoPath;
    private String audRepoPath;
    private String vidRepoPath;
    private String prgmRepoPath;
    private String othrRepoPath;


    public String getDownloadPath(String contentType) {
        if (contentType.contains("image/")) {
            return imgRepoPath;
        } else if (contentType.contains("audio/") || contentType.contains("music/")) {
            return audRepoPath;
        } else if (contentType.contains("video/")) {
            return vidRepoPath;
        } else if(contentType.contains("text/")) {
            return documentRepoPath;
        } else if(contentType.contains("application/")) {
            if(contentType.contains("pdf") || contentType.contains("msword") || contentType.contains("excel")) {
                return documentRepoPath;
            } else if(contentType.contains("x-gzip") || contentType.contains("x-compressed") || contentType.contains("zip") || contentType.contains("x-zip")) {
                return othrRepoPath;
            } else {
                return prgmRepoPath;
            }
        } else {
            return othrRepoPath;
        }
    }

    @Autowired
    public TaskScheduler(@Autowired EmailSenderService emailSenderService, @Qualifier("downloadRepository") @Autowired DownloadRepository downloadRepository, @Autowired @Qualifier("downloadTypeRepository") DownloadTypeRepository downloadTypeRepository, @Autowired @Qualifier("tempDownloadRepository") TempDownloadRepository tempDownloadRepository, @Autowired DownloadManager downloadManager) {
        this.downloadTypeRepository = downloadTypeRepository;
        this.tempDownloadRepository = tempDownloadRepository;
        this.downloadRepository = downloadRepository;

        this.downloadManager = downloadManager;
        this.repositoryManager = new RepositoryManager(downloadRepository, downloadTypeRepository);

        this.emailSenderService = emailSenderService;

        documentRepoPath = downloadTypeRepository.findByFileType("documents").getDefaultPath();
        imgRepoPath = downloadTypeRepository.findByFileType("images").getDefaultPath();
        audRepoPath = downloadTypeRepository.findByFileType("audios").getDefaultPath();
        vidRepoPath = downloadTypeRepository.findByFileType("videos").getDefaultPath();
        prgmRepoPath = downloadTypeRepository.findByFileType("programs").getDefaultPath();
        othrRepoPath = downloadTypeRepository.findByFileType("other").getDefaultPath();

        initialize();
        initCleaning();
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

    private void initCleaning() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(ZONE_ID));
        ZonedDateTime nextDownloadStart = now.withHour(CLEANING_START_HOUR)
                .withMinute(CLEANING_START_MIN)
                .withSecond(CLEANING_START_SEC);

        ZonedDateTime nextDownloadEnd = now.withHour(CLEANING_END_HOUR)
                .withMinute(CLEANING_END_MIN)
                .withSecond(CLEANING_END_SEC);

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
                repositoryManager.startCleaning();
            }
        }, startDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                repositoryManager.stopCleaning();
            }
        }, endDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    public void populateUncompletedDownloads() {

        tempDownloadRepository.findAll().forEach(tempDownload -> {
            try {
                downloadManager.addToQueue(
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
        downloadManager.start();
    }

    public void terminateDownloads() {
        downloadManager.stop();
    }

    public void addDownload(DownloadDTO downloadDTO) {
        downloadManager.addDownload(downloadDTO);
    }

    public boolean removeDownload(DownloadDTO downloadDTO) {
        return downloadManager.removeDownload(downloadDTO);
    }

    public Queue<DownloadDTO> getDownloadsQueue() {
        return downloadManager.getDownloadsQueue();
    }

    //remove downloadDTO from queue and temp_download table and add to download table
    @Transactional
    public void notifyDownloadFinish(DownloadDTO downloadDTO) {
        Download download = new Download();
        try {
            download.setAddedDate(FORMATTER.parse(downloadDTO.getAddedDate()));
        } catch (ParseException e) {
            download.setAddedDate(new Date());
        }

        downloadManager.removeDownload(downloadDTO);

        User user = userRepository.findById(downloadDTO.getUserId()).orElse(null);
        DownloadType type = downloadTypeRepository.findByFileType(downloadManager.getType(downloadDTO.getContentType()));

        download.setDownloadedDate(new Date());
        download.setFileSize(downloadDTO.getFileSize());
        download.setName(downloadDTO.getFileName());
        download.setUrl(downloadDTO.getUrl().toString());
        download.setUsedTimes(1);
        download.setUser(user);
        download.setType(type);

        downloadRepository.save(download);

        tempDownloadRepository.deleteByUrl(downloadDTO.getUrl().toString());

        System.out.println("Notifying : " + downloadDTO.getUserId());

        notifyUser(downloadDTO.getUserId(), downloadDTO.getFileName(), downloadDTO.getAddedDate(), download.getDownloadedDate().toString());

    }

    //remove downloadDTO from queue and add back
    public void notifyInterruptedDownload(DownloadDTO downloadDTO) {
        downloadManager.removeDownload(downloadDTO);
        downloadManager.addDownload(downloadDTO);
    }

    public void notifyUser(String email, String fileName, String addedDate, String downloaded) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Download Completion");
        mailMessage.setFrom("nightwolfdownloader@gmail.com");
        mailMessage.setText("The following download has been finished downloading\r\n"
                + "File Name : " + fileName + "\r\n"
                + "Added Date : " + addedDate + "\r\n"
                + "Downloaded Date : " + downloaded + "\r\n\r\n"
                + "If this download was not added by you, please inform an administrator or send an email to : "
                + "nightwolfdownloader@gmail.com\r\n"
        );

        emailSenderService.sendEmail(mailMessage);
    }

    public RepositoryManager getRepositoryManager() {
        return repositoryManager;
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

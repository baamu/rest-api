package io.github.nightwolf.restapi.controller.admin;

import io.github.nightwolf.restapi.dto.BasicReplyDTO;
import io.github.nightwolf.restapi.dto.DownloadDTO;
import io.github.nightwolf.restapi.util.scheduler.TaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oshan
 */
@RestController
@RequestMapping("api/admin")
public class AdminController {

    public static TaskScheduler TASK_SCHEDULER;

    static {
        System.setProperty("http.agent", "Chrome");
    }

    @Autowired
    AdminController(TaskScheduler taskScheduler) {
        TASK_SCHEDULER = taskScheduler;
    }

    @GetMapping("/download/start")
    @ResponseBody
    public BasicReplyDTO startAllDownloads() {
//        PublicController.downloads.forEach(DownloadDTO::run);

        TASK_SCHEDULER.startDownloads();
        return new BasicReplyDTO("Downloads started!");
    }

    @GetMapping("/download/get-all")
    public List<DownloadDTO> getAllOnGoingDownloads() {
//        return PublicController.downloads;
        return new ArrayList<>(TASK_SCHEDULER.getDownloadsQueue());
    }

    @GetMapping("/download/stop-all")
    @ResponseBody
    public BasicReplyDTO stopAllDownloads() {
        TASK_SCHEDULER.terminateDownloads();
        return new BasicReplyDTO("Downloading terminated!");
    }

    @PostMapping("/download/stop")
    @ResponseBody
    public BasicReplyDTO stopDownload(@RequestBody DownloadDTO download) {
        return TASK_SCHEDULER.removeDownload(download)
                ? new BasicReplyDTO("Download terminated!")
                : new BasicReplyDTO("Download termination failed!");
    }

}

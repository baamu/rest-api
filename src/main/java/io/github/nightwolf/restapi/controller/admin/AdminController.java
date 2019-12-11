package io.github.nightwolf.restapi.controller.admin;

import io.github.nightwolf.restapi.dto.BasicReplyDTO;
import io.github.nightwolf.restapi.dto.DownloadDTO;
import io.github.nightwolf.restapi.util.scheduler.TaskScheduler;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oshan
 */
@RestController
@RequestMapping("api/admin")
public class AdminController {

    public final static TaskScheduler TASK_SCHEDULER;

    static {
        System.setProperty("http.agent", "Chrome");
        TASK_SCHEDULER = new TaskScheduler();
    }

    @GetMapping("/download/start")
    @ResponseBody
    public BasicReplyDTO startAllDownloads() {
//        PublicController.downloads.forEach(DownloadDTO::run);

        TASK_SCHEDULER.startDownloads();
        return new BasicReplyDTO("Downloads started!");
    }

    @GetMapping("/download/getall")
    public List<DownloadDTO> getAllDownloads() {
//        return PublicController.downloads;
        return new ArrayList<>(TASK_SCHEDULER.getDownloadsQueue());
    }

}

package io.github.nightwolf.restapi.controller.admin;

import io.github.nightwolf.restapi.dto.BasicReplyDTO;
import io.github.nightwolf.restapi.dto.DownloadDTO;
import io.github.nightwolf.restapi.dto.DownloadHistoryDTO;
import io.github.nightwolf.restapi.dto.UserDTO;
import io.github.nightwolf.restapi.repository.DownloadRepository;
import io.github.nightwolf.restapi.repository.UserRepository;
import io.github.nightwolf.restapi.util.manager.RepositoryManager;
import io.github.nightwolf.restapi.util.scheduler.TaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author oshan
 */
@RestController
@RequestMapping("api/admin")
public class AdminController {

    public static TaskScheduler TASK_SCHEDULER;

    @Autowired
    @Qualifier(value = "downloadRepository")
    private DownloadRepository downloadRepository;

    @Qualifier(value = "userRepository")
    @Autowired
    UserRepository userRepository;

    static {
        System.setProperty("http.agent", "Chrome");
    }

    @Autowired
    AdminController(TaskScheduler taskScheduler) {
        TASK_SCHEDULER = taskScheduler;
        TASK_SCHEDULER.populateUncompletedDownloads();
    }

    @GetMapping("/download/start")
    @ResponseBody
    public BasicReplyDTO startAllDownloads() {
        TASK_SCHEDULER.startDownloads();
        return new BasicReplyDTO("Downloads started!");
    }

    @GetMapping("/download/get-all")
    public List<DownloadDTO> getAllOnGoingDownloads() {
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

    @PostMapping("/download/pause")
    @ResponseBody
    public BasicReplyDTO pauseDownload(@RequestBody DownloadDTO downloadDTO) {
        TASK_SCHEDULER.pauseDownload(downloadDTO);

        return new BasicReplyDTO("Download paused!");
    }

    @PostMapping("/download/resume")
    @ResponseBody
    public BasicReplyDTO resumeDownload(@RequestBody DownloadDTO downloadDTO) {
        TASK_SCHEDULER.resumeDownload(downloadDTO);

        return new BasicReplyDTO("Download resumed!");
    }

    @GetMapping("/repository/delete")
    @Transactional
    @ResponseBody
    public BasicReplyDTO deleteFile(@RequestParam("id") long id) {
        if(TASK_SCHEDULER.deleteFile(id)) {
            return new BasicReplyDTO("File deleted successfully");
        } else {
            return new BasicReplyDTO("File deletion failed!");
        }
    }

    @GetMapping("/repository/get-all")
    @ResponseBody
    public List<DownloadHistoryDTO> getAllFiles() {
        return downloadRepository.findAll()
                .stream()
                .map(DownloadHistoryDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/user/get-all")
    @ResponseBody
    public List<UserDTO> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

}

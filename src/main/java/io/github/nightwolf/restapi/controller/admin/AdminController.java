package io.github.nightwolf.restapi.controller.admin;

import io.github.nightwolf.restapi.controller.common.PublicController;
import io.github.nightwolf.restapi.dto.BasicReplyDTO;
import io.github.nightwolf.restapi.dto.DownloadDTO;
import io.github.nightwolf.restapi.util.manager.DownloadManager;
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

    public final static DownloadManager downloadManager;

    static {
        downloadManager = new DownloadManager();
    }

    @GetMapping("/download/start")
    @ResponseBody
    public BasicReplyDTO startAllDownloads() {
//        PublicController.downloads.forEach(DownloadDTO::run);

        downloadManager.start();
        return new BasicReplyDTO("Downloads started!");
    }

    @GetMapping("/download/getall")
    public List<DownloadDTO> getAllDownloads() {
//        return PublicController.downloads;
        return new ArrayList<>(downloadManager.getDownloadsQueue());
    }

}

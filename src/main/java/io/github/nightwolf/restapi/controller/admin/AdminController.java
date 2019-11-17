package io.github.nightwolf.restapi.controller.admin;

import io.github.nightwolf.restapi.controller.common.PublicController;
import io.github.nightwolf.restapi.dto.BasicReplyDTO;
import io.github.nightwolf.restapi.dto.DownloadDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author oshan
 */
@RestController
@RequestMapping("api/admin")
public class AdminController {

    @GetMapping("/download/start")
    @ResponseBody
    public BasicReplyDTO startAllDownloads() {
        PublicController.downloads.forEach(DownloadDTO::run);

        return new BasicReplyDTO("Downloads started!");
    }

    @GetMapping("/download/getall")
    public List<DownloadDTO> getAllDownloads() {
        return PublicController.downloads;
    }
}

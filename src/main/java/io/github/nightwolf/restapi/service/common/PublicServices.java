package io.github.nightwolf.restapi.service.common;

import io.github.nightwolf.restapi.dto.BasicReply;
import io.github.nightwolf.restapi.dto.Download;
import io.github.nightwolf.restapi.dto.DownloadRequest;
import io.github.nightwolf.restapi.dto.TempUser;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oshan
 */
@RestController
@RequestMapping("api/public")
public class PublicServices {

    //for testing
    private List<Download> downloads = new ArrayList<>();

    @PostMapping("/user/register")
    @ResponseBody
    public BasicReply registerUser(@RequestBody TempUser tempUser) {
        System.out.println(tempUser);

        return new BasicReply("Request sent for registration");
    }


    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    +                                                               +
    +               User Specific Downloads                         +
    +                                                               +
    ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/


    //add new download to the queue
    @PostMapping("/download/add")
    @ResponseBody
    public BasicReply addDownload(@RequestBody DownloadRequest downloadRequest) {
        System.out.println("URL : "+ downloadRequest.getUrl());
        downloads.add(new Download("D001", downloadRequest.getUserId() , downloadRequest.getUrl()));
        downloads.get(0).setDownloadedSize(40);
        return new BasicReply("Success");
    }

    //remove a download in waiting or downloading queue
    @PostMapping("/download/remove")
    @ResponseBody
    public BasicReply removeDownload(@RequestBody Download download) {
        //test code
        return downloads.remove(download) ? new BasicReply("Success") : new BasicReply("Failed!");
    }

    //get all downloads of the user (previous downloads and waiting/ongoing downloads)
    @GetMapping("/download/getall")
    @ResponseBody
    public List<Download> getAllDownloads() {
        //test code
        return downloads;
    }

    //get the downloaded percentage of a download
    @GetMapping("/download/getpercent/{id}")
    @ResponseBody
    public double getDownloadPercent(@PathVariable String id) {
        //test code
        return downloads.get(downloads.indexOf(new Download(id))).getDownloadedSize();
    }



}

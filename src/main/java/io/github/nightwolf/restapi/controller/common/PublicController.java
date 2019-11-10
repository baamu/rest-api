package io.github.nightwolf.restapi.controller.common;

import io.github.nightwolf.restapi.dto.BasicReplyDTO;
import io.github.nightwolf.restapi.dto.DownloadDTO;
import io.github.nightwolf.restapi.dto.DownloadRequestDTO;
import io.github.nightwolf.restapi.entity.User;
import io.github.nightwolf.restapi.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oshan
 */
@CrossOrigin("*")
@RestController
@RequestMapping("api/public")
public class PublicController {

    //for testing
    private List<DownloadDTO> downloads = new ArrayList<>();

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/user/register")
    @ResponseBody
    public BasicReplyDTO registerUser(@RequestBody User user) {
        System.out.println(user);
        userDetailsServiceImpl.registerUser(user);
        return new BasicReplyDTO("Signed up!");
    }


    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    +                                                               +
    +               User Specific Downloads                         +
    +                                                               +
    ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/


    //add new download to the queue
    //add the download to temp database and in the response, sent the id of it
    //that id will be the id of that download until finished
    @PostMapping("/download/add")
    @ResponseBody
    public BasicReplyDTO addDownload(@RequestBody DownloadRequestDTO downloadRequestDTO) {
        System.out.println("URL : "+ downloadRequestDTO.getUrl());
        downloads.add(new DownloadDTO("D001", downloadRequestDTO.getUserId() , downloadRequestDTO.getUrl()));
        downloads.get(0).setDownloadedSize(40);
        return new BasicReplyDTO("Success");
    }

    //remove a download in waiting or downloading queue
    @PostMapping("/download/remove")
    @ResponseBody
    public BasicReplyDTO removeDownload(@RequestBody DownloadDTO download) {
        //test code
        return downloads.remove(download) ? new BasicReplyDTO("Success") : new BasicReplyDTO("Failed!");
    }

    //get all downloads of the service (previous downloads and waiting/ongoing downloads)
    @GetMapping("/download/getall")
    @ResponseBody
    public List<DownloadDTO> getAllDownloads() {
        //test code
        return downloads;
    }

    //get the downloaded percentage of a download
    @GetMapping("/download/getpercent/{id}")
    @ResponseBody
    public double getDownloadPercent(@PathVariable String id) {
        //test code
        return downloads.get(downloads.indexOf(new DownloadDTO(id))).getDownloadedSize();
    }



}
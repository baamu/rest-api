package io.github.nightwolf.restapi.controller.common;

import io.github.nightwolf.restapi.dto.BasicReplyDTO;
import io.github.nightwolf.restapi.dto.DownloadDTO;
import io.github.nightwolf.restapi.dto.DownloadRequestDTO;
import io.github.nightwolf.restapi.entity.ConfirmationToken;
import io.github.nightwolf.restapi.entity.TempUser;
import io.github.nightwolf.restapi.entity.User;
import io.github.nightwolf.restapi.repository.ConfirmationTokenRepository;
import io.github.nightwolf.restapi.repository.TempUserRepository;
import io.github.nightwolf.restapi.repository.UserRepository;
import io.github.nightwolf.restapi.service.EmailSenderService;
import io.github.nightwolf.restapi.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author oshan
 */
@RestController
@RequestMapping("api/public")
public class PublicController {

    //for testing
    public static List<DownloadDTO> downloads = new ArrayList<>();


    @Autowired
    @Qualifier(value = "userRepository")
    private UserRepository userRepository;

    @Autowired
    @Qualifier(value = "tempUserRepository")
    private TempUserRepository tempUserRepository;

    @Autowired
    @Qualifier(value = "confirmationTokenRepository")
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/user/register")
    @ResponseBody
    public BasicReplyDTO registerUser(@RequestBody TempUser tempUser) {
//        userDetailsServiceImpl.registerUser(user);

        User user = userRepository.findById(tempUser.getEmail()).orElse(null);
        TempUser temp = tempUserRepository.findById(tempUser.getEmail()).orElse(null);

        if(user != null) {
            return new BasicReplyDTO("This email already exists!");
        } else if (temp != null) {
            return new BasicReplyDTO("Verification already sent for this email!");
        }

        tempUser.setPassword(bCryptPasswordEncoder.encode(tempUser.getPassword()));
        tempUserRepository.save(tempUser);

        tempUser = tempUserRepository.findById(tempUser.getEmail()).get();

        ConfirmationToken confirmationToken = new ConfirmationToken(tempUser);
        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(tempUser.getEmail());
        mailMessage.setSubject("NightWolf Registration");
        mailMessage.setFrom("nightwolfdownloadmanager@gmail.com");
        mailMessage.setText("To confirm your account, please click on the below link : "
                + "http://localhost:8080/api/public/user/confirm-account?token="
                + confirmationToken.getToken()
        );

        emailSenderService.sendEmail(mailMessage);

        return new BasicReplyDTO("Verification email sent to complete registration!");
    }


    @GetMapping("user/confirm-account")
    @Transactional
    public BasicReplyDTO confirmUser(@RequestParam("token") String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);

        if(confirmationToken != null) {
            TempUser tempUser = tempUserRepository.findById(confirmationToken.getTempUser().getEmail()).orElse(null);
            if (tempUser != null) {
                User user = new User(tempUser);
                userRepository.save(user);
                tempUserRepository.deleteById(confirmationToken.getTempUser().getEmail());

                return new BasicReplyDTO("accountVerified");
            } else {
                return new BasicReplyDTO("User does not exist! Please register first!");

            }
        } else {
            return new BasicReplyDTO("The link is invalid or broken!");
        }
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

        String id = (SecurityContextHolder.getContext().getAuthentication().getPrincipal()).toString();

        System.out.println(id);

        try {
            downloads.add(new DownloadDTO(id , new URL(downloadRequestDTO.getUrl())));
        } catch (MalformedURLException e) {
            return new BasicReplyDTO("Error! Download URL failed!");
        }

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
        String id = (SecurityContextHolder.getContext().getAuthentication().getPrincipal()).toString();
        return downloads.stream().filter(downloadDTO -> downloadDTO.getUserId().equals(id)).collect(Collectors.toList());
    }

    //get the downloaded percentage of a download
    @GetMapping("/download/getpercent/{id}")
    @ResponseBody
    public double getDownloadPercent(@PathVariable String id) {
        //test code
        return downloads.get(downloads.indexOf(new DownloadDTO(id))).getDownloadedSize();
    }

    //this is only for testing
    @GetMapping("/download/start")
    @ResponseBody
    public BasicReplyDTO startDownloads() {
        String id = (SecurityContextHolder.getContext().getAuthentication().getPrincipal()).toString();

        downloads.stream()
                .filter(downloadDTO -> downloadDTO.getUserId().equals(id))
                .collect(Collectors.toList())
                .forEach(DownloadDTO::run);

        return new BasicReplyDTO("Downloads started!");
    }



}

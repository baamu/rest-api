package io.github.nightwolf.restapi.controller.common;

import io.github.nightwolf.restapi.controller.admin.AdminController;
import io.github.nightwolf.restapi.dto.BasicReplyDTO;
import io.github.nightwolf.restapi.dto.DownloadDTO;
import io.github.nightwolf.restapi.dto.DownloadHistoryDTO;
import io.github.nightwolf.restapi.dto.DownloadRequestDTO;
import io.github.nightwolf.restapi.entity.ConfirmationToken;
import io.github.nightwolf.restapi.entity.TempUser;
import io.github.nightwolf.restapi.entity.User;
import io.github.nightwolf.restapi.repository.ConfirmationTokenRepository;
import io.github.nightwolf.restapi.repository.DownloadRepository;
import io.github.nightwolf.restapi.repository.TempUserRepository;
import io.github.nightwolf.restapi.repository.UserRepository;
import io.github.nightwolf.restapi.service.EmailSenderService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author oshan
 */
@RestController
@RequestMapping("api/public")
public class PublicController {

    //for testing
    public static List<DownloadDTO> downloads = new ArrayList<>();

    @Value("${server.address}")
    private String ip;

    @Value("${server.port}")
    private String port;

    @Autowired
    @Qualifier(value = "userRepository")
    private UserRepository userRepository;

    @Autowired
    @Qualifier(value = "tempUserRepository")
    private TempUserRepository tempUserRepository;

    @Autowired
    @Qualifier(value = "downloadRepository")
    private DownloadRepository downloadRepository;

    @Autowired
    @Qualifier(value = "confirmationTokenRepository")
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * @param tempUser user to be registered to the system
     * @return BasicReplyDTO with Success or Fail message
     */
    @PostMapping("/user/register")
    @ResponseBody
    public BasicReplyDTO registerUser(@RequestBody TempUser tempUser) {

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
        mailMessage.setFrom("nightwolfdownloader@gmail.com");
        mailMessage.setText("To confirm your account, please click on the below link : "
                + "http://"
                +"localhost"
                +":"
                +"8080"
                +"/api/public/user/confirm-account?token="
                + confirmationToken.getToken()
        );

        emailSenderService.sendEmail(mailMessage);

        return new BasicReplyDTO("Verification email sent to complete registration!");
    }


    /**
     * @param token Confirmation Token in the query string
     * @return BasicReplyDTO with account verification message
     */
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

                return new BasicReplyDTO("Account Verified");
            } else {
                return new BasicReplyDTO("User does not exist! Please register first!");

            }
        } else {
            return new BasicReplyDTO("The link is invalid or broken!");
        }
    }


    /*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    +                                                               +
    +                       Repository                              +
    +                                                               +
    ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

    @GetMapping("/repository/get-trending")
    @ResponseBody
    public List<DownloadHistoryDTO> getTrendingFiles() {
        List<DownloadHistoryDTO> downloads;

        downloads = downloadRepository.findTop10ByOrderByUsedTimes()
                .stream()
                .map(DownloadHistoryDTO::new)
                .collect(Collectors.toList());

        return downloads;
    }

    @GetMapping("repository/{dir}")
    @ResponseBody
    public List<DownloadHistoryDTO> getDownloadedFiles(@PathVariable String dir, @RequestParam(value = "page", defaultValue = "1") int page) {
        int directory = -1;

        switch (dir) {
            case "documents" : directory = 1; break;
            case "images" : directory = 2; break;
            case "audios" : directory = 3; break;
            case "videos" : directory = 4; break;
            case "programs" : directory = 5; break;
            case "other" : directory = 6; break;
        }

        if(directory == -1) {
            return null;
        }

        return downloadRepository.findAllByType(directory, PageRequest.of(page, 10))
                .stream()
                .map(DownloadHistoryDTO::new)
                .collect(Collectors.toList());
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
        String link = downloadRequestDTO.getUrl();

        if(link.isEmpty()) {
            return new BasicReplyDTO("Error! Download URL failed!");
        }

        System.out.println("URL : "+ link);

        String id = (SecurityContextHolder.getContext().getAuthentication().getPrincipal()).toString();

        System.out.println(id);

        try {
            if(link.contains("www.youtube.com")) {
                URL url = getYoutubeDownloadLink(link);
                if(url != null) {
                    AdminController.TASK_SCHEDULER.addDownload(new DownloadDTO(id , url, getYoutubeTitle(link)));
                } else {
                    return new BasicReplyDTO("Error! Download URL failed!");
                }
            } else {
                AdminController.TASK_SCHEDULER.addDownload(new DownloadDTO(id , new URL(downloadRequestDTO.getUrl())));
            }

        } catch (MalformedURLException e) {
            return new BasicReplyDTO("Error! Download URL failed!");
        }

        return new BasicReplyDTO("Success");
    }

    /**
     * @param download on going download to be removed from queue
     * @return BasicReplyDTO with Success or Failed message
     */
    //remove a download in waiting or downloading queue
    @PostMapping("/download/remove")
    @ResponseBody
    public BasicReplyDTO removeDownload(@RequestBody DownloadDTO download) {
        //test code
//        return downloads.remove(download) ? new BasicReplyDTO("Success") : new BasicReplyDTO("Failed!");
        return AdminController.TASK_SCHEDULER.removeDownload(download) ? new BasicReplyDTO("Success") : new BasicReplyDTO("Failed!");
    }

    /**
     * @return On going downloads of the user
     */
    //get all downloads of the service (previous downloads and waiting/ongoing downloads)
    @GetMapping("/download/getall")
    @ResponseBody
    public List<DownloadDTO> getAllDownloads() {
        //test code
        String id = (SecurityContextHolder.getContext().getAuthentication().getPrincipal()).toString();
        return AdminController.TASK_SCHEDULER.getDownloadsQueue()
                .stream()
                .parallel()
                .filter(downloadDTO -> downloadDTO.getUserId().equals(id))
                .collect(Collectors.toList());
    }

    /**
     * @param id id of the on going download
     * @return downloaded size of the file
     */
    //get the downloaded percentage of a download
    @GetMapping("/download/getpercent/{id}")
    @ResponseBody
    public double getDownloadPercent(@PathVariable String id) {
        //test code
        List<DownloadDTO> downloads = new ArrayList<>(AdminController.TASK_SCHEDULER.getDownloadsQueue());
        return downloads.get(downloads.indexOf(new DownloadDTO(id))).getDownloadedSize();
    }


    /**
     * @return Last 25 downloads of the user
     */
    @GetMapping("/download/history")
    @ResponseBody
    public List<DownloadHistoryDTO> getDownloadHistory() {
        String id = (SecurityContextHolder.getContext().getAuthentication().getPrincipal()).toString();
        User user = userRepository.findById(id).get();
        return downloadRepository.findFirst25ByUserOrderByDownloadedDateDesc(user)
                .stream()
                .map(DownloadHistoryDTO::new)
                .collect(Collectors.toList());
    }

    //this is only for testing
//    @GetMapping("/download/start")
//    @ResponseBody
//    public BasicReplyDTO startDownloads() {
//        String id = (SecurityContextHolder.getContext().getAuthentication().getPrincipal()).toString();
//
//        downloads.stream()
//                .filter(downloadDTO -> downloadDTO.getUserId().equals(id))
//                .collect(Collectors.toList())
//                .forEach(DownloadDTO::run);
//
//        return new BasicReplyDTO("Downloads started!");
//    }

    private String getYoutubeTitle(String link) {
        try {
            return Jsoup.connect(link).get().title()+".mp4";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getHTMLLink(String link) {
        HttpURLConnection conn = null;
        StringBuilder contents = new StringBuilder();
        try {
            conn = (HttpURLConnection) new URL(link).openConnection();
//            conn.setConnectTimeout(CONNECT_TIMEOUT);
//            conn.setReadTimeout(READ_TIMEOUT);

            InputStream is = conn.getInputStream();

            String enc = conn.getContentEncoding();

            if (enc == null) {
                Pattern p = Pattern.compile("charset=(.*)");
                Matcher m = p.matcher(conn.getHeaderField("Content-Type"));
                if (m.find()) {
                    enc = m.group(1);
                }
            }

            if (enc == null)
                enc = "UTF-8";

            BufferedReader br = new BufferedReader(new InputStreamReader(is, enc));

            String line;


            while ((line = br.readLine()) != null) {
                contents.append(line);
                contents.append("\n");
            }

            br.close();

        }catch (IOException ex) {

        } finally {
            assert conn != null;
            conn.disconnect();
        }

        return contents.toString();
    }

    private URL getYoutubeDownloadLink(String link) {
        List<String> urlList = new ArrayList<>();
        Pattern urlencod = Pattern.compile("\"url_encoded_fmt_stream_map\":\"([^\"]*)\"");
        Matcher urlencodMatch = urlencod.matcher(getHTMLLink(link));

        if (urlencodMatch.find()) {
            String url_encoded_fmt_stream_map;
            url_encoded_fmt_stream_map = urlencodMatch.group(1);
            Pattern encod = Pattern.compile("url=(.*)");
            Matcher encodMatch = encod.matcher(url_encoded_fmt_stream_map);
            if (encodMatch.find()) {
                String sline = encodMatch.group(1);
                String[] urlStrings = sline.split("url=");
                for (String urlString : urlStrings) {
                    String url = null;
                    urlString = StringEscapeUtils.unescapeJava(urlString);
                    Pattern link2 = Pattern.compile("([^&,]*)[&,]");
                    Matcher linkMatch = link2.matcher(urlString);
                    if (linkMatch.find()) {
                        url = linkMatch.group(1);
                        try {
                            url = URLDecoder.decode(url, "UTF8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    urlList.add(url);
                }
            }
        }

        try {
            return new URL(urlList.get(0));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        return null;
    }


}

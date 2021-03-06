package io.github.nightwolf.restapi.controller.common;

import io.github.nightwolf.restapi.controller.admin.AdminController;
import io.github.nightwolf.restapi.dto.BasicReplyDTO;
import io.github.nightwolf.restapi.dto.DownloadDTO;
import io.github.nightwolf.restapi.dto.DownloadHistoryDTO;
import io.github.nightwolf.restapi.dto.DownloadRequestDTO;
import io.github.nightwolf.restapi.entity.*;
import io.github.nightwolf.restapi.repository.*;
import io.github.nightwolf.restapi.security.SecurityConstants;
import io.github.nightwolf.restapi.service.EmailSenderService;
import io.github.nightwolf.restapi.util.manager.DownloadManager;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    @Qualifier(value = "downloadTypeRepository")
    private DownloadTypeRepository downloadTypeRepository;

    @Autowired
    @Qualifier(value = "confirmationTokenRepository")
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public PublicController() {
        System.out.println(SecurityConstants.RESTRICTED_SITES);
    }

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
                +"10.22.166.122"
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
        DownloadType type = downloadTypeRepository.findByFileType(dir);

        return downloadRepository.findAllByType(type, PageRequest.of(page-1, 1000))
                .stream()
                .map(DownloadHistoryDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("repository/get")
    @ResponseBody
    public ResponseEntity getFileFromRepository(@RequestParam(value = "id", defaultValue = "-1") long fileId, HttpServletRequest request) {
        if(fileId == -1)
            return ResponseEntity.badRequest().build();

        Download download = downloadRepository.findById(fileId).orElse(null);
        if(download == null)
            return ResponseEntity.notFound().build();

        File file = new File(download.getType().getDefaultPath(), download.getName());
        String mimeType = request.getServletContext().getMimeType(file.getAbsolutePath());

        System.out.println(" MIME Type : " + mimeType);

        download.setUsedTimes(download.getUsedTimes() + 1);
        downloadRepository.save(download);

        try {
            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .headers(header)
                    .contentType(MediaType.parseMediaType(mimeType))
                    .contentLength(file.length())
                    .body(resource);


        } catch (IOException e) {
            return ResponseEntity.notFound().build();
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
        String link = downloadRequestDTO.getUrl();
        System.out.println(link);


        if(link == null || link.isEmpty()) {
            return new BasicReplyDTO("Error! Download URL is null!");
        }


        for(String url : SecurityConstants.RESTRICTED_SITES) {
            if(link.contains(url)) {
                return new BasicReplyDTO("Downloading is prohibited from " + url);
            }
        }

        for (DownloadDTO dto : AdminController.TASK_SCHEDULER.getDownloadsQueue()) {
            if(dto.getUrl().toString().equals(link)) {
                return new BasicReplyDTO("Already scheduled for downloading!");
            }
        }

        if(downloadRepository.findByUrl(link) != null ) {
            return new BasicReplyDTO("Already downloaded in repository!");
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

    @GetMapping("download/remove")
    @Transactional
    @ResponseBody
    public BasicReplyDTO removeDownload(@RequestParam("id") String id) {
        System.out.println(id);
        String user_id = (SecurityContextHolder.getContext().getAuthentication().getPrincipal()).toString();
        try {
            DownloadDTO down = AdminController.TASK_SCHEDULER.getDownloadsQueue()
                    .stream()
                    .filter(downloadDTO -> downloadDTO.getId().equals(id))
                    .collect(Collectors.toList()).get(0);

            if (!down.getUserId().equals(user_id)) {
                return new BasicReplyDTO("Failed to delete!");
            }

            boolean bool = AdminController.TASK_SCHEDULER.removeDownload(down);
            if(!bool) {
                return new BasicReplyDTO("Failed to delete!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new BasicReplyDTO("Failed to delete!");
        }

        return new BasicReplyDTO("Deleted!");
    }

    /**
     * @return On going downloads of the user
     */
    //get all downloads of the service (waiting/ongoing downloads)
    @GetMapping("/download/get-all")
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
    @GetMapping("/download/get-percent")
    @ResponseBody
    public double getDownloadPercent(@RequestParam(name = "id", defaultValue = "-1") String id) {
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
        User user = userRepository.findById(id).orElse(null);
        System.out.println(user);
        if(user == null)
            return new ArrayList<>();

        return downloadRepository.findTop25ByUserOrderByDownloadedDateDesc(user)
                .stream()
                .map(DownloadHistoryDTO::new)
                .collect(Collectors.toList());
    }


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
                            url = URLDecoder.decode(url, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    urlList.add(url);
                }
            }
        }

        try {
            if(urlList.get(0) == null) {
                return null;
            }
            return new URL(urlList.get(0));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
        return null;
    }


}
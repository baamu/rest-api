package io.github.nightwolf.restapi.dto;

import io.github.nightwolf.restapi.controller.admin.AdminController;
import io.github.nightwolf.restapi.security.SecurityConstants;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author oshan
 */
public class DownloadDTO implements Runnable{
    private String id;
    private String userId;
    private URL url;
    private double downloadedSize;
    private double fileSize;        //Bytes
    private boolean completed = false;

    private SimpleDateFormat datePattern;

    private String added_date;
    private String downloadedDate;
    private String lastModified;        //set value when the download is added to queue (at download manager)

    private File downloadFile;
    private String fileName;

    private String fileType;

    private String documentPath=SecurityConstants.FILE_DOWNLOAD_PATH;

    private volatile boolean isExit = false;

    {
        datePattern = new SimpleDateFormat("yyyy/MM/dd");
    }

    public DownloadDTO() {
        added_date = datePattern.format(new Date());
    }

    public DownloadDTO(String id) {
        this.id = id;
    }

    public DownloadDTO(String userId, URL url) {
        this();
        this.userId = userId;
        this.url = url;

        try {
            setMetaData();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public DownloadDTO(String userId, URL url, String fileName) {
        this();
        this.userId = userId;
        this.url = url;
        this.fileName = fileName;
        documentPath = AdminController.TASK_SCHEDULER.getDownloadPath("video/");
        try {
            setMetaData();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public DownloadDTO(String id, String userId, URL url) {
        this.id = id;
        this.userId = userId;
        this.url = url;
    }

    public DownloadDTO(String id, String userId, URL url, double fileSize, String added_date, String lastModified, String fileName) {
        this.id = id;
        this.userId = userId;
        this.url = url;
        this.fileSize = fileSize;
        this.added_date = added_date;
        this.lastModified = lastModified;
//        this.fileName = fileName;
//
//        downloadFile = new File(documentPath +File.separator+fileName);
        String d[] = fileName.split("\\.");
        fileType = d[d.length-1];
    }

    private void setMetaData() throws IOException {
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        String disposition = http.getHeaderField("Content-Disposition");
        String contentType = http.getHeaderField("Content-Type");

        System.out.println("Disposition : "+disposition);

        if(fileName == null) {
            if(disposition != null) {
                fileName = disposition.substring(disposition.indexOf("filename=") + 10, disposition.length() - 1);
            } else {
                String[] urlData = url.getFile().split("/");
                fileName = urlData[urlData.length - 1];
            }

            documentPath = AdminController.TASK_SCHEDULER.getDownloadPath(contentType);
        }

        fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "");

        downloadFile = new File(documentPath +File.separator+fileName);

        fileSize = http.getContentLength(); //Bytes

        fileType = contentType.split("/")[1];

//        System.out.println("File Name : "+fileName);
//        System.out.println("File Size : "+ fileSize +"Bytes");
//        System.out.println("File type : "+ fileType);
//
//        System.out.println("Headers ");
//        System.out.println("/////////////////////////");
//
//        for(Map.Entry<String, List<String>> header : http.getHeaderFields().entrySet()) {
//            System.out.print(header.getKey() + " : ");
//            for (String s : header.getValue()) {
//                System.out.print(s + ", ");
//            }
//            System.out.println();
//        }
//
//        System.out.println("///////////////////////////");
        http.disconnect();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public double getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(double downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getAddedDate() {
        return added_date;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadDTO that = (DownloadDTO) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(url);
    }

    @Override
    public void run() {
        try {
//            System.setProperty("http.agent", "Chrome");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.addRequestProperty("User-Agent", "Mozilla/4.76");
            BufferedInputStream inputStream = new BufferedInputStream(http.getInputStream());
            FileOutputStream fout = new FileOutputStream(downloadFile);
            BufferedOutputStream bout = new BufferedOutputStream(fout,1024 * 1024 * 100);
            byte[] buffer = new byte[1024 * 1024 * 5];
            int read = 0;
            int readSize = 0;

            long bytes = downloadFile.getAbsoluteFile().length();

            //check whether the file was partially downloaded before
            if(bytes > 0L) {
                downloadedSize = bytes;
                http.setRequestProperty("Range", "bytes="+bytes+"-");
                http.setRequestProperty("If-Range", lastModified);
            }

            while ( !isExit && (read = inputStream.read(buffer)) != -1) {
                bout.write(buffer,0,read);
                downloadedSize += read;
                readSize += read;

                if(readSize >= 1024 * 1024 * 100)     //flushes data on each 100MB
                {
                    readSize+=0;
                    bout.flush();
                }

                double downloadedPercent = (downloadedSize/fileSize) * 100;
                System.out.println(String.format("Downloaded %.2f/%.2f (MB): %.2f%%",downloadedSize/(1024*1024),fileSize/(1024*1024),downloadedPercent));
            }


            if(isExit) {
                System.out.println("Download Stopped!");
            } else {
                System.out.println(String.format("Downloaded %.2f/%.2f (MB): %.2f%%",downloadedSize/(1024*1024),fileSize/(1024*1024),downloadedSize/fileSize * 100));
                System.out.println("Download completed!");
                completed = true;
            }

            bout.flush();
            bout.close();
            fout.close();
            inputStream.close();
            http.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        isExit = true;
    }
}

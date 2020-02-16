package io.github.nightwolf.restapi.util.manager;

import io.github.nightwolf.restapi.entity.Download;
import io.github.nightwolf.restapi.entity.DownloadType;
import io.github.nightwolf.restapi.repository.DownloadRepository;
import io.github.nightwolf.restapi.repository.DownloadTypeRepository;
import io.github.nightwolf.restapi.security.SecurityConstants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author oshan
 */
public class RepositoryManager {

    private final DownloadRepository downloadRepository;
    private final DownloadTypeRepository downloadTypeRepository;
    private static boolean isCleaning = false;

    private Path repoPath = Paths.get(SecurityConstants.REPOSITORY_BASE_PATH);

    public RepositoryManager(DownloadRepository downloadRepository, DownloadTypeRepository downloadTypeRepository) {
        this.downloadRepository = downloadRepository;
        this.downloadTypeRepository = downloadTypeRepository;
        try {
            System.out.println("Repo Size(MB) : " + getRepositorySize()/(1024*1024));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getRepositorySize() throws IOException {

//        return FileUtils.sizeOfDirectory(new File(SecurityConstants.REPOSITORY_BASE_PATH));

        return Files.walk(repoPath)
                .filter(path -> path.toFile().isFile() && path.toFile().canRead())
                .mapToLong(path->path.toFile().length())
                .sum();
    }

    public boolean deleteFile(long fileId) {
        Download download = downloadRepository.findById(fileId).orElse(null);
        if(download == null)
            return false;

        String path = download.getType().getDefaultPath() + File.separator + download.getName();

        try {
            Files.delete(Paths.get(path));
            downloadRepository.deleteById(download.getId());
        } catch (IOException e) {
            return false;
        }

        return true;
    }


    public void startCleaning() {
        isCleaning = true;
        System.out.println("Start repo cleaning");
    }

    public void stopCleaning() {
        if(!isCleaning) {
            return;
        }

        isCleaning = false;
        System.out.println("Stopping repo cleaning");
    }


}

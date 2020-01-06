package io.github.nightwolf.restapi.util.manager;

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

    private static boolean isCleaning = false;

    //test
    Path repoPath = Paths.get(SecurityConstants.REPOSITORY_BASE_PATH);

    public RepositoryManager() {
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

    public void startCleaning() {
        isCleaning = true;
    }

    public void stopCleaning() {
        if(!isCleaning) {
            return;
        }

        isCleaning = false;
    }


}

package io.github.nightwolf.restapi.repository;

import io.github.nightwolf.restapi.entity.Download;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author oshan
 */

@Repository(value = "downloadRepository")
public interface DownloadRepository extends JpaRepository<Download, Integer> {

}
package io.github.nightwolf.restapi.repository;

import io.github.nightwolf.restapi.entity.Download;
import io.github.nightwolf.restapi.entity.DownloadType;
import io.github.nightwolf.restapi.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author oshan
 */

@Repository(value = "downloadRepository")
public interface DownloadRepository extends JpaRepository<Download, Long> {
    List<Download> findFirst25ByUserOrderByDownloadedDateDesc(User user);
    List<Download> findTop10ByOrderByUsedTimes();
    List<Download> findAllByType(DownloadType type, Pageable pageable);
}

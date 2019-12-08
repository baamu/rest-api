package io.github.nightwolf.restapi.repository;

import io.github.nightwolf.restapi.entity.TempDownload;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author oshan
 */

@Repository(value = "tempDownloadRepository")
public interface TempDownloadRepository extends CrudRepository<TempDownload, Integer> {
    void deleteByUrl(String url);
}

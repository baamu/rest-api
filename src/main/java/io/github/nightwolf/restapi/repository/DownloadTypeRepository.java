package io.github.nightwolf.restapi.repository;

import io.github.nightwolf.restapi.entity.DownloadType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author oshan
 */

@Repository(value = "downloadTypeRepository")
public interface DownloadTypeRepository extends CrudRepository<DownloadType, Integer> {
}

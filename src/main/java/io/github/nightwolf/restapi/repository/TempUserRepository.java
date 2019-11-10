package io.github.nightwolf.restapi.repository;

import io.github.nightwolf.restapi.entity.TempUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author oshan
 */

@Repository(value = "tempUserRepository")
public interface TempUserRepository extends CrudRepository<TempUser, String> {
}

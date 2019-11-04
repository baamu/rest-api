package io.github.nightwolf.restapi.repository;

import io.github.nightwolf.restapi.entity.User;
import org.springframework.data.repository.CrudRepository;

/**
 * @author oshan
 */
public interface UserRepository extends CrudRepository<User, String> {
}

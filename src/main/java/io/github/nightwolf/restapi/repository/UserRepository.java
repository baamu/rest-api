package io.github.nightwolf.restapi.repository;

import io.github.nightwolf.restapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author oshan
 */
@Repository(value = "userRepository")
public interface UserRepository extends JpaRepository<User, String> {


}

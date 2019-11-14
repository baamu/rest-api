package io.github.nightwolf.restapi.repository;

import io.github.nightwolf.restapi.entity.ConfirmationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author oshan
 */

@Repository(value = "confirmationTokenRepository")
public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Integer> {
    ConfirmationToken findByToken(String token);
}

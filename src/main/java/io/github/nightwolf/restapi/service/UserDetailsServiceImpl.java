package io.github.nightwolf.restapi.service;

import io.github.nightwolf.restapi.entity.Role;
import io.github.nightwolf.restapi.entity.User;
import io.github.nightwolf.restapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author oshan
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    @Qualifier(value = "userRepository")
    private  UserRepository userRepo;


    @Override
    @CrossOrigin(origins = "*")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findById(username).orElse(null);
        if(user == null) {
            throw new UsernameNotFoundException(username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>(
                        Collections.singleton(new SimpleGrantedAuthority(user.getRole().getRole()))
                )
        );

    }
}

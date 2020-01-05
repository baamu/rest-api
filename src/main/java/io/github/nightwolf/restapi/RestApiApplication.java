package io.github.nightwolf.restapi;

import io.github.nightwolf.restapi.repository.DownloadRepository;
import io.github.nightwolf.restapi.repository.DownloadTypeRepository;
import io.github.nightwolf.restapi.repository.TempDownloadRepository;
import io.github.nightwolf.restapi.repository.UserRepository;
import io.github.nightwolf.restapi.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableJpaRepositories("io.github.nightwolf.restapi.repository")
@ComponentScan("io.github.nightwolf.restapi")
public class RestApiApplication {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Qualifier(value = "userRepository")
    @Autowired
    UserRepository userRepository;

    @Autowired
    @Qualifier(value = "downloadRepository")
    DownloadRepository downloadRepository;

    @Autowired
    @Qualifier(value = "downloadTypeRepository")
    DownloadTypeRepository downloadTypeRepository;

    @Autowired
    @Qualifier(value = "tempDownloadRepository")
    private TempDownloadRepository tempDownloadRepository;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
    }

}

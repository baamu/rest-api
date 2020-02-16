package io.github.nightwolf.restapi.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author oshan
 */
public class SecurityConstants {
    public static final String SECRET = "SecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/public/user/register";

    public static final String FILE_DOWNLOAD_PATH = "C:\\Users\\oshan\\Desktop\\Downloads\\Documents";
    public static final String REPOSITORY_BASE_PATH = "C:\\Users\\oshan\\Desktop\\Downloads";

    public static String url;

    static {
        try (InputStream input = new FileInputStream("server-settings.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            System.out.println(prop.getProperty("server.location"));
            System.out.println(prop.getProperty("server.secret"));
            System.out.println(prop.getProperty("server.repo.base"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

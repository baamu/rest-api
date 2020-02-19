package io.github.nightwolf.restapi.security;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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

    public static final List<String> RESTRICTED_SITES = new ArrayList<>();

    public static String url;

    static {

        RESTRICTED_SITES.add("mrt.ac.lk");

    }
}

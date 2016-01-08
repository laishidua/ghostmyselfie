package com.laishidua.utils;

/**
 * Class that contains all the Constants required in our GhostMySelfie Upload
 * client App.
 */
public class Constants {

    // use this one for standard emulators
	public static String SERVER_URL_STANDARD = "https://10.0.2.2:8443";
	public static String SERVER_URL_AMAZON = "https://ec2-52-33-3-65.us-west-2.compute.amazonaws.com:8443/ghostmyselfie-server-0.1.0";

	/**
     * URL of the GhostMySelfieWebService.  Please Read the Instructions in
     * README.md to set up the SERVER_URL.
     */
    public static String SERVER_URL = SERVER_URL_AMAZON;

    /**
     * Define a constant for 1 MB.
     */
    public static final long MEGA_BYTE = 1024 * 1024;

    /**
     * Maximum size of GhostMySelfie to be uploaded in MB.
     */
    public static final long MAX_SIZE_MEGA_BYTE = 50 * MEGA_BYTE;
    
    // ugly temporary unsafe place for user/pass
    public static String user;
    public static String pass;
}


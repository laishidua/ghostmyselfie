package com.laishidua.mobilecloud.ghostmyselfie.client;

import java.util.List;

import com.laishidua.mobilecloud.ghostmyselfie.model.User;
import com.laishidua.mobilecloud.ghostmyselfie.model.UserCredentialsStatus;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

public interface UserSvcApi {
	
	public static final String ROLE_PATH = "/role";	
	
	public static final String INSECURE_PATH = "/insecure";
	
	public static final String ID_PARAMETER = "id";

	public static final String USER_ID_PARAMETER = "userid";

	public static final String PASSWORD_PARAMETER = "password";

	public static final String USERNAME_PARAMETER = "username";

	public static final String EMAIL_PARAMETER = "email";
	
	public static final String TOKEN_PATH = "/oauth/token";
	
	public static final String USER_SVC_PATH = "/user";
	
	public static final String USER_COUNTALL_PATH = USER_SVC_PATH + "/countall";
	
	public static final String USER_ADD_NEW_PATH = INSECURE_PATH + USER_SVC_PATH;

	public static final String USER_NAME_SEARCH_PATH = USER_SVC_PATH + "/search/findByName";

	//We get username as email.
	public static final String USER_CHECK_CREDENTIALS_PATH = INSECURE_PATH + USER_SVC_PATH + "/check";	
	
	public static final String USER_USERNAME_AND_PASSWORD_SEARCH_PATH = USER_SVC_PATH + "/login";
	
	@POST(USER_ADD_NEW_PATH)
	public User addUser(@Body User u);
	
	@GET(USER_SVC_PATH)
	public List<User> getUserList();
	
	@POST(USER_CHECK_CREDENTIALS_PATH)
	public UserCredentialsStatus checkCredentialsState(@Body User u);
	
	@FormUrlEncoded
	@POST(USER_USERNAME_AND_PASSWORD_SEARCH_PATH)
	public User login(@Field(USERNAME_PARAMETER) String username, @Field(PASSWORD_PARAMETER) String password);
	
}

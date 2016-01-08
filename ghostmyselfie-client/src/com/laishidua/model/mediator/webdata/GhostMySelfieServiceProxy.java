package com.laishidua.model.mediator.webdata;

import java.util.Collection;


import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Streaming;
import retrofit.mime.TypedFile;

/**
 * This interface defines an API for a GhostMySelfie Service web service.  The
 * interface is used to provide a contract for client/server
 * interactions.  The interface is annotated with Retrofit annotations
 * to send Requests and automatically convert the GhostMySelfie.
 */
public interface GhostMySelfieServiceProxy {
    /**
     * Used as Request Parameter for GhostMySelfie data.
     */
    public static final String DATA_PARAMETER = "data";

    /**
     * Used as Request Parameter for GhostMySelfieId.
     */
    public static final String ID_PARAMETER = "id";

    /**
     * Used as Request Parameter for the user rating.
     */
	public static final String RATING_PARAMETER = "vote";

    /**
     * The path where we expect the VideoSvc to live.
     */
    public static final String GHOSTMYSELFIE_SVC_PATH = "/ghostmyselfie";
	
    /**
     * The path where we expect the GhostMySelfieSvc to live.
     */
    public static final String GHOSTMYSELFIE_DATA_PATH = 
        GHOSTMYSELFIE_SVC_PATH 
        + "/{"
        + GhostMySelfieServiceProxy.ID_PARAMETER
        + "}/data";

	public static final String GHOSTMYSELFIE_RATING_PATH =
		GHOSTMYSELFIE_SVC_PATH
		+ "/{id}/rating/{vote}";

	public static final String TOKEN_PATH = "/oauth/token";
	
	public static final String INSECURE_PATH = "/insecure";	
	
	public static final String USER_SVC_PATH = "/user";	
	
	public static final String USER_ADD_NEW_PATH = INSECURE_PATH + USER_SVC_PATH;
	
	public static final String USER_CHECK_CREDENTIALS_PATH = INSECURE_PATH + USER_SVC_PATH + "/check";
	
	public static final String USERNAME_PARAMETER = "username";

	public static final String EMAIL_PARAMETER = "email";	

    /**
     * Sends a GET request to get the List of Selfies from GhostMySelfie
     * Web service using a two-way Retrofit RPC call.
     */
    @GET(GHOSTMYSELFIE_SVC_PATH)
    public Collection<GhostMySelfie> getGhostMySelfieList();
    
	/**
	 * Check if connection client - server is established.
	 * 
	 * @return
	 */	
	@GET(GHOSTMYSELFIE_SVC_PATH + "/test")
	public Integer conectionEstablished();  
	
	/**
	 * Delete GhostMySelfieById from server.
	 * 
	 * @return
	 */	
	@GET(GHOSTMYSELFIE_SVC_PATH + "/delete/{id}")
	public int deleteGhostMySelfieById(@Path("id") long id);	
    
    /**
     * Sends a POST request to add the GhostMySelfie metadata to the GhostMySelfie 
     * Web service using a two-way Retrofit RPC call.
     *
     * @param ghostMySelfie meta-data
     * @return Updated selfie meta-data returned from the GhostMySelfie Service.
     */
    @POST(GHOSTMYSELFIE_SVC_PATH)
    public GhostMySelfie addGhostMySelfie(@Body GhostMySelfie ghostMySelfie);
	
    /**
     * Sends a POST request to Upload the GhostMySelfie data to the GhostMySelfie Web
     * service using a two-way Retrofit RPC call.  @Multipart is used
     * to transfer multiple content (i.e. several files in case of a
     * file upload to a server) within one request entity.  When doing
     * so, a REST client can save the overhead of sending a sequence
     * of single requests to the server, thereby reducing network
     * latency.
     * 
     * @param id
     * @param ghostmyselfieData
     * @return ghostmyselfieStatus indicating status of the uploaded selfie.
     */
    @Multipart
    @POST(GHOSTMYSELFIE_DATA_PATH)
    public GhostMySelfieStatus setGhostMySelfieData(@Path(ID_PARAMETER) long id,
                                    @Part(DATA_PARAMETER) TypedFile ghostmyselfieData);
	
    /**
     * This method uses Retrofit's @Streaming annotation to indicate
     * that the method is going to access a large stream of data
     * (e.g., the mpeg selfie data on the server).  The client can
     * access this stream of data by obtaining an InputStream from the
     * Response as shown below:
     * 
     * GhostMySelfieServiceProxy client = ... // use retrofit to create the client
     * Response response = client.getData(someGhostMySelfieId); 
     * 
     * @param id
     * @return Response which contains the actual GhostMySelfie data.
     */
    @Streaming
    @GET(GHOSTMYSELFIE_DATA_PATH)
    Response getGhostMySelfieData(@Path(ID_PARAMETER) long id);

	@POST(GHOSTMYSELFIE_RATING_PATH)
	public AverageGhostMySelfieRating rateGhostMySelfie(@Path(ID_PARAMETER) long id, @Path(RATING_PARAMETER) long rating);

	@GET(GHOSTMYSELFIE_RATING_PATH)
	public GhostMySelfie getGhostMySelfie(@Path(ID_PARAMETER) long id);
	
	@POST(USER_ADD_NEW_PATH)
	public User addUser(@Body User u);
	
	@POST(USER_CHECK_CREDENTIALS_PATH)
	public UserCredentialsStatus checkCredentialsState(@Body User u);	

}

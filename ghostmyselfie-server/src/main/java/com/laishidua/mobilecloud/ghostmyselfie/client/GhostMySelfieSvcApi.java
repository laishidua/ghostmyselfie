package com.laishidua.mobilecloud.ghostmyselfie.client;

import java.util.Collection;

import com.laishidua.mobilecloud.ghostmyselfie.model.AverageGhostMySelfieRating;
import com.laishidua.mobilecloud.ghostmyselfie.model.GhostMySelfie;
import com.laishidua.mobilecloud.ghostmyselfie.model.GhostMySelfieStatus;

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
 * This interface defines an API for a GhostMySelfieSvc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 * 
 * 
 * @author jules
 *
 */
public interface GhostMySelfieSvcApi {

	public static final String DATA_PARAMETER = "data";

	public static final String ID_PARAMETER = "id";

	public static final String TOKEN_PATH = "/oauth/token";

	public static final String RATING_PARAMETER = "rating";

	// The path where we expect the GhostMySelfieSvc to live
	public static final String GHOSTMYSELFIE_SVC_PATH = "/ghostmyselfie";

	public static final String GHOSTMYSELFIE_DATA_PATH = GHOSTMYSELFIE_SVC_PATH + "/{"+ GhostMySelfieSvcApi.ID_PARAMETER+"}/data";

	public static final String GHOSTMYSELFIE_GET_RATING_PATH = GHOSTMYSELFIE_SVC_PATH
			+ "/{" + GhostMySelfieSvcApi.ID_PARAMETER + "}/" + RATING_PARAMETER;

	public static final String GHOSTMYSELFIE_RATING_PATH = GHOSTMYSELFIE_GET_RATING_PATH + "/{" + RATING_PARAMETER + "}";

	
	@GET(GHOSTMYSELFIE_SVC_PATH)
	public Collection<GhostMySelfie> getGhostMySelfieList();
	
	@GET(GHOSTMYSELFIE_SVC_PATH + "/test")
	public Integer conectionEstablished();	
	
	@GET(GHOSTMYSELFIE_SVC_PATH + "/{id}")
	public GhostMySelfie getGhostMySelfieById(@Path("id") long id);
	
	@GET(GHOSTMYSELFIE_SVC_PATH + "/delete/{id}")
	public int deleteGhostMySelfieById(@Path("id") long id);	
	
	@POST(GHOSTMYSELFIE_SVC_PATH)
	public GhostMySelfie addGhostMySelfie(@Body GhostMySelfie gms);
	
	@POST(GHOSTMYSELFIE_SVC_PATH+"/{id}/rating/{rating}")
	public AverageGhostMySelfieRating rateGhostMySelfie(@Path("id") long id, @Path("rating") int rating);
	
	@GET(GHOSTMYSELFIE_SVC_PATH+"/{id}/rating")
	public AverageGhostMySelfieRating getGhostMySelfieRating(@Path("id") long id);
	
	@Multipart
	@POST(GHOSTMYSELFIE_DATA_PATH)
	public GhostMySelfieStatus setGhostMySelfieData(@Path(ID_PARAMETER) long id, @Part(DATA_PARAMETER) TypedFile ghostmyselfieData);
	
	/**
	 * This method uses Retrofit's @Streaming annotation to indicate that the
	 * method is going to access a large stream of data (e.g., the mpeg GhostMySelfie 
	 * data on the server). The client can access this stream of data by obtaining
	 * an InputStream from the Response as shown below:
	 * 
	 * GhostMySelfieSvcApi client = ... // use retrofit to create the client
	 * Response response = client.getData(someGhostMySelfieId);
	 * InputStream GhostMySelfieDataStream = response.getBody().in();
	 * 
	 * @param id
	 * @return
	 */
	@Streaming
    @GET(GHOSTMYSELFIE_DATA_PATH)
    Response getGhostMySelfieData(@Path(ID_PARAMETER) long id);
	
}

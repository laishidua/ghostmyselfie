/*
 * based on the assignment2 GhostMySelfieSvcApi which is...
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.laishidua.model.services;

/**
 *                       MODIFY THIS INTERFACE
      ___           ___                                     ___                            
     /\  \         /\  \         _____                     /\__\                        
    |::\  \       /::\  \       /::\  \       ___         /:/ _/_         ___           
    |:|:\  \     /:/\:\  \     /:/\:\  \     /\__\       /:/ /\__\       /|  |          
  __|:|\:\  \   /:/  \:\  \   /:/  \:\__\   /:/__/      /:/ /:/  /      |:|  |          
 /::::|_\:\__\ /:/__/ \:\__\ /:/__/ \:|__| /::\  \     /:/_/:/  /       |:|  |          
 \:\~~\  \/__/ \:\  \ /:/  / \:\  \ /:/  / \/\:\  \__  \:\/:/  /      __|:|__|          
  \:\  \        \:\  /:/  /   \:\  /:/  /   ~~\:\/\__\  \::/__/      /::::\  \          
   \:\  \        \:\/:/  /     \:\/:/  /       \::/  /   \:\  \      ~~~~\:\  \         
    \:\__\        \::/  /       \::/  /        /:/  /     \:\__\          \:\__\        
     \/__/         \/__/         \/__/         \/__/       \/__/           \/__/        
 */
import java.util.Collection;

import com.laishidua.model.mediator.webdata.AverageGhostMySelfieRating;
import com.laishidua.model.mediator.webdata.GhostMySelfie;
import com.laishidua.model.mediator.webdata.GhostMySelfieStatus;

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
 * interface into a client capable of sending the appropriate
 * HTTP requests.
 * 
 * The HTTP API that you must implement so that this interface
 * will work:
 * 
 * GET /ghostmyselfie
 *   - Returns the list of selfies that have been added to the
 *     server as JSON. The list of selfies does not have to be
 *     persisted across restarts of the server. The list of
 *     GhostMySelfie objects should be able to be unmarshalled by the
 *     client into a Collection<GhostMySelfie>.
 *     
 * POST /ghostmyselfie
 *   - The selfie data is provided as an application/json request
 *     body. The JSON should generate a valid instance of the 
 *     GhostMySelfie class when deserialized by Spring's default 
 *     Jackson library.
 *   - Returns the JSON representation of the GhostMySelfie object that
 *     was stored along with any updates to that object. 
 *     --The server should generate a unique identifier for the GhostMySelfie
 *     object and assign it to the GhostMySelfie by calling its setId(...)
 *     method. The returned GhostMySelfie JSON should include this server-generated
 *     identifier so that the client can refer to it when uploading the
 *     binary mpeg selfie content for the GhostMySelfie.
 *    -- The server should also generate a "data url" for the
 *     GhostMySelfie. The "data url" is the url of the binary data for a
 *     GhostMySelfie (e.g., the raw mpeg data). The URL should be the *full* URL
 *     for the selfie and not just the path. You can use a method like the
 *     following to figure out the name of your server:
 *     
 *     	private String getUrlBaseForLocalServer() {
 *		   HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
 *		   String base = "http://"+request.getServerName()+((request.getServerPort() != 80) ? ":"+request.getServerPort() : "");
 *		   return base;
 *		}
 *     
 * POST /ghostmyselfie/{id}/data
 *   - The binary mpeg data for the selfie should be provided in a multipart
 *     request as a part with the key "data". The id in the path should be
 *     replaced with the unique identifier generated by the server for the
 *     GhostMySelfie. A client MUST *create* a GhostMySelfie first by sending a POST to /ghostmyselfie
 *     and getting the identifier for the newly created GhostMySelfie object before
 *     sending a POST to /ghostmyselfie/{id}/data. 
 *     
 * GET /ghostmyselfie/{id}/data
 *   - Returns the binary mpeg data (if any) for the selfie with the given
 *     identifier. If no mpeg data has been uploaded for the specified selfie,
 *     then the server should return a 404 status code.
 *     
 *     
 * The GhostMySelfieSvcApi interface described below should be used as the ultimate ground
 * truth for what should be implemented in the assignment. If there are any details
 * in the description above that conflict with the GhostMySelfieSvcApi interface below, use
 * the details in the GhostMySelfieSvcApi interface and report the discrepancy on the course
 * forums. 
 * 
 * For the ultimate ground truth of how the assignment will be graded, please see 
 * AutoGradingTest, which shows the specific tests that will be run to grade your
 * solution. 
 *   
 * @author jules
 *
 */
public interface GhostMySelfieSvcApi {

	public static final String DATA_PARAMETER = "data";

	public static final String ID_PARAMETER = "id";

	public static final String RATING_PARAMETER = "vote";

	public static final String GHOSTMYSELFIE_SVC_PATH = "/ghostmyselfie";
	
	public static final String GHOSTMYSELFIE_DATA_PATH = GHOSTMYSELFIE_SVC_PATH + "/{id}/data";

	public static final String GHOSTMYSELFIE_RATING_PATH = GHOSTMYSELFIE_SVC_PATH + "/{id}/voting/{vote}";

	/**
	 * This endpoint in the API returns a list of the selfies that have
	 * been added to the server. The GhostMySelfie objects should be returned as
	 * JSON. 
	 * 
	 * To manually test this endpoint, run your server and open this URL in a browser:
	 * http://localhost:8080/ghostmyselfie
	 * 
	 * @return
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
	 * This endpoint allows clients to add GhostMySelfie objects by sending POST requests
	 * that have an application/json body containing the GhostMySelfie object information. 
	 * 
	 * @return
	 */
	@POST(GHOSTMYSELFIE_SVC_PATH)
	public GhostMySelfie addGhostMySelfie(@Body GhostMySelfie v);
	
	/**
	 * This endpoint allows clients to set the mpeg selfie data for previously
	 * added GhostMySelfie objects by sending multipart POST requests to the server.
	 * The URL that the POST requests should be sent to includes the ID of the
	 * GhostMySelfie that the data should be associated with (e.g., replace {id} in
	 * the url /ghostmyselfie/{id}/data with a valid ID of a ghostmyselfie, such as /ghostmyselfie/1/data
	 * -- assuming that "1" is a valid ID of a ghostmyselfie). 
	 * 
	 * @return
	 */
	@Multipart
	@POST(GHOSTMYSELFIE_DATA_PATH)
	public GhostMySelfieStatus setGhostMySelfieData(@Path(ID_PARAMETER) long id, @Part(DATA_PARAMETER) TypedFile ghostmyselfieData);
	
	/**
	 * This endpoint should return the selfie data that has been associated with
	 * a GhostMySelfie object or a 404 if no selfie data has been set yet. The URL scheme
	 * is the same as in the method above and assumes that the client knows the ID
	 * of the GhostMySelfie object that it would like to retrieve selfie data for.
	 * 
	 * This method uses Retrofit's @Streaming annotation to indicate that the
	 * method is going to access a large stream of data (e.g., the mpeg selfie 
	 * data on the server). The client can access this stream of data by obtaining
	 * an InputStream from the Response as shown below:
	 * 
	 * GhostMySelfieSvcApi client = ... // use retrofit to create the client
	 * Response response = client.getData(someGhostMySelfieId);
	 * 
	 * @param id
	 * @return
	 */
	@Streaming
    @GET(GHOSTMYSELFIE_DATA_PATH)
    Response getGhostMySelfieData(@Path(ID_PARAMETER) long id);
	
	/**
	 * This endpoint allows clients to add GhostMySelfie objects by sending POST requests
	 * that have an application/json body containing the GhostMySelfie object information. 
	 * 
	 * @return
	 */
	@POST(GHOSTMYSELFIE_RATING_PATH)
	//public GhostMySelfie rateGhostMySelfie(@Path(ID_PARAMETER) long id, @Path(RATING_PARAMETER) long rating);
	public AverageGhostMySelfieRating rateGhostMySelfie(@Path(ID_PARAMETER) long id, @Path(RATING_PARAMETER) long rating);

	
	// for quick tests via browser :)
	@GET(GHOSTMYSELFIE_RATING_PATH)
	public GhostMySelfie rateGhostMySelfieByGet(@Path(ID_PARAMETER) long id, @Path(RATING_PARAMETER) long rating);

}

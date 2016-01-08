/*
 * derived from AnEmptyController.java which is
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
package com.laishidua.mobilecloud.ghostmyselfie.controller;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Files;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.laishidua.mobilecloud.ghostmyselfie.client.GhostMySelfieSvcApi;
import com.laishidua.mobilecloud.ghostmyselfie.model.AverageGhostMySelfieRating;
import com.laishidua.mobilecloud.ghostmyselfie.model.GhostMySelfie;
import com.laishidua.mobilecloud.ghostmyselfie.model.GhostMySelfieRepository;
import com.laishidua.mobilecloud.ghostmyselfie.model.GhostMySelfieStatus;

@Controller
public class GhostMySelfieController {
	
	@Autowired
	private GhostMySelfieRepository ghostMySelphie;
    
	private GhostMySelfieFileManager ghostMySelfieDataMgr;

  	public void saveSomeGhostMySelfie(GhostMySelfie gms, MultipartFile ghostMySelfieData) throws IOException {
  		ghostMySelfieDataMgr.saveGhostMySelfieData(gms, ghostMySelfieData.getInputStream());
  	}
  	
  	public void serveSomeGhostMySelfie(GhostMySelfie gms, HttpServletResponse response) throws IOException {
  	     // Of course, you would need to send some headers, etc. to the
  	     // client too!
  	     //  ...
  	     ghostMySelfieDataMgr.copyGhostMySelfieData(gms, response.getOutputStream());
  	     // okay, okay, it already passes the tests as is, but let's
  	     // pretend we're being real...
  	     response.setContentType(gms.getContentType());
  	     // ...could add more, but we get the idea :)
  	}

 	/**
 	 * Adds a vote to the collection of votes for a Selfie.
 	 * 
 	 * @param gms	the ghostmyselfie
 	 * @param name	the user who cast the vote
 	 * @param vote	the vote
 	 */
 	private void addVote(GhostMySelfie gms, String name, long vote) {
 		// In a real use case, such operations should probably
 		// be protected by an EntityManager transaction
 		gms.setTotalVote(gms.getTotalVote() + vote);
		gms.setVotes(gms.getVotes() + 1);
		gms.getRatings().put(name, (Long) vote); 
 		ghostMySelphie.save(gms);
 		
	}

 	/**
 	 * Updates a user's vote for a Selfie.
 	 * 
 	 * @param gms		: the ghostmyselfie
 	 * @param name	: the user who cast the vote
 	 * @param vote	: the vote
 	 */
 	private void updateVote(GhostMySelfie gms, String name, long vote) {
 		// In a real use case, such operations should probably
 		// be protected by an EntityManager transaction
 		Long oldVote = gms.getRatings().get(name);
 		gms.setTotalVote(gms.getTotalVote() - oldVote + vote);
		gms.getRatings().put(name, (Long) vote); 
 		ghostMySelphie.save(gms);
 		
	}

 	/**
 	 * Updates the non-key properties of an existing Selfie with the new ones; used when the posted Selfie already exists according to its hashcode.
 	 * 
 	 * @param gmsOld : the already existing ghostmyselfie data
 	 * @param vNew : the posted ghostmyselfie data
 	 */
 	private void updateGhostMySelfie(GhostMySelfie gmsOld, GhostMySelfie vNew) {
 		gmsOld.setContentType(vNew.getContentType());
 		gmsOld.setLocation(vNew.getLocation());
 		gmsOld.setFilters(vNew.getFilters());
	}

 	
 	/**
 	 * 
 	 * Serves the Selfie list from a user, leaving the heavy lifting to Spring.
 	 * 
 	 * @return ...the ghostmyselfie list, of course!
 	 */
	@RequestMapping(value=GhostMySelfieSvcApi.GHOSTMYSELFIE_SVC_PATH, method=RequestMethod.GET)
	public @ResponseBody List<GhostMySelfie> getGhostMySelfieList(Principal p){
		return Lists.newArrayList(ghostMySelphie.findByOwner(p.getName()));
	}
	
 	/**
 	 * 
 	 * Check connection to client - server is established
 	 * 
 	 * @return ...the ghostmyselfie list, of course!
 	 */
	@RequestMapping(value=GhostMySelfieSvcApi.GHOSTMYSELFIE_SVC_PATH + "/test", method=RequestMethod.GET)
	public @ResponseBody Integer conectionEstablished(){
		return 1;
	}	
	
	/**
	 * Provides the average ghostmyselfie rating for a ghostmyselfie
	 * 
	 * @param id		: id of the ghostmyselfie to retrieve
	 * @param response	: http response, exposed to allow manipulation like setting
	 * 						error codes
	 * @return			: an AverageGhostMySelfieRating object
	 * @throws IOException
	 */
	@RequestMapping(value=GhostMySelfieSvcApi.GHOSTMYSELFIE_SVC_PATH + "/delete/{id}", method=RequestMethod.GET)
	public @ResponseBody Integer deleteGhostMySelfieById(
			@PathVariable(GhostMySelfieSvcApi.ID_PARAMETER) long id,
			Principal p,
            HttpServletResponse response) throws IOException {
		GhostMySelfie gms1 = ghostMySelphie.findOne(id);
		// let's see if the ghostmyselfie exists already, according to its hashcode
		GhostMySelfie gms2 = ghostMySelphie.findExactGhostMySelfieById(
				id, p.getName());
		if (gms1 != null && gms2 != null) {
			try{
			  Path selfie = ghostMySelfieDataMgr.getGhostMySelfiePath(gms1);
			  Files.delete(selfie);
			} catch (Exception e) {e.printStackTrace();}
			ghostMySelphie.delete(gms1);
			return 1;
		} else if (gms1 != null && gms2 == null) {
			response.sendError(401, "Sorry, your cannot delete this Selfie because you are not the owner.");
			return 0;
		} else {
			response.sendError(404, "Sorry, your ghostmyselfie is in another castle.");
			return 0;
		}
	}	

	
 	/**
 	 * 
 	 * adds the incoming ghostmyselfie metadata to the list, attributing them a proper id
 	 * 
 	 * @param gms : ghostmyselfie metadata to add to the list
 	 * @return  : the updated ghostmyselfie metadata
 	 */
	@RequestMapping(value=GhostMySelfieSvcApi.GHOSTMYSELFIE_SVC_PATH, method=RequestMethod.POST)
	public ResponseEntity<GhostMySelfie> addGhostMySelfie(@RequestBody GhostMySelfie gms, Principal p){
		
		// let's see if the ghostmyselfie exists already, according to its hashcode
		GhostMySelfie gmsOld = ghostMySelphie.findExactGhostMySelfie(
				gms.getTitle(), p.getName());
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		if (null != gmsOld) { // updating the existing ghostmyselfie
			updateGhostMySelfie(gmsOld, gms);
			ghostMySelphie.save(gmsOld);
			return new ResponseEntity<GhostMySelfie>(gmsOld, responseHeaders, HttpStatus.CREATED);
		}
		else { // saving a new ghostmyselfie
			gms.setOwner(p.getName());
			ghostMySelphie.save(gms);
			return new ResponseEntity<GhostMySelfie>(ghostMySelphie.findOne(gms.getId()), HttpStatus.OK);
		}
	}

	
	/**
	 * 
	 * @param id 		: the id of the ghostmyselfie to associate this data stream with
	 * @param ghostMyselfieData : the data stream
	 * @param response	: http response, exposed to allow manipulation like setting
	 * 						error codes
	 * @return			: a GhostMySelfieStatus object if successful, null otherwise
	 * @throws IOException
	 */
	@RequestMapping(value=GhostMySelfieSvcApi.GHOSTMYSELFIE_DATA_PATH, method=RequestMethod.POST)
	public ResponseEntity<GhostMySelfieStatus> setGhostMySelfieData(
            @PathVariable(GhostMySelfieSvcApi.ID_PARAMETER) long id,
            @RequestPart(GhostMySelfieSvcApi.DATA_PARAMETER) 
                                  MultipartFile ghostMySelfieData,
            HttpServletResponse response, Principal p) throws IOException {
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.setContentType(MediaType.APPLICATION_JSON);		
		GhostMySelfie gms = ghostMySelphie.findOne(id);
		if (gms != null) {
			if (p.getName().equals(gms.getOwner())) {
				String mimeType = URLConnection.guessContentTypeFromStream(ghostMySelfieData.getInputStream());		
				if (!mimeType.equals("image/jpeg") && !mimeType.equals("image/png")) {
					response.sendError(400, "Just jpg, jpeg and png images supported");
					return null;
				}
				ghostMySelfieDataMgr = GhostMySelfieFileManager.get();
				saveSomeGhostMySelfie(gms, ghostMySelfieData);
				return new ResponseEntity<GhostMySelfieStatus>(new GhostMySelfieStatus(GhostMySelfieStatus.GhostMySelfieState.READY), responseHeaders, HttpStatus.CREATED);
			}
			else {
				response.sendError(400, "Not your Selfie, hands off!");
				return null;
			}
		}
		else {
			response.sendError(404, "Your Selfie is in another castle.");
			return null;
		}
	}

	
	/**
	 * 
	 * serves the ghostmyselfie corresponding to the provided id (if it exists) 
	 * 
	 * @param id		: id of the ghostmyselfie to retrieve
	 * @param response	: http response, exposed to allow manipulation like setting
	 * 						error codes
	 * @throws IOException
	 */
	@RequestMapping(value=GhostMySelfieSvcApi.GHOSTMYSELFIE_DATA_PATH, method=RequestMethod.GET)
	public void getGhostMySelfieData(
            @PathVariable(GhostMySelfieSvcApi.ID_PARAMETER) long id,
            HttpServletResponse response) throws IOException {
		
		GhostMySelfie gms = ghostMySelphie.findOne(id);
		if (gms != null) {
			ghostMySelfieDataMgr = GhostMySelfieFileManager.get();
			serveSomeGhostMySelfie(gms, response);
		}
		else {
			response.sendError(404, "Sorry, your ghostmyselfie is in another castle :)");
		}
	}

	/**
	 * Provides the average ghostmyselfie rating for a ghostmyselfie
	 * 
	 * @param id		: id of the ghostmyselfie to retrieve
	 * @param response	: http response, exposed to allow manipulation like setting
	 * 						error codes
	 * @return			: an AverageGhostMySelfieRating object
	 * @throws IOException
	 */
	@RequestMapping(value=GhostMySelfieSvcApi.GHOSTMYSELFIE_GET_RATING_PATH, method=RequestMethod.GET)
	public @ResponseBody AverageGhostMySelfieRating getGhostMySelfieRating(
			@PathVariable(GhostMySelfieSvcApi.ID_PARAMETER) long id,
            HttpServletResponse response) throws IOException {
		GhostMySelfie gms = ghostMySelphie.findOne(id);
		if (gms != null) {
			return new AverageGhostMySelfieRating(gms.getAverageVote(), id, (int) gms.getVotes());
		}
		else {
			response.sendError(404, "Sorry, your ghostmyselfie is in another castle :)");
			return null;
		}
	}

	
	/**
	 * Method to rate a selfie, only a a selfie is rated when rate corresponds to its user owner.
	 * @param id 		: the id of the ghostmyselfie to rate
	 * @param rating    : the user's vote
	 * @param response	: http response, exposed to allow manipulation like setting
	 * 						error codes
	 * @return			: the updated GhostMySelfie metadata if successful, null otherwise
	 * @throws IOException
	 */
	@RequestMapping(value=GhostMySelfieSvcApi.GHOSTMYSELFIE_RATING_PATH, method=RequestMethod.POST)
	public @ResponseBody AverageGhostMySelfieRating rateGhostMySelfie(
            @PathVariable(GhostMySelfieSvcApi.ID_PARAMETER) long id,
            @PathVariable(GhostMySelfieSvcApi.RATING_PARAMETER) int rating,
            HttpServletResponse response, Principal p) throws IOException {
		
		System.out.println("rating id: " + id);
		GhostMySelfie gms = ghostMySelphie.findOne(id);
		if (gms != null) {
			if (gms.getRatings().containsKey(p.getName())) {

				System.out.println("updating vote to: " + rating);
				updateVote(gms, p.getName(), rating);
				return new AverageGhostMySelfieRating(gms.getAverageVote(), id, (int) gms.getVotes());
			}
			else {

				System.out.println("adding vote: " + rating);
				addVote(gms, p.getName(), rating);
				return new AverageGhostMySelfieRating(gms.getAverageVote(), id, (int) gms.getVotes());
			}
		}
		else {
			response.sendError(404, "Sorry, your ghostmyselfie is in another castle :)");
			return null;
		}
	}
	
}

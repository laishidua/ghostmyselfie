package com.laishidua.mobilecloud.ghostmyselfie;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laishidua.mobilecloud.ghostmyselfie.model.GhostMySelfie;

/**
 * This is a utility class to aid in the construction of
 * GhostMySelfie objects with random names, urls, and durations.
 * The class also provides a facility to convert objects
 * into JSON using Jackson, which is the format that the
 * GhostMySelfieSvc controller is going to expect data in for
 * integration testing.
 * 
 * @author jules
 *
 */
public class TestData {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Construct and return a GhostMySelfie object with a
	 * rnadom name, url, and duration.
	 * 
	 * @return
	 */
	public static GhostMySelfie randomGhostMySelfie() {
		// Information about the selfie
		// Construct a random identifier using Java's UUID class
		String id = UUID.randomUUID().toString();
		String title = "GhostMySelfie-"+id;
		GhostMySelfie gms = new GhostMySelfie(null, title, 0, null);
		List<String> filters =  new ArrayList<String>();
		filters.add("ghost");
		filters.add("blur");
		filters.add("gray");
		filters.add("dark");
		gms.setFilters(filters);
		return gms;
	}		
	
	/**
	 *  Convert an object to JSON using Jackson's ObjectMapper
	 *  
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public static String toJson(Object o) throws Exception{
		return objectMapper.writeValueAsString(o);
	}
}

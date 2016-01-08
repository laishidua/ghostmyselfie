package com.laishidua.mobilecloud.integration.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import com.google.gson.JsonObject;
import com.laishidua.mobilecloud.ghostmyselfie.TestData;
import com.laishidua.mobilecloud.ghostmyselfie.client.SecuredRestBuilder;
import com.laishidua.mobilecloud.ghostmyselfie.client.SecuredRestException;
import com.laishidua.mobilecloud.ghostmyselfie.client.GhostMySelfieSvcApi;
import com.laishidua.mobilecloud.ghostmyselfie.client.UserSvcApi;
import com.laishidua.mobilecloud.ghostmyselfie.model.AverageGhostMySelfieRating;
import com.laishidua.mobilecloud.ghostmyselfie.model.GhostMySelfie;
import com.laishidua.mobilecloud.ghostmyselfie.model.GhostMySelfieStatus;
import com.laishidua.mobilecloud.ghostmyselfie.model.User;
import com.laishidua.mobilecloud.ghostmyselfie.model.GhostMySelfieStatus.GhostMySelfieState;

/**
 * 
 * The test requires that the GhostMySelfieSvc be running first (see the directions in
 * the README.md file for how to launch the Application).
 * 
 * To run this test, right-click on it in Eclipse and select
 * "Run As"->"JUnit Test"
 * 
 * 
 * @author jules
 *
 */
public class GhostMySelfieSvcClientApiTest {

	private final String USERNAME = "host@ghostmyselfie.com";
	private final String PASSWORD = "1q2w3e4r5t6y";
	private final String USERNAME2 = "user0@gmail.com";
	private final String PASSWORD2 = "pass";
	private final String CLIENT_ID = "mobile";
	private final String READ_ONLY_CLIENT_ID = "mobileReader";

	//private final String TEST_URL = "https://ec2-52-33-3-65.us-west-2.compute.amazonaws.com:8443/ghostmyselfie-server-0.1.0";	
	//private final String TEST_URL = "https://localhost:8443/ghostmyselfie-server-0.1.0";
	private final String TEST_URL = "https://localhost:8443";

	private File testGhostMySelfieData = new File("src/test/resources/lanix.jpg");

	private UserSvcApi userSvc = new SecuredRestBuilder()
	.setLoginEndpoint(TEST_URL + UserSvcApi.TOKEN_PATH)
	.setUsername(USERNAME)
	.setPassword(PASSWORD)
	.setClientId(CLIENT_ID)
	.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
	.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
	.create(UserSvcApi.class);	
	
	private GhostMySelfieSvcApi GhostMySelfieSvc = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL + GhostMySelfieSvcApi.TOKEN_PATH)
			.setUsername(USERNAME)
			.setPassword(PASSWORD)
			.setClientId(CLIENT_ID)
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
			.create(GhostMySelfieSvcApi.class);

	private GhostMySelfieSvcApi GhostMySelfieSvc2 = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL + GhostMySelfieSvcApi.TOKEN_PATH)
			.setUsername(USERNAME2)
			.setPassword(PASSWORD2)
			.setClientId(CLIENT_ID)
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
			.create(GhostMySelfieSvcApi.class);

	private GhostMySelfieSvcApi readOnlyGhostMySelfieService = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL + GhostMySelfieSvcApi.TOKEN_PATH)
			.setUsername(USERNAME)
			.setPassword(PASSWORD)
			.setClientId(READ_ONLY_CLIENT_ID)
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
			.create(GhostMySelfieSvcApi.class);

	private GhostMySelfieSvcApi invalidClientGhostMySelfieService = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL + GhostMySelfieSvcApi.TOKEN_PATH)
			.setUsername(UUID.randomUUID().toString())
			.setPassword(UUID.randomUUID().toString())
			.setClientId(UUID.randomUUID().toString())
			.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
			.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
			.create(GhostMySelfieSvcApi.class);

	private GhostMySelfie ghostMySelfie = TestData.randomGhostMySelfie();

	/**
	 * This test creates a GhostMySelfie, adds the GhostMySelfie to the GhostMySelfieSvc, and then
	 * checks that the GhostMySelfie is included in the list when getGhostMySelfieList() is
	 * called.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUser() throws Exception {
		User user = userSvc.addUser(User.create("user0@gmail.com", "pass", "user0@gmail.com", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT"));
		boolean resp = false;
		if (user != null) {
			resp = true;
		}
		assertTrue(resp);
	}		
	
	/**
	 * This test creates a GhostMySelfie, adds the GhostMySelfie to the GhostMySelfieSvc, and then
	 * checks that the GhostMySelfie is included in the list when getGhostMySelfieList() is
	 * called.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGhostMySelfieAddAndList() throws Exception {
		// Add the ghostMySelfie
		GhostMySelfieSvc.addGhostMySelfie(ghostMySelfie);

		// We should get back the ghostMySelfie that we added above
		Collection<GhostMySelfie> ghostMySelfies = GhostMySelfieSvc.getGhostMySelfieList();

		// The server isn't going to send back owner information
		// with the GhostMySelfies, so we shouldn't expect to find it
		ghostMySelfie.setOwner(null);

		assertTrue(ghostMySelfies.contains(ghostMySelfie));
	}

	/**
	 * This test ensures that clients with invalid credentials cannot get access
	 * to GhostMySelfies.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAccessDeniedWithIncorrectCredentials() throws Exception {

		try {
			// Add the ghostMySelfie
			invalidClientGhostMySelfieService.addGhostMySelfie(ghostMySelfie);

			fail("The server should have prevented the client from adding a ghostMySelfie"
					+ " because it presented invalid client/user credentials");
		} catch (RetrofitError e) {
			assert (e.getCause() instanceof SecuredRestException);
		}
	}

	/**
	 * This test ensures that read-only clients can access the ghostMySelfie list but
	 * not add new GhostMySelfies.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReadOnlyClientAccess() throws Exception {

		Collection<GhostMySelfie> ghostMySelfies = readOnlyGhostMySelfieService.getGhostMySelfieList();
		assertNotNull(ghostMySelfies);

		try {
			// Add the ghostMySelfie
			readOnlyGhostMySelfieService.addGhostMySelfie(ghostMySelfie);

			fail("The server should have prevented the client from adding a ghostMySelfie"
					+ " because it is using a read-only client ID");
		} catch (RetrofitError e) {
			JsonObject body = (JsonObject) e.getBodyAs(JsonObject.class);
			assertEquals("insufficient_scope", body.get("error").getAsString());
		}
	}

	@Test
	public void testAddGhostMySelfieMetadata() throws Exception {
		GhostMySelfie received = GhostMySelfieSvc.addGhostMySelfie(ghostMySelfie);
		assertEquals(ghostMySelfie.getTitle(), received.getTitle());
		assertTrue(received.getId() > 0);
	}

	@Test
	public void testAddGetGhostMySelfie() throws Exception {
		GhostMySelfieSvc.addGhostMySelfie(ghostMySelfie);
		Collection<GhostMySelfie> stored = GhostMySelfieSvc.getGhostMySelfieList();
		assertTrue(stored.contains(ghostMySelfie));
	}

	@Test
	public void testAddGhostMySelfieData() throws Exception {
		GhostMySelfie received = GhostMySelfieSvc.addGhostMySelfie(ghostMySelfie);
		GhostMySelfieStatus status = GhostMySelfieSvc.setGhostMySelfieData(received.getId(),
				new TypedFile("image/jpeg", testGhostMySelfieData));
		assertEquals(GhostMySelfieState.READY, status.getState());

		Response response = GhostMySelfieSvc.getGhostMySelfieData(received.getId());
		assertEquals(200, response.getStatus());

		InputStream GhostMySelfieData = response.getBody().in();
		byte[] originalFile = IOUtils.toByteArray(new FileInputStream(
				testGhostMySelfieData));
		byte[] retrievedFile = IOUtils.toByteArray(GhostMySelfieData);
		assertTrue(Arrays.equals(originalFile, retrievedFile));
	}

	@Test
	public void testAddGhostMySelfieDataForOtherUsersGhostMySelfie() throws Exception {
		GhostMySelfie received = GhostMySelfieSvc.addGhostMySelfie(ghostMySelfie);

		try {
			GhostMySelfieSvc2.setGhostMySelfieData(received.getId(),
					new TypedFile("ghostMySelfie/mpeg", testGhostMySelfieData));
			
			fail("A user should not be able to set the ghostMySelfie data for another user's ghostMySelfie");
		} catch (Exception e) {
			// This is what we expect because we shouldn't have access
			// to the other user's ghostMySelfie. Ideally, we should also be
			// checking for an appropriate response code here too.
		}
	}
	
	@Test
	public void testUsersCanOnlyHaveASingleRatingForAGhostMySelfie() throws Exception {
		GhostMySelfie received = GhostMySelfieSvc.addGhostMySelfie(ghostMySelfie);
		GhostMySelfieSvc.rateGhostMySelfie(received.getId(), 1);
		GhostMySelfieSvc.rateGhostMySelfie(received.getId(), 2);
		AverageGhostMySelfieRating rating = GhostMySelfieSvc.getGhostMySelfieRating(received.getId());
		
		assertEquals(2, rating.getRating(), 0);
		assertEquals(1, rating.getTotalRatings());
		
		rating = GhostMySelfieSvc2.getGhostMySelfieRating(received.getId());
		assertEquals(2, rating.getRating(), 0);
		assertEquals(1, rating.getTotalRatings());
		
		GhostMySelfieSvc2.rateGhostMySelfie(received.getId(), 4);
		rating = GhostMySelfieSvc2.getGhostMySelfieRating(received.getId());
		assertEquals(3, rating.getRating(), 0);
		assertEquals(2, rating.getTotalRatings());
	}

	@Test
	public void testGetNonExistantGhostMySelfiesData() throws Exception {

		long nonExistantId = getInvalidGhostMySelfieId();

		try {
			Response r = GhostMySelfieSvc.getGhostMySelfieData(nonExistantId);
			assertEquals(404, r.getStatus());
		} catch (RetrofitError e) {
			assertEquals(404, e.getResponse().getStatus());
		}
	}

	@Test
	public void testAddNonExistantGhostMySelfiesData() throws Exception {
		long nonExistantId = getInvalidGhostMySelfieId();
		try {
			GhostMySelfieSvc.setGhostMySelfieData(nonExistantId, new TypedFile("ghostMySelfie/mpeg",
					testGhostMySelfieData));
			fail("The client should receive a 404 error code and throw an exception if an invalid"
					+ " ghostMySelfie ID is provided in setGhostMySelfieData()");
		} catch (RetrofitError e) {
			assertEquals(404, e.getResponse().getStatus());
		}
	}

	private long getInvalidGhostMySelfieId() {
		Set<Long> ids = new HashSet<Long>();
		Collection<GhostMySelfie> stored = GhostMySelfieSvc.getGhostMySelfieList();
		for (GhostMySelfie v : stored) {
			ids.add(v.getId());
		}

		long nonExistantId = Long.MIN_VALUE;
		while (ids.contains(nonExistantId)) {
			nonExistantId++;
		}
		return nonExistantId;
	}

}

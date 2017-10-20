package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.colostate.cs.cs414.andyetitcompiles.p3.client.JungleClient;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.*;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.*;

public class JungleClientTest {
	public JungleClient jClient;
	public static KryoServerMock mockServer;
	
	// setUpClass and tearDownClass are only run once for this test class.
	// This is so we can use one server instance for all the test cases
	@BeforeClass
	public static void setUpClass() {
		try {
			mockServer = new KryoServerMock();
		}
		catch(IOException ex) {
			System.out.println("Something went wrong starting the server: " + ex.getMessage());
		}
	}
	
	@AfterClass
	public static void tearDownClass() {
		mockServer.stop();
		mockServer = null;
	}
	
	// setUp and tearDown are run for each individual test case
	@Before
	public void setUp() throws Exception {
		jClient = new JungleClient();
		// Wait for the client to fully start
		Thread.sleep(500);
	}

	@After
	public void tearDown() throws Exception {
		jClient.stop();
		jClient = null;
	}

	@Test
	public void testLoginSucessful() throws InterruptedException {
		jClient.login("email", "password");
		// Have to wait for the server to receive the message
		Thread.sleep(500);
		// The actual object the server receives
		Object actual = mockServer.getLastReceived();
		// Make sure the server received the same object that was sent
		if(actual instanceof LoginRequest) {
			assertEquals("email", ((LoginRequest) actual).getEmail());
		}
		else {
			fail("Incorrect object type received from client");
		}
		// Make sure the client updates its login state and updates the clients user profile
		assertTrue(jClient.getLoggedInStatus());
		
	}
	
	@Test
	public void testLoginFailure() throws InterruptedException {
		jClient.login("wrongemail", "wrongpassword");
		Thread.sleep(500);
		// The actual object the server receives
		Object actual = mockServer.getLastReceived();
		// Make sure the server received the same object that was sent
		if(actual instanceof LoginRequest) {
			assertEquals("wrongemail", ((LoginRequest) actual).getEmail());
		}
		else {
			fail("Incorrect object type received from client");
		}
		// Make sure the client is still set to logged off
		assertFalse(jClient.getLoggedInStatus());	
	}
	
	@Test
	public void testRegisterSuccess() throws InterruptedException {
		jClient.register("email", "nickname", "password");
		Thread.sleep(500);
		Object actual = mockServer.getLastReceived();
		if(actual instanceof RegisterRequest) {
			assertEquals("email", ((RegisterRequest) actual).getEmail());
		}
		else {
			fail("Incorrect object type recevied from client");
		}
		// TODO: Somehow check the response to see if it shows success
	}

	@Test
	public void testRegisterFailure() throws InterruptedException {
		jClient.register("bademail", "badnickname", "badpassword");
		Thread.sleep(500);
		Object actual = mockServer.getLastReceived();
		if(actual instanceof RegisterRequest) {
			assertEquals("bademail", ((RegisterRequest) actual).getEmail());
		}
		else {
			fail("Incorrect object type recevied from client");
		}
		// TODO: Somehow check the response to see if it shows failure
	}
	
	@Test
	public void testUnregisterSuccess() throws InterruptedException {
		jClient.unregister("email", "password");
		Thread.sleep(500);
		Object actual = mockServer.getLastReceived();
		if(actual instanceof UnregisterRequest) {
			assertEquals("email", ((UnregisterRequest) actual).getEmail());
		}
		else {
			fail("Incorrect object type recevied from client");
		}
		// TODO: Somehow check the response to see if it shows success
	}
	
	@Test
	public void testUnregisterFailure() throws InterruptedException {
		jClient.unregister("bademail", "badpassword");
		Thread.sleep(500);
		Object actual = mockServer.getLastReceived();
		if(actual instanceof UnregisterRequest) {
			assertEquals("bademail", ((UnregisterRequest) actual).getEmail());
		}
		else {
			fail("Incorrect object type recevied from client");
		}
		// TODO: Somehow check the response to see if it shows failure
	}
	
	@Test
	public void testFindUserSuccess() throws InterruptedException {
		User actualUser = jClient.findUser("nickname");
		Thread.sleep(500);
		UserRequest expected = new UserRequest("nickname");
		Object lastReceived = mockServer.getLastReceived();
		if(lastReceived instanceof UserRequest) 
			assertEquals(expected, (UserRequest)lastReceived);
		else
			fail("incorrect object type received from client");
		// Received a real user from the server, indicating success
		assertEquals(actualUser, new User("email", "nickname", "password"));
	}
	
	@Test
	public void testFindUserFailure() throws InterruptedException {
		User actualUser = jClient.findUser("badnickname");
		Thread.sleep(500);
		UserRequest expected = new UserRequest("badnickname");
		Object lastReceived = mockServer.getLastReceived();
		if(lastReceived instanceof UserRequest) 
			assertEquals(expected, (UserRequest)lastReceived);
		else
			fail("incorrect object type received from client");
		// Did not receive a real user from the server, indicating failure
		assertEquals(actualUser, null);
	}
	@Test
	public void testInvite() throws InterruptedException {
		// Invites don't block the client to wait for a response, so we don't have to 
		// test how it handles receiving a response
		User user = new User("email", "nickname", "password");
		jClient.invite(user);
		Thread.sleep(500);
		InviteRequest expected = new InviteRequest(user, user);
		Object lastReceived = mockServer.getLastReceived();
		if(lastReceived instanceof InviteRequest)
			assertEquals(expected, (UserRequest)lastReceived);
		else
			fail("incorrect object type received from the client");
	}
}

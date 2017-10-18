package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import com.esotericsoftware.kryonet.Client;

import edu.colostate.cs.cs414.andyetitcompiles.p3.client.JungleClient;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.*;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.*;

public class JungleClientTest {
	public JungleClient jClient;
	public KryoServerMock mockServer;
	
	@Before
	public void setUp() throws Exception {
		mockServer = new KryoServerMock();
		jClient = new JungleClient();
	}

	@After
	public void tearDown() throws Exception {
		jClient = null;
		mockServer.stop();
		mockServer = null;
	}

	@Test
	public void testInitializeKryoClient() {
		jClient.initializeKryoClient();
		assertTrue(jClient.getConnectedStatus());
	}
	
	@Test
	public void testLoginSucessful() {
		jClient.login("email", "password");
		// The expected object that the server receives
		LoginRequest expected = new LoginRequest("email", "password");
		// The actual object the server receives
		Object lastReceived = mockServer.getLastReceived();
		// Make sure the server received the same object that was sent
		if(lastReceived instanceof LoginRequest) {
			assertEquals(expected, (LoginRequest)lastReceived);
		}
		else {
			fail("Incorrect object type received from client");
		}
		// Make sure the client updates its login state and updates the clients user profile
		assertTrue(jClient.getLoggedInStatus());
		
	}
	
	@Test
	public void testLoginFailure() {
		jClient.login("wrongemail", "wrongpassword");
		// The expected object that the server receives
		LoginRequest expected = new LoginRequest("wrongemail", "wrongpassword");
		// The actual object the server receives
		Object lastReceived = mockServer.getLastReceived();
		// Make sure the server received the same object that was sent
		if(lastReceived instanceof LoginRequest) {
			assertEquals(expected, (LoginRequest)lastReceived);
		}
		else {
			fail("Incorrect object type received from client");
		}
		// Make sure the client updates its login state
		assertFalse(jClient.getLoggedInStatus());	
	}
	
	@Test
	public void testRegisterSuccess() {
		jClient.register("email", "nickname", "password");
		RegisterRequest expected = new RegisterRequest("email", "nickname", "password");
		Object lastReceived = mockServer.getLastReceived();
		if(lastReceived instanceof RegisterRequest) {
			assertEquals(expected, (RegisterRequest)lastReceived);
		}
		else {
			fail("Incorrect object type recevied from client");
		}
		// TODO: Somehow check the response to see if it shows success
	}

	@Test
	public void testRegisterFailure() {
		jClient.register("bademail", "badnickname", "badpassword");
		RegisterRequest expected = new RegisterRequest("bademail", "badnickname", "badpassword");
		Object lastReceived = mockServer.getLastReceived();
		if(lastReceived instanceof RegisterRequest) {
			assertEquals(expected, (RegisterRequest)mockServer.lastReceived);
		}
		else {
			fail("Incorrect object type recevied from client");
		}
		// TODO: Somehow check the response to see if it shows failure
	}
	
	@Test
	public void testUnregisterSuccess() {
		jClient.unregister("email", "password");
		UnregisterRequest expected = new UnregisterRequest("email", "password");
		Object lastReceived = mockServer.getLastReceived();
		if(lastReceived instanceof RegisterRequest) {
			assertEquals(expected, (UnregisterRequest)lastReceived);
		}
		else {
			fail("Incorrect object type recevied from client");
		}
		// TODO: Somehow check the response to see if it shows success
	}
	
	@Test
	public void testUnregisterFailure() {
		jClient.unregister("bademail", "badpassword");
		UnregisterRequest expected = new UnregisterRequest("bademail", "badpassword");
		Object lastReceived = mockServer.getLastReceived();
		if(lastReceived instanceof RegisterRequest) {
			assertEquals(expected, (UnregisterRequest)lastReceived);
		}
		else {
			fail("Incorrect object type recevied from client");
		}
		// TODO: Somehow check the response to see if it shows failure
	}
	
	@Test
	public void testFindUserSuccess() {
		User actualUser = jClient.findUser("nickname");
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
	public void testFindUserFailure() {
		User actualUser = jClient.findUser("badnickname");
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
	public void testInvite() {
		// Invites don't block the client to wait for a response, so we don't have to 
		// test how it handles receiving a response
		User user = new User("email", "nickname", "password");
		InviteRequest expected = new InviteRequest(user);
		Object lastReceived = mockServer.getLastReceived();
		if(lastReceived instanceof InviteRequest)
			assertEquals(expected, (UserRequest)lastReceived);
		else
			fail("incorrect object type received from the client");
	}
}

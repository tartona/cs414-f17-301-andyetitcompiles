/**
 * 
 */
package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.LoginRequest;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.LoginResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.RegisterRequest;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.RegisterResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.UnregisterRequest;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.UnregisterResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.server.JungleServer;

/**
 * @author Brian Larson
 *
 */
public class JungleServerTest {

	JungleServer jServer = new JungleServer();
	KryoClientMock client = new KryoClientMock();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		jServer = new JungleServer();
		client = new KryoClientMock();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		jServer.stop();
		client=null;
	}

	
	// **********Begin Tests********** //

	@Test
	public void testRegisterSuccess() {
		client.send(new RegisterRequest("Email@email.com", "Nickname", "Password"));
		Object response = client.getResp();
		if(response instanceof RegisterResponse) {
			assertTrue(((RegisterResponse)response).successful());
		}else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testRegisterFailure1() {
		client.send(new RegisterRequest("Email@email.com", "Nickname", "Password"));
		Object response = client.getResp();
		if(response instanceof RegisterResponse) {
			//Should register email/username
			assertTrue(((RegisterResponse)response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}

		//email already in database
		client.send(new RegisterRequest("Email@email.com", "newNickname", "Password"));
		response = client.getResp();
		if(response instanceof RegisterResponse) {
			assertFalse(((RegisterResponse)response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}

		//username already in database
		client.send(new RegisterRequest("newEmail@email.com", "Nickname", "Password"));
		response = client.getResp();
		if(response instanceof RegisterResponse) {
			assertFalse(((RegisterResponse)response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}

	}

	@Test
	public void testRegisterFailure2() {
		//incorrect email format
		client.send(new RegisterRequest("Email%email.com", "Nickname", "Password"));
		Object response = client.getResp();
		if(response instanceof RegisterResponse) {
			assertFalse(((RegisterResponse)response).successful());
		}else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testLoginSuccess() {
		//send request to server;
		client.send(new LoginRequest("UserName", "Password"));
		Object response = client.getResp();
		if(response instanceof LoginResponse) {
			assertTrue(((LoginResponse)response).successful());
		}else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testLoginFailure1() {
		//send request to server;
		client.send(new LoginRequest("wrongUserName", "Password"));
		Object response = client.getResp();
		if(response instanceof LoginResponse) {
			assertFalse(((LoginResponse)response).successful());
		}else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testLoginFailure2() {
		//send request to server;
		client.send(new LoginRequest("UserName", "wrongPassword"));
		Object response = client.getResp();
		if(response instanceof LoginResponse) {
			assertFalse(((LoginResponse)response).successful());
		}else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testUnregisterSuccess() {
		client.send(new UnregisterRequest("Email@email.com", "Password"));
		Object response = client.getResp();
		if(response instanceof RegisterResponse) {
			assertTrue(((RegisterResponse)response).successful());
		}else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testUnregisterFailure1() {
		//email not in database
		client.send(new UnregisterRequest("Email%email.com", "Password"));
		Object response = client.getResp();
		if(response instanceof UnregisterResponse) {
			assertFalse(((UnregisterResponse)response).successful());
		}else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testUnregisterFailure2() {
		//Wrong password for email.
		client.send(new UnregisterRequest("Email@email.com", "wrongPassword"));
		Object response = client.getResp();
		if(response instanceof UnregisterResponse) {
			assertFalse(((UnregisterResponse)response).successful());
		}else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testInviteSuccess() {
		fail("Not yet implemented");
	}

	@Test
	public void testInviteFailure() {
		fail("Not yet implemented");
	}

}

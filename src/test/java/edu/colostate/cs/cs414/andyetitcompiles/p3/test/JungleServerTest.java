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

	JungleServer jServer;
	KryoClientMock client = new KryoClientMock();

	// @BeforeClass
	// public static void setUpClass() {
	// try {
	// jServer = new JungleServer();
	// }
	// catch(IOException ex) {
	// System.out.println("Something went wrong starting the server: " +
	// ex.getMessage());
	// }
	// }
	//
	// @AfterClass
	// public static void tearDownClass() {
	// jServer.stop();
	// }

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		jServer = new JungleServer();
		client = new KryoClientMock();
		Thread.sleep(500);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		jServer.stop();
		client = null;
		Thread.sleep(500);
	}

	// **********Begin Tests********** //

	@Test
	public void testRegisterSuccess() throws InterruptedException {
		client.send(new RegisterRequest("Email@email.com", "Nickname", "Password"));
		Thread.sleep(500);
		Object response = client.getResp();
		if (response instanceof RegisterResponse) {
			assertTrue(((RegisterResponse) response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testRegisterFailure1() throws InterruptedException {
		client.send(new RegisterRequest("Email@email.com", "Nickname", "Password"));
		Thread.sleep(500);
		Object response = client.getResp();
		if (response instanceof RegisterResponse) {
			// Should register email/username
			assertTrue(((RegisterResponse) response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}

		// email already in database
		client.send(new RegisterRequest("Email@email.com", "newNickname", "Password"));
		Thread.sleep(500);
		response = client.getResp();
		if (response instanceof RegisterResponse) {
			System.out.println(((RegisterResponse) response).getMessage());
			assertFalse(((RegisterResponse) response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}

		// username already in database
		client.send(new RegisterRequest("newEmail@email.com", "Nickname", "Password"));
		response = client.getResp();
		if (response instanceof RegisterResponse) {
			assertFalse(((RegisterResponse) response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}

	}

	@Test
	public void testRegisterFailure2() throws InterruptedException {
		// incorrect email format
		client.send(new RegisterRequest("Email%email.com", "Nickname", "Password"));
		Thread.sleep(500);
		Object response = client.getResp();
		if (response instanceof RegisterResponse) {
			assertFalse(((RegisterResponse) response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testLoginSuccess() throws InterruptedException {
		client.send(new RegisterRequest("Email@email.com", "Nickname", "Password"));
		Thread.sleep(500);
		// send request to server;
		client.send(new LoginRequest("Email@email.com", "Password"));
		Thread.sleep(500);
		Object response = client.getResp();
		if (response instanceof LoginResponse) {
			System.out.println(((LoginResponse) response).getMessage());
			assertTrue(((LoginResponse) response).successful());
		} else {
			if (response != null) {
				System.out.println("recieved:" + response.getClass().getName() + " ...Expected LoginResponse");
			} else {
				System.out.println("Server sent null object");
			}
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testLoginFailure1() throws InterruptedException {
		client.send(new RegisterRequest("Email@email.com", "Nickname", "Password"));
		Thread.sleep(500);
		// send request to server;
		client.send(new LoginRequest("wrongUserName", "Password"));
		Thread.sleep(500);
		Object response = client.getResp();
		Thread.sleep(500);
		if (response instanceof LoginResponse) {
			assertFalse(((LoginResponse) response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testLoginFailure2() throws InterruptedException {
		client.send(new RegisterRequest("Email@email.com", "Nickname", "Password"));
		Thread.sleep(500);
		// send request to server;
		client.send(new LoginRequest("UserName", "wrongPassword"));
		Thread.sleep(500);
		Object response = client.getResp();
		if (response instanceof LoginResponse) {
			assertFalse(((LoginResponse) response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testUnregisterSuccess() throws InterruptedException {
		client.send(new RegisterRequest("Email@email.com", "Nickname", "Password"));
		Thread.sleep(500);
		client.send(new UnregisterRequest("Email@email.com", "Password"));
		Object response = client.getResp();
		Thread.sleep(500);
		if (response instanceof RegisterResponse) {
			assertTrue(((RegisterResponse) response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testUnregisterFailure1() throws InterruptedException {
		// email not in database
		client.send(new UnregisterRequest("Email%email.com", "Password"));
		Thread.sleep(500);
		Object response = client.getResp();
		if (response instanceof UnregisterResponse) {
			assertFalse(((UnregisterResponse) response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}
	}

	@Test
	public void testUnregisterFailure2() throws InterruptedException {
		// Wrong password for email.
		client.send(new UnregisterRequest("Email@email.com", "wrongPassword"));
		Thread.sleep(500);
		Object response = client.getResp();
		if (response instanceof UnregisterResponse) {
			assertFalse(((UnregisterResponse) response).successful());
		} else {
			fail("Incorrect object type received from server!");
		}
	}

	// @Test
	// public void testInviteSuccess() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testInviteFailure() {
	// fail("Not yet implemented");
	// }

}

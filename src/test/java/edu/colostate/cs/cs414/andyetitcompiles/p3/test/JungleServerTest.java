/**
 * 
 */
package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.LoginRequest;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.LoginResponse;
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
	public void testLoginRequest() {
		//send request to server;
		client.send(new LoginRequest("UserName", "Password"));
		Object response = client.getResp();
		Object expected = new LoginResponse(false, null);
		assertEquals(expected, response);
	}

	@Test
	public void testRegisterRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnregisterRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testBadUnregisterRequest() {
		fail("Not yet implemented");
	}

	@Test
	public void testInviteRequest() {
		fail("Not yet implemented");
	}

}

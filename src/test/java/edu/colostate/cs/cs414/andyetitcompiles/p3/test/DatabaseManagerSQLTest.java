package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.UserStatus;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.LoginResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.RegisterResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.UnregisterResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.UserResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.server.DatabaseManagerSQL;

public class DatabaseManagerSQLTest {

	static DatabaseManagerSQL db;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = new DatabaseManagerSQL("~/unitTest","test","password");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		db.resetTable();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRegisterUser() {
		User user = new User("Email@Email.com", "nickname", "password");
		RegisterResponse regResp = db.registerUser(user);
		assertTrue(regResp.successful());
		
		UserResponse searchResp = db.findUser(user.getNickname());
		User userResp = searchResp.getUser();
		assertTrue(searchResp.successful());
		if(userResp!=null) {
			assertTrue(userResp.getNickname().equalsIgnoreCase(user.getNickname()));
			assertTrue(userResp.getEmail().equalsIgnoreCase(user.getEmail()));
		} else {
			fail("Returned user was null");
		}
		
		//make sure multiple registrations with matching info fails
		regResp = db.registerUser(new User("EmAiL@eMaIl.CoM", "othername", "anypassword"));
		assertFalse(regResp.successful());

		regResp = db.registerUser(new User("otherEmail@email", "nIcKnAmE", "anypassword"));
		assertFalse(regResp.successful());
	}

	@Test
	public void testUnRegisterUser() {
		User user = new User("Email@Email.com", "nickname", "password");
		//register user
		RegisterResponse regResp = db.registerUser(user);
		assertTrue(regResp.successful());
		
		//test incorrect unregister ... test case sensitivity
		UnregisterResponse unRegResp = db.unRegisterUser(user.getEmail(), "PaSsWoRd");
		assertFalse(unRegResp.successful());	

		//unregister user
		unRegResp = db.unRegisterUser(user.getEmail(), user.getPassword());
		assertTrue(unRegResp.getMessage(),unRegResp.successful());

	}

	@Test
	public void testAuthenticateUser() {
		User user = new User("Email@Email.com", "nickname", "password");
		//register user
		RegisterResponse regResp = db.registerUser(user);
		assertTrue(regResp.successful());

		//test incorrect logins
		LoginResponse loginResp = db.authenticateUser(user.getEmail()+"n", user.getPassword());
		assertFalse(loginResp.getMessage(), loginResp.successful());

		loginResp = db.authenticateUser(user.getEmail(), "PassWOrD");//test case sensitivity
		assertFalse(loginResp.getMessage(), loginResp.successful());
		
		//test success
		loginResp = db.authenticateUser(user.getEmail(), user.getPassword());
		assertTrue(loginResp.getMessage(), loginResp.successful());
		assertTrue(loginResp.getUser().getStatus().compareTo(UserStatus.ONLINE) == 0); //assure user is logged in

		db.logout(user);
		UserResponse userResp = db.findUser(user.getNickname());
		assertTrue(new String(userResp.getUser().getStatus()+""),userResp.getUser().getStatus().compareTo(UserStatus.OFFLINE) == 0); //assure user is logged in
		
		loginResp = db.authenticateUser(user.getEmail().toUpperCase(), user.getPassword());//test case insensitivity
		assertTrue(loginResp.getMessage(), loginResp.successful());

	}

	@Test
	public void testFindUser() {
		User user = new User("Email@Email.com", "nickname", "password");
		//register user
		RegisterResponse regResp = db.registerUser(user);
		assertTrue(regResp.successful());

		UserResponse userResp = db.findUser(user.getNickname());
		assertTrue(userResp.getMessage(), userResp.successful());
		
		userResp = db.findUser("NotUsername");
		assertFalse(userResp.getMessage(), userResp.successful());

	}

}
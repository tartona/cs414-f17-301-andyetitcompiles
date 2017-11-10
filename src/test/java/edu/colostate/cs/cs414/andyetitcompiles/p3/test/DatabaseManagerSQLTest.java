package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import java.sql.Timestamp;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.GameRecord;
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

	@Test
	public void testUserHistory() {
		//create user1
		User user1 = new User("Email1@Email.com", "nickname1", "password");
		RegisterResponse regResp = db.registerUser(user1);
		assertTrue(regResp.successful());
		user1 = db.findUser(user1.getNickname()).getUser();

		//create user2
		User user2 = new User("Email2@Email.com", "nickname2", "password");
		regResp = db.registerUser(user2);
		assertTrue(regResp.successful());
		user2 = db.findUser(user2.getNickname()).getUser();

		db.addGame(new GameRecord(user1.getId(), user2.getNickname(), new Timestamp(10000), new Timestamp(10005), true, false),
				   new GameRecord(user2.getId(), user1.getNickname(), new Timestamp(10000), new Timestamp(10005), false, false));

		db.addGame(new GameRecord(user1.getId(), user2.getNickname(), new Timestamp(10500), new Timestamp(10505), false, false),
				   new GameRecord(user2.getId(), user1.getNickname(), new Timestamp(10500), new Timestamp(10505), true, false));

		db.addGame(new GameRecord(user1.getId(), user2.getNickname(), new Timestamp(40000), new Timestamp(50005), true, true),
				   new GameRecord(user2.getId(), user1.getNickname(), new Timestamp(40000), new Timestamp(50005), false, true));

		db.addGame(new GameRecord(user1.getId(), user2.getNickname(), new Timestamp(80000), new Timestamp(88888), true, false),
				   new GameRecord(user2.getId(), user1.getNickname(), new Timestamp(80000), new Timestamp(88888), false, false));

		//get user information including updated user history
		user1 = db.findUser(user1.getNickname()).getUser();
		user2 = db.findUser(user2.getNickname()).getUser();
		
		System.out.println(user1.toString());
		int winCount=0;
		for(GameRecord temp:user1.getRecords()) {
			if(temp.isWon()) {
				winCount++;
			}
		}

		assertEquals(3, winCount);//user1 should have 3 wins
		
		winCount=0;
		for(GameRecord temp:user2.getRecords()) {
			if(temp.isWon()) {
				winCount++;
			}
		}
		assertEquals(1, winCount);//user2 should have 1 wins

	}
}

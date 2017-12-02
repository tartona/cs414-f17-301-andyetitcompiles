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
		
		//make sure user is not online
		assertTrue(db.onlineUsers().size()==0);

		//test success
		loginResp = db.authenticateUser(user.getEmail(), user.getPassword());
		assertTrue(loginResp.getMessage(), loginResp.successful());
		assertTrue(loginResp.getUser().getStatus().compareTo(UserStatus.ONLINE) == 0); //assure user is logged in
		//make sure 1 user is online
		assertTrue(db.onlineUsers().size()==1);

		db.logout(user);
		UserResponse userResp = db.findUser(user.getNickname());
		assertTrue(new String(userResp.getUser().getStatus()+""),userResp.getUser().getStatus().compareTo(UserStatus.OFFLINE) == 0); //assure user is logged in

		//make sure user is not online after logging out
		assertTrue(db.onlineUsers().size()==0);
		
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
		
		//make sure gameIDs are in database when record is added.
		db.addGame(0, user1.getId(), user2.getId(), new Timestamp(10000), 1, "");
		db.addGame(1, user1.getId(), user2.getId(), new Timestamp(10500), 2, "");
		db.addGame(2, user1.getId(), user2.getId(), new Timestamp(40000), 1, "");
		db.addGame(3, user1.getId(), user2.getId(), new Timestamp(System.currentTimeMillis()-10000000), 4, "");

		db.addGameRecord(0, new GameRecord(user1.getId(), user2.getNickname(), new Timestamp(10000), new Timestamp(10005), true, false),
				   new GameRecord(user2.getId(), user1.getNickname(), new Timestamp(10000), new Timestamp(10005), false, false));

		db.addGameRecord(1, new GameRecord(user1.getId(), user2.getNickname(), new Timestamp(10500), new Timestamp(10505), false, false),
				   new GameRecord(user2.getId(), user1.getNickname(), new Timestamp(10500), new Timestamp(10505), true, false));

		db.addGameRecord(2, new GameRecord(user1.getId(), user2.getNickname(), new Timestamp(40000), new Timestamp(50005), true, true),
				   new GameRecord(user2.getId(), user1.getNickname(), new Timestamp(40000), new Timestamp(50005), false, true));

		db.addGameRecord(3, new GameRecord(user1.getId(), user2.getNickname(), new Timestamp(System.currentTimeMillis()-10000000), new Timestamp(System.currentTimeMillis()), true, false),
				   new GameRecord(user2.getId(), user1.getNickname(), new Timestamp(System.currentTimeMillis()-10000000), new Timestamp(System.currentTimeMillis()), false, false));

		//get user information including updated user history
		user1 = db.findUser(user1.getNickname()).getUser();
		user2 = db.findUser(user2.getNickname()).getUser();
		
		System.out.println(user1.toString());
		System.out.println(user2.toString());
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

		//Additional code coverage, include path where user played against is missing. 
		db.unRegisterUser(user2.getEmail(), "password");
		user1 = db.findUser(user1.getNickname()).getUser();
		winCount=0;
		for(GameRecord temp:user1.getRecords()) {
			if(temp.isWon()) {
				winCount++;
			}
		}
		assertEquals(3, winCount);//user1 should have 3 wins

	}
	
	@Test
	public void testGameStorage() {	
		//setup 2 users
		User user1 = new User("Email@Email.com", "nickname", "password");
		RegisterResponse regResp1 = db.registerUser(user1);
		assertTrue(regResp1.successful());
		LoginResponse loginResp1 = db.authenticateUser(user1.getEmail(), user1.getPassword());//test case sensitivity
		assertTrue(loginResp1.getMessage(), loginResp1.successful());
		user1 = loginResp1.getUser();
		
		User user2 = new User("Email2@Email.com", "nickname2", "password");
		RegisterResponse regResp2 = db.registerUser(user2);
		assertTrue(regResp2.successful());
		LoginResponse loginResp2 = db.authenticateUser(user2.getEmail(), user2.getPassword());//test case sensitivity
		assertTrue(loginResp2.getMessage(), loginResp2.successful());
		user2 = loginResp2.getUser();
		
		//test new game
		String board = "Not an accurate repressentation of a game board but should work";
		assertTrue(db.addGame(1, user1.getId(), user2.getId(), new Timestamp(System.currentTimeMillis()), 1, board));
		assertTrue(db.findGame(1).getGameConfig().equals(board));

		//test board update
		String newBoard = "Not an accurate haaay look I changed some letters! i  work";
		assertTrue(db.updateGame(1, newBoard));
		assertTrue(db.findGame(1).getGameConfig().equals(newBoard));
		
		//test games removed when user unregisteres. 
		db.unRegisterUser("email2@email.com", "password");
		//make sure game was removed
		assertTrue(db.findGame(1)==null);
	}
}

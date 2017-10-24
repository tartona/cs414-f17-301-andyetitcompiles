package edu.colostate.cs.cs414.andyetitcompiles.p3.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserTest {
	protected User u;
	protected User u2;

	@Before
	public void setUp() throws Exception {
		u = new User("wangdddd@gmail.com","123456","NAN",UserStatus.ONLINE);
		u2 = new User();
		u2.setemail("wesa@qq.com");
		u2.setnickname("BOB");
		u2.setpassword("1234567");
		u2.setuserstatus(UserStatus.OFFLINE);
	}

	@After
	public void tearDown() throws Exception {
		u = null; u2= null;
	}

	@Test
	public void testgetemail() {
		assertTrue("It is wrong.",u.getemail().toString().equals("wangdddd@gmail.com"));
	}
	@Test
	public void testgetpassword() {
		assertTrue("It is wrong.",u.getpassword().toString().equals("123456"));
	}
	@Test
	public void testgetnickname() {
		assertTrue("It is wrong.",u.getnickname().toString().equals("NAN"));
	}
	@Test
	public void testgetuserstatus() {
		assertEquals("It is wrong.",u.getuserstatus(),UserStatus.ONLINE);
	}
	@Test
	public void testsetemail() {
		assertTrue("It is wrong.",u2.getemail().toString().equals("wesa@qq.com"));	
	}
	@Test
	public void testsetnickname() {
		assertTrue("It is wrong.",u2.getnickname().toString().equals("BOB"));	
	}
	@Test
	public void testpassword() {
		assertTrue("It is wrong.",u2.getpassword().toString().equals("1234567"));
	}
	@Test
	public void testuserstatus() {
		assertEquals("It is wrong.",u2.getuserstatus(),UserStatus.OFFLINE);
	}

}

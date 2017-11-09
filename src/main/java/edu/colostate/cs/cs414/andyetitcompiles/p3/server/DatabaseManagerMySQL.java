package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import java.util.Set;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.UserStatus;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.LoginResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.RegisterResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.UnregisterResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.UserResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class DatabaseManagerMySQL extends DatabaseManager {

	private Set<User> onlineUsers;	//Contains online users
	private final static String dbURL = "jdbc:mysql://localhost:3306/jungledb" 
									  + "?verifyServerCertificate=true"
									  + "&useSSL=true";
	private final static String dbUsername = "root";
	private final static String dbPassword = "1234";
	
	Connection connection;

	public DatabaseManagerMySQL() throws ClassNotFoundException, SQLException{
		
		onlineUsers = new HashSet<>();
		setupDB();

		//setup database connection
	}
	
	/**
	 * Connect to database and setup tables if not already set up.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void setupDB() throws ClassNotFoundException, SQLException {
		System.out.println("-------- Connecting to JungleDB ------------");
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
		System.out.println("-------- Connected to JungleDB ------------");

		//*****setup tables*****
		
		//setup userProfile table
		String sql = "CREATE TABLE IF NOT EXISTS `jungledb`.`userprofile` (\r\n" + 
				"  `idUser` INT(11) NOT NULL AUTO_INCREMENT,\r\n" + 
				"  `Username` VARCHAR(63) NOT NULL,\r\n" + 
				"  `Email` VARCHAR(63) CHARACTER SET 'dec8' NOT NULL,\r\n" + 
				"  `Password` VARCHAR(63) NOT NULL,\r\n" + 
				"  PRIMARY KEY (`idUser`),\r\n" + 
				"  UNIQUE INDEX `idUser_UNIQUE` (`idUser` ASC),\r\n" + 
				"  UNIQUE INDEX `Username_UNIQUE` (`Username` ASC),\r\n" + 
				"  UNIQUE INDEX `Email_UNIQUE` (`Email` ASC))\r\n" + 
				"ENGINE = InnoDB\r\n" + 
				"AUTO_INCREMENT = 12\r\n" + 
				"DEFAULT CHARACTER SET = utf8";
		connection.prepareStatement(sql).execute();
		
		//setup userHistory table
		sql = "CREATE TABLE IF NOT EXISTS `jungledb`.`userhistory` (\r\n" + 
				"  `idUser` INT(11) NOT NULL,\r\n" + 
				"  `opponent` INT(11) NOT NULL,\r\n" + 
				"  `startTimestamp` TIMESTAMP NULL DEFAULT NULL,\r\n" + 
				"  `endTimestamp` TIMESTAMP NULL DEFAULT NULL,\r\n" + 
				"  `won` BINARY(1) NULL DEFAULT NULL,\r\n" + 
				"  `abandoned` BINARY(1) NULL DEFAULT NULL,\r\n" + 
				"  INDEX `fk_UserHistory_UserProfile_idx` (`idUser` ASC),\r\n" + 
				"  PRIMARY KEY (`idUser`),\r\n" + 
				"  CONSTRAINT `fk_UserHistory_UserProfile`\r\n" + 
				"    FOREIGN KEY (`idUser`)\r\n" + 
				"    REFERENCES `jungledb`.`userprofile` (`idUser`)\r\n" + 
				"    ON DELETE CASCADE\r\n" + 
				"    ON UPDATE NO ACTION)\r\n" + 
				"ENGINE = InnoDB\r\n" + 
				"DEFAULT CHARACTER SET = utf8";
		connection.prepareStatement(sql).execute();
			
			
	}

	public RegisterResponse registerUser(User user) {
		if (checkUser(user)) {
			return new RegisterResponse(false, "Username or Email already in use");
		}

		String query = "INSERT INTO userProfile (Username, Email, Password)"
					 + "VALUES('" + user.getNickname() + "', '" + user.getEmail() + "', '" + user.getPassword() + "');";
		try {
			connection.prepareStatement(query).execute();
			return new RegisterResponse(true, "User Registered");

		} catch (SQLException e) {
			e.printStackTrace();
			return new RegisterResponse(false, "User Failed to register in database");
		}

	}

	private boolean checkUser(User user) {
		String sql = "SELECT * FROM userProfile" 
				   + " WHERE Username LIKE " + user.getNickname()
				   + " OR Email LIKE " + user.getEmail()
				   + " ;";
		try {
			connection.prepareStatement(sql);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public RegisterResponse registerUser(String email, String password, String nickname) {
		return registerUser(new User(email, password, nickname));
	}

	public UnregisterResponse unRegisterUser(String email, String password) {
		
		//search for Email with matching password
		String sql = "SELECT * FROM userProfile" 
				   + " WHERE Email LIKE " + email			//if email matches 
				   + " AND BINARY Password LIKE " + password//AND password (Case Sensitive) matches
				   + " ;";
		ResultSet rtnSet = null;
		try {
			rtnSet = connection.prepareStatement(sql).executeQuery();
			int n=0;
			while(rtnSet.next()) {
				n++; //should never be more than 1
			}
			//found 1 user
			if(n==1) {

				//remove user
				sql = "DELETE FROM userProfile idUser=" + rtnSet.getString("idUser");
				connection.prepareStatement(sql).execute();
				return new UnregisterResponse(true, "Account Deleted");
			}
			if(n==0) {
				return new UnregisterResponse(false, "Account not found");
			}
			if(n>1) {
				System.out.println("SERIOUS DATABASE ERROR OCCURED!");
				return new UnregisterResponse(false, "Serious error occured");
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return new UnregisterResponse(false, "Failed to remove user");
					
		}
	}

	public LoginResponse authenticateUser(String email, String password) {
		String sql = "SELECT FROM userProfile "
				   + "WHERE email LIKE " + email
				   + "AND BINARY password LIKE " + password;
		try {
			ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
			int n = 0;
			while (rtnSet.next()) {
				n++; // should never be more than 1
			}
			// found 1 user
			if (n == 1) {
				// Return user information
				sql = "DELETE FROM userProfile idUser=" + rtnSet.getString("idUser");
				connection.prepareStatement(sql).execute();
				User tempUser = new User(rtnSet.getInt("idUser"),rtnSet.getString("Email"),rtnSet.getString("Username"),"", UserStatus.ONLINE); 
				return new LoginResponse(true, tempUser, "User login successful.");
			}
			if (n == 0) {
				return new LoginResponse(false, null, "User not found.");
			}
			if (n > 1) {
				System.out.println("SERIOUS DATABASE ERROR OCCURED!");
				return new LoginResponse(false, null, "Serious Error Occured.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new LoginResponse(false, null, "User login failed.");
	}

	public void logout(User user) {
		onlineUsers.remove(user);
	}

	// take username or email as input for user.
	public UserResponse findUser(String username) {
		for (User user : onlineUsers) {
			if (user.getEmail().equalsIgnoreCase(username) || user.getNickname().equalsIgnoreCase(username)) {
				return new UserResponse(true, user, "User Found");
			}
		}
		return new UserResponse(false, null, "User not found");
	}
}
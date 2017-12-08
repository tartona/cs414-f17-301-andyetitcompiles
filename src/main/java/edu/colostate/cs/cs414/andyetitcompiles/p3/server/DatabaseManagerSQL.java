package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.GameRecord;
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
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

public class DatabaseManagerSQL {

	private String dbLocation = "~/jungleDB";
//									  + "?verifyServerCertificate=true"

	private String dbUsername = "Admin";
	private String useSSL = "&useSSL=true";
	private String ignoreCase = ";IGNORECASE=FALSE";
	private String dbPassword = "43533431342d4631372d416e645965744974436f6d70696c6573"; //CS414-F17-AndYetItCompiles
	
	private Connection connection;

	public DatabaseManagerSQL() throws ClassNotFoundException, SQLException{
		setupDB();//setup database connection and tables
	}
	
	public DatabaseManagerSQL(String dbFileLocation, String dbUsername, String dbPassword) throws ClassNotFoundException, SQLException{

		this.dbLocation = dbFileLocation;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;

		setupDB();//setup database connection and tables
	}
	
	/**
	 * Connect to database and setup tables if not already set up.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private void setupDB() throws ClassNotFoundException, SQLException {
		System.out.println("-------- Connecting to "+dbLocation+" -----------");
		Class.forName("org.h2.Driver");
		connection = DriverManager.getConnection("jdbc:h2:"+dbLocation+useSSL+ignoreCase, dbUsername, dbPassword);
		System.out.println("-------- Connected to "+dbLocation+" -----------");
		setupTables();

		//Set all online statuses to 0 when database starts.
		String sql = "UPDATE userProfile "
					+ "SET Online = '0' ";
				connection.prepareStatement(sql).executeUpdate();
	}
	
	/**
	 * Sets up the user profile and user history sql tables.
	 * @throws SQLException
	 */
	private void setupTables() throws SQLException {
		//*****setup tables*****
		
		//setup userProfile table
		String sql = "CREATE TABLE IF NOT EXISTS userProfile (\r\n"
				+ "  idUser INT(11) NOT NULL AUTO_INCREMENT,\r\n"
				+ "  nickname VARCHAR_IGNORECASE(63) NOT NULL,\r\n"
				+ "  email VARCHAR_IGNORECASE(63) NOT NULL,\r\n"
				+ "  password VARCHAR(63) NOT NULL,\r\n"
				+ "  online TINYINT(1) NULL DEFAULT NULL,\r\n"
				+ "  PRIMARY KEY (idUser),\r\n"
				+ "  UNIQUE INDEX idUser_UNIQUE (idUser ASC),\r\n"
				+ "  UNIQUE INDEX nickname_UNIQUE (nickname ASC),\r\n"
				+ "  UNIQUE INDEX Email_UNIQUE (Email ASC))\r\n"
				+ "ENGINE = InnoDB\r\n"
				+ "AUTO_INCREMENT = 1"
				+ ";";
		connection.prepareStatement(sql).executeUpdate();
		
		//setup userHistory table
		sql = "CREATE TABLE IF NOT EXISTS `userHistory` (\r\n" + 
				"  `idUser` INT(11) NOT NULL,\r\n" + 
				"  `opponent` INT(11) NOT NULL,\r\n" + 
				"  `startTimestamp` TIMESTAMP NULL DEFAULT NULL,\r\n" + 
				"  `endTimestamp` TIMESTAMP NULL DEFAULT NULL,\r\n" + 
				"  `won` TINYINT(1) NULL DEFAULT NULL,\r\n" + 
				"  `abandoned` TINYINT(1) NULL DEFAULT NULL,\r\n" + 
				"    FOREIGN KEY (`idUser`)\r\n" + 
				"    REFERENCES `userprofile` (`idUser`)\r\n" + 
				"    ON DELETE CASCADE\r\n" + 
				"    ON UPDATE NO ACTION)\r\n" + 
				"ENGINE = InnoDB;";
		connection.prepareStatement(sql).executeUpdate();
			
		//setup gameList table
		sql = "CREATE TABLE IF NOT EXISTS `gameList` (\r\n" + 
				"  `gameID` INT(11) NOT NULL,\r\n" + 
				"  `user1` INT(11) NOT NULL,\r\n" + 
				"  `user2` INT(11) NOT NULL,\r\n" + 
				"  `startTimestamp` TIMESTAMP NULL DEFAULT NULL,\r\n" + 
				"  `lastTurnTime` TIMESTAMP NULL DEFAULT NULL,\r\n" + 
				"  `playerTurn` TINYINT(1) NULL DEFAULT NULL,\r\n" + //who's turn it should is currently. (1 or 2)
				"  `gameConfig` VARCHAR(63) NOT NULL,\r\n" +
				"  UNIQUE INDEX gameID_UNIQUE (gameID ASC),\r\n" +
				"    FOREIGN KEY (`user1`)\r\n" + 
				"    REFERENCES `userprofile` (`idUser`),\r\n" + 
				"    FOREIGN KEY (`user2`)\r\n" + 
				"    REFERENCES `userprofile` (`idUser`)\r\n" + 
				"    ON DELETE CASCADE\r\n" + 
				"    ON UPDATE NO ACTION)\r\n" + 
				"ENGINE = InnoDB;";
		connection.prepareStatement(sql).executeUpdate();
	
		//setup user invite table
		sql = "CREATE TABLE IF NOT EXISTS `userInvites` (\r\n" + 
				"  `idUser` INT(11) NOT NULL,\r\n" + 
				"  `opponent` INT(11) NOT NULL,\r\n" + 
				"  UNIQUE INDEX opponent_UNIQUE (opponent ASC),\r\n" + 
				"    FOREIGN KEY (`idUser`)\r\n" + 
				"    REFERENCES `userprofile` (`idUser`)\r\n" + 
				"    ON DELETE CASCADE\r\n" + 
				"    ON UPDATE NO ACTION)\r\n" + 
				"ENGINE = InnoDB;";
		connection.prepareStatement(sql).executeUpdate();
			
		System.out.println("-------- Database tables configured -------");
	}
	
	/**
	 * Drop tables and create new empty tables. 
	 * @throws SQLException
	 */
	public void resetTable() throws SQLException{
		System.out.println("-------- Database tables deleted -------");
		connection.prepareStatement("DROP TABLE IF EXISTS userHistory").execute();
		connection.prepareStatement("DROP TABLE IF EXISTS userProfile").execute();
		connection.prepareStatement("DROP TABLE IF EXISTS gameList"   ).execute();
		connection.prepareStatement("DROP TABLE IF EXISTS userInvites").execute();
		
		setupTables();
	}

	/**
	 * Check that nickname and email are both unique. If both are unique, register user in database.
	 * 
	 * @param user must contain nickname, email, and password.
	 */
	public RegisterResponse registerUser(User user) {
		if (checkUser(user)) {
			return new RegisterResponse(false, "Nickname or Email already in use");
		}

		String query = "INSERT INTO userProfile (nickname, email, password, online) "
					 + "VALUES('" + user.getNickname() + "', '" + user.getEmail() + "', '" + user.getPassword() + "', '0');";
		try {
			connection.prepareStatement(query).executeUpdate();
			return new RegisterResponse(true, "User Registered");

		} catch (SQLException e) {
			e.printStackTrace();
			return new RegisterResponse(false, "User Failed to register in database");
		}

	}

	/**
	 * Check that nickname and email are not already in use.
	 * 
	 * @param user
	 * @return
	 */
	private boolean checkUser(User user) {
		String sql = "SELECT * FROM userProfile" 
				   + " WHERE nickname = '" + user.getNickname() +"'"
				   + " OR Email = '" + user.getEmail() +"'"
				   + " ;";
		try {
			ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
			int n=0;
			while(rtnSet.next()) {
				n++; 
			}
			if(n>0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public RegisterResponse registerUser(String email, String nickname, String password) {
		return registerUser(new User(email, nickname, password));
	}

	/**
	 * Allows user to remove their account and all game records. 
	 * 
	 * @param email User email address
	 * @param password user password
	 * 
	 * @return unregister response
	 */
	public UnregisterResponse unRegisterUser(String email, String password) {
		
		//search for Email with matching password
		String sql = "SELECT * FROM userProfile" 
				   + " WHERE Email = '" + email	+ "'"			//if email matches 
				   + " AND Password = '" + password +"'"	//AND password (Case Sensitive) matches
				   + " ;";
		ResultSet rtnSet = null;
		try {
			rtnSet = connection.prepareStatement(sql).executeQuery();
			int n = 0;
			int idUser = -1;
			while (rtnSet.next()) {
				n++; // should never be more than 1
				idUser = rtnSet.getInt("idUser");
			}
			// found 1 user
			if (n == 1) {
				sql = "SELECT * FROM gameList" + " WHERE user1 = '" + idUser + "'";
				rtnSet = connection.prepareStatement(sql).executeQuery();
				// find all current games and end them.
				while (rtnSet.next()) {
					int opponent = rtnSet.getInt("user2");
					Timestamp startTime = rtnSet.getTimestamp("startTimeStamp");
					int gameID = rtnSet.getInt("gameID");
					addGameRecord(gameID, new GameRecord(opponent, searchNickname(idUser), startTime, new Timestamp(System.currentTimeMillis()),true, true),
							 			  new GameRecord(idUser, searchNickname(opponent), startTime, new Timestamp(System.currentTimeMillis()),false, true));
				}
				
				sql = "SELECT * FROM gameList" + " WHERE user2 = '" + idUser + "'";
				rtnSet = connection.prepareStatement(sql).executeQuery();
				// find all current games and end them.
				while (rtnSet.next()) {
					int opponent = rtnSet.getInt("user1");
					Timestamp startTime = rtnSet.getTimestamp("startTimeStamp");
					int gameID = rtnSet.getInt("gameID");
					addGameRecord(gameID, new GameRecord(opponent, searchNickname(idUser), startTime, new Timestamp(System.currentTimeMillis()),true, true),
							 			  new GameRecord(idUser, searchNickname(opponent), startTime, new Timestamp(System.currentTimeMillis()),false, true));
				}	
				
				// remove user
				sql = "DELETE FROM userProfile WHERE idUser = " + idUser;
				connection.prepareStatement(sql).executeUpdate();
				return new UnregisterResponse(true, "Account Deleted");
			}
			if (n == 0) {
				return new UnregisterResponse(false, "Account not found");
			}
			if(n>1) {
				System.err.println("SERIOUS DATABASE ERROR OCCURED!");
				return new UnregisterResponse(false, "Serious error occured");
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return new UnregisterResponse(false, "Failed to remove user");
					
		}
	}

	/**
	 * User sends email and password to login.
	 * returns login response.
	 */
	public LoginResponse authenticateUser(String email, String password) {
		String sql = "SELECT * FROM userProfile "
				   + "WHERE email LIKE '" + email + "'"
				   + "AND password LIKE '" + password + "'";
		try {
			ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
			int idUser = -1;
			User tempUser = null;
			int n = 0;
			while (rtnSet.next()) {
				n++; // should never be more than 1
				idUser = rtnSet.getInt("idUser");
				tempUser = new User(idUser,rtnSet.getString("Email"),rtnSet.getString("nickname"),"", UserStatus.ONLINE,gameHistory(idUser),invites(idUser),gameIDs(idUser)); 
			}
			// found 1 user
			if (n == 1) {
				// Return user information
				sql = "UPDATE userProfile "
					+ "SET Online = '1' "
					+ "WHERE idUser = '" + idUser + "'"; 
				connection.prepareStatement(sql).executeUpdate();
				return new LoginResponse(true, tempUser, "User login successful.");
			}
			if (n == 0) {
				return new LoginResponse(false, new User(), "User not found.");
			}
			if (n > 1) {
				System.err.println("SERIOUS DATABASE ERROR OCCURED!");
				return new LoginResponse(false, new User(), "Serious Error Occured.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new LoginResponse(false, new User(), "User login failed.");
	}

	/**
	 * Logout sets user profile to offline.
	 */
	public void logout(User user) {
		String sql = "UPDATE userProfile "
				   + "SET Online = '0' "
				   + "WHERE nickname = '" + user.getNickname() + "'";
		try {
			connection.prepareStatement(sql).executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Set<User> onlineUsers(){
		Set<User> onlineUsers = new HashSet<>();
		
		String sql = "SELECT * FROM userProfile WHERE online=1";
		try {
			ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
			while (rtnSet.next()) {
				int idUser = rtnSet.getInt("idUser");
				onlineUsers.add(new User(idUser,rtnSet.getString("Email"),rtnSet.getString("nickname"),"", UserStatus.ONLINE,gameHistory(idUser),invites(idUser),gameIDs(idUser)));
			}
			// found 1 user
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return onlineUsers;
	}
	
	/**
	 * Search for user profile and game history in database. 
	 */
	public UserResponse findUser(String nickname) {
		String sql = "SELECT * FROM userProfile WHERE nickname LIKE '" + nickname +"'";
		try {
			ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
			int n = 0;
			User tempUser = new User();
			while (rtnSet.next()) {
				n++; // should never be more than 1
				boolean online = rtnSet.getBoolean("Online");
				int idUser = rtnSet.getInt("idUser");
				if(online) {
					tempUser = new User(idUser,rtnSet.getString("Email"),rtnSet.getString("nickname"),"", UserStatus.ONLINE,gameHistory(idUser),invites(idUser),gameIDs(idUser)); //user online
				}else {
					tempUser = new User(idUser,rtnSet.getString("Email"),rtnSet.getString("nickname"),"", UserStatus.OFFLINE,gameHistory(idUser),invites(idUser),gameIDs(idUser)); //user offline
				}
			}
			// found 1 user
			if (n == 1) {
				// Return user information
				return new UserResponse(true, tempUser, "User Found.");
			}
			if (n == 0) {
				return new UserResponse(false, new User(), "User not found.");
			}
			if (n > 1) {
				System.err.println("SERIOUS DATABASE ERROR OCCURED!");
				return new UserResponse(false, new User(), "Serious Error Occured.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new UserResponse(false, new User(), "User not found");
	}
	
	/**
	 * add stored game in database. 
	 * call when a game is started.
	 * @param gameId
	 * @param user1
	 * @param user2
	 * @param startTime
	 * @param playerTurn must be a 1 or a 2 for user 1 or 2
	 * @param gameConfig
	 * @return boolean for success
	 */
	public boolean addGame(int gameId, int user1, int user2, Timestamp startTime, int playerTurn, String gameConfig) {
		if(gameConfig != null && gameConfig.length()!=63) {
			return false;
		}
		
		String sql = "SELECT * FROM gameList WHERE gameID = '" + gameId +"'";
		try {
			ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
			int n = 0;
			while (rtnSet.next()) {
				n++;
			}
			// found 1 user
			if (n == 1) {
				return false;
			}
			if (n == 0) {
				sql = "INSERT INTO gameList (gameID, user1, user2, startTimestamp, lastTurnTime, playerTurn, gameConfig) "
				    + "VALUES('" + gameId + "', '" + user1 + "', '" + user2 + "', '" + startTime + "', '" + new Timestamp(System.currentTimeMillis()) + "', '" + playerTurn + "', '" + gameConfig + "');";
				connection.prepareStatement(sql).executeUpdate(); 
				
				return true;// successfully added to database
			}
			if (n > 1) {
				System.err.println("SERIOUS DATABASE ERROR OCCURED!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * add stored game in database. 
	 * call when a move is made.
	 * @param gameId
	 * @param gameConfig String that can be used to represent game board. 
	 * @return whether or not successful
	 */
	public boolean updateGame(int gameId, String gameConfig, int playerTurn) {
		if(gameConfig.length()!=63) {
			return false;
		}
		String sql = "SELECT * FROM gameList WHERE gameID = '" + gameId +"'";
		try {
			ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
			int n = 0;
			while (rtnSet.next()) {
				n++;
			}
			// found 1 user
			if (n == 0) {
				return false;
			}
			if (n == 1) {
				sql = "UPDATE gameList "
						+ "SET gameConfig = '" + gameConfig +"', " + "playerTurn = '" + playerTurn + "' "
						+ "WHERE gameID = '" + gameId + "'"; 
				connection.prepareStatement(sql).executeUpdate(); 
				
				return true;// successfully added to database
			}
			if (n > 1) {
				System.err.println("SERIOUS DATABASE ERROR OCCURED!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return false;
	}

	/**
	 * returns game configuration
	 * @param idUser
	 * @return 63 character string representing the game board
	 */
	public gameInfo findGame(int gameID) {
		String sql = "SELECT * FROM gameList WHERE gameID = '" + gameID +"'";
		gameInfo rtnGame = null;
		try {
			ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
			int n = 0;

			while (rtnSet.next()) {
				n++;
				rtnGame = new gameInfo(gameID, rtnSet.getShort("user1"),rtnSet.getInt("user2"),rtnSet.getTimestamp("startTimeStamp"),rtnSet.getInt("playerTurn"),rtnSet.getString("gameConfig")); rtnSet.getString("gameConfig");
			}
			// found 1 user
			if (n == 0) {
				return null;
			}
			if (n == 1) {
				return rtnGame;
			}
			if (n > 1) {
				System.err.println("SERIOUS DATABASE ERROR OCCURED!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return null;
	}
	
	/**
	 * Adds game records of both users in a game to the database. Also removes game from running games table. 
	 * @param record1 
	 * @param record2
	 * @return true for successfully added to database, false when unsuccessful. 
	 */
	public boolean addGameRecord(int gameID, GameRecord record1, GameRecord record2) {

		try {
			int idUser1 = record1.getIdUser();
			int idUser2 = record2.getIdUser();
			Timestamp startTime1 = record1.getStartTime();
			Timestamp startTime2 = record2.getStartTime();
			Timestamp endTime1 = record1.getEndTime();
			Timestamp endTime2 = record2.getEndTime();
			int won1=0;
			if(record1.isWon()) { 
				won1=1;
			}
			int won2=0;
			if(record2.isWon()) { 
				won2=1;
			}
			int abandoned=0;
			if(record1.isAbandoned()) { 
				abandoned=1;
			}
			
			String query = "INSERT INTO userHistory (idUser, opponent, startTimestamp, endTimeStamp, won, abandoned) "
					+ "VALUES('" + idUser1 + "', '" + idUser2 + "', '"+startTime1+"', '"+endTime1+"', '"+won1+"', '"+abandoned+"' );";
			connection.prepareStatement(query).executeUpdate();

			query = "INSERT INTO userHistory (idUser, opponent, startTimestamp, endTimeStamp, won, abandoned) "
					+ "VALUES('" + idUser2 + "', '" + idUser1 + "', '"+startTime2+"', '"+endTime2+"', '"+won2+"', '"+abandoned+"' );";
			connection.prepareStatement(query).executeUpdate();
			
			//remove from gamelist
			String sql = "SELECT * FROM gameList WHERE gameID = '" + gameID +"'";
			try {
				ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
				int n = 0;

				while (rtnSet.next()) {
					n++;
				}
				// found 1 user
				if (n == 0) {
					System.err.println("Game " + gameID + " not found!");
				}
				if (n == 1) {
					sql="DELETE FROM gameList WHERE gameID = " + gameID;
					connection.prepareStatement(sql).executeUpdate();
				}
				if (n > 1) {
					System.err.println("SERIOUS DATABASE ERROR OCCURED!");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}	
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * 
	 * @param idUser user identification number
	 * @return hashset<GameRecord> containing all game records for user
	 */
	public Set<GameRecord> gameHistory(int idUser){
		Set<GameRecord> record = new HashSet<>();
		
		String sql = "SELECT * FROM userHistory WHERE idUser = '" + idUser +"'";
			try {
				ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
				while (rtnSet.next()) {
					record.add(new GameRecord(rtnSet.getInt("idUser"), searchNickname(rtnSet.getInt("opponent")) , rtnSet.getTimestamp("startTimestamp"), rtnSet.getTimestamp("endTimestamp"), rtnSet.getBoolean("won"), rtnSet.getBoolean("abandoned")));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		return record;
	}
	
	/**
	 * Removes invite for idUser, from opponent
	 * @param idUser
	 * @param opponent
	 * @return
	 */
	public boolean removeInvite(int idUser, int opponent) {
		
		String sql = "DELETE FROM userInvites WHERE idUser = " + idUser + " AND opponent = " + opponent;
		try {
			connection.prepareStatement(sql).executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Removes all invites for a user. 
	 * One option is to use this after logging in. The user should have already received all invites, so they may be deleted. 
	 * @param idUser
	 * @return
	 */
	public boolean removeAllInvites(int idUser) {
		
		String sql = "DELETE FROM userInvites WHERE idUser = " + idUser;
		try {
			connection.prepareStatement(sql).executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private Set<String> invites(int idUser){
		Set<String> record = new HashSet<>();
		
		String sql = "SELECT * FROM userInvites WHERE idUser = '" + idUser +"'";
			try {
				ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
				while (rtnSet.next()) {
					record.add(findUser(rtnSet.getString("opponent")).getUser().getNickname());
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		return record;	
	}
	
	Set<Integer> gameIDs(int idUser){
		Set<Integer> record = new HashSet<>();
		
		String sql = "SELECT * FROM gameList WHERE "
				   + "user1 = '" + idUser +"' "
				   + "OR user2 = '" + idUser + "'";//TODO finish command
			try {
				ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
				while (rtnSet.next()) {
					record.add(rtnSet.getInt("gameID"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
		return record;	
	}
	
	/**
	 * @param idUser user identification number
	 * @return Matching nickname for given id.
	 */
	public String searchNickname(int idUser) {
		String sql = "SELECT * FROM userProfile WHERE idUser = '" + idUser +"'";
		try {
			ResultSet rtnSet = connection.prepareStatement(sql).executeQuery();
			while (rtnSet.next()) {
				return rtnSet.getString("nickname"); //user online
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return "User Not Found";
	}
	
	/**
	 * 
	 * @param inviter id of user sending invitation
	 * @param invitee id of user receiving invitation
	 * @return whether it was successfully added to database
	 */
	public boolean addInvite(int inviter, int invitee) {
		String query = "INSERT INTO userInvites (idUser, opponent) "
					 + "VALUES('" + invitee + "', '" + inviter + "', );" ;
		try {
			connection.prepareStatement(query).executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
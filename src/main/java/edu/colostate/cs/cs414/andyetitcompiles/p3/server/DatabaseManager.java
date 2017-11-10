package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import java.util.Set;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.GameRecord;
import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.LoginResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.RegisterResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.UnregisterResponse;
import edu.colostate.cs.cs414.andyetitcompiles.p3.protocol.UserResponse;


public abstract class DatabaseManager {
	// TODO add real database

	public DatabaseManager() {
	}

	/**
	 * Check that Username(nickname) and email are both unique. 
	 * If both are unique, register user in database.
	 * 
	 * @param user must contain nickname, email, and password.
	 * @return RegisterResponse
	 */
	public abstract RegisterResponse registerUser(User user);

	public abstract RegisterResponse registerUser(String email, String password, String nickname);

	public abstract UnregisterResponse unRegisterUser(String email, String password);

	public abstract LoginResponse authenticateUser(String email, String password);

	public abstract void logout(User user);

	public abstract UserResponse findUser(String username);
	
	public abstract boolean addGame(GameRecord user1, GameRecord user2);

	public abstract Set<GameRecord> gameHistory(int idUser);

	
}
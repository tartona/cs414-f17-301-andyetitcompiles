package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import java.util.Set;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

import java.util.HashSet;
import java.util.Iterator;
public class DatabaseManager {
	private Set<User> registeredUsers;
	private Set<User> users = new HashSet<User>();
	//TODO add real database
	
	public DatabaseManager() {
		
	}

	
	public boolean registerUser(User user) {
		return registeredUsers.add(user);
	}
	public boolean registerUser(String email, String password, String nickname)
	{
		return registerUser(new User(email, password, nickname));
    }
	
	public boolean unRegisterUser(User user) {
		return registeredUsers.remove(user);
	}
	
	public boolean authenticateUser(String email,String password)
	{
		for(User user:users) {
        	if(user.getemail().equalsIgnoreCase(email) || user.getnickname().equalsIgnoreCase(email)) {
        		if(user.getpassword().equals(password)) {
        			return true;
        		}
        	}
        }
		return false;
	}
	
	// take username or email as input for user.
    public void findUser(String user) 
    {
    	//iteration search
        for(Iterator<User> iterator = users.iterator();iterator.hasNext();)
        {  
        	System.out.println(iterator.next());  
        }  
    }
}

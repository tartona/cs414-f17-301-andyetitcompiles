package edu.colostate.cs.cs414.andyetitcompiles.p3.server;

import java.util.Set;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

import java.util.HashSet;
import java.util.Iterator;
public class DatabaseManager {
	private User RegisteredUsers;
	private User OnlineUsers;
	private Set<User> users = new HashSet<User>();
	
	public void registerUser(String email, String password, String nickname) 
	{
		RegisteredUsers.setemail(email);
		RegisteredUsers.setpassword(password);
		RegisteredUsers.setnickname(nickname);
    }
	public boolean authenticateUser(String email,String password)
	{
		if(OnlineUsers.getemail().equals(email)&&OnlineUsers.getpassword().equals(password))
		{   users.add(OnlineUsers);
		    return true;
		}
		else 
			return false;
    
	}
    public void findUser() 
    {
    	//iteration search
        for(Iterator<User> iterator = users.iterator();iterator.hasNext();)
        {  
        System.out.println(iterator.next());  
        }  
    }
}

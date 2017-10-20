package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

// InviteRequests are sent by the client to the server when a client invites another user to play a game
// InviteRequests are also sent by the server to the recipient of an InviteRequest sent by another client
public class InviteRequest {
	User recipient;
	User sender;
	
	public InviteRequest(User recipient, User sender) {
		this.recipient = recipient;
		this.sender = sender;
	}

}

package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

// InviteResponses are sent by a client that has received an InviteRequest from the server
// InviteResponses are also sent by the server when it receives an InviteResponse from a client
public class InviteResponse {
	boolean isAccepted;
	User sender;
	User recipient;
	
	public InviteResponse(boolean isAccepted, User sender, User recipient) {
		this.isAccepted = true;
		this.sender = sender;
		this.recipient = recipient;
	}
	
	public InviteResponse() {}

	public boolean isAccepted() {
		return isAccepted;
	}

	public User getSender() {
		return sender;
	}

	public User getRecipient() {
		return recipient;
	}

}

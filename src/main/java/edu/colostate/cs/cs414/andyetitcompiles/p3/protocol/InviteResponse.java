package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.User;

// InviteResponses are sent by a client that has received an InviteRequest from the server
// InviteResponses are also sent by the server when it receives an InviteResponse from a client
public class InviteResponse {
	boolean isAccepted;
	User inviter;
	User invitee;
	String message;
	
	public InviteResponse(boolean isAccepted, User inviter, User invitee, String message) {
		this.isAccepted = true;
		this.inviter = inviter;
		this.invitee = invitee;
		this.message = message;
	}
	
	public InviteResponse() {}

	public boolean isAccepted() {
		return isAccepted;
	}

	public User getInviter() {
		return inviter;
	}

	public User getInvitee() {
		return invitee;
	}
	
	public String getMessage() {
		return message;
	}
}

package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

public class TournamentMessage {
	String tournamentID;
	int gameID;
	String tournamentOwner;
	String player;
	String winner;
	String loser;
	String result;
	TournamentMessageType type;

	public TournamentMessage() {}

	// CREATE, START, END of the tournament
	public TournamentMessage(String tournamentID, TournamentMessageType type, String tournamentOwner) {
		this.tournamentID = tournamentID;
		this.type = type;
		this.tournamentOwner = tournamentOwner;
	}

	// JOIN, LEAVE, and RESULT? couldn't come up with overloading the constructor
	public TournamentMessage(String tournamentID, TournamentMessageType type, String tournamentOwner, String playerOrResult) {
		this.tournamentID = tournamentID;
		this.type = type;
		this.tournamentOwner = tournamentOwner;
		if(type == TournamentMessageType.RESULT){
			this.result = playerOrResult;
		}else{
			this.player = playerOrResult;
		}
	}

	// REPORT WINNER
	public TournamentMessage(String tournamentID, TournamentMessageType type, String tournamentOwner, int gameID, String winner, String loser) {
		this.tournamentID = tournamentID;
		this.type = type;
		this.tournamentOwner = tournamentOwner;
		this.gameID = gameID;
		this.winner = winner;
		this.loser = loser;
	}

	public String getTournamentID() {
		return tournamentID;
	}

	public int getGameID() {
		return gameID;
	}

	public String getTournamentOwner() {
		return tournamentOwner;
	}

	public String getPlayer() {
		return player;
	}

	public String getWinner() {
		return winner;
	}

	public String getLoser() {
		return loser;
	}

	public TournamentMessageType getType() {
		return type;
	}
}

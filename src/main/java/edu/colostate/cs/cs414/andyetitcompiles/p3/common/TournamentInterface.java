package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.util.ArrayList;

public interface TournamentInterface {


	/**
	 * @return the winner's nickname
	 * if return value is empty, the tournament is in progress
	 */
	public String getWinner();

	/**
	 * @return list of user pairs in this tournament
	 * each pair should play a game and report winner
	 */
	public ArrayList<String> getCurrentPlacement();

	/**
	 * @return round number.
	 * round 0 means the tournament hasn't started yet and another player can join
	 */
	public int getRoundNum();

	/**
	 * @return tournamentID
	 * for the management purpose
	 */
	public String getTournamentID();

	/**
	 * @return tournament history in String format
	 */
	public String getTournamentHistory();

	/**
	 * @param nicknamer of winner of the match
	 * @return 1 if the winner has successfully reported
	 */
	public int reportWinner(String nickname);

	/**
	 * @param player's nickname
	 * @return 1 if the player has successfully added to the tournament
	 * it automatically starts the tournament when the pool reaches the max num of players
	 */
	public int addPlayer(String nickname);

	/**
	 * @param player's nickname
	 * @return 1 if the player has successfully removed to the tournament
	 */
	public int removePlayer(String nickname);

	/**
	 * requires at least 2 players to start the tournament
	 * @return round number if the tournament has successfully started
	 */
	public int start();
}

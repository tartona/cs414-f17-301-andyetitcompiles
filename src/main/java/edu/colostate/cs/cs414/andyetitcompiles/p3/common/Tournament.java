package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.colostate.cs.cs414.andyetitcompiles.p3.server.JungleClientConnection;

public class Tournament implements TournamentInterface{
	private String tournamentID = "";
	private String tournamentOwner = "";
	private ArrayList<JungleClientConnection> playerConnections = new ArrayList<JungleClientConnection>();
	private HashMap<String, Integer> playerStatus = new HashMap<String, Integer>();
	private String tournamentHistory = "";
	private ArrayList<String> currentPlacement = new ArrayList<String>();
	private int round = 0;
	private int maxPlayer = 0;
	private String winner = "";

	public Tournament(String tournamentID, String tournamentOwner) {
		this.tournamentID = tournamentID;
		this.tournamentOwner = tournamentOwner;
		this.maxPlayer = 8;
	}

	public Tournament(String tournamentID, String tournamentOwner, int maxPlayer) {
		this.tournamentID = tournamentID;
		this.tournamentOwner = tournamentOwner;
		this.maxPlayer = maxPlayer;
	}

	public String getWinner() {
		return winner;
	}

	public ArrayList<String> getCurrentPlacement() {
		return currentPlacement;
	}

	public int getRoundNum() {
		return round;
	}

	public String getTournamentID() {
		return tournamentID;
	}

	public String getTournamentHistory() {
		String tmpstr = tournamentHistory.substring(0, tournamentHistory.length()-2).replace("/-", "\n");
		return tmpstr;
	}

	public int reportWinner(JungleClientConnection player) {
		if(!winner.isEmpty()){
			return 0;
		}
		boolean found = false;
		for(String tmp : currentPlacement){
			if(tmp.contains(",")&&tmp.contains(player.getUser().getNickname())){
				found = true;
				String[] tmp2 = tmp.split(",");
				if(tmp2[0].equals(player.getUser().getNickname())){
					playerStatus.replace(tmp2[1], 0);
				}else if(tmp2[1].equals(player.getUser().getNickname())){
					playerStatus.replace(tmp2[0], 0);
				}
			}
		}
		if(currentPlacement.size()==1){
			winner = player.getUser().getNickname();
		}
		if(checkEndOfRound()){
			round++;
			generatePlacement();
		}
		if(found){
			return 1;
		}else{
			return 0;
		}
	}

	public int addPlayer(JungleClientConnection player) {
		if(playerConnections.size()<maxPlayer && round==0) {
			playerConnections.add(player);
			playerStatus.put(player.getUser().getNickname(), 1);
			if(playerConnections.size()==maxPlayer) {
				this.start();
			}
			return 1;
		}else{
			return 0;
		}
	}

	public int removePlayer(JungleClientConnection player) {
		if(playerStatus.size()>1 && round==0) {
			playerConnections.remove(player);
			playerStatus.remove(player.getUser().getNickname());
			return 1;
		}else{
			return 0;
		}
	}

	private void generatePlacement() {
		currentPlacement.clear();
		String tmp="";
		for(int i=0; i<playerConnections.size(); i++){
			if(playerStatus.get(playerConnections.get(i).getUser().getNickname())==1){
				if(tmp.endsWith(",")){
					tmp+=playerConnections.get(i).getUser().getNickname();
					currentPlacement.add(tmp);
					tmp="";
				}else{
					if(i==playerConnections.size()-1 || !winner.isEmpty()){
						tmp+=playerConnections.get(i).getUser().getNickname();
						currentPlacement.add(tmp);
					}else{
						tmp+=playerConnections.get(i).getUser().getNickname()+",";
					}
				}
			}
		}

		tournamentHistory+="Round"+round+":";
		for(String pair : currentPlacement){
			tournamentHistory+=pair+"/";
		}
		tournamentHistory+="-";
	}

	private boolean checkEndOfRound() {
		int winners = 0;
		for(int i : playerStatus.values()){
			winners+=i;
		}
		if(winners==currentPlacement.size()){
			return true;
		}
		return false;
	}

	public int start() {
		if(round==0 && playerConnections.size()>1) {
			Collections.shuffle(playerConnections);
			round = 1;
			generatePlacement();
			return round;
		}else {
			return 0;
		}
	}
}

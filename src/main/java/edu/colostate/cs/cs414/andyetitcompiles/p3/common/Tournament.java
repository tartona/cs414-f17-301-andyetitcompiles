package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

import java.util.ArrayList;
import java.util.Collections;

public class Tournament implements TournamentInterface{
	private String tournamentID = "";
	private ArrayList<String> players = new ArrayList<String>();
	private ArrayList<Integer> remainingPlayers = new ArrayList<Integer>();
	private String tournamentHistory = "";
	private ArrayList<String> currentPlacement = new ArrayList<String>();
	private int round = 0;
	private int maxPlayer = 0;
	private String winner = "";

	public Tournament(String tournamentID) {
		this.tournamentID = tournamentID;
		this.maxPlayer = 8;
	}

	public Tournament(String tournamentID, int maxPlayer) {
		this.tournamentID = tournamentID;
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

	public int reportWinner(String nickname) {
		if(!winner.isEmpty()){
			return 0;
		}
		boolean found = false;
		for(String tmp : currentPlacement){
			if(tmp.contains(",")&&tmp.contains(nickname)){
				found = true;
				String[] tmp2 = tmp.split(",");
				if(tmp2[0].equals(nickname)){
					remainingPlayers.set(players.indexOf(tmp2[1]), 0);
				}else if(tmp2[1].equals(nickname)){
					remainingPlayers.set(players.indexOf(tmp2[0]), 0);
				}
			}
		}
		if(currentPlacement.size()==1){
			winner = nickname;
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

	public int addPlayer(String nickname) {
		if(players.size()<maxPlayer && round==0) {
			players.add(nickname);
			remainingPlayers.add(1);
			if(players.size()==maxPlayer) {
				this.start();
			}
			return 1;
		}else{
			return 0;
		}
	}

	public int removePlayer(String nickname) {
		if(remainingPlayers.size()>1 && round==0) {
			players.remove(nickname);
			remainingPlayers.remove(remainingPlayers.size()-1);
			return 1;
		}else{
			return 0;
		}
	}

	private void generatePlacement() {
		currentPlacement.clear();
		String tmp="";
		for(int i=0; i<players.size(); i++){
			if(remainingPlayers.get(i)==1){
				if(tmp.endsWith(",")){
					tmp+=players.get(i);
					currentPlacement.add(tmp);
					tmp="";
				}else{
					if(i==players.size()-1 || !winner.isEmpty()){
						tmp+=players.get(i);
						currentPlacement.add(tmp);
					}else{
						tmp+=players.get(i)+",";
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
		for(int i : remainingPlayers){
			winners+=i;
		}
		if(winners==currentPlacement.size()){
			return true;
		}
		return false;
	}

	public int start() {
		if(round==0 && players.size()>1) {
			//Collections.shuffle(players);
			round = 1;
			generatePlacement();
			return round;
		}else {
			return 0;
		}
	}

	public static void main(String args[]) {
		Tournament a = new Tournament("hello", 4);
		a.addPlayer("dill");
		a.addPlayer("dill2");
		a.addPlayer("dill3");
		a.addPlayer("dill4");
		ArrayList<String> place = a.getCurrentPlacement();
		for(String p : place){
			System.out.println(p);
		}
		a.reportWinner("dill");
		a.reportWinner("dill4");
		place = a.getCurrentPlacement();
		for(String p : place){
			System.out.println(p);
		}
		a.reportWinner("dill");
		place = a.getCurrentPlacement();
		for(String p : place){
			System.out.println(p);
		}
		System.out.println(a.getTournamentHistory());
		System.out.println(a.getWinner());
	}
}

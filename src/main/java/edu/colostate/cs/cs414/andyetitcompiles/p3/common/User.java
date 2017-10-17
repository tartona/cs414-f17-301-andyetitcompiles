
package edu.colostate.cs.cs414.andyetitcompiles.p3.common;

public class User {
  
    private String email;
		private String password;
		private String nickname;
		private GameRecord GR = new GameRecord();
		private UserStatus ustatus;
	    private JunglePiece piece;
		
		public String getEmail() {return this.email;}
		public String getPassword() {return this.password;}
		public String getNickname() {return this.nickname;}
		public String setEmail(String email) {return this.email = email;}
		public String setPassword(String password) {return this.password = password;}
		public String setNickname(String nickname) {return this.nickname = nickname;}

}

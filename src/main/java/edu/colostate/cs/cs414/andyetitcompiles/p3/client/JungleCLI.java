package edu.colostate.cs.cs414.andyetitcompiles.p3.client;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class JungleCLI implements Runnable {
	private BlockingQueue<String> inQueue;
	private BlockingQueue<String> outQueue;
	private JungleClient client;
	private String nickname;
	private Thread userInputThread = null;
	private JFrame gamesWindow;
	private JTabbedPane tabs;

	public JungleCLI(JungleClient client, BlockingQueue<String> inQueue, BlockingQueue<String> outQueue) {
		// Queue for incoming messages from client
		this.inQueue = inQueue;
		// Queue for outgoing messages to client
		this.outQueue = outQueue;
		this.client = client;
		// Create a the gamesWindow
		createAndShowUI();
	}
	
	private void createAndShowUI() {
		gamesWindow = new JFrame();
		tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		gamesWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gamesWindow.setTitle("XGame: Jungle");
		gamesWindow.add(tabs);
		gamesWindow.pack();
		gamesWindow.setVisible(true);
	}
	
	public void addGame(ClientGameController game) {
		tabs.addTab(game.getName(), game);
		gamesWindow.pack();
	}
	
	public void removeGame(ClientGameController game) {
		tabs.remove(game);
		gamesWindow.pack();
	}

	@Override
	public void run() {
		Scanner iStream = new Scanner(System.in);
		print("Welcome to XGame: Jungle");
		// Wait for the connected message from the client
		takeUpdate();
		// Ask for registration
		String input;
		do {
			print("Register new account? Y/N");
			input = iStream.next();
		}
		while(!input.equals("Y") && !input.equals("N"));
		if(input.equals("Y")) {
			while(true) {
				String email;
				String password;
				String nickname;
				print("Email:");
				email = iStream.next();
				print("Password:");
				password = iStream.next();
				print("Nickname:");
				nickname = iStream.next();
				client.register(email, nickname, password);
				String message = takeUpdate();
				print(message);
				if(message.equals("Registration successful")) {
					break;
				}
			}
		}
		// Ask for login
		print("Please log in to play jungle");
		while(!Thread.interrupted()) {
			String email;
			String password;
			print("Email:");
			email = iStream.next();
			print("Password:");
			password = iStream.next();
			client.login(email, password);
			String[] tmp = takeUpdate().split(":");
			String message = tmp[0];
			if(message.equals("Login successful")) {
				this.nickname = tmp[1];
				print(message);
				break;
			}
			else
				print(message);
		}
		// Main event loop
		listenForUserInput(iStream);
		while(true) {
			String message = takeUpdate();
			if(message.split(":")[0].equals("User found")) {
				print("User " + client.getRequestedUser().getNickname() + " exists. Invite or view profile?");
			}
			else if(message.split(":")[0].equals("User not found")) {
				print(message.split(":")[1]);
			}
			else if(message.split(":")[0].equals("New invite")) {
				String user = message.split(":")[1];
				print("New invitation from " + user + ". Accept? Y/N");
			}
			else if(message.split(":")[0].equals("Invite accepted")) {
				print("Your invite to user " + message.split(":")[1] + " was accepted");
			}
			else	
				print(message);
		}
	}
	
	// Start a new thread that listens for user input
	public void listenForUserInput(Scanner input) {
		userInputThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true) {
						if(input.hasNextLine()) {
							String message = input.nextLine();
							if(message.split(" ")[0].equals("find")) {
								print("Searching for user");
								client.findUser(message.split(" ")[1]);
							}
							if(message.equals("profile")) {
								System.out.print(client.getRequestedUser());
							}
							else if(message.equals("invite")) {
								client.invite(client.getRequestedUser());
								print("Invite sent");
							}
							else if(message.split(" ")[0].equals("Y")) {
								pushUpdate("Accept");
							}
							else if(message.split(" ")[0].equals("N")) {
								pushUpdate("Reject");
							}
							else if(message.equals("")) {
								// Do nothing. for some reason, when this thread starts, system.in gets an empty input
							}
							else {
								print("Unrecognized command: " + message);
							}
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
					input.close();
				}
				input.close();
			}
		});
		print("Starting user input thread");
		userInputThread.start();
	}

	private String takeUpdate() {
		try {
			return inQueue.take();
		} catch (InterruptedException e) {
			System.out.println("UI thread interupted");
			return null;
		}
	}
	private void pushUpdate(String message) {
		try {
			outQueue.put(message);
		} catch (InterruptedException e) {
			System.out.println("UI thread interupted");
		}
	}
	private void print(String message) {
		if(nickname == null)
			System.out.println("> "+message);
		else
			System.out.println(nickname + "> " + message);
	}
}

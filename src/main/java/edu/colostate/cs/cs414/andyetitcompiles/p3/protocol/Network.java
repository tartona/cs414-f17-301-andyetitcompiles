package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.Color;

import java.sql.Timestamp;

import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import edu.colostate.cs.cs414.andyetitcompiles.p3.common.*;

public class Network {
	
	static public final int port = 22222;
	static public final String host = "localhost";

	// This method registers all the classes that will be sent over the network.
	// This keeps things consistent between the server and client
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		// Classes within the project
		kryo.register(LoginRequest.class);
		kryo.register(LoginResponse.class);
		kryo.register(RegisterRequest.class);
		kryo.register(RegisterResponse.class);
		kryo.register(UnregisterRequest.class);
		kryo.register(UnregisterResponse.class);
		kryo.register(UserRequest.class);
		kryo.register(UserResponse.class);
		kryo.register(InviteRequest.class);
		kryo.register(InviteResponse.class);
		kryo.register(Set.class);
		kryo.register(User.class);
		kryo.register(HashSet.class);
		kryo.register(UserStatus.class);
		kryo.register(GameMessage.class);
		kryo.register(GameMessageType.class);
		kryo.register(GameInstance.class);
		kryo.register(GameRecord.class);
		kryo.register(Color.class);
		kryo.register(Timestamp.class);
		// Add any classes that we want to send as we develop the project
	}
}
package edu.colostate.cs.cs414.andyetitcompiles.p3.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
	
	static public final int port = 22222;
	static public final String host = "localhost";

	// This method registers all the classes that will be sent over the network.
	// This keeps things consistent between the server and client
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
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
		// Add any classes that we want to send as we develop the project
	}
}

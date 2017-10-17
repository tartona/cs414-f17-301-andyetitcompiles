package edu.colostate.cs.cs414.andyetitcompiles.kryotest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
	
	static public final int port = 22222;

	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(Message.class);
	}
}

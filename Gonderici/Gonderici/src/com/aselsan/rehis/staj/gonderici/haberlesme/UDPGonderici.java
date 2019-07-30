package com.aselsan.rehis.staj.gonderici.haberlesme;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UDPGonderici {

	// private static final int PACKET_SIZE = 3200;

	private InetAddress ip;
	private int port;
	private DatagramSocket sendSocket;

	private DatagramChannel dc;
	private InetSocketAddress sAddress;

	public UDPGonderici(String arg0, int arg1) throws IOException {

		// new InetSocketAddress(arg0, arg1);

		ip = InetAddress.getByName(arg0);
		port = arg1;

		dc = DatagramChannel.open();
		sAddress = new InetSocketAddress(ip, port);

	}

	public boolean send(ByteBuffer arg0) {

		// DatagramPacket sendPacket = new DatagramPacket(arg0, arg0.length, ip, port);

		try {

			arg0.rewind();
			dc.send(arg0, sAddress);
			// sendSocket.send(sendPacket);

		} catch (IOException e) {

			return false;
		}

		return true;
	}

}

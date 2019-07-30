package com.aselsan.rehis.staj.alici.haberlesme;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.function.Consumer;

public class UDPAlici {

	private int port;
	private ByteBuffer receiveBuffer = ByteBuffer.allocate(3229);
	private DatagramChannel dc;

	public UDPAlici(String arg0, int arg1) throws IOException {

		InetAddress.getByName(arg0);
		port = arg1;

		dc = DatagramChannel.open();
		dc.bind(new InetSocketAddress(port));

	}

	public void receive(Consumer<ByteBuffer> arg0) {
		System.out.println("receive methoduna girdi");
		Thread receiveThread = new Thread(() -> {

			while (true) {

				try {
					receiveBuffer.rewind();
					dc.receive(receiveBuffer);

					arg0.accept(receiveBuffer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		// receiveThread.setDaemon(true);
		receiveThread.start();

	}

}

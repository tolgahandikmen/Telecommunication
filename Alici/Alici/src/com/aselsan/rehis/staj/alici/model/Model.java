package com.aselsan.rehis.staj.alici.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.aselsan.rehis.staj.alici.haberlesme.UDPAlici;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Model {

	private final BooleanProperty activeProperty = new SimpleBooleanProperty(false);

	private static final AudioFormat AUDIO_FORMAT_ULAW = new AudioFormat(AudioFormat.Encoding.ULAW, 8000, 8, 1, 1, 8000,
			false);
	private static final AudioFormat AUDIO_FORMAT_PCM = new AudioFormat(8000, 16, 1, true, false);

	private final Map<Integer, LinkedBlockingQueue<byte[]>> syncMap = Collections
			.synchronizedMap(new HashMap<Integer, LinkedBlockingQueue<byte[]>>());

	public void startBroadcast(String ip, int port)
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		System.out.println("broadcast started");

		UDPAlici receiver = new UDPAlici(ip, port);
		readPackets(receiver);

		activeProperty.set(true);
	}

	/***************************************************/

	private void readPackets(UDPAlici receiver) {
		System.out.println("readPacket methoduna girdi");
		receiver.receive(this::processMessage);
		System.out.println("readPacket methoduna çýktý");
	}

	/***************************************************/

	private void processMessage(ByteBuffer arg0) {
		System.out.println("processMessage methoduna girdi");
		if (arg0.position() < 4)
			return;
		byte[] receivedData = new byte[arg0.position() - 4];
		arg0.rewind();
		int channelNumber = arg0.getInt();
		arg0.get(receivedData);
		if (!syncMap.containsKey(channelNumber)) {
			syncMap.put(channelNumber, new LinkedBlockingQueue<byte[]>());
			ULAWtoPCM2Speakers(channelNumber);
		}
		syncMap.get(channelNumber).offer(receivedData);

		System.out.println(receivedData.length);

		// int totalChannelNumber = arg0.getInt();
		// int channelNumber = arg0.getInt();
		// arg0.get(receivedData);

		// System.out.println(receivedData);
	}

	/**
	 * 
	 * @param arg0
	 *            Channel no
	 */
	private void ULAWtoPCM2Speakers(int arg0) {

		new Thread(() -> {

			SourceDataLine sdl;

			try {
				DataLine.Info hopInfo = new DataLine.Info(SourceDataLine.class, AUDIO_FORMAT_PCM);

				sdl = (SourceDataLine) AudioSystem.getLine(hopInfo);

				sdl.open();
				sdl.start();
			} catch (Exception e) {

				e.printStackTrace();

				return;

			}

			while (true) {

				try {

					byte[] receivedData = syncMap.get(arg0).take();

					byte[] outArray = new byte[1024];

					AudioInputStream aisDecoded = AudioSystem.getAudioInputStream(AUDIO_FORMAT_PCM,
							new AudioInputStream(new ByteArrayInputStream(receivedData), AUDIO_FORMAT_ULAW,
									receivedData.length));

					int readSize = aisDecoded.read(outArray);

					System.out.println(readSize);

					sdl.write(Arrays.copyOf(outArray, readSize), 0, readSize);

					System.out.println(System.currentTimeMillis());

					// System.out.println("ulaw decoded to pcm > readSize >> " + readSize);

					// sdl.drain();
					// sdl.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}).start();

	}

	/***************************************************/

	public void stopBroadcast() {
		activeProperty.set(false);
	}

	/***************************************************/

	public final BooleanProperty activeProperty() {
		return activeProperty;
	}

	/***************************************************/

}

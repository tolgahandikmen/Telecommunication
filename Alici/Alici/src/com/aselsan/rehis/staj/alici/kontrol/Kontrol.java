package com.aselsan.rehis.staj.alici.kontrol;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.aselsan.rehis.staj.alici.model.Model;

public class Kontrol {

	private final Model model;

	public Kontrol(Model arg0) {
		model = arg0;
	}

	public void startRequested(String ip, int port)
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		System.out.println("request sent");

		model.startBroadcast(ip, port);
	}

	public void stopRequest() {
		model.stopBroadcast();
	}

}

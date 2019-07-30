package com.aselsan.rehis.staj.gonderici.kontrol;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.aselsan.rehis.staj.gonderici.model.Model;

public class Kontrol {

	private final Model model;

	public Kontrol(Model arg0) {
		model = arg0;
	}

	public void setFileList(List<File> arg0) {

		if (arg0 == null) {
			return;
		}

		model.setFileList(arg0);

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

package com.aselsan.rehis.staj.gonderici.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.PCMProcessor;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.util.ByteData;
import org.kc7bfi.jflac.util.WavWriter;

import com.aselsan.rehis.staj.gonderici.haberlesme.UDPGonderici;

import javaFlacEncoder.EncodingConfiguration;
import javaFlacEncoder.FLACEncoder;
import javaFlacEncoder.FLACStreamOutputStream;
import javaFlacEncoder.FLAC_FileEncoder;
import javaFlacEncoder.StreamConfiguration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Model implements PCMProcessor {

	private final BooleanProperty activeProperty = new SimpleBooleanProperty(false);
	private final ObjectProperty<List<File>> fileListProperty = new SimpleObjectProperty<List<File>>(
			new ArrayList<File>());

	private final int BUFFER_SIZE = 3200;
	private WavWriter wav;
	private ByteBuffer byteBuf = ByteBuffer.allocate(3229);

	private int totalChannelNumber;

	private AudioInputStream audioStream = null;
	private AudioFormat audioFormat;
	private SourceDataLine sourceLine = null;

	public void startBroadcast(String ip, int port)
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		System.out.println("broadcast started");
		totalChannelNumber = fileListProperty.get().size();

		for (File file : fileListProperty.get()) {
			/* thread part */
			Thread t = new Thread(new Runnable() {
				int channel = fileListProperty.get().indexOf(file);

				@Override
				public void run() {
					try {
						UDPGonderici sender = new UDPGonderici(ip, port);
						System.out.println("Model.startBroadcast()");
						readAudioFile(file, sender, channel);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (LineUnavailableException e) {
						e.printStackTrace();
					} catch (UnsupportedAudioFileException e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
		activeProperty.set(true);

	}

	/***************************************************/

	public void stopBroadcast() {
		activeProperty.set(false);
	}

	public void setFileList(List<File> list) {
		fileListProperty.set(list);
	}

	public final BooleanProperty activeProperty() {
		return activeProperty;
	}

	public final ObjectProperty<List<File>> fileListProperty() {
		return fileListProperty;
	}

	/***************************************************/

	private File decodeAudioFile(File file) throws IOException {
		File output = new File("deneme_output.wav");
		wav = new WavWriter(Files.newOutputStream(output.toPath()));

		InputStream is = Files.newInputStream(file.toPath());

		FLACDecoder fd = new FLACDecoder(is);

		fd.addPCMProcessor(this);

		fd.decode();

		return output;
	}

	/***************************************************/

	/***************************************************/

	private byte[] decodeAudio(byte[] arg0) throws IOException {
		System.out.println("decode etmeye baþlandý");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		wav = new WavWriter(out);

		InputStream in = new ByteArrayInputStream(arg0);

		FLACDecoder fd = new FLACDecoder(in);

		fd.addPCMProcessor(this);

		fd.decode();
		System.out.println("decoded!");
		return out.toByteArray();
	}

	/***************************************************/

	private void decodeAudio2Speaker(byte[] arg0) throws IOException {
		SourceDataLine sourceLine = null;

		// InputStream ios = new ByteArrayInputStream(arg0);
		// AudioInputStream ais = new AudioInputStream(ios, audioFormat,
		// audioStream.getFrameLength());
		System.out.println(">>");
		// audioStream = AudioSystem.getAudioInputStream(ios);
		// System.out.println("gg");
		// audioFormat = ais.getFormat();

		System.out.println("***** " + arg0.length);

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		try {
			sourceLine = (SourceDataLine) AudioSystem.getLine(info);
			sourceLine.open(audioFormat);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sourceLine.start();

		System.out.println("Speaker on");

		sourceLine.write(arg0, 0, arg0.length);

		// int nBytesRead = 0;
		// byte[] abData = new byte[3200];
		//
		// int c = 0;
		// while ((nBytesRead != -1) && activeProperty().get()) {
		// c++;
		// nBytesRead = ais.read(abData, 0, abData.length);
		// // System.out.println(nBytesRead + " okundu");
		//
		// /* ses cikisi */
		// if (nBytesRead >= 0) {
		// sourceLine.write(abData, 0, nBytesRead);
		// }
		// }
		// System.out.println("c = " + c);
		sourceLine.drain();
		sourceLine.close();
		System.out.println("speaker off for the packet");
		return;
	}

	/***************************************************/
	private File encodeAudioFile(File file, UDPGonderici sender) {
		// File encoding part
		/*************/
		String outPath = file.toString() + ".flac";
		File output = new File(outPath);

		FLAC_FileEncoder fileEncoder = new FLAC_FileEncoder();

		fileEncoder.encode(file, output);
		System.out.println("input > " + file.length());
		System.out.println("output> " + output.length());

		return output;
	}

	/***************************************************/

	private byte[] encodeAudio(AudioInputStream audioStream, UDPGonderici sender)
			throws IOException, UnsupportedAudioFileException {
		// File encoding part
		/*************/
		// File output = new File("output.flac");
		//
		// FLAC_FileEncoder fileEncoder = new FLAC_FileEncoder();
		//
		// fileEncoder.encode(file, output);
		// System.out.println("input > " + file.length());
		// System.out.println("output> " + output.length());
		/*************/
		FLACEncoder flacEncoder = new FLACEncoder();

		audioFormat = audioStream.getFormat();

		StreamConfiguration streamConfiguration = new StreamConfiguration();
		streamConfiguration.setSampleRate((int) audioFormat.getSampleRate());
		streamConfiguration.setBitsPerSample(audioFormat.getSampleSizeInBits());
		streamConfiguration.setChannelCount(audioFormat.getChannels());

		flacEncoder.setStreamConfiguration(streamConfiguration);

		flacEncoder.setEncodingConfiguration(new EncodingConfiguration());

		// flacEncoder.addSamples(arg0, arg1);
		// flacEncoder.encodeSamples(arg0, false);

		System.out.println(audioStream.available());

		// AudioStreamEncoder.encodeAudioInputStream(audioStream,
		// audioStream.available(), flacEncoder, false);

		/* output stream */
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		FLACStreamOutputStream fsos = new FLACStreamOutputStream(bos);

		byte[] byteBuffer = new byte[2 * BUFFER_SIZE];
		int[] intBuffer = new int[BUFFER_SIZE];

		int readSize;

		int mark = 0;

		flacEncoder.setOutputStream(fsos);
		while ((readSize = audioStream.read(byteBuffer)) != -1) {
			flacEncoder.openFLACStream();

			ByteBuffer bb = ByteBuffer.wrap(byteBuffer, 0, readSize);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			int i;
			for (i = 0; i < readSize / 2; i++) {
				intBuffer[i] = bb.getShort();
			}
			if (readSize % 2 == 1) {
				intBuffer[i++] = bb.get();
			}
			System.out.println(i + ", " + intBuffer.length);

			flacEncoder.addSamples(intBuffer, i / audioFormat.getChannels());
			int compSize = flacEncoder.encodeSamples(i, false);
			compSize = flacEncoder.encodeSamples(flacEncoder.samplesAvailableToEncode(), true);

			System.out.println("compSize > " + compSize);
			System.out.println("i > " + i);

			System.out.println("bos size> " + bos.size());

			int newMark = bos.size();
			System.out.println("mark > " + mark);
			System.out.println("newMark > " + newMark);

			// byte[] compressed = Arrays.copyOfRange(bos.toByteArray(), mark, newMark -
			// mark);
			// System.out.println("compressed size> " + compressed.length);

			decodeAudio2Speaker(decodeAudio(bos.toByteArray()));

			// bos.reset();

			flacEncoder.clear();
			fsos.close();
			mark = newMark;

			/**************/

			// byte[] compressed = bos.toByteArray();
			// int j = 0;
			// while (j <= compressed.length) {
			//
			// byteBuf.rewind();
			//
			// byteBuf.putInt(totalChannelNumber);
			// byteBuf.putInt(2);
			// byteBuf.putFloat(audioFormat.getSampleRate());
			// byteBuf.putInt(audioFormat.getSampleSizeInBits());
			// byteBuf.putInt(audioFormat.getChannels());
			// byteBuf.putLong(audioStream.getFrameLength());
			// byteBuf.put((byte) (audioFormat.isBigEndian() ? 1 : 0));
			// if (j >= compressed.length - 3200) {
			// byteBuf.put(compressed, j, compressed.length - j);
			// } else {
			// byteBuf.put(compressed, j, 3200);
			// }
			// j += 3200;
			// sender.send(byteBuf);
			// }
		}

		/************************/

		// int frameLength = (int) audioStream.getFrameLength();
		// if (frameLength <= AudioSystem.NOT_SPECIFIED) {
		// frameLength = 3200; // arbitrary file size
		// }
		//
		// int[] sampleData = new int[frameLength];
		// byte[] samplesIn = new byte[audioFormat.getFrameSize()];
		// int i = 0;
		// while (audioStream.read(samplesIn, 0, audioFormat.getFrameSize()) != -1) {
		// if (audioFormat.getFrameSize() != 1) {
		// ByteBuffer bb = ByteBuffer.wrap(samplesIn);
		// bb.order(ByteOrder.LITTLE_ENDIAN);
		// short shortVal = bb.getShort();
		// sampleData[i] = shortVal;
		// } else {
		// sampleData[i] = samplesIn[0];
		// }
		// i++;
		// }
		//
		// flacEncoder.addSamples(sampleData, i);
		// flacEncoder.encodeSamples(i, false);
		// flacEncoder.encodeSamples(flacEncoder.samplesAvailableToEncode(), true);

		// System.out.println("fsos.size() : " + fsos.size() + " encode bitti!");

		// return bos.toByteArray();
		return null;
	}

	/**
	 * @throws UnsupportedAudioFileException
	 *************************************************/

	private void readAudioFile(File file, UDPGonderici sender, int channel)
			throws IOException, LineUnavailableException, UnsupportedAudioFileException {

		byte[] compressed;

		// InputStream is = Files.newInputStream(file.toPath());
		audioStream = AudioSystem.getAudioInputStream(file);
		audioFormat = audioStream.getFormat();
		File flacFile;
		File wavFile;

		flacFile = encodeAudioFile(file, sender);
		// compressed = encodeAudio(audioStream, sender);
		System.out.println("flacFile length > " + flacFile.length());

		wavFile = decodeAudioFile(flacFile);

		/****** speaker ******/
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(wavFile);
		AudioFormat audioFormat = audioStream.getFormat();

		SourceDataLine sourceLine = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		try {
			sourceLine = (SourceDataLine) AudioSystem.getLine(info);
			sourceLine.open(audioFormat);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sourceLine.start();
		System.out.println("Speaker on");
		int nBytesRead = 0;
		byte[] abData = new byte[BUFFER_SIZE];

		int c = 0;
		while ((nBytesRead != -1) && activeProperty().get()) {
			c++;
			nBytesRead = audioStream.read(abData, 0, abData.length);
			// System.out.println(nBytesRead + " okundu");

			/* ses cikisi */
			if (nBytesRead >= 0) {
				sourceLine.write(abData, 0, nBytesRead);
			}
		}
		sourceLine.drain();
		sourceLine.close();
		/****** speaker ******/

		/************************/
		// int i = 0;
		// while (i <= compressed.length) {
		//
		// byteBuf.rewind();
		//
		// byteBuf.putInt(totalChannelNumber);
		// byteBuf.putInt(channel);
		// byteBuf.putFloat(audioFormat.getSampleRate());
		// byteBuf.putInt(audioFormat.getSampleSizeInBits());
		// byteBuf.putInt(audioFormat.getChannels());
		// byteBuf.putLong(audioStream.getFrameLength());
		// byteBuf.put((byte) (audioFormat.isBigEndian() ? 1 : 0));
		// if (i >= compressed.length - 3200) {
		// byteBuf.put(compressed, i, compressed.length - i);
		// } else {
		// byteBuf.put(compressed, i, 3200);
		// }
		// i += 3200;
		// sender.send(byteBuf);
		// }
		//
		/************************/

		System.out.println("byteBuffer kýsmý bitti");
		//

		// compressed = decodeAudio(compressed);
		// decodeAudio2Speaker(compressed);

		// decodeAudio(file);
	}

	/***************************************************/

	@Override
	public void processPCM(ByteData arg0) {
		try {
			wav.writePCM(arg0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***************************************************/

	@Override
	public void processStreamInfo(StreamInfo arg0) {
		System.out.println(wav);
		try {
			wav.writeHeader(arg0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

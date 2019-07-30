package com.aselsan.rehis.staj.gonderici.model;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.aselsan.rehis.staj.gonderici.haberlesme.UDPGonderici;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Model {

	private static int counter = 0;

	private final BooleanProperty activeProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty micActiveProperty = new SimpleBooleanProperty(false);
	private final ObjectProperty<List<File>> fileListProperty = new SimpleObjectProperty<List<File>>(
			new ArrayList<File>());

	private static final AudioFormat AUDIO_FORMAT_ULAW = new AudioFormat(AudioFormat.Encoding.ULAW, 8000, 8, 1, 1, 8000,
			false);
	private static final AudioFormat AUDIO_FORMAT_PCM = new AudioFormat(8000, 16, 1, true, false);

	private int totalChannelNumber;
	private int total = 0;

	public void startBroadcast(String ip, int port)
			throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		System.out.println("broadcast started");
		totalChannelNumber = fileListProperty.get().size();

		UDPGonderici sender = new UDPGonderici(ip, port);
		System.out.println("Model.startBroadcast()");

		PCMtoULAWFromMic(sender);

		if (fileListProperty().isNotNull().get()) {
			System.out.println("*****************girdi*********");
			for (File file : fileListProperty.get()) {
				/* thread part */
				Thread t = new Thread(new Runnable() {
					int channel = fileListProperty.get().indexOf(file);

					@Override
					public void run() {
						PCMtoULAWFromFile(file, sender, channel);
					}
				});
				t.start();
			}
		}
		activeProperty.set(true);

	}

	/***************************************************/

	public void activateMic() {
		micActiveProperty.set(true);
	}

	/***************************************************/

	public void deactivateMic() {
		micActiveProperty.set(false);
	}

	/***************************************************/

	private boolean isMicActive() {
		return micActiveProperty.get();
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

	public final BooleanProperty micActiveProperty() {
		return micActiveProperty;
	}

	public final ObjectProperty<List<File>> fileListProperty() {
		return fileListProperty;
	}

	/***************************************************/

	private void PCMtoULAWFromMic(UDPGonderici sender) {
		new Thread(() -> {
			int threadNumber;
			synchronized (this) {
				threadNumber = counter++;
			}

			try {
				DataLine.Info micInfo = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT_PCM);

				TargetDataLine tdl = (TargetDataLine) AudioSystem.getLine(micInfo);

				/*** from mic ***/
				AudioInputStream aisMic = AudioSystem.getAudioInputStream(AUDIO_FORMAT_ULAW, new AudioInputStream(tdl));
				/*** from mic ***/

				tdl.open();

				tdl.start();

				byte[] outArray = new byte[16];

				while (activeProperty().get()) {

					if (!micActiveProperty().get()) {

						try {
							Thread.sleep(100);
						} catch (Exception e) {
						}
						continue;
					}

					aisMic.read(outArray, 0, outArray.length);

					ByteBuffer bb = ByteBuffer.allocate(20);
					bb.putInt(threadNumber);
					bb.put(outArray);

					sender.send(bb);
				}

				tdl.drain();
				tdl.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	/***************************************************/

	private void PCMtoULAWFromFile(File file, UDPGonderici sender, int channel) {
		try {
			int threadNumber;
			synchronized (this) {
				threadNumber = counter++;
			}

			System.out.println("THREAD: " + threadNumber);

			/*** file to AudioInputStream (PCM to ULAW) ***/
			AudioInputStream ais = AudioSystem.getAudioInputStream(AUDIO_FORMAT_ULAW,
					AudioSystem.getAudioInputStream(AUDIO_FORMAT_PCM, AudioSystem.getAudioInputStream(file)));

			System.out.println("file size pcm > " + file.length());

			/*** file to AudioInputStream (PCM to ULAW) ***/

			byte[] inArray = new byte[16];

			long period = 1000 * 16 / 8000;

			ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
			ses.scheduleAtFixedRate(() -> {

				if (!activeProperty().get()) {
					ses.shutdown();
				}

				int readSize;

				try {
					if ((readSize = ais.read(inArray)) != -1) {
						total += readSize;

						ByteBuffer bb = ByteBuffer.allocate(readSize + 4);

						bb.putInt(threadNumber);
						bb.put(inArray, 0, readSize);
						sender.send(bb);

					} else if (ses != null) {

						System.out.println("file size ulaw > " + total);
						ses.shutdown();

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}, 0, period, TimeUnit.MILLISECONDS);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*************************************************/

	// private void readAudioFile(File file, UDPGonderici sender, int channel)
	// throws IOException, LineUnavailableException, UnsupportedAudioFileException {
	// /*** file to AudioInputStream ***/
	// AudioInputStream ais = AudioSystem.getAudioInputStream(AUDIO_FORMAT_ULAW,
	// AudioSystem.getAudioInputStream(AUDIO_FORMAT_PCM,
	// AudioSystem.getAudioInputStream(file)));
	// /*** file to AudioInputStream ***/
	//
	// PCMtoULAWFromFile(ais, sender);
	//
	// }

	/***************************************************/
	/***************************************************/

	/***************************************************/

	/***************************************************/
	// private File encodeAudioFile(File file, UDPGonderici sender) {
	// // File encoding part
	// /*************/
	// String outPath = file.toString() + ".flac";
	// File output = new File(outPath);
	//
	// FLAC_FileEncoder fileEncoder = new FLAC_FileEncoder();
	//
	// fileEncoder.encode(file, output);
	// System.out.println("input > " + file.length());
	// System.out.println("output> " + output.length());
	//
	// return output;
	// }
	//
	// /***************************************************/
	//
	// private byte[] encodeAudio(AudioInputStream audioStream, UDPGonderici sender)
	// throws IOException, UnsupportedAudioFileException {
	// // File encoding part
	// /*************/
	// // File output = new File("output.flac");
	// //
	// // FLAC_FileEncoder fileEncoder = new FLAC_FileEncoder();
	// //
	// // fileEncoder.encode(file, output);
	// // System.out.println("input > " + file.length());
	// // System.out.println("output> " + output.length());
	// /*************/
	// FLACEncoder flacEncoder = new FLACEncoder();
	//
	// audioFormat = audioStream.getFormat();
	//
	// StreamConfiguration streamConfiguration = new StreamConfiguration();
	// streamConfiguration.setSampleRate((int) audioFormat.getSampleRate());
	// streamConfiguration.setBitsPerSample(audioFormat.getSampleSizeInBits());
	// streamConfiguration.setChannelCount(audioFormat.getChannels());
	//
	// flacEncoder.setStreamConfiguration(streamConfiguration);
	//
	// flacEncoder.setEncodingConfiguration(new EncodingConfiguration());
	//
	// // flacEncoder.addSamples(arg0, arg1);
	// // flacEncoder.encodeSamples(arg0, false);
	//
	// System.out.println(audioStream.available());
	//
	// // AudioStreamEncoder.encodeAudioInputStream(audioStream,
	// // audioStream.available(), flacEncoder, false);
	//
	// /* output stream */
	// ByteArrayOutputStream bos = new ByteArrayOutputStream();
	// FLACStreamOutputStream fsos = new FLACStreamOutputStream(bos);
	//
	// byte[] byteBuffer = new byte[2 * BUFFER_SIZE];
	// int[] intBuffer = new int[BUFFER_SIZE];
	//
	// int readSize;
	//
	// int mark = 0;
	//
	// flacEncoder.setOutputStream(fsos);
	// while ((readSize = audioStream.read(byteBuffer)) != -1) {
	// flacEncoder.openFLACStream();
	//
	// ByteBuffer bb = ByteBuffer.wrap(byteBuffer, 0, readSize);
	// bb.order(ByteOrder.LITTLE_ENDIAN);
	// int i;
	// for (i = 0; i < readSize / 2; i++) {
	// intBuffer[i] = bb.getShort();
	// }
	// if (readSize % 2 == 1) {
	// intBuffer[i++] = bb.get();
	// }
	// System.out.println(i + ", " + intBuffer.length);
	//
	// flacEncoder.addSamples(intBuffer, i / audioFormat.getChannels());
	// int compSize = flacEncoder.encodeSamples(i, false);
	// compSize = flacEncoder.encodeSamples(flacEncoder.samplesAvailableToEncode(),
	// true);
	//
	// System.out.println("compSize > " + compSize);
	// System.out.println("i > " + i);
	//
	// System.out.println("bos size> " + bos.size());
	//
	// int newMark = bos.size();
	// System.out.println("mark > " + mark);
	// System.out.println("newMark > " + newMark);
	//
	// // byte[] compressed = Arrays.copyOfRange(bos.toByteArray(), mark, newMark -
	// // mark);
	// // System.out.println("compressed size> " + compressed.length);
	//
	// decodeAudio2Speaker(decodeAudio(bos.toByteArray()));
	//
	// // bos.reset();
	//
	// flacEncoder.clear();
	// fsos.close();
	// mark = newMark;
	//
	// /**************/
	//
	// // byte[] compressed = bos.toByteArray();
	// // int j = 0;
	// // while (j <= compressed.length) {
	// //
	// // byteBuf.rewind();
	// //
	// // byteBuf.putInt(totalChannelNumber);
	// // byteBuf.putInt(2);
	// // byteBuf.putFloat(audioFormat.getSampleRate());
	// // byteBuf.putInt(audioFormat.getSampleSizeInBits());
	// // byteBuf.putInt(audioFormat.getChannels());
	// // byteBuf.putLong(audioStream.getFrameLength());
	// // byteBuf.put((byte) (audioFormat.isBigEndian() ? 1 : 0));
	// // if (j >= compressed.length - 3200) {
	// // byteBuf.put(compressed, j, compressed.length - j);
	// // } else {
	// // byteBuf.put(compressed, j, 3200);
	// // }
	// // j += 3200;
	// // sender.send(byteBuf);
	// // }
	// }
	//
	// /************************/
	//
	// // int frameLength = (int) audioStream.getFrameLength();
	// // if (frameLength <= AudioSystem.NOT_SPECIFIED) {
	// // frameLength = 3200; // arbitrary file size
	// // }
	// //
	// // int[] sampleData = new int[frameLength];
	// // byte[] samplesIn = new byte[audioFormat.getFrameSize()];
	// // int i = 0;
	// // while (audioStream.read(samplesIn, 0, audioFormat.getFrameSize()) != -1) {
	// // if (audioFormat.getFrameSize() != 1) {
	// // ByteBuffer bb = ByteBuffer.wrap(samplesIn);
	// // bb.order(ByteOrder.LITTLE_ENDIAN);
	// // short shortVal = bb.getShort();
	// // sampleData[i] = shortVal;
	// // } else {
	// // sampleData[i] = samplesIn[0];
	// // }
	// // i++;
	// // }
	// //
	// // flacEncoder.addSamples(sampleData, i);
	// // flacEncoder.encodeSamples(i, false);
	// // flacEncoder.encodeSamples(flacEncoder.samplesAvailableToEncode(), true);
	//
	// // System.out.println("fsos.size() : " + fsos.size() + " encode bitti!");
	//
	// // return bos.toByteArray();
	// return null;
	// }
	//
	// /**
	// * @throws UnsupportedAudioFileException
	//
	// @Override
	// public void processPCM(ByteData arg0) {
	// try {
	// wav.writePCM(arg0);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// /***************************************************/
	//
	// @Override
	// public void processStreamInfo(StreamInfo arg0) {
	// System.out.println(wav);
	// try {
	// wav.writeHeader(arg0);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}

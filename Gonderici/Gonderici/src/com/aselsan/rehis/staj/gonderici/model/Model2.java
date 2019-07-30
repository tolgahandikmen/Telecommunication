package com.aselsan.rehis.staj.gonderici.model;

public class Model2 {

	// private final BooleanProperty activeProperty = new
	// SimpleBooleanProperty(false);
	// private final ObjectProperty<List<File>> fileListProperty = new
	// SimpleObjectProperty<List<File>>(
	// new ArrayList<File>());
	//
	// private final int BUFFER_SIZE = 3200;
	// // private LZ4Compressor compressor =
	// // LZ4Factory.fastestInstance().fastCompressor();
	// private FLACEncoder flacEncoder = new FLACEncoder();
	// // private FLAC_ flacFE = new FLAC_FileEncoder();
	// private InputStream ios;
	// private WavWriter wav;
	//
	// public void startBroadcast(String ip, int port)
	// throws UnsupportedAudioFileException, IOException, LineUnavailableException {
	// System.out.println("broadcast started");
	//
	// for (File file : fileListProperty.get()) {
	// /* thread part */
	// Thread t = new Thread(new Runnable() {
	// @Override
	// public void run() {
	// try {
	// UDPGonderici sender = new UDPGonderici(ip, port);
	// System.out.println("Model.startBroadcast()");
	// readAudioFile(file, sender);
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (LineUnavailableException e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// t.start();
	// }
	// activeProperty.set(true);
	//
	// }
	//
	// public void stopBroadcast() {
	// activeProperty.set(false);
	// }
	//
	// public void setFileList(List<File> list) {
	// fileListProperty.set(list);
	// }
	//
	// public final BooleanProperty activeProperty() {
	// return activeProperty;
	// }
	//
	// public final ObjectProperty<List<File>> fileListProperty() {
	// return fileListProperty;
	// }
	//
	// /***************************************************/
	//
	// private byte[] encodeAudio(AudioInputStream audioStream) throws IOException {
	//
	// // FileInputStream is = new
	// FileInputStream("C:\\Users\\staj\\Music\\sendenbaska.wav");
	// // FileOutputStream fos = new FileOutputStream("deneme.wav");
	// // wav = new WavWriter(fos);
	//
	// org.kc7bfi.jflac.FLACEncoder encoder = new org.kc7bfi.jflac.FLACEncoder();
	//
	// // Compression
	// AudioFormat audioFormat;
	// audioFormat = audioStream.getFormat();
	//
	// StreamConfiguration streamConfiguration = new StreamConfiguration();
	// streamConfiguration.setSampleRate((int) audioFormat.getSampleRate());
	// streamConfiguration.setBitsPerSample(audioFormat.getSampleSizeInBits());
	// streamConfiguration.setChannelCount(audioFormat.getChannels());
	//
	// encoder.(streamConfiguration);
	//
	// /* output stream */
	// ByteArrayOutputStream bos = new ByteArrayOutputStream();
	// FLACStreamOutputStream fsos = new FLACStreamOutputStream(bos);
	//
	//
	// System.out.println(fsos.size());
	//
	// encoder.setOutputStream(fsos);
	// encoder.openFLACStream();
	//
	// int frameLength = (int) audioStream.getFrameLength();
	// if (frameLength <= AudioSystem.NOT_SPECIFIED) {
	// frameLength = 16384; // arbitrary file size
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
	// encoder.addSamples(sampleData, i);
	//
	// encoder.encodeSamples(i, false);
	// encoder.encodeSamples(encoder.samplesAvailableToEncode(), true);
	//
	// System.out.println(fsos.size());
	//
	// return bos.toByteArray();
	//
	// }
	//
	// /***************************************************/
	//
	// private void readAudioFile(File file, UDPGonderici sender) throws
	// IOException, LineUnavailableException {
	// AudioInputStream audioStream = null;
	// // AudioFormat audioFormat;
	// // SourceDataLine sourceLine = null;
	//
	// byte[] compressed = new byte[BUFFER_SIZE];
	//
	// try {
	// Files.newInputStream(file.toPath());
	// System.out.println(AudioSystem.getAudioFileFormat(file));
	//
	// audioStream = AudioSystem.getAudioInputStream(file);
	//
	// // compressed = compressAudio(audioStream);
	// compressed = encodeAudio(audioStream);
	// System.out.println("compressedLength " + compressed.length);
	//
	// sender.send(compressed);
	//
	// // decodeFlac(compressed);
	//
	// // decompressAudio(compressed);
	//
	// // audioFormat = audioStream.getFormat();
	// //
	// // DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	// // try {
	// // sourceLine = (SourceDataLine) AudioSystem.getLine(info);
	// // sourceLine.open(audioFormat);
	// // } catch (LineUnavailableException e) {
	// // e.printStackTrace();
	// // } catch (Exception e) {
	// // e.printStackTrace();
	// // }
	// // sourceLine.start();
	// //
	// // int nBytesRead = 0;
	// // byte[] abData = new byte[BUFFER_SIZE];
	// //
	// // int c = 0;
	// // while ((nBytesRead != -1) && activeProperty().get()) {
	// // c++;
	// // nBytesRead = audioStream.read(abData, 0, abData.length);
	// // // System.out.println(nBytesRead + " okundu");
	// //
	// // /* compress and send part */
	// // int compressedLen = compressor.compress(abData, 0, abData.length,
	// compressed,
	// // 0, compressed.length);
	// // // compressed = compressor.compress(abData);
	// // // flacEncoder.setEncodingConfiguration();
	// //
	// // sender.send(compressed);
	// //
	// // System.out.println((double) compressedLen / abData.length + " " +
	// // compressed.length + " <<");
	// //
	// // /* ses cikisi */
	// // if (nBytesRead >= 0) {
	// // sourceLine.write(abData, 0, nBytesRead);
	// // }
	// // }
	// // System.out.println("c = " + c);
	// // sourceLine.drain();
	// // sourceLine.close();
	// } catch (UnsupportedAudioFileException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// /***************************************************/
	//
	// private byte[] compressAudio(AudioInputStream audioStream) throws IOException
	// {
	// // Compression
	// AudioFormat audioFormat;
	// audioFormat = audioStream.getFormat();
	//
	// StreamConfiguration streamConfiguration = new StreamConfiguration();
	// streamConfiguration.setSampleRate((int) audioFormat.getSampleRate());
	// streamConfiguration.setBitsPerSample(audioFormat.getSampleSizeInBits());
	// streamConfiguration.setChannelCount(audioFormat.getChannels());
	//
	// flacEncoder.setStreamConfiguration(streamConfiguration);
	//
	// /* output stream */
	// ByteArrayOutputStream bos = new ByteArrayOutputStream();
	// FLACStreamOutputStream fsos = new FLACStreamOutputStream(bos);
	//
	// System.out.println(fsos.size());
	//
	// flacEncoder.setOutputStream(fsos);
	// flacEncoder.openFLACStream();
	//
	// int frameLength = (int) audioStream.getFrameLength();
	// if (frameLength <= AudioSystem.NOT_SPECIFIED) {
	// frameLength = 16384; // arbitrary file size
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
	//
	// flacEncoder.encodeSamples(i, false);
	// flacEncoder.encodeSamples(flacEncoder.samplesAvailableToEncode(), true);
	//
	// System.out.println(fsos.size());
	//
	// return bos.toByteArray();
	// }
	//
	// /***************************************************/
	//
	// private void decompressAudio(byte[] compressed) throws IOException,
	// UnsupportedAudioFileException {
	// AudioInputStream audioStream = null;
	// AudioFormat audioFormat;
	// SourceDataLine sourceLine = null;
	//
	// ios = new ByteArrayInputStream(compressed);
	// System.out.println(">>");
	// audioStream = AudioSystem.getAudioInputStream(ios);
	// System.out.println("gg");
	// audioFormat = audioStream.getFormat();
	//
	// DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
	// try {
	// sourceLine = (SourceDataLine) AudioSystem.getLine(info);
	// sourceLine.open(audioFormat);
	// } catch (LineUnavailableException e) {
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// sourceLine.start();
	//
	// int nBytesRead = 0;
	// byte[] abData = new byte[BUFFER_SIZE];
	//
	// int c = 0;
	// while ((nBytesRead != -1) && activeProperty().get()) {
	// c++;
	// nBytesRead = audioStream.read(abData, 0, abData.length);
	// // System.out.println(nBytesRead + " okundu");
	//
	// /* ses cikisi */
	// if (nBytesRead >= 0) {
	// sourceLine.write(abData, 0, nBytesRead);
	// }
	// }
	// System.out.println("c = " + c);
	// sourceLine.drain();
	// sourceLine.close();
	//
	// return;
	// }
	//
	// /***************************************************/
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
	// try {
	// wav.writeHeader(arg0);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}

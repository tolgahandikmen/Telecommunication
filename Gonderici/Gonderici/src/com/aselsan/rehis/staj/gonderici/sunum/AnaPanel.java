package com.aselsan.rehis.staj.gonderici.sunum;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.aselsan.rehis.staj.gonderici.kontrol.Kontrol;
import com.aselsan.rehis.staj.gonderici.model.Model;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class AnaPanel extends GridPane {

	private final Model model;
	private final Kontrol kontrol;

	private GridPane transferViewGrid = null;
	private GridPane fileViewGrid = null;

	private TextField ipField = null;
	private TextField portField = null;

	private Text ipText = null;
	private Text portText = null;
	private Text outputMessageText = null;

	private TextArea textArea = null;

	private Button startButton = null;
	private Button fileSelectButton = null;
	private Button fileListClearButton = null;

	private CheckBox micCheckBox = null;

	private Separator separator = null;

	private ScrollPane scrollPane = null;

	private FileChooser fileChooser = null;

	private final Object syncObject = new Object();
	private boolean timerRestarted;

	public AnaPanel(Model arg0, Kontrol arg1) {

		model = arg0;
		kontrol = arg1;

		getChildren().addAll(getTransferViewGrid(), getLine(), getFileViewGrid(), getOutputMessageText());
	}

	private GridPane getTransferViewGrid() {
		if (transferViewGrid == null) {
			transferViewGrid = new GridPane();

			transferViewGrid.getChildren().addAll(getIpText(), getIPField(), getPortText(), getPortField(),
					getStartButton());
			GridPane.setConstraints(transferViewGrid, 0, 0, 1, 1, HPos.LEFT, VPos.TOP, Priority.ALWAYS, Priority.NEVER);
		}
		return transferViewGrid;
	}

	private GridPane getFileViewGrid() {
		if (fileViewGrid == null) {
			fileViewGrid = new GridPane();

			fileViewGrid.getChildren().addAll(getMicCheckBox(), getFileListClearButton(), getFileSelectionButton(),
					getScrollPane(), getTextArea());
			GridPane.setConstraints(fileViewGrid, 2, 0, 1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER);
		}
		return fileViewGrid;
	}

	private TextField getIPField() {
		if (ipField == null) {
			ipField = new TextField();

			ipField.setText("127.0.0.1");
			GridPane.setConstraints(ipField, 1, 0, 1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER,
					new Insets(15));
			ipField.setMinWidth(120);
		}
		return ipField;
	}

	private TextField getPortField() {
		if (portField == null) {
			portField = new TextField();

			portField.setText("10003");
			GridPane.setConstraints(portField, 1, 1, 1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER,
					new Insets(15));
		}
		return portField;
	}

	private Text getIpText() {
		if (ipText == null) {
			ipText = new Text();
			ipText.setText("IP Number ");

			GridPane.setConstraints(ipText, 0, 0, 1, 1, HPos.LEFT, VPos.TOP, Priority.NEVER, Priority.NEVER,
					new Insets(15));
		}
		return ipText;
	}

	private Text getPortText() {
		if (portText == null) {
			portText = new Text();
			portText.setText("Port Number ");

			GridPane.setConstraints(portText, 0, 1, 1, 1, HPos.LEFT, VPos.TOP, Priority.NEVER, Priority.NEVER,
					new Insets(15));
		}
		return portText;
	}

	private Text getOutputMessageText() {
		if (outputMessageText == null) {
			outputMessageText = new Text();

			outputMessageText.setFont(Font.font("Verdana", 12));
			outputMessageText.setFill(Paint.valueOf("red"));

			GridPane.setConstraints(outputMessageText, 0, 1, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER,
					Priority.NEVER, new Insets(10));
		}

		return outputMessageText;
	}

	private Separator getLine() {
		if (separator == null) {
			separator = new Separator();
			separator.setValignment(VPos.CENTER);
			separator.setOrientation(Orientation.VERTICAL);

			GridPane.setConstraints(separator, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS,
					new Insets(15));
		}
		return separator;
	}

	private Button getFileSelectionButton() {
		if (fileSelectButton == null) {
			fileSelectButton = new Button();
			fileSelectButton.setText("Select File(s)");

			GridPane.setConstraints(fileSelectButton, 0, 1, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.NEVER,
					Priority.NEVER, new Insets(15));

			fileSelectButton.setOnMouseClicked(e -> {
				kontrol.setFileList(getFileChooser().showOpenMultipleDialog(null));
			});
		}
		return fileSelectButton;
	}

	private Button getFileListClearButton() {
		if (fileListClearButton == null) {
			fileListClearButton = new Button();
			fileListClearButton.setText("Clear");

			GridPane.setConstraints(fileListClearButton, 0, 1, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER,
					Priority.NEVER, new Insets(15));

			fileListClearButton.setOnMouseClicked(e -> {
				model.fileListProperty().set(null);
			});
		}
		return fileListClearButton;
	}

	private CheckBox getMicCheckBox() {
		if (micCheckBox == null) {
			micCheckBox = new CheckBox();
			micCheckBox.setText("Microphone on/off");
			micCheckBox.setTextFill(Paint.valueOf("red"));

			GridPane.setConstraints(micCheckBox, 0, 2, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER,
					new Insets(15));

			micCheckBox.setOnMouseClicked(e -> {
				if (!model.micActiveProperty().get()) {
					model.activateMic();

					micCheckBox.setText("Microphone on/off");
					micCheckBox.setTextFill(Paint.valueOf("green"));
				} else {
					model.deactivateMic();

					micCheckBox.setText("Microphone on/off");
					micCheckBox.setTextFill(Paint.valueOf("red"));
				}
			});
		}
		return micCheckBox;
	}

	private Button getStartButton() {
		if (startButton == null) {
			startButton = new Button();

			GridPane.setConstraints(startButton, 1, 2, 1, 1, HPos.RIGHT, VPos.TOP, Priority.ALWAYS, Priority.ALWAYS,
					new Insets(15));
			// VBox.setMargin(startButton, new Insets(15));

			startButton.setOnMouseClicked(e -> {
				if (model.activeProperty().get()) {
					kontrol.stopRequest();
					outputMessageText.setFill(Paint.valueOf("grey"));
					getOutputMessageText().setText("Akýþ durduruldu");
				} else {
					try {
						/* thread */
						kontrol.startRequested(getIPField().getText(), Integer.parseInt(getPortField().getText()));
						outputMessageText.setFill(Paint.valueOf("green"));
						getOutputMessageText().setText("Baðlantý kuruldu");
					} catch (NumberFormatException e1) {
						outputMessageText.setFill(Paint.valueOf("red"));
						getOutputMessageText().setText("Hatalý Port Numarasý Giriþi");

						new Thread(() -> {

							boolean aktif = true;

							synchronized (syncObject) {

								timerRestarted = true;
								syncObject.notifyAll();

								while (aktif) {
									try {
										syncObject.wait(2000);
										timerRestarted = false;
										System.out.println(timerRestarted);
										aktif = false;
										if (timerRestarted) {
											getOutputMessageText().setText("aaaaa");
											System.out.println("AnaPanel.getStartButton()");
											System.out.println("thread döndü");
											return;
										}
										System.out.println("thread bitti");
									} catch (InterruptedException e2) {

									}
								}

							}

							Platform.runLater(() -> getOutputMessageText().setText(""));

						}).start();

					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
						outputMessageText.setFill(Paint.valueOf("red"));
						getOutputMessageText().setText("Ses dosyasý hatasý");
						e1.printStackTrace();
					}
				}
			});

			startButton.textProperty().bind(Bindings.createStringBinding(() -> {
				if (model.activeProperty().get()) {
					return "Stop";
				}
				return "Start";

			}, model.activeProperty()));
		}
		return startButton;

	}

	private ScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new ScrollPane(getTextArea());
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);

			GridPane.setConstraints(scrollPane, 0, 0, 1, 1, HPos.LEFT, VPos.TOP, Priority.NEVER, Priority.NEVER,
					new Insets(15));

		}
		return scrollPane;
	}

	private FileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new FileChooser();

			fileChooser.getExtensionFilters().add(new ExtensionFilter("WAV", "*.wav"));
		}
		return fileChooser;
	}

	private TextArea getTextArea() {
		if (textArea == null) {
			textArea = new TextArea();

			textArea.textProperty().bind(Bindings.createStringBinding(() -> {

				StringBuilder text = new StringBuilder();
				if (model.fileListProperty().isNotNull().get()) {

					for (File file : model.fileListProperty().get()) {

						text.append(file.getName()).append("\n");

					}
				}

				return text.toString();

			}, model.fileListProperty()));

		}
		return textArea;
	}

}

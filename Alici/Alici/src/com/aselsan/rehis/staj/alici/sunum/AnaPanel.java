package com.aselsan.rehis.staj.alici.sunum;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.aselsan.rehis.staj.alici.kontrol.Kontrol;
import com.aselsan.rehis.staj.alici.model.Model;

import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class AnaPanel extends GridPane {

	private final Model model;
	private final Kontrol kontrol;

	private TextField ipField = null;
	private TextField portField = null;

	private Text ipText = null;
	private Text portText = null;
	private Text outputMessageText = null;

	// private TextArea textArea = null;

	private Button startButton = null;

	// private Separator separator = null;

	// private ScrollPane scrollPane = null;

	private GridPane transferViewGrid = null;
	// private GridPane channelsViewGrid = null;

	public AnaPanel(Model arg0, Kontrol arg1) {
		model = arg0;
		kontrol = arg1;

		getChildren().addAll(getTransferViewGrid(), /* getLine(), getChannelsViewGrid(), */ getOutputMessageText());
	}

	// private GridPane getChannelsViewGrid() {
	// if (channelsViewGrid == null) {
	// channelsViewGrid = new GridPane();
	//
	// channelsViewGrid.getChildren().addAll(getScrollPane(), getTextArea());
	// GridPane.setConstraints(channelsViewGrid, 2, 0, 1, 1, HPos.RIGHT, VPos.TOP,
	// Priority.NEVER, Priority.NEVER);
	// }
	// return channelsViewGrid;
	// }

	private GridPane getTransferViewGrid() {
		if (transferViewGrid == null) {
			transferViewGrid = new GridPane();

			transferViewGrid.getChildren().addAll(getIpText(), getPortText(), getIPField(), getPortField(),
					getStartButton());

			GridPane.setConstraints(transferViewGrid, 0, 0, 1, 1, HPos.LEFT, VPos.TOP, Priority.ALWAYS, Priority.NEVER);
		}
		return transferViewGrid;
	}

	// private Separator getLine() {
	// if (separator == null) {
	// separator = new Separator();
	// separator.setValignment(VPos.CENTER);
	// separator.setOrientation(Orientation.VERTICAL);
	//
	// GridPane.setConstraints(separator, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER,
	// Priority.NEVER, Priority.ALWAYS,
	// new Insets(15));
	// }
	// return separator;
	// }

	private Button getStartButton() {
		if (startButton == null) {
			startButton = new Button();

			GridPane.setConstraints(startButton, 1, 2, 1, 1, HPos.CENTER, VPos.TOP, Priority.NEVER, Priority.NEVER,
					new Insets(15));
		}

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
		return startButton;
	}

	private TextField getIPField() {
		if (ipField == null) {
			ipField = new TextField();

			ipField.setText("192.168.88.1");

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

	// private ScrollPane getScrollPane() {
	// if (scrollPane == null) {
	// scrollPane = new ScrollPane(getTextArea());
	// scrollPane.setFitToHeight(true);
	// scrollPane.setFitToWidth(true);
	//
	// GridPane.setConstraints(scrollPane, 0, 0, 1, 1, HPos.LEFT, VPos.TOP,
	// Priority.NEVER, Priority.NEVER,
	// new Insets(15));
	//
	// }
	// return scrollPane;
}

// private TextArea getTextArea() {
// if (textArea == null) {
// textArea = new TextArea();
//
// textArea.setText(
// "deneme\ndeneme2\ndeneme3\ndeneme4\ndeneme5\ndeneme6\ndeneme7\ndeneme8\ndeneme9\ndeneme\ndeneme2\ndeneme3\ndeneme4\ndeneme5\ndeneme6\ndeneme7\ndeneme8\ndeneme9");
// GridPane.setConstraints(textArea, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER,
// Priority.NEVER, Priority.NEVER);
// }
// return textArea;
// }

// }

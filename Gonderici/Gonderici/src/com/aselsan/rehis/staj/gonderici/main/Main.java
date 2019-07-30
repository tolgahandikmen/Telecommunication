package com.aselsan.rehis.staj.gonderici.main;

import com.aselsan.rehis.staj.gonderici.fabrika.Fabrika;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {

		launch(args);

	}

	@Override
	public void start(Stage arg0) throws Exception {
		arg0.setTitle("Gönderici");
		// arg0.setResizable(false);

		Scene scene = new Scene(Fabrika.getAnaPanel(), 660, 380);

		arg0.setScene(scene);

		arg0.show();

	}

}

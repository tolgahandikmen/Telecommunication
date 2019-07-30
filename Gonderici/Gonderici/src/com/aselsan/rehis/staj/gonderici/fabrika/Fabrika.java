package com.aselsan.rehis.staj.gonderici.fabrika;

import com.aselsan.rehis.staj.gonderici.kontrol.Kontrol;
import com.aselsan.rehis.staj.gonderici.model.Model;
import com.aselsan.rehis.staj.gonderici.sunum.AnaPanel;

public class Fabrika {

	private static AnaPanel anaPanel = null;
	private static Model model = null;
	private static Kontrol kontrol = null;

	public static AnaPanel getAnaPanel() {

		if (anaPanel == null) {

			anaPanel = new AnaPanel(getModel(), getKontrol());

		}

		return anaPanel;
	}

	public static Kontrol getKontrol() {
		if (kontrol == null) {
			kontrol = new Kontrol(getModel());
		}
		return kontrol;
	}

	public static Model getModel() {
		if (model == null) {

			model = new Model();

		}

		return model;
	}

}

package com.aselsan.rehis.staj.alici.fabrika;

import javax.sound.sampled.LineUnavailableException;

import com.aselsan.rehis.staj.alici.kontrol.Kontrol;
import com.aselsan.rehis.staj.alici.model.Model;
import com.aselsan.rehis.staj.alici.sunum.AnaPanel;

public class Fabrika {

	private static AnaPanel anaPanel = null;
	private static Model model = null;
	private static Kontrol kontrol = null;

	public static AnaPanel getAnaPanel() throws LineUnavailableException {
		if (anaPanel == null) {
			anaPanel = new AnaPanel(getModel(), getKontrol());
		}
		return anaPanel;
	}

	public static Kontrol getKontrol() throws LineUnavailableException {
		if (kontrol == null) {
			kontrol = new Kontrol(getModel());
		}

		return kontrol;
	}

	public static Model getModel() throws LineUnavailableException {
		if (model == null) {
			model = new Model();
		}

		return model;
	}
}

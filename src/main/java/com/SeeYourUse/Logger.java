package com.SeeYourUse;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Logger {
	public static void error(String text) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText("Ooooups...");
		alert.setContentText(text);
		alert.show();
	}
	public static void info(String text) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Information");
		alert.setContentText(text);
		alert.show();	
	}
}

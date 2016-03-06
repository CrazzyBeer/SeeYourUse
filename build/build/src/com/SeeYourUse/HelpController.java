package com.SeeYourUse;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class HelpController {
	
	public Pane pane;
	
	public void initialize() {
		
		final WebView webView =  new WebView();
		final WebEngine webEngine = webView.getEngine();
		
		String url = Main.class.getResource("/HTML/help.html").toExternalForm();  
		webEngine.load(url);
		pane.getChildren().add(webView);
	}
}

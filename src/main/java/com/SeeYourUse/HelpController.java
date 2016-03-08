package com.SeeYourUse;

import java.net.URI;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class HelpController {
	
	public Pane pane;
	
	public void initialize() {
		
		final WebView webView =  new WebView();
		final WebEngine webEngine = webView.getEngine();
		
		
		String url = "";
		try 
		{
		url = Main.class.getResource("/HTML/Help.html").toExternalForm();  
		} catch (Exception e) {
			Logger.logErr(e);
		}
		
		webEngine.load(url);
		//webEngine.setUserStyleSheetLocation(Main.class.getResource("/HTML/CSS/help.css").toExternalForm());
		pane.getChildren().add(webView);
	}
}

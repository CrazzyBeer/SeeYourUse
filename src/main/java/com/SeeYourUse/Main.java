package com.SeeYourUse;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
	try {

	    SQLiteDB db = new SQLiteDB();

	    db.init();

	    moveFile("Help.html");
	    moveFile("CSS/help.css");
	    moveFile("Images/GraphWindow.PNG");
	    moveFile("Images/MainWindow.PNG");
	    
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
	    Pane root = loader.load();
	    MainController controller = loader.<MainController> getController();

	    /*
	     * For Passing MainController to the GraphController and
	     * OptionsController
	     */
	    controller.setStage(primaryStage);

	    Scene scene = new Scene(root, 430, 550);
	    scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

	    primaryStage.setScene(scene);
	    primaryStage.setMinWidth(430);
	    primaryStage.setMinHeight(550);
	    primaryStage.getIcons().add(new Image("/img/thumb_up_icon.png"));

	    primaryStage.show();

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    /**
     * Moves the files from the jar to a local storage so they can be properly shown in the window
     * @param name  - file name
     */
    public void moveFile(String name) {
	// Copies the HTML File and css from Help Window to the local storage directory
	try {
	    URL htmlURL = getClass().getResource("/HTML/" + name);
	    File dest = new File(System.getProperty("user.home") + "/.SeeYourUse/HTML/" + name);
	    FileUtils.copyURLToFile(htmlURL, dest);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	launch(args);
    }
}

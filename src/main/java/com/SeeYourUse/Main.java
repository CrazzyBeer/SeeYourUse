package com.SeeYourUse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		try {
			
			SQLiteDB db = new SQLiteDB();

			db.init();

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

	public static void main(String[] args) {
		launch(args);
	}
}

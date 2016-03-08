package com.SeeYourUse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MainController {

	private Stage thisStage;

	public Button saveButton;
	public Slider pointsSlider;
	public Label pointsLabel;
	public TextArea messageField;

	public VBox messageBox;

	public Label history;

	public MenuBar menuBar;
	public Menu menuGraph;
	public MenuItem startGraphStage;

	public ImageView resetMessages;

	public TextField findField;

	public Vector<Record> currentMessages;

	public Label pagesLabel;
	public int currentPage = 1;
	public int totalPages = 1;
	public int perPage;
	public TextField pageSelect;

	public SQLiteDB db;

	/**
	 * Gets called from Main.java when MainController is called
	 */
	public void initialize() {

		// Sets message field to be empty at start
		messageField.setText(null);

		currentMessages = new Vector<Record>();

		// Checks the pageSelect field to be only for numbers
		pageSelect.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// If the string is not null and it doesn't match the criteria
				// of being a number
				// It gets replaced with it's old value
				if (!newValue.matches("\\d*") && newValue.length() != 0) {
					pageSelect.setText(oldValue);
					pageSelect.positionCaret(pageSelect.getLength());
				}
			}
		});
		// Creates a new instance of the database class
		db = new SQLiteDB();

		// Loads the messages from the database
		loadMessages();
	}

	/**
	 * Sets the currents stage of the controller, get called from Main.java
	 * 
	 * @param stage
	 */
	public void setStage(Stage stage) {
		thisStage = stage;
	}

	/**
	 * Shows the current controller stage Necessary to jump from graphing window
	 * back to main window
	 */
	public void showStage() {
		thisStage.show();
	}

	/**
	 * Every time a new vector of messages is loaded (from graph or from search)
	 * The number of pages changes in function of the number of messages
	 * selected This fuction does not load the messages, it only updates
	 * variables perPage curentPage totalPages And changes the labels
	 */
	public void paginate() {
		int messages = currentMessages.size();

		// Loads from the db the number of messages per page
		perPage = db.getPerPage();

		// Updates the current page
		currentPage = 1;

		// Updates the pageSelect textfield
		pageSelect.setText("1");

		// Calculates totalPages = ceil(messages/perPage)
		totalPages = (messages % perPage == 0) ? messages / perPage : (messages / perPage) + 1;

		changePagesLabel();
	}

	/**
	 * Changes the pagesLabel and writes down the new values of
	 * currentPage/totalPages
	 */
	public void changePagesLabel() {
		pagesLabel.setText("Page " + currentPage + "/" + totalPages);
	}

	/**
	 * Gets fired when user types any key inside pageSelect textfield
	 */
	public void pageSelectKeyReleased() {
		String asString = pageSelect.getText();
		if (asString.length() > 0) {

			int page = Integer.parseInt(pageSelect.getText());
			currentPage = page;

			changePagesLabel();

			loadPage(page);
		}

	}

	/**
	 * Loads specific page (given in parameters Calculates the index of the
	 * first and the last message from the page Clears the message box and adds
	 * new messages
	 * 
	 * @param page
	 */
	public void loadPage(int page) {
		// Indexes in vectors start from 0, so it's not (page - 1) * perPage + 1
		int from = (page - 1) * perPage;

		int to = page * perPage - 1;
		if (to > currentMessages.size() - 1)
			to = currentMessages.size() - 1;

		// Clears all the messages in the box
		messageBox.getChildren().clear();

		for (int i = from; i <= to; i++) {
			addMessage(currentMessages.elementAt(i));
		}
	}

	/**
	 * Gets called when Save button is clicked Collects point information from
	 * pointsSlider, dateNow from Calendar class And creates a String variable
	 * timestamp containing the date and points
	 */
	public void onSaveClicked() {
		// Reads the message and clears the message field
		String message = messageField.getText();
		messageField.setText(null);

		// Reads the points and sets the label and the slider to 0
		int points = (int) pointsSlider.getValue();
		pointsSlider.setValue(0);
		pointsLabel.setText("0");

		// Gets the formatted date
		String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());

		if (message != null) {
			// Saves the record to the DB with the ID bigger than the last seen
			// id in the history
			Record rec = new Record(message, dateNow, points);
			currentMessages.add(0, rec);
			db.insert(rec);
			addMessage(rec);
		}

	}

	/**
	 * Creates a new message VBox Creates a HBox and packs timestamp and buttons
	 * in there Adds the HBox to the message VBox newMessage Adds content to the
	 * message VBox newMessage Adds the message VBox (newMessage) the the main
	 * VBox (messageBox) Creates the fadeOut effect Adds content hide listeners
	 * 
	 * @param message
	 *            String. The message from the messageField
	 * @param points
	 *            Int. Points from the pointsSlider
	 */
	public void addMessage(Record rec) {

		// Container for the whole message
		VBox newMessage = new VBox();

		// Content for the top level of the message (timestamp + button)
		HBox top = new HBox();

		Label timestampLabel = new Label();
		Label content = new Label();

		HBox buttonBox = new HBox();

		// Button for removing the whole VBox newMessage from VBox messageBox
		Button delete = new Button();
		Button copy = new Button();

		timestampLabel.setText(rec.getTimestamp());
		timestampLabel.getStyleClass().add("timestamp");
		timestampLabel.setWrapText(true);
		timestampLabel.prefWidthProperty().bind(messageBox.widthProperty().subtract(50));
		/*
		 * The possibility to hide the message content by clicking on the
		 * timestamp
		 */
		timestampLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				if (content.getPrefHeight() == -1) {
					content.setPrefHeight(0);
				} else {
					content.setPrefHeight(-1);
				}

			}

		});

		/*
		 * Button to delete messages from VBox messageBox
		 */
		delete.setPrefWidth(50);
		delete.getStyleClass().add("deleteButton");
		delete.setTooltip(new Tooltip("Delete this message"));
		delete.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				db.delete(rec);
				messageBox.getChildren().remove(newMessage);
			}

		});

		/*
		 * Button to copy the message to the system defaul clipboard
		 */
		copy.setPrefWidth(30);
		copy.getStyleClass().add("copyButton");
		copy.setTooltip(new Tooltip("Copy this message"));
		copy.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String value = rec.getTimestamp() + "\n" + rec.getContent();

				final Clipboard clipboard = Clipboard.getSystemClipboard();
				final ClipboardContent content = new ClipboardContent();
				content.putString(value);
				clipboard.setContent(content);

			}

		});

		buttonBox.getChildren().addAll(copy, delete);
		timestampLabel.setGraphic(buttonBox);

		top.getChildren().addAll(timestampLabel);

		content.setText(rec.getContent());
		content.getStyleClass().add("messageContent");
		content.setWrapText(true);
		content.setTextAlignment(TextAlignment.JUSTIFY);
		content.prefWidthProperty().bind(messageBox.widthProperty().subtract(50));
		content.setPrefHeight(0);
		/*
		 * The possibility to hide the message content by clicking on the
		 * content
		 */
		content.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				if (content.getPrefHeight() == -1) {

					content.setPrefHeight(0);

				} else {

					content.setPrefHeight(-1);
				}

			}

		});

		newMessage.getChildren().addAll(top, content);
		newMessage.prefWidthProperty().bind(messageBox.widthProperty());
		newMessage.getStyleClass().add("message");

		/*
		 * MESSAGE ADDING TO THE MESSAGEBOX MESSAGE IS ADDED WITH INDEX 0, SO IT
		 * IS FIRST IN THE LIST
		 */
		messageBox.getChildren().add(0, newMessage);

		/*
		 * FadeIn transition when the message is added
		 * 
		 * FadeTransition contentFadeIN = new
		 * FadeTransition(Duration.millis(1000));
		 * contentFadeIN.setNode(content); contentFadeIN.setFromValue(0.0);
		 * contentFadeIN.setToValue(1.0); contentFadeIN.setCycleCount(1);
		 * contentFadeIN.playFromStart();
		 */
	}

	/**
	 * Loads the messaged from the DB Containing functions update variables,
	 * labels and clear the messageBox Also loadPage loads the first page
	 * according to the number of messages per page
	 */
	public void loadMessages() {
		currentMessages = db.selectMessages();

		paginate();

		loadPage(1);

	}

	/**
	 * Loads messages given from GraphController
	 * 
	 * @param messages
	 *            The messages vector loaded from the database (all the messages
	 *            in the DB)
	 * @param period
	 *            time period give by the user. This is used to properly search
	 *            for messages in the DB
	 */
	public void loadMessages(Vector<Record> messages, String period) {
		messageBox.getChildren().clear();

		history.setText("History (" + period + ")");
		history.setTooltip(new Tooltip("Reset to all time"));

		currentMessages = messages;

		paginate();

		loadPage(1);

	}

	/**
	 * Gets called when ImageView resetMessages is clicked (onMouseClicked)
	 * Resets all the selecting by loading all the messages from the db
	 */
	public void resetMessagesClicked() {
		messageBox.getChildren().clear();
		history.setText("History (All time)");
		history.setTooltip(null);

		loadMessages();
	}

	/**
	 * Label pointsLabel gets a new value every time the pointsSlider is dragged
	 * (onMouseDragged)
	 */
	public void pointsSliderDragged() {
		pointsLabel.setText(Integer.toString((int) pointsSlider.getValue()));
	}

	/**
	 * Gets fired when MenuItem menuGraph is clicked Starts a new stage
	 * containing elements from "GraphWindow.fxml"
	 */
	public void showGraphStage() {
		Stage graphStage = new Stage();
		Pane root = null;
		try {
			/* Passing MainController to the GraphController */
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GraphWindow.fxml"));
			root = loader.load();
			GraphController controller = loader.<GraphController> getController();
			controller.intializeWithMain(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

		graphStage.setScene(scene);
		graphStage.setMinWidth(630);
		graphStage.setMinHeight(470);
		graphStage.getIcons().add(new Image("/img/line_chart_icon.png"));

		graphStage.show();
	}

	/**
	 * Gets fired when menuItem "Preferences" is clicked from the menu on the
	 * top
	 */
	public void showOptionsStage() {
		Stage optionsStage = new Stage();
		Pane root = null;
		try {
			/* Passing MainController to the OptionsController */
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OptionsWindow.fxml"));
			root = loader.load();
			OptionsController controller = loader.<OptionsController> getController();
			controller.initializeWithMain(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

		optionsStage.setScene(scene);
		optionsStage.setWidth(470);
		optionsStage.setResizable(false);
		optionsStage.getIcons().add(new Image("/img/options_icon.png"));

		optionsStage.show();
	}

	/**
	 * Gets fired when menuItem "Help window" is clicked from the menu on the
	 * top
	 */
	public void showHelpStage() {
		Stage helpStage = new Stage();
		Pane root = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HelpWindow.fxml"));
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene helpScene = new Scene(root);
		helpScene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
		
		helpStage.setScene(helpScene);
		helpStage.setResizable(false);
		helpStage.show();
		
	}

	/**
	 * Gets fired when any changes occur in the findField Calls the searchFor
	 * method with the new string value
	 * 
	 * Also gets called from OptionsController when search option is changed
	 */
	public void findFieldKeyReleased() {
		String searchQuery = findField.getText();

		searchFor(searchQuery);

	}

	/**
	 * Loads new messages from the db each time a key if the findField is
	 * released Changes the "history" label by adding the Search query to the
	 * label text
	 */
	public void searchFor(String query) {

		currentMessages = db.find(query);

		if (query.length() > 0) {
			history.setText("History (Search: " + query + ")");
		} else {
			history.setText("History (All time)");
		}

		messageBox.getChildren().clear();

		for (Record r : currentMessages) {
			addMessage(r);
		}

	}

}

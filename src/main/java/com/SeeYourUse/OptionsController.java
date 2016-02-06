package com.SeeYourUse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

public class OptionsController {

	public MainController main;
	public VBox mainBox;

	public Button button10;
	public Button button20;
	public Button button50;
	public Button button100;

	public Button clearButton;

	public int currentPerPage;

	public ComboBox<String> searchOptionBox;

	public SQLiteDB db;

	/**
	 * Gets called only when the controller is created
	 */
	public void initialize() {

		db = new SQLiteDB();

		colorClearButton();

		/*
		 * Gets the currentPerPage value from the DB and assigns the active
		 * class to the corresponding button
		 */
		currentPerPage = db.getPerPage();
		Button currentActivePerPageButton = (Button) mainBox.lookup("#" + currentPerPage);

		// Wipes out the classes in the currentButton and adds them from the
		// start
		makeActive(currentActivePerPageButton);

		// Add action listeners to each perPage button
		assignListeners();

		// Adds values to comboBox
		createSearchOptionComboBox();

	}

	/*
	 * Function to get the reference to the MainController And the ability to
	 * change it from this controller
	 */
	public void initializeWithMain(MainController main) {

		this.main = main;

	}

	/**
	 * Adds action listeners for per page buttons
	 */
	public void assignListeners() {
		button10.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				perPageClicked(button10);

			}

		});

		button20.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				perPageClicked(button20);

			}

		});

		button50.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				perPageClicked(button50);

			}

		});

		button100.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				perPageClicked(button100);

			}

		});
	}

	public void createSearchOptionComboBox() {

		ObservableList<String> values = FXCollections.observableArrayList("Only content", "Content, Date and Points");

		searchOptionBox.setItems(values);

		int preferedOption = db.getSearchOption();
		searchOptionBox.setValue(values.get(preferedOption));

	}

	/**
	 * Gets fired when any button (button10, 20, 50, 100) get clicked Action
	 * listeners are coded in initialize() function
	 * 
	 * @param button
	 */
	public void perPageClicked(Button button) {

		moveActive(button);

		// Id is equal to the amount of messages per page
		int id = Integer.parseInt(button.getId());

		// Changes and saves the new amount of messages per page
		db.setPerPage(id);

		main.loadMessages();
	}

	/**
	 * Sets toButton to active and fromButton to inactive Making the toButton
	 * green and fromButton red
	 * 
	 * @param toButton
	 */
	public void moveActive(Button toButton) {

		// Clears the styles of the old button that was active
		Button fromButton = (Button) mainBox.lookup("#" + currentPerPage);
		makeInactive(fromButton);

		makeActive(toButton);

		currentPerPage = Integer.parseInt(toButton.getId());

	}

	/**
	 * Wipes all the classes and adds them from the start, making the button
	 * green
	 * 
	 * @param button
	 */
	public void makeActive(Button button) {

		button.getStyleClass().clear();
		button.getStyleClass().addAll("button", "optionsButtonActive");

	}

	/**
	 * Wipes all the classes and adds them from the start, making the button red
	 * 
	 * @param button
	 */
	public void makeInactive(Button button) {

		button.getStyleClass().clear();
		button.getStyleClass().addAll("button", "optionsButtonInactive");

	}

	/**
	 * If the current number of messages in the db is zero, the "Clear"
	 * clearButton will be red Otherwise - green
	 */
	public void colorClearButton() {

		int size = db.size();
		if (size > 0) {

			makeActive(clearButton);

		} else {

			makeInactive(clearButton);

		}
	}

	/**
	 * Gets fired the clearButton is clicked Truncates the table MESSAGES
	 */
	public void clearButtonClicked() {

		db.truncate("MESSAGES");

		main.loadMessages();

		colorClearButton();

	}

	/**
	 * Populates the database and updates the messages Also, colors the clear
	 * button in green, because there are no more 0 messages Loads the new
	 * messages in the main window
	 */
	public void populateClicked() {

		db.populate(50);

		colorClearButton();

		main.loadMessages();

	}

	/**
	 * Saves to the DB the new search preference selected by the user
	 */
	public void searchOptionChanged() {

		int option = searchOptionBox.getSelectionModel().getSelectedIndex();

		db.setSearchOption(option);

		main.findFieldKeyReleased();

	}

}

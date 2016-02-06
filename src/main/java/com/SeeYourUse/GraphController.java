package com.SeeYourUse;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Vector;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

public class GraphController {

	public TextField from;
	public TextField to;
	public Button graphButton;
	public Label viewLabel;
	public Label messagesSelected;
	public Label numberOfDays;
	public BorderPane borderPane;

	public LineChart<String, Number> messageGraph;

	public SQLiteDB db;

	public MainController main;

	public Vector<Record> selectedMessages;
	public String selectedPeriod = null;

	public void initialize() {

		selectedMessages = new Vector<Record>();

		db = new SQLiteDB();

	}

	/*
	 * Function to get the reference to the MainController And the ability to
	 * change it from this controller
	 */
	public void intializeWithMain(MainController main) {
		this.main = main;
	}

	/**
	 * graphButton actionListener
	 */
	public void graphButtonPressed() {
		String fromDate = from.getText();
		String toDate = to.getText();

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date parsedFromDate;
		Date parsedToDate;
		/*
		 * Tries to parse the date from Textfield fromDate. If it throws and
		 * error, adds the "dateEntryError" class to the TextField, which makes
		 * its borders color red
		 */
		try {
			parsedFromDate = dateFormat.parse(fromDate);
		} catch (ParseException e) {
			from.getStyleClass().add("dateEntryError");
			return;
		}

		/*
		 * Tries to parse the date from Textfield toDate. If it throws and
		 * error, adds the "dateEntryError" class to the TextField, which makes
		 * its borders color red
		 */
		try {
			parsedToDate = dateFormat.parse(toDate);
		} catch (ParseException e) {
			to.getStyleClass().add("dateEntryError");
			return;
		}

		/**
		 * SELECTS from the DB all the messages ranging between those dates
		 */
		selectedMessages = db.selectBetweenDates(fromDate, toDate);
		selectedPeriod = fromDate + " to " + toDate;

		messagesSelected.setText(selectedMessages.size() + " messages");

		drawGraph(selectedMessages);

	}

	/**
	 * Draws a graph displaying all the messages categorized by days in the
	 * given period from graphButtonPressed function
	 * 
	 * @param messages
	 *            Messages loaded from the DB in a certain period of time (given
	 *            by the user)
	 */
	public void drawGraph(Vector<Record> messages) {
		// Used to display in the label the number of selected days
		int daysNumber = 0;

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Day");
		yAxis.setLabel("Points");

		// creating the chart
		messageGraph = new LineChart<String, Number>(xAxis, yAxis);

		/*
		 * Sorts all the messages by their day The hashmap is used to stack up
		 * the points in the same days (with the same date) The time period is
		 * cut up from the whole date
		 */
		LinkedHashMap<String, Integer> days = new LinkedHashMap<String, Integer>();
		for (Record r : messages) {
			String date = r.getDate().substring(0, 10);
			int curr = 0;
			if (days.containsKey(date)) {
				curr = days.get(date);
			}
			// Adds the points to already existing
			days.put(date, curr + r.getPoints());
		}

		/*
		 * Each unique day has it's own number of points Also, each new day
		 * increases the daysNumber counter
		 */
		final Series<String, Number> series = new Series<String, Number>();
		series.setName("Points earned each day");
		for (String s : days.keySet()) {
			daysNumber++;
			series.getData().add(new Data<String, Number>(s, days.get(s)));
		}

		messageGraph.getData().add(series);
		addTooltips(series);

		numberOfDays.setText(daysNumber + " days");

		borderPane.setCenter(messageGraph);

	}

	/**
	 * Adds a tooltip containing each data's Xvalue and Yvalue
	 * 
	 * @param series
	 *            Series with messages from each DAY (String) with all the
	 *            POINTS summed (Number)
	 */
	private void addTooltips(Series<String, Number> series) {
		for (final Data<String, Number> data : series.getData()) {
			Tooltip tooltip = new Tooltip();
			// EX: 2016-11-11 100 Points
			tooltip.setText(data.getXValue() + " " + data.getYValue().toString() + " Points");
			Tooltip.install(data.getNode(), tooltip);
		}
	}

	/**
	 * When the field is selected, the "error mark" is removed So the user could
	 * now introduce the correct date
	 */
	public void fromFieldSelected() {
		from.getStyleClass().remove("dateEntryError");
	}

	public void toFieldSelected() {
		to.getStyleClass().remove("dateEntryError");
	}

	public void viewClicked() {
		if (!selectedMessages.isEmpty()) {
			main.loadMessages(selectedMessages, selectedPeriod);
			main.showStage();
		}

	}
}

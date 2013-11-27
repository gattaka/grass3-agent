package org.myftp.gattserver.grass3.agent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	class WindowButtons extends HBox {

		public WindowButtons(final Stage primaryStage) {
			Button closeBtn = new Button("x");
			closeBtn.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent actionEvent) {
					Platform.exit();
				}
			});
			Button maximizeBtn = new Button("□");
			maximizeBtn.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent actionEvent) {
					primaryStage.setIconified(true);
				}
			});
			Button minimizeBtn = new Button("_");
			minimizeBtn.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent actionEvent) {
					primaryStage.setIconified(true);
				}
			});

			this.getChildren().add(minimizeBtn);
			this.getChildren().add(maximizeBtn);
			this.getChildren().add(closeBtn);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// remove window decoration
		primaryStage.initStyle(StageStyle.TRANSPARENT);

		BorderPane borderPane = new BorderPane();
		borderPane.setStyle("-fx-background-color: green;");

		ToolBar toolBar = new ToolBar();

		int height = 25;
		toolBar.setPrefHeight(height);
		toolBar.setMinHeight(height);
		toolBar.setMaxHeight(height);
		toolBar.getItems().add(new WindowButtons(primaryStage));

		borderPane.setTop(toolBar);

		primaryStage.setScene(new Scene(borderPane, 300, 250));
		primaryStage.show();
	}
}

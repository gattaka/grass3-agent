package org.myftp.gattserver.grass3.agent;

import javax.naming.OperationNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import insidefx.undecorator.Undecorator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

	private static final int MIN_WIDTH = 500;
	private static final int MIN_HEIGHT = 400;

	private static Logger logger = LoggerFactory.getLogger(App.class);

	private Tray tray;
	private Stage stage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		this.stage = stage;
		AnchorPane root = new AnchorPane();
		root.setStyle("-fx-background-color:#ece5cc");

		Undecorator undecorator = new Undecorator(stage, root,
				"stagedecoration.fxml", StageStyle.DECORATED);
		Scene scene = new Scene(undecorator, MIN_WIDTH, MIN_HEIGHT);
		undecorator.setFullscreenVisible(false);

		// app ikona
		stage.getIcons().add(
				new Image(getClass().getResourceAsStream("grass.gif")));

		// průhlednost kolem stínu okna
		scene.setFill(Color.TRANSPARENT);
		// žádné ovládací prvky ani okraje
		stage.initStyle(StageStyle.TRANSPARENT);

		// Set minimum size
		stage.setMinWidth(MIN_WIDTH);
		stage.setMinHeight(MIN_HEIGHT);

		stage.setTitle("GRASS3 Agent");
		stage.setScene(scene);

		// Tray
		try {
			tray = new Tray() {

				@Override
				protected void onExit() {
					end();
				}

				@Override
				protected void onShowWindow() {
					showWindow();
				}
			};
		} catch (OperationNotSupportedException e) {
			logger.info("Tray icon not supported - app will minimize to taskbar");
		}

		// Listeners
		undecorator.closeProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				end();
			}
		});

		// Components

		TextField text = new TextField("Text");
		text.setMaxSize(140, 20);
		root.getChildren().add(text);
		root.setPadding(Insets.EMPTY);
		AnchorPane.setTopAnchor(text, 50.0);
		AnchorPane.setLeftAnchor(text, 10.0);

		showWindow();
	}

	private void showWindow() {
		stage.show();
	}

	private void end() {
		if (tray != null) {
			tray.destroy();
		}
		Platform.exit();
	}
}

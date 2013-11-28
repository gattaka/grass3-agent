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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	private AnchorPane root;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		this.stage = stage;
		stage.setTitle("GRASS3 Agent");
		root = new AnchorPane();
		root.setStyle("-fx-background-image: url(\"\\skin\\bgr.png\")");

		// nezavírej aplikaci když se na okně udělá hide()
		Platform.setImplicitExit(false);

		Undecorator undecorator = new Undecorator(stage, root, "stagedecoration.fxml", StageStyle.DECORATED);
		Scene scene = new Scene(undecorator, MIN_WIDTH, MIN_HEIGHT);
		undecorator.setAsStageDraggable(stage, root);

		// app ikona
		stage.getIcons().add(new Image(getClass().getResourceAsStream("grass.gif")));

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
					if (tray != null) {
						tray.destroy();
					}
					Platform.exit();
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
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				end();
			}
		});

		// Components
		ImageView titleImage = new ImageView(new Image("/skin/title.png", 142, 10, true, true));
		Glow glow = new Glow(0.8);
		titleImage.effectProperty().set(glow);
		root.getChildren().add(titleImage);
		AnchorPane.setTopAnchor(titleImage, 7.0);
		AnchorPane.setLeftAnchor(titleImage, 7.0);

		createToolBar();

		showWindow();
	}

	private void createToolBar() {
		final ToolBar toolbar = new ToolBar();
		// zajišťuje sepnutí pouze jednoho tlačítka
		ToggleGroup pageButtonGroup = new ToggleGroup();
		ToggleButton homeBtn = new ToggleButton("Home");
		homeBtn.setSelected(true);
		toolbar.getItems().add(homeBtn);

		ToggleButton settingsBtn = new ToggleButton("Settings");
		ToggleButton statusBtn = new ToggleButton("Server status");

		toolbar.getItems().add(settingsBtn);
		toolbar.getItems().add(statusBtn);

		for (Node node : toolbar.getItems()) {
			if (node instanceof ToggleButton) {
				final ToggleButton button = (ToggleButton) node;
				button.setToggleGroup(pageButtonGroup);
			}
		}

		toolbar.setMaxWidth(-1.0);
		root.getChildren().add(toolbar);
		AnchorPane.setTopAnchor(toolbar, 29.0);
		AnchorPane.setLeftAnchor(toolbar, 0.0);
		AnchorPane.setRightAnchor(toolbar, 0.0);
	}

	private void showWindow() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				stage.show();
			}
		});
	}

	private void end() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (tray != null) {
					stage.hide();
				} else {
					System.exit(0);
				}
			}
		});
	}
}

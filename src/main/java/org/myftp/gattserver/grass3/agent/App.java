package org.myftp.gattserver.grass3.agent;

import insidefx.undecorator.Undecorator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
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

import javax.naming.OperationNotSupportedException;

import org.myftp.gattserver.grass3.agent.PingAgent.PingObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App extends Application {

	private static final int MIN_WIDTH = 500;
	private static final int MIN_HEIGHT = 400;

	private static Logger logger = LoggerFactory.getLogger(App.class);

	private Tray tray;
	private Stage stage;
	private AnchorPane root;
	private ApplicationContext context;
	private PingAgent pingAgent;

	private boolean connectionStateOk = true;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		context = new ClassPathXmlApplicationContext("spring/app-context.xml");
		assert context != null;

		this.stage = stage;
		stage.setTitle("GRASS3 Agent");
		root = new AnchorPane();
		root.setStyle("-fx-background-image: url(\"skin/bgr.png\")");

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

		// Ping agent
		createPingAgent();

		// Listeners
		undecorator.closeProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				close();
			}
		});

		// Components
		createTitle();
		createToolBar();
		createGraph();

		showWindow();
	}

	private void createGraph() {
		PingChart chart = new PingChart();
		root.getChildren().add(chart);
		AnchorPane.setTopAnchor(chart, 55.0);
		AnchorPane.setLeftAnchor(chart, 5.0);
		AnchorPane.setRightAnchor(chart, 5.0);
		pingAgent.addObserver(chart);
	}

	private void createPingAgent() {
		pingAgent = context.getBean(PingAgent.class);
		pingAgent.addObserver(new PingObserver() {
			@Override
			public void onPing(Long time) {
				if (time == null) {
					if (connectionStateOk) {
						connectionStateOk = false;
						tray.showWarning("Nezdařilo se připojit k serveru");
					}
				} else {
					if (connectionStateOk == false) {
						connectionStateOk = true;
						tray.showInfo("Spojení se serverem bylo opět navázáno");
						tray.showNormal();
					}
				}
			}
		});
		pingAgent.start(500);
	}

	private void createTitle() {
		ImageView titleImage = new ImageView(new Image("skin/title.png", 142, 10, true, true));
		Glow glow = new Glow(0.8);
		titleImage.effectProperty().set(glow);
		root.getChildren().add(titleImage);
		AnchorPane.setTopAnchor(titleImage, 7.0);
		AnchorPane.setLeftAnchor(titleImage, 7.0);
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

	private void close() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// TODO odkomentovat
				// if (tray != null) {
				// stage.hide();
				// } else {
				end();
				// }
			}
		});
	}

	private void end() {
		if (tray != null) {
			tray.destroy();
		}
		if (pingAgent != null)
			pingAgent.stop();
		Platform.exit();
		System.exit(0);
	}
}
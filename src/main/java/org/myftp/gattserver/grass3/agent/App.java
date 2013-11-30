package org.myftp.gattserver.grass3.agent;

import java.util.Timer;
import java.util.TimerTask;

import javax.naming.OperationNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import insidefx.undecorator.Undecorator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
	private ApplicationContext context;

	private Timer timer;
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

		testService();

		showWindow();
	}

	private void createGraph() {

		XYChart.Data<String, Number>[] series1Data;

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis(0, 50, 10);
		final BarChart<String, Number> bc = new BarChart<String, Number>(xAxis, yAxis);
		bc.setId("pingGraph");
		bc.setLegendVisible(false);
		bc.setAnimated(false);
		bc.setBarGap(0);
		bc.setCategoryGap(1);
		bc.setVerticalGridLinesVisible(false);
		
		bc.setPrefHeight(40);

		// setup chart
		bc.setTitle("Server Ping");
		yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, null, "ms"));
		xAxis.setTickLabelsVisible(false);

		// add starting data
		XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();

		// noinspection unchecked
		series1Data = new XYChart.Data[50];
		String[] categories = new String[50];
		for (int i = 0; i < series1Data.length; i++) {
			categories[i] = Integer.toString(i + 1);
			series1Data[i] = new XYChart.Data<String, Number>(categories[i], 10);
			series1.getData().add(series1Data[i]);
		}
		bc.getData().add(series1);

		for (int i = 0; i < series1Data.length; i++) {
			series1Data[i].setYValue(i);
		}

		root.getChildren().add(bc);
		AnchorPane.setTopAnchor(bc, 55.0);
		AnchorPane.setLeftAnchor(bc, 5.0);
		AnchorPane.setRightAnchor(bc, 5.0);

	}

	private void testService() {

		final PingBean pingBean = context.getBean(PingBean.class);

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					String response = pingBean.ping();
					tray.showInfo(response);
					pingBean.ping();
					logger.info(response);
					if (connectionStateOk == false) {
						connectionStateOk = true;
						tray.showNormal();
					}
				} catch (RestClientException e) {
					logger.warn(e.getMessage());
					if (connectionStateOk) {
						connectionStateOk = false;
						tray.showWarning("Nezdařilo se připojit k serveru");
					}
				}
			}
		}, 5000, 5000);

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
		timer.cancel();
		Platform.exit();
		System.exit(0);
	}
}

package org.myftp.gattserver.grass3.agent;

import org.myftp.gattserver.grass3.agent.PingAgent.PingObserver;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class PingChart extends BarChart<String, Number> implements PingObserver {

	@SuppressWarnings("unchecked")
	private XYChart.Data<String, Number>[] series1Data = new XYChart.Data[50];

	public PingChart() {
		super(new CategoryAxis(), new NumberAxis(0, 500, 20));

		CategoryAxis xAxis = (CategoryAxis) getXAxis();
		NumberAxis yAxis = (NumberAxis) getYAxis();

		setId("pingGraph");
		setLegendVisible(false);
		setAnimated(false);
		setBarGap(0);
		setCategoryGap(1);
		setVerticalGridLinesVisible(false);

		setPrefHeight(200);

		// setup chart
		setTitle("Server Ping");
		yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, null, "ms"));
		xAxis.setTickLabelsVisible(false);

		// add starting data
		XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();

		// noinspection unchecked
		String[] categories = new String[50];
		for (int i = 0; i < series1Data.length; i++) {
			categories[i] = Integer.toString(i + 1);
			series1Data[i] = new XYChart.Data<String, Number>(categories[i], 10);
			series1.getData().add(series1Data[i]);
		}
		getData().add(series1);

	}

	@Override
	public void onPing(Long time) {
		for (int i = 0; i < series1Data.length; i++) {
			if (i < series1Data.length - 2) {
				series1Data[i].setYValue(series1Data[i + 1].getYValue());
			} else {
				series1Data[i].setYValue(time == null ? 999 : time);
			}
		}
	}

}

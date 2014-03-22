package org.myftp.gattserver.grass3.agent;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
public class PingAgent {

	@Autowired
	private IPingBean pingBean;

	private Timer timer;
	private boolean started = false;
	private Set<PingObserver> observers = new HashSet<>();

	public static interface PingObserver {
		public void onPing(Long time);
	}

	public boolean addObserver(PingObserver observer) {
		return observers.add(observer);
	}

	public boolean removeObserver(PingObserver observer) {
		return observers.remove(observer);
	}

	public boolean isStarted() {
		return started;
	}

	public void start(long period) {
		started = true;
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					Long response = pingBean.ping();
					for (PingObserver observer : observers)
						observer.onPing(response);
				} catch (RestClientException e) {
					for (PingObserver observer : observers)
						observer.onPing(null);
				}
			}
		}, 1, period);
	}

	public void stop() {
		started = false;
		timer.cancel();
	}

}

package org.myftp.gattserver.grass3.agent;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.web.client.RestClientException;

/**
 * @param <O> Typ observeru
 * @param <R> Typ návratové pozorované hodnoty 
 */
public abstract class TimedAgent<O extends Observer<R>,R> {

	private Timer timer;
	private boolean started = false;
	private Set<O> observers = new HashSet<>();

	protected abstract R checkEvent(); 
	
	public boolean addObserver(O observer) {
		return observers.add(observer);
	}

	public boolean removeObserver(O observer) {
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
					R response = checkEvent();
					for (O observer : observers)
						observer.onEvent(response);
				} catch (RestClientException e) {
					for (O observer : observers)
						observer.onEvent(null);
				}
			}
		}, 1, period);
	}

	public void stop() {
		started = false;
		timer.cancel();
	}

}

package org.myftp.gattserver.grass3.agent;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.net.URL;

import javax.naming.OperationNotSupportedException;
import javax.swing.ImageIcon;

abstract class Tray {

	private static final String ERROR_IMG = "grass_err.gif";
	private static final String INFO_IMG = "grass_info.gif";
	private static final String WARN_IMG = "grass_warn.gif";
	private static final String TIME_IMG = "grass_time.gif";
	private static final String OK_IMG = "grass.gif";

	private TrayIcon trayIcon;
	private SystemTray tray;
	private PopupMenu popup;

	public void showError(String msg) {
		configureTray(ERROR_IMG, "Problém");
		trayIcon.displayMessage("Problém", msg, TrayIcon.MessageType.ERROR);
	}

	public void showInfo(String msg) {
		configureTray(INFO_IMG, "Info");
		trayIcon.displayMessage("Info", msg, TrayIcon.MessageType.INFO);
	}

	public void showWarning(String msg) {
		configureTray(WARN_IMG, "Upozornění");
		trayIcon.displayMessage("Upozornění", msg, TrayIcon.MessageType.WARNING);
	}

	public void showRemind(String msg) {
		configureTray(TIME_IMG, "Připomínka");
		trayIcon.displayMessage("Připomínka", msg, TrayIcon.MessageType.INFO);
	}

	public void showNormal() {
		configureTray(OK_IMG);
	}

	private void configureTray(String imgName) {
		configureTray(imgName, null);
	}

	private void configureTray(String imgName, String description) {
		if (tray == null)
			return;
		trayIcon.setImage(createImage(imgName, "tray icon"));
		trayIcon.setToolTip("GRASS3 Agent" + (description == null ? "" : " - ${description}"));
	}

	private static Image createImage(String path, String description) {
		URL imageURL = Tray.class.getResource(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}

	public void destroy() {
		tray.remove(trayIcon);
	}

	Tray() throws OperationNotSupportedException {

		if (!SystemTray.isSupported()) {
			throw new OperationNotSupportedException();
		}

		popup = new PopupMenu();
		tray = SystemTray.getSystemTray();

		// Create a pop-up menu components
		MenuItem agentItem = new MenuItem("Grass3 Agent menu");
		MenuItem exitItem = new MenuItem("Konec");

		// Add components to pop-up menu
		popup.add(agentItem);
		popup.addSeparator();
		popup.add(exitItem);

		trayIcon = new TrayIcon(createImage(OK_IMG, "tray icon"));
		trayIcon.setPopupMenu(popup);

		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				onShowWindow();
			}
		});

		showNormal();

		agentItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onShowWindow();
			}
		});
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExit();
			}
		});

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
		}

	}

	protected abstract void onExit();

	protected abstract void onShowWindow();

}
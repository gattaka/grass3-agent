package org.myftp.gattserver.grass3.agent

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.naming.OperationNotSupportedException;
import javax.swing.ImageIcon

abstract class Tray {

	private def TrayIcon trayIcon;
	private def SystemTray tray;
	private def PopupMenu popup;

	def showError(String msg) {
		configureTray("grass_err.gif","Problém");
		trayIcon.displayMessage("Problém", msg,
				TrayIcon.MessageType.ERROR);
	}

	def showInfo(String msg) {
		configureTray("grass_info.gif","Info");
		trayIcon.displayMessage("Info", msg,
				TrayIcon.MessageType.INFO);
	}

	def showWarning(String msg) {
		configureTray("grass_warn.gif","Upozornění");
		trayIcon.displayMessage("Upozornění", msg,
				TrayIcon.MessageType.WARNING);
	}

	def showRemind(String msg) {
		configureTray("grass_time.gif","Připomínka");
		trayIcon.displayMessage("Připomínka", msg,
				TrayIcon.MessageType.INFO);
	}

	def showNormal() {
		configureTray("grass.gif");
	}

	private def configureTray(String imgName) {
		configureTray(imgName, null);
	}

	private def configureTray(String imgName, String description) {
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

		//Add components to pop-up menu
		popup.add(agentItem);
		popup.addSeparator();
		popup.add(exitItem);

		trayIcon = new TrayIcon();
		trayIcon.setPopupMenu(popup);

		showNormal();

		agentItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onShowWindow();
					}
				})
		exitItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onExit();
					}
				})

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
		}

	}

	protected abstract void onExit();

	protected abstract void onShowWindow();

}

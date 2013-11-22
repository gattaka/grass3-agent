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

import javax.swing.ImageIcon

class Tray {

	private def TrayIcon trayIcon;
	private def SystemTray tray;
	private def PopupMenu popup;

	def showError(String msg) {
		configureTray("grass_err.gif","Probl�m");
		trayIcon.displayMessage("Probl�m", msg,
				TrayIcon.MessageType.ERROR);
	}

	def showInfo(String msg) {
		configureTray("grass_info.gif","Info");
		trayIcon.displayMessage("Info", msg,
				TrayIcon.MessageType.INFO);
	}

	def showWarning(String msg) {
		configureTray("grass_warn.gif","Upozorn�n�");
		trayIcon.displayMessage("Upozorn�n�", msg,
				TrayIcon.MessageType.WARNING);
	}

	def showRemind(String msg) {
		configureTray("grass_time.gif","P�ipom�nka");
		trayIcon.displayMessage("P�ipom�nka", msg,
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

	Tray() {

		if (!SystemTray.isSupported()) {
			println "SystemTray is not supported";
			return;
		}

		popup = new PopupMenu();
		tray = SystemTray.getSystemTray();

		// Create a pop-up menu components
		MenuItem aboutItem = new MenuItem("O aplikaci");
		CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
		CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
		Menu displayMenu = new Menu("Zobrazit");
		MenuItem errorItem = new MenuItem("Probl�m");
		MenuItem warningItem = new MenuItem("Upozorn�n�");
		MenuItem infoItem = new MenuItem("Info");
		MenuItem remindItem = new MenuItem("P�ipom�nka");
		MenuItem noneItem = new MenuItem("Nic");
		MenuItem exitItem = new MenuItem("Konec");

		//Add components to pop-up menu
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(cb1);
		popup.add(cb2);
		popup.addSeparator();
		popup.add(displayMenu);
		displayMenu.add(errorItem);
		displayMenu.add(warningItem);
		displayMenu.add(infoItem);
		displayMenu.add(remindItem);
		displayMenu.add(noneItem);
		popup.add(exitItem);

		trayIcon = new TrayIcon();
		trayIcon.setPopupMenu(popup);
		showNormal();

		errorItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showError("Chyba p�ipojen� - nem��u se p�ipojit k serveru");
					}
				});

		warningItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showWarning("Upozorn�n� - pl�nova� zaznamenal pro�lou p�ipom�nku");
					}
				});

		infoItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showInfo("P�ipojen� bylo znovu �sp�n� nav�z�no");
					}
				});


		remindItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showRemind("P�ipom�nka l�ka�e");
					}
				});

		noneItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showNormal();
					}
				});


		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
		}

	}

}

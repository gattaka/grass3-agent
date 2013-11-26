package org.myftp.gattserver.grass3.agent

import javax.naming.OperationNotSupportedException;

class GrassAgent {

	private Tray tray;

	def startAgent() {

		try {
			tray = new Tray()
		} catch (OperationNotSupportedException e){
			println "SystemTray is not supported";
		}
	}
}



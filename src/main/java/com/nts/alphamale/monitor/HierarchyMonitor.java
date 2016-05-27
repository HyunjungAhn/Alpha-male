/**
 * AlphaMale for web
Copyright (C) 2016 NHN Technology Services

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 */

package com.nts.alphamale.monitor;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import com.nts.alphamale.data.DataQueue;
import com.nts.alphamale.handler.DeviceHandler;
import com.nts.alphamale.handler.DocumentHandler;

public class HierarchyMonitor implements Runnable {

	Logger log = LogManager.getLogger(this.getClass());
	private String serial;
	private static HierarchyMonitor instance;

	private volatile boolean suspended = false;  

	public String getSerial() {
		return serial;
	}

	private HierarchyMonitor(String serial) {
		this.serial = serial;
		HierarchyMonitor.instance = this;
	}

	public static HierarchyMonitor getInstance(String serial) {
		if (instance == null) {
			instance = new HierarchyMonitor(serial);
		} else {
			if (!serial.equals(instance.getSerial())) {
				instance = new HierarchyMonitor(serial);
			}
		}
		return instance;
	}

	public void removeInstance() {
		instance = null;
	}

	public void run() {
		try {
			while (DataQueue.IS_CONTROLED) {
				log.info("IS_CONTROLED is true");
				if (DataQueue.IS_AUTO_MODE) {
					synchronized (this) {
						while (suspended) {
							log.info("dump monitoring suspend");
							wait();
						}
					}
					getHierarchyDump();
					Thread.sleep(200);
				}else{
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void suspend() {
		suspended = true;
	}

	public synchronized void resume() {
		suspended = false;
		notify();
	}

	private void getHierarchyDump() {
		log.info("getHierarchyDump");
		long dumpTime = System.currentTimeMillis();
		String hierchary = DeviceHandler.createDump(serial);
		if (StringUtils.isNotEmpty(hierchary) && hierchary.contains("hierarchy rotation")) {
			hierchary = hierchary.replaceFirst("hierarchy rotation", "hierarchy time=\"" + dumpTime + "\" rotation");
			try {
				Document doc = DocumentHandler.convertStringToDocument(hierchary);
				if (DataQueue.DOCUMENT_QUEUE.size() == 3)
					DataQueue.DOCUMENT_QUEUE.poll();
				DataQueue.DOCUMENT_QUEUE.offer(doc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

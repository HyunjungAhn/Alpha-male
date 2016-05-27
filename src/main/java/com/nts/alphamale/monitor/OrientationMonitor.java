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

import com.nts.alphamale.controller.LeaderController;
import com.nts.alphamale.data.DataQueue;
import com.nts.alphamale.handler.DeviceHandler;


public class OrientationMonitor implements Runnable {

	String serial;
	OrientationMonitoringListener listener;
	public interface OrientationMonitoringListener {
		public void onOrientaionChangeEvent(int orientation);
	}

	public OrientationMonitor(String serial, LeaderController leader) {
		this.serial = serial;
		listener = leader;
	}
	
	public void run() {
		if(DataQueue.IS_CONTROLED){
			int currentOrientation = DeviceHandler.getOrientation(serial);
			if(DataQueue.CURRENT_ORIENTATION != currentOrientation){
				listener.onOrientaionChangeEvent(currentOrientation);
			}
		}
	}
}

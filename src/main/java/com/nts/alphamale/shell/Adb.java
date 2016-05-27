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

package com.nts.alphamale.shell;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nts.alphamale.data.DeviceInfo;

@Service
public class Adb {
	/*
	public static AndroidDebugBridge adb = null;
	private static ResourceBundle ADB_COMMAND_BUNDLE = ResourceBundle.getBundle("adbcommand");

	public static void init() {
		if (adb == null) {
			AndroidDebugBridge.init(false);
			adb = AndroidDebugBridge.createBridge(Utils.adbPath(), true);
		} else {
			if (adb.isConnected() == false) {
				adb = AndroidDebugBridge.createBridge(Utils.adbPath(), true);
			}
		}
	}

	public static void finish() {
		if (adb != null) {
			AndroidDebugBridge.disconnectBridge();
			AndroidDebugBridge.terminate();
		}
	}
	*/

/*	*//***
	 * 화면단에 접속시 초기 접속되어 있는 디바이스 목록을 조회
	 * @return
	 * @throws InterruptedException
	 *//*
	public static List<String> getDeviceList() throws InterruptedException {
		init();
		int trials = 10;
		while (trials > 0) {
			Thread.sleep(50);
			if (adb.hasInitialDeviceList()) {
				break;
			}
			trials--;
		}
		if (!adb.hasInitialDeviceList()) {
			return null;
		}
		while (!adb.hasInitialDeviceList()) {}
		
		IDevice[] devices =  adb.getDevices();
		
		List<String> rtnDevices = new ArrayList<>();
		for(IDevice device : devices){
			DataQueue.DEVICE_MAP.put(device.getSerialNumber(), device);
			rtnDevices.add(device.getSerialNumber());
		}		
		return rtnDevices;
		
	}*/

	
/*	public static void pushJarFile(IDevice device){
		String bundlejarPath = Resources.getResource("bundle.jar").getPath();
		String stubJarPath = Resources.getResource("uiautomator-stub.jar").getPath();
		try {
			device.pushFile(bundlejarPath.substring(1, bundlejarPath.length()), "/data/local/tmp/");
			device.pushFile( stubJarPath.substring(1, stubJarPath.length()), "/data/local/tmp/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	
	/***
	 * record start 시점 이후에 device 정보를 가져오는 부분
	 * @return
	 */
	public static List<DeviceInfo> getDeviceInfo(String serial){
		if(serial != null){
			
//			device.executeShellCommand("dumpsys input", arg1);
		}
		return null;
	}

	/***
	 * Device 변경되면 호출되는 listener 
	 * @throws Exception
	 */
	public void usingDeviceChangeListener() throws Exception {
		/*
		AndroidDebugBridge.addDeviceChangeListener(new AndroidDebugBridge.IDeviceChangeListener() {
			public void deviceConnected(IDevice device) {
				System.out.println("* " + device.getSerialNumber() + " Connected");
			}

			public void deviceDisconnected(IDevice device) {
				System.out.println("* " + device.getSerialNumber() + " disConnected");
			}

			public void deviceChanged(IDevice device, int changeMask) {
				System.out.println("* " + device.getSerialNumber() + " changed");
			}
		});
		*/
	}
}

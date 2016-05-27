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

package com.nts.alphamale.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Service;

import com.nts.alphamale.data.DataQueue;
import com.nts.alphamale.handler.DeviceHandler;
import com.nts.alphamale.util.Utils;

@Service
public class AlphaMaleController {

	private ResourceBundle keywordListBundle = ResourceBundle.getBundle("event_keyword");
	private String kwdRegex = "(\\d)\\^(\\d)\\^(\\d)\\^(.*)";	
	private String unlockApk = "./lib/Unlock.apk";
	private String unlockActivity = "io.appium.unlock/.Unlock";
	
	public void restartAdb(){
		DeviceHandler.restartAdb();
	}
	
	/***
	 * 연결된 디바이스 리스트 조회
	 * @return
	 */
	public List<String> scanDevices(){
		List<String> devices = DeviceHandler.getDeviceList();
		for(String serial:devices){
			DataQueue.DEVICE_MAP.put(serial, DeviceHandler.getDeviceInfomation(serial));
		}
		return devices;
	}
	
	/***
	 * 선택된 디바이스 재시작
	 * @param devices
	 */
	public void rebootDevices(List<String> devices){
		DeviceHandler.rebootDeivce(devices, 60);
		DataQueue.DEVICE_MAP.clear();
	}
	
	public List<String> uninstallApp(List<String> serial, boolean keepData, String pkgName){
		List<String> results =  DeviceHandler.unInstallApp(serial, keepData, pkgName, 60);
		return results;
	}
	
	/***
	 * 선택된 디바이스 lock 해제
	 * @param devices
	 */
	public void unlockDevices(List<String> devices) {
		DeviceHandler.installApp(devices, true, unlockApk, 5);
		DeviceHandler.startActivity(devices, unlockActivity, 5);
		DeviceHandler.pressKey(devices, "back", 5);
	}
	
	/***
	 * 선택된 디바이스들에 apk 설치 
	 * @param devices 
	 * @param reinstall : 재설치 여부 
	 * @param apk : apk 파일 경로
	 * @return
	 */
	public String installApp(List<String> devices, boolean reinstall, String apk){
		return DeviceHandler.installApp(devices, reinstall, apk, 60);
	}
	
	/***
	 * 선택된 다비이스에 설치된 앱 목록 조회
	 * @param device
	 * @return
	 */
	public List<String> getPkgList(String device){
		return DeviceHandler.get3rdPartyPackageList(device);
	}
	
	
	/***
	 * 이벤트 키워드 리스트 조회 (/resourece/evet_keyword.properties)
	 * @return
	 */
	public List<String> getKeywordList(){
		List<String> kwdList = new ArrayList<String>(keywordListBundle.keySet());
		Collections.sort(kwdList);
		return kwdList;
	}
	
	public String startActivity(String leader, List<String> serial, String pkgName) {
		String activity = DeviceHandler.getMainActivity(leader, pkgName);
		return DeviceHandler.startActivity(serial, activity, 60);
	}
	
	public String clearAppData(List<String> serial, String pkgName) {
		return DeviceHandler.clearAppData(serial, pkgName, 60);
	}
	
	/***
	 * 해당 이벤트 키워드에 조건조회
	 * @param kwd
	 * @return
	 */
	@Deprecated
	public List<Boolean> getKwdConfig(String kwd){
		String kwdValue = keywordListBundle.getString(kwd);
		String element = Utils.convertRegex(kwdRegex, kwdValue, 1);
		String direction = Utils.convertRegex(kwdRegex, kwdValue, 2);
		String value = Utils.convertRegex(kwdRegex, kwdValue, 3);
		List<Boolean> config = new ArrayList<Boolean>();
		config.add(element.equals("1")?true:false);
		config.add(direction.equals("1")?true:false);
		config.add(value.equals("1")?true:false);
		return config;
	}
	
	/***
	 * 해당 이벤트 키워드 툴팁 조회
	 * @param kwd
	 * @return
	 */
	@Deprecated
	public String getKwdToolTip(String kwd){
		String kwdValue = "";;
		try {
			kwdValue = new String(keywordListBundle.getString(kwd).getBytes("8859_1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Utils.convertRegex(kwdRegex, kwdValue, 3);
	}
}

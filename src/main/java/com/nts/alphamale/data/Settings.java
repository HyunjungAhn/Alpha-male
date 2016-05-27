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

package com.nts.alphamale.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.output.FileWriterWithEncoding;


public class Settings {

	/** EVENT LOGGING EXECUTOR TIMEOUT **/
	public static long EXECUTOR_TIMEOUT = 5 * 1000;
	
	/** DOUBLE TAP EVENT THRESHOLD **/
	public static double DOUBLE_TAP_THRESHOLD = 0.7;
	
	/** LONG TAP으로 판단할 수 있는 이벤트 지속 시간의 임계치 **/
	public static double LONG_TAP_THRESHOLD = 0.7;
	
	/** TOUCH와 SWIPE 이벤트 식별을 위한 면적 임계치 **/
	public static double SWIPE_AREA_THRESHOLD = 500;
	
	/** SWIPE 방향을 판단할 수 있는 시작과 끝 좌표 기반의 기울기 **/
	public static double SWIPE_ANGLE_THRESHOLD = 45;
	
	/** UIAutomator 요청 후 IDLE까지 대기하는 시간 **/
	public static int WAIT_FOR_IDLE_TIMEOUT = 2000;

	/** UIAutomator 요청 시 요소를 찾는데 소요되는 시간 **/
	public static long FIND_ELEMENT_TIMEOUT= 3000;
	
	/** Orientation을 식별하는 주기 **/
	public static long ORIENTATION_SCHEDULE = 500;
	
	/** Keypad를 식별하는 주기 **/
	public static long KEYPAD_SCHEDULE = 500;
	
	/** FOLLOWER EVENT INJECTION INTERVAL **/
	public static int EVENT_INTERVAL = 500;
	
	public static String TOUCH_DEVICE = "touch_dev,clearpad,sensor00,atmel_mxt_540s,synaptics_rmi,mtk-tpd";
	
	private static String SETTINGS = System.getProperty("user.dir")+File.separator+"settings.properties";
	
	public static void loadSettings(){
		if(new File(SETTINGS).exists()){
			Properties prop = new Properties();
			try {
				prop.load(new FileReader(SETTINGS));
				if(prop.getProperty("command_timeout")!=null)	EXECUTOR_TIMEOUT = Long.parseLong(prop.getProperty("command_timeout"));
				if(prop.getProperty("double_tap_threshold")!=null)	DOUBLE_TAP_THRESHOLD = Double.parseDouble(prop.getProperty("double_tap_threshold"));
				if(prop.getProperty("long_tap_threshold")!=null)	LONG_TAP_THRESHOLD = Double.parseDouble(prop.getProperty("long_tap_threshold"));
				if(prop.getProperty("swipe_area_threshold")!=null)	SWIPE_AREA_THRESHOLD = Integer.parseInt(prop.getProperty("swipe_area_threshold"));
				if(prop.getProperty("swipe_angle_threshold")!=null)	SWIPE_ANGLE_THRESHOLD = Integer.parseInt(prop.getProperty("swipe_angle_threshold"));
				if(prop.getProperty("find_timeout")!=null)	FIND_ELEMENT_TIMEOUT = Long.parseLong(prop.getProperty("find_timeout"));
				if(prop.getProperty("idle_timeout")!=null)	WAIT_FOR_IDLE_TIMEOUT = Integer.parseInt(prop.getProperty("idle_timeout"));
				if(prop.getProperty("orientation_schedule")!=null)	ORIENTATION_SCHEDULE = Long.parseLong(prop.getProperty("orientation_schedule"));
				if(prop.getProperty("keypad_schedule")!=null)	KEYPAD_SCHEDULE = Long.parseLong(prop.getProperty("keypad_schedule"));
				if(prop.getProperty("event_interval")!=null)	EVENT_INTERVAL = Integer.parseInt(prop.getProperty("event_interval"));
				if(prop.getProperty("touchscreen_device")!=null)	TOUCH_DEVICE = prop.getProperty("touchscreen_device");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			FileWriterWithEncoding fw = null;
			String lineSeparator = System.getProperty("line.separator");
			try {
				fw = new FileWriterWithEncoding(SETTINGS, "UTF-8");
				fw.write("# command timeout(ms)"+lineSeparator);
				fw.write("command_timeout = 5000"+lineSeparator);
				fw.write(lineSeparator);
				fw.write("# double tap threshold(sec)"+lineSeparator);
				fw.write("double_tap_threshold = 0.7"+lineSeparator);
				fw.write(lineSeparator);
				fw.write("# long tap threshold(sec)"+lineSeparator);
				fw.write("long_tap_threshold = 0.7"+lineSeparator);
				fw.write(lineSeparator);
				fw.write("# swipe area threshold(px)"+lineSeparator);
				fw.write("swipe_area_threshold = 100"+lineSeparator);
				fw.write(lineSeparator);
				fw.write("# swipe angle threshold"+lineSeparator);
				fw.write("swipe_angle_threshold = 45"+lineSeparator);
				fw.write(lineSeparator);
				fw.write("# find element threshold(ms)"+lineSeparator);
				fw.write("find_timeout = 2000"+lineSeparator);
				fw.write(lineSeparator);
				fw.write("# idle threshold(ms)"+lineSeparator);
				fw.write("idle_timeout = 2000"+lineSeparator);
				fw.write(lineSeparator);
				fw.write("# orientation recognition schedule"+lineSeparator);
				fw.write("orientation_schedule = 500"+lineSeparator);
				fw.write(lineSeparator);
				fw.write("# keypad recognition schedule"+lineSeparator);
				fw.write("keypad_schedule = 500"+lineSeparator);
				fw.write(lineSeparator);
				fw.write("# follower event injection interval(ms)"+lineSeparator);
				fw.write("event_interval = 500"+lineSeparator);
				fw.write(lineSeparator);
				fw.write("# touch screen device"+lineSeparator);
				fw.write("touchscreen_device = _touchscreen,touch_dev,clearpad,sensor00,atmel_mxt_540s,synaptics_rmi,mtk-tpd");
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
}

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

package com.nts.alphamale.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nts.alphamale.client.FollowerRpcClient;
import com.nts.alphamale.data.DataQueue;
import com.nts.alphamale.data.DeviceInfo;
import com.nts.alphamale.data.Settings;
import com.nts.alphamale.shell.AdbShellCommand;
import com.nts.alphamale.shell.AdbShellExecutor;
import com.nts.alphamale.util.Utils;

public class DeviceHandler {

	static Logger log = LogManager.getLogger(DeviceHandler.class);

	/**
	 * adb kill-server && adb start-server를 통해 adb를 재시작함
	 * 
	 */
	public static void restartAdb() {
		Map<String, Object> executorMap = new AdbShellExecutor().execute(AdbShellCommand.cmd("", "cmd_kill_server"),
				true, Settings.EXECUTOR_TIMEOUT);
		if (!executorMap.isEmpty()) {
			Utils.destoryExecutor(executorMap.get("executor"));
		}
		executorMap = new AdbShellExecutor().execute(AdbShellCommand.cmd("", "cmd_start_server"), true,
				Settings.EXECUTOR_TIMEOUT);
		if (!executorMap.isEmpty()) {
			Utils.destoryExecutor(executorMap.get("executor"));
		}
	}

	/**
	 * adb devices 커멘드를 이용하여 인자로 받은 단말의 연결 상태 확인
	 * 
	 * @return
	 */
	public static boolean isAdbReady(String serial) {
		boolean rtnValue = false;
		Map<String, Object> executorMap = new AdbShellExecutor().execute(AdbShellCommand.cmd("", "cmd_devices"), true,
				Settings.EXECUTOR_TIMEOUT);
		if (executorMap.isEmpty()) {
			return rtnValue;
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
		while (li != null && li.hasNext()) {
			String device = li.nextLine();
			if (!device.contains("List of devices attached") && device.contains(serial)) {
				rtnValue = true;
			}
		}
		Utils.destoryExecutor(executorMap.get("executor"));
		return rtnValue;
	}

	public static List<String> getDeviceList() {
		List<String> deviceList = new ArrayList<String>();
		Map<String, Object> executorMap = new AdbShellExecutor().execute(AdbShellCommand.cmd("", "cmd_devices"), true,
				Settings.EXECUTOR_TIMEOUT);
		if (executorMap.isEmpty()) {
			return deviceList;
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
		while (li != null && li.hasNext()) {
			String device = li.nextLine();
			if (device.contains("device")) {
				if (!device.contains("List of devices attached"))
					deviceList.add(device.replace("device", "").trim());
			}
		}
		Utils.destoryExecutor(executorMap.get("executor"));
		return deviceList;
	}

	public static CommandLine getEventCommandLine(String serial) {
		return AdbShellCommand.cmd(serial, "cmd_getevent");
	}

	public static void rebootDeivce(List<String> serial, int timeoutSecond) {
		List<CommandLine> cmdList = new ArrayList<CommandLine>();
		for (String s : serial) {
			cmdList.add(AdbShellCommand.cmd(s, "cmd_reboot_device"));
		}
		new ExecutorHandler().executeParallel(cmdList, timeoutSecond);
	}

	/**
	 * Press "Home" or "Back" button
	 * 
	 * @param serial
	 * @param key
	 * @param timeoutSecond
	 */
	public static void pressKey(List<String> serial, String key, int timeoutSecond) {
		List<CommandLine> cmdList = new ArrayList<CommandLine>();
		for (String s : serial) {
			if (key.equals("home"))
				cmdList.add(AdbShellCommand.cmd(s, "cmd_press_home"));
			if (key.equals("back"))
				cmdList.add(AdbShellCommand.cmd(s, "cmd_press_back"));
		}
		new ExecutorHandler().executeParallel(cmdList, timeoutSecond);
	}

	/**
	 * getInputManager의 SurfaceOrientation을 통해 획득
	 * {@link #getInputManager(String)}
	 * 
	 * @param serial
	 * @return
	 */
	public static int getOrientation(String serial) {
		int rtnValue = 0;
		Map<String, Object> executorMap = new AdbShellExecutor()
				.execute(AdbShellCommand.cmd(serial, "cmd_get_orientation"), false, Settings.EXECUTOR_TIMEOUT);
		if (executorMap.isEmpty()) {
			return rtnValue;
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
		while (li != null && li.hasNext()) {
			String out = li.nextLine().trim();
			if (out.contains("SurfaceOrientation:")) {
				rtnValue = Integer.parseInt(Utils.convertRegex("(\\d)", out, 1));
			}
		}
		Utils.destoryExecutor(executorMap.get("executor"));
		return rtnValue;
	}

	public static String installApp(String serial, String apkPath, int timeoutSecond) {
		String rtnValue = "";
		Map<String, Object> executorMap = new AdbShellExecutor().execute(
				AdbShellCommand.cmd(serial, "cmd_install_app", new String[] { apkPath }), false,
				Settings.EXECUTOR_TIMEOUT);
		if (executorMap.isEmpty()) {
			return rtnValue;
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
		while (li != null && li.hasNext()) {
			rtnValue += li.nextLine().trim();
		}
		Utils.destoryExecutor(executorMap.get("executor"));
		return rtnValue;
	}

	public static String unInstallApp(String serial, String packageName, int timeoutSecond) {
		String rtnValue = "";
		Map<String, Object> executorMap = new AdbShellExecutor().execute(
				AdbShellCommand.cmd(serial, "cmd_uninstall_app", new String[] { packageName }), false,
				Settings.EXECUTOR_TIMEOUT);
		if (executorMap.isEmpty()) {
			return rtnValue;
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
		while (li != null && li.hasNext()) {
			rtnValue += li.nextLine().trim();
		}
		Utils.destoryExecutor(executorMap.get("executor"));
		return rtnValue;
	}

	public static String installApp(List<String> serial, boolean isReinstall, String apkPath, int timeoutSecond) {
		String rtnStr = "";
		List<CommandLine> cmdList = new ArrayList<CommandLine>();
		for (String s : serial) {
			if (isReinstall) {
				cmdList.add(AdbShellCommand.cmd(s, "cmd_reinstall_app", new String[] { apkPath }));
			} else {
				cmdList.add(AdbShellCommand.cmd(s, "cmd_install_app", new String[] { apkPath }));
			}
		}
		List<Map<String, Object>> results = new ExecutorHandler().executeParallel(cmdList, timeoutSecond);
		for (Map<String, Object> executorMap : results) {
			if (executorMap.isEmpty()) {
				break;
			}
			LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
			while (li != null && li.hasNext()) {
				String output = li.nextLine();
				if (output.contains("Success")) {
					rtnStr = (new File(apkPath).getName() + " is successfully installed on " + serial + " device");
				}
				if (output.contains("Failure")) {
					rtnStr = ("Fail to install on " + serial + " device (" + output + ")");
				}
			}
		}
		return rtnStr;
	}

	public static List<String> unInstallApp(List<String> serial, boolean keepData, String pkgName, int timeoutSecond) {
		List<String> rtnStr = new ArrayList<String>();
		List<CommandLine> cmdList = new ArrayList<CommandLine>();
		for (String s : serial) {
			if (keepData) {
				cmdList.add(AdbShellCommand.cmd(s, "cmd_uninstall_app_keep_data", new String[] { pkgName }));
			} else {
				cmdList.add(AdbShellCommand.cmd(s, "cmd_uninstall_app", new String[] { pkgName }));
			}
		}

		List<Map<String, Object>> results = new ExecutorHandler().executeParallel(cmdList, timeoutSecond);
		for (Map<String, Object> executorMap : results) {
			if (executorMap.isEmpty()) {
				break;
			}
			LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
			while (li != null && li.hasNext()) {
				String device = li.nextLine();
				if (device.contains("Success")) {
					rtnStr.add(pkgName + " is successfully uninstalled on " + serial + " device");
				}
				if (device.contains("Failure")) {
					rtnStr.add("Fail to uninstalled on " + serial + " device");
				}
			}
		}
		return rtnStr;
	}

	public static String clearAppData(List<String> serial, String pkgName, int timeoutSecond) {
		String rtnStr = "";
		List<CommandLine> cmdList = new ArrayList<CommandLine>();
		for (String s : serial) {
			cmdList.add(AdbShellCommand.cmd(s, "cmd_clear_app_data", new String[] { pkgName }));
		}
		List<Map<String, Object>> results = new ExecutorHandler().executeParallel(cmdList, timeoutSecond);
		for (Map<String, Object> executorMap : results) {
			if (executorMap.isEmpty()) {
				break;
			}
			LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
			while (li != null && li.hasNext()) {
				String device = li.nextLine();
				if (device.contains("Success")) {
					rtnStr = pkgName + " is cleared on " + serial + " device";
				}
			}
		}
		return rtnStr;
	}

	public static void pushJar(String serial, String jarPath) {
		Map<String, Object> executorMap = new AdbShellExecutor().execute(
				AdbShellCommand.cmd(serial, "cmd_push", new String[] { jarPath, "/data/local/tmp/" }), true, 30 * 1000);
		if (!executorMap.isEmpty()) {
			Utils.destoryExecutor(executorMap.get("executor"));
		}
	}

	public static void pull(String serial, String srcPath, String destPath) {
		Map<String, Object> executorMap = new AdbShellExecutor()
				.execute(AdbShellCommand.cmd(serial, "cmd_pull", new String[] { srcPath, destPath }), true, 30 * 1000);
		if (!executorMap.isEmpty()) {
			Utils.destoryExecutor(executorMap.get("executor"));
		}
	}

	public static void deleteJar(String serial, String jarPath) {
		Map<String, Object> executorMap = new AdbShellExecutor().execute(
				AdbShellCommand.cmd(serial, "cmd_delete", new String[] { "/data/local/tmp/" + jarPath }), true,
				Settings.EXECUTOR_TIMEOUT);
		if (!executorMap.isEmpty()) {
			Utils.destoryExecutor(executorMap.get("executor"));
		}
	}


	public static Map<String, String> getProp(String serial) {
		Map<String, String> rtnValue = new HashMap<String, String>();
		Map<String, Object> executorMap = new AdbShellExecutor().execute(AdbShellCommand.cmd(serial, "cmd_getprop"),
				false, Settings.EXECUTOR_TIMEOUT);
		if (executorMap.isEmpty()) {
			return null;
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
		while (li != null && li.hasNext()) {
			String out = li.nextLine().trim();
			if (!out.isEmpty() && !out.startsWith("[]")) {
				out = out.replace("]: [", ":").replace("[", "").replace("]", "");
				if (out.split(":").length == 2)
					rtnValue.put(out.split(":")[0], out.split(":")[1]);
			}
		}
		Utils.destoryExecutor(executorMap.get("executor"));
		return rtnValue;
	}

	/**
	 * Dumpsys Input Diagnostics를 통해 다음과 같은 데이터를 획득 할 수 있다. 1) Touch Screen의
	 * width와 height 2) Touch Screen의 Scaling Factors - XScale, YScale 3)
	 * SurfaceOrientation을 이용한 현재 화면의 Orientation 정보 4) FocusedWindow을 이용한 현재
	 * 화면의 Activity 정보
	 * 
	 * @see <a href="https://source.android.com/devices/input/diagnostics.html">
	 *      Dumpsys Input Diagnostics</a>
	 * @param serial
	 * @return
	 */
	
	public static Map<String, Object> getInputManager(String serial) {
		Map<String, Object> rtnValue = new HashMap<String, Object>();
		final String activity_regex = "((\\S+)/(\\S+))[\\s|\\}]";
		final String dispaly_regex = "[X|Y]: min=\\d, max=(\\d+),";
		final String orientation_regex = "(\\d)";
		final String scaling_regex = "[X|Y]Scale:\\s(.*)";
		//final String touchscreen_regex = "Device\\s\\d+:\\s(\\S+)";
		final String touchscreen_regex = "DeviceType: touchScreen";
		final String pointerVelocityControlParameters_regex = "PointerVelocityControlParameters:\\s(.*)";
		final String tabInterval_regex = "TapInterval:\\s(.*)ms";
		final String tapDragInterval_regex = "TapDragInterval:\\s(.*)ms";
		final String multitouchSettleInterval_regex = "MultitouchSettleInterval:\\s(.*)ms";
		final String swipeMaxWidthRatio_regex = "SwipeMaxWidthRatio:\\s(.*)";
		final String movementSpeedRatio_regex = "MovementSpeedRatio:\\s(.*)";
		final String zoomSpeedRatio_regex = "ZoomSpeedRatio:\\s(.*)";
		final String velocity_scale_regex = "scale=(.*), lowThreshold";
		final String velocity_lowThreshold_regex= "lowThreshold=(.*), highThreshold";
		final String velocity_highThreshold_regex="highThreshold=(.*), acceleration";
		final String velocity_acceleration_regex="acceleration=(.*)";

		boolean isTouchScreen = false;

		Map<String, Object> executorMap = new AdbShellExecutor()
				.execute(AdbShellCommand.cmd(serial, "cmd_dumpsys_input"), false, Settings.EXECUTOR_TIMEOUT);
		if (executorMap.isEmpty()) {
			return null;
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
		while (li.hasNext()) {
			String output = li.nextLine().trim();
			if(output.contains(touchscreen_regex)){
				isTouchScreen = true;
			}
			if(isTouchScreen && output.contains("Last Cooked Touch")){
				isTouchScreen = false;
			}
			if(isTouchScreen && output.contains("X: min=0, max=")){
				if(!rtnValue.containsKey("width"))
					rtnValue.put("width", (Integer.valueOf(Utils.convertRegex(dispaly_regex, output, 1))+1)+"");
			}
			if(isTouchScreen && output.contains("Y: min=0, max=")){
				if(!rtnValue.containsKey("height"))
					rtnValue.put("height", (Integer.valueOf(Utils.convertRegex(dispaly_regex, output, 1))+1)+"");
			}
			if(isTouchScreen && output.contains("XScale:") && !output.contains("TiltXScale")){
				if(!rtnValue.containsKey("xScale"))
					rtnValue.put("xScale", Utils.convertRegex(scaling_regex, output, 1));
			}
			if(isTouchScreen && output.contains("YScale:") && !output.contains("TiltYScale")){
				if(!rtnValue.containsKey("yScale"))
					rtnValue.put("yScale", Utils.convertRegex(scaling_regex, output, 1));
			}
			if(output.contains("SwipeMaxWidthRatio")){
				if(!rtnValue.containsKey("SwipeMaxWidthRatio")){
					log.info("width  : " + String.valueOf(rtnValue.get("width")) + " height : " + String.valueOf(rtnValue.get("height")));
					String swipeMaxWidthRatio  = Utils.convertRegex(swipeMaxWidthRatio_regex, output, 1);
					Integer width = Integer.valueOf((String)rtnValue.get("width"));
					Integer height = Integer.valueOf((String)rtnValue.get("height"));
					//Double swipe_distance = (Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) * Double.valueOf(swipeMaxWidthRatio));
					Double swipe_distance = (Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)) * Double.valueOf(swipeMaxWidthRatio));
					Settings.SWIPE_AREA_THRESHOLD = swipe_distance.intValue(); 
				}
			}
			if(output.contains("SurfaceOrientation:"))	DataQueue.CURRENT_ORIENTATION = Integer.valueOf(Utils.convertRegex(orientation_regex, output, 1));
/*			
 * 			String output = li.nextLine().trim();
			if (output.contains("TOUCH_MAJOR"))
				isTouchScreen = true;
			if (isTouchScreen && output.contains("Input Dispatcher State:"))
				isTouchScreen = false;
			if (isTouchScreen && output.contains("X: min=0, max=")) {
				if (!rtnValue.containsKey("width"))
					rtnValue.put("width", (Integer.valueOf(Utils.convertRegex(dispaly_regex, output, 1)) + 1) + "");
			}
			if (isTouchScreen && output.contains("Y: min=0, max=")) {
				if (!rtnValue.containsKey("height"))
					rtnValue.put("height", (Integer.valueOf(Utils.convertRegex(dispaly_regex, output, 1)) + 1) + "");
			}
			if (isTouchScreen && output.contains("XScale:") && !output.contains("TiltXScale")) {
				if (!rtnValue.containsKey("xScale"))
					rtnValue.put("xScale", Utils.convertRegex(scaling_regex, output, 1));
			}
			if (isTouchScreen && output.contains("YScale:") && !output.contains("TiltYScale")) {
				if (!rtnValue.containsKey("yScale"))
					rtnValue.put("yScale", Utils.convertRegex(scaling_regex, output, 1));
			}

			if (isTouchScreen && output.contains("PointerVelocityControlParameters")) {
				if (!rtnValue.containsKey("pointerVelocityControlParameters")){

					String velocityInfo = Utils.convertRegex(pointerVelocityControlParameters_regex, output, 1);
					Map<String,String> detailInfo = new HashMap<String,String>();
					detailInfo.put("scale", Utils.convertRegex(velocity_scale_regex, velocityInfo, 1));
					detailInfo.put("lowThreshold", Utils.convertRegex(velocity_lowThreshold_regex, velocityInfo, 1));
					detailInfo.put("highThreshold", Utils.convertRegex(velocity_highThreshold_regex, velocityInfo, 1));
					detailInfo.put("acceleration", Utils.convertRegex(velocity_acceleration_regex, velocityInfo, 1));
					rtnValue.put("pointerVelocityControlParameters",detailInfo);
				}
			}
			if (isTouchScreen && output.contains("TapInterval")) {
				if (!rtnValue.containsKey("tapInterval"))
					rtnValue.put("tapInterval", Utils.convertRegex(tabInterval_regex, output, 1));

			}
			if (isTouchScreen && output.contains("TapDragInterval")) {
				if (!rtnValue.containsKey("tapDragInterval"))
					rtnValue.put("tapDragInterval", Utils.convertRegex(tapDragInterval_regex, output, 1));

			}
			if (isTouchScreen && output.contains("MultitouchSettleInterval")) {
				if (!rtnValue.containsKey("multitouchSettleInterval"))
					rtnValue.put("multitouchSettleInterval",
							Utils.convertRegex(multitouchSettleInterval_regex, output, 1));
			}
			if (isTouchScreen && output.contains("SwipeMaxWidthRatio")) {
				if (!rtnValue.containsKey("swipeMaxWidthRatio"))
					rtnValue.put("swipeMaxWidthRatio", Utils.convertRegex(swipeMaxWidthRatio_regex, output, 1));
			}
			if (isTouchScreen && output.contains("MovementSpeedRatio")) {
				if (!rtnValue.containsKey("movementSpeedRatio"))
					rtnValue.put("movementSpeedRatio", Utils.convertRegex(movementSpeedRatio_regex, output, 1));
			}
			if (isTouchScreen && output.contains("ZoomSpeedRatio")) {
				if (!rtnValue.containsKey("zoomSpeedRatio"))
					rtnValue.put("zoomSpeedRatio", Utils.convertRegex(zoomSpeedRatio_regex, output, 1));
			}*/

			if (output.contains("SurfaceOrientation:"))
				DataQueue.CURRENT_ORIENTATION = Integer.valueOf(Utils.convertRegex(orientation_regex, output, 1));
		}
		Utils.destoryExecutor(executorMap.get("executor"));
		return rtnValue;
	}

	public static void setSamsungKeyboard(String serial) {
		Map<String, Object> executorMap = new AdbShellExecutor().execute(
				AdbShellCommand.cmd(serial, "cmd_set_keyboard",
						new String[] { "com.sec.android.inputmethod/.SamsungKeyPad" }),
				true, Settings.EXECUTOR_TIMEOUT);
		if (!executorMap.isEmpty()) {
			Utils.destoryExecutor(executorMap.get("executor"));
		}
	}

	public static Map<String, Object> runUiAutomatorTest(String serial) {
		return new AdbShellExecutor().execute(AdbShellCommand.cmd(serial, "cmd_uiautomator_runtest"), false,
				ExecuteWatchdog.INFINITE_TIMEOUT);
	}

	public static List<String> get3rdPartyPackageList(String serial) {
		List<String> pkgList = new ArrayList<String>();
		Map<String, Object> executorMap = new AdbShellExecutor()
				.execute(AdbShellCommand.cmd(serial, "cmd_3rd_party_package_list"), false, Settings.EXECUTOR_TIMEOUT);
		if (executorMap.isEmpty()) {
			return pkgList;
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
		while (li != null && li.hasNext()) {
			String str = li.nextLine().trim();
			if (str.contains("package:")) {
				pkgList.add(str.replace("package:", ""));
			}
		}
		Utils.destoryExecutor(executorMap.get("executor"));
		return pkgList;
	}

	/**
	 * Leader 단말의 3rd party앱의 package명을 이용하여 해당앱의 MainActivity를 획득
	 * 
	 * @param serial
	 * @param pkgName
	 * @return
	 */
	public static String getMainActivity(String serial, String pkgName) {
		String laucherActivity = "";
		Map<String, Object> executorMap = new AdbShellExecutor().execute(
				AdbShellCommand.cmd(serial, "cmd_dumpsys_package", new String[] { pkgName }), false,
				Settings.EXECUTOR_TIMEOUT);
		if (executorMap.isEmpty()) {
			return "";
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
		boolean flag = false;
		while (li != null && li.hasNext()) {
			String str = li.nextLine().trim();
			if (str.contains("android.intent.action.MAIN:"))
				flag = true;
			// if(flag && str.contains("filter")){
			if (flag && (!str.isEmpty() && str.split(" ").length > 1)) {
				laucherActivity = str.split(" ")[1];
				flag = false;
			}
		}
		Utils.destoryExecutor(executorMap.get("executor"));
		return laucherActivity;
	}

	public static void startActivity(String serial, String activity) {
		Map<String, Object> executorMap = new AdbShellExecutor().execute(
				AdbShellCommand.cmd(serial, "cmd_start_activity", new String[] { "-S", activity }), true,
				Settings.EXECUTOR_TIMEOUT);
		if (!executorMap.isEmpty()) {
			Utils.destoryExecutor(executorMap.get("executor"));
		}
	}

	public static String startActivity(List<String> serial, String activity, int timeoutSecond) {
		String rtnStr = "";
		List<CommandLine> cmdList = new ArrayList<CommandLine>();
		for (String s : serial) {
			cmdList.add(AdbShellCommand.cmd(s, "cmd_start_activity", new String[] { activity }));
		}
		List<Map<String, Object>> results = new ExecutorHandler().executeParallel(cmdList, timeoutSecond);
		for (Map<String, Object> executorMap : results) {
			if (executorMap.isEmpty()) {
				break;
			}
			LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));
			while (li != null && li.hasNext()) {
				rtnStr = li.nextLine();
			}
		}
		return rtnStr;
	}

	public static void portForward(String serial, int port) {
		new AdbShellExecutor().execute(
				AdbShellCommand.cmd(serial, "cmd_port_forward", new String[] { "tcp:" + port, "tcp:7016" }), 0);
	}

	public static void portForwardRemove(String serial, int port) {
		new AdbShellExecutor().execute(
				AdbShellCommand.cmd(serial, "cmd_port_forward", new String[] { "--remove", "tcp:" + port }), 0);
	}

	public static Map<String, String> getCurrentActivity(String serial) {
		Map<String, String> currentActivity = new HashMap<String, String>();
		String activityName = "";
		Map<String, Object> executorMap = new AdbShellExecutor()
				.execute(AdbShellCommand.cmd(serial, "cmd_dumpsys_activity_top"), false, Settings.EXECUTOR_TIMEOUT);

		if (executorMap.isEmpty()) {
			return currentActivity;
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));

		boolean isHierarchyInfo = false;
		StringBuilder sb = new StringBuilder();

		while (li != null && li.hasNext()) {
			String output = li.nextLine();
			if (output.contains("ACTIVITY")) {
				activityName = output.trim().split(" ")[1];
			}
			if (output.contains("Looper"))
				isHierarchyInfo = false;
			if (isHierarchyInfo) {
				output = output.split("\\{")[0].trim();
				if (!output.isEmpty())
					sb.append(output + "\n");
			}
			if (output.contains("View Hierarchy"))
				isHierarchyInfo = true;
		}
		currentActivity.put(activityName, sb.toString());
		Utils.destoryExecutor(executorMap.get("executor"));
		return currentActivity;
	}

	public static String getTopActivity(String serial) {
		String rtnValue = "";
		Map<String, Object> executorMap = new AdbShellExecutor()
				.execute(AdbShellCommand.cmd(serial, "cmd_dumpsys_activity_top"), false, Settings.EXECUTOR_TIMEOUT);

		if (executorMap.isEmpty()) {
			return rtnValue;
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));

		while (li != null && li.hasNext()) {
			String output = li.nextLine();
			if (output.contains("ACTIVITY")) {
				rtnValue = output.trim().split(" ")[1];
			}
		}
		Utils.destoryExecutor(executorMap.get("executor"));
		return rtnValue;
	}

	public static String createDump(String serial) {
		FollowerHandler leader =  DataQueue.followerListenerList.get(0); //leader 
		FollowerRpcClient client = leader.getFollowerRpcClient();
		if(client == null){
			return null;
		}
		try {
			String response = (String)client.invoke("dumpWindowHierarchy",new Object[]{false,""});
			if(StringUtils.isNotEmpty(response)){
				return response;
			}
		} catch (Throwable e) {
			log.error(e);
		}
		return null;
	}

	public static StringBuilder readDump(String serial, String dumpPath, StringBuilder hierarchy) {
		Map<String, Object> executorMap = new AdbShellExecutor()
				.execute(AdbShellCommand.cmd(serial, "cmd_cat", new String[] { dumpPath }), false, 3 * 1000);

		if (executorMap.isEmpty()) {
			return new StringBuilder();
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));

		while (li != null && li.hasNext()) {
			hierarchy.append(li.nextLine().trim());
		}

		Utils.destoryExecutor(executorMap.get("executor"));
		return hierarchy;
	}

	public static boolean isKeyPadAppear(String serial) {
		Map<String, Object> executorMap = new AdbShellExecutor()
				.execute(AdbShellCommand.cmd(serial, "cmd_keypad_monitor"), false, Settings.EXECUTOR_TIMEOUT);

		if (executorMap.isEmpty()) {
			return false;
		}
		LineIterator li = Utils.getLineIterator(executorMap.get("stdOut"));

		if (li != null && li.hasNext()) {
			String str = li.nextLine().trim();
			if (str.contains("HasSurface=true")) {
				DataQueue.IS_KEYPAD_ON = true;
			} else {
				DataQueue.IS_KEYPAD_ON = false;
			}
		}

		Utils.destoryExecutor(executorMap.get("executor"));
		return DataQueue.IS_KEYPAD_ON;
	}

	public static DeviceInfo getDeviceInfomation(String serial) {

		DeviceInfo device;
		if (DataQueue.DEVICE_MAP.containsKey(serial)) {
			return DataQueue.DEVICE_MAP.get(serial);
		}
		device = new DeviceInfo(serial);

		Map<String, String> prop = DeviceHandler.getProp(serial);
		if (!prop.isEmpty()) {
			device.setManufacturer(prop.get("ro.product.manufacturer"));
			device.setDeviceModel(prop.get("ro.product.model"));
			device.setBuild(prop.get("ro.build.version.release"));
		}
		Map<String, Object> input = DeviceHandler.getInputManager(serial);
		if (!input.isEmpty()) {
			// f(input.get("velocity")!=null)
			// device.setVelocity(Double.valueOf(input.get("velocity")));
			if (input.get("xScale") != null)
				device.setxScale(Double.valueOf((String)input.get("xScale")));
			if (input.get("yScale") != null)
				device.setyScale(Double.valueOf((String)input.get("yScale")));
			if (input.get("width") != null)
				device.setWidth(Integer.valueOf((String)input.get("width")));
			if (input.get("height") != null)
				device.setHeight(Integer.valueOf((String)input.get("height")));
			if (input.get("pointerVelocityControlParameters") != null) {
			//	device.setVelocityParams((Map<String,Object>)input.get("pointerVelocityControlParameters"));
			}
			if (input.get("tapInterval") != null) {
				//device.setTapInterval((Double)input.get("tapInterval"));
			}
			if (input.get("tapDragInterval") != null) {
				//device.setTapDragInterval((Double)input.get("tapDragInterval"));
			}
			if (input.get("multitouchSettleInterval") != null) {
				//device.setMultitouchSettleInterval((Double)input.get("multitouchSettleInterval"));
			}
			if (input.get("multitouchMinDistance") != null) {
				//device.setMultitouchMinDistance((Double)input.get("multitouchMinDistance"));
			}
			if (input.get("swipeTransitionAngleCosine") != null) {
				//device.setSwipeTransitionAngleCosine((Double)input.get("swipeTransitionAngleCosine"));
			}
			if (input.get("swipeMaxWidthRatio") != null) {
				//device.setSwipeMaxWidthRatio((Double)input.get("swipeMaxWidthRatio"));
			}
			if (input.get("movementSpeedRatio") != null) {
				//device.setMovementSpeedRatio((Double)input.get("movementSpeedRatio"));
			}
			if (input.get("zoomSpeedRatio") != null) {
				//device.setZoomSpeedRatio((Double)input.get("zoomSpeedRatio"));
			}
		}
		return device;
	}
}
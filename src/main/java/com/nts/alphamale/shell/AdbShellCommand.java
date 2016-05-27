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

import java.util.ResourceBundle;

import org.apache.commons.exec.CommandLine;

import com.nts.alphamale.util.Utils;


public class AdbShellCommand {
	
	private static ResourceBundle ADB_COMMAND_BUNDLE = ResourceBundle.getBundle("adbcommand");
	
	/**
	 * 1. adbcommand.properties에 저장된 adb command list 중 입력된 커멘드를 ResourceBudle을 이용하여 커멘드 라인을 반환.
	 * 2. 입력한 adb 커멘드를 이용하여 커멘드 라인을 반환. 
	 * @param serial 단말의 시리얼
	 * @param adbCmd adb 커멘드
	 * @return adb command line
	 */
	public static CommandLine cmd(String serial, String adbCmd){
		CommandLine cmd = new CommandLine(Utils.adb());
		if(!serial.isEmpty()){
			cmd.addArgument("-s");
			cmd.addArgument(serial);
		}
		if(ADB_COMMAND_BUNDLE.getString(adbCmd).isEmpty()){
			cmd.addArguments(adbCmd.split(" "));
		}else{
			cmd.addArguments(ADB_COMMAND_BUNDLE.getString(adbCmd).split(" "));
		}
		return cmd;
	}
	
	/**
	 * 1. adbcommand.properties에 저장된 adb command list 중 입력된 커멘드를 ResourceBudle을 이용하여 커멘드 라인을 반환.
	 * 2. 입력한 adb 커멘드를 이용하여 커멘드 라인을 반환. 
	 * @param serial 단말의 시리얼
	 * @param adbCmd adb 커멘드
	 * @param args adb 커멘드외 추가적인 입력값(들)
	 * @return adb command line
	 */
	public static CommandLine cmd(String serial, String adbCmd, String[] args){
		CommandLine cmd = cmd(serial, adbCmd);
		cmd.addArguments(args);
		return cmd;
	}
}

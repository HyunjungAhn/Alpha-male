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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AdbShellExecutor {
	
	Logger log = LogManager.getLogger(this.getClass());

	DefaultExecutor executor = new DefaultExecutor();
	
	/**
	 * @param cmd adb 커맨드
	 * @param synch 커맨드 수행 방식 선택 true: synchronous, false: asynchronous
	 * @param 커맨드 타임아웃(ms)
	 * @return Executor와 Standard Output의 Map
	 */
	public Map<String, Object> execute(CommandLine cmd, boolean synch, long timeoutMilliseconds){
		Map<String, Object> executorMap = new HashMap<String, Object>();
		DefaultExecutor executor = new DefaultExecutor();
		PipedOutputStream pos = new PipedOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler(pos, System.err);
		streamHandler.setStopTimeout(timeoutMilliseconds);
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new PipedInputStream(pos));
		} catch (IOException e) {
			//log.error(e.getCause() + " : " + e.getMessage());
		}
		executor.setStreamHandler(streamHandler);
		ExecuteWatchdog watchDog = new ExecuteWatchdog(timeoutMilliseconds);
		executor.setWatchdog(watchDog);
		try {
			if(synch)
				executor.execute(cmd);
			else
				executor.execute(cmd, new DefaultExecuteResultHandler());
			
			log.debug(cmd.toString());
			LineIterator li = IOUtils.lineIterator(dis, "UTF-8");
			if(li!=null){
				executorMap.put("executor", executor);
				executorMap.put("stdOut", li);
			}
		} catch (Exception e) {
			log.error(e.getCause()+":"+e.getMessage()+"["+cmd+"]");
		}
		return executorMap;
	}
	
	/**
	 * @param cmd adb 커맨드
	 * @param synch 커맨드 수행 방식 선택 true: synchronous, false: asynchronous
	 * @param 커맨드 타임아웃(ms)
	 * @return Executor와 Standard Output의 Map
	 */
	public int execute(CommandLine cmd, int exitValue){
		int rtnValue = -1;
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(exitValue);
		try {
				rtnValue = executor.execute(cmd);
		} catch (Exception e) {
			log.error(e.getCause()+":"+e.getMessage()+"["+cmd+"]");
		}
		return rtnValue;
	}
}
 
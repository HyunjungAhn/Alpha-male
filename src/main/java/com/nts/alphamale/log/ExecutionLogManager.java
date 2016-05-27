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

package com.nts.alphamale.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nts.alphamale.data.DataQueue;
import com.nts.alphamaleWeb.handler.ExecutionLogWSHandler;
import com.nts.alphamaleWeb.msg.ExecutionLogVO;

/***
 * 수행로그 관리
 * @author NAVER
 *
 */
@Component
public class ExecutionLogManager {
	
	//websocket handler
	private static ExecutionLogWSHandler executionLogWSHandler;
	
	@Autowired
	public ExecutionLogManager( ExecutionLogWSHandler handler){
		ExecutionLogManager.executionLogWSHandler = handler;
	}
	
	private static Map<String, List<ExecutionLog>> logListMap = new HashMap<String,List<ExecutionLog>>();
	
	public static void clear(){
		logListMap.clear();
	}
	
	/**
	 * 다수의 follower들의 결과 추가 
	 * @param serial
	 * @param model
	 * @param isSuccess
	 * @param cause
	 */
	public static void add(int seq, String title,String serial, String model, boolean isSuccess, String cause){
		ExecutionLog log = new ExecutionLog(seq, title,serial, model, isSuccess, cause);
		List<ExecutionLog> logList = logListMap.get(String.valueOf(seq));
		if(logList == null){
			logList = new ArrayList<ExecutionLog>();
		}
		logList.add(log);
		logListMap.put(String.valueOf(seq), logList);
	}

	/***
	 * 모든 follower에 결과가 왔을 경우, websocket으로 UI 로 데이터 전송
	 */
	public static void send(int seq, boolean isReplay){
		List<ExecutionLog> logList =  logListMap.get(String.valueOf(seq));
		
		boolean isComplete = false;
		if(logList != null && logList.size() != 0){
			ExecutionLog logt = logList.get(0);
			if(isReplay == false && DataQueue.followerListenerList.size()-1 == logList.size()){
					isComplete = true;
					
			}
			if(isReplay == true && DataQueue.followerListenerList.size() == logList.size()){
				isComplete = true;
			}
			
			if(isComplete == true){
				
				boolean isResult = true;
				for(ExecutionLog log : logList){
					if(log.isSuccess() ==false){
						isResult = false;
						break;
					}
				}
				Gson gson = new GsonBuilder().create();
				ExecutionLogVO vo = new ExecutionLogVO(seq, logt.getJobName(), isResult, logList);
				String json = gson.toJson(vo);
				executionLogWSHandler.sendExecutionLogMessage(json);
			}
		}
	}
}

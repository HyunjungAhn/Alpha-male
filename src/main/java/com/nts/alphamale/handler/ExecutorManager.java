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

import java.util.List;

import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nts.alphamale.controller.LeaderController;
import com.nts.alphamale.data.DataQueue;
import com.nts.alphamale.data.Job;
import com.nts.alphamale.data.Settings;
import com.nts.alphamale.monitor.EventMonitor;
import com.nts.alphamale.monitor.HierarchyMonitor;
import com.nts.alphamale.monitor.KeyPadMonitor;
import com.nts.alphamale.monitor.OrientationMonitor;

/***
 * ExecutorManager
 *  - event, keypad, orientation, leader 모니터링을 위한 exeutor 관리 클래스 
 *  - start / end executor
 * @author NAVER
 *
 */
@Service
public class ExecutorManager {

	Logger log = LogManager.getLogger(this.getClass());
	
	//event monitoring
	private ExecutorHandler eventExecutor = new ExecutorHandler();
	//keypad  on/off monitoring
	private ExecutorHandler keypadExecutor = new ExecutorHandler();
	//orientation change monitoring
	private ExecutorHandler orientationExecutor = new ExecutorHandler();
	//hierarchy dump monitoring 
	private ExecutorHandler dumpExecutor = new ExecutorHandler();
	
	
	@Autowired
	LeaderController leader;
	/***
	 * executor start 
	 * @param leaderSerial
	 * @param followerSerialList
	 */
	public void startControl(String leaderSerial, List<String> followerSerialList){
		
		DataQueue.followerListenerList.clear();
		log.info("start control");
		if(StringUtils.isNotEmpty(leaderSerial)){
			DataQueue.IS_CONTROLED = true;
			if(!DataQueue.EVENT_EXECUTOR.isEmpty()){
				((DefaultExecutor) DataQueue.EVENT_EXECUTOR.poll().get("executor")).getWatchdog().destroyProcess();
			}
			
			leader.init(leaderSerial, followerSerialList);
			
			DataQueue.EVENT_EXECUTOR.offer(eventExecutor.executeAsynchronous(DeviceHandler.getEventCommandLine(leaderSerial), 60*60*1000));
			eventExecutor.executeSingle(new EventMonitor(leaderSerial,leader));
			keypadExecutor.executePeriodically(new KeyPadMonitor(leaderSerial), Settings.KEYPAD_SCHEDULE);
			orientationExecutor.executePeriodically(new OrientationMonitor(leaderSerial,leader), Settings.ORIENTATION_SCHEDULE);
			
			//leader register 
			FollowerHandler leaderListener = new FollowerHandler(true,leaderSerial, 9000);
			DataQueue.followerListenerList.add(leaderListener);
			DataQueue.followerEventBus.register(leaderListener);
			
			startDumpExecutor(leaderSerial);
		}
		
		if(!CollectionUtils.isEmpty(followerSerialList)){
			//subscribe follower register
			for(int i=0,len = followerSerialList.size();i<len;i++){
				FollowerHandler followerListener = new FollowerHandler(false,followerSerialList.get(i), 9001 + i);
				DataQueue.followerListenerList.add(followerListener);
				DataQueue.followerEventBus.register(followerListener);
			}	
		}
	}
	
	/***
	 * executor finish
	 */
	public void stopControl(){
		DataQueue.IS_CONTROLED = false;
		if(!DataQueue.EVENT_EXECUTOR.isEmpty()){
			((DefaultExecutor) DataQueue.EVENT_EXECUTOR.poll().get("executor")).getWatchdog().destroyProcess();
		}
		eventExecutor.shutdownExecutor(eventExecutor.getSingleExecutor(),3);
		keypadExecutor.shutdownExecutor(keypadExecutor.getScheduledExecutor(),1);
		orientationExecutor.shutdownExecutor(orientationExecutor.getScheduledExecutor(),1);
		
		//unregister event bus follower 
		for(int i=0;i<DataQueue.followerListenerList.size();i++){
			DataQueue.followerListenerList.get(i).shutDown();
			DataQueue.followerEventBus.unregister(DataQueue.followerListenerList.get(i));
		}
		DataQueue.followerListenerList.clear();
		stopDumpExecutor();
		DataQueue.IS_CONTROLED = false;
		
	}
	
	public void startDumpExecutor(String leaderSerial){
		if(!leaderSerial.isEmpty())
			dumpExecutor.executeSingle(HierarchyMonitor.getInstance(leaderSerial));		
	}
	
	public void stopDumpExecutor(){
		dumpExecutor.shutdownExecutor(dumpExecutor.getSingleExecutor(), 3);
	}
	
	
	public void replay(List<Job> jobs) {
		DataQueue.followerEventBus.post(jobs);
	}
}
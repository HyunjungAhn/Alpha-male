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
import java.util.Map;

import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.nts.alphamale.client.FollowerRpcClient;
import com.nts.alphamale.data.DeviceInfo;
import com.nts.alphamale.data.Job;
import com.nts.alphamale.log.ExecutionLogManager;
import com.nts.alphamale.type.JobType;

public class FollowerHandler {

	static Logger log = LogManager.getLogger(FollowerHandler.class);

	// identify device serial
	private String serial;
	private DeviceInfo deviceInfo;
	private int port;
	private boolean isLeader = false;

	// rpc client
	private FollowerRpcClient client;

	// uiautomator rpc stub
	private Map<String, Object> executorMap;

	public FollowerHandler(boolean isLeader, String targetSerial, int port) {
		this.serial = targetSerial;
		this.port = port;
		this.client = new FollowerRpcClient(serial, port);
		this.deviceInfo = getDeviceInfo(targetSerial);
		
		// uiautomatore test stub settup
		this.setUp();
		
		this.isLeader = isLeader;
		client.setWaitForSelectorTimeout();
	}

	public FollowerRpcClient getFollowerRpcClient() {
		return client;
	}

	private DeviceInfo getDeviceInfo(String serial) {
		return DeviceHandler.getDeviceInfomation(serial);
	}

	private void setUp() {
		// 해당 단말에 UiautomatorTest가 실행 중일 경우를 대비해서 serverstop을 호출함
		client.serverStop(); 
		if (StringUtils.isNotEmpty(serial)) {
			String bundlejarPath = "./lib/bundle.jar";
			String stubJarPath = "./lib/uiautomator-stub.jar";
			DeviceHandler.pushJar(serial, bundlejarPath);
			DeviceHandler.pushJar(serial, stubJarPath);
			executorMap = DeviceHandler.runUiAutomatorTest(serial);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (StringUtils.isNotEmpty(serial)) {
			DeviceHandler.portForward(serial, port);
		}
		client.createClient();
	}

	public void shutDown() {
		client.serverStop();
		if (StringUtils.isNotEmpty(serial)) {
			DeviceHandler.deleteJar(serial, "bundle.jar");
			DeviceHandler.deleteJar(serial, "uiautomator-stub.jar");
			DeviceHandler.portForwardRemove(serial, port);
		}
		((DefaultExecutor) executorMap.get("executor")).getWatchdog().destroyProcess();
	}

	/***
	 * 일반적인 record 상황에서 호출
	 * @param job
	 */
	@Subscribe
	@AllowConcurrentEvents
	public void task(Job job){
		Job rjob = null;
		try {
			rjob = (Job) job.clone();
			if (isLeader == false) {
				if (rjob.getJobType() == JobType.POSITION_BASE){
					rjob.convertPoint(deviceInfo);
				}
			}else{
				return;
			}

			Object rsltObj = client.invoke(rjob);
			if(rsltObj instanceof Boolean){
				boolean isSuccess = (Boolean)rsltObj;
				ExecutionLogManager.add(rjob.getSeq(), rjob.getTitle(),serial, deviceInfo.getModel(), isSuccess, "unknown");
			}
		} catch (Throwable e) {
			log.info("Exception Follower(" + deviceInfo.getModel() + ") - " + e.getStackTrace());
			ExecutionLogManager.add(rjob.getSeq(), rjob.getTitle(), serial, deviceInfo.getModel(), false, e.getLocalizedMessage());
		}finally{
			ExecutionLogManager.send(rjob.getSeq(),false);
		}
	}

	/***
	 * replay 상황에서 호출
	 * @param jobs
	 */
	@Subscribe
	public void task(List<Job> jobs){
		ExecutionLogManager.clear();
		for(Job job : jobs){
			Job rjob = null;
			String eMsg = null;
			try {
				rjob = (Job) job.clone();
				if (rjob.getJobType() == JobType.POSITION_BASE){
					rjob.convertPoint(deviceInfo);
				}
				Object rsltObj = client.invoke(rjob);
				if(rsltObj instanceof Boolean){
					boolean isSuccess = (Boolean)rsltObj;
					ExecutionLogManager.add(rjob.getSeq(), rjob.getTitle(),serial, deviceInfo.getModel(), isSuccess, "unknown");
				}
			}catch(Throwable e){
				if(e.getCause() != null){
					eMsg = e.getCause().getMessage();
				}else{
					eMsg = e.getMessage();
				}
				log.info("Exception Follower(" + deviceInfo.getModel() + ") - " +  e.getStackTrace());
				ExecutionLogManager.add(rjob.getSeq(), rjob.getTitle(), serial, deviceInfo.getModel(), false, eMsg);
			}finally{
				ExecutionLogManager.send(rjob.getSeq(),true);
			}
		}
	}

}
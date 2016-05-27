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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nts.alphamale.data.DataQueue;
import com.nts.alphamale.data.DeviceInfo;
import com.nts.alphamale.data.Job;
import com.nts.alphamale.event.Event;
import com.nts.alphamale.event.EventFactory;
import com.nts.alphamale.handler.DeviceHandler;
import com.nts.alphamale.monitor.EventMonitor.EventMonitoringListener;
import com.nts.alphamale.monitor.OrientationMonitor.OrientationMonitoringListener;
import com.nts.alphamale.type.EventType;
import com.nts.alphamale.util.Utils;
import com.nts.alphamaleWeb.handler.RecordReplayWSHandler;

@Service
public class LeaderController implements EventMonitoringListener, OrientationMonitoringListener {

	Logger log = LogManager.getLogger(this.getClass());
	
	/*webwsoket handler*/
	@Autowired
	RecordReplayWSHandler eventHandler;

	/*리더에서 생성된 이벤트 처리 인터페이스 */
	LeaderEventListener listener;
	public interface LeaderEventListener {
		public void onCatchTouchEvent(Job job);
	}

	/* Leader 단말 정보 */
	String serial;
	DeviceInfo leader;

	/* Follower Serial List */
	List<String> followerSerialList;

	/***
	 * leader controller 초기화
	 * @param leaderSerial
	 * @param followerSerialListg
	 */
	public void init(String leaderSerial, List<String> followerSerialList) {
		this.listener = eventHandler;
		this.serial = leaderSerial;
		this.followerSerialList = followerSerialList;
		this.leader = DeviceHandler.getDeviceInfomation(serial);
	}

	/***
	 * 리더 디바이스 정보 조회
	 * @return
	 */
	public DeviceInfo getLeaderDeviceInfo() {
		return leader;
	}

	/***
	 * 이벤트를 실행 수행할 job개체로 변환
	 * @param event
	 */
	private void identifyAnEvent(Event event) {
		// event catch
		if (event != null) {
			if(event.getPosition() != null){
				event.convertPoint(leader);
			}
			if(DataQueue.IS_KEYPAD_ON == false && DataQueue.IS_AUTO_MODE == true){
				event.setElement();
			}
			
			makeAJob(event);
		}
	}

	/***
	 * event , element 정보를 바탕으로 job개체 생성
	 * @param event
	 */
	public void makeAJob(Event event) {
		//job개체생성
		Job job = new Job(leader, event);
		//리스너에 job 전달 (websockethandler) 해서 UI 갱신
		listener.onCatchTouchEvent(job);
		//event bus로 follower로 메시지 전달 
		DataQueue.followerEventBus.post(job);
	}
	

	/***
	 * Event Monitoring 중 catch 된 event 들을 처리하는 handler
	 */
	public void onCatchEvent(Event event) {
		//현재 레토딩 수행중일때만 수행
		if (DataQueue.IS_CONTROLED) {
			identifyAnEvent(event);
		}
	}

	/***
	 * Orientation Monitoring 중 catch 된 event 들을 처리하는 handler
	 */
	public void onOrientaionChangeEvent(int orientataion) {
		// orientation change
		// natrue is positive condition
		DataQueue.CURRENT_ORIENTATION = orientataion;

		Event event = EventFactory.createEvent(EventType.ORIENTATION_CHANGE);
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("direction", Utils.getRotation(orientataion));
		event.setEventMonitoringData(data);
		makeAJob(event);
	}
}
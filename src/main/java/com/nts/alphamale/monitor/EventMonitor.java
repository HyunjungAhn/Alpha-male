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

package com.nts.alphamale.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.LineIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nts.alphamale.controller.LeaderController;
import com.nts.alphamale.data.DataQueue;
import com.nts.alphamale.data.EventLog;
import com.nts.alphamale.data.Point;
import com.nts.alphamale.data.PositionInfo;
import com.nts.alphamale.data.Settings;
import com.nts.alphamale.event.Event;
import com.nts.alphamale.event.EventFactory;
import com.nts.alphamale.type.EventType;

/***
 * 이벤트를 모니터링 하고 분석하는 class
 * @author NAVER
 *
 */
public class EventMonitor implements Runnable {
	
	
	EventMonitoringListener listener;
	public interface EventMonitoringListener {
		public void onCatchEvent(Event event);
	}

	Logger log = LogManager.getLogger(this.getClass());
	String serial;
	
	final String regex = "^\\[(.*)\\]\\s{1,}(\\S+)\\s{1,}(EV_ABS|EV_KEY)\\s{1,}(\\S+)\\s{1,}(\\S+)";
	final Pattern p = Pattern.compile(regex);
	
	public EventMonitor(String serial,LeaderController leaderController) {
		this.serial = serial;
		this.listener = leaderController;
	}
	
	public void run(){
		try{
			if(DataQueue.IS_CONTROLED){
				log.info("log monitoring");
				eventLogAnalysis((LineIterator) DataQueue.EVENT_EXECUTOR.peek().get("stdOut"));
			}
		}catch(Exception t){
			log.error( "Caught exception in EventMonitor. StackTrace:\n" + t.getStackTrace() );
			this.run();
		}
	}

	
	/**
	 * "adb [-s serial] shell getevent -lt" 스트림을 읽어 이벤트 종류를 판단한다.
	 * @see <a  href="https://source.android.com/devices/input/getevent.html">Getevent</a>
	 * @param li
	 * @throws InterruptedException 
	 */
	public void eventLogAnalysis(LineIterator li) throws Exception {
		boolean tracking= false;
		int multiCount = 0;
		List<EventLog> evtLogList = new ArrayList<EventLog>();
		Map<Integer, List<Point>> multiSlot = new HashMap<Integer, List<Point>>();
		while(li.hasNext()){
			String readLine = li.nextLine().trim();
			Matcher m = p.matcher(readLine);
			if(m.find()){
				EventLog event = new EventLog(m);
				if(readLine.contains("EV_KEY")){
					makeKeyEvent(event);
					evtLogList.clear();
				}
				if(event.getAbsLabel().equals("ABS_MT_TRACKING_ID") && event.getAbsValue()!=Integer.MAX_VALUE){
					if(!multiSlot.containsKey(multiCount))	
						multiSlot.put(multiCount, new ArrayList<Point>());
					multiCount++;
					tracking = true;
					
				}
				if(event.getAbsLabel().equals("ABS_MT_TRACKING_ID") && event.getAbsValue()==Integer.MAX_VALUE){
					multiCount--;
					if(multiCount==0){
						tracking = false;
						if(!evtLogList.isEmpty()){
							makeMultiTrackingEvent(multiSlot, evtLogList);
						}
					}
				}
				
				if(tracking==true){
					if(event.getAbsLabel().contains("ABS_MT_POSITION")  || event.getAbsLabel().contains("ABS_MT_SLOT"))
						evtLogList.add(event);
				}
			}
		}
	}
	
	private EventType guessingEventType(PositionInfo position, double elapsedTime){
		if(position.getTrackPoint().size()>0){
			Point startXY =position.getStartPoint();
			Point endXY = position.getEndPoint();
			int width = startXY.x-endXY.x;
			int height = startXY.y-endXY.y;
			int absWidth = Math.abs(width);
			int absHeight = Math.abs(height);
			//int swipe_distance = absWidth * absHeight;
			Double swipe_distance = Math.sqrt(Math.pow(absWidth, 2) + Math.pow(absHeight, 2));
			
			log.error("swipe distance : " + swipe_distance);
			log.error("swipe SWIPE_AREA_THRESHOLD : " + Settings.SWIPE_AREA_THRESHOLD);
			log.error("elapsedTime : " + elapsedTime);
			log.error("LONG_TAP_THRESHOLD : " + Settings.LONG_TAP_THRESHOLD);
			
			if((swipe_distance / elapsedTime) > 100){
				if(elapsedTime > Settings.LONG_TAP_THRESHOLD){
					return EventType.DRAG;
				}
				if(swipe_distance > 100){
					return EventType.SWIPE;
				}
			}
			
			if(swipe_distance > Settings.SWIPE_AREA_THRESHOLD){
				if(elapsedTime > Settings.LONG_TAP_THRESHOLD){
					return EventType.DRAG;
				}
			}
		}
		
		if(elapsedTime >Settings.LONG_TAP_THRESHOLD){
			return EventType.LONG_TAP;
		}
		return EventType.TAP;
	}

	private void makeMultiTrackingEvent(Map<Integer, List<Point>> multiSlot, List<EventLog> evtLogList){
		//EventInfo event = new EventInfo();
		
		PositionInfo pos = new PositionInfo();
			
		//event start time (timestamp) 
		long evtStartTime = evtLogList.get(0).getCurTimeStamp();
		//event end time (timestamp) 
		long evtEndTime = evtLogList.get(evtLogList.size()-1).getCurTimeStamp();
		//event elaspese time (cpu time not ms) 
		double elaspedTime = evtLogList.get(evtLogList.size()-1).getCpuTimestamp() - evtLogList.get(0).getCpuTimestamp();
		double sElaspedTime = evtLogList.get(evtLogList.size()-1).getCurTimeStamp() - evtLogList.get(0).getCurTimeStamp();
		
		int mt_slot = 0;
		for(EventLog log:evtLogList){
			int x=0,y = 0;
			if(log.getAbsLabel().equals("ABS_MT_SLOT")){
				mt_slot = log.getAbsValue();
			}
			
			if(log.getAbsLabel().contains("ABS_MT_POSITION_X")) x = Integer.valueOf(log.getAbsValue());
			if(log.getAbsLabel().contains("ABS_MT_POSITION_Y")) y = Integer.valueOf(log.getAbsValue());
			
			multiSlot.get(mt_slot).add(new Point(x, y));
		}
		
		Integer[] slots = new Integer[multiSlot.size()];
		slots = multiSlot.keySet().toArray(slots);
		
		Map<Integer, List<Point>> multiTrackInfo = new HashMap<Integer, List<Point>>();
		for(int slot:slots){
			List<Point> points = multiSlot.get(slot);
			List<Point> xyPairs = new ArrayList<Point>();
			int x=0, y=0;
			
			for(Point p:points){
				x = p.x>0?p.x:x;
				y = p.y>0?p.y:y;
				if(x>0 && y>0){
					xyPairs.add(new Point(x,y));
					x = 0;y = 0;
				}
			}
			multiTrackInfo.put(slot, xyPairs);
		}
		Event event = null;
		if(multiSlot.size()==1){
			pos.setTrackPoint(multiTrackInfo.get(0));
			if(!evtLogList.isEmpty()){
				EventType evtType = guessingEventType(pos,elaspedTime);
				event = EventFactory.createEvent(evtType);
			}
		}else if(multiSlot.size()>1){
			pos.setMultiPoint(multiTrackInfo);
			if(!evtLogList.isEmpty()){
				event = EventFactory.createEvent(EventType.GESTURE);
			}
		}
		if(event != null){
			HierarchyMonitor.getInstance(serial).suspend();
			event.setEventStartTime(evtStartTime);
			event.setEventEndTime(evtEndTime);
			event.setElapsedTime(elaspedTime);
			event.setSElapsedTime(sElaspedTime);
			event.setPosition(pos);
			listener.onCatchEvent(event);
			HierarchyMonitor.getInstance(serial).resume();
		}
		multiSlot.clear();
		evtLogList.clear();
	}
	
	private void makeKeyEvent(EventLog evtLog){
		
		if(evtLog.getAbsLabel().equals("BTN_TOUCH")){
			return;
		}
		if(evtLog.getAbsLabel().equals("BTN_TOOL_FINGER")){
			return;
		}
		if(evtLog.getAbsLabel().equals("MT_TOOL_PEN")){
			return;
		}
		
		//TODO 하드웨어 키 long press 처리해야 될 경우 abs_value max(EV_KEY UP 이벤트) 값 확인 후 timestamp를 비교해야 함.
		long evtTime = evtLog.getCurTimeStamp();
				
		if(evtLog.getAbsValue() == Integer.MIN_VALUE){
			Event event = EventFactory.createEvent(EventType.PRESS_KEY);
			Map<String,Object> value = new HashMap<String,Object>();
			value.put("keyValue", "recent");
			if(evtLog.getAbsLabel().contains("HOME")){
				value.put("keyValue", "home");
			}
			if(evtLog.getAbsLabel().contains("BACK")){
				value.put("keyValue", "back");
			}
			if(evtLog.getAbsLabel().contains("VOLUMEUP")){
				value.put("keyValue", "volume up");
			}
			if(evtLog.getAbsLabel().contains("VOLUMEDOWN")){
				value.put("keyValue", "volume down");
			}
			if(evtLog.getAbsLabel().contains("POWER")){
				value.put("keyValue", "power");
			}
			event.setEventStartTime(evtTime);
			event.setEventEndTime(evtTime);
			event.setElapsedTime(0);
			event.setEventMonitoringData(value);
			listener.onCatchEvent(event);
		}
	}
	
}
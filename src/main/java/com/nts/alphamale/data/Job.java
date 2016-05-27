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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.nts.alphamale.event.Event;
import com.nts.alphamale.event.EventFactory;
import com.nts.alphamale.type.EventType;
import com.nts.alphamale.type.JobType;

import lombok.Data;

/***
 * 수행 작업단위인 job 정보
 * Event 및 Element, Leader Device 정보
 *
 */
@Data
public class Job implements Cloneable {

	//cloneable 지원
	public Job clone() throws CloneNotSupportedException {
		Job job = (Job) super.clone();
		job.eventInfo = eventInfo.clone();
		return job;
	}
	   
	static Logger log = LogManager.getLogger(Job.class);

	JobType jobType;

	@Expose
	int seq;
	@Expose
	String title;
	@Expose
	String jobEType;
	@Expose
	DeviceInfo leaderDeviceInfo;
	@Expose
	private Event eventInfo;

	/***
	 * job 개체 생성 (recording)
	 * @param deviceInfo
	 * @param eventInfo
	 */
	public Job(DeviceInfo deviceInfo, Event eventInfo) {
		this.seq = DataQueue.JOB_SEQ++;
		this.leaderDeviceInfo = deviceInfo;
		this.eventInfo = eventInfo;
		this.jobType = eventInfo.getEventSelectorType();
		this.jobEType = jobType.toString();
	}
	
	/***
	 * json 문자열에서 job 개체 생성 (replay)
	 * @param json
	 */
	@SuppressWarnings("unchecked")
	public Job(String json){
		
		Gson gson = new Gson();
		JsonObject jsonObj = gson.fromJson(json,JsonObject.class);
		JsonObject evtObj = jsonObj.get("eventInfo").getAsJsonObject();
		JsonObject dvcObj = jsonObj.get("leaderDeviceInfo").getAsJsonObject();
		
		String type = evtObj.get("eType").getAsString();
		this.seq = jsonObj.get("seq").getAsInt();
		this.title = jsonObj.get("title").getAsString();
		this.jobType = JobType.matchOf(jsonObj.get("jobEType").getAsString());
		this.jobEType =  jobType.toString();
		this.eventInfo = gson.fromJson(evtObj,EventFactory.getClass(EventType.matchOf(type)));
		this.leaderDeviceInfo = gson.fromJson(dvcObj, DeviceInfo.class);
		
		//update 시 
		this.title = eventInfo.getTitle();
	}
	
	public Job(String name, String json){
		Gson gson = new Gson();
		JsonObject jsonObj = gson.fromJson(json,JsonObject.class);
		
		String type = jsonObj.get("eType").getAsString();
		//Event event = EventFactory.createEvent(EventType.matchOf(type));
		
		JsonObject evtObj = jsonObj.get("eventInfo").getAsJsonObject();
		this.eventInfo = gson.fromJson(evtObj,EventFactory.getClass(EventType.matchOf(type)));
		this.eventInfo.setPosition(gson.fromJson(evtObj.get("position"),PositionInfo.class));

		JsonObject dvcObj = jsonObj.get("deviceInfo").getAsJsonObject();
		this.leaderDeviceInfo = gson.fromJson(dvcObj, DeviceInfo.class);
		
		this.seq = DataQueue.JOB_SEQ++;
		this.jobType = eventInfo.getEventSelectorType();
		this.jobEType = jobType.toString();
	}
	
	/***
	 * job object 를 job 문자열로 변환 
	 * @return
	 */
	public String toJson(){
		if(StringUtils.isEmpty(title)){
			title = eventInfo.getTitle();	
		}
		//expose anntation으로 정의된 필드만 포함
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json  = gson.toJson(this);
		return json;
	}
	
	/***
	 * 단말정보에 맞게 좌표정보 변환
	 * @param targetDeivceInfo
	 */
	public void convertPoint(DeviceInfo targetDeivceInfo){
		eventInfo.convertTargetPoint(leaderDeviceInfo, targetDeivceInfo);
	}
}

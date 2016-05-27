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

package com.nts.alphamale.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.google.gson.annotations.Expose;
import com.nts.alphamale.data.DataQueue;
import com.nts.alphamale.data.DeviceInfo;
import com.nts.alphamale.data.ElementInfo;
import com.nts.alphamale.data.Point;
import com.nts.alphamale.data.PositionInfo;
import com.nts.alphamale.handler.DocumentHandler;
import com.nts.alphamale.type.EventType;
import com.nts.alphamale.type.JobType;
import com.nts.alphamale.type.SeletorType;
import com.nts.alphamale.util.Utils;

import lombok.Data;

@Data
public abstract class Event implements Cloneable{
	
	public Event clone() throws CloneNotSupportedException {
		Event event = (Event) super.clone();
		if(position !=null){
			event.position = position.clone();
		}
		return event;
	}
	
	@Expose
	protected PositionInfo position = null;
	@Expose
	protected ElementInfo element = null;
	
	//event type 
	protected EventType type;
	//selector type 
	@Expose
	protected SeletorType sType;
	//expose event type;
	@Expose
	protected String eType;
	
	// event time & elspased time
	protected long eventStartTime;
	protected long eventEndTime;

	protected double elapsedTime;
	protected double sElapsedTime;
	
	protected String title;
	@Expose
	protected String template;
	

	public Event(){}
	
	public Event(EventType type){
		this.type = type;
	}
	
	/** 좌표 및 요소 정보가 아닌 추가 정보가 모니터링중에 전달되는 경우 데이터 세팅**/
	public void setEventMonitoringData(Map<String,Object> data){}
	/** element 정보 세팅**/
	public void setElement(){}
	/** 좌표정보 세팅 **/
	public void setPosition(PositionInfo position){
		this.position = position;
	};
	/** jsonrpc로 전달할 parameter 정보  조회**/
	abstract public List<Object> getParamsInfo();
	/** UI에 노출할 타이틀  **/
	abstract public String getTitle();
	/** UI에 노출 **/
	abstract  public String getTemplate();
	
	/****
	 * event의 job type 결정
	 * @return
	 */
	public JobType getEventSelectorType(){
		if(position == null && element == null){
			return JobType.SYSTEM_BASE;
		}
		
		if(element != null && element.isIdentified()){
			return JobType.ELEMENT_BASE;
		}
		
		return JobType.POSITION_BASE;
	}
	
	/***
	 * 디바이스 정보에 맞는 좌표 정보로 변환
	 * @param device
	 */
	public void convertPoint(DeviceInfo device){
		Point startXY = Utils.rotaionPoint(DataQueue.CURRENT_ORIENTATION,
				Utils.scalePoint(position.getStartPoint(), device.getxScale(), device.getyScale()), device.getWidth(),
				device.getHeight());
		Point endXY = Utils.rotaionPoint(DataQueue.CURRENT_ORIENTATION,
					Utils.scalePoint(position.getEndPoint(), device.getxScale(), device.getyScale()), device.getWidth(),
					device.getHeight());
		position.getTrackPoint().clear();
		position.setStartPoint(startXY);
		position.setEndPoint(endXY);
	}
	
	/**
	 * leader 단말의 좌표정보를 follower에 맞는 좌표 정보로 변환
	 * @param orgin
	 * @param target
	 */
	public void convertTargetPoint(DeviceInfo orgin , DeviceInfo target){
		int targetWidth = target.getWidth();
		int targetHeight = target.getHeight();
		
		double xScale = target.getxScale();
		double yScale = target.getyScale();
		
		//start, end point converted
		Point cvtStartPt = Utils.convertPoint(orgin.getWidth(),orgin.getHeight(), position.getStartPoint(), targetWidth, targetHeight, xScale, yScale);
		Point cvtEndPt = Utils.convertPoint(orgin.getWidth(),orgin.getHeight(), position.getEndPoint(),  targetWidth, targetHeight, xScale, yScale);
		position.setStartPoint(cvtStartPt);
		position.setEndPoint(cvtEndPt);
		
		//gesture extra point converted
		if(position.getEx_startPoint() != null && position.getEx_endPoint() != null){
			Point cvtExStartPt = Utils.convertPoint(orgin.getWidth(),orgin.getHeight(), position.getEx_startPoint(), targetWidth, targetHeight, xScale, yScale);
			Point cvtExEndPt = Utils.convertPoint(orgin.getWidth(),orgin.getHeight(), position.getEx_endPoint(),  targetWidth, targetHeight, xScale, yScale);
			position.setEx_startPoint(cvtExStartPt);
			position.setEx_endPoint(cvtExEndPt);
		}
	}
	
	/***
	 * hierarchy dump 에서 좌표를 기반으로 Element 정보 추출 
	 * @return
	 */
	protected Map<String, Object> getSelectedElementInfo(){
		
		/** hierarchy dump 실패 시 이전의 dump를 이용 **/
		Document doc = null;
		Node node =null;
		
		doc = !DataQueue.DOCUMENT_QUEUE.isEmpty() ? DataQueue.DOCUMENT_QUEUE.pollLast() : doc;

		String dt = doc!=null?DocumentHandler.findStringByXPath(doc,"//@time"):null;
		long dumpTime = 0l;

		if (null != dt)
			dumpTime = Long.parseLong(dt);

		while (eventStartTime < dumpTime && !DataQueue.DOCUMENT_QUEUE.isEmpty()) {
			/** hierarchy dump 실패 시 이전의 dump를 이용 **/
			doc = !DataQueue.DOCUMENT_QUEUE.isEmpty() ? DataQueue.DOCUMENT_QUEUE.pollLast() : doc;
			dt = doc!=null?DocumentHandler.findStringByXPath(doc,"//@time"):null;
			if (null != dt)
				dumpTime = Long.parseLong(dt);
		}
		
		String bounds = "";
		int instanceAt = 0;
		if (null != doc && position != null) {
			int x = position.getStartPoint().x;
			int y = position.getStartPoint().y;
			node = DocumentHandler.findElementWithBounds(doc, x, y);
			bounds = node != null ? node.getAttributes().getNamedItem("bounds").getNodeValue() : null;
			instanceAt = node != null
					? DocumentHandler.getInstanceAt(doc, DocumentHandler.convertNodeToXpath(node), "bounds", bounds)
					: -1;
		}
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("node", node);
		result.put("instanceAt", instanceAt);
		return result;
	}
}

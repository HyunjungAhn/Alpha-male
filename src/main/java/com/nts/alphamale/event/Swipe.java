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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.nts.alphamale.data.DeviceInfo;
import com.nts.alphamale.data.Point;
import com.nts.alphamale.data.PositionInfo;
import com.nts.alphamale.type.DirectionType;
import com.nts.alphamale.type.EventType;
import com.nts.alphamale.type.SeletorType;
import com.nts.alphamaleWeb.msg.TemplateVO;

public class Swipe extends Event{

	Logger log = LogManager.getLogger(Swipe.class);	
	
	@Expose
	private int speed = 50;
	@Expose
	private DirectionType direction;


	/***
	 * set point info
	 * @param postion
	 */
	public Swipe(){
		type = EventType.SWIPE;
		sType = SeletorType.POSITION;
		eType = type.toString();
	}
	

	@Override
	public List<Object> getParamsInfo(){
		List<Object> params = new ArrayList<Object>();
		params.add(position.getStartPoint().x);
		params.add(position.getStartPoint().y);
		params.add(position.getEndPoint().x);
		params.add(position.getEndPoint().y);
		params.add(speed);
		return params;
	}
	
	@Override
	public String getTitle(){
		String format = "%s %s (%d,%d -> %d,%d)";
		return String.format(format, type.toString(), direction.toString(), position.getStartPoint().x, position.getStartPoint().y, position.getEndPoint().x, position.getEndPoint().y);
	}
	
	@Override
	public void setPosition(PositionInfo position){
		this.position = position;
		setDirection();
	}
	
	private void setDirection(){
		int tmpX = position.getStartPoint().x - position.getEndPoint().x;
		int tmpY = position.getStartPoint().y - position.getEndPoint().y;
		double tmpAbs = Math.abs(tmpX) - Math.abs(tmpY);
		
		if (tmpAbs > 0) {
			if (tmpX > 0) {
				this.direction =  DirectionType.LEFT;
			} else {
				this.direction = DirectionType.RIGHT;
			}
			Double dSpeed = (100/(Math.abs(tmpX) / (elapsedTime * 1000)));
			this.speed = dSpeed.intValue(); 
		} else {
			if (tmpY > 0) {
				this.direction = DirectionType.UP;
			} else {
				this.direction = DirectionType.DOWN;
			}
			Double dSpeed = (100/(Math.abs(tmpY) / (elapsedTime * 1000)));
			if(dSpeed.intValue() != 0){
				this.speed = dSpeed.intValue();
			}
		}
	}
	
	public void convertTargetPoint(DeviceInfo origin, DeviceInfo target){
		super.convertTargetPoint(origin, target);
		Point cvtEndPoint = position.getEndPoint();
		
		double rate_X = (target.getWidth() / target.getxScale()) / (origin.getWidth()/ origin.getxScale());
		double rate_Y = (target.getHeight() / target.getyScale()) / (origin.getHeight()/ origin.getyScale());
		//int eFactor = Double.valueOf(extra_distance * rate).intValue(); 
		
		//log.info(extra_distance + " : " + rate + " : "  + eFactor);
		
		log.info("convert swipe point / speed  " + speed +" : " + rate_Y);
		
		if(rate_X < 1){
			rate_X = 1;
		}
		if(rate_Y < 1){
			rate_Y = 2;
		}
		
		if((direction == DirectionType.UP || direction == DirectionType.DOWN)){
			this.speed = Double.valueOf(this.speed / rate_Y).intValue();
			//DOWN
			if(position.getStartPoint().y > position.getEndPoint().y){
				cvtEndPoint.y = cvtEndPoint.y  -  Double.valueOf(Math.pow(speed * rate_Y,2)).intValue();
				//cvtEndPoint.y = cvtEndPoint.y  -  eFactor;
				if(cvtEndPoint.y < 0 ) cvtEndPoint.y = 0;
			}else{
				cvtEndPoint.y = cvtEndPoint.y  +   Double.valueOf(Math.pow(speed * rate_Y,2)).intValue();
				//cvtEndPoint.y = cvtEndPoint.y  +  eFactor;
				int height =  target.getHeight();
				if(cvtEndPoint.y > height) cvtEndPoint.y = height;
			}
		}
		
		if(( direction == DirectionType.LEFT || direction == DirectionType.RIGHT)){
			this.speed = Double.valueOf(this.speed / rate_Y).intValue();
			if(position.getStartPoint().x > position.getEndPoint().x){
				cvtEndPoint.x = cvtEndPoint.x  -  Double.valueOf(Math.pow(speed,rate_X)).intValue();
				//cvtEndPoint.x = cvtEndPoint.x  -  eFactor;
				if(cvtEndPoint.x < 0 ) cvtEndPoint.x = 0;
			}else{
				cvtEndPoint.x = cvtEndPoint.x  +   Double.valueOf(Math.pow(speed,rate_X)).intValue();
				//cvtEndPoint.x = cvtEndPoint.x +  eFactor;
				int width =  target.getWidth();
				if(cvtEndPoint.x > width) cvtEndPoint.x = width;
			}
		}
		position.setEndPoint(cvtEndPoint);
	}

	@Override
	public void setEventMonitoringData(Map<String, Object> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTemplate() {
		Gson gson = new Gson();
		String template= null; 
		TemplateVO vo = new TemplateVO(sType,template);
		String json = gson.toJson(vo);
		return json;
	}
}

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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import lombok.Data;

/****
 * Element 정보 
 *
 */
@Data
public class ElementInfo {
	
	static Logger log = LogManager.getLogger(ElementInfo.class);
	
	//uiautomator element info
	private int index;
	@Expose
	private String text;
	@Expose
	private String resourceId;
	@Expose
	private String className;
	private String packageName;
	@Expose
	private String contentDesc;
	private boolean checkable;
	private boolean checked;
	private boolean clickable;
	private boolean enalbled;
	private boolean focusable;
	private boolean foucsed;
	private boolean scrollable;
	private boolean longClickable;
	private boolean password;
	private boolean selected;
	@Expose
	private String bounds;
	@Expose
	private String instance ="0";

	//bounds parsing element 
	private int width;
	private int height;
	
	/***
	 * 조회된 Element 정보가 사용가능한지 여부 확인 
	 * @return
	 */
	public boolean isIdentified(){
		if(StringUtils.isEmpty(instance)){
			instance = "0";
		}
		if(StringUtils.isNotEmpty(text) 
			|| (StringUtils.isNotEmpty(className) && (Integer.valueOf(instance) !=0)) 
			|| StringUtils.isNotEmpty(contentDesc)
			|| StringUtils.isNotEmpty(resourceId)){
				return true;
			}
		return false;
	}
	
	/***
	 * Element 정보를 UIAutomator에서 사용할 수 있는 형태의 Seletor 형태로 변환
	 * @return
	 */
	public Object getSelector() {
		Map<String, Object> selecotr = new HashMap<String,Object>();
		int mask = 0;
		selecotr.put("childOrSiblingSelector", new Object[] {});

		if (StringUtils.isNotEmpty(resourceId)) {
			selecotr.put("resourceId",resourceId);
			mask = mask + 2097152;
		}else{
		}
		if(StringUtils.isNotEmpty(text)){
			selecotr.put("text", text);
			mask = mask + 1;
		}

		if(StringUtils.isEmpty(text) && StringUtils.isNotEmpty(contentDesc)){
			selecotr.put("description", contentDesc);
			mask = mask + 64;
		}
		
		if (StringUtils.isNotEmpty(className)) {
			selecotr.put("className", className);
			mask = mask + 16;
		}
		if(StringUtils.isNotEmpty(instance) && !"0".equals(instance)){
			selecotr.put("instance", instance);
			mask = mask + 16777216;
		}

		selecotr.put("mask", mask);
		selecotr.put("childOrSibling", new Object[] {});
		return selecotr;
	}
	/***
	 * Element 정보를 json 문자열로 변환 
	 * @return
	 */
	public String toJson(){
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json  = gson.toJson(this);
		return json;
	}
	
	/***
	 * json에서 Element 정보로 변환
	 * @param json 
	 */
	public static ElementInfo fromJson(String json){
		Gson gson = new GsonBuilder().create();
		ElementInfo element = gson.fromJson(json, ElementInfo.class);
		return element;
	}
	
	/**
	 * 타이틀 생성을 위한 식별 문자열 생성 
	 * @return
	 */
	public String getIndentified() {
		String identified = null;
		if(StringUtils.isNotEmpty(resourceId)){
			identified = "id : " + resourceId;
		}else if(StringUtils.isNotEmpty(text)){
			identified = "text : " + text;
		}else if(StringUtils.isNotEmpty(contentDesc)){
			identified = "desc : " + contentDesc;
		}else if(StringUtils.isNotEmpty(className)){
			identified = "class : "+className;
		}
		if(StringUtils.isEmpty(instance)){
			this.instance="0";
		}
		if(Integer.valueOf(instance) != 0){
			identified += "[" + instance + "]";
		}
		
		return identified;
	}
	
	public void setInstance(String instance){
		if(StringUtils.isEmpty(instance)){
			this.instance="0";
		}
		this.instance = instance;
	}
}

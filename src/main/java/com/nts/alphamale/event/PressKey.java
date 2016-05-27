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

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.nts.alphamale.type.EventType;
import com.nts.alphamale.type.SeletorType;
import com.nts.alphamaleWeb.msg.TemplateVO;

import lombok.Data;

@Data
public class PressKey extends Event {
	@Expose
	public String keyValue;
	
	public PressKey(){
		type = EventType.PRESS_KEY;
		eType = type.toString();
		sType = SeletorType.NONE;
	}
	
	
	@Override
	public List<Object> getParamsInfo() {
		List<Object> params = new ArrayList<Object>();
		params.add(keyValue);
		return params;
	}
	
	@Override 
	public void setEventMonitoringData(Map<String,Object> data){
		keyValue = (String) data.get("keyValue");
	}


	@Override
	public String getTitle(){
		String title=type.toString() + "( " +keyValue+ " )";
		this.template = getTemplate();
		return title;
	}
	
	@Override
	public String getTemplate() {
		Gson gson = new Gson();
		String template = "<tr><td>Key Value : <input type=\"text\" value=\"%s\" id=\"keyValue\"> </td></tr>";
		TemplateVO vo = new TemplateVO(sType,String.format(template, keyValue));
		String json = gson.toJson(vo);
		return json;
		
	}
}

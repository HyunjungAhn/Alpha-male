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

import org.w3c.dom.Node;

import com.google.gson.Gson;
import com.nts.alphamale.handler.DocumentHandler;
import com.nts.alphamale.type.EventType;
import com.nts.alphamale.type.JobType;
import com.nts.alphamale.type.SeletorType;
import com.nts.alphamaleWeb.msg.TemplateVO;

public class Click extends Event{

	public Click(){
		type = EventType.TAP;
		sType = SeletorType.BOTH;
		eType = type.toString();
	}
	
	@Override
	public void setElement(){
		Map<String,Object> nodeInfo = super.getSelectedElementInfo();
		Node node = (Node) nodeInfo.get("node");
		element = DocumentHandler.convertNodeToElementObject(node);
		if (element != null) {
			element.setInstance(String.valueOf(nodeInfo.get("instanceAt")));
		}
	}
	
	@Override
	public List<Object> getParamsInfo() {
		List<Object> params = new ArrayList<Object>();
		if (getEventSelectorType() == JobType.ELEMENT_BASE) {
			params.add(element.getSelector());
		} else {
			params.add(position.getStartPoint().x);
			params.add(position.getStartPoint().y);
		}
		return params;
	}
	
	@Override
	public String getTitle(){
		if (getEventSelectorType() == JobType.ELEMENT_BASE){
			String format = "%s (%s)";
			title  = String.format(format, type.toString(), element.getIndentified());
		}else{
			String format = "%s (%d,%d)";
			title  = String.format(format, type.toString() , position.getStartPoint().x,position.getStartPoint().y);
		}
		return title;
	}
	
	@Override
	public String getTemplate(){
		Gson gson = new Gson();
		String template= null; 
		TemplateVO vo = new TemplateVO(sType,template);
		String json = gson.toJson(vo);
		return json;
	}

}

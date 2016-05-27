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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.nts.alphamale.type.EventType;
import com.nts.alphamale.type.SeletorType;
import com.nts.alphamale.util.Utils;
import com.nts.alphamaleWeb.msg.TemplateVO;

public class Gesture extends Event {
	
	public Gesture(){
		type = EventType.GESTURE;
		eType = type.toString();
		sType = SeletorType.EX_POSITION;
	}
	
	@Override
	public List<Object> getParamsInfo() {
		List<Object> params = new ArrayList<Object>();
		
		Map<String, Object> ele = new HashMap<String, Object>();
		ele.put("childOrSiblingSelector", new Object[]{});
		ele.put("className", "android.widget.FrameLayout");
		ele.put("mask", 16);
		ele.put("childOrSibling", new Object[]{});
		params.add(ele);
		
		params.add(Utils.pointToMap(position.getStartPoint()));
		params.add(Utils.pointToMap(position.getEx_startPoint()));
		params.add(Utils.pointToMap(position.getEndPoint()));
		params.add(Utils.pointToMap(position.getEx_endPoint()));
		params.add(20);
		return params;
	}

	@Override
	public String getTemplate() {
		Gson gson = new Gson();
		String template= null; 
		TemplateVO vo = new TemplateVO(sType,template);
		String json = gson.toJson(vo);
		return json;
	}
	
	@Override
	public String getTitle(){
		String format = "%s (%d,%d,%d,%d -> %d,%d,%d,%d)";
		title  = String.format(format, type.toString() , position.getStartPoint().x,position.getStartPoint().y, position.getEndPoint().x, position.getEndPoint().y,
				position.getEx_startPoint().x, position.getEx_startPoint().y, position.getEx_endPoint().x, position.getEx_endPoint().y);
		return title;
	}
}

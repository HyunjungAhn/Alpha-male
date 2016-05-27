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
import com.nts.alphamale.type.EventType;
import com.nts.alphamale.type.SeletorType;
import com.nts.alphamaleWeb.msg.TemplateVO;

public class DoubleClick extends Event{
	
	public DoubleClick() {
		type = EventType.DOUBLE_TAP;
		eType = type.toString();
		sType = SeletorType.POSITION;
	}

	@Override
	public void setEventMonitoringData(Map<String, Object> data) {
	}

	@Override
	public List<Object> getParamsInfo() {
		List<Object> params = new ArrayList<Object>();
		params.add(position.getStartPoint().x);
		params.add(position.getStartPoint().y);
		return params;
	}

	@Override
	public String getTitle() {
		String format = "%s (%d,%d)";
		return String.format(format, type.toString(), position.getStartPoint().x, position.getStartPoint().y);
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

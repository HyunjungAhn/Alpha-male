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
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.nts.alphamale.data.Point;
import com.nts.alphamale.type.DirectionType;
import com.nts.alphamale.type.EventType;
import com.nts.alphamale.type.SeletorType;
import com.nts.alphamale.util.Utils;
import com.nts.alphamaleWeb.msg.TemplateVO;

import lombok.Data;

@Data
public class ScrollPage extends Event{
	@Expose
	private int page = 1;
	@Expose
	private String direction;
	
	public ScrollPage(){
		type = EventType.SCROLL_PAGE;
		eType = type.toString();
		sType = SeletorType.ELEMENT;
	}

	@Override
	public List<Object> getParamsInfo() {
		List<Integer> bounds =  Utils.covertBoundsToStringArray(element.getBounds());
		int x = bounds.get(0); 
		int y = bounds.get(1);
		int width = bounds.get(2);
		int height = bounds.get(3);
		int xBuffer = Double.valueOf(width * 0.10).intValue();
		int yBuffer = Double.valueOf(height * 0.20).intValue();
		
		Point startPoint = null;
		Point endPoint = null;
		//터치영역을 인식하기 위한 -2 (실제 1080에서 -1 pixel 만큼을 인식함)
		DirectionType dtnCd = DirectionType.matchOf(this.direction);
		switch(dtnCd){
		case LEFT:
			startPoint = new Point(x + xBuffer, (y+height) / 2);
			endPoint = new Point(x + width - 2, (y+height) / 2);
			break;
		case RIGHT:
			startPoint = new Point(x + width - 2, (y+height) / 2);
			endPoint = new Point(xBuffer, (y+height) / 2);
			break;
		case UP:
			startPoint = new Point((x+width) /2, y + yBuffer);
			endPoint = new Point((x+width) /2, y+height - 2);
			break;
		case DOWN:
			startPoint = new Point((x+width) /2, y + height - yBuffer);
			endPoint = new Point((x+width) /2, y-2);
			break;
		}
		
		if( startPoint != null && endPoint != null){
			List<Object> objects = new ArrayList<Object>();
			objects.add(startPoint.x);
			objects.add(startPoint.y);
			objects.add(endPoint.x);
			objects.add(endPoint.y);
			objects.add(50);
			return objects;
		}else{
			return null;
		}
	}

	@Override
	public String getTitle() {
		this.template = this.getTemplate();
		String format = "%s %s (page = %d)";
		return String.format(format, type.toString(), direction, page);
	}

	@Override
	public String getTemplate() {
		Gson gson = new Gson();
		String template = "<tr><td>Page Count : <input type=\"text\" value=\"%d\" id=\"page\"> </td></tr><tr><td>Direction : <input type=\"text\" value=\"%s\" id=\"direction\"> </td></tr>";
		TemplateVO vo = new TemplateVO(sType,String.format(template, page,direction));
		String json = gson.toJson(vo);
		return json;
	}
}

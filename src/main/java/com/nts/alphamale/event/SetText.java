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
import com.nts.alphamale.type.EventType;
import com.nts.alphamale.type.SeletorType;
import com.nts.alphamaleWeb.msg.TemplateVO;

public class SetText extends Event {
	@Expose
	private String text;
	
	public SetText(){
		type = EventType.SET_TEXT;
		eType = type.toString();
		sType = SeletorType.ELEMENT;
	}
	
	@Override
	public List<Object> getParamsInfo() {
		List<Object> params = new ArrayList<Object>();
		params.add(element.getSelector());
		params.add(text);
		return params;
	}

	@Override
	public String getTitle() {
		this.template = getTemplate();
		String title=type.toString() + "( " +text+ " )";
		return title;
	}

	@Override
	public String getTemplate() {
		Gson gson = new Gson();
		String template = "<tr><td> Text : <input type=\"text\" value=\"%s\" id=\"text\"></td></tr>";
		TemplateVO vo = new TemplateVO(sType,String.format(template, text));
		String json = gson.toJson(vo);
		return json;
	}

}

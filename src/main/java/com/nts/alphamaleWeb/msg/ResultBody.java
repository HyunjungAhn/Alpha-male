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

package com.nts.alphamaleWeb.msg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.nts.alphamaleWeb.type.Code;

public class ResultBody<T> {
	@Expose
	public Code code;
	@Expose
	public String msg;
	@Expose
	public T payload;

	
	public void setCode(Code code, T payload){
		this.code =  code;
		this.msg = code.getMsg();
		if(payload != null){
			this.payload = payload;
		}
	}
	
	public void setCode(Code code){
		this.code =  code;
		this.msg = code.getMsg();
	}
	
	public String toJson(){
		Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
		return  gson.toJson(this);
	}
}

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

package com.nts.alphamaleWeb.type;

public enum Code {
	OK("SUCCESS"),
	D100("검색된 접속된 기기가 없습니다"),
	F100("export 파일 생성에 실패 했습니다"),
	F200("파일을 Import 하는데 실패했습니다."),
	F300("Import 파일 양식에 맞지 않습니다."),
	F400("APK파일을 업로드 하는데 실패했습니다.");

	public String value;
	Code(String value){
		this.value = value;
	}
	
	public String getMsg(){
		return value;
	}

	
}

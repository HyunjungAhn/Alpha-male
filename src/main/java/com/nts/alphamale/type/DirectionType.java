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

package com.nts.alphamale.type;

import org.apache.commons.lang3.StringUtils;

/***
 * 방향 
 * (drag, swipe , orientation change에서 사용)
 *
 */
public enum DirectionType {
	NATURAL("natural"),
	LEFT("left"),
	RIGHT("right"),
	UP("up"),
	DOWN("down");
	
	private final String value;
	DirectionType(String value){
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}

	public static DirectionType matchOf(String value) {
		if(StringUtils.isEmpty(value)){
			return DirectionType.NATURAL;
		}
		for (DirectionType v : values()) {
			if (v.toString().equalsIgnoreCase(value)) {
				return v;
			}
		}
		throw new IllegalArgumentException();
	}
}

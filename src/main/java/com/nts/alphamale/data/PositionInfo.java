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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;

import lombok.Data;

/***
 * 이벤트 좌표 정보
 *
 */
@Data 
public class PositionInfo implements Cloneable{
	public PositionInfo clone() throws CloneNotSupportedException {
		return (PositionInfo) super.clone();
	}
	
	@Expose
	private Point startPoint;
	@Expose
	private Point endPoint;
	@Expose
	private Point ex_startPoint;
	@Expose
	private Point ex_endPoint;
	
	// track point
	private List<Point> trackPoint = new ArrayList<Point>();

	// multi point
	private Map<Integer, List<Point>> multiPoint = new HashMap<Integer, List<Point>>();
	
	
	public void setTrackPoint(List<Point> trackPoint){
		this.trackPoint = trackPoint;
		this.startPoint = trackPoint.get(0);
		this.endPoint =  trackPoint.get(trackPoint.size() - 1);
	}
	
	public void setMultiPoint(Map<Integer, List<Point>> multiPoint){
		this.multiPoint = multiPoint;
		Integer[] slots = new Integer[this.multiPoint.size()];
		slots = this.multiPoint.keySet().toArray(slots);
		List<Point> mp = new ArrayList<Point>();
		for(int slot:slots){
			int mpCount = this.multiPoint.get(slot).size();
			if(mpCount>1){
				mp.add(this.multiPoint.get(slot).get(0));
				mp.add(this.multiPoint.get(slot).get(mpCount-1));
			}
		}
		if(mp.size()>3){
			this.startPoint = mp.get(0);
			this.ex_startPoint = mp.get(2);
			this.endPoint = mp.get(1);
			this.ex_endPoint = mp.get(3);
		}
	}
}

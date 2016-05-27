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

import java.util.regex.Matcher;

import org.apache.commons.exec.util.StringUtils;

import lombok.Data;

/***
 * getEvent에서 조회된 Event 정보
 *
 */
@Data
public class EventLog {

	String absLabel;
	int absValue;
	String deviceName;
	String evSynOrAbs;
	double cpuTimestamp;
	long curTimeStamp;

	public EventLog(double timestamp, String deviceName, String ev_syn_or_abs, String abs_label, int abs_value){
		this.cpuTimestamp = timestamp;
		this.curTimeStamp = System.currentTimeMillis();
		this.deviceName = deviceName;
		this.evSynOrAbs = ev_syn_or_abs;
		this.absLabel = abs_label;
		this.absValue = abs_value;
	}
	
	public EventLog(Matcher m){
		cpuTimestamp = Double.valueOf(m.group(1));
		curTimeStamp = System.currentTimeMillis();
		deviceName = m.group(2);
		evSynOrAbs = m.group(3);
		absLabel = m.group(4);
		try{
		absValue = !m.group(5).equals("ffffffff")?Integer.valueOf(m.group(5), 16):Integer.MAX_VALUE;
		}catch(NumberFormatException e){
			if(m.group(5).equalsIgnoreCase("down")){
				absValue = Integer.MIN_VALUE;
			}
			if(m.group(5).equalsIgnoreCase("up")){
				absValue = Integer.MAX_VALUE;
			}
		}
	}

	public EventLog(String eventLog){
		String[] evLog = StringUtils.split(eventLog, " ");
		cpuTimestamp = Double.valueOf(StringUtils.split(evLog[1], "]")[0]);
		curTimeStamp = System.currentTimeMillis();
		deviceName = evLog[2];
		evSynOrAbs = evLog[3];
		absLabel = evLog[4];
		try{
		absValue = !evLog[5].equals("ffffffff")?Integer.valueOf(evLog[5], 16):Integer.MAX_VALUE;
		}catch(NumberFormatException e){
			if(evLog[5].equalsIgnoreCase("down")){
				absValue = Integer.MIN_VALUE;
			}
			if(evLog[5].equalsIgnoreCase("up")){
				absValue = Integer.MAX_VALUE;
			}
		}
	}

	
	public String toString(){
		return cpuTimestamp+" "+deviceName+" "+evSynOrAbs+" "+absLabel+" "+absValue;
	}
}

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

package com.nts.alphamale.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.annotations.Expose;

import lombok.Data;

/***
 * 수행로그
 * @author NAVER
 *
 */
@Data
public class ExecutionLog {
	@Expose
	private String serial;
	@Expose
	private String model;
	@Expose
	private int jobSeq;
	@Expose
	private String jobName;
	@Expose
	private String time;
	@Expose
	private boolean isSuccess;
	@Expose
	private String cause;
	
	public ExecutionLog(int jobSeq , String jobName, String serial, String model, boolean isSuccess, String cause){
		this.serial = serial;
		this.model = model;
		this.isSuccess = isSuccess;
		this.cause = cause;
		
		this.jobName = jobName;
		this.jobSeq = jobSeq;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
		Date currentTime = new Date();
		this.time = formatter.format(currentTime);
	}
}

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

import com.google.gson.annotations.Expose;
import com.nts.alphamale.data.DeviceInfo;

import lombok.Data;

@Data
public class ScanDeviceResultVO{
	@Expose
	public String title;
	@Expose
	public DeviceInfo deviceInfo;
	
	public ScanDeviceResultVO(String title, DeviceInfo deviceInfo){
		this.title = title;
		this.deviceInfo = deviceInfo;
	}
}

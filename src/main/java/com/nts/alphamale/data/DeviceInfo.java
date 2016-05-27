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

import java.util.Map;

import com.google.gson.annotations.Expose;
import com.nts.alphamale.util.Utils;

/***
 * 디바이스 정보 
 *
 */
public class DeviceInfo{
	
	String build;
	String manufacturer;
	String model;
	@Expose
	String serial;

	// adb shell dumpsys input
	@Expose
	int width = 0;
	@Expose
	int height = 0;
	@Expose
	double xScale = 1;
	@Expose
	double yScale = 1;
	
	double tapInterval;
	double tapDragInterval;
	double multitouchSettleInterval;
	double multitouchMinDistance;
	double swipeTransitionAngleCosine;
	double swipeMaxWidthRatio;
	double movementSpeedRatio;
	double zoomSpeedRatio;

	PointerVelocityControlParameters velocityParams;

	public DeviceInfo(String serial) {
		this.serial = serial;
	}
	
	public DeviceInfo(int width, int height){
		this.width = width;
		this.height = height;
	}

	public DeviceInfo() {
	}

	public String getBuild() {
		return build;
	}

	public int getHeight() {
		if(yScale ==0){
			yScale = 1;
		}
		return Math.round((float) (height * getyScale()));
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public String getModel() {
		return model;
	}

	public String getSerial() {
		return serial;
	}

	public int getWidth() {
		if(xScale ==0){
			xScale = 1;
		}
		return Math.round((float) (width * getxScale()));
	}

	public double getxScale() {
		return xScale;
	}

	public double getyScale() {
		return yScale;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public void setDeviceModel(String model) {
		this.model = model;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setxScale(double xScale) {
		this.xScale = xScale;
	}

	public void setyScale(double yScale) {
		this.yScale = yScale;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public PointerVelocityControlParameters getVelocityParams() {
		return velocityParams;
	}

	public void setVelocityParams(Map<String, Object> velocityParams) {
		this.velocityParams = new PointerVelocityControlParameters(
				(String)velocityParams.get("scale"),
				(String)velocityParams.get("lowThreshold"),
				(String)velocityParams.get("highThreshold"),
				(String)velocityParams.get("acceleration"));
	}

	public Point convertPoint(int originWidth, int originHeight, Point originPt) {
		double[] ratioXY = Utils.getScreenRatio(originWidth, originHeight, this.width, this.height);
		Point convertPt = new Point((int) (originPt.x * ratioXY[0]), (int) (originPt.y * ratioXY[1]));
		return convertPt;
	}

	public double getTapInterval() {
		return tapInterval;
	}

	public void setTapInterval(double tapInterval) {
		this.tapInterval = tapInterval;
	}

	public double getTapDragInterval() {
		return tapDragInterval;
	}

	public void setTapDragInterval(double tapDragInterval) {
		this.tapDragInterval = tapDragInterval;
	}

	public double getMultitouchSettleInterval() {
		return multitouchSettleInterval;
	}

	public void setMultitouchSettleInterval(double multitouchSettleInterval) {
		this.multitouchSettleInterval = multitouchSettleInterval;
	}

	public double getMultitouchMinDistance() {
		return multitouchMinDistance;
	}

	public void setMultitouchMinDistance(double multitouchMinDistance) {
		this.multitouchMinDistance = multitouchMinDistance;
	}

	public double getSwipeTransitionAngleCosine() {
		return swipeTransitionAngleCosine;
	}

	public void setSwipeTransitionAngleCosine(double swipeTransitionAngleCosine) {
		this.swipeTransitionAngleCosine = swipeTransitionAngleCosine;
	}

	public double getSwipeMaxWidthRatio() {
		return swipeMaxWidthRatio;
	}

	public void setSwipeMaxWidthRatio(double swipeMaxWidthRatio) {
		this.swipeMaxWidthRatio = swipeMaxWidthRatio;
	}

	public double getMovementSpeedRatio() {
		return movementSpeedRatio;
	}

	public void setMovementSpeedRatio(double movementSpeedRatio) {
		this.movementSpeedRatio = movementSpeedRatio;
	}

	public double getZoomSpeedRatio() {
		return zoomSpeedRatio;
	}

	public void setZoomSpeedRatio(double zoomSpeedRatio) {
		this.zoomSpeedRatio = zoomSpeedRatio;
	}
	
	// PointerGesture configuration
	class PointerVelocityControlParameters {
		double scale;
		double lowThreshold;
		double highThreshold;
		double acceleration;
		
		public PointerVelocityControlParameters(String scale, String lowThreshold, String highThreshold, String acceleration) {
			this.scale = Double.valueOf(scale);
			this.lowThreshold = Double.valueOf(lowThreshold);
			this.highThreshold = Double.valueOf(highThreshold);
			this.acceleration = Double.valueOf(acceleration);
		}


		public PointerVelocityControlParameters(double scale, double lowThreshold, double highThreshold, double acceleration) {
			this.scale = scale;
			this.lowThreshold = lowThreshold;
			this.highThreshold = highThreshold;
			this.acceleration = acceleration;
		}

		public double getScale() {
			return scale;
		}

		public void setScale(double scale) {
			this.scale = scale;
		}

		public double getLowThreshold() {
			return lowThreshold;
		}

		public void setLowThreshold(double lowThreshold) {
			this.lowThreshold = lowThreshold;
		}

		public double getHighThreshold() {
			return highThreshold;
		}

		public void setHighThreshold(double highThreshold) {
			this.highThreshold = highThreshold;
		}

		public double getAcceleration() {
			return acceleration;
		}

		public void setAcceleration(double acceleration) {
			this.acceleration = acceleration;
		}
	}
}

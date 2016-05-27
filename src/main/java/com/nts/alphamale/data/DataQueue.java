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
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.w3c.dom.Document;

import com.google.common.eventbus.AsyncEventBus;
import com.nts.alphamale.handler.FollowerHandler;

/****
 * 전역으로 사용된 Data 정보 설정 클래스
 *
 */
public class DataQueue {
	/** Hierarchy Dump Queue **/
	public static LinkedBlockingDeque<Document> DOCUMENT_QUEUE = new LinkedBlockingDeque<Document>(3);
	/** CURRENT LEADER ORIENTATION CODE(0:natural, 1:left, 3:right) **/
	public static Integer CURRENT_ORIENTATION = 0;
	/** EVENT EXECUTOR QUEUE **/
	public static LinkedBlockingQueue<Map<String, Object>> EVENT_EXECUTOR = new LinkedBlockingQueue<Map<String, Object>>(1);
	
	/** Choose Test Driver(Default: ELEMENT DRIVEN) **/
	public static Boolean IS_AUTO_MODE = true;
	/** Control(Event Injection) on/off **/
	public static Boolean IS_CONTROLED = false;
	/** KEYPAD MONITOR **/
	public static Boolean IS_KEYPAD_ON = false;
	/** DEVICE LIST MAP **/
	public static Map<String, DeviceInfo> DEVICE_MAP = new HashMap<String, DeviceInfo>();
	/** job sequence **/
	public static int JOB_SEQ = 0 ;
	/** EventBus Subscriber and Subscriber list **/	
	public static List<FollowerHandler> followerListenerList = new ArrayList<FollowerHandler>();
	public static AsyncEventBus followerEventBus = new AsyncEventBus(Executors.newFixedThreadPool(10));
}

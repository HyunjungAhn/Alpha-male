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

public enum EventType {
	//input event 
	TAP("click"),
	LONG_TAP("longClick"),
	DOUBLE_TAP("doubleClick"),
	DRAG("drag"),
	SWIPE("swipe"),
	GESTURE("gesture"),
	PRESS_KEY("pressKey"),
	
	//custom - add 
	COUNT("count"),
	CLEAR_TEXT_FIELD("clearTextField"),
	SCROLL_TO_BEGINNING("scrollToBeginning"),
	SCROLL_TO_END("scrollToEnd"),
	SCROLL_PAGE("scrollPage"),
	FREEZE_RETATION("freezeRotation"),
	SET_TEXT("setText"),
	GET_TEXT("getText"),
	WAIT_FOR_EXISTS("waitForExists"),
	WAIT_FOR_IDLE("waitForIdle"),
	WAIT_UNTIL_GONE("waitUntilGone"),
	EXIST("exist"),
	
	//system
	WAKEUP("wakeUp"),
	ORIENTATION_CHANGE("setOrientation"),
	SLEEP("sleep"),
	IS_SCREEN_ON("isScreenOn"),
	SCREENSHOT("takeScreenshot"),
	OPEN_NOTIFICATION("openNotification"),
	OPEN_QUICKSETTING("openQuickSettings")
	;
	
	private final String value;
	EventType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
	
	public static EventType matchOf(String value) {
		for (EventType v : values()){
			if (v.toString().equalsIgnoreCase(value)){
				return v;
			}
		}
		throw new IllegalArgumentException();
	}
}

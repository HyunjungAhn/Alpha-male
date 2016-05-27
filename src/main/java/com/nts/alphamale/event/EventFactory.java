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

package com.nts.alphamale.event;

import com.nts.alphamale.type.EventType;

public class EventFactory {
	public static Event createEvent(EventType type){
		Event event= null;
		switch(type){
		case TAP : 
			event = new Click();
			break;
		case SWIPE :
			event = new Swipe();
			break;
		case DOUBLE_TAP:
			event = new DoubleClick();
			break;
		case DRAG:
			event = new Drag();
			break;
		case GESTURE:
			event = new Gesture();
			break;
		case LONG_TAP:
			event =  new LongClick();
			break;
		case OPEN_NOTIFICATION:
			event = new OpenNotification();
			break;
		case OPEN_QUICKSETTING:
			event = new OpenQuickSetting();
			break;
		case ORIENTATION_CHANGE:
			event = new OrientationChange();
			break;
		case PRESS_KEY:
			event = new PressKey();
			break;
		case SCROLL_PAGE:
			event = new ScrollPage();
			break;
		case SCROLL_TO_BEGINNING:
			event = new ScrollToBeginning();
			break;
		case SCROLL_TO_END:
			event = new ScrollToEnd();
			break;
		case SET_TEXT:
			event = new SetText();
			break;
		case SLEEP:
			event = new Sleep();
			break;
		case WAIT_FOR_EXISTS:
			event = new WaitForExists();
			break;
		case WAIT_FOR_IDLE:
			event = new WaitForIdle();
			break;
		case WAIT_UNTIL_GONE:
			event = new WaitUntilGone();
			break;
		case WAKEUP:
			event = new WakeUp();
			break;
		case SCREENSHOT:
			event = new ScreenShot();
			break;
		case CLEAR_TEXT_FIELD:
			event = new ClearTextField();
			break;
		//데이터 확인 및 벨리데이션 관련 이벤트들은 자동화 지원기능 
		//uiautomator에서 명령어로 제공하며, 좀 더 신중하게 접근
		default:
			break;
		}
		return event; 
	}
	
	public static Class getClass(EventType type){
		switch(type){
		case TAP : 
			return Click.class;
		case SWIPE :
			return Swipe.class;
		case DRAG : 
			return Drag.class;
		case PRESS_KEY : 
			return PressKey.class;
		case ORIENTATION_CHANGE:
			return OrientationChange.class;
		case LONG_TAP:
			return LongClick.class;
		case CLEAR_TEXT_FIELD:
			return ClearTextField.class;
		case DOUBLE_TAP:
			return DoubleClick.class;
		case GESTURE:
			return Gesture.class;
		case IS_SCREEN_ON:
		case OPEN_NOTIFICATION:
			return OpenNotification.class;
		case OPEN_QUICKSETTING:
			return OpenQuickSetting.class;
		case SCREENSHOT:
			return ScreenShot.class;
		case SCROLL_PAGE:
			return ScrollPage.class;
		case SCROLL_TO_BEGINNING:
			return ScrollToBeginning.class;
		case SCROLL_TO_END:
			return ScrollToEnd.class;
		case SET_TEXT:
			return SetText.class;
		case SLEEP:
			return Sleep.class;
		case WAIT_FOR_EXISTS:
			return WaitForExists.class;
		case WAIT_FOR_IDLE:
			return WaitForIdle.class;
		case WAIT_UNTIL_GONE:
			return WaitUntilGone.class;
		case WAKEUP:
			return WakeUp.class;
		default:
			break;
		}
		return null;
	}
}

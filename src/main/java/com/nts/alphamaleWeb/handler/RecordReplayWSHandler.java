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

package com.nts.alphamaleWeb.handler;

import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.nts.alphamale.controller.LeaderController.LeaderEventListener;
import com.nts.alphamale.data.Job;

/***
 * Event catch Websocket handler 
 * @author NAVER
 *
 */
@Service
public class RecordReplayWSHandler extends TextWebSocketHandler implements LeaderEventListener{
	WebSocketSession session ;
	
	Logger log = LogManager.getLogger(RecordReplayWSHandler.class);	
	
	/***
	 * client 로 부터 메시지 수신시점에 호출 
	 * Replay 기능시점에 
	 */
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		// ...
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("RecordReplayWSHandler connection established");
		this.session = session;
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		this.session =null;
		
	}
	

	/***
	 * 이벤트 캐치시점에, 화면단에 캐치된 이벤트 정보를 호출한다.
	 */
	@Override
	public void onCatchTouchEvent(Job job) {
		log.info("onCatchTouchEvent");
		if(this.session == null){
			log.info("session is empty");
			return;
		}
		try {
			//log.info("send message : " + job.getEventInfo().getTitle());
			TextMessage msg = new TextMessage(job.toJson());
			this.session.sendMessage(msg);
		} catch (IOException e) {
			log.error("session is empty");
		}
	}
	
	//@Override
	public void onCatchExecutionResult(String resultLogJson){
		
		if(this.session == null){
			log.debug("session is empty");
			return;
		}
		if(StringUtils.isEmpty(resultLogJson)){
			log.debug("session is empty");
			return;
		}
		try {
			TextMessage msg = new TextMessage(resultLogJson);
			this.session.sendMessage(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

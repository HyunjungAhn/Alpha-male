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

@Service
public class ExecutionLogWSHandler extends TextWebSocketHandler {
	WebSocketSession session ;
	
	Logger log = LogManager.getLogger(ExecutionLogWSHandler.class);	
	
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
		log.info("ExecutionLogWSHandler connection established");
		this.session = session;
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		this.session =null;
		
	}
	
	public void sendExecutionLogMessage(String json){
		try {
			if(this.session == null){
				log.debug("session is empty");
				return;
			}
			if(StringUtils.isEmpty(json)){
				log.debug("session is empty");
				return;
			}
			TextMessage msg = new TextMessage(json);
			this.session.sendMessage(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

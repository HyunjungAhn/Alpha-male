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

package com.nts.alphamale.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcClient.RequestListener;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.nts.alphamale.data.Job;
import com.nts.alphamale.data.Settings;
import com.nts.alphamale.event.Event;
import com.nts.alphamale.event.ScrollPage;
import com.nts.alphamale.type.EventType;


public class FollowerRpcClient {

	private JsonRpcHttpClient client;
	private String serial;
	private int port;

	Logger log = LogManager.getLogger(FollowerRpcClient.class);

	public FollowerRpcClient(String serial, int port) {
		this.serial = serial;
		this.port = port;
	}
	
	public JsonRpcHttpClient getJsonRpcHttpClient(){
		return client;
	}
	
	public void createClient() {
		try {
			client = new JsonRpcHttpClient(new URL("http://localhost:" + port + "/jsonrpc/0"));
			client.setConnectionTimeoutMillis(5000);
			client.setRequestListener(new RequestListener() {
				public void onBeforeResponseProcessed(JsonRpcClient client,ObjectNode response) {
					if(response == null){
						return;
					}
					JsonNode resultNode = response.get("result");
					if(resultNode != null && resultNode.asText() != null){
						if(!resultNode.asText().startsWith("<?xml")){
							log.info("response: "+response);
						}
					}
				}
				
				public void onBeforeRequestSent(JsonRpcClient client, ObjectNode request) {
					JsonNode resultNode = request.get("method");
					if (resultNode != null && resultNode.asText() != null) {
						if (!resultNode.asText().contains("dumpWindowHierarchy")) {
							log.info(serial + " : request: " + request);
						}
					}
				}
			});
		} catch (MalformedURLException e) {
			log.error(e.getCause() + ":" + e.getLocalizedMessage() + "[URL: "+ "http://localhost:" + port + "/jsonrpc/0]");
		}
	}
	
	public void setWaitForSelectorTimeout(){
		client.setConnectionTimeoutMillis(2000);
		client.setReadTimeoutMillis((int)(Settings.FIND_ELEMENT_TIMEOUT*2));
		Map<String, Object> parmas = new HashMap<String, Object>();
		parmas.put("waitForSelectorTimeout", Settings.FIND_ELEMENT_TIMEOUT);
		parmas.put("waitForIdleTimeout", Settings.WAIT_FOR_IDLE_TIMEOUT);
		try {
			client.invoke("setConfigurator", new Object[]{parmas}, Object.class);
		} catch (Throwable e) {
			log.error(e.getCause() + ":" + e.getLocalizedMessage() + "[set timeout]");
		}
	}
	
	public String ping(){
		try {
			return client.invoke("ping", new Object[] {}, String.class);
		} catch (Throwable e) {
			log.error(e.getCause() + ":" + e.getLocalizedMessage() + "[ping]");
		}
		return "";
	}
	
	
	public Object invoke(String methodName, Object[] params) throws Throwable{
		Map<String,String> header = new HashMap<String, String>();
		header.put("Content-type", "application/json; charset=utf-8");
		return client.invoke(methodName, params, Object.class,header);
	}
	
	public Object invoke(Job job) throws Throwable{
		Event event = job.getEventInfo();
		EventType evtTpCd = event.getType();
		
		client.invoke("waitForIdle", new Object[]{4000}, Object.class);
		
		Object result = null;
		switch(evtTpCd){
			case LONG_TAP:
				result = client.invoke("drag",event.getParamsInfo(), Object.class);
				break;
			case DOUBLE_TAP : 
				Map<String, Object> parmas = new HashMap<String, Object>();
				parmas.put("actionAcknowledgmentTimeout",40);
				try {
					client.invoke("setConfigurator", new Object[]{parmas}, Object.class);
					client.invoke("click", event.getParamsInfo(), Object.class);
					client.invoke("click", event.getParamsInfo(), Object.class);
				} catch (Throwable e) {
					throw e	;
				} finally{
				 	parmas.put("actionAcknowledgmentTimeout",3000);
				 	result = client.invoke("setConfigurator", new Object[]{parmas}, Object.class);
				}
				break;
			case SCROLL_PAGE:
				ScrollPage spEvt = (ScrollPage)event;
				for(int i=0; i < spEvt.getPage() ;i++){
					client.invoke("swipe", spEvt.getParamsInfo(), Object.class);
				}
				break;
			default : 
				result =  client.invoke(
						event.getType().toString(), 
						event.getParamsInfo(),
						Object.class);

		}
		return result;
	}
	
	
	public boolean waitForWindowUpdate(int timeout){
		try {
			return (Boolean) client.invoke("waitForWindowUpdate", new Object[]{null,timeout*1000}, Object.class);
		} catch (Throwable e) {
			log.error("[FC] " + serial + " | "+e.getMessage());
		}
		return false;
	}
	
	public void serverStop(){
		try {
			client = new JsonRpcHttpClient(new URL("http://localhost:"+ port +"/stop"));
		} catch (MalformedURLException e) {
			log.error("[FC] " + port + " | "+e.getMessage());
		}
	}
	
}
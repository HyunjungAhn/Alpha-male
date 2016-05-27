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

package com.nts.alphamaleWeb.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.nts.alphamale.controller.AlphaMaleController;
import com.nts.alphamale.data.DataQueue;
import com.nts.alphamale.data.DeviceInfo;
import com.nts.alphamale.data.Job;
import com.nts.alphamale.event.Event;
import com.nts.alphamale.event.EventFactory;
import com.nts.alphamale.handler.ExecutorManager;
import com.nts.alphamale.type.EventType;
import com.nts.alphamaleWeb.msg.ResultBody;
import com.nts.alphamaleWeb.msg.ScanDeviceResultVO;
import com.nts.alphamaleWeb.type.Code;

@Controller
@EnableAutoConfiguration
public class ServiceController {
	
	Logger log = LogManager.getLogger(ServiceController.class);	
	
	@Autowired
	private ExecutorManager executorManager;
	
	@Autowired
	private AlphaMaleController alphamaleController;
	
	@RequestMapping("/hello")
	public ModelAndView showMessage(
			@RequestParam(value = "name", required = false, defaultValue = "World") String name) {
		System.out.println("in controller");
		ModelAndView mv = new ModelAndView("index");
		mv.addObject("message", "test"); 
		mv.addObject("name", name);
		return mv;
	}
	
	/***
	 * alpha-male 
	 * @return
	 */
	@RequestMapping("/alphamale")
	public ModelAndView alphamale(){
		ModelAndView mav = new ModelAndView("alphamale");
		if(DataQueue.IS_CONTROLED){
			mav.addObject("duplicate", true);
		}else{
			List<String> evtCmdList = alphamaleController.getKeywordList();
			mav.addObject("duplicate", false);
			mav.addObject("evtCmdList",evtCmdList);
		}
		return mav;
	}
	
	@RequestMapping("/getTemplate")
	@ResponseBody
	public String getTemplate(
			@RequestParam(required=true) String evtName)
	{
		String template="";
		EventType evtType = EventType.matchOf(evtName);
		if(evtType != null){
			Event event = EventFactory.createEvent(evtType);
			template = event.getTemplate();
		}
		return template;
	}
	
	@RequestMapping("/exportJobs")
	@ResponseBody
	public String exportJobs(@RequestParam(required=true) String jobJsons){
		ResultBody<Boolean> result = new ResultBody<Boolean>();
		try{
			String fileName = "export_" + System.currentTimeMillis() +".txt";
	        File file = new File(fileName) ;
	        FileWriter fw = new FileWriter(file, false) ;
	        fw.write(jobJsons);
	        fw.flush();
	        fw.close();
	        result.setCode(Code.OK, true);	
		}catch(Exception e){
			e.printStackTrace();
			result.setCode(Code.F100);
		}
		return result.toJson();
	}
	
	@RequestMapping(value="/importJobs", method = RequestMethod.POST)
	@ResponseBody
	public String importJobs(MultipartHttpServletRequest request, HttpServletResponse response) {
		
		ResultBody<String> result = new ResultBody<String>();
		Iterator<String> itr =  request.getFileNames();
        MultipartFile mpf = null;
 
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        while(itr.hasNext()){
            mpf = request.getFile(itr.next()); 
            try {
            	String json = new String(mpf.getBytes());
            	JsonArray arr = gson.fromJson(json, JsonArray.class);
            	result.setCode(Code.OK, gson.toJson(arr));
            } catch (IOException e) {
            	result.setCode(Code.F200);
            }
         }
        return result.toJson();
	}
	
	@RequestMapping("/createEvt")
	@ResponseBody
	public String createEvt(@RequestParam(required=true) String jobJson){
		Job job = new Job("msg", jobJson);
		return job.toJson();
	}
	
	@RequestMapping("/isControll")
	@ResponseBody
	public Boolean isControll(){
		return DataQueue.IS_CONTROLED;
	}
	
	@RequestMapping("/updateEvt")
	@ResponseBody
	public String updateEvt(@RequestParam(required=true) String jobJson){
		Job job = new Job(jobJson);
		return job.toJson();
	}
	
	@RequestMapping("/replay")
	@ResponseBody
	public String replay(@RequestParam(required=true) String jobJsons){
		String result = "{\"result\" : \"success\"}";
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<Job> jobs = new ArrayList<Job>();
		
		JsonArray array = gson.fromJson(jobJsons, JsonArray.class);
		for(JsonElement json : array){
			Job job = new Job(json.getAsString());
			jobs.add(job);
		}
		
		executorManager.replay(jobs);
		return result;
	}
	
	@RequestMapping("/autoModeToggle")
	@ResponseBody
	public Boolean autoModeToggle(){
		if(DataQueue.IS_AUTO_MODE == true){
			DataQueue.IS_AUTO_MODE = false;
		}else if(DataQueue.IS_AUTO_MODE == false){
			DataQueue.IS_AUTO_MODE = true;
		}
		DataQueue.DOCUMENT_QUEUE.clear();
		return DataQueue.IS_AUTO_MODE;
	}
	
	
	@RequestMapping("/startRecord")
	@ResponseBody
	public String startControl(
			@RequestParam(required=false) String leader,
			@RequestParam(value="followers[]", required=false) List<String> followers){
		
		String json ="success";
		executorManager.startControl(leader, followers);
		return json;
	}
	
	
	@RequestMapping("/stopRecord")
	@ResponseBody
	public String stopRecord(){
		if(DataQueue.IS_CONTROLED){
			executorManager.stopControl();
		}
		return "success";
	}
	
	@RequestMapping("/getDevices")
	@ResponseBody
	public String getDevices(){
		List<String> devices = alphamaleController.scanDevices();
		
		List<ScanDeviceResultVO> resultList = new ArrayList<ScanDeviceResultVO>();
		for(String serial:devices){
			DeviceInfo dInfo = DataQueue.DEVICE_MAP.get(serial);
			String title = serial = "[" + serial + "] " + dInfo.getModel() + ", " + dInfo.getBuild();
			resultList.add(new ScanDeviceResultVO(title, dInfo));
		}

		ResultBody<List<ScanDeviceResultVO>> result = new ResultBody<List<ScanDeviceResultVO>>();
		if(CollectionUtils.isEmpty(resultList)){
			result.setCode(Code.D100);	
		}else{
			result.setCode(Code.OK, resultList);
		}
		return result.toJson();
	}
	
	/****
	 * Utils
	 * @return
	 */
	@RequestMapping("/restartADB")
	@ResponseBody
	public String restartADB(){
		alphamaleController.restartAdb();
		return "success";
	}
	
	private List<String> getAsDeivces(String json){
		List<String> devices = new ArrayList<String>();
		Gson gson = new Gson();
		JsonArray jarr = gson.fromJson(json, JsonArray.class);
		for(JsonElement serail : jarr){
			devices.add(serail.getAsString());			
		}
		return devices;
	}
	
	private List<String> getDeivceListFromString(String arr){
		List<String> devices = new ArrayList<String>();
		if(StringUtils.isNotEmpty(arr)){
			String[] sArr= arr.split(",");
			 devices = Arrays.asList(sArr);
		}
		return devices;
	}
	
	@RequestMapping("/unLockDevice")
	@ResponseBody
	public String unLockDevice(String devJson){
		List<String> devices = getAsDeivces(devJson);
		alphamaleController.unlockDevices(devices);
		return "success";
	}
	
	@RequestMapping(value="/installApp" , method = RequestMethod.POST)
	@ResponseBody
	public String installApp(
			@RequestParam String devJson,
			@RequestPart("file_apk") MultipartFile mpf){
		
		ResultBody<String> result = new ResultBody<String>();
		List<String> devices = getDeivceListFromString(devJson);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String path = mpf.getOriginalFilename();
        try {
        	if(mpf.isEmpty()){
        		result.setCode(Code.F400);
        	}else{	
        		File file = new File(path);
            	mpf.transferTo(new File(file.getAbsolutePath()));
            	String results = alphamaleController.installApp(devices, true,file.getAbsolutePath() );
        		String resultJson = gson.toJson(results);
        		result.setCode(Code.OK, resultJson);		
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        	result.setCode(Code.F400);
        }
		
		return result.toJson();
	}
	
	@RequestMapping("/uninstallApp")
	@ResponseBody
	public List<String> uninstallApp(String devJson, String pkgName){
		List<String> devices = getDeivceListFromString(devJson);
		return  alphamaleController.uninstallApp(devices, false, pkgName);
	}
	
	@RequestMapping("/uninstallAppKeppData")
	@ResponseBody
	public List<String> uninstallAppKeppData(String devJson, String pkgName){
		List<String> devices = getDeivceListFromString(devJson);
		return alphamaleController.uninstallApp(devices, true, pkgName);
	}
	
	@RequestMapping("/startApp")
	@ResponseBody
	public String startApp(String leader, String devJson, String pkgName){
		List<String> devices = getDeivceListFromString(devJson);
		return alphamaleController.startActivity(leader, devices, pkgName);
	}
	
	@RequestMapping("/getPackageList")
	@ResponseBody
	public String getPackageList(String leader){
		List<String> pkgList = alphamaleController.getPkgList(leader);
		Gson gson = new Gson();
		String result =  gson.toJson(pkgList);
		return result;
	}
	
	@RequestMapping("/clearAppData")
	@ResponseBody
	public String clearAppData(String devJson, String pkgName){
		List<String> devices = getDeivceListFromString(devJson);
		return alphamaleController.clearAppData(devices, pkgName);
	}
}
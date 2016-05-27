package com.nts.alphamaleWeb.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nts.alphamaleWeb.msg.ResultBody;
import com.nts.alphamaleWeb.msg.ScanDeviceResultVO;
import com.nts.alphamaleWeb.type.Code;

public class TestResultMsg {
	
	static Logger log = LogManager.getLogger(TestResultMsg.class);
	
	@Test
	public void testResultType2(){
		/*ScanDeviceResultVO vo = new ScanDeviceResultVO();
		vo.leaderDevice= "l00";
		
		List<String> fs = new ArrayList<>();
		fs.add("f01");
		fs.add("f02");
		fs.add("f03");
		fs.add("f04");
		vo.followerDevices = fs;
		
		ResultBody<ScanDeviceResultVO> re = new ResultBody<>();
		re.setCode(Code.D200, vo);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String resultJson = gson.toJson(re);
		
		log.info(resultJson);
		*/
	}
	

	@Test
	public void testResultType3(){
		ResultBody<ScanDeviceResultVO> re = new ResultBody<ScanDeviceResultVO>();
		re.setCode(Code.D100);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String resultJson = gson.toJson(re);
		
		log.info(resultJson);
		
	}
	
	@Test
	public void testResultType4(){
		ResultBody<Object> re = new ResultBody<Object>();
		re.setCode(Code.D100);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String resultJson = gson.toJson(re);
		
		log.info(resultJson);
		
	}
}

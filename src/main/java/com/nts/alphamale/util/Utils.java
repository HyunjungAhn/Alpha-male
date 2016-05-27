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

package com.nts.alphamale.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import com.nts.alphamale.data.Point;
import com.nts.alphamale.type.DirectionType;

/***
 * Utils class 
 *
 */
public class Utils {
	
	public static final String ANDROID_HOME = System.getenv("ANDROID_HOME");

	/***
	 * adb.exe의 File object 조회  
	 * @return
	 */
	public static File adb(){
		return new File(platformToolsHome(), "adb" + platformExecutableSuffixExe());
	}
	
	/***
	 * adb 절대 경로 조회
	 * @return
	 */
	public static String adbPath(){
		return platformToolsHome()+"/adb" + platformExecutableSuffixExe();
	}

	/***
	 * adb home의 위치 File Object로 조회
	 * @return
	 */
	public static File platformToolsHome(){	
		StringBuffer command = new StringBuffer();
		if(ANDROID_HOME!=null && !ANDROID_HOME.isEmpty())
			command.append(ANDROID_HOME);
		else
			command.append(System.getProperty("ANDROID_HOME"));
		command.append(File.separator);
		command.append("platform-tools");
		command.append(File.separator);
		return new File(command.toString());
	}

	/***
	 * window os 여부 확인
	 * @return
	 */
	public static boolean isWindows(){
		return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
	}

	/***
	 * 플랫폼에 맞는 실행파일 확장자 조회 
	 * @return
	 */
	public static String platformExecutableSuffixExe(){
		return isWindows() ? ".exe" : "";
	}
	
	
	/***
	 * 숫자로된 orientation 정보를 DirectoryType으로 변환
	 * @param direction
	 * @return
	 */
	public static DirectionType getRotation(int direction){
		switch(direction){
		case 0:
			return DirectionType.NATURAL;
		case 1:
			return DirectionType.LEFT;
		case 3:
			return DirectionType.RIGHT;
		}
		return DirectionType.NATURAL;
	}
	
	/***
	 * String 타입의 좌표 정보를 Point 타입의 좌표정보로 변환
	 * @param pointStr
	 * @return
	 */
	public static Point stringToPoint(String pointStr){
		if(pointStr!=null && pointStr.contains(",")){
			return new Point(Integer.valueOf(StringUtils.split(pointStr, ",")[0]),Integer.valueOf(StringUtils.split(pointStr, ",")[1]));
		}
		return null;
	}
	
	/***
	 * Point 타입의 좌표정보를 String 타입의 좌표 정보로 변환
	 * @param point
	 * @return
	 */
	public static String pointToStr(Point point){
		if(point!=null) return point.x+","+point.y;
		return null;
	}
	
	/***
	 * command 결과를  lineIterator로 변환
	 * @param li
	 * @return
	 */
	public static LineIterator getLineIterator(Object li){
		return (LineIterator) li;
	}
	
	/***
	 * 해당 executor를 종료 시킨다.
	 * @param executor
	 */
	public static void destoryExecutor(Object executor){
		DefaultExecutor exec = (DefaultExecutor) executor;
		exec.getWatchdog().destroyProcess();
	}
	
	/***
	 * 원본 화면정보와 대상의 가로 세로 벙보를 바탕으로 비율 계산 
	 * @param screenSize : 원본 화면사이즈 (ex : 720X1280)
	 * @param width : 대상단말 화면 가로 
	 * @param height : 대상단말 화면 세로 
	 * @return
	 */
	public static double[] getScreenRatio(String screenSize, int width, int height){
		if(screenSize!=null){
			if(screenSize.contains("x")) screenSize = screenSize.replace("x", ",");
			if(screenSize.contains("X")) screenSize = screenSize.replace("X", ",");
			
			double ratioX = Utils.getRatio(Integer.parseInt(StringUtils.split(screenSize, ",")[0]), width);
			double ratioY = Utils.getRatio(Integer.parseInt(StringUtils.split(screenSize, ",")[1]), height);
			return new double[]{ratioX,ratioY}; 
		}
				
		return new double[]{1.0,1.0};
	}
	
	public static double[] getScreenRatio(int pWidth, int pHeight, int nWidth, int nHeight){
		double ratioX = Utils.getRatio(pWidth, nWidth);
		double ratioY = Utils.getRatio(pHeight, nHeight);
		return new double[]{ratioX,ratioY}; 
	}
	
	
	/***
	 * 길이의 비율 계산 결과 조회 
	 * @param orgLength : 원본 
	 * @param dstLength : 비교대상
	 * @return
	 */
	public static double getRatio(int orgLength, int dstLength){
		return ((double)dstLength/(double)orgLength);
	}
	
	/***
	 * orientation에 따른 좌표 변환
	 * @param orientaion : 세로/가로 모드 
	 * @param xy : 원본좌표 
	 * @param width : 화면가로 
	 * @param height : 화면세로 
	 * @return
	 */
	public static  Point rotaionPoint(int orientaion, Point xy, int width, int height){
		int x = 0;
		int y = 0;
		switch(orientaion){
		case 1:
			x = xy.y;
			y = width-xy.x;
			return new Point(x,y);
		case 3:
			x = height-xy.y;
			y = xy.x;
			return new Point(x,y);
		default:
			return xy;
		}
	}
	
	/***
	 * 리더 단말의 좌표를 팔로워 단말의 좌표로 변환 
	 * @param leaderWidth : 리더단말 화면 가로 
	 * @param leaderHeight : 리더단말 화면 세로 
	 * @param point : 좌표 
	 * @param followerWidth : 팔로워단말 화면 가로 
	 * @param followerHeight : 팔로워 단말 화면 세로 
	 * @param xScale : x 스케일 
	 * @param yScale : y 스케일 
	 * @return
	 */
	public static Point convertPoint(int leaderWidth , int leaderHeight, Point point, int followerWidth, int followerHeight, double xScale, double yScale){
		String screenSize = String.valueOf(leaderWidth) +"X" + String.valueOf(leaderHeight);
		double[] ratioXY = getScreenRatio(screenSize, followerWidth, followerHeight);
		int x = (int)((point.x )*ratioXY[0]);
		int y = (int)((point.y)*ratioXY[1]);
		return new Point(x, y);	
	}
	
	public static  String convertPoint(String leaderScreenSize, String pointStr, int width, int height){
		double[] ratioXY = getScreenRatio(leaderScreenSize, width, height);
		Point xy = Utils.stringToPoint(pointStr);
		return (int)(xy.x*ratioXY[0])+","+(int)(xy.y*ratioXY[1]);
	}
	
	public static String[] objectArrayToStringArray(Object[] objArr){
		String[] rtn = new String[objArr.length];
		for(int i=0, len=objArr.length;i<len;i++){
			if(objArr[i]!=null)
				rtn[i] = objArr[i].toString().trim();
			else
				rtn[i] = "";
		}
		return rtn;
	}
	
	/***
	 * Point 타입의 좌표정보를  Map타입의 좌표정보로 변환 
	 * @param point
	 * @return
	 */
	public static Map<String, Integer> pointToMap(Point point){
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("x", point.x);
		map.put("y", point.y);
		return map;
	}
	
	/***
	 * String 타입의 좌표 정보를 Map타입의 좌표 정보로 변환
	 * @param pointStr
	 * @return
	 */
	public static Map<String, Integer> pointToMap(String pointStr){
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("x", Integer.valueOf(StringUtils.split(pointStr, ",")[0]));
		map.put("y", Integer.valueOf(StringUtils.split(pointStr, ",")[1]));
		return map;
	}
	
	/**
	 * @param regex 비교할 패턴
	 * @param input 비교 대상 문자열
	 * @return 패턴과 매칭되는 첫번째 문자열
	 */
	public static String convertRegex(String regex, String input, int index){
		String rtnValue = "";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		if(m.find()){
			return m.group(index); 
		}
		return rtnValue;
	}
	
	/**
	 * getevent로 식별된 좌표값에 Scaling factor를 적용하여 변환 함
	 * @param getevent로 입력받은 좌표값
	 * @return Scaling factor로 변환된 좌표값
	 */
	public static Point scalePoint(Point original, double xScale, double yScale){
		int scaleX = Math.round((float)(original.getX() * xScale));
		int scaleY = Math.round((float)(original.getY() * yScale));
        return new Point(scaleX, scaleY);
    }
	
	/***
	 * leader 단말 replay를 위해, scaled point 를 원복
	 * @param original
	 * @param xScale
	 * @param yScale
	 * @return
	 */
	public static Point scaleReversePoint(Point original, double xScale, double yScale){
		int scaleX = Math.round((float)(original.getX() / xScale));
		int scaleY = Math.round((float)(original.getY() / yScale));
        return new Point(scaleX, scaleY);
    }
	
	/**
	 * directory 생성
	 * @param 생성할 dir
	 * @return 폴더가 존재하면 true, 없는 경우 폴더를 생성한 결과
	 */
	public static boolean makeDir(String dir){
		if(new File(dir).exists() && new File(dir).isDirectory())	return true;
		else return new File(dir).mkdir();
	}
	
	
	public static List<Integer> covertBoundsToStringArray(String bounds){
		Pattern p = Pattern.compile("\\[(.*?)\\]");
		Matcher m = p.matcher(bounds);
		
		List<Integer> boundsInfo = new ArrayList<Integer>();
		m.find();
		boundsInfo.add(Integer.valueOf(m.group(1).split(",")[0]));
		boundsInfo.add(Integer.valueOf(m.group(1).split(",")[1]));
		m.find();
		boundsInfo.add(Integer.valueOf(m.group(1).split(",")[0]));
		boundsInfo.add(Integer.valueOf(m.group(1).split(",")[1]));
		return boundsInfo;
	}
	
	
}

<%@ page language="java" contentType="text/html; charset=utf8"
	pageEncoding="utf8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<title>Alpha-male</title>
<link rel="stylesheet" href="./css/alphamale.css">
<link rel="stylesheet" href="./css/jquery-ui.structure.min.css">
<link rel="stylesheet" href="./css/jquery-ui.theme.min.css">
<link rel="stylesheet" href="./css/waitMe.min.css">
<script src="./js/jquery-2.2.1.min.js"></script>
<script src="./js/jquery-ui.min.js"></script>
<script src="./js/jquery.fileupload.js"></script>
<script src="./js/alphamale.js"></script>
<script src="./js/waitMe.min.js"></script>

</head>
<body>
    <header id="header">
			<div class="innertube">
				<h1>AlphaMale WEB</h1>
			</div>
	</header>
	<div id="wrapper">
		<main>
		<div id="content">
		<div class="innertube">
		<div id="container">
			<div style="float:right;">
		       <button id="btnStartRecord" style="height:50px">START</button>
				<button id="btnStopRecord" style="height:50px">STOP</button>
				<button id="btnReplay" style="height:50px">Replay</button>
			</div>
		    <div style=""><h1 style="font-size:14px">Mode : </h1> 
		    <button id="btnAutoModeToogle">Auto Mode</button>
		    </div>
			
			<!--  devices area -->
			<hr>
			<p style="font-size:12px;font-color:blue;">
			※  Leader 단말은 안드로이드 4.4 이상의 삼성 단말을 추천합니다. (5.0 버전은 Leader 지정 불가) <br>
			※  Android 4.4 or higher version Samsung devices are recommended as a Leader device. (Android 5.0 is not available)
			</p> 
			<!-- <p style="margin:0px;"><u>Device Infomation : </u></p> -->
			<div id="device_lst_area">
			    <table>
			    <tr>
			    <td rowspan="2" style="width:380px"> 
				<div id="scan_device">
					<h1 class="" style="font-size:15px">Scan : <button id="btnScanDevice">Scan Devices</button></h1>
					<div class="ui-widget-content" style="height:152px; width:355px">
						<ul class="selectable" id="scaned_device_list">
						</ul>
					</div>
				</div>
				</td>
				<td valign="bottom" style="width:20px"><button id="btnSelLeader">▶</button></td>
				<td>
				<div id="sel_leader" style="width:380px">
					<h1 class="" style="font-size:14px">Leader</h1>
					<div class="ui-widget-content">
						<ul>
						</ul>
					</div>
				</div>
				</td>
				</tr>
				<tr>
				<td style="width:20px"><button id="btnSelFollowers">▶</button></td>
				<td>
				<div id="sel_follower" style="width:380px">
					<h1 class="" style="font-size:14px">Followers</h1>
					<div class="ui-widget-content">
						<ul>
						</ul>
					</div>
				</div>
				</td>
				</tr></table>
			</div>
			<!-- event area -->
			<hr>
			<p style="font-size:12px;font-color:blue;">
			※  이벤트 순서 변경 : Drag & Drop 으로 이벤트 순서를 변경할 수 있습니다. (다중 선택 후 ">" 기호 드래그) <br>
			※  Event Order : You can change the event order by dragging and dropping. (select event(s) and drag ">" symbol)
			</p> 
			<!-- <p style="margin:0px;"><u>Event Infomation </u> </p> -->
			<table>
				<tr style="background:#c0c0c0;height:30px">
					<th style="font-size:14px; align :left; padding-top:15px;">Event List 
						<input type="image" id="btnImportJobs" src="/images/ic_import.png" style="float: left;" title="Import events">
								<input type="image" id="btnExportJobs" src="/images/ic_export.png" style="float: left;" title="Export events" />
								<input type="image" id="btnEventListClear" src="/images/ic_clear_all.png" style="float: right; background: #c0c0c0;" title="Delete All event"> 
								<input type="image" id="btnDeleteEvent" src="/images/ic_delete_event.png" style="float: right; background: #c0c0c0;" title="Delete event" />
								<input type="image" id="btnAddEvent" src="/images/ic_add_event.png" style="float: right; background: #c0c0c0;" title="Add events" />
					</th>
					<th style="font-size:14px;">Event Detail</th>
				</tr>
				<tr>
					<td>
						<div id="event_list_area">
							<ul id="event_list" class="selectable-scrollable">
							</ul>
						</div>
					</td>
					<td>
						<div id="event_detail_area">
							<div id="evtListArea" style="display:none;">EVENT : 
								      <select name="evtList" id="evtList" style="width:200px;">
								      <option value="">event list</option>
									   	<c:forEach var="evt" items="${evtCmdList}">
											<option value="${evt}">${evt}</option>
										</c:forEach>
									   </select>
							</div>
							<table id="evtDetailInfoTable">
								<tr><td></td></tr>
							</table>
							<table id="evtDetailTemplateTable">
							</table>
							<div id="evtDetailBtnArea">
								<input type="button" id="btnSaveEventDetail" value="Save Event" style="display:none;"></input>
								<input type="button" id="btnCreateEvent" value="Create Event" style="display:none;"></input>
								<input type="hidden" id="tmplJson" value=""/>
							</div>
						</div>
					</td>
		   </table>
		   <hr>
			<!-- <p style="margin:0px;"><u>Log Infomation </u></p> -->
			<table>
				<tr style="background:#e6e6fa;height:30px">
					<th style="font-size:15px;">Log List</th>
					<th style="font-size:15px;">Log Detail</th>
				</tr>
				<tr>
				<td>
					<div id="log_list_area">
						<ul class="selectable" id="log_list">
						</ul>
					</div>
				</td>
				<td>
					<div id="log_detail_area">
						<table id="log_detail_list">
						</table>
					</div>
				</td>
			</table>
		    <input  type="file" id="file" name="file" data-url="/importJobs">
		    <input  type="file" id="file_apk" name="file_apk" data-url="/installApp">
		</div>
		</div>
		</div>
		</main>
		
		<nav id="nav">
				<div class="innertube">
					<h3>AlphaMale</h3>
					<ul>
						<li><a href="#">Record&Replay</a></li>
					</ul>
					<h3>Uitls</h3>
					<ul>
						<li><a href="javascript:void(0);" id="btnRestartAdb">Restart ADB</a></li>
						<li><a href="javascript:void(0);" id="btnUnlockDevices">Unlock Deivces</a></li>
						<li><a href="javascript:void(0);" id="btnInstallApp">Install APP</a></li>
						<li><a href="javascript:void(0);" id="btnUnInstallApp">UnInstall APP</a></li>
						<li><a href="javascript:void(0);" id="btnUnInstallAppKeepData">UnInstall APP(Keep Data)</a></li>
						<li><a href="javascript:void(0);" id="btnStartApp">Start APP</a></li>
						<li><a href="javascript:void(0);" id="btnClearAppData">Clear App Data</a></li>
					</ul>
				</div>
			</nav>
	</div>
	<footer id="footer">
			<div class="innertube">
				<p></p>
			</div>
	</footer>
	<div id="dialog-confirm" title="Select PackageList">
	  <p>
	  	<select id="pkgList">
	  	</select>
	  	</p>
	</div>
	<div id="noti-confirm" title="Confiem">
	  <p id="noti_content">  </p>
	</div>

</body>
<script type="text/javascript">
	var main = nts.alphamale.main;
	main.init();
	
</script>
</html>
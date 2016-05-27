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

/**
 * alphamale tab javascript literal
 * @name hwa chang deuk
 */

if (typeof (nts) === 'undefined') {nts = {};};
if (typeof (nts.alphamale) === 'undefined') {nts.alphamale = {};};
if (typeof (nts.alphamale.main) === 'undefined') {nts.alphamale.main = {};};
nts.alphamale.main = {
		//variable
		host : "localhost:8080",
		_rooltEl : null,
		_containerEl : null,
		_webSocket : null,
		_isRunning : false,
		_deivceList : null,
		_selectedEvtLi : null,
		_isControll : false,

		//ajax configure data 
		_data : {
			//ajax 
			ajStartRecord : {
				url : '/startRecord',
				time : 3000,
				dataType : 'text',
				type : 'GET',
				success : 'handleAtferStartRecord',
				params : ['leader','followers'],
			},
			ajStopRecord : {
				url : '/stopRecord',
				time : 3000,
				dataType : 'text',
				type : 'GET',
			},
			ajReplay : {
				url : '/replay',
				time : 3000,
				dataType : 'json',
				type : 'POST',
				success : 'handleAtferReplay',
				params : ['jobJsons'],
			},

			ajUpdateEvt : {
				url : '/updateEvt',
				time : 3000,
				dataType : 'json',
				type : 'POST',
				success : 'handleAtferUpdateEvt',
				params : ['jobJson'],
			},

			ajIsControll : {
				url : '/isControll',
				time : 1000,
				dataType : 'text',
				type : 'GET',
				success : 'handleAtferIsControll',
			}, 

			ajImportJobs : {
				url : '/importJobs',
				time : 3000,
				dataType : 'json',
				type : 'POST',
				success : 'handleAtferImportJob',
				params : ['filePath']
			},

			ajExportJobs : {
				url : '/exportJobs',
				time : 3000,
				dataType : 'json',
				type : 'POST',
				success : 'handleAtferExportJob',
				params : ['jobJsons']
			},

			ajCreateEvt : {
				url : '/createEvt',
				time : 3000,
				dataType : 'json',
				type : 'POST',
				success : 'handleAtferCreateEvt',
				params : ['jobJson'],
			},

			ajAutoModeToggle : {
				url : '/autoModeToggle',
				time : 1000,
				dataType : 'text',
				type : 'GET',
				success : 'handleAutoModeToggle',
			},

			ajScanDevices : {
				url : '/getDevices',
				time : 3000,
				dataType : 'json',
				type : 'GET',
				success : 'handleAtferScanDevices',
			},

			ajRestartADB : {
				url : '/restartADB',
				time : 60000,
				dataType:'text',
				type : 'GET',
				success : 'handleAfterRestartADB',
			},

			ajUnlockDevice : {
				url : '/unLockDevice',
				time : 60000,
				dataType:'text',
				type : 'POST',
				success : 'handleAtferRequestComplete',
				params : ['devJson']
			},

			ajPackageList : {
				url : '/getPackageList',
				time : 60000,
				dataType:'json',
				type : 'GET',
				success : 'handleAtferGetPackageList',
				params : ['leader']
			},

			ajStartApp : {
				url : '/startApp',
				time : 3000,
				dataType:'text',
				type : 'POST',
				success : 'handleAtferRequestComplete',
				params : ['leader', 'devJson', 'pkgName']
			},

			ajUninstallApp : { 
				url : '/uninstallApp',
				time : 3000,
				dataType:'text',
				type : 'POST',
				success : 'handleAtferRequestComplete',
				params : ['devJson', 'pkgName']

			},

			ajUninstallAppKeppData : {
				url : '/uninstallAppKeppData',
				time : 3000,
				dataType:'text',
				type : 'POST',
				success : 'handleAtferRequestComplete',
				params : ['devJson', 'pkgName']
			},

			aJclearAppData : {
				url : '/clearAppData',
				time : 3000,
				dataType:'text',
				type : 'POST',
				success : 'handleAtferRequestComplete',
				params : ['devJson', 'pkgName']
			},

			ajTemplate : {
				url : '/getTemplate',
				time : 3000,
				dataType : 'json',
				type : 'GET',
				success : 'handleAtferGetTemplate',
				params : ['evtName']
			},

			//websocket 
			wsEventMonitor : {
				url : '/getEvent',
				message : 'handleAfterEventCatched',
			},
			wsExecitionLogMonitor : {
				url : '/getExecutionLog',
				open : null, 
				close :  null, 
				error :  null , 
				message : 'handleAfterExecutionLogCatched'
			}
		},

		// init
		init : function() {
			_rooltEl = $("body");
			_containerEl = $("#contriner");
			_deivceList = $(".scaned_device_list");
			this.eventBind();

			this._ajaxFunc(this._data.ajIsControll);

			this._showLoadingDiv();

			// test code : visible 속성
			$("#btnStartRecord").show();
			$("#btnStopRecord").hide();

			$(".selectable").selectable();
			/*
			 * $("#scaned_device_list").draggable({ cancel: "a.delete", // clicking
			 * an icon won't initiate dragging revert: "invalid", // when not
			 * dropped, the item will revert back to its initial position
			 * containment: "document", helper: "clone", cursor: "move" });
			 */
			////////////////////////////////////////////
			$("#sel_leader ul").droppable({
				activeClass : "ui-state-default",
				hoverClass : "ui-state-hover",
				accept : ":not(.ui-sortable-helper)",
				drop : function(event, ui) {
					$(this).find(".placeholder").remove();

					if(!$(this).children() || $(this).children().length > 0){
						alert("리더 단말은 한대만 등록 가능합니다");
						return;
					}  
					$(ui.draggable).find(".delete").show();
					$("<li class='ui-widget-content'></li>").html(ui.draggable.html()).appendTo(this);
					$(ui.draggable).remove();
				}
			});

			$("#sel_follower ul").droppable({
				activeClass : "ui-state-default",
				hoverClass : "ui-state-hover",
				accept : ":not(.ui-sortable-helper)",
				drop : function(event, ui) {
					$(this).find(".placeholder").remove();
					$(ui.draggable).find(".delete").show();
					$("<li class='ui-widget-content'></li>").html(ui.draggable.html()).appendTo(this);
					$(ui.draggable).remove();
				}
			});
			////////////////////////////////////////////

			$('#event_list').selectable({
				filter : 'li',
				cancel: '.sort-handle',
				selecting: function( event, ui ) {
					/*  var y = event.originalEvent.clientY; (293)
				  var h = $("#event_list_area").height(); (300)
				  if(h-20 < y ){
					  $("#event_list_area").scrollTop(y + 20);
				  }*/
				}
			}).sortable({
				axis: "y",
				items: "> li",
				handle: '.sort-handle',
				helper: function(e, item) {
					if (!item.hasClass('ui-selected')) {
						item.parent().children('.ui-selected').removeClass('ui-selected');
						item.addClass('ui-selected');
					}
					var selected = item.parent().children('.ui-selected').clone();
					item.data('multidrag', selected).siblings('.ui-selected').remove();
					return $('<li/>').append(selected);
				},
				stop: function(e, ui) {
					var selected = ui.item.data('multidrag');
					ui.item.after(selected);
					ui.item.remove();
				},
				update: function() {
					var order = $("#list").sortable('serialize');
					console.log(order);
				}
			});

			this.buttonStateChange();

			$('#file').fileupload({
				dataType: 'json',
				done: function (e, data) {
					var resultString = JSON.stringify(data.result)
					var resJson = $.parseJSON(resultString);
					if(resJson.code !='OK'){
						alert("파일 Import에 실패했습니다");
					}else{
						var jsonArray = $.parseJSON(resJson.payload);
						$.each( jsonArray, function( i, val ) {
							var itemJson = $.parseJSON(val);
							var addItem = ['<li class="ui-widget-content" id="evt_', i ,'">',
							               '<span class="sort-handle">&gt;</span>',itemJson.title,
							               '<input type="hidden" value=\'' , val,'\'/>',
							               '</li>'].join('');
							$("#event_list").append(addItem);
						});	
					}
				},

				progressall: function (e, data) {
					var progress = parseInt(data.loaded / data.total * 100, 10);
					//progress
				}
			});

			$('#file_apk').fileupload({
				dataType: 'json',
				done: function (e, data) {
					var resultString = JSON.stringify(data.result)
					var resJson = $.parseJSON(resultString);
					nts.alphamale.main._hideLoadingDiv();
					if(resJson.code !='OK'){
						alert("apk 설치에  실패했습니다");
					}else{
						$("#noti_content").append(resJson.payload);
						nts.alphamale.main._openNotifyConfirmDialog();
					}

				},
				submit : function(e,data){
					var devs = nts.alphamale.main._getDeviceList();
					data.formData = {
							"devJson" : devs.toString()
					};
				},
				progressall: function (e, data) {
					var progress = parseInt(data.loaded / data.total * 100, 10);
					if(progress < 100){
					}
				}
			});

			// scan devices
			this._ajaxFunc(this._data.ajScanDevices);

		},

		// dispose
		dispose : function(){
			this.eventUnbind();
		}, 

		// event bind
		eventBind : function(){
			$("#btnStartRecord").on("click", $.proxy(this.handleClickStartRecord,this));
			$("#btnStopRecord").on("click", $.proxy(this.handleClickStopRecord,this));	
			$("#btnReplay").on("click",$.proxy(this.handleClickReplay,this));
			$("#event_list" ).on("selectableselected", $.proxy(this.handleClickEventList,this));
			$("#log_list" ).on( "selectableselected", this.handleClickLogList);
			$("#btnScanDevice").on("click",$.proxy(this.handleClickScanDevice,this));
			$("#btnEventListClear").on("click",$.proxy(this.handleClickEvtListClear,this));

			$("#btnDeleteEvent").on("click",$.proxy(this.handleClickDeleteEvent,this));
			$("#btnAddEvent").on("click",$.proxy(this.handleClickAddEvent,this));
			$("#evtList").on('change',$.proxy(this.handleChangeCmdSelect,this));
			$("#btnSaveEventDetail").on("click",$.proxy(this.handleSaveDetailEvent,this));
			$("#btnCreateEvent").on("click",$.proxy(this.handleCreateEvent,this));
			$("#event_detail_area").on("focusout",'input',this.handleInputFocusOut);
			$("#btnAutoModeToogle").on("click",$.proxy(this.handleClickAutoModeToggle,this));
			$("#btnImportJobs").on("click",$.proxy(this.handleClickImportJobs,this));
			$("#btnExportJobs").on("click",$.proxy(this.handleClickExportJobs,this));
			$("#sel_leader, #sel_follower").on("click",'.delete',$.proxy(this.handleClickDeleteDevice,this));
			$("#btnSelLeader").on("click",$.proxy(this.handleClickSelLeader,this));
			$("#btnSelFollowers").on("click",$.proxy(this.handleClickSelFollowers,this));

			$(window).bind("beforeunload",function() {
				return '새로고침 하시게 되면  \n작성한 모든 내용은 삭제되며, 알파메일이 초기화 됩니다.';
			});

			$(window).unload(function() {
				console.log("stop record");
				$("#btnStopRecord").click();
			});


			//utils 
			$("#btnRestartAdb").on("click",$.proxy(this.handleClickRestartADB,this));
			$("#btnUnlockDevices").on("click",$.proxy(this.handleClickUnlockDeivces,this));
			$("#btnStartApp").on("click",$.proxy(this.handleClickStartApp,this));
			$("#btnUnInstallApp").on("click",$.proxy(this.handleClickUninstallApp,this));
			$("#btnUnInstallAppKeepData").on("click",$.proxy(this.handleClickUnInstallAppKeepData,this));
			$("#btnClearAppData").on("click",$.proxy(this.handleClickClearAppData,this));
			$("#btnInstallApp").on("click",$.proxy(this.handleClickInstallApp,this));
		},

		//event unbind
		eventUnbind : function(){
			$("#btnStartRecord").off("click", $.proxy(this.handleClickStartRecord,this));
			$("#btnStopRecord").off("click", $.proxy(this.handleClickStopRecord,this));
			$("#btnReplay").off("click",$.proxy(this.handleClickStopRecord,this));
			$("#event_list" ).off( "selectableselected", this.handleClickEventList);
			$("#log_list" ).off( "selectableselected", this.handleClickLogList);
			$("#btnScanDevice").off("click",$.proxy(this.handleClickScanDevice,this));
			$("#btnEventListClear").off("click",$.proxy(this.handleClickEvtListClear,this));
		}, 

		_showLoadingDiv : function(){
			$('#wrapper').waitMe({
				effect : 'bounce',
				text: 'Please waiting...',
				bg : 'rgba(255,255,255,0.7)',
				color : '#000'
			});
		},

		_hideLoadingDiv : function(){
			$('#wrapper').waitMe('hide');
		},

		_openConfirmDialog : function(func){
			$("#dialog-confirm").dialog({
				resizable : false,
				height : 300,
				width : 500,
				modal : true,
				buttons : {
					"OK" : function() {
						func();
						$(this).dialog("close");
					},
					Cancel : function() {
						$(this).dialog("close");
					}
				}
			});
		},

		_openNotifyConfirmDialog : function(){
			$("#noti-confirm").dialog({
				resizable : false,
				height : 300,
				width : 500,
				modal : true,
				buttons : {
					"OK" : function() {
						$(this).dialog("close");
					},
				}
			});

		},

		_getDeviceList : function(){
			var leader = $('#sel_leader li').children()[0];
			var followers = $('#sel_follower li');
			var devices =  new Array();

			if(leader){
				json_l = $.parseJSON(leader.value);
				devices.push(json_l.serial);	
			}


			$.each( followers, function( i, val ) {
				item = $(val).children().val();
				if(item){
					json_f = $.parseJSON(item);
					devices.push(json_f.serial);
				}
			});

			return devices;
		},

		_getPackageList : function(func){
			var leader = $('#sel_leader li').children()[0];
			var serial ="" ;
			if(leader){
				var json = $.parseJSON(leader.value);
				serial = json.serial;
			}
			if(serial && serial != ""){
				var data = {
						'leader' : serial
				}
				debugger;
				this._ajaxFunc(this._data.ajPackageList,data);
				this._openConfirmDialog($.proxy(func,this));
			}else{
				alert("리더 단말을 정상적으로 지정해 주세요.")
			}
		},

		_startApp : function(){
			var devices = this._getDeviceList();
			if(devices.length<=0){
				alert("단말을 지정해 주세요");
				return;
			}
			var pkgName = $('#pkgList option:selected').val();
			var data = { 
					'leader' : devices[0],
					'pkgName' : pkgName,
					'devJson' : devices.toString(),
			}
			this._showLoadingDiv();
			this._ajaxFunc(this._data.ajStartApp,data);
		},

		_uninstallApp : function(){
			var devices = this._getDeviceList();
			if(devices.length<=0){
				alert("단말을 지정해 주세요");
				return;
			}
			var pkgName = $('#pkgList option:selected').val();
			var data = { 
					'pkgName' : pkgName,
					'devJson' : devices.toString(),
			}
			this._showLoadingDiv();
			this._ajaxFunc(this._data.ajUninstallApp ,data);
		},

		_uninstallAppKD : function(){
			var devices = this._getDeviceList();
			if(devices.length<=0){
				alert("단말을 지정해 주세요");
				return;
			}
			var pkgName = $('#pkgList option:selected').val();
			var data = { 
					'pkgName' : pkgName,
					'devJson' : devices.toString(),
			}
			this._showLoadingDiv();
			this._ajaxFunc(this._data.ajUninstallAppKeppData  ,data);
		},

		_clearAppData : function(){
			var devices = this._getDeviceList();
			if(devices.length<=0){
				alert("단말을 지정해 주세요");
				return;
			}
			var pkgName = $('#pkgList option:selected').val();
			var data = { 
					'pkgName' : pkgName,
					'devJson' : devices.toString(),
			}
			this._showLoadingDiv();
			this._ajaxFunc(this._data.aJclearAppData  ,data);
		},

		_isNumber : function(s) {
			s += ''; // 문자열로 변환
			s = s.replace(/^\s*|\s*$/g, ''); // 좌우 공백 제거
			if (s == '' || isNaN(s)) return false;
			return true;
		},

		_validationPositionForm : function(){
			// evtPositionInfo
			// evtExPositionInfo
			if($("#evtPositionInfo").length > 0){
				var result = true;
				var posInfos = $("#evtPositionInfo").find('input');
				$.each( posInfos, function( i, val ) {
					if(!val.value || val.value == '' || val ==null || val=='null' || nts.alphamale.main._isNumber(val.value) == false){
						result = false;
					}
				});
			}

			if($("#evtExPositionInfo").length > 0){
				var posExInfos = $("#evtExPositionInfo").find('input');
				$.each( posInfos, function( i, val ) {
					if(!val.value || val.value == '' || val ==null || val=='null' || nts.alphamale.main._isNumber(val.value) == false){
						result = false;
					}
				});
			}

			if($("#evtElementInfo").length > 0){
				result = false;
				var eleExInfos = $("#evtElementInfo").find('input');
				$.each( eleExInfos, function( i, val ) {
					if(val.id && val.id.indexOf("ele_")  > -1){
						if(val.value && val.value != '' && val !=null && val !='null'){
							if(val.id == 'ele_at' && result == false){
								result =  false;
							}else{
								result = true;
							}
						}
					}
				});
			}

			if($("#evtDetailTemplateTable").length > 0){
				var posExInfos = $("#evtDetailTemplateTable").find('input');
				$.each( posInfos, function( i, val ) {
					if(!val.value || val.value == '' || val ==null || val=='null'){
						result = false;
					}
				});
			}
			return result;
		},

		handleClickSelLeader : function(){
			if($("#sel_leader li").length > 0){
				alert("리더 단말은 한대만 등록 가능합니다");
				return;
			}
			var lis = $("#scaned_device_list .ui-selected");
			if(lis.length <= 0 ){
				alert("리더 단말을 지정해 주세요");
				return;
			}
			if(lis.length > 1){
				alert("리더 단말은 한대만 등록 가능합니다");
				return;
			}

			var leader = lis[0];
			$(leader).find(".delete").show();
			$("<li class='ui-widget-content'></li>").html($(leader).html()).appendTo($("#sel_leader ul"));
			$(leader).remove();
		},

		handleClickSelFollowers : function(){
			var lis = $("#scaned_device_list .ui-selected");
			if(lis <= 0 ){
				alert("팔로워 단말을 지정해 주세요");
				return;
			}

			if(lis > 1){
				alert("리더 단말은 한대만 등록 가능합니다");
				return;
			}

			for(var i = 0; i < lis.length; i++){
				var follower = lis[i];
				$(follower).find(".delete").show();
				$("<li class='ui-widget-content'></li>").html($(follower).html()).appendTo($("#sel_follower ul"));
				$(follower).remove();
			}
		},

		handleAfterRestartADB : function(){
			this._ajaxFunc(this._data.ajScanDevices);
		},

		handleAtferRequestComplete : function(){
			this._hideLoadingDiv();
		},

		handleClickInstallApp : function(){
			var devices = this._getDeviceList();
			if(devices.length > 0){
				nts.alphamale.main._showLoadingDiv();
				$("#file_apk").click();
			}else{
				alert("지정된 단말이 없습니다.");
				return;
			}
		},

		handleClickUninstallApp : function(){
			this._getPackageList(this._uninstallApp);
		},

		handleClickUnInstallAppKeepData : function(){
			this._getPackageList(this._uninstallAppKD);
		},

		handleClickClearAppData : function(){
			this._getPackageList(this._clearAppData);
		},

		handleClickStartApp : function() {
			var leader = $('#sel_leader li').children()[0];
			var serial ="" ;
			if(leader){
				var json = $.parseJSON(leader.value);
				serial = json.serial;
			}
			if(serial && serial != ""){
				var data = {
						'leader' : serial
				}
				this._ajaxFunc(this._data.ajPackageList,data);
				this._openConfirmDialog($.proxy(this._startApp,this));
			}else{
				alert("리더 단말을 정상적으로 지정해 주세요.")
			}
		},

		handleAtferGetPackageList : function(result){
			if(result){
				$("#pkgList").empty();
				$.each( result, function( i, val ) {
					pkgItem = ['<option value=', val ,'>',val,'</option>'].join('');
					$("#pkgList").append(pkgItem);
				});
			}
		},

		handleClickRestartADB : function(){
			if(this._isControll){
				alert("실행중에 adb를 restart 하실 수 없습니다. stop 버튼을 누른 후 수행해 주세요");
				return ;
			}
			this._showLoadingDiv();
			this._ajaxFunc(this._data.ajRestartADB);
		},

		handleClickUnlockDeivces : function(){
			var devices = this._getDeviceList();
			if(devices.length > 0){
				var jsonStr = JSON.stringify(devices, null , '\t');
				var data = {
						'devJson' : jsonStr
				}
				this._showLoadingDiv();
				this._ajaxFunc(this._data.ajUnlockDevice,data);		
			}else{
				alert("단말을 leader 혹은 follower로 지정해 주세요.");
			}
		},


		buttonStateChange : function(){
			if(this._isControll){
				$("#btnReplay").attr("disabled",false);
				$("#btnScanDevice").attr("disabled",true);
			}else{
				$("#btnReplay").attr("disabled",true);
				$("#btnScanDevice").attr("disabled",false);
			}
		} ,

		handleClickDeleteDevice : function(evt){
			$("#scaned_device_list").append($($(evt.target).parent()));
			$("#scaned_device_list").find(".delete").hide();
		},

		handleClickImportJobs : function(){
			$("#file").click();
		},

		handleAtferExportJob : function(result){
			alert("실행 경로에 export 파일이 생성 되었습니다.")
		},

		handleClickExportJobs : function(){
			var selecteds = $("#event_list .ui-selected");

			if(selecteds.length == 0){
				selecteds = $("#event_list li");
			}

			if(selecteds.length <= 0){
				alert("이벤트가 없습니다.");
				return;
			}

			var arr = new Array();
			$.each( selecteds, function( i, val ) {
				item = $(val).children()[1].value;
				arr.push(item);	
			});
			var jsonObj = JSON.stringify(arr, null , '\t');
			var data = { 
					'jobJsons' : jsonObj
			};

			this._ajaxFunc(this._data.ajExportJobs, data);
		},

		handleAtferIsControll : function(result){
			if(result =='true'){
				alert("이미 alphamale이 실행중입니다. 만약 실행중이 아니라면 재실행 부탁드립니다.");
				window.close();
			}
		},

		handleClickAutoModeToggle : function(){
			this._ajaxFunc(this._data.ajAutoModeToggle);
		},

		handleAutoModeToggle : function(result){
			console.log(result);
			if(result == 'true'){
				$("#btnAutoModeToogle").text("Auto Mode");
			}else{
				$("#btnAutoModeToogle").text("Position Mode");
			}
		},

		handleAtferCreateEvt : function(result){
			var str = JSON.stringify(result, null, '\t');
			console.log(str);
			var replaceItem = ['<span class="sort-handle">&gt;</span>', result.title,
			                   '<input type="hidden" value=\'' , str,'\'/>'
			                   ].join('');
			$("#evt_tmpl").html(replaceItem);
			id = "evt_" + result.seq;
			$("#evt_tmpl").attr('id', id);
			$('#evtList').val('');
			this._hideLoadingDiv();
		},

		handleCreateEvent : function(){

			if(this._validationPositionForm() ==false){
				alert("입력값을 정확하게 입력해주세요.");
				return;
			}
			if($('#evtList').val() ==''){
				alert("이벤트를 지정해 주세요 ");
				return;
			}
			//좌표 정보 
			var result ={};
			var event ={};
			var position={};

			var eType = $('#evtList option:selected').val();;
			result['eType'] = eType;

			var device ={"serial":"38ffb04d","width":1080,"height":1920,"xScale":1.0,"yScale":1.0};
			result['deviceInfo'] = device;

			if($("#evtElementInfo").length){
				var element = {'resourceId' : $('#ele_id').val(), 'text' : $('#ele_txt').val(), 'className' : $('#ele_cls').val(), 'contentDesc' : $('#ele_desc').val() , 'instance' : $('#ele_at').val()};
				event['element']  = element;
			}

			if($('#evtPositionInfo').length){
				var startPoint = {'x' : $('#pos_s_x').val(), 'y' : $('#pos_s_y').val()};
				var endPoint = {'x' : $('#pos_e_x').val() , 'y' : $('#pos_e_y').val()};
				position['startPoint']  = startPoint;
				position['endPoint']   = endPoint;

				if($('#evtExPositionInfo').length){
					var ex_startPoint = {'x' : $('#ex_pos_s_x').val(), 'y' : $('#ex_pos_s_y').val()};
					var ex_endPoint = {'x' : $('#ex_pos_e_x').val() , 'y' : $('#ex_pos_e_y').val()};
					position['ex_startPoint']  = ex_startPoint;
					position['ex_endPoint']  = ex_endPoint; 
				}
				event['position'] = position;
			}

			if($('#evtDetailTemplateTable').length){
				var inputs = $('#evtDetailTemplateTable input');
				inputs.each(function(){
					id = $(this).attr('id');
					event[id] =  $(this).val();
				});

				event['template'] = $('#evtDetailTemplateTable').html();
			}
			result['eventInfo'] = event;

			var str = JSON.stringify(result, null, '\t');
			console.log(str);
			var data = { 
					'jobJson' : str
			};
			this._showLoadingDiv();
			this._ajaxFunc(this._data.ajCreateEvt, data);
		},

		handleInputFocusOut : function(evt){
			$(evt.target).attr('value',$(evt.target).val());
		},

		handleAtferUpdateEvt : function(json){
			console.log(json);
			var replaceItem = [ '<span class="sort-handle">&gt;</span>',json.title,
			                    '<input type="hidden" value=\'' , JSON.stringify(json),'\'/>'
			                    ].join('');
			var id = "#evt_" + json.seq;
			$(id).html(replaceItem);
		},

		handleSaveDetailEvent : function(){
			//json update
			if(this._selectedEvtLi){
				var json = $.parseJSON(this._selectedEvtLi);
				if(json.eventInfo.position){
					var startPoint = {'x' : $('#pos_s_x').val(), 'y' : $('#pos_s_y').val()};
					var endPoint = {'x' : $('#pos_e_x').val() , 'y' : $('#pos_e_y').val()};
					json.eventInfo.position.startPoint  = startPoint;
					json.eventInfo.position.endPoint  = endPoint;

					if(json.eventInfo.position.ex_startPoint){
						var ex_startPoint = {'x' : $('#ex_pos_s_x').val(), 'y' : $('#ex_pos_s_y').val()};
						var ex_endPoint = {'x' : $('#ex_pos_e_x').val() , 'y' : $('#ex_pos_e_y').val()};
						json.eventInfo.position.ex_startPoint  = ex_startPoint;
						json.eventInfo.position.ex_endPoint  = ex_endPoint; 
					}
				}
				if(json.eventInfo.element){
					var element = {'resourceId' : $('#ele_id').val(), 'text' : $('#ele_txt').val(), 'className' : $('#ele_cls').val(), 'contentDesc' : $('#ele_desc').val() , 'instance' : $('#ele_at').val()};
					json.eventInfo.element  = element;
				}
				if(json.eventInfo.template){
					var inputs = $('#evtDetailTemplateTable input');
					inputs.each(function(){
						id = $(this).attr('id');
						json.eventInfo[id] =  $(this).val();
					});

					json.eventInfo.template.template = $('#evtDetailTemplateTable').html();
				}
				var str = JSON.stringify(json, null, '\t');

				console.log(str);
				var data = { 
						'jobJson' : str
				};
				this._ajaxFunc(this._data.ajUpdateEvt, data);

			}
		},

		handleChangeSelectorType : function(){
			var radioValue = $('input[name=stype]:checked').val();
			if(radioValue == 'POSITION'){
				$('#evtElementInfo').remove();
				this._addPositionForm();
			}
			if(radioValue == 'ELEMENT'){
				$('#evtPositionInfo').remove();
				this._addElementForm();

			}
		},

		handleChangeCmdSelect : function(){
			$("#evtDetailInfoTable").empty();
			$("#evtDetailTemplateTable").empty();
			var item = $('#evtList option:selected').val();
			var data = { 
					'evtName' : item
			};
			this._ajaxFunc(this._data.ajTemplate, data);
		},

		handleClickScanDevice : function(evt){
			$("#scaned_device_list").empty();
			$("#sel_leader li").remove();
			$("#sel_follower li").remove();
			// scan devices
			this._ajaxFunc(this._data.ajScanDevices);

		},

		handleClickDeleteEvent : function(evt){
			var selecteds = $("#event_list .ui-selected");
			$.each( selecteds, function( i, val ) {
				$(val).remove();
			});

			$("#evtListArea").hide();
			$("#evtDetailInfoTable").empty();
			$("#evtDetailTemplateTable").empty();
			$("#btnSaveEventDetail").hide();
			$("#btnCreateEvent").hide();
		},

		handleClickAddEvent : function(evt){
			if($("#evt_tmpl").length){
				alert("기존에 생성하고자 하는 이벤트가 존재함");
			}else{
				var addItem = ['<li class="ui-widget-content" id="evt_tmpl">new Event',
				               '<input type="hidden" value=""/>',
				               '</li>'].join('');
				$("#event_list").append(addItem);
			}
		},

		handleClickEvtListClear : function(evt){
			$("#event_list").empty();
			$("#evtDetailInfoTable").empty();
			$("#evtDetailTemplateTable").empty();
			$("#evtListArea").hide();
			$("#btnSaveEventDetail").hide();
			$("#btnCreateEvent").hide();
			$("#log_list").empty();
			$("#log_detail_list").empty();

		},

		handleClickReplay : function(evt){
			var selecteds = $("#event_list .ui-selected");
			var arr = new Array();
			$.each( selecteds, function( i, val ) {
				item = $(val).children()[1].value;
				arr.push(item);	
			});
			var jsonObj = JSON.stringify(arr);
			var data = { 
					'jobJsons' : jsonObj
			};

			this._ajaxFunc(this._data.ajReplay, data);
		},

		handleAtferReplay : function(result){
		},


		handleAtferGetTemplate : function(result){
			$("#tmplJson").val(JSON.stringify(result));
			if(result.type == 'BOTH'){
				this._addTypeSeletedForm();
			}
			if(result.type == 'POSITION'){
				this._addPositionForm();
			}
			if(result.type == 'EX_POSITION'){
				this._addPositionForm();
				this._addExPositionForm();
			}
			if(result.type == 'ELEMENT'){
				this._addElementForm();
			}
			if(result.template){
				$('#evtDetailTemplateTable').append('<tr><td>' + result.template + '</td></tr>');
			}
		},

		//Event Handler
		handleClickLogList : function(evt, ui){
			var json = $.parseJSON($(ui.selected.children).val());
			var table=$("#log_detail_list");
			table.empty();
			var header = [	
			              '<tr>',
			              '<td>serial</td>' ,
			              '<td>model</td>',
			              '<td>time</td>',
			              '<td>success?</td>',
			              '<td>cause</td>',
			              '</tr>'
			              ].join('');
			table.append(header);

			$.each( json, function( i, val ) {
				var tmpl = [
				            '<tr>',
				            '<td>', val.serial ,'</td>',
				            '<td>', val.model ,'</td>',
				            '<td>', val.time ,'</td>',
				            '<td>', val.isSuccess, '</td>',
				            '<td>', val.cause, '</td>',
				            '</tr>'
				            ].join('');
				table.append(tmpl)
			});
		},

		/**
		 * 이벤트 리스트 선택시, 이벤트 상세를 보여주기 위한 작업
		 */
		parseJsonToForm : function(json){
			var position = json.eventInfo.position;
			var element = json.eventInfo.element;
			if(json.eventInfo.template && json.eventInfo.template.length > 0){
				var template = $.parseJSON(json.eventInfo.template);
			}
			//positino 정보가 있을 경우 
			if(json.jobEType =='좌표'){
				this._addPositionForm(position.startPoint.x, position.startPoint.y, position.endPoint.x, position.endPoint.y);
				if(position.ex_startPoint){
					this._addExPositionForm(position.ex_startPoint.x, position.ex_startPoint.y, position.ex_endPoint.x, position.ex_endPoint.y);
				}
			}
			//element 정보가 있을 경우 
			if(json.jobEType =='요소'){
				this._addElementForm(element.resourceId, element.text, element.contentDesc , element.className, element.instance);
			}

			if(template && template.template){
				$('#evtDetailTemplateTable').append(template.template);
			}
			//추가 template이 있을 경우

		},

		handleClickEventList : function(evt, ui){
			this._selectedEvtLi = $(ui.selected.children[1]).val();

			if($(ui.selected).attr('id') !='evt_tmpl'){
				$("#evtListArea").hide();
				$("#evtDetailInfoTable").empty();
				$("#evtDetailTemplateTable").empty();

				var json = $.parseJSON($(ui.selected.children[1]).val());
				$('#evtDetailInfoTable').append('<tr><td> Title : ' + json.title +  '</tr>');
				$('#evtDetailInfoTable').append('<tr><td> Type : ' + json.jobEType +  '</tr>');
				$('#evtDetailInfoTable').append('<tr><td> Event name : ' + json.eventInfo.eType +  '</tr>');
				this.parseJsonToForm(json);
				$("#btnSaveEventDetail").show();
				$("#btnCreateEvent").hide();
			}else{
				var item = $('#evtList option:selected').val();
				if(item ==''){
					$("#evtDetailInfoTable").empty();
					$("#evtDetailTemplateTable").empty();
				}
				$("#evtListArea").show();
				$("#btnSaveEventDetail").hide();
				$("#btnCreateEvent").show();
			}
		},

		/***
		 * start btn click handler 
		 */
		handleClickStartRecord : function(evt){
			var leader = $('#sel_leader li').children()[0];
			var followers = $('#sel_follower li');

			if(leader){
				this._showLoadingDiv();
				leaderJson = $.parseJSON($(leader).val());
				if(followers.length > 0){
					var a = [];
					for (var i = 0; i < followers.length; i++) {
						followerJson = $.parseJSON($(followers[i].children).val());
						a.push(followerJson.serial);
					}
				}
				var data = { 
						'leader' : leaderJson.serial,
						'followers' : a 
				};
				this._ajaxFunc(this._data.ajStartRecord, data);		
			}else{
				alert("리더 단말은 반드시 지정되어야 합니다. You should select a leader.");
				this._hideLoadingDiv();
			}

		},

		handleClickStopRecord : function(evt){
			this._isRunning = false;
			$("#btnStartRecord").show();
			$("#btnStopRecord").hide();
			this._ajaxFunc(this._data.ajStopRecord);
			this._isControll = false;
			this.buttonStateChange();
		},

		//ajax handler 
		handleAtferStartRecord : function(result){
			this._isRunning = true;
			$("#btnStartRecord").hide();
			$("#btnStopRecord").show();
			this._webScoketInit(this._data.wsEventMonitor);
			this._webScoketInit(this._data.wsExecitionLogMonitor);
			this._isControll = true;
			this.buttonStateChange();
			this._hideLoadingDiv();
		},

		handleAtferScanDevices : function(result){
			console.log(JSON.stringify(result));
			this._hideLoadingDiv();

			$("#scaned_device_list").empty();
			$("#sel_leader ul").empty();
			$("#sel_follower ul").empty();

			var devices = result.payload;
			if(result.code === 'OK'){
				var addItems = [];
				$.each( devices, function( i, val ) {
					addItems.push(['<li class="ui-widget-content">',
					               val.title ,
					               '<input type="hidden" value=\'' , ,JSON.stringify(val.deviceInfo),'\'/>',
					               '<a href="#" class="delete" style="display:none;">x</a>',
					               '</li>'].join(''));
				});
				$("#scaned_device_list").append(addItems.join(''));
				///////////////////////////////////
//				$("#scaned_device_list li").draggable({
//					appendTo: "body",
//					helper: "clone"
//				});
				///////////////////////////////////
			}
			$('#wrapper').waitMe('hide');
		},

		//Websock Handler 
		handleAfterEventCatched : function(evt){
			var json = $.parseJSON(evt.data);
			var addItem = ['<li class="ui-widget-content" id="evt_', json.seq , '" title="',json.title,'">',
			               '<span class="sort-handle">&gt;</span>', json.title,
			               '<input type="hidden" value=\'' , evt.data,'\'/>',
			               '</li>'].join('');
			$("#event_list").append(addItem);
			$("#event_list_area").scrollTop($("#event_list").height());
		},

		handleAfterExecutionLogCatched : function(evt){
			var json = $.parseJSON(evt.data);
			var addItem = ['<li class="ui-widget-content" title="',json.jobName,':',json.result,'">',
			               json.jobName,' : ' , json.result,
			               '<input type="hidden" value=\'' , JSON.stringify(json.logList),'\'/>',
			               '</li>'].join('');

			if(json.jobSeq != -1){
				if(json.result == false){
					bColor  = "#ff7f50";	
				}
				if(json.result == true){
					bColor = "#90ee90";
				}
				var id = "#evt_" + json.jobSeq;
				$(id).css("background",bColor);
			}
			$("#log_list").append(addItem);
			$("#log_list_area").scrollTop($("#log_list").height());
		},

		//common method 

		_addTypeSeletedForm : function(){
			var _typeTemplate = [
			                     '<tr><td>',
			                     '<input type="radio" name="stype" value="POSITION"> 좌표기반 ', 
			                     '<input type="radio" name="stype" value="ELEMENT"> 요소기반<br>',
			                     '<td><tr>'
			                     ].join('');
			$('#evtDetailInfoTable').append(_typeTemplate);
			$('input[name=stype]').on('change',$.proxy(this.handleChangeSelectorType,this));
		},

		_addPositionForm : function(sX,sY,eX,eY){
			var _posTemplate  = [
			                     '<table id="evtPositionInfo">',
			                     '<tr><td colspan="2"><u>First Point</u></td></tr>',
			                     '<tr><td>Start {X} : </td><td><input type="text" id="pos_s_x" value="', sX ,'"></input></td></tr>',
			                     '<tr><td>Start {Y} :  </td><td><input type="text" id="pos_s_y" value="', sY ,'"></input></td></tr>',
			                     '<tr><td>End {X} :  </td><td><input type="text" id="pos_e_x" value="', eX ,'"></input></td></tr>',
			                     '<tr><td>End {Y} :  </td><td><input type="text" id="pos_e_y" value="', eY ,'"></input></td></tr>',
			                     '</table>'
			                     ].join('');
			$('#evtDetailInfoTable').append(_posTemplate);
		},

		_addExPositionForm : function(sX,sY,eX,eY){
			var _exPosTemplate  = [
			                       '<table id="evtExPositionInfo">',
			                       '<tr><td colspan="2"><u>Second Point</u></td></tr>',
			                       '<tr><td>Start {X} : </td><td><input type="text" id="ex_pos_s_x" value="', sX ,'"></input></td></tr>',
			                       '<tr><td>Start {Y} : </td><td><input type="text" id="ex_pos_s_y" value="', sY ,'"></input></td></tr>',
			                       '<tr><td>End {X} : </td><td><input type="text" id="ex_pos_e_x" value="', eX ,'"></input></td></tr>',
			                       '<tr><td>End {Y} : </td><td><input type="text" id="ex_pos_e_y" value="', eY ,'"></input></td></tr>',
			                       '</table>'
			                       ].join('');
			$('#evtDetailInfoTable').append(_exPosTemplate);
		},

		_addElementForm : function(id, text, desc , cls, at){
			var _eleTemplate =  [
			                     '<table id="evtElementInfo">',
			                     '<tr><td colspan="2"><u>Element</u></td></tr>',
			                     '<tr><td>ID :  </td><td><input type="text" id="ele_id" value="', id ,'"></input></td></tr>',
			                     '<tr><td>Text :  </td><td><input type="text" id="ele_txt" value="', text ,'"></input></td></tr>',
			                     '<tr><td>Description :  </td><td><input type="text" id="ele_desc" value="', desc ,'"></input></td></tr>',
			                     '<tr><td>Class :  </td><td><input type="text" id="ele_cls" value="', cls ,'"></input></td></tr>',
			                     '<tr><td>InstanceAt :  </td><td><input type="text" id="ele_at" value="', at ,'"></input></td></tr>',
			                     '</table>'
			                     ].join('');
			$('#evtDetailInfoTable').append(_eleTemplate);
		},


		/**
		 * get ajax paramter  
		 */
		_ajFillParams : function(params, requriedParams, activeData){
			var result = {};
			if(params!=null){
				var _p = params,
				_len = _p.length;
				for(var i=0; i<_len; i++){
					if(activeData && activeData[_p[i]] && activeData[_p[i]] !=='NaN'){
						result[_p[i]] = activeData[_p[i]];
					}
				}
				var dataVaild = true;
				if(requriedParams){
					var _pLen = requriedParams.length;
					for (var i=0; i<_pLen; i++){
						if(!result[requriedParams[i]]){
							dataVaild = false;
							break;
						}
					}
				}
				if(!dataVaild){
					result = {};
					console.log('ajax handle requried parameter missing');
				}
			}
			return result;
		},

		/**
		 * ajax request method  
		 */
		_ajaxFunc : function(data, activeData){
			var params;
			if(data.params){
				params = this._ajFillParams(data.params, data.requiredParams, activeData);
			}
			$.ajax({
				url : data.url,
				data : params,
				cache : data.cache,
				async : data.async,
				method : data.type,
				dataType : data.dataType
			}).done($.proxy(function(result){
				if(data.success)
					this[data.success](result);
			},this)).fail($.proxy(function( jqXHR, textStatus ) {
				console.log( "Request failed: " + textStatus );
				if(data.error){
					this[data.error](result);
				}
			},this));
		},

		/**
		 * web socket init
		 */
		_webScoketInit : function(data){
			var wsUrl = [];
			wsUrl.push([
			            'ws://' , 
			            this.host,
			            data.url
			            ].join(''));

			//socket create
			_webSocket = new WebSocket(wsUrl); 
			//Connected to server
			_webSocket.onopen = this[data.open];    
			//Connection close
			_webSocket.onclose = this[data.close];    
			//Message Receved
			_webSocket.onmessage = this[data.message];    
			//Error
			_webSocket.onerror = this[data.error];    
		}
}
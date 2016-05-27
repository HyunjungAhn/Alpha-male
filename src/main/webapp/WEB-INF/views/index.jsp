<!-- 
AlphaMale for web
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

 -->

<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Alpha-male</title>
<link rel="stylesheet" href="./css/alphamale.css">
<link rel="stylesheet" href="./css/jquery-ui.structure.css">
<link rel="stylesheet" href="./css/jquery-ui.theme.css">
<script src="./js/jquery-2.2.1.min.js"></script>
<script src="./js/jquery-ui.js"></script>
<script src="./js/alphamale.js"></script>

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
			<h3>알파메일을 소개 합니다~</h3>
		</div>
		</div>
		</main>
		
		<nav id="nav">
				<div class="innertube">
					<h3><a href="#">AlphaMale</a></h3>
					<h3><a href="#">Utils</a></h3>
				</div>
			</nav>
	</div>
	<footer id="footer">
			<div class="innertube">
				<p>Footer...</p>
			</div>
	</footer>
</body>
<script type="text/javascript">
	var main = nts.alphamale.main;
	main.init();
	
</script>
</html>
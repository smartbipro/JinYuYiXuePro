<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Cache-Control" content="no-cache, must-revalidate">
<meta http-equiv="expires" content="Sat, 01 Jan 2000 00:00:00 GMT">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>测试</title>
<style>
body{padding-left: 20px;}
input{width:300px}
.addbgcolor{background:yellowgreen}
</style>
    <link rel="stylesheet" type="text/css" href="css/bof_merge.css.jsp"/>
    <script type="text/javascript" src="js/freequery/lang/JSLoader.js"></script>
	<script type="text/javascript" src="js/customeModuleDemo/customeModuleDemo.js"></script>
	<script type="text/javascript" src="js/customeOpendialog/customOpendialogConfig.js"></script>	
	<script type="text/javascript" src="js/customeOpendialog/CustomChangePasswordDialog.js"></script>  
	  
</head>
<body onload="init();" onunload="cleanup();">
	<div style="text-align: center;font-size: 24px;color: blue;">功能测试界面</div>
	<hr>
	<div style="padding-top:5px;">
		<label style="color:red;">【登录】</label>
		<label>用户名：</label><input type="text" id="username" style="width:180px;" value="admin" placeholder="请输入用户名"/>
		<label>密码：</label><input type="text" id="password" style="width:180px;" value="admin" placeholder="请输入密码"/>
		<input type="button" value="登录" onclick="login();" style="width:100px;" />
		<label id="loginmsg" style="color:blue;font-size:8px;"></label>
	</div>	
	<hr>
	<div style="padding-top:5px;">
		<label>【新增】</label>
		<input type="button" value="即席查询" onclick="addReport('combinedquery');" style="width:100px;" />
		<input type="button" value="透视分析" onclick="addReport('insight');" style="width:100px;" />
		<input type="button" value="仪表盘" onclick="addReport('dashboard');" style="width:100px;" />
	</div>
	<hr>
	<div style="padding-top:5px;">
		<label style="color:red;">资源id(resid)</label>
		<label style="color:blue;padding-left:5px;">即席查询：</label><input type="text" id="jxcxresid" style="width:330px;" value="I8a8a8c76018673377337d07901867344cdc5001c"/>
		<label style="color:blue;padding-left:5px;">透视分析：</label><input type="text" id="tsfxresid" style="width:330px;" value="I8a8a8c76018678107810151101867ce2d0530809"/>
		<label style="color:blue;padding-left:5px;">仪表盘：</label><input type="text" id="ybpresid" style="width:330px;" value="I8a8a8c7601867ce47ce4e58501867ce4e5850000"/>
	</div>	
	<div style="padding-top:5px;">
		<div style="display: inline-block;">
		<label>【修改】</label>
		<input type="button" value="即席查询" onclick="editReport('combinedquery');" style="width:100px;" />
		<input type="button" value="透视分析" onclick="editReport('insight');" style="width:100px;" />
		<input type="button" value="仪表盘" onclick="editReport('dashboard');" style="width:100px;" />
		</div>		
		
		<div style="display: inline-block;padding-left:50px;">
		<label>【查询(只读)】</label>
		<input type="button" value="即席查询" onclick="searchReport('combinedquery');" style="width:100px;" />
		<input type="button" value="透视分析" onclick="searchReport('insight');" style="width:100px;" />
		<input type="button" value="仪表盘" onclick="searchReport('dashboard');" style="width:100px;" />
		</div>
		
	</div>
	<hr>
	<div style="padding-top:5px;">
		<label style="color:red;">删除资源id(resid)：</label><input type="text" id="deleteresid" style="width:330px;" value="I8a8a8c7601867ce47ce4e58501867d6f76f70001"/>
		
		<div style="display: inline-block;padding-left:5px;">
		<label>【删除】</label>
		<input type="button" value="删除资源" onclick="deleteReport('I8a8a8c7601867ce47ce4e58501867d6f76f70001');" style="width:100px;" />
		<label style="color:red;font-size:8px;">删除即席查询、透视分析、仪表盘</label>
		</div>
	</div>
	<hr>
	<div>
	<label style="color:red;">埋点：</label> <label>指标ID：</label><input type="text" id="indexId" value="指标id" placeholder="请输入指标ID"/>
	<label>指标名称：</label><input type="text" id="indexName" value="指标名称" placeholder="请输入指标名称"/>
	<input type="button" value="埋点查询计算" onclick="indexClickCalc(1);" style="width:100px;">
	<input type="button" value="埋点点击计算" onclick="indexClickCalc(2);" style="width:100px;">
	</div>
	<hr>
	<div>
	<label style="color:red;">弹窗相关</label>
	<input type="button" value="弹窗加载资源" onclick="openCustomeDialog();" style="width:100px;">
	<input type="button" value="自定义弹窗" onclick="openCustomeChangePasswordDialog();" style="width:100px;">
	</div>	
	<hr>	
	<div>
	<label style="color:red;">实现对象(module)：</label><input type="text" id="moduleName" value="JinYuYiXueDataPortalModule"/>
	<label>接口方法：</label><input type="text" id="moduleFn" value="getIndexHomeTreeNodes"/>
	<label>参数：</label><input type="text" id="params"/>
	<input type="button" value="调用接口" onclick="doModule();" style="width:100px;">
	</div>
	<hr>	
	<textarea rows="40" cols="200" id="data"></textarea>
</body>
</html>

var dialogFactory;
//1、窗口的宽度和高度设置
var formConfig = {
	'width': 1200,
	'height': 600,
	'title': '弹窗标题'
}

if (!dialogFactory) {
	dialogFactory = jsloader.imports('freequery.dialog.dialogFactory').getInstance();
}
//弹窗点击事件实现方法
function openCustomeDialog() {
	var dialogSize = [formConfig.width, formConfig.height];
	//弹窗配置
	var dialogConfig = {
		title: formConfig.title,
		size: dialogSize,
		dialogType: "modal",
		fullName: 'freequery.dialog.BaseDialogEx'
	};
	var data = [];
	data.push({
		size: dialogSize
	});
	var opts = {
		success: openResourceDialog
	};	
	dialogFactory.showDialog(dialogConfig, data, null, this, opts);
}

function openResourceDialog(context) {
	//2、资源id
	var resId = 'I2c94ea86298cbe6c01298cfd9ba900fa';
	//3、参数配置
	var paramsInfo = [];
	paramsInfo.push({
		name: "用户",
		value: 'I8a8ae5ca016c46bd46bdd161016c6acfdf10460f',
		displayValue: 'ceshi'
	});
	// 调节按钮间距
	context.div = document.createElement("div");
	context.div.style.height = "100%";
	context.dialogBody.appendChild(context.div);
	var url = '';
	var elem = window.dialog.dialogBody;
	elem.innerHTML = '<iframe src="" width="100%" height="100%" border="0" frameSpacing="0" frameBorder="0"></iframe>';
	var btnOK = window.dialog.btnOK;
	var btnCancel = window.dialog.btnCancel;
	btnOK.style.display = 'none';
	btnCancel.style.display = 'none';
	context.btnButtonArea.firstElementChild.style.padding = "5px";
	context.btnButtonArea.firstElementChild.style.height = "0px";

	var myparamsInfo = [{
		name: "paramsInfo",
		value: lang.toJSONString(paramsInfo)
	}];
	if (!form) {
		var form = document.createElement("FORM");
		context.dialogBody.firstChild.contentDocument.body.appendChild(form);
	}
	form.innerHTML = "";
	form.style.display = "none";
	form.id = "__openURLForm";
	form.action = "openresource.jsp?smx_mobile=true";
	form.method = 'POST';
	form.target = "_self";
	var residinput = document.createElement("input");
	residinput.name = "resid";
	residinput.value = resId;
	form.appendChild(residinput);
	for (var i = 0; i < myparamsInfo.length; i++) {
		var input = document.createElement("input");
		input.name = myparamsInfo[i].name;
		input.value = myparamsInfo[i].value;
		form.appendChild(input);
	}
	form.submit();
}

//修改密码
function openCustomeChangePasswordDialog() {
	//弹窗配置
	var dialogConfig = {
		title: "修改密码",
		size: dialogFactory.size.SMALL,
		dialogType: "modal",
		fullName: 'customeOpendialog.CustomChangePasswordDialog'
	};
	var data = [];
	data.push({
	});
	dialogFactory.showDialog(dialogConfig, data, null, this);	
}
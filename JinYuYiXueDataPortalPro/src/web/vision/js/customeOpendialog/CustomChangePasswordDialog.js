/**
 * 自定义修改密码对话框
 */
var BaseDialogEx = jsloader.resolve("freequery.dialog.BaseDialogEx");
var SetPasswordView = jsloader.resolve("freequery.usermanager.SetPasswordView");

/************************************************************************************
* CustomChangePasswordDialog
 ***********************************************************************************/
var CustomChangePasswordDialog = function(){
	CustomChangePasswordDialog.superclass.constructor.call(this);
	this.setPasswordView = new SetPasswordView();
	this.setPasswordView.onClose.subscribe(this.doClose, this);
	this.parent = null;
}

lang.extend(CustomChangePasswordDialog, BaseDialogEx);

CustomChangePasswordDialog.prototype.destroy = function(){
	this.setPasswordView.unrender(this.parent);
	this.setPasswordView.destroy();
	this.setPasswordView = null;
}

CustomChangePasswordDialog.prototype.init = function(parent,params, fn, obj){
	CustomChangePasswordDialog.superclass.init.call(this,parent, params, fn, obj);
	this.parent = parent;
	/**
	 * 修改确定按钮的样式
	 */
	this.reSetButtonCss(this.btnOK, "#409EFF", "#FFF", "#DCDFE6", 
			function(){this.style.background = "#61aefd";},
			function(){this.style.background="#409EFF";}
	);
	/**
	 * 修改取消按钮的样式
	 */
	this.reSetButtonCss(this.btnCancel, "#FFF", "#606266", "#DCDFE6", 
			function(){this.style.border = "1px solid #409EFF";
					   this.style.background = "#f6fbff";},
			function(){this.style.border = "1px solid #DCDFE6";
					   this.style.background = "#FFF";}
	);	
	this.btnOK.parentElement.style.paddingTop="10px";
	this.setPasswordView.element.innerHTML = this.reSetInput();
	this.reCreateInput();
	this.setPasswordView.render(this.dialogBody);
}

CustomChangePasswordDialog.prototype.doClose = function(obj, e, newPass){
	this.close(newPass, false);
}

CustomChangePasswordDialog.prototype.doOK = function(){
	this.setPasswordView.doOk();
}

/**
 * 重置按钮的样式
 */
CustomChangePasswordDialog.prototype.reSetButtonCss = function(button, 
		backgroundColor, fontColor, borderColor, 
		onmouseover, onmouseout){
	button.style.width = "80px";
	button.style.height = "30px";
	button.style.background = backgroundColor;
	button.style.color = fontColor;
	button.style.border = "0px";
	button.style.border = "1px solid " + borderColor;
	button.style.borderRadius="4px";
	button.style.outline = "none";
	button.style.cursor = "pointer";
	button.onmouseover = onmouseover;
	button.onmouseout = onmouseout;
}

/**
 * 重置table中的html
 */
CustomChangePasswordDialog.prototype.reSetInput = function(){
	var result = [];
	var inputStyle = " style=\"width:100%;height: 25px;outline:none;cursor:pointer;border:1px solid #DCDFE6;\"";
	var trStyle = " style=\"padding-left: 12px;font: 400 13.3333px Arial;\"";
	result.push("<div><table class=\"dialog_editformitemstable flat\">");
	result.push("<tbody><tr>");
	result.push("	<td width=\"115px\" height=\"35px\" ");
	result.push(trStyle);
	result.push(">旧密码：</td>");
	result.push("	<td><input class=\"_oldPassword inputtext\" type=\"password\" maxlength=\"122\" ");
	result.push(inputStyle)
	result.push("></td>");
	result.push("</tr>");
	result.push("<tr>");
	result.push("	<td height=\"35px\" ");
	result.push(trStyle);
	result.push(">新密码：</td>");
	result.push("	<td><input class=\"_newPassword inputtext\" type=\"password\" maxlength=\"122\" ");
	result.push(inputStyle)
	result.push("></td>");
	result.push("</tr>");
	result.push("<tr>");
	result.push("	<td height=\"35px\" ");
	result.push(trStyle);
	result.push(">确认新密码：</td>");
	result.push("	<td><input class=\"_confirmPassword inputtext\" type=\"password\" maxlength=\"122\" ")
	result.push(inputStyle)
	result.push("></td>");
	result.push("</tr>");
	result.push("</tbody></table></div>");
	return result.join('');
}
/**
 * 重新生成html后需要重置各个密码控件
 */
CustomChangePasswordDialog.prototype.reCreateInput = function(){
	var psView = this.setPasswordView;
	psView.oldPassword = domutils.findElementByClassName(psView.element, psView.cls_oldPassword);
	psView.newPassword = domutils.findElementByClassName(psView.element, psView.cls_newPassword);
	psView.confirmPassword = domutils.findElementByClassName(psView.element, psView.cls_confirmPassword);
	//重新定义确定按钮事件
	psView.doOk = function(){
		var oldPwd = psView.oldPassword.value;
		var newPwd = psView.newPassword.value;
		var confirmPwd = psView.confirmPassword.value;
		if(oldPwd==null){
			alert("${Pleaseenteryouroldpassword}.");
			return;
		}
		if(newPwd==null){
			alert("${Pleaseenternewpassword}.");
			return;
		}
		if(confirmPwd==null){
			alert("${PleaseentertheConfirmnewpassword}.");
			return;
		}
		// 不能使用manager作为新密码
		if (newPwd == "manager" || confirmPwd == "manager") {
			alert("&nbsp;${UserPasswordCannotBeDefault}!");
	        return false;
	    }
		if(newPwd != confirmPwd){
			alert("${Thenewpasswordandconfirmnewpassworddonotmatch}.");
			return;
		}
		
		// 判断密码是否满足复杂度要求
		var pwdComplexity = ".*";
		var pwdComplexityNotics = "";
		var ret = util.remoteInvokeEx("ConfigClientService", "getConfigLongValue",
				[ "USER_PASSWORD_COMPLEXITY" ]);
		if (ret && ret.result) {
			var objComplexity = lang.parseJSON(ret.result);
			pwdComplexity = objComplexity.rule;
			pwdComplexityNotics = objComplexity.notics;
			if (pwdComplexity.length > 0) {
				var pwdCheck = new RegExp(pwdComplexity);
				if (!pwdCheck.test(newPwd)) {
					alert("${Passwordinvalid}${RightSquareBracket}"+ pwdComplexityNotics + "${LeftSquareBracket}");
					return;
				}
			}
		}
		// 保存修改后的密码
		var ret = util.remoteInvoke("UserService", "changePassword", [oldPwd, newPwd]);
	 	if (ret.succeeded) {
	 		alert("${Modifypasswordsuccessfully}.");
	 		this.oldPassword.value = "";
	 		this.newPassword.value = "";
	 		this.confirmPassword.value = "";
	 	} else {
			alert(ret.result);
			return;
	 	}
	 	this.doClose(newPwd);
	}
}


<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<title>B-JUI 系统登录</title>
<meta name="renderer" content="webkit">
<script src="<%=basePath%>B-JUI/js/jquery-1.11.3.min.js"></script>
<script src="<%=basePath%>B-JUI/js/jquery.cookie.js"></script>
<link href="<%=basePath%>B-JUI/themes/css/bootstrap.min.css" rel="stylesheet">
<style type="text/css">
html, body { height: 100%; overflow: hidden; }
body {
    font-family: "Verdana", "Tahoma", "Lucida Grande", "Microsoft YaHei", "Hiragino Sans GB", sans-serif;
    background: url(<%=basePath%>images/loginbg_01.jpg) no-repeat center center fixed;
    background-size: cover;
}
.form-control{height:37px;}
.main_box{position:absolute; top:45%; left:50%; margin:-200px 0 0 -180px; padding:15px 20px; width:360px; height:400px; min-width:320px; background:#FAFAFA; background:rgba(255,255,255,0.5); box-shadow: 1px 5px 8px #888888; border-radius:6px;}
.login_msg{height:30px;}
.input-group >.input-group-addon.code{padding:0;}
#captcha_img{cursor:pointer;}
.main_box .logo img{height:35px;}
@media (min-width: 768px) {
    .main_box {margin-left:-240px; padding:15px 55px; width:480px;}
    .main_box .logo img{height:40px;}
}
</style>
<script type="text/javascript">
function member_login(form) {
    var $m = $('#j_username'), m = $.trim($m.val()),
        $s = $('#j_password'), s = $s.val(),
        $f = $(form)
    
    if (!m || !s) {
        Message.warn('账号和密码不能为空！')
        return false
    }
    
    $f.find(':submit').addClass('disabled').prop('disabled', true).text('登录中...')
    
    $.post('<%=path%>/page/managelogon', $f.serializeArray(), function(json) {
        if (json['statusCode'] == 200) {
            Message.success('登录成功！')
            if (json.message) {
                window.location.href = '<%=path%>/'+ json.message
            } else {
                window.location.href = '<%=path%>/member'
            }
        } else if (json['statusCode'] == 301) {
            Message.warn('账号未激活！')
            if (json.message) {
                window.location.href = '/'+ json.message
            }
        } else {
            Message.warn(json.message)
        }
        $f.find(':submit').removeClass('disabled').prop('disabled', false).text('确认登录')
    }, 'json')
    
    return false
}
</script>
</head>
<body>
<!--[if lte IE 7]>
<style type="text/css">
#errorie {position: fixed; top: 0; z-index: 100000; height: 30px; background: #FCF8E3;}
#errorie div {width: 900px; margin: 0 auto; line-height: 30px; color: orange; font-size: 14px; text-align: center;}
#errorie div a {color: #459f79;font-size: 14px;}
#errorie div a:hover {text-decoration: underline;}
</style>
<div id="errorie"><div>您还在使用老掉牙的IE，请升级您的浏览器到 IE8以上版本 <a target="_blank" href="http://windows.microsoft.com/zh-cn/internet-explorer/ie-8-worldwide-languages">点击升级</a>&nbsp;&nbsp;强烈建议您更改换浏览器：<a href="http://down.tech.sina.com.cn/content/40975.html" target="_blank">谷歌 Chrome</a></div></div>
<![endif]-->
<div class="container">
    <div class="main_box">
        <form action="<%=path%>/page/managelogon" id="login_form" method="post"  onsubmit="return member_login(this);">
            <p class="text-center logo"></p>
            <div class="login_msg text-center"><font color="red"></font></div>
            <div class="form-group">
                <div class="input-group">
                    <span class="input-group-addon" id="sizing-addon-user"><span class="glyphicon glyphicon-user"></span></span>
                    <input type="text" class="form-control" id="j_username" name="username" value="" placeholder="登录账号" aria-describedby="sizing-addon-user">
                </div>
            </div>
            <div class="form-group">
                <div class="input-group">
                    <span class="input-group-addon" id="sizing-addon-password"><span class="glyphicon glyphicon-lock"></span></span>
                    <input type="password" class="form-control" id="j_password" name="password" placeholder="登录密码" aria-describedby="sizing-addon-password">
                </div>
            </div>
            <div class="text-center">
                <button type="submit" id="login_ok" class="btn btn-primary btn-lg">&nbsp;登&nbsp;录&nbsp;</button>&nbsp;&nbsp;&nbsp;&nbsp;
                <button type="reset" class="btn btn-default btn-lg">&nbsp;重&nbsp;置&nbsp;</button>
            </div>
        </form>
    </div>
</div>
<script src="<%=basePath%>js/Message.js"></script>
</body>
</html>
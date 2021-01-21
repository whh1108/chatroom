<%--
  Created by IntelliJ IDEA.
  User: wwh
  Date: 2021/1/11
  Time: 23:46
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>聊天室登录</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-1.11.3.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/lbt.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css" type="text/css" media="all" />
</head>
<body>

    <!--登陆区域开始-->
    <div class="loginMain">
    <%--    <div class="con">
            <div class="inCon">
                <ul class="imgList">
                    <li><img src="${pageContext.request.contextPath}/resources/images/1.png" width="680" height="494" /></li>
                    <li><img src="${pageContext.request.contextPath}/resources/images/2.png" width="680" height="494" /></li>
                    <li><img src="${pageContext.request.contextPath}/resources/images/3.png" width="680" height="494" /></li>
                    <li><img src="${pageContext.request.contextPath}/resources/images/4.png" width="680" height="494" /></li>
                    <li><img src="${pageContext.request.contextPath}/resources/images/5.png" width="680" height="494" /></li>
                    <li><img src="${pageContext.request.contextPath}/resources/images/6.png" width="680" height="494" /></li>
                    <li><img src="${pageContext.request.contextPath}/resources/images/7.png" width="680" height="494" /></li>
                </ul>
            </div>
            <ol class="btnList">
                <li class="current"></li>
                <li></li>
                <li></li>
                <li></li>
                <li></li>
                <li></li>
                <li></li>
            </ol>
            <a href="javascript:;" class="left"></a>
            <a href="javascript:;" class="right"></a>
        </div>--%>


        <div class="loginArea">
            <h2>欢迎登陆</h2>
            <p>欢迎您来到聊天空间！</p>
            <div><font color="red" size="16">${requestScope.errorTips }</font></div>
            <form action="${pageContext.request.contextPath }/chat/login"  method="post">
                <input type="text" name="nickname" id="myText" placeholder="请输入账号"/>
                <input type="text" name="password" id="password" placeholder="请输入密码"/>
                <button>进入聊天室</button>
            </form>
            <form action="${pageContext.request.contextPath }/chat/registerfw"  method="post">
                <button>注册</button>
            </form>
        </div>
    </div>
    <!--登陆区域结束-->

</body>
</html>

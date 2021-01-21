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
    <title>聊天室注册</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-1.11.3.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/lbt.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/register.css" type="text/css" media="all" />
</head>
<body>
<div class="loginArea">
    <h2>注册</h2>
    <p>请输入账号名以及密码</p>
    <div><font color="red" size="16">${requestScope.errorTips }</font></div>
    <form action="${pageContext.request.contextPath }/chat/register"  method="post">
        <input type="text" name="nickname" id="myText" placeholder="请输入账号"/>
        <input type="text" name="password" id="password" placeholder="请输入密码"/>
        <button>注册</button>
    </form>
</div>

</body>
</html>

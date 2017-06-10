<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title>Search facts</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css" integrity="sha384-rwoIResjU2yc3z8GV/NPeZWAv56rSmLldC3R/AZzGRnGxQQKnKkoFVhFQhNUwEyJ" crossorigin="anonymous">
    <style type="text/css">
        <%@ include file="mainSearchFacts.css" %>
        .center {
            text-align:  center;
        }
    </style>

</head>
<body>

<form class="form-wrapper" method="get" action="/">

    <input name="expression" type="text" id="search" placeholder="Please enter the query" required>
    <input type="submit" value="go" id="submit">
</form>

<c:if test="${expressionViewJSP != null}">
    <div class="center">
    <c:if test="${answerViewJSP == null}">
        <p><code>К сожалению, по запросу '${expressionViewJSP}' не было найдено фактов.
        Попробуйте скорректировать запрос.</code></p>
    </c:if>
    <c:if test="${answerViewJSP != null}">
        <p><mark>${expressionViewJSP}: ${answerViewJSP}</mark></p>
    </c:if>
    </div>
</c:if>

</body>
</html>

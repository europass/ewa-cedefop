<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>LinkedIn Callback URL</title>
    </head>
    <body>

        <%
            String userAgent = request.getHeader("User-Agent");

            String reload = "";
            if(userAgent.indexOf("MSIE 9.0") > -1 || ( userAgent.indexOf("iOS") > -1 && userAgent.indexOf("Chrome") > -1) ) {
                    reload = "window.opener.location.reload( true );";
            }
        %>

        <script>
            (function () {
                var redirectURI = "<%=request.getParameter("uri")%>";
                console.log("redirectURI: " + redirectURI);
                window.opener.location.href = redirectURI;
            <%=reload%>
            })();
        </script>
        <script>
            (function () {
                window.close();
            })();
        </script>
    </body>
</html>
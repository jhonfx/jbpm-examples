<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
 
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>

        <form name="newProcess" action="TaskProcessServlet">
            <table  >
                <thead>
                    <tr>
                        <th colspan="2">Process Request</th>
                        
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Create a new process:</td>
                        <td><input type="submit"  name="action"  value="CreateProcess" /></td>
                    </tr>
                   
                     <tr>
                        <td colspan="2">Result: <%
        if(request.getAttribute("message")!=null){
            out.println("Action = "+request.getAttribute("message"));
        }
        %></td>
                    </tr>
                     <tr>
                        <td colspan="2"><a href="index.jsp">Back</a></td>

                    </tr>
                </tbody>
            </table>
          
        </form>
        
    </body>
</html>

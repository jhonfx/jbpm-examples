
<%@page contentType="text/html" import="java.util.*,java.lang.*,com.sample.processserver.*,org.drools.runtime.process.WorkflowProcessInstance,org.drools.runtime.*,com.sample.processserver.*,org.jbpm.task.query.TaskSummary" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
   
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <form name="taskList" action="TaskProcessServlet">
            <select name="user">
                <option value="krisv">Kris</option>
                <option value="john">John</option>
            </select>
            <input type="hidden" name="action" value="listtasks">
            <input type="submit" value="Select User" name="selectUser" />
        </form>
        <table border="1">
            <tr>
                <td>Task Id</td>
                <td>Task Name</td>
                <td>Task Status</td>
                <td>Task Owner</td>
                <td>Task Action</td>
            </tr>
         <%
         
         
	    			
            List<TaskSummary> tIS = (List<TaskSummary>)request.getAttribute("tasks");
            if(tIS !=null){
            
	    for (TaskSummary taskSummary : tIS) { 
	      if(!(taskSummary.getStatus().toString()).equals("Completed"))
	        {
                     out.println("<tr>");
                     out.println("<td>");
                     out.println(taskSummary.getId());
                     out.println("</td>");
                     out.println("<td>");
	                 out.println(taskSummary.getName());
                     out.println("</td>");
                     
                     out.println("<td>");
	                 out.println(taskSummary.getStatus());
                     out.println("</td>");
                     
                     out.println("<td>");
                     out.println(taskSummary.getActualOwner());
                     out.println("</td>");	             
                     
                     out.println("<td>");
                   
                     out.println("<a href='TaskProcessServlet?action=taskinit&taskId="+taskSummary.getId()+"&taskStatus="+taskSummary.getStatus()+"&processId="+taskSummary.getProcessInstanceId()+"&user="+request.getAttribute("user")+"'>Work on Task</a>");
                     out.println("</td>");
                     
                     out.println("</tr>");
	     }    
	    			}
	    			
            }
         %>
         <tr>
                <td colspan="4"><a href="index.jsp">Back</a></td>
            </tr>
        </table>
    </body>
</html>

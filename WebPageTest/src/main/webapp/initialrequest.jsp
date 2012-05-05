
<%@page contentType="text/html" import="java.util.*,org.drools.runtime.*,java.lang.*,com.sample.ProcessServer.*,org.drools.runtime.process.WorkflowProcessInstance,org.jbpm.task.query.TaskSummary" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Initial Request</title>
    </head>
    <body>
     
      <% 
     StatefulKnowledgeSession ksession = JbpmConnector.getSession();
         Long processId = new Long((String)request.getAttribute("processId"));
	WorkflowProcessInstance process = (WorkflowProcessInstance)ksession.getProcessInstance(processId);
        %>
        
     <form name="taskForm" action="ProcessTaskServlet"> <br>
         <input type="hidden"  name="user" value="<%=request.getAttribute("user")  %>" /> <br>
	 <input type="hidden" name="taskId" value="<%=request.getAttribute("taskId")  %>" /> <br>
         <input type="hidden" name="taskStatus" value="<%=request.getAttribute("taskStatus")  %>" />
            
	   		 Priority: <input  name="priority" value="<%=process.getVariable("priority")  %>" /> <br>

            Model Number: <input  name="modelNumber" value="<%=process.getVariable("modelNumber")  %>" />  <br>
            Quantity ( 1 to 1000): <input  name="quantity" value="<%=process.getVariable("quantity")  %>" /><br>
            Amount: <input  name="amt" value="<%=process.getVariable("amt")  %>" /><br><br>
           
            <input type="submit" value="Submit" name="action" />  <br>

        </form>
        

      
    </body>
</html>

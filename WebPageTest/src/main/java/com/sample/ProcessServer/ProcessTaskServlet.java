package com.sample.ProcessServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.ProcessBuilderFactory;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.marshalling.impl.ProcessMarshallerFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.process.workitem.wsht.WSHumanTaskHandler;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;

import antlr.collections.List;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

public class ProcessTaskServlet extends HttpServlet {

	 private static Log log = LogFactory.getLog(ProcessTaskServlet.class);

	 public void init() throws ServletException {

	        super.init();

	        try {
	        	
	        	UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
	            ut.begin();
	            StatefulKnowledgeSession ksession = JbpmConnector.getSession();
				
		        /*Create the knowledge base  */
		       
				KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newThreadedFileLogger(ksession, "testlog", 1000);
				CommandBasedWSHumanTaskHandler taskHandler = new CommandBasedWSHumanTaskHandler(ksession);
				ksession.getWorkItemManager().registerWorkItemHandler("Human Task", taskHandler);
				taskHandler.connect();
				ut.commit();
				
	            
				logger.close();
			} catch (Throwable t) {
				log.error(t.getMessage(), t.getCause());
				throw new RuntimeException("error while creating session",t);
			}
			
	 }
	 
	 protected void processRequest(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	        response.setContentType("text/html;charset=UTF-8");
	        PrintWriter out = response.getWriter();

	        try {
	            String action = request.getParameter("action");
	           
	            if (action.equals("CreateProcess")) {
	               
	            	UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
		            ut.begin();
	            	StatefulKnowledgeSession ksession = JbpmConnector.getSession();
	            	
	            	Map<String, Object> params = new HashMap<String, Object>();
	    			params.put("priority", "High");
	    			params.put("modelNumber", "123"); 
	    			params.put("quantity", "66");
	    			params.put("amt",12000);
	    			
	            	
		          	ksession.startProcess("HumanTaskExternalUI",params);	
		          	
					ksession.fireAllRules();
					ut.commit();
		            
	                request.setAttribute("message", "Process Created!");
	                RequestDispatcher rD = request.getRequestDispatcher("adminuser.jsp");
	                rD.forward(request, response);
	            }
	            else if(action.equals("listtasks")) {
	            	String user = request.getParameter("user");
	            	
	            	//UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
	    			/*
	    			 * Get all the task assigned to 'user'
	    			 */
	                
	    			java.util.List<TaskSummary> tasks = JbpmConnector.getAssignedTasks(user);
	    			TaskSummary task = null;  
	    			
	    		
	    			BlockingTaskOperationResponseHandler responseHandler = null;
	    			ContentData contentData = null;
	    			Map data = new HashMap();
	    			
	    			for (TaskSummary taskSummary : tasks) {   
	    			System.out.println(taskSummary.getId() + " : "     + taskSummary.getActualOwner()); 
	    			}
	    			
	    			request.setAttribute("tasks",tasks);
	    			request.setAttribute("user",user);
	                RequestDispatcher rD = request.getRequestDispatcher("taskrequest.jsp");
	                rD.forward(request, response);
	    			
	            }
	            
	            else if(action.equals("Submit")) {
	            	
	            	UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
	            	 ut.begin();
	            	String user = request.getParameter("user");
	            	/*
	    			 * Get all the task assigned to 'user'
	    			 */
	            	 Map data = new HashMap();
	            	 data.put("priority",request.getParameter("priority"));
	            	 data.put("modelNumber",request.getParameter("modelNumber"));
	            	 data.put("quantity",request.getParameter("quantity"));
	            	 data.put("amt", request.getParameter("amt"));
	            	long taskId = new Long(request.getParameter("taskId")).longValue();
	            	String taskStatus = request.getParameter("taskStatus");
	            	
	            	if (!taskStatus.equals("Completed"))
	    			JbpmConnector.completeTask(taskId, data, user);
	    			
	            	
	    			ut.commit();
	    			
	    			//request.setAttribute("tasks",tasks);
	                RequestDispatcher rD = request.getRequestDispatcher("index.jsp");
	                rD.forward(request, response);		
	            }
	            else if(action.equals("taskinit")){
	            	
	            	
	            	request.setAttribute("taskId",request.getParameter("taskId"));
	            	request.setAttribute("taskStatus",request.getParameter("taskStatus"));
	            	request.setAttribute("user",request.getParameter("user"));
	            	request.setAttribute("processId",request.getParameter("processId"));
	            	
	            	
	                RequestDispatcher rD = request.getRequestDispatcher("initialrequest.jsp");
	                rD.forward(request, response);		
	            }
	        } catch(Exception e){
	            out.println("Error:"+ e.getMessage().toString());
	        }
	        finally {
	            out.close();
	        }
	        
	    } 
		 
	 
			 public void destroy() {
				    super.destroy();
				  }

				  protected void doGet(HttpServletRequest request, HttpServletResponse response)
				    throws ServletException, IOException
				  {
					  processRequest(request, response);
				    
				  }

				  protected void doPost(HttpServletRequest request, HttpServletResponse response)
				    throws ServletException, IOException
				  {
					  processRequest(request, response);
				     
				  }
}

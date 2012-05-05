package com.sample.taskserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.SystemEventListenerFactory;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;

import com.sample.processserver.TaskProcessServlet;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class HumanTaskStartupServlet extends HttpServlet {

	 private static Log log = LogFactory.getLog(HumanTaskStartupServlet.class);
	 
	 public void init() throws ServletException {

	        super.init();
	        
	        /*
			 * Start local h2 datbase
			 * This is not required if the application connects to a remote database
			 */
		
			
			try {
				
				DeleteDbFiles.execute("", "JPADroolsFlow", true);
				 Server h2Server = Server.createTcpServer(new String[0]);
				h2Server.start();
			} catch (SQLException e) {
				log.error(e.getMessage(), e.getCause());
				throw new RuntimeException("can't start h2 server db",e);
			}
			
	        

	       
			try {
				
				
				
	            
			EntityManagerFactory emfTask = Persistence.createEntityManagerFactory( "org.jbpm.task" );
			TaskService taskService = new TaskService(emfTask, SystemEventListenerFactory.getSystemEventListener());
			
			
			
			/*
	         * Add the required users 
	         */
	        TaskServiceSession taskSession = taskService.createSession();   
	        taskSession.addUser(new User("Administrator"));
	        taskSession.addUser(new User("krisv")); 
	        taskSession.addUser(new User("john"));
	        taskSession.addUser(new User("mary"));
	        
	        
	        /* Start Mina server for HT*/
	        MinaTaskServer server = new MinaTaskServer(taskService);
	        Thread thread = new Thread(server);
	        thread.start();
	       
	        log.debug("Mina Server started ..."); 
			} catch (Throwable t) {
				log.error(t.getMessage(), t.getCause());
				throw new RuntimeException("can't start Mina server",t);
				
			}
			
	 }
		 
			 public void destroy() {
				    super.destroy();
				  }

				  protected void doGet(HttpServletRequest request, HttpServletResponse response)
				    throws ServletException, IOException
				  {
				    doPost(request, response);
				  }

				  protected void doPost(HttpServletRequest reqquest, HttpServletResponse response)
				    throws ServletException, IOException
				  {
				    response.sendError(1001, "POST Method Not Allowed Here");
				  }
}

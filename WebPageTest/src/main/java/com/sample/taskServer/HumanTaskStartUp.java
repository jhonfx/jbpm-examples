package com.sample.taskServer;

import java.io.IOException;
import java.sql.SQLException;

import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.SystemEventListenerFactory;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.jboss.seam.persistence.EntityManagerFactory;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.slf4j.LoggerFactory;

public class HumanTaskStartUp extends HttpServlet{
	
	private static Log log = LogFactory.getLog(HumanTaskStartUp.class);
	public void init() throws ServletException{
	super.init();
	
	try
	{
		DeleteDbFiles.execute("", "JPADroolsFlow", true);
		Server h2Server = Server.createTcpServer(new String[0]);
		h2Server.start();
	}
	catch(SQLException e)
	{
		log.error(e.getMessage(),e.getCause());
		throw new RuntimeException("can't start h2 server db",e);
	}
	try
	{
		javax.persistence.EntityManagerFactory emfTask = Persistence.createEntityManagerFactory("org.jbpm.task");
		TaskService taskService = new TaskService(emfTask, SystemEventListenerFactory.getSystemEventListener());
		
		//Add the required users.
		TaskServiceSession taskSession = taskService.createSession();
		taskSession.addUser(new User("krisv"));
		taskSession.addUser(new User("john"));
		taskSession.addUser(new User("mary"));
		
		// start the mina server for HT
		MinaTaskServer server = new MinaTaskServer(taskService);
		Thread thread = new Thread(server);
		thread.start();
		log.debug("Mina server started...............");
	}
	catch(Throwable t){
		log.error(t.getMessage(), t.getCause());
		throw new RuntimeException("can't start the mina server....");
	}
	}

	
	public void destroy()
	{
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

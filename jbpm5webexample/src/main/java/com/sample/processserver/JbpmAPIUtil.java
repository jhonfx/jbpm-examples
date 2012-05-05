package com.sample.processserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
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
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.task.AccessType;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

import bitronix.tm.TransactionManagerServices;

public class JbpmAPIUtil {

	private static String ipAddress = "127.0.0.1";
	private static int port = 9123;
	private static TaskClient client;
	private static Map<String, List<String>> groupListMap = new HashMap<String, List<String>>();
	private static StatefulKnowledgeSession ksession;
	
	
	public  void setConnection(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	/*
	 * Connect to Mina client
	 */
	public static void connect() {
		if (client == null) {
			client = new TaskClient(new MinaTaskClientConnector("org.drools.process.workitem.wsht.WSHumanTaskHandler",
									new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
			boolean connected = client.connect(ipAddress, port);
			if (!connected) {
				throw new IllegalArgumentException(
					"Could not connect task client");
			}
		}
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URL url = null;
			String propertyName = "roles.properties";

			if (loader instanceof URLClassLoader) {
				URLClassLoader ucl = (URLClassLoader) loader;
				url = ucl.findResource(propertyName);
			}
			if (url == null) {
				url = loader.getResource(propertyName);
			}
			if (url == null) {
				System.out.println("No properties file: " + propertyName + " found");
			} else {
				Properties bundle = new Properties();
				InputStream is = url.openStream();
				if (is != null) {
					bundle.load(is);
					is.close();
				} else {
					throw new IOException("Properties file " + propertyName	+ " not available");
				}
				Enumeration<?> propertyNames = bundle.propertyNames();
				while (propertyNames.hasMoreElements()) {
					String key = (String) propertyNames.nextElement();
					String value = bundle.getProperty(key);
					groupListMap.put(key, Arrays.asList(value.split(",")));
					System.out.print("Loaded user " + key + ":");
					for (String role: groupListMap.get(key)) {
						System.out.print(" " + role);
					}
					System.out.println();
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}
	
	/*
	 * Get all the tasks assigned to a user
	 */
	public static List <TaskSummary> getAssignedTasks(String idRef) {
		connect();
		List<TaskSummary> tasks = null;
		try {
			BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
			client.getTasksOwned(idRef, "en-UK", responseHandler);
	        tasks = responseHandler.getResults();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return tasks;
	}
	
        /*
	 * Complete a task with a taskid and data for a user 
	 */
	public static void completeTask(long taskId, Map data, String userId) throws InterruptedException {
		connect();
		
		BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
		client.start(taskId, userId, responseHandler);
		responseHandler.waitTillDone(5000);
		//Thread.sleep(10000);
		responseHandler = new BlockingTaskOperationResponseHandler();
		ContentData contentData = null;
		if (data != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out;
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(data);
				out.close();
				contentData = new ContentData();
				contentData.setContent(bos.toByteArray());
				contentData.setAccessType(AccessType.Inline);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		client.complete(taskId, userId, contentData, responseHandler);
		responseHandler.waitTillDone(5000);
	}
	

/*
	 * This is similar to 'completeTask' method, but to complete a task that is in 'Progress' state.
	 *  In this case client start method is not called
	 */
	
	public static void completeProgressTask(long taskId, Map data, String userId) throws InterruptedException {
		connect();
		
		BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
		responseHandler.waitTillDone(5000);
		//Thread.sleep(10000);
		responseHandler = new BlockingTaskOperationResponseHandler();
		ContentData contentData = null;
		if (data != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out;
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(data);
				out.close();
				contentData = new ContentData();
				contentData.setContent(bos.toByteArray());
				contentData.setAccessType(AccessType.Inline);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		client.complete(taskId, userId, contentData, responseHandler);
		responseHandler.waitTillDone(5000);
	}
	
	/*
	 * Assign the task to a user 
	 */
	public void assignTask(long taskId, String idRef, String userId) {
		connect();
		BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
		if (idRef == null) {
			client.release(taskId, userId, responseHandler);
		} else if (idRef.equals(userId)) {
			List<String> roles = groupListMap.get(userId);
			if (roles == null) {
				client.claim(taskId, idRef, responseHandler);
			} else {
				client.claim(taskId, idRef, roles, responseHandler);
			}
		} else {
			client.delegate(taskId, userId, idRef, responseHandler);
		}
		responseHandler.waitTillDone(5000);
	}
	
	/*
	 * Load the bpmn file into knowledgebase
	 */
	public static KnowledgeBase readKnowledgeBase(String process) throws Exception {
		ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
		ProcessMarshallerFactory.setProcessMarshallerFactoryService(new ProcessMarshallerFactoryServiceImpl());
		ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
		BPMN2ProcessFactory.setBPMN2ProcessProvider(new BPMN2ProcessProviderImpl());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}

	
	
	
	
	
	
	
	public static StatefulKnowledgeSession getSession() throws Exception {
		if (ksession == null) {
			ksession = createSession();
		}
		return ksession;
	}
	
	
	/*
	 * Create EntityManagerFactory and register it in the environment
	 * Create the knowledge session that uses JPA to persists runtime state
	 */
	
	public static StatefulKnowledgeSession createSession() throws Exception {
		
		/*
		 * Create the knowledgebase using the required bpmn and drl files
		 */
		KnowledgeBase kbase = readKnowledgeBase("SampleHumanTaskFormVariables.bpmn");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.jbpm.persistence.jpa" );
		Environment env = KnowledgeBaseFactory.newEnvironment();
		env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );
		env.set( EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager() );
		env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );
		
		Properties properties = new Properties();
		properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
		KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
	
		return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
			
	}
	
}

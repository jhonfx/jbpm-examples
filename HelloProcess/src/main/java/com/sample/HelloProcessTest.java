package com.sample;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.ProcessBuilderFactory;
import org.drools.impl.EnvironmentFactory;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.marshalling.impl.ProcessMarshallerFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.process.workitem.wsht.WSHumanTaskHandler;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;

/**
 * This is a sample file to launch a process.
 */
public class HelloProcessTest {

	public static final void main(String[] args) {
		try {
			
			
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.task");
	        TaskService taskService  = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
	        
	        /*
	         * Add the required users 
	         */
	        TaskServiceSession taskSession = taskService.createSession();   
	        taskSession.addUser(new User("Administrator"));
	        taskSession.addUser(new User("krisv")); 
	        
	        /* Start Mina server for HT*/
	        MinaTaskServer server = new MinaTaskServer(taskService);
	        Thread thread = new Thread(server);
	        thread.start();
	        System.out.println("Server started ..."); 
	       
	        /*Create the knowledge base for HelloProcss.bpmn */
	        KnowledgeBase kbase = readKnowledgeBase();
			StatefulKnowledgeSession ksession = createSession(kbase);
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newThreadedFileLogger(ksession, "testlog", 1000);
			
			ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new WSHumanTaskHandler());
			//ProcessTest pt = new ProcessTest();
			//ksession.insert(pt);
			
			System.out.println(" started the humantask");
			ksession.startProcess("Hello");
		
			
	        
	        logger.close();
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
		ProcessMarshallerFactory.setProcessMarshallerFactoryService(new ProcessMarshallerFactoryServiceImpl());
		ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
		BPMN2ProcessFactory.setBPMN2ProcessProvider(new BPMN2ProcessProviderImpl());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("HelloProcess.bpmn"), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}

	private static StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
		Properties properties = new Properties();
		properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
		KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
		return kbase.newStatefulKnowledgeSession(config, EnvironmentFactory.newEnvironment());
	}
	
}
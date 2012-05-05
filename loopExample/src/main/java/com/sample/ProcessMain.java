package com.sample;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.ProcessBuilderFactory;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.marshalling.impl.ProcessMarshallerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;

import java.util.*;

public class ProcessMain {

	/**
	 * @param args
	 */
	public static final void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
			ProcessMarshallerFactory.setProcessMarshallerFactoryService(new ProcessMarshallerFactoryServiceImpl());
			ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
			BPMN2ProcessFactory.setBPMN2ProcessProvider(new BPMN2ProcessProviderImpl());
			KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add(ResourceFactory.newClassPathResource("looptest.bpmn"), ResourceType.BPMN2);
			kbuilder.add(ResourceFactory.newClassPathResource("LoopConditionRules.drl"), ResourceType.DRL);
			KnowledgeBase kbase = kbuilder.newKnowledgeBase();			
			StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
			Map<String, Object> params = new HashMap<String, Object>();
			/*HelloProcessModel hpm = new HelloProcessModel();
			hpm.setCount(new Integer("3"));
			hpm.setUserlocation("NewYorkUser");*/
			params.put("count", 1);
			params.put("loopcondition", 3);
			ksession.startProcess("looptestdemo", params);
			//ksession.fireAllRules();
			logger.close();
		} catch (Throwable e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		

	}

}

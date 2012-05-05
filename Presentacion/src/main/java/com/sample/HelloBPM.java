package com.sample;

import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class HelloBPM {
	public static final void main(String[] args) {
		Hour hour = new Hour(6);
		Integer today  = hour.getValue();
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("HelloBPM.bpmn"), ResourceType.BPMN2);		
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		Map<String, Object> dato = new HashMap<String, Object>();
		dato.put("today", today);		
		ksession.startProcess("com.sample.bpmn.example1", dato);
		ksession.fireAllRules();
		
		//ksession.dispose();
	}

}

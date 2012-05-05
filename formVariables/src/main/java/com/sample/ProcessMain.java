package com.sample;

import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.compiler.BPMN2ProcessProvider;
import org.drools.compiler.BaseKnowledgeBuilderResultImpl;

/**
 * This is a sample file to launch a process.
 */
public class ProcessMain {

	public static final void main(String[] args) throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("usuarioedad.drl"), ResourceType.DRL);
		// load up the knowledge base
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();		
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		// start a new process instance
		Map<String, Object> params = new HashMap<String, Object>();
		Usuario usuario = new Usuario();
		usuario.setEdad(33);
		params.put("usuario", usuario.getUsuario());
		params.put("edad", usuario.getEdad());
		ksession.startProcess("com.sample.bpmn.hello", params);
		ksession.fireAllRules();
		ksession.dispose();
	}
}
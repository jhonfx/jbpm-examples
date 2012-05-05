package com.sample;

public class ListaNombres {
	
	public ListaNombres(){
		System.out.println("Lista de nombres:");		
	}
	
	public String[] mostrarNombres(){
		String [] nombres = {"Juan","Paco","Rafa","Daniel","Sergio","Mario"};		
		for(String n : nombres) {
			System.out.println(n);
		}
		return nombres;
	}
}

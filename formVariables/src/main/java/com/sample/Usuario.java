package com.sample;

public class Usuario {
	
	String usuario = "paco";
	int edad = 20;
	
	public Usuario(){}
	
	void setUsuario(String usuario){
		this.usuario = usuario;
	}
	
	public String getUsuario(){
		return usuario;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public int getEdad() {
		return edad;
	}	

}

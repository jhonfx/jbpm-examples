package com.sample;

public class Alumno {
	
	private String nombre;
	private String edad;
	private String grado;
	private String grupo;
	private String materia;
	private String cal;
	
	
	public Alumno(){
		System.out.println("Nuevo alumno:");
	}
	
	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getEdad() {
		return edad;
	}


	public void setEdad(String edad) {
		this.edad = edad;
	}


	public String getGrado() {
		return grado;
	}


	public void setGrado(String grado) {
		this.grado = grado;
	}


	public String getGrupo() {
		return grupo;
	}


	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}


	public String getMateria() {
		return materia;
	}


	public void setMateria(String materia) {
		this.materia = materia;
	}


	public String getCal() {
		return cal;
	}


	public void setCal(String cal) {
		this.cal = cal;
	}
}

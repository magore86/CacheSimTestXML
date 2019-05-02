package controller;

import java.util.HashMap;

import io.ActualInstruction_x86;

public class Procedure {
	
	private String name;
	private HashMap<String,ActualInstruction_x86> instructions;
	private boolean random;
	
	public Procedure(String name, HashMap<String,ActualInstruction_x86> instructions, boolean random)
	{
		this.name=name;
		this.instructions=instructions;
		this.random=random;
	}

}

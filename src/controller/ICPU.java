package controller;

import java.util.LinkedList;

//Connecting CPU to controller, filereader and bus
public interface ICPU {
	
	public LinkedList<CPUController> cpu_controllers=new LinkedList<CPUController>();
}

package mainmemory;

import java.util.LinkedList;

import controller.MainController;

public interface IMC {
	//Connecting main controller to all controllers and buses
	public LinkedList<MainController> main_controllers=new LinkedList<MainController>();

}

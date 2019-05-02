package controller;



import mainmemory.*;


public class BaseController implements IBus,IMC,ICPU{
	
	private CPUController cpu_controller;
	/* A control class, no intermediate methods. Initialization class*/
	public BaseController()
	{
		cpu_controller = new CPUController();
		cpu_controllers.add(cpu_controller);
		Bus bus=new Bus();
		buses.add(bus);
		MainController main_controller = new MainController();
		main_controllers.add(main_controller);
	}
	public PerformanceMetric run()
	{
		
		PerformanceMetric pm =  cpu_controller.run();
		return pm;
	}
	public void init_controllers(boolean sepregs,boolean en_mm,boolean prefetch,int number_of_levels,int number_of_CPUs,int[] capacity, int[] blocksize, int[] associativity, int address_length,int incl_policy, String path_name) {
		
		cpu_controller.set_param(sepregs,en_mm,prefetch,number_of_levels,number_of_CPUs,incl_policy,capacity,blocksize,associativity,address_length,path_name);
		
	}
	public MainController get_main_controller()
	{
		return main_controllers.peekFirst();
	}
	public Bus get_bus()
	{
		return buses.peekFirst();
	}
	public CPUController get_cpu_controller()
	{
		return cpu_controllers.peekFirst();
	}
	public void clear() 
	{
		cpu_controllers.clear();
		buses.clear();
		main_controllers.clear();
		
	}
	

}

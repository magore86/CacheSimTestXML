package hardware;

import java.util.LinkedList;

import controller.*;


import io.Register_x86;
import mainmemory.Bus;
import mainmemory.IMC;


public class CPU implements IBus,IMC {


	private LinkedList<CPUProcess> processes;
	private int number_of_levels;
	private int[] subtype_numbers;
	
	private CacheController cache_controller;
	private Bus bus;
	private MainController main_controller;
	
	
	
	
	/*CPU, intermediate methods and some R/W methods */
	public CPU(int cpu_no,int number_of_levels,int incl_policy,int[] capacity,int[] blocksize,int[] associativity, int address_length)
	{
		set_bus_mc();
 		processes=new LinkedList<CPUProcess>();
 		
 		this.cache_controller=new CacheController(cpu_no,number_of_levels,incl_policy,capacity, blocksize,associativity, address_length);
 		this.number_of_levels=number_of_levels;
 		
 		
 		
	}
	public void set_bus_mc()
	{
		bus=buses.peekFirst();
		main_controller=main_controllers.peekFirst();
	}
	public LinkedList<CPUProcess> get_process_list()
	{
		return processes;
	}
	public int processes_length()
	{
		return processes.size();
	}
	public void write_MESIF(short cache_lvl,boolean[] bin_address, short[] hex_address, short[] data,int length)
	{
		cache_controller.write_slot_ME(bin_address, hex_address,data);
	}
	public void write(boolean[] bin_addr,short[] hex_addr,short[] data)
	{
		cache_controller.write_slot_ME(bin_addr,hex_addr,data);
	}
	public LinkedList<Entry> read(int cache_lvl, short[] hex_address, boolean[] bin_address, short data_length,boolean prefetch) {
		
		return cache_controller.read(cache_lvl, data_length, bin_address, hex_address, prefetch);
		
	}
	public int[] count_types_of_misses() 
	{
		subtype_numbers=new int[4];
	
		LinkedList<Miss> misses=cache_controller.get_misses();
		for(Miss m:misses)
		{
			subtype_numbers[m.get_sub_type()]++;
		}
		
		return subtype_numbers;
	}
	public int[] get_subtypes_numbers()
	{
		return subtype_numbers;
	}
	public int[] get_no_of_hits() {
		
		return cache_controller.get_no_of_hits();
	}
	public int[] get_no_of_misses() {
		return cache_controller.get_no_of_misses();
	}
	public LinkedList<Slot> find_slots(boolean[] start_bin_addr, LinkedList<Slot> slots, int length) {
		
		Slot slot=null;
		
		for(int i=0;i<number_of_levels;i++)
		{
			slot=cache_controller.get_slot(i, start_bin_addr);
			
			if(slot!=null)
			{		
				if(!slot.is_invalid())
				{
					if(slot.is_exclusive() || slot.is_forwarding() || slot.is_modified())
					{
						slots.add(slot);
						break;
					}
					else
					{
						slots.add(slot);
					}
				}
			}
		}
		return slots;
	}
	public void invalidate_slot(boolean[] address) {
		cache_controller.invalidate_slot(address);		
	}
	public void flush(boolean[] start_bin_addr, short[] start_hex_addr, Slot valid_slot)
	{
		cache_controller.flush(start_bin_addr,start_hex_addr,valid_slot);	
	}
	public LinkedList<Slot> change_F(boolean[] bin_addr,LinkedList<Slot> list) {
		
		return cache_controller.change_F(bin_addr,list);
	}
	public LinkedList<Slot> change_E(boolean[] bin_addr, LinkedList<Slot> list) 
	{
		return cache_controller.change_F(bin_addr,list);
	}
	public void clear_caches() {

		cache_controller.clear();
		cache_controller=null;
		
	}
	
}

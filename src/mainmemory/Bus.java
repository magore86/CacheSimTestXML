package mainmemory;

import java.util.LinkedList;

import controller.MainController;
import hardware.CPU;
import hardware.Entry;
import hardware.Slot;

public class Bus implements IMC{
	

	//only one bus in use at the time
	

	private int number_of_CPUs;
	private MainController main_controller;
	/*Intermediate class: Connects cache controller to main controller */
	public Bus() //Empty constructor, just a method class
	{
		
	}
	public void set_param(int number_of_CPUs, int address_length, int number_of_levels) {
		
		this.number_of_CPUs=number_of_CPUs;
		main_controller=main_controllers.peekFirst();
	}
	public void write_MESIF(int cpu_no,int cache_lvl,Entry entry, boolean[] address)
	{
		write_MESIF(cache_lvl,cpu_no, entry, address);
	}
	public Entry read_MESIF(int cpu_no,int cache_lvl, boolean[] address)
	{
		return read_MESIF(cache_lvl,cpu_no, address);
	}
	public void write_back(short[] hex_address,boolean[] bin_address, short data) {
		main_controller.write_back(bin_address,hex_address,data);
		
	}
	public void write_around(boolean[] address,Entry entry)
	{
		write_around(address,entry);
	}
	
	public boolean[] inc_bin_address(boolean[] next_bin_address) {
		
		return main_controller.inc_address(next_bin_address);
	}
	public short[] get_next_hex_address() {
		
		return main_controller.get_next_hex_address();
	}
	public void inc_address(int length) 
	{
		main_controller.inc_address(length);
	}
	public boolean[] get_next_bin_address() {
	
		return main_controller.get_next_bin_address();
	}
	public short[] inc_hex_address(short[] address) {
		return main_controller.inc_hex_address(address);
	}
	public void write_to_memory(short[] hex_address, boolean[] bin_address, short data) {
		main_controller.write_to_memory(hex_address, bin_address, data);
		
	}
	public short read_from_memory(boolean[] bin_address,short[] hex_address) {
		return main_controller.read_from_memory(hex_address,bin_address);	
	}
	public short[] read_from_memory_3(short[] hex_addr,boolean[] bin_addr, int length) {
		short data[] = new short[length];
		data =main_controller.read_from_memory_3(hex_addr,bin_addr,length);
		return data;
	}
	public void write_to_memory(short[] start_hex_addr, boolean[] start_bin_addr, short[] data) {
		main_controller.write_to_memory(start_hex_addr, start_bin_addr, data);
		
	}
	public LinkedList<Slot> find_slots(boolean[] start_bin_addr,int length) {
		LinkedList<Slot> slots = new LinkedList<Slot>();
		for(int i=0;i<number_of_CPUs;i++)
		{
			slots=main_controller.find_slots(i,length,start_bin_addr,slots);
			
		}
		
		return slots;
		
	}
	public void invalidate_slot(boolean[] address) {
		for(int i=0;i<number_of_CPUs;i++)
		{
				main_controller.invalidate_slot(i,address);
		}
		
	}
	public void flush(Slot valid_slot, boolean[] start_bin_addr, short[] start_hex_addr)
	{
		
		for(int i=0;i<number_of_CPUs;i++)
		{
			main_controller.flush(i,start_bin_addr, start_hex_addr,valid_slot);
		}
	}
	public void change_F(boolean[] bin_addr) {
		
		LinkedList<Slot> list=new LinkedList<Slot>();
		LinkedList<Slot> temp=new LinkedList<Slot>();
		boolean shared = false;
		for(int i=0;i<number_of_CPUs;i++)
		{
			list = main_controller.change_F(i,bin_addr,list);
			
			if(!list.isEmpty() && !shared)
			{
				for(Slot s: list)
				{
					s.set_forwarding(true);
					temp.add(s);
				}
				shared=true;
			}
		}
		if(list.size()==temp.size())
		{
			for(Slot s: temp)
			{
				s.set_forwarding(false);
				s.set_shared(false);
				s.set_exclusive(true);
			}
		}
		
		
	}
	public void change_E(boolean[] bin_addr) 
	{
		LinkedList<Slot> list=new LinkedList<Slot>();
		
	
		for(int i=0;i<number_of_CPUs;i++)
		{
			list = main_controller.change_F(i,bin_addr,list);
			
		}
		if(list!=null && list.size()==1)
		{
			for(Slot s: list)
			{
				s.set_forwarding(false);
				s.set_shared(false);
				s.set_exclusive(true);
			}
		}
		
		
	}
	
}

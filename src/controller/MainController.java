package controller;

import java.util.LinkedList;

import hardware.Slot;
import mainmemory.*;


public class MainController implements IBus,ICPU{
	
	private MainMemory main_memory;
	private CPUController cpu_controller;

	boolean has_mm;


	/*Intermediate control methods: link between main memory and CPUController and bus*/
	public MainController()
	{
		
	}
	public void init_main_memory(boolean space,int address_length) 
	{
		has_mm=space;
		cpu_controller=cpu_controllers.peekFirst();
		main_memory=new MainMemory(space,address_length);
	}
	public void write_to_memory(short[] hex_address,boolean[] bin_address,short data)
	{	
		main_memory.write_to_memory(bin_address,hex_address,data);
	}
	public boolean[] get_next_bin_address()
	{
		return main_memory.peek_next_bin_address();
	}
	public short[] get_next_hex_address() 
	{
		return main_memory.peek_next_hex_address();
	}
	public short read_from_memory(short[] hex_address,boolean[] bin_address) 
	{
		return main_memory.read(bin_address,hex_address);
	}
	public short[] read_from_memory_3(short[] hex_addr,boolean[] bin_addr, int length)
	{
		if(has_mm)
			return main_memory.read_3(hex_addr,bin_addr,length);
		else return main_memory.read_random(length);
	}
	public void inc_address(int data_length) 
	{
		main_memory.increment_addresses(data_length);
	}
	public void write_back(boolean[] bin_address,short[] hex_address,short data)
	{
		if(has_mm)
			main_memory.write_to_memory(bin_address, hex_address,data);

	}
	public boolean[] inc_address(boolean[] b) {
		return main_memory.inc_bin_address(b);
	}
	public short[] inc_address(short[] h) {
		return main_memory.inc_hex_address(h);
	}
	public short[] inc_hex_address(short[] address) {
		return main_memory.inc_hex_address(address);
	}
	
	public void write_to_memory(short[] start_hex_addr, boolean[] start_bin_addr, short[] data)
	{
		if(has_mm)
			main_memory.write(data, start_hex_addr, start_bin_addr);
	}
	public LinkedList<Slot> find_slots(int cpu_no,int length, boolean[] start_bin_addr, LinkedList<Slot> slots)
	{
		return cpu_controller.find_slots(cpu_no,length,start_bin_addr,slots);
	}
	public void invalidate_slot(int cpu_no, boolean[] address) 
	{
		 cpu_controller.invalidate_slot(cpu_no, address);
	}
	public void flush(int cpu_no, boolean[] start_bin_addr, short[] start_hex_addr, Slot valid_slot) 
	{
		cpu_controller.flush(cpu_no,start_bin_addr,start_hex_addr, valid_slot);
	}
	public LinkedList<Slot> change_F(int cpu_no, boolean[] bin_addr,LinkedList<Slot> list) 
	{
		return cpu_controller.change_F(cpu_no,bin_addr,list);
	}
	public LinkedList<Slot> change_E(int cpu_no, boolean[] bin_addr, LinkedList<Slot> list) 
	{
		return cpu_controller.change_F(cpu_no,bin_addr,list);
	}
	public void clear_memory(int address_length,boolean space) {
		main_memory.clear(address_length, space);
		main_memory=new MainMemory(space, address_length);
	
		
	}
	
}

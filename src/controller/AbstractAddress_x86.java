package controller;

import io.ActualInstruction_x86;

import io.Register;


public class AbstractAddress_x86 {
	
	private String register_name;
	private String abstract_address;
	boolean has_register;
	boolean has_rex;
	private short[] register_value;
	private short[] displacement;
	
	
	private short sib;
	private Register register;
	private boolean[] binary_address;
	private short[] hex_address;
	private ActualInstruction_x86 instruction;
	private boolean has_rel;
	
	private boolean has_offset;
	private boolean has_pointer;

	private short[] relative_address=null;
	private short[] offset=null;
	private short[] pointer=null;
	
	private short memory_address_type;
	
	private boolean is_write;
	private int readers;
	private int cpu_no;
	private int number_of_cache_misses;
	private int number_of_accesses;
	
	
	private boolean block;
	private short[] abs_addr;
	
	private int number_of_ops;
	private short[] data;
	
	//A type of virtual address
 	public AbstractAddress_x86 (String register_name,short[] register_value,
			                  Register register, boolean[] binary_address,short[] hex_address,ActualInstruction_x86 instruction)
	{
		//this.main_controller=main_controller;
		if(register!=null)
		
			this.register_name=register.get_name();
			this.register_value=register_value;
		
		this.has_rex=instruction.has_rex();
		
		this.displacement=instruction.get_displacement();
	
		this.register=register;
		this.binary_address=binary_address;
		this.hex_address=hex_address;
		this.instruction=instruction;
		this.number_of_ops=1;
		
		if(this.instruction.has_relative_address())
		{
			has_rel=true;
			this.relative_address=this.instruction.get_relative_address();
		}
		if(this.instruction.has_offset())
		{
			has_offset=true;
			this.offset=this.instruction.get_offset();
			
		}
		if(this.instruction.has_pointer())
		{
			has_pointer=true;
			this.pointer=this.instruction.get_pointer();
		}
		
		if(register!=null)
			  set_name();
	}
	private void set_name() {
		
		abstract_address="" + register_name+  ":";
		for(int i=0;i<register_value.length;i++)
		{
			abstract_address+=Integer.toHexString(register_value[i]);
		}
		abstract_address+=":";
		if(displacement!=null)
		{
			for(int i=0;i<displacement.length;i++)
			{
				abstract_address+=Integer.toHexString(displacement[i]);
			}
			abstract_address+=":";
		}
	}
	public String get_register_name()
	{
		return register_name;
	}
	public short[] get_register_value()
	{
		return  register_value;
	}
	public short[] get_displacement()
	{
		return  displacement;
	}
	public short get_sib()
	{
		return  sib;
	}
	public boolean has_relative_address()
	{
		return has_rel;
	}
	public short[] get_relative_address()
	{
		return relative_address;
	}
	public boolean has_offset()
	{
		return has_offset;
	}
	public short[] get_offset()
	{
		return offset;
	}
	public boolean has_pointer()
	{
		return has_pointer;
	}
	public short[] get_pointer()
	{
		return pointer;
	}
	public short get_memory_address_type()
	{
		return memory_address_type;
	}
	public Register get_register()
	{
		return register;
	
	}
	public boolean[] get_binary_address()
	{
		return binary_address;
	}
	public short[] get_hex_address()
	{
		return hex_address;
	}
	public ActualInstruction_x86 get_instruction()
	{
		return instruction;
	}
	public boolean is_write() {
		return is_write;
	}
	public void set_is_write(boolean is_write) {
		this.is_write = is_write;
	}
	public int get_readers() {
		return readers;
	}
	public void set_readers(int readers) {
		this.readers = readers;
	}
	public int get_cpu_no() {
		return cpu_no;
	}
	public void set_cpu_no(int cpu_no) {
		this.cpu_no = cpu_no;
	}
	public void assign_address(boolean[] binary_address, short[] hex_address) {
		this.binary_address=binary_address;
		this.hex_address=hex_address;
		
	}
	public int get_number_of_cache_misses() {
		return number_of_cache_misses;
	}
	public void inc_number_of_cache_misses() {
		this.number_of_cache_misses++;
	}
	public int get_number_of_accesses() {
		return number_of_accesses;
	}
	public void inc_number_of_accesses() {
		this.number_of_accesses++;
	}
	public String get_name() {
		return abstract_address;
	}
	public void inc_number_of_readers() {
		readers++;
		
	}
	public void dec_number_of_readers() {
		readers--;
		
	}
	public void set_data(short[] data) {
		this.data=data;
		
	}
	public void set_address(short[] hex_address) {
		this.hex_address=hex_address;
		
	}
	public void block() {
		block=true;
		
	}
	public void unblock() {
		block=false;
		
	}
	public boolean is_blocked()
	{
		return block;
	}
	public void set_abs_addr(short[] value) {
	
		abs_addr=value;
	}
	public short[] get_abs_addr()
	{
		return abs_addr;
	}
	public int get_number_of_ops() {
		return number_of_ops;
	}
	public void inc_number_of_ops() {
		this.number_of_ops++;
	}
	public void dec_number_of_ops() {
		this.number_of_ops++;
	}
	
	

}

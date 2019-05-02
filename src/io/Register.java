package io;
import java.util.LinkedList;
import java.util.Random;

import controller.CPUProcess;


public class Register {
	
	private String r_m_name;
	private String reg_name;

	private short[] data;
	private short[] address;
	private short[] displacement;
	private short[][] immediate;
	
	private short sib;
	
	private boolean src_is_rm;
	private short mod;
	private short reg;
	private short rm;
	private short d_bit;
	
	private boolean has_sib;
	private boolean is_FPU;
	private short size_FPU_reg;

	private short number_of_ops;
	private ActualInstruction_x86 instruction;
	private short number_of_immediates;
	private short number_of_regs;
	private int mode;
	private boolean has_rex;
	
	//Order of which instr. to be assigned to a processor
	private LinkedList<Integer> cpu_numbers_list;
	private LinkedList<Integer> is_write_cpu_no_list;
	private LinkedList<Integer> is_write_instr_order_no_list;
	private boolean is_write;
	private boolean is_read;
	private int number_of_readers;
	private int cpu_no;
	private String name;
	private LinkedList<CPUProcess> proc_order;
	private boolean block;
	
	/* Generates a register and randomly generates data */
	
	/* Generates a register and randomly generates data
	 * Also includes state of lock, and blocking */
	public Register(String name,boolean has_rex,boolean is_FPU,int length)
	{
		this.name=name;
		this.has_rex=has_rex;
		this.is_FPU=is_FPU;
		this.data=gen_random_data(length);
		
	}
	private short[] gen_random_data(int length) {
		
		Random random=new Random();
		short[] hex_data=new short[length/8];
		int bound=0x100;

		for(int i=0;i<length/8;i++)
		{
			hex_data[i]=(short)(random.nextInt(bound) % bound);
		}
		return hex_data;
	
	}
	
	public short get_number_of_registers()
	{
		return number_of_regs;
	}
	public short get_reg_index(short purpose,int opsize)
	{
		if(purpose<0x00)
		{
			if(opsize==0x08)
			{
				return 0;
			}
			else if(opsize==0x10)
			{
				return 1;
			}
			else if(opsize==0x20)
			{
				return 2;
			}
			else if(opsize==0x40)
			{
				return 3;
			}
		}
		else if(purpose==0x01)
			return 4;
		else if(purpose==0x02)
			return 5;
		else if(purpose==0x03)
			return 6;
		else if(purpose==0x04)
			return 7;
		else return 8;
		
		return -1;
		
	}
	public void set_name(String name)
	{
		this.name=name;
	}
	
	public short get_mod()
	{
		return mod;
	}
	public short get_reg()
	{
		return reg;
	}
	public short get_r_m()
	{
		return rm;
	}
	public short[] get_address()
	{
		return address;
	}
	public boolean has_sib() {
		
		return has_sib;
	}
	public short[] get_displacement() {
		
		return displacement;
	}
	public boolean src_is_modrm() {
		
		return src_is_rm;
	}
	public short[] get_data() {
		return data;
	}
	public short get_sib() {
		
		return sib;
	}
	public boolean is_FPU()
	{
		return is_FPU;
	}
	public short get_size_FPU_reg()
	{
		return size_FPU_reg;
	}
	public ActualInstruction_x86 get_instruction()
	{
		return instruction;
	}
	public String get_reg_name() {
		return reg_name;
	}
	public String get_r_m_name()
	{
		return r_m_name;
	}
	public short[][] get_immediate() {
		return immediate;
	}
	public short get_d_bit()
	{
		return d_bit;
	}
	public boolean has_rex()
	{
		return has_rex;
	}
	public void set_is_write(boolean is_write)
	{
		this.is_write=is_write;
	}
	public boolean is_write()
	{
		return is_write;
	}
	
	public boolean get_is_read() {
		return is_read;
	}
	public void set_is_read(boolean is_read) {
		this.is_read = is_read;
	}
	public int get_number_of_readers() {
		return number_of_readers;
	}
	public void set_number_of_readers(int number_of_readers) {
		this.number_of_readers=number_of_readers;
		
	}
	public int get_cpu_no() {
		return cpu_no;
	}
	public void set_cpu_no(int cpu_no) {
		this.cpu_no = cpu_no;
	}
	public void write(short[] hex_data)
	{
		this.data=hex_data;
	}
	public String get_name() {
		// TODO Auto-generated method stub
		return this.name;
	}
	public void inc_number_of_readers() {
		number_of_readers++;
	}
	public void dec_number_of_readers() {
		number_of_readers--;
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
}

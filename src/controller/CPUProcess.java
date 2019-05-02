package controller;

import io.ActualInstruction_x86;

import io.Register;

import java.util.LinkedList;

import hardware.CPU;

public class CPUProcess {
	
	private CPU cpu;
	private ActualInstruction_x86 instruction;
	private Register[] registers; //1:Src 2:Dest 3:Tert 4:Quater
	private AbstractAddress_x86 abstract_address;
	private int cpu_no;
	private int sequence_order;
	private int number_of_registers;
	private boolean wait;
	private boolean is_to_memory;
	private boolean is_from_memory;

	private int cycle_length;
	private boolean finished;
	private int proc_id;
	private boolean run;
	private LinkedList<Procedure> procedures;
	
	/*Process class, mostly get and set methods */
	public CPUProcess(AbstractAddress_x86 abstract_address,ActualInstruction_x86 instruction,Register[] registers,int cpu_no, int sequence_order,int number_of_registers)
	{
		this.abstract_address=abstract_address;
		this.instruction=instruction;
		this.registers=registers;
		this.cpu_no=cpu_no;
		this.sequence_order=sequence_order;
		this.number_of_registers=number_of_registers;
		this.is_to_memory=instruction.write_to_memory();
		this.is_from_memory=instruction.read_from_memory();
		procedures=new LinkedList<Procedure>();
		
		
	}
	
	public CPU get_cpu()
	{
		return cpu;
	}
	public ActualInstruction_x86 get_instruction()
	{
		return instruction;
	}
	public AbstractAddress_x86 get_abs_address()
	{
		return abstract_address;
	}
	public Register[] get_registers()
	{
		return registers;
	}
	public int get_cpu_no()
	{
		return cpu_no;
	}
	public int get_sequence_order()
	{
		return sequence_order;
	}
	public int get_number_of_registers()
	{
		return number_of_registers;
	}
	public void set_read(int i,boolean is_read)
	{
		registers[i].set_is_read(is_read);
	}
	public void set_write(int i,boolean is_write)
	{
		registers[i].set_is_read(is_write);
	}
	
	public Register get_register(int i)
	{
		if(i<number_of_registers)
			return registers[i];
		return null;
	}
	public void proc_wait()
	{
		wait=true;
	}
	public void proc_continue()
	{
		wait=false;
	}
	public void proc_finished()
	{
		int n_readers;
		for(int i=0;i<number_of_registers;i++)
		{
			registers[i].set_is_write(false);
			n_readers=registers[i].get_number_of_readers();
			if(n_readers==0)
				registers[i].set_is_read(false);
			else
				registers[i].set_number_of_readers(n_readers-1);
		}
	}

	public void set_sequence_order(int sequence_order) {
		this.sequence_order=sequence_order;
		
	}
	public boolean is_write_to_memory()
	{
		return is_to_memory;
	}
	public boolean is_read_from_memory()
	{
		return is_from_memory;
	}

	public void set_cpu_no(int cpu_no) {
		this.cpu_no=cpu_no;
		
	}

	public void set_cycle_length(int cycle_length) {
		this.cycle_length=cycle_length;
		
	}
	public int get_cycle_length()
	{
		return this.cycle_length;
	}
	public void inc_cycle_length()
	{
		this.cycle_length++;
	}
	public void dec_cycle_length()
	{
		this.cycle_length--;
	}

	public void set_finished(boolean finished) {
		this.finished=finished;
		
	}
	public boolean finished()
	{
		return this.finished;
	}

	public void set_proc_id(int proc_id) {

		this.proc_id=proc_id;
		
	}
	public int get_proc_id() {
		return proc_id;
	}

	public void p_run() {
		wait=false;
		run=true;
		
	}
	public void p_wait()
	{
		wait=true;
	}
	public void p_finished()
	{
		finished=true;
		run=false;
	}
	public boolean get_p_finished()
	{
		return finished;
		
	}
	public boolean get_p_run()
	{
		return run;
	}
	public boolean get_p_wait()
	{
		return wait;
	}

	public void add_procedure(Procedure procedure) {
		 procedures.addLast(procedure);
		
	}
	public void remove_procedure()
	{
		if(procedures.isEmpty())
			return;
		procedures.removeFirst();
	}
	public Procedure get_procedure()
	{
		return procedures.removeFirst();
	}

	public boolean procedures_is_finished() {
		if(procedures.isEmpty())
			return true;
		else
			return false;
	}
	public LinkedList<Procedure> get_procedures()
	{
		return procedures;
	}
	
}

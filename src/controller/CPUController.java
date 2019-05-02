package controller;

import java.io.File;

import java.math.RoundingMode;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import java.util.Random;



import application.SimResult;
import io.ActualInstruction_x86;

import io.FileReader;
import io.MemoryHandler;
import io.Register;
import io.Register_x86;
import hardware.CPU;
import hardware.Entry;
import hardware.Slot;
import mainmemory.Bus;
import mainmemory.IMC;


public class CPUController implements IBus,IMC{

	//Scheduler for CPU
	//private MainController main_controller;

	private CPU[] CPUs;

	private FileReader reader;
	
	private MemoryHandler memory_handler;

	private Register_x86 registers;

	public int number_of_CPUs;

	private int address_length;

	private LinkedList<CPUProcess> processes;
	private int number_of_processes;
	private LinkedList<AbstractAddress_x86> abs_addresses;
	

	private File file;
	private int number_of_levels;

	private boolean space;
	private boolean prefetch;
	private int sequence_order;
	private ArrayList<Result> result_list;
	private int[] capacity;
	private int[] blocksize;
	private int[] associativity;
	private int incl_policy;
	private HashMap<String,Address> addresses;

	private int proc_index;
	private HashMap<Integer,CPUProcess> process_overview;
	private int memory_access;

	private PerformanceMetric perf_metrics;
	private int[] subtype_numbers;
	private MainController main_controller;
	private Bus bus;

	private boolean seperate_registers;

	private String path_name;




	private static final String[] subtype_labels={"Cold","Coherence","Capacity","Conflict"};


	/* 
	 * This class is a controller class. Its task is to generate processes, execute and finish them. 
	 * Includes process and pseudo-random procedure generator, scheduler and in general logic for communicating with I/O units
	 * */


	public void set_param(boolean sepregs,boolean space,boolean prefetch,int number_of_levels,int number_of_CPUs,int incl_policy, int[] capacity,int[] blocksize,int[] associativity,int address_length, String path_name){

	
		main_controller=main_controllers.peekFirst();
		bus = buses.peekFirst();
		seperate_registers=sepregs;

		this.number_of_CPUs=number_of_CPUs;
		this.CPUs=new CPU[number_of_CPUs];
		this.space=space; //enable main memory :True:Enable mm
		this.prefetch=prefetch;
		this.capacity=capacity;
		this.blocksize=blocksize;
		this.associativity=associativity;
		for(int i=0;i<CPUs.length;i++)
			CPUs[i]=new CPU(i,number_of_levels,incl_policy,capacity,blocksize,associativity,address_length);


		registers=new Register_x86();
		processes=new LinkedList<CPUProcess>();

		abs_addresses=new LinkedList<AbstractAddress_x86>();

	
		result_list=new ArrayList<Result>();
		addresses= new HashMap<String,Address>();
		process_overview=new HashMap<Integer,CPUProcess>();


		number_of_CPUs=CPUs.length;
		this.address_length=address_length;
	
		this.number_of_levels=number_of_levels;
		this.incl_policy=incl_policy;
		file=new File(path_name);

		main_controller.init_main_memory(space,address_length);

		bus.set_param(number_of_CPUs,address_length,number_of_levels);
		this.path_name=path_name;
		reader=new FileReader(path_name, address_length); //Find Better solution
	}
	private void clear_all() {

		for(int i=0;i<CPUs.length;i++)
			CPUs[i].clear_caches();
		CPUs=null;
		CPUs=new CPU[number_of_CPUs];
		for(int i=0;i<CPUs.length;i++)
			CPUs[i]=new CPU(i,number_of_levels,incl_policy,capacity,blocksize,associativity,address_length);

		registers=new Register_x86();
		process_overview=new HashMap<Integer,CPUProcess>();
		memory_access=0;
		proc_index=0;
		abs_addresses.clear();
		addresses.clear();
		main_controller.clear_memory(address_length,space);
		reader=new FileReader(path_name, address_length);
		file=new File(path_name);
		System.gc(); //Must call GC. Since the GC is lazy

		open_file();
		reader.add_to_list(seperate_registers);
		close();

	}
	public void clear() {
		
		for(int i=0;i<CPUs.length;i++)
			CPUs[i].clear_caches();
		CPUs=null;
		CPUs=new CPU[number_of_CPUs];
		for(int i=0;i<CPUs.length;i++)
			CPUs[i]=new CPU(i,number_of_levels,incl_policy,capacity,blocksize,associativity,address_length);

		registers=new Register_x86();
		process_overview=new HashMap<Integer,CPUProcess>();
		proc_index=0;
		memory_access=0;
		abs_addresses.clear();
		addresses.clear();
		main_controller.clear_memory(address_length,space);
		reader=new FileReader(path_name, address_length);
		System.gc(); //Must call GC. Since the GC is lazy
	}
	public PerformanceMetric run() {

		clear_all();
		reader.read(seperate_registers);
		return assemble_results();

	}
	public void close()
	{
		reader.close_file();
	}
	public PerformanceMetric assemble_results()
	{
		SimResult sr=null;
		int[] temp;
		int[] subtype_numbers= new int[0x04];
		NumberFormat format = NumberFormat.getInstance(Locale.UK);
		format.setRoundingMode(RoundingMode.CEILING);
		format.setMinimumFractionDigits(0);
		format.setMaximumFractionDigits(2);

		ArrayList<SimResult> al=new ArrayList<SimResult>();
		double[] n_hits = new double[number_of_levels];
		double[] n_misses = new double[number_of_levels];
		for(int i=0;i<number_of_CPUs;i++)
		{
			CPUs[i].count_types_of_misses();
			for(int j=0;j<number_of_levels;j++)
			{
				n_hits[j]+=CPUs[i].get_no_of_hits()[j];
				n_misses[j]+=CPUs[i].get_no_of_misses()[j];

			}
			for(int j=0;j<subtype_numbers.length;j++)
			{
				temp=CPUs[i].get_subtypes_numbers();
				subtype_numbers[j]+=temp[j];
			}
		}
		for(int j=0;j<number_of_levels;j++)
		{

			double sum=n_hits[j]+n_misses[j];
			double ratio=(n_misses[j]/sum)*100;
			ratio = Double.parseDouble(format.format(ratio));
			sr=new SimResult((int)n_hits[j],(int)n_misses[j],ratio);
			al.add(sr);

		}
		TypesOfMisses tom = new TypesOfMisses(subtype_numbers);
		perf_metrics.add_miss_types(tom);
		perf_metrics.set_results(al);
		return perf_metrics;
	}
	public PerformanceMetric get_perf_metrics()
	{
		return perf_metrics;
	}
	public boolean open_file()
	{
		if(reader.run_file(file))
			return true;
		else
			return false;
	}
	public boolean[] next_address_2(boolean[] current_address)
	{
		if(current_address==null)
			current_address=new boolean[address_length];
		else
		{
			for(int i=address_length-1;i>-1;i--)
			{
				if(current_address[i])
				{
					current_address[i]=false;
				}
				else
				{
					current_address[i]=true;
					return current_address;
				}
			}
		}
		return current_address;
	}
	public void progr_to_mem(ArrayList<Short> byte_al, int length) //try with non-variable datasizes
	{
		if(space)
		{
			short[] hex_address=null;

			boolean[] bin_address=null;
			try 
			{

				for(int i=0;i<byte_al.size();i++)
				{
					bin_address=this.save_bin_address(bus.get_next_bin_address());
					hex_address=this.save_hex_address(bus.get_next_hex_address());
					bus.write_back(hex_address,bin_address,byte_al.get(i));
					bus.inc_address(1);
				}
			}
			catch(Exception e)
			{
				return;
			}
		}
		
	}
	private short[] random_data(int length) {

		Random random=new Random();
		short[] hex_data=new short[length/8];
		int bound=0x100;

		for(int i=0;i<length/8;i++)
		{
			hex_data[i]=(short)(random.nextInt(bound) % bound);
		}
		return hex_data;
	}

	/* The main purpose of this method is to create processes
	 * Each instruction in passed through here. If the instructions are memory (LOAD/STORE) operations they will be assigned an abstract address object.
	 * */
	public void create_processes(ActualInstruction_x86 instr_head, int cpu_no,int seq_no, int rdm_index, int rdm_length, CPUProcess old_proc)
	{
		short modrm=-1;
		short mod=-1;
		short rm=-1;
		short reg=-1;
		short number_of_registers=-1;

		int length=0;
		Register[] regs=new Register[0x04];
		CPUProcess proc;
		ActualInstruction_x86 instr = instr_head;
		AbstractAddress_x86 abs_addr = null;

		if(instr!=null)
		{
			if(old_proc==null)
			{

				modrm=instr.get_modrm();
				mod=instr.get_mod();
				rm=instr.get_rm();
				reg=instr.get_reg();
				number_of_registers=instr.get_number_of_ops();
				if(instr.has_immediate())
				{
					number_of_registers--;
				}
				if(instr.has_relative_address())
				{
					number_of_registers--;
				}
				if(instr.has_pointer())
				{
					number_of_registers--;
				}
				if(instr.has_offset())
				{
					number_of_registers--;
				}
				if(instr.has_memory_address())
				{
					number_of_registers=0;
				}
				if(instr.get_opsizes()[0x01] == 1)
				{
					number_of_registers--;
				}
				if(modrm>-1)
				{
					if(number_of_registers==0x00)
					{
						length=instr.get_opsizes()[0x00]/8;
						boolean[] bin_address=save_bin_address(bus.get_next_bin_address());
						short[] hex_address=save_hex_address(bus.get_next_hex_address());
						short[] value=create_abs_addr(instr,hex_address,(short) 0);
						bus.inc_address(length);
						short[] data=generate_hex_data(length, true);

						abs_addr=new AbstractAddress_x86("Memory address", data,null,bin_address,hex_address, instr);
						abs_addr.set_abs_addr(value);
						abs_addresses.add(abs_addr);
					}
					if(number_of_registers==0x01)
					{
						if(mod==0x03) //register to register operation
						{	
							regs[0]=registers.get_register(instr.has_rex(), true, instr.is_fpu(), instr.get_opsizes()[0x00],
									instr.get_reg_purpose_indexes()[0x00], mod,rm,reg);

							short[] temp=regs[0].get_data();
							short[] data=new short[temp.length];
							for(int i=0;i<data.length;i++)
							{
								data[i]=temp[i];
							}

						}
						else
						{
							regs[0]=registers.get_register(instr.has_rex(), true, instr.is_fpu(), instr.get_opsizes()[0x00],
									instr.get_reg_purpose_indexes()[0x00], mod,rm,reg);
							regs[1]=registers.get_register(instr.has_rex(), false, instr.is_fpu(), instr.get_opsizes()[0x01], 
									instr.get_reg_purpose_indexes()[0x01], mod,rm,reg);
							if(instr.get_d_bit()==0x00)
							{
								short[] temp=regs[0].get_data();

								short[] data=new short[temp.length];
								for(int i=0;i<data.length;i++)
								{
									data[i]=temp[i];

								}
							}
						}
						short[] value=create_abs_addr(instr,regs[0].get_data(),(short) 0);
						length=instr.get_opsizes()[0x00]/8;
						if(instr.has_immediate())
						{
							boolean[] bin_address=save_bin_address(bus.get_next_bin_address());
							short[] hex_address=save_hex_address(bus.get_next_hex_address());
							bus.inc_address(length);
							short[] data=generate_hex_data(length, true);
							abs_addr=new AbstractAddress_x86("Immediate", data, null,bin_address,hex_address, instr);
							//
							abs_addr.set_abs_addr(value);
							abs_addresses.add(abs_addr);
						}
						if(instr.get_offset()!=null)
						{
							boolean[] bin_address=save_bin_address(bus.get_next_bin_address());
							short[] hex_address=save_hex_address(bus.get_next_hex_address());
							bus.inc_address(length);
							short[] data=generate_hex_data(length, true);
							abs_addr=new AbstractAddress_x86("Offset", data, null,bin_address,hex_address, instr);
							abs_addr.set_abs_addr(value);
							abs_addresses.add(abs_addr);
						}
						else if(instr.get_pointer()!=null)
						{
							boolean[] bin_address=save_bin_address(bus.get_next_bin_address());
							short[] hex_address=save_hex_address(bus.get_next_hex_address());
							bus.inc_address(length);
							short[] data=generate_hex_data(length, true);
							abs_addr=new AbstractAddress_x86("Pointer", data, null,bin_address,hex_address, instr);
							abs_addr.set_abs_addr(value);
							abs_addresses.add(abs_addr);
						}
						else  if(instr.get_relative_address()!=null)
						{
							//true if it is a an instruction that changes the program counter
							boolean[] bin_address=save_bin_address(bus.get_next_bin_address());
							short[] hex_address=save_hex_address(bus.get_next_hex_address());
							bus.inc_address(length);
							short[] data=generate_hex_data(length, true);
							regs[0x00]=new Register("rel",false,false,0);
							abs_addr=new AbstractAddress_x86("Relative address", data, regs[0x00],bin_address,hex_address, instr);
							abs_addr.set_abs_addr(value);
							abs_addresses.add(abs_addr);
						}
					}
					else
					{
						if(mod==0x03) // reg to reg
						{	
							regs[1]=registers.get_register(instr.has_rex(), true, instr.is_fpu(), instr.get_opsizes()[0x00],
									instr.get_reg_purpose_indexes()[0x00], mod,rm,reg);
							short[] value=create_abs_addr(instr,regs[1].get_data(),(short) 0);
							short[] temp=regs[1].get_data();
							short[] data=new short[temp.length];

							for(int i=0;i<data.length;i++)
							{
								data[i]=temp[i];
							}
							abs_addr=new AbstractAddress_x86(regs[1].get_r_m_name(),data, regs[1],null,null, instr);
							abs_addr.set_abs_addr(value);
							abs_addresses.add(abs_addr);
							regs[0]=registers.get_register(instr.has_rex(), true, instr.is_fpu(), instr.get_opsizes()[0x01],
									instr.get_reg_purpose_indexes()[0x01], mod,rm,reg);
							regs[0].write(data);
						}
						else //Memory operation
						{

							regs[0]=registers.get_register(instr.has_rex(), true, instr.is_fpu(), instr.get_opsizes()[0x00], 
									instr.get_reg_purpose_indexes()[0x00], mod,rm,reg);
							regs[1]=registers.get_register(instr.has_rex(), false, instr.is_fpu(), 
									instr.get_opsizes()[0x01], instr.get_reg_purpose_indexes()[0x01], 
									mod,rm,reg);
							length=instr.get_opsizes()[0x00]/8;
							boolean[] bin_address;
							short[] hex_address;
							int index=0;

							if(instr.get_d_bit()==0x00) //Direction bit
							{
								short[] temp=null;
								if(instr.has_memory_address())
								{
									temp=generate_hex_data(length, true);
								}
								else
									temp=regs[1].get_data();
								short[] data=new short[temp.length];
								short[] value=create_abs_addr(instr,regs[0].get_data(),(short) 0);
								abs_addr=compare_instr(value);
								for(int i=0;i<data.length;i++)
								{
									data[i]=temp[i];
								}
								if(abs_addr!=null)
								{
									bin_address=abs_addr.get_binary_address();
									hex_address=abs_addr.get_hex_address();
									abs_addr.set_abs_addr(value);

									abs_addr.inc_number_of_ops();
								}
								else
								{
									bin_address=save_bin_address(bus.get_next_bin_address());
									hex_address=save_hex_address(bus.get_next_hex_address());
									bus.inc_address(length);
									abs_addr=new AbstractAddress_x86(regs[1].get_r_m_name(),data, regs[1],bin_address,hex_address, instr);

									abs_addr.set_abs_addr(value);
									abs_addresses.add(abs_addr);
								}
							}
							else
							{ 
								short[] temp=regs[1].get_data();
								short[] data=new short[temp.length];
								length=instr.get_opsizes()[0x00]/8;
								for(int i=0;i<data.length;i++)
								{
									data[i]=temp[i];
								}
								short[] value=create_abs_addr(instr,regs[1].get_data(),(short) 0); 
								int mod_instr=instr.get_mod();
								int mod_abs_addr=-2;
								index=0;
								if((abs_addresses.size())>1) //fix
								{
									while(index<abs_addresses.size())
									{
										//index=random.nextInt((abs_addresses.size()-1));
										abs_addr=abs_addresses.get(index);
										mod_abs_addr=abs_addr.get_instruction().get_mod();
										if(mod_abs_addr!=mod_instr)
										{
											break;
										}
										index++;
									}
									if(mod_abs_addr!=mod_instr) //Comparing mod from AA to instr
									{
										bin_address=save_bin_address(bus.get_next_bin_address());
										hex_address=save_hex_address(bus.get_next_hex_address());
										bus.inc_address(length);
										abs_addr=new AbstractAddress_x86(regs[1].get_r_m_name(),data, regs[1],bin_address,hex_address, instr);

										abs_addr.set_abs_addr(value);
										abs_addresses.add(abs_addr);
									}
									else
									{
										bin_address=abs_addr.get_binary_address();
										hex_address=abs_addr.get_hex_address();
									}	
								}
								else
								{
									bin_address=save_bin_address(bus.get_next_bin_address());
									hex_address=save_hex_address(bus.get_next_hex_address());
									bus.inc_address(length);
									abs_addr=new AbstractAddress_x86(regs[1].get_r_m_name(),data, regs[1],bin_address,hex_address, instr);

									abs_addr.set_abs_addr(value);
									abs_addresses.add(abs_addr);

								}

							}
						}
					}
					if(number_of_registers>2)
					{
						int k=0;
						for(int j=2;j<number_of_registers;j++)
						{
							regs[j]=registers.get_register(instr.has_rex(), true, instr.is_fpu(), instr.get_opsizes()[j], instr.get_reg_purpose_indexes()[j],(short)3,(short)k,(short)k);
							k++;
						}
					}
				}
				else //Cases when there is no modrm byte, but implicit reg-code in Opcode, and fixed registers. FIX!!!
				{
					if(instr.get_reg_is_in_opcode())
					{
						regs[0]=registers.get_register(instr.has_rex(), true, instr.is_fpu(), instr.get_opsizes()[0x00], instr.get_reg_purpose_indexes()[0x00], mod,rm,reg);
					}
					if(number_of_registers>1)
					{
						int k=0;
						for(int j=1;j<number_of_registers;j++)
						{
							regs[j]=registers.get_register(instr.has_rex(), true, instr.is_fpu(), instr.get_opsizes()[j], instr.get_reg_purpose_indexes()[j],(short)3,(short)k,(short)k);
							k++;
						}
					}
					//Special cases
					length=instr.get_opsizes()[0x00];
					short[] value=create_abs_addr(instr,null,(short) 0);
					if(instr.has_immediate()) 
					{
						boolean[] bin_address=save_bin_address(bus.get_next_bin_address());
						short[] hex_address=save_hex_address(bus.get_next_hex_address());
						bus.inc_address(length);
						short[] data=generate_hex_data(length, true);
						abs_addr=new AbstractAddress_x86("Immediate", data, null,bin_address,hex_address, instr);
						abs_addr.set_abs_addr(value);
						abs_addresses.add(abs_addr);
					}
					if(instr.get_offset()!=null)
					{
						boolean[] bin_address=save_bin_address(bus.get_next_bin_address());
						short[] hex_address=save_hex_address(bus.get_next_hex_address());
						bus.inc_address(length);
						short[] data=generate_hex_data(length, true);
						abs_addr=new AbstractAddress_x86("Offset", data, null,bin_address,hex_address, instr);
						abs_addr.set_abs_addr(value);
						abs_addresses.add(abs_addr);
					}
					else if(instr.get_pointer()!=null)
					{
						boolean[] bin_address=save_bin_address(bus.get_next_bin_address());
						short[] hex_address=save_hex_address(bus.get_next_hex_address());
						bus.inc_address(length);
						short[] data=generate_hex_data(length, true);
						abs_addr=new AbstractAddress_x86("Pointer", data, null,bin_address,hex_address, instr);
						abs_addr.set_abs_addr(value);
						abs_addresses.add(abs_addr);
					}
					else  if(instr.get_relative_address()!=null)
					{
						//is a JMP op or call

						boolean[] bin_address=save_bin_address(bus.get_next_bin_address());
						short[] hex_address=save_hex_address(bus.get_next_hex_address());
						bus.inc_address(length);
						short[] data=generate_hex_data(length, true);
						regs[0x00]=new Register("rel",false,false,0);
						abs_addr=new AbstractAddress_x86("Relative address", data, regs[0x00],bin_address,hex_address, instr);
						abs_addr.set_abs_addr(value);
						abs_addresses.add(abs_addr);
					}
					else
					{
						boolean[] bin_address=save_bin_address(bus.get_next_bin_address());

						short[] hex_address=save_hex_address(bus.get_next_hex_address());
						bus.inc_address(length);
						short[] data=generate_hex_data(length, true);
						abs_addr=new AbstractAddress_x86("Unknown", data, regs[0x00],bin_address,hex_address, instr);
						abs_addr.set_abs_addr(value);
						abs_addresses.add(abs_addr);
					}
				}
				length=instr.get_opsizes()[0x00];
				//fill_caches_random(length);
				sequence_order++;
				proc = new CPUProcess(abs_addr, instr,regs,cpu_no,sequence_order,number_of_registers);


				process_overview.put(proc_index,proc);

				if(proc_index<Integer.MAX_VALUE)
					proc_index++;
				else
					proc_index=0;


				add_process_to_list(proc,cpu_no);
				instr=instr.get_next();
			}
			else
			{

				proc = new CPUProcess(old_proc.get_abs_address(), instr,regs,cpu_no,seq_no,old_proc.get_number_of_registers());
				add_process_to_list(old_proc,cpu_no);
			}

		}
		number_of_processes=processes.size();
	}

	//Generating an abstract address, based on instruction and register value
	private short[] create_abs_addr(ActualInstruction_x86 instr, short[] value, short index) 
	{
		short[] disp=instr.get_displacement();
		short[][] imm = instr.get_immediates();
		short[] rel=instr.get_relative_address();
		short[] pointer = instr.get_pointer();
		short[] offset =instr.get_offset();
		short base = instr.get_base();
		short mod = instr.get_mod();
		LinkedList<Short> temp_list=new LinkedList<Short>();

		if(mod>-1)
		{
			temp_list.add((short) -8);
			temp_list.add(mod);
		}
		if(value!=null)
		{
			temp_list.add((short) -1);

			for(int i=0; i<value.length; i++)
			{
				temp_list.add((short) value[i]);
			}
		}
		if(base>-1)
		{
			temp_list.add((short) -2);
			temp_list.add(base);

		}
		if(disp!=null)
		{
			temp_list.add((short) -3);
			for(int i=0; i<disp.length; i++)
			{
				temp_list.add((short)disp[i]);
			}
		}
		if(imm!=null)
		{
			temp_list.add((short) -4);
			for(int i=0; i<imm.length; i++)
			{
				temp_list.add((short)imm[index][i]);
			}
		}
		if(rel!=null)
		{
			temp_list.add((short) -5);;
			for(int i=0; i<rel.length; i++)
			{
				temp_list.add((short)rel[i]);
			}
		}
		if(pointer!=null)
		{
			temp_list.add((short) -6);
			for(int i=0; i<pointer.length; i++)
			{
				temp_list.add((short)pointer[i]);
			}
		}
		if(offset!=null)
		{
			temp_list.add((short) -7);
			for(int i=0; i<offset.length; i++)
			{
				temp_list.add((short)offset[i]);
			}
		}
		int i =0;
		short[] abs_addr=new short[temp_list.size()];
		while(!temp_list.isEmpty())
		{
			abs_addr[i]=temp_list.removeFirst();
			i++;
		}
		return abs_addr;
	}
	private AbstractAddress_x86 compare_instr(short[] value) {

		short[] abs_addr=null;
		boolean match = false;
		for(AbstractAddress_x86 aa:abs_addresses)
		{
			abs_addr=aa.get_abs_addr();
			if(value.length==abs_addr.length)
			{
				match=true;
				for(int i=0;i<value.length;i++)
				{
					if(value[i]!=abs_addr[i])
					{
						match=false;
						break;
					}	
				}
			}
			else
				match=false;
			if(match)
				return aa;
		}
		return null;
	}
	/*On JMP operation, a random branch will be generated. Based on the processor list, it can JMP to a random process
	and change the program counter to the address associated with that process*/
	private void gen_random_branch() //fix
	{
		LinkedList<CPUProcess> temp=new LinkedList<CPUProcess>();
		Random random_index=new Random();
		Random random_cond=new Random();
		CPUProcess proc=null;
		int i=0;
		ActualInstruction_x86 instr=null;

		while(!processes.isEmpty())
		{
			if(i<processes.size())
				proc=processes.remove(i);
			else{

				i=0;
				proc=processes.remove(i);
			}
			instr=proc.get_instruction();
			if(instr.has_relative_address() && !instr.is_call() && !instr.is_loop())
			{
				if(random_cond.nextBoolean())
				{
					if(((processes.size())>0))
						i=random_index.nextInt(processes.size());
					else break;
				}
				else 
				{
					if((processes.size()>0)&& i<(processes.size()))
						i++;

				}
			}
			else
			{
				if((processes.size()>0) && i<(processes.size()))
					i++;
				else if(((processes.size()-1)>-1))
					i=random_index.nextInt(processes.size());
				else break;

				temp.add(proc);
			}
		}
		processes=temp;

	}
	public void write_to_memory(short[] hex_address,boolean[] bin_address,short data)
	{
		bus.write_to_memory(hex_address,bin_address,data);
	}

	private void add_process_to_list(CPUProcess proc, int cpu_no) 
	{	
		if(cpu_no>-1)
			processes.addFirst(proc);
		else
		{
			processes.add(proc);
		}
	}
	public CPUProcess fetch_and_remove_next_process()
	{
		return processes.removeFirst();
	}
	public CPUProcess peek_at_next_proc()
	{
		return processes.peek();
	}
	public void assign_memory_address(CPUProcess proc)
	{
		AbstractAddress_x86 abs_addr=proc.get_abs_address();
		short[] hex_address=null;
		if(abs_addr.get_hex_address()!=null)
		{	
			hex_address=bus.get_next_hex_address();
			abs_addr.set_address(hex_address);
		}
	}
	/* Scheduler with register dependencies. Threads*/
	public void execute_proc() 
	{
		LinkedList<CPUProcess> wait_list=new LinkedList<CPUProcess>();
		LinkedList<CPUProcess> exec_list=new LinkedList<CPUProcess>();	
		LinkedList<Integer> avail_cpu_numbers=new LinkedList<Integer>();
		Address address=null;

		int serial_exe=0;
		int[] processor_avg=new int[number_of_CPUs];

		for(int i=0;i<number_of_CPUs;i++)
		{
			avail_cpu_numbers.add(i);
		}
		boolean[] cpu_no_is_not_avail = new boolean[number_of_CPUs];

		int min = number_of_CPUs;
		int cycle_length;
		int avail_cpu_no=0;
		int cpus_in_use=0;
		int temp=0;
		int finished=0;
		boolean wait=true;

		CPUProcess proc=null;
		CPUProcess wait_proc=null;
		gen_random_branch();



		/* Observation A: processes + wait + exec = total number of processes: True */
		while(!processes.isEmpty() || !wait_list.isEmpty() || !exec_list.isEmpty()) 
		{
			wait=true;
			cycle_length=0;
			/*Fills the job-queue with all the waiting object*/
			while(!wait_list.isEmpty())
			{
				wait_proc=wait_list.removeFirst();
				unblock(wait_proc);
				processes.addFirst(wait_proc);
			}
			/*Observation B: All registers are free: True */



			while(wait)
			{	
				if(!processes.isEmpty())
				{
					/* Invariant A: True*/
					proc=processes.removeFirst();
				}
				else
				{
					break;
				}
				/* 
				 *If a process has instructions CALL or LOOP, an process has been added. However, the invariant A is still true
				  		if a process have a CPU number, it has been generated by a process and inherits the parent's CPU number.
				 */
				//Section A:Location
				if(proc.get_cpu_no() > -1)
				{	
					if (cpu_no_is_not_avail[proc.get_cpu_no()])
					{

						wait_list.add(proc);
					}
					else
					{
						if(check_r_w(proc) && check_mem_loc(proc,exec_list)) //Checks if register or memory location is being written to
						{	
							if(!reg_is_blocked(proc) && !abs_addr_is_blocked(proc)) //Checks register dependency
							{

								assign_r_w(proc);
								exec_list.add(proc);
								cpu_no_is_not_avail[proc.get_cpu_no()]=true;
								if(exec_list.size()>=number_of_CPUs || wait_list.size() >=number_of_CPUs)
								{

									break;
								}
							}
							else
							{
								block_locations(proc);
								wait_list.add(proc);
							}
						}
						else
						{
							/*Every time process is is denied execution, the registers and memory locations must be be blocked to prevent hazards */
							if(processes.isEmpty())
							{
								block_locations(proc);
								wait_list.add(proc);
								break;
							}
							else
							{
								block_locations(proc);
								wait_list.add(proc);
							}
						}
					}
				}
				else //Section B: Location. Logic is same as in A, except for CPU numbers
				{

					if(check_r_w(proc) && check_mem_loc(proc,exec_list))
					{	
						if(!reg_is_blocked(proc) && !abs_addr_is_blocked(proc))
						{
							assign_r_w(proc);
							exec_list.add(proc);
							if(exec_list.size()>=number_of_CPUs || wait_list.size() >=number_of_CPUs)
							{
								break;
							}

						}
						else
						{
							block_locations(proc);
							wait_list.add(proc);
						}

					}
					else
					{

						if(processes.isEmpty())
						{
							block_locations(proc);
							wait_list.add(proc);
							break;
						}
						else
						{
							block_locations(proc);
							wait_list.add(proc);
						}
					}
				}
			}
			//Section C: execution
			while(!exec_list.isEmpty())
			{
				/*Removes the first object of the exec queue 
				 */
				proc=exec_list.removeFirst();

				if(proc.get_cpu_no()<=-1) //Job, not generated by another job
				{
					avail_cpu_no=avail_cpu_numbers.removeFirst();
					//The address needs to be checked if it has a processor associated with it
					if(proc.get_abs_address() !=null && proc.get_abs_address().get_binary_address() !=null && proc.get_abs_address().get_hex_address()!=null)
					{
						address = new Address(proc.get_abs_address().get_binary_address(),proc.get_abs_address().get_hex_address());
						if(addresses.containsKey(address.get_txt_bin_addr()))
						{
							address=addresses.get(address.get_txt_bin_addr()); //Checks the address HashMap
							if(address.contains_cpu(avail_cpu_no))
								proc.set_cpu_no(avail_cpu_no);
							else
							{
								avail_cpu_numbers.addLast(avail_cpu_no);
								avail_cpu_no=-2;
								for(Integer i:avail_cpu_numbers)
								{
									if(address.contains_cpu(i.intValue())) //Checks if other associated CPUs are available
									{
										avail_cpu_no=i.intValue();
										break;
									}
								}
								if(avail_cpu_no>-1) //assigns CPU, if there is an associated address
								{
									avail_cpu_numbers.remove(avail_cpu_no);
									proc.set_cpu_no(avail_cpu_no);
								}
								else  //assigns CPU, assigns any free CPU
								{
									avail_cpu_no=avail_cpu_numbers.removeLast();
									proc.set_cpu_no(avail_cpu_no);
									address.assign_cpu(avail_cpu_no);
								}
							}
						}
						else //Binds CPU to address
						{
							address.assign_cpu(avail_cpu_no);
							addresses.put(address.get_txt_bin_addr(),address);
							proc.set_cpu_no(avail_cpu_no);
						}
					}// assigns the first available CPU
					else
					{
						proc.set_cpu_no(avail_cpu_no);
					}
				}
				else //The job already has been assigned a CPU
				{

					avail_cpu_numbers.remove(proc.get_cpu_no());

				}
				//Executes instructions
				cycle_length=instr_exec(proc);

				proc.set_cycle_length(cycle_length);
				unassign_r_w(proc);
				temp++;
				avail_cpu_numbers.add(proc.get_cpu_no());

				finished++;
			}
			if(temp>cpus_in_use)
				cpus_in_use=temp;
			if(temp<min)
				min=temp;


			//Metrics
			if(temp>1)
			{
				processor_avg[temp-1]++;
			}
			else 
			{
				serial_exe++;
			}
			finish_result();

			cpu_no_is_not_avail=new boolean[number_of_CPUs];
			temp=0;
		}
		int sum_proc=0;
		int sum=0;

		for(int i=1;i<processor_avg.length;i++)
		{
			if(processor_avg[i]>0)
			{
				sum_proc+=processor_avg[i];
				sum+=processor_avg[i]*(i+1);	
			}
		}

		double s = (double)finished/((double)sum_proc+(double)serial_exe);
		double p= ((double)1-(1/s))*((double)number_of_CPUs/(number_of_CPUs-1));

		perf_metrics = new PerformanceMetric(s,p,finished, serial_exe,sum_proc,sum, memory_access);
		memory_access=0;

	}
	/* Simplified out-of-order execution */
	public void execute_proc_sepregs() 
	{
		LinkedList<CPUProcess> wait_list=new LinkedList<CPUProcess>();
		LinkedList<CPUProcess> exec_list=new LinkedList<CPUProcess>();	
		LinkedList<Integer> avail_cpu_numbers=new LinkedList<Integer>();
		Address address=null;

		int serial_exe=0;
		int[] processor_avg=new int[number_of_CPUs];

		for(int i=0;i<number_of_CPUs;i++)
		{
			avail_cpu_numbers.add(i);
		}
		boolean[] cpu_no_is_not_avail = new boolean[number_of_CPUs];

		int min = number_of_CPUs;
		int cycle_length;
		int avail_cpu_no=0;
		int cpus_in_use=0;
		int temp=0;
		int finished=0;
		boolean wait=true;

		CPUProcess proc=null;
		CPUProcess wait_proc=null;
		gen_random_branch();



		/* Observation A: processes + wait + exec = total number of processes: True */
		while(!processes.isEmpty() || !wait_list.isEmpty() || !exec_list.isEmpty()) 
		{
			wait=true;
			cycle_length=0;
			/*Fills the job-queue with all the waiting object*/
			while(!wait_list.isEmpty())
			{
				wait_proc=wait_list.removeFirst();
				unblock(wait_proc);
				processes.addFirst(wait_proc);
			}
			/*B: All registers are free: Always True */



			while(wait)
			{	
				if(!processes.isEmpty())
				{
					/* Invariant A: True*/
					proc=processes.removeFirst();
				}
				else
				{
					break;
				}
				/* 
				 *If a process has instructions CALL or LOOP, an process has been added. However, the invariant A is still true
				  		if a process have a CPU number, it has been generated by a process and inherits the parent's CPU number.
				 */
				//Section A:Location
				if(proc.get_cpu_no() > -1)
				{	
					if (cpu_no_is_not_avail[proc.get_cpu_no()])
					{

						wait_list.add(proc);
					}
					else
					{
						if(check_mem_loc(proc,exec_list)) //Checks if register or memory location is being written to
						{	
							if(!abs_addr_is_blocked(proc)) //Checks memora access dependency
							{
								assign_r_w(proc);
								exec_list.add(proc);
								cpu_no_is_not_avail[proc.get_cpu_no()]=true;
								if(exec_list.size()>=number_of_CPUs || wait_list.size() >=number_of_CPUs)
								{

									break;
								}
							}
							else
							{
								block_locations(proc);
								wait_list.add(proc);
							}
						}
						else
						{
							/*Every time process is is denied execution, the registers and memory locations must be be blocked to prevent hazards */
							if(processes.isEmpty())
							{
								block_locations(proc);
								wait_list.add(proc);
								break;
							}
							else
							{
								block_locations(proc);
								wait_list.add(proc);
							}
						}
					}
				}
				else //Section B: Location. Logic is same as in A, except for CPU numbers
				{

					if(check_mem_loc(proc,exec_list))
					{	
						if(!abs_addr_is_blocked(proc))
						{
							assign_r_w(proc);
							exec_list.add(proc);
							if(exec_list.size()>=number_of_CPUs 
							 || wait_list.size() >=number_of_CPUs)
							{
								break;
							}

						}
						else
						{
							block_locations(proc);
							wait_list.add(proc);
						}

					}
					else
					{

						if(processes.isEmpty())
						{
							block_locations(proc);
							wait_list.add(proc);
							break;
						}
						else
						{
							block_locations(proc);
							wait_list.add(proc);
						}
					}
				}
			}
			//Section C: execution
			while(!exec_list.isEmpty())
			{
				/*Removes the first object of the exec queue 
				 */
				proc=exec_list.removeFirst();

				if(proc.get_cpu_no()<=-1) //Job, not generated by another job
				{
					avail_cpu_no=avail_cpu_numbers.removeFirst();
					//The address needs to be checked if it has a processor associated with it
					if(proc.get_abs_address() !=null 
						&& proc.get_abs_address().get_binary_address() !=null 
						&& proc.get_abs_address().get_hex_address()!=null)
					{
						address = new Address(proc.get_abs_address().get_binary_address(),
								  proc.get_abs_address().get_hex_address());
						if(addresses.containsKey(address.get_txt_bin_addr()))
						{
							address=addresses.get(address.get_txt_bin_addr()); //Checks the address HashMap
							if(address.contains_cpu(avail_cpu_no))
								proc.set_cpu_no(avail_cpu_no);
							else
							{
								avail_cpu_numbers.addLast(avail_cpu_no);
								avail_cpu_no=-2;
								for(Integer i:avail_cpu_numbers)
								{
									if(address.contains_cpu(i.intValue())) //Checks if other associated CPUs are available
									{
										avail_cpu_no=i.intValue();
										break;
									}
								}
								if(avail_cpu_no>-1) //assigns CPU, if there is an associated address
								{
									avail_cpu_numbers.remove(avail_cpu_no);
									proc.set_cpu_no(avail_cpu_no);
								}
								else  //assigns CPU, assigns any free CPU
								{
									avail_cpu_no=avail_cpu_numbers.removeLast();
									proc.set_cpu_no(avail_cpu_no);
									address.assign_cpu(avail_cpu_no);
								}
							}
						}
						else //Binds CPU to address
						{
							address.assign_cpu(avail_cpu_no);
							addresses.put(address.get_txt_bin_addr(),address);
							proc.set_cpu_no(avail_cpu_no);
						}
					}// assigns the first available CPU
					else
					{
						proc.set_cpu_no(avail_cpu_no);
					}
				}
				else //The job already has been assigned a CPU
				{

					avail_cpu_numbers.remove(proc.get_cpu_no());

				}
				//Executes instructions
				cycle_length=instr_exec(proc);

				proc.set_cycle_length(cycle_length);
				unassign_r_w(proc);
				temp++;
				avail_cpu_numbers.add(proc.get_cpu_no());

				finished++;
			}
			if(temp>cpus_in_use)
				cpus_in_use=temp;
			if(temp<min)
				min=temp;


			//Metrics
			if(temp>1)
			{
				processor_avg[temp-1]++;
			}
			else 
			{
				serial_exe++;
			}
			finish_result();

			cpu_no_is_not_avail=new boolean[number_of_CPUs];
			temp=0;
		}
		int sum_proc=0;
		int sum=0;

		for(int i=1;i<processor_avg.length;i++)
		{
			if(processor_avg[i]>0)
			{
				sum_proc+=processor_avg[i];
				sum+=processor_avg[i]*(i+1);	
			}
		}

		double s = (double)finished/((double)sum_proc+(double)serial_exe);
		double p= ((double)1-(1/s))*((double)number_of_CPUs/(number_of_CPUs-1));

		perf_metrics = new PerformanceMetric(s,p,finished, serial_exe,sum_proc,sum, memory_access);
		memory_access=0;

	}
	
	
	/*Methods for locking and blocking */
	private void unblock(CPUProcess proc) {
		AbstractAddress_x86 abs_addr=proc.get_abs_address();
		Register reg=null;
		if(abs_addr!=null)
			abs_addr.unblock();

		for(int i = 0;i<proc.get_number_of_registers();i++)
		{
			reg=proc.get_register(i);
			if(reg!=null)
				reg.unblock();
		}
	}
	private void block_locations(CPUProcess proc) {

		AbstractAddress_x86 abs_addr=proc.get_abs_address();
		Register reg=null;
		if(abs_addr!=null)
			abs_addr.block();

		for(int i = 0;i<proc.get_number_of_registers();i++)
		{
			reg=proc.get_register(i);
			if(reg!=null)
				reg.block();
		}
	}
	private boolean abs_addr_is_blocked(CPUProcess proc) {

		AbstractAddress_x86 abs_addr=proc.get_abs_address();

		if(abs_addr!=null)
		{		

			if(abs_addr.is_blocked())
				return true;
		}
		return false;
	}
	private boolean reg_is_blocked(CPUProcess proc) { // Some registers are 

		Register reg=null;

		for(int i = 0;i<proc.get_number_of_registers();i++)
		{
			reg=proc.get_register(i);
			if(reg != null && reg.is_blocked())
				return true;
		}
		return false;
	}
	public boolean check_address(CPUProcess proc,LinkedList<Integer> avail_cpu_numbers,LinkedList<CPUProcess> exec_list)
	{

		if(proc.get_abs_address()!=null)
		{
			assign_memory_address(proc);
			if(!compare_address(proc,exec_list))
			{
				if(!avail_cpu_numbers.isEmpty())
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			else 
			{
				return false;
			}
		}
		else
		{		
			return true;
		}
	}
	public LinkedList<CPUProcess> next_cycle(LinkedList<CPUProcess> exec_list)
	{
		for(CPUProcess p:exec_list)
		{
			if(p.get_cycle_length()>0)
				p.dec_cycle_length();
			else
				p.set_finished(true);
		}
		return exec_list;
	}
	public boolean compare_address(CPUProcess proc,LinkedList<CPUProcess> exec_list)
	{
		boolean mismatch=false;
		AbstractAddress_x86 abs_addr=proc.get_abs_address();
		AbstractAddress_x86 temp_abs_addr=null;
		short[] hex_address=abs_addr.get_hex_address();
		short[] temp_address=null;
		for(CPUProcess p:exec_list)
		{
			temp_abs_addr=p.get_abs_address();
			if(temp_abs_addr!=null)
			{	
				temp_address=temp_abs_addr.get_hex_address();
				for(int i=0;i<hex_address.length;i++)
				{
					if(temp_address[i]!=hex_address[i]){
						mismatch=true;
						break;
					}
				}
				if(!mismatch)
					return true;
			}
		}
		return false;
	}
	//actual execution of instructions
	private int instr_exec(CPUProcess proc) {

		ActualInstruction_x86 instruction=proc.get_instruction();
		boolean is_mov=instruction.is_mov();
		boolean is_pop=instruction.is_pop();
		boolean is_push=instruction.is_push();
		boolean is_lea=instruction.is_lea();
		boolean is_call=instruction.is_call();
		boolean is_loop=instruction.is_loop();
		short d_bit=instruction.get_d_bit();
		short mod=instruction.get_mod();
		short[] data=null;
		short[] temp=null;
		short[] hex_address=null;
		boolean[] bin_address=null;
		Register register=null;
		Register[] registers=proc.get_registers();
		AbstractAddress_x86 abs_addr;
		int cycle_length=0;
		Result result=null;
		if(is_mov)
		{
			memory_access++;
			if(proc.get_number_of_registers()==2)
			{
				if(mod<0x03) //memory operation 
				{
					if(d_bit==0x00) //write to mem
					{
						register=proc.get_register(0x01);
						abs_addr=proc.get_abs_address();
						cycle_length++;
						temp=abs_addr.get_register_value();

						data=new short[temp.length];
						for(int i=0;i<temp.length;i++)
						{
							data[i]=temp[i];
						}
						abs_addr.set_data(data);
						cycle_length++;
						hex_address=save_hex_address(bus.get_next_hex_address());
						bin_address=save_bin_address(bus.get_next_bin_address());

						abs_addr.set_address(hex_address);

						result=new Result(proc,hex_address,bin_address,data);
						result_list.add(result);

						return cycle_length;
					}
					else //read from mem
					{
						abs_addr=proc.get_abs_address();
						registers=proc.get_registers();
						short data_length=(short) (proc.get_instruction().get_opsizes()[0x01]/8);
						if(abs_addr!=null)
						{
							temp=read_MESIF(proc.get_cpu_no(),abs_addr.get_hex_address(),abs_addr.get_binary_address(),data_length);
						}
						if(temp!=null)
						{
							data=new short[temp.length];
							for(int i=0;i<temp.length;i++)
							{
								data[i]=temp[i];
							}
							ActualInstruction_x86 instr=proc.get_instruction();
							if(instr.has_immediate())
							{
								boolean[] b=abs_addr.get_binary_address(); 
								short[] h= abs_addr.get_hex_address();

								result=new Result(proc,h,b,data);
								result_list.add(result);
							}
							else{

								boolean[] b=abs_addr.get_binary_address(); 
								short[] h= abs_addr.get_hex_address();
								result=new Result(proc,h,b,data);
								result_list.add(result);

							}
							return 5;
						}
						else
						{
							int l=proc.get_instruction().get_opsizes()[0x00];
							data=random_data(l);
							boolean[] b=abs_addr.get_binary_address(); 
							short[] h= abs_addr.get_hex_address();
							result=new Result(proc,h,b,data);
							result_list.add(result);
						}
					}
				}
				else
				{
					registers=proc.get_registers();

					abs_addr=proc.get_abs_address();
					temp=abs_addr.get_register_value();
					data=new short[temp.length];
					for(int i=0;i<temp.length;i++)
					{
						data[i]=temp[i];
					}

					result=new Result(proc,hex_address,bin_address,data);
					result_list.add(result);

					return 3;
				}
			}
			else if(proc.get_number_of_registers()==0x01)
			{
				registers=proc.get_registers();
				temp=registers[0x00].get_data();
				data=new short[temp.length];
				for(int i=0;i<temp.length;i++)
				{
					data[i]=temp[i];
				}
				ActualInstruction_x86 instr=proc.get_instruction();
				if(instr.has_immediate() || instr.has_offset() || instr.has_pointer() || instr.has_relative_address())
				{
					abs_addr=proc.get_abs_address();
					boolean[] b=abs_addr.get_binary_address(); 
					short[] h= abs_addr.get_hex_address();

					result=new Result(proc,h,b,data);
					result_list.add(result);
				}
			}
			else if(proc.get_number_of_registers()==0x00)
			{
				abs_addr=proc.get_abs_address();
				boolean[] b=abs_addr.get_binary_address(); 
				short[] h= abs_addr.get_hex_address();
				data=abs_addr.get_register_value();
				result=new Result(proc,h,b,data);
				result_list.add(result);
			}
		}
		else if(is_lea)
		{
			memory_access++;
			if(mod<0x03) //memory operation, Always true for LEA
			{
				if(d_bit==0x00)
				{
					register=proc.get_register(0x01);
					abs_addr=proc.get_abs_address();
					cycle_length++;
					temp=register.get_data();
					data=new short[temp.length];
					for(int i=0;i<temp.length;i++)
					{
						data[i]=temp[i];
					}
					abs_addr.set_data(data);
					cycle_length++;
					hex_address=save_hex_address(bus.get_next_hex_address());
					abs_addr.set_address(hex_address);

					hex_address=save_hex_address(bus.get_next_hex_address());
					bin_address=save_bin_address(bus.get_next_bin_address());

					bus.inc_address(1);
					result=new Result(proc,hex_address,bin_address,data);
					result_list.add(result);
					cycle_length+=3;
					return cycle_length;
				}
				else
				{
					abs_addr=proc.get_abs_address();
					registers=proc.get_registers();
					temp=abs_addr.get_hex_address();
					data=new short[temp.length];
					for(int i=0;i<temp.length;i++)
					{
						data[i]=temp[i];
					}
					ActualInstruction_x86 instr=proc.get_instruction();
					if(instr.has_immediate())
					{
						boolean[] b=abs_addr.get_binary_address(); 
						short[] h= abs_addr.get_hex_address();

						write(proc.get_cpu_no(),data.length,bin_address,hex_address,data);
						b=bus.inc_bin_address(b);
						h=bus.inc_hex_address(h);

					}
					else{

						registers[0x00].write(data);
					}
					return 5;
				}
			}
		}
		else if(is_push) //No function
		{			
			return 3;
		}
		else if(is_pop) //No function
		{

			return 3;
		}
		else if(is_call) //random procedure call. Creates process to be added first in the list
		{

			Random random= new Random();

			int rdm_index=random.nextInt() % (process_overview.size());
			if(rdm_index<-1)
			{
				rdm_index*=-1;
			}
			rdm_index--;

			int rdm_length =(random.nextInt() % 100);
			if(rdm_length<-1)
			{
				rdm_length*=-1;
			}
			rdm_length++;
			int j=rdm_index + rdm_length-1;
			CPUProcess old_proc=null;
			while(j>(rdm_index-1)) //Starts at the last element within the paramets
			{
				if((rdm_index+rdm_length)<process_overview.size()) //Checks if possible
				{
					old_proc=process_overview.get(j);
					old_proc=new CPUProcess(old_proc.get_abs_address(), old_proc.get_instruction(), old_proc.get_registers(), old_proc.get_cpu_no(), 
							old_proc.get_sequence_order(),old_proc.get_number_of_registers());
					create_processes(old_proc.get_instruction(),proc.get_cpu_no(),proc.get_sequence_order(),rdm_index, rdm_length, old_proc);
					j--;
				}
				else //Reduces length if to close to end
				{
					rdm_length--;
					j=rdm_index + rdm_length-1;
				}
			}
		}
		else if(is_loop) //Creates a loop randomly, works same as CALL, but with a an iteration value
		{

			Random random= new Random();

			int rdm_index=random.nextInt() % (process_overview.size());
			if(rdm_index<-1)
			{
				rdm_index*=-1;
			}
			rdm_index--;

			int rdm_length =(random.nextInt() % 100);
			if(rdm_length<-1)
			{
				rdm_length*=-1;
			}
			rdm_length++;
			int rdm_iterations =(random.nextInt() % 1000); 
			if(rdm_iterations<-1)
			{
				rdm_iterations*=-1;
			}
			rdm_iterations++;
			int j=0;
			int i= 0;
			CPUProcess old_proc=null;
			while(i<rdm_iterations)
			{
				j=rdm_index + rdm_length-1;
				while(j>(rdm_index-1))
				{
					if((rdm_index+rdm_length)<process_overview.size())
					{
						old_proc=process_overview.get(j);
						old_proc=new CPUProcess(old_proc.get_abs_address(), old_proc.get_instruction(), old_proc.get_registers(), old_proc.get_cpu_no(), 
								old_proc.get_sequence_order(),old_proc.get_number_of_registers());
						create_processes(old_proc.get_instruction(),proc.get_cpu_no(),proc.get_sequence_order(),rdm_index, rdm_length, old_proc);
						j--;
					}
					else
					{
						rdm_length--;
						j=rdm_index + rdm_length-1;
					}
				}
				j=rdm_index + rdm_length-1;
				i++;
			}
		}
		else //Other instructions
		{

			ActualInstruction_x86 instr = proc.get_instruction();
			if(instr.has_relative_address())
			{
				//random jump
				Random random= new Random();
				boolean cond=random.nextBoolean();
				if(cond)

					return 3;
			}
			return 5;
		}


		return 5;
	}
	//Finish executions in order
	public void finish_result()
	{
		Result result=null;
		Register register=null;
		CPUProcess p=null;

		Collections.sort(result_list,new Comparator<Result>() { 

			@Override
			public int compare(Result r1, Result r2) {

				CPUProcess p1=r1.get_proc();
				CPUProcess p2=r2.get_proc();

				if(p1.get_sequence_order()>p2.get_sequence_order())
				{
					return 1;
				}
				else if(p1.get_sequence_order()<p2.get_sequence_order())
				{
					return -1;
				}
				return 0;
			}});

		while(result_list.size()>0)
		{
			result=result_list.remove(0);

			if(result.get_hex_addr()==null && result.get_bin_addr()==null) //Atomic
			{
				p=result.get_proc();
				register = p.get_register(0);
				if(register!=null)
					register.write(result.get_data());
			}
			else
			{
				p=result.get_proc();
				write(p.get_cpu_no(),result.get_data().length,result.get_bin_addr(),result.get_hex_addr(),result.get_data());
			}

		}
	}
	private void write(int i,int length, boolean[] bin_address, short[] hex_address, short[] data) //Used to write
	{
		CPUs[i].write(bin_address, hex_address, data);
	}
	public short[] read_MESIF(int cpu_no,short[] hex_address,boolean[] bin_address,short data_length)
	{
		LinkedList<Entry> entries=CPUs[cpu_no].read(0,hex_address, bin_address, data_length,prefetch);
		short[] data=new short[entries.size()];
		int k=0;
		for(Entry e: entries)
		{
			if(e!=null){
				data[k]=e.get_data();
			}
		}
		return data;
	}
	private boolean[] save_bin_address(boolean[] bin_address) {
		boolean[] address=new boolean[bin_address.length];
		for(int i=0;i<bin_address.length;i++)
			address[i]=bin_address[i];

		return address;
	}
	private short[] save_hex_address(short[] hex_address) {
		short[] address=new short[hex_address.length];
		for(int i=0;i<hex_address.length;i++)
			address[i]=hex_address[i];

		return address;
	}
	public void assign_r_w(CPUProcess proc)
	{	
		Register register=null;
		Register[] registers=proc.get_registers();
		AbstractAddress_x86 abs_addr=proc.get_abs_address();

		for(int i=0;i<registers.length;i++)
		{
			register=registers[i];
			if(register!=null)
			{

				if(proc.is_write_to_memory())
				{ 
					if(i!=0x01)
					{
						register.inc_number_of_readers();
					}
				}
				else if(proc.is_read_from_memory())
				{
					if(i==0x01)
						register.set_is_write(true);
				}
			}
			else break;
		}
		if(abs_addr!=null)
		{
			if(proc.is_write_to_memory())
			{
				abs_addr.set_is_write(true);
			}
			else
			{
				abs_addr.inc_number_of_readers();
			}
		}
	}
	public void unassign_r_w(CPUProcess proc)
	{	
		Register register=null;
		Register[] registers=proc.get_registers();
		AbstractAddress_x86 abs_addr=proc.get_abs_address();

		for(int i=0;i<registers.length;i++)
		{
			register=registers[i];
			if(register!=null)
			{
				if(register.is_write())
				{
					register.set_is_write(false);
				}
				else if(register.get_number_of_readers()>0)
					register.dec_number_of_readers();
			}
		}

		if(abs_addr!=null)
		{
			if(abs_addr.is_write())
			{
				abs_addr.set_is_write(false);
			}
			else if(abs_addr.get_readers()>0)
			{
				abs_addr.dec_number_of_readers();
			}
		}
	}
	public boolean check_r_w(CPUProcess proc)
	{
		Register[] registers=proc.get_registers();
		Register register=null;
		for(int i=0;i<registers.length;i++)
		{
			register=registers[i];
			if(proc.is_read_from_memory())
			{
				if(register!=null)
				{
					if(register.is_write())
						return false;
					else if(register.get_number_of_readers()>0)
						return false;


				}
				else return true;

			}
			else if(proc.is_write_to_memory())
			{
				if(register!=null)
				{
					if(register.is_write())
						return false;

				}
				else return true;
			}


		}
		return true;	
	}

	public boolean check_mem_loc(CPUProcess proc,LinkedList<CPUProcess> exec_list) //FIX
	{
		AbstractAddress_x86 abs_addr=proc.get_abs_address();
		AbstractAddress_x86 temp=null;

		if(proc.is_read_from_memory())
		{
			if(abs_addr!=null)
			{
				for(CPUProcess p:exec_list)
				{
					//if(p.finished()) 
					{
						temp=p.get_abs_address();
						if( temp!=null)
							if(compare_address(abs_addr,temp))
							{
								if(temp.is_write())
									return false;
							}
					}
					if(abs_addr.is_write())
						return false;
				}

			}
			else return true;
		}
		else if(proc.is_write_to_memory())
		{
			if(abs_addr!=null)
			{
				for(CPUProcess p:exec_list)
				{
					//if(p.finished()) 
					{
						temp=p.get_abs_address();
						if(temp!=null){
							if(compare_address(abs_addr,temp))
							{
								if(temp.is_write())
									return false;
								else if(temp.get_readers()>0)
									return false;
							}
						}
					}
				}
				return true;

			}
			else return true;
		}
		else return true;
		return true;
	}
	private boolean compare_address(AbstractAddress_x86 abs_addr, AbstractAddress_x86 temp) {

		short[] mem_addr_1=null;
		short[] mem_addr_2=null;
		if(abs_addr!=null && temp!=null)
		{
			mem_addr_1=abs_addr.get_hex_address();
			mem_addr_2=temp.get_hex_address();
			if(mem_addr_1!=null && mem_addr_2!=null)
			{
				for(int i=0;i<mem_addr_1.length;i++)
				{
					if(mem_addr_1[i]!=mem_addr_2[i])
						return false;
				}
				return true;
			}
			else return false;
		}
		return false;
	}

	private short[] generate_hex_data(int length,boolean is_random) {


		if(is_random)
		{
			Random random=new Random();
			short[] hex_data=new short[length/8];
			int bound=0x100;

			for(int i=0;i<length/8;i++)
			{
				hex_data[i]=(short)(random.nextInt(bound) % bound);
			}
			return hex_data;
		}
		else
			return memory_handler.get_next_hex_address();
	}
	public CPU[] get_CPUs() {
		return CPUs;
	}
	//Methods intermediate: invd,flush, search

	public LinkedList<Slot> find_slots(int cpu_no,int length, boolean[] start_bin_addr, LinkedList<Slot> slots) {
		return CPUs[cpu_no].find_slots(start_bin_addr,slots,length);
	}
	public void invalidate_slot(int cpu_no, boolean[] address) {
		CPUs[cpu_no].invalidate_slot(address);		
	}
	public void flush(int cpu_no, boolean[] start_bin_addr, short[] start_hex_addr, Slot valid_slot) {

		CPUs[cpu_no].flush(start_bin_addr,start_hex_addr,valid_slot);
	}
	public LinkedList<Slot> change_F(int cpu_no, boolean[] bin_addr,LinkedList<Slot> list) {
		return CPUs[cpu_no].change_F(bin_addr,list);

	}
	public LinkedList<Slot> change_E(int cpu_no,boolean[] bin_addr, LinkedList<Slot> list) {

		list=CPUs[cpu_no].change_E(bin_addr,list);

		return null;
	}
	






}

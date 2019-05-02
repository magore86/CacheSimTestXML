package io;



import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import controller.CPUController;
import controller.ICPU;
import hardware.Opcodes;

/* Main I/O class: Contains calculation of instruction length and file reading */
public class FileReader implements ICPU{

	private Path path;
	private FileInputStream input;
	private short input_byte;
	private int address_length;
	
	private boolean is_two_byte;
	private boolean has_rex;
	private short default_mode = 32;
	public short[] next_available_address;
	private Instruction_x86 instruction_x86;
	private ExeFileReader exe_file_reader;
	private ExeHeader exe_header;
	private ArrayList<Short> byte_al;
	private Process process;
	
	private CPUController cpu_controller;


	public FileReader(String path_name, int address_length)
	{
		path = Paths.get(path_name);
		this.address_length=address_length;

		cpu_controller=cpu_controllers.peekFirst();
	}
	public boolean run_file(File file)
	{
		try
		{	
			input=new FileInputStream(file.toString());
			exe_file_reader=new ExeFileReader(input);
			exe_header=exe_file_reader.read_header();
			
			return true;
		}
		catch (Exception e)
		{	
			
			return false;
		}
		
	}
	public boolean close_file()
	{
		try
		{			
			input.close();
			return true;	
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public void add_to_list(boolean space) //add all data to list, then read all sections
	{
		try
		{	
			byte_al=new ArrayList<Short>();
			
			short s=(short)input.read();
			while(s!=-1)
			{
				
				byte_al.add(s);
				s=(short)input.read();
			}
			cpu_controller.progr_to_mem(byte_al,address_length);
			
		}
		catch(Exception e)
		{
			close_file();
		}
		finally
		{
			close_file();
		}
	}
	public void read(boolean seperate_registers) //add all data to list, then read all sections
	{
		
			int[] pointers=exe_header.get_r_pointer();
			int[] r_sizes=exe_header.get_raw_data_size();
			int no_of_sections=exe_header.get_number_of_sections();
			int current_position=exe_header.get_start();
			int start=0;
			int stop=Integer.MAX_VALUE;
			
			for(int i=0;i<no_of_sections;i++)
			{
				start=pointers[i];
				current_position=start;
				stop=pointers[i]+r_sizes[i];
				while(current_position<stop) //Iterates through the input bytes per section
				{
					ActualInstruction_x86 instruction=check_length(current_position);
					cpu_controller.create_processes(instruction,-1,-1,-1,-1,null);
					current_position=current_position+instruction.get_length();
				}	
			}
			if(seperate_registers)
			{
				cpu_controller.execute_proc_sepregs();
			}
			else
				cpu_controller.execute_proc();
			
		
		
		
	}
	//Calculating instruction length
	public ActualInstruction_x86 check_length(int start_index)
	{


		ActualInstruction_x86 current_instruction=null;
		
		int[] op_sizes={default_mode,default_mode,-1,-1};

		//Some of these can be used with extending the program
		short mode = default_mode;
	

		boolean has_imm=false;;
		boolean is_invalid=false;;
	
		boolean is_push=false;
		boolean is_pop=false;
		boolean is_mov=false;
		boolean is_cmov=false;
	
		boolean is_lea=false;;
		boolean is_memory_address=false;
		boolean is_xchg=false;
		boolean is_bound=false;
		boolean is_pause=false;
		boolean is_test=false;
		boolean is_no_op=false;
		int mem_size;
		boolean is_rel=false;;
		short rel_length=-1;
		boolean has_pointer=false;
		short pointer_length=-1;
		boolean has_offset=false;
		short offset_length=-1;
		boolean has_memory_address = false;
		short address_length=-1;
		short address_type=-1;
		boolean is_FPU_ins = false;
		boolean has_real=false;
		short reg_FPU_size = -1;
		short fpu_mode=0x80;
		boolean is_call_f=false;
		short f_size=-1;

		boolean[] reg_purpose=new boolean[0x06];
		short[] reg_purposes_indexes = {0,0,0,0};

		boolean is_sreg;
		boolean is_eee_1;
		boolean is_eee_2;

		boolean has_fixed_register_1;
		boolean has_fixed_register_2;
		String fixed_register_1;
		String fixed_register_2;

		String segment="";
		String[] segments = {"ES","CS","SS","DS"};
		int seg_index;

		short current_position=0; //prefix={0,1,2,3},opcodes{4,5,6},ext/modrm={7},sib={7},disp{7,8},imm[7,8,9,10,11,12}
		short[] prefixes = {-1,-1,-1,-1};
		short[] opcodes = {-1,-1,-1};
		short pri_op = -1;
		short sec_op =-1;
		short tert_op = -1;
		short ext = -1;
		short ext_no = -1;

		int w=-1;
		int d=-1;
		short modrm=-1;
		short sib=-1;
		short displacement;
		short[] imm_size={-1,-1,-1,-1};
		short[] ins_opcodes= {-1,-1,-1};

		boolean has_flag;
		boolean has_flags;
		boolean has_1;
		boolean has_rel = false;
		Instruction_x86 instruction;
		String instruction_string="";

		instruction_x86 = new Instruction_x86();

		/*Some of these are not used, but can be used in extension of the program*/
		boolean has_register=false;
		boolean has_disp=false;
		boolean has_sib=false;
		boolean has_modr_m=false;
		boolean has_ext=false;
		boolean has_sec=false;
		boolean has_tert=false;
		boolean has_extension=false;
		boolean has_immediate=false;
		boolean has_prefix=false;
		boolean has_prefix_0x66=false;
		boolean has_prefix_0xF2=false;
		boolean has_prefix_0xF3=false;
		boolean is_prefix=false;
		boolean is_rex_prefix=false;
		boolean add_reg=false;
		boolean is_NOP;
		boolean hint_NOP;
		boolean is_prefetch;
		boolean no_reg=false;
		boolean is_wait=false;
		boolean is_mwait=false;
		boolean is_lock=false;
		boolean is_call=false;
		boolean is_loop=false;
		short number_of_ops = 0;
		short instruction_length=0;
		
		int current_index=start_index;

		


		

		try
		{
			input_byte=byte_al.get(current_index);
			current_index++;
			
			current_position=0;

			for(int i=0;i<0x04;i++)
			{

				if(input_byte==0x26)
				{
					has_prefix=true;
					is_prefix=true;
					prefixes[i]=input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					current_position++;
				}
				else if(input_byte==0x2E)
				{
					has_prefix=true;
					is_prefix=true;
					prefixes[i]=input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					current_position++;
				}
				else if(input_byte==0x36)
				{
					has_prefix=true;
					is_prefix=true;
					prefixes[i]=input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					current_position++;
				}
				else if(input_byte==0x3E)
				{
					has_prefix=true;
					is_prefix=true;
					prefixes[i]=input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					current_position++;
				}
				else if(input_byte>=0x40 && input_byte<0x48)
				{
					if(mode==0x40)
					{
						is_prefix=true;
						is_rex_prefix=true;
						input_byte=byte_al.get(current_index);
						current_index++;
						current_position++;
					}
					else
					{
						add_reg=true;
						number_of_ops=1;
					}
				}
				else if(input_byte==0x48 && input_byte<50)
				{
					if(mode==0x40)
					{
						is_prefix=true;
						is_rex_prefix=true;
						prefixes[i]=input_byte;
						input_byte=byte_al.get(current_index);
						current_index++;
						current_position++;
					}
					else
					{
						add_reg=true;
						number_of_ops=1;
						break;
					}
				}
				else if(input_byte>=0x64 && input_byte<0x68)
				{
					has_prefix=true;
					if(input_byte==0x66)
						has_prefix_0x66=true;
					is_prefix=true;
					prefixes[i]=input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					current_position++;
				}
				else if(input_byte==0x9B)
				{
					has_prefix=true;
					is_prefix=true;
					prefixes[i]=input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					current_position++;
				}
				else if(input_byte==0xF0)
				{
					has_prefix=true;
					is_prefix=true;
					prefixes[i]=input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					current_position++;
				}
				else if(input_byte==0xF2)
				{
					has_prefix=true;
					has_prefix_0xF2=true;
					is_prefix=true;
					prefixes[i]=input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					current_position++;
				}
				else if(input_byte==0xF3)
				{
					has_prefix=true;
					has_prefix_0xF3=true;
					is_prefix=true;
					prefixes[i]=input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					current_position++;
				}
				else
				{
					break;
				}
			}
			if(input_byte==0x0F)
			{
				is_two_byte=true;
				pri_op=0x0F;
				ins_opcodes[0]=pri_op;
				input_byte=byte_al.get(current_index);
				current_index++;
				current_position=5; //approximate of byte-class
				ins_opcodes[1]=input_byte;
				sec_op=input_byte;
			}
			else
			{
				pri_op=input_byte;
				ins_opcodes[0]=input_byte;
				current_position=4;
			}
			if(is_two_byte)
			{

				if(sec_op==0x00)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					number_of_ops=2;
					
					if(ext_no>=0x02)
					{
						op_sizes[1]=0x10;
						op_sizes[0]=0x10;
					}
				}
				else if(sec_op==0x1)
				{
					if(input_byte>=0xC1 && input_byte<0xC5)
					{
						has_ext=true;
						tert_op=input_byte;
						ins_opcodes[2]=tert_op;
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
						number_of_ops=0;
					}
					else if(input_byte==0xC8)
					{
						has_ext=true;
						tert_op=input_byte;
						ins_opcodes[2]=tert_op;
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
						number_of_ops=3;

						address_length=0x08;
						
						if(mode==0x40)
							address_type=0x02;
						else address_type=0x01;
						
						op_sizes[2]=0x20;
						op_sizes[1]=0x20;
						op_sizes[0]=0x08;
					}
					else if(input_byte==0xC9)
					{
						has_ext=true;
						tert_op=input_byte;
						ins_opcodes[2]=tert_op;
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
						number_of_ops=2;
						is_mwait=true;
						op_sizes[1]=0x20;
						op_sizes[0]=0x20;
					}
					else
					{
						input_byte=byte_al.get(current_index);
						current_index++;
						if(input_byte>0x07)
						{
							tert_op=input_byte;
							ins_opcodes[2]=tert_op;
							has_tert=true;
							input_byte=byte_al.get(current_index);
							current_index++;
							ext_no=instruction_x86.get_ext(input_byte);
							current_position=7;
						}
						else	
						{
							ext_no=instruction_x86.get_ext(input_byte);
							current_position=7;
						}
						
						if(ext_no==0x00)
						{
							number_of_ops=2;
						}
						else if(ext_no==0x01)
						{

							if(has_tert)
							{
								if(tert_op==0xC8)
								{
									number_of_ops=3;
									op_sizes[2]=0x20;
									op_sizes[1]=0x20;
									op_sizes[0]=0x08;

								}
								else if(tert_op==0xC9)
								{
									number_of_ops=2;
									op_sizes[1]=0x20;
									op_sizes[0]=0x20;
								}

							}
							else
							{
								number_of_ops=2;
							}
						}
						else if(ext_no==0x02)
						{
							if(has_tert)
							{
								if(tert_op==0xD0)
								{
									number_of_ops=4;
									op_sizes[3]=0x20;
									op_sizes[2]=0x20;
									op_sizes[1]=0x20;
									op_sizes[0]=0x20;
								}
								else if(tert_op==0xD1)
								{
									number_of_ops=4;
									op_sizes[3]=0x20;
									op_sizes[2]=0x20;
									op_sizes[1]=0x20;
									op_sizes[0]=0x20;
								}
							}
							else	
								number_of_ops=2;
						}
						else if(ext_no==0x03)
						{
							number_of_ops=2;
						}
						else if(ext_no==0x04)
						{
							number_of_ops=2;
						}
						else if(ext_no==0x05)
						{
							number_of_ops=2;
						}
						else if(ext_no==0x06)
						{
							number_of_ops=2;
							op_sizes[1]=0x10;
							op_sizes[0]=0x10;
						}
						else if(ext_no==0x07)
						{
							if(has_tert)
							{
								if(tert_op==0xF8)
								{
									number_of_ops=2;
									op_sizes[1]=0x20;
									op_sizes[0]=0x20;
								}
								else if(tert_op==0xF9)
								{
									number_of_ops=3;
									op_sizes[2]=0x20;
									op_sizes[1]=0x20;
									op_sizes[0]=0x20;
								}
							}
							else
								number_of_ops=1;
						}
					}
				}
				else if(sec_op>=0x2 && sec_op<0x4)
				{
					has_modr_m=true;
					number_of_ops=2;

				}
				else if(sec_op==0x4)
				{
					//Nothing
				}
				else if(sec_op==0x5)
				{
					number_of_ops=3;
					op_sizes[2]=0x40;
					op_sizes[1]=0x40;
					op_sizes[0]=0x40;
				}
				else if(sec_op==0x6)
				{
					number_of_ops=1;
				}
				else if(sec_op==0x7)
				{
					number_of_ops=3;
					op_sizes[2]=0x40;
					op_sizes[1]=0x40;
					op_sizes[0]=0x40;
				}
				else if(sec_op==0x8)
				{
					number_of_ops=0;
				}
				else if(sec_op==0x9)
				{
					number_of_ops=0;
				}
				else if(sec_op==0xa)
				{
					//Nothing
				}
				else if(sec_op==0xb)
				{
					number_of_ops=0;
				}
				else if(sec_op==0xc)
				{
					//Nothing
				}
				else if(sec_op==0xd)
				{
					number_of_ops=1;
				}
				else if(sec_op==0xe)
				{
					//Nothing
				}
				else if(sec_op==0xf)
				{
					//Nothing
				}
				else if(sec_op>=0x10 || sec_op<0x14)
				{
					has_modr_m=true;
					is_mov=true;
					number_of_ops=2;
					op_sizes[1]=0x80;
					op_sizes[0]=0x80;
					reg_purpose[0x02]=true;
					reg_purposes_indexes[0x00]=0x02;
					reg_purposes_indexes[0x01]=0x02;
				}
				else if(sec_op>=0x14 && sec_op<0x16)
				{
					has_modr_m=true;
					has_memory_address=true;
					address_length=0x08;
					number_of_ops=2;
					op_sizes[1]=0x80;
					op_sizes[0]=0x80;
				}
				else if(sec_op>=0x16 && sec_op<0x18)
				{
					has_modr_m=true;
					is_mov=true;
					number_of_ops=2;
					op_sizes[1]=0x80;
					op_sizes[0]=0x80;
					reg_purpose[0x02]=true;
					reg_purposes_indexes[0x00]=0x02;
					reg_purposes_indexes[0x01]=0x02;
				}
				else if(sec_op==0x18)
				{
					//rememeber to change everywhere 0x10 is confused with 0x08
					has_ext=true;
					pri_op=input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					if(ext_no>=0x00 && ext_no<0x04)
					{
						is_prefetch = true;
						address_length=0x08;
						op_sizes[0]=0x08;
						number_of_ops=1;
					}
					else if(ext_no>=0x04 && ext_no<0x09)
					{
						hint_NOP=true;
						number_of_ops=1;
					}
				}
				else if(sec_op>=0x19 && sec_op<0x1F)
				{
					hint_NOP=true;
					number_of_ops=1;
				}
				else if(sec_op==0x1f)
				{
					pri_op=input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					if(ext_no==0x00)
					{
						is_NOP = true;
						number_of_ops=0;
					}
					else
					{
						hint_NOP=true;
						number_of_ops=1;
					}
				}
				else if(sec_op>=0x20 && sec_op<0x28)
				{
					has_modr_m=true;
					is_mov=true;
					number_of_ops=2;
					op_sizes[1]=0x40;
					op_sizes[0]=0x40;
					if(sec_op==0x20 || sec_op==0x22)
					{
						reg_purpose[0x00]=true;
						reg_purpose[0x04]=true;
						if(sec_op==0x20)
						{

							reg_purposes_indexes[0x00]=0x00;
							reg_purposes_indexes[0x01]=0x04;
						}
						else
						{
							reg_purposes_indexes[0x00]=0x04;
							reg_purposes_indexes[0x01]=0x00;
						}
					}
					else if(sec_op==0x21 || sec_op==0x23)
					{
						reg_purpose[0x00]=true;
						reg_purpose[0x05]=true;
						if(sec_op==0x21)
						{
							reg_purposes_indexes[0x00]=0x00;
							reg_purposes_indexes[0x01]=0x05;
						}
						else
						{
							reg_purposes_indexes[0x00]=0x05;
							reg_purposes_indexes[0x01]=0x00;
						}
					}
				}
				else if(sec_op>=0x28 && sec_op<0x2A)
				{
					has_modr_m=true;
					is_mov=true;
					number_of_ops=2;
					op_sizes[1]=0x80;
					op_sizes[0]=0x80;
					reg_purpose[0x02]=true;
					reg_purposes_indexes[0x00]=0x02;
					reg_purposes_indexes[0x01]=0x02;
				}
				else if(sec_op==0x2a)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=0x40;
					op_sizes[0]=0x80;
					reg_purpose[0x02]=true;
				}
				else if(sec_op==0x2b)
				{
					has_modr_m=true;
					is_mov=true;
					has_memory_address=true;
					address_length=0x80;
					op_sizes[1]=0x80;
					op_sizes[0]=0x80;
					reg_purpose[0x02]=true;
					reg_purposes_indexes[0x00]=0x02;
					reg_purposes_indexes[0x01]=0x02;
					number_of_ops=2;
				}
				else if(sec_op>=0x2c && sec_op<0x2E)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=0x40;
					op_sizes[0]=0x80;
				}
				else if(sec_op>=0x2E && sec_op<0x30)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=0x80;
					op_sizes[0]=0x80;
				}
				else if(sec_op==0x30)
				{
					number_of_ops=4;
					op_sizes[3]=0x40;
					op_sizes[2]=0x40;
					op_sizes[1]=0x40;
					op_sizes[0]=0x40;
				}
				else if(sec_op==0x31)
				{
					number_of_ops=3;
					op_sizes[2]=0x20;
					op_sizes[1]=0x20;
					op_sizes[0]=0x20;
				}
				else if(sec_op==0x32)
				{
					number_of_ops=4;
					op_sizes[3]=0x40;
					op_sizes[2]=0x40;
					op_sizes[1]=0x40;
					op_sizes[0]=0x40;
				}
				else if(sec_op==0x33)
				{
					number_of_ops=3;
					op_sizes[2]=0x20;
					op_sizes[1]=0x20;
					op_sizes[0]=0x20;
				}
				else if(sec_op==0x34)
				{
					number_of_ops=3;
					op_sizes[2]=0x20;
					op_sizes[1]=0x20;
					op_sizes[0]=0x20;
				}
				else if(sec_op==0x35)
				{
					number_of_ops=3;
					op_sizes[2]=0x40;
					op_sizes[1]=0x40;
					op_sizes[0]=0x40;
				}
				else if(sec_op==0x36)
				{

				}
				else if(sec_op==0x37)
				{
					number_of_ops=1;
					op_sizes[0]=0x20;
				}
				else if(sec_op>=0x38 && sec_op <0x3B)
				{
					has_modr_m=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ins_opcodes[2]=input_byte;
					tert_op=input_byte;
					current_position=6;
					if(tert_op==0x80 || tert_op==0x81)
					{
						number_of_ops=2;
						if (has_prefix_0x66)
						{
							op_sizes[1]=0x80;
							op_sizes[0]=0x40;
							has_memory_address=true;
							address_length=0x80;

						}
						else
						{
							op_sizes[1]=mode;
							op_sizes[0]=mode;
							has_memory_address=true;
							address_length=default_mode;
						}
					}
					else if (tert_op==0xF0 || tert_op==0xF1)
					{
						number_of_ops=2;

						if(has_prefix_0xF2)
						{
							op_sizes[1]=0x80;
							op_sizes[0]=0x100;
							if(tert_op==0xf0)
							{
								op_sizes[1]=0x08;
								op_sizes[0]=0x100;
								has_memory_address=true;
								address_length=0x08;
							}
							else
							{
								op_sizes[1]=mode;
								op_sizes[0]=mode;
							}
						}
						else
						{
							has_memory_address=true;
						}
					}

					else if (tert_op>=0x08 && tert_op<0x0F)
					{
						number_of_ops=3;
						op_sizes[2]=0x08; //imm
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;;
						imm_size[0]=0x08;
					}
					else if (tert_op==0x0F)
					{
						number_of_ops=2;

						if(has_prefix_0x66)
						{
							op_sizes[1]=0x80;
							op_sizes[0]=0x80;
						}
						else
						{
							op_sizes[1]=0x80;
							op_sizes[0]=0x80;
						}
					}
					else if (tert_op==0x14 && tert_op<0x23)
					{
						number_of_ops=3;
						op_sizes[2]=0x08; //imm
						op_sizes[1]=0x80;
						op_sizes[0]=mode;
						imm_size[0]=0x08;
						if(tert_op==0x14){

							has_memory_address=true;
							address_length=8;
						}
						else if(tert_op==0x15)
						{
							has_memory_address=true;
							address_length=16;
						}
					}
					else if (tert_op>=0x40 || tert_op<0x43)
					{
						number_of_ops=2;
						op_sizes[1]=0x80; //imm
						op_sizes[0]=0x80;
						if(tert_op==0x42)
						{
							op_sizes[2]=0x08;
							imm_size[0]=0x08;
						}
						else if (tert_op==0x60 && tert_op<0x61)
						{
							number_of_ops=3;
							op_sizes[2]=0x08; //imm
							op_sizes[1]=0x80;
							op_sizes[0]=0x80;
							imm_size[0]=0x08;
						}
						else if (tert_op==0x60)
						{
							number_of_ops=3;

							op_sizes[2]=0x80;
							op_sizes[1]=0x80;
							op_sizes[0]=0x80;

						}
						else if (tert_op==0x61)
						{
							number_of_ops=3;
							op_sizes[3]=0x08; //imm
							op_sizes[2]=0x80;
							op_sizes[1]=0x80;
							op_sizes[0]=0x40;
							imm_size[0]=0x08;
						}
						else if (tert_op==0x62)
						{
							number_of_ops=4;
							op_sizes[3]=0x08; //imm
							op_sizes[2]=0x80;
							op_sizes[1]=0x80;
							op_sizes[0]=0x80;
							imm_size[0]=0x08;
						}
						else if (tert_op==0x63)
						{
							number_of_ops=4;
							op_sizes[3]=0x08; //imm
							op_sizes[2]=0x80;
							op_sizes[1]=0x80;
							op_sizes[0]=0x40;
							imm_size[0]=0x08;
						}
					}
					else if(sec_op==0x3b)
					{
						//Nothing
					}
					else if(sec_op==0x3c)
					{
						//Nothing
					}
					else if(sec_op==0x3d)
					{
						//Nothing
					}
					else if(sec_op==0x3e)
					{
						//Nothing
					}
					else if(sec_op==0x3f)
					{
						//Nothing
					}
					else if(sec_op>=0x40 && sec_op<0x50) //Check rex prefixes
					{
						is_cmov=true;
						//gjør CMOV random. 
						number_of_ops=2;
						op_sizes[1]=default_mode;
						op_sizes[0]=default_mode;
						reg_purpose[0x00]=true;
						reg_purposes_indexes[0x00]=0x00;
						reg_purposes_indexes[0x01]=0x00;
					}
					else if(sec_op==0x50)
					{
						is_mov=true;
						op_sizes[1]=0x80;
						op_sizes[0]=default_mode;
						reg_purpose[0x00]=true;
						reg_purpose[0x02]=true;
						reg_purposes_indexes[0x00]=0x00;
						reg_purposes_indexes[0x01]=0x02;
					}
					else if(sec_op>0x50 && sec_op<0x60)
					{
						number_of_ops=2;
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
					}
					else if(sec_op>=0x60 && sec_op<0x6E)
					{
						number_of_ops=2;

						if(has_prefix_0x66)
						{
							op_sizes[1]=0x80;
							op_sizes[0]=0x80;
						}
						else
						{
							op_sizes[1]=0x40;
							op_sizes[0]=0x40;
						}
						if(sec_op==0x70)
						{
							op_sizes[2]=0x08;
							imm_size[0]=0x08;
						}
					}
					else if(sec_op>=0x6E) 
					{
						number_of_ops=2;
						is_mov=true;
						if(has_prefix_0x66)
						{
							op_sizes[1]=0x80;
							op_sizes[0]=mode;
							reg_purpose[0x00]=true;
							reg_purpose[0x02]=true;
							reg_purposes_indexes[0x00]=0x02;
							reg_purposes_indexes[0x01]=0x00;

						}
						else
						{
							op_sizes[1]=mode;
							op_sizes[0]=0x40;
							reg_purpose[0x00]=true;
							reg_purpose[0x01]=true;
							reg_purposes_indexes[0x00]=0x01;
							reg_purposes_indexes[0x01]=0x00;
						}
					}
					else if(sec_op==0x6F)
					{
						number_of_ops=2;
						is_mov=true;
						if(has_prefix_0x66)
						{
							op_sizes[1]=0x80;
							op_sizes[0]=0x80;
							reg_purpose[0x02]=true;
							reg_purposes_indexes[0x00]=0x02;
							reg_purposes_indexes[0x01]=0x02;
						}
						else if(has_prefix_0xF3)
						{
							op_sizes[1]=0x80;
							op_sizes[0]=0x80;

							reg_purpose[0x02]=true;
							reg_purposes_indexes[0x00]=0x02;
							reg_purposes_indexes[0x01]=0x02;
						}
						else
						{
							op_sizes[1]=0x40;
							op_sizes[0]=0x40;
							reg_purpose[0x01]=true;
							reg_purposes_indexes[0x00]=0x01;
							reg_purposes_indexes[0x01]=0x01;
						}
					}
					else if(sec_op==0x70)
					{
						number_of_ops=3;
						has_imm=true;
						op_sizes[2]=0x08;
						imm_size[0]=0x08;
						if(has_prefix_0x66)
						{
							op_sizes[1]=0x80;
							op_sizes[0]=0x80;
						}
						else if(has_prefix_0xF2)
						{
							op_sizes[1]=0x80;
							op_sizes[0]=0x80;
						}
						else if(has_prefix_0xF3)
						{
							op_sizes[1]=0x80;
							op_sizes[0]=0x80;
						}
						else
						{
							op_sizes[1]=0x40;
							op_sizes[0]=0x40;
						}
					}	
					else if(sec_op>=0x71 && sec_op<0x74)
					{
						has_ext=true;
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;

						number_of_ops=2;

						if(has_prefix_0x66)
						{
							op_sizes[0]=0x80;
						}
						else
						{
							op_sizes[0]=0x40;
						}
						op_sizes[1]=0x08;
						imm_size[0]=0x08;
					}
				}
				else if(sec_op>=0x74 && sec_op<0x77)
				{
					has_modr_m=true;
					number_of_ops=2;

					if(has_prefix_0x66)
					{
						op_sizes[0]=0x80;
					}
					else
					{
						op_sizes[0]=0x40;
					}
				}
				else if(sec_op==0x77)
				{
					number_of_ops=0;
				}
				else if(sec_op>=0x78 && sec_op<0x7A)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=0x40;
					op_sizes[0]=0x40;
				}
				else if(sec_op>=0x7A && sec_op<0x7E)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=0x80;
					op_sizes[0]=0x80;
				}
				else if(sec_op==0x7E)
				{
					has_modr_m=true;
					is_mov=true;
					number_of_ops=2;

					if(has_prefix_0x66)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=mode;
						reg_purpose[0x00]=true;
						reg_purpose[0x02]=true;
						reg_purposes_indexes[0x00]=0x00;
						reg_purposes_indexes[0x01]=0x02;
					}
					else if(has_prefix_0xF3)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
						reg_purpose[0x02]=true;
						reg_purposes_indexes[0x00]=0x02;
						reg_purposes_indexes[0x01]=0x02;
					}
					else
					{
						op_sizes[1]=0x40;
						op_sizes[0]=0x40;
						reg_purpose[0x00]=true;
						reg_purpose[0x01]=true;
						reg_purposes_indexes[0x00]=0x00;
						reg_purposes_indexes[0x01]=0x01;
					}
				}
				else if(sec_op==0x7F)
				{
					has_modr_m=true;
					is_mov=true;
					number_of_ops=2;

					if(has_prefix_0x66)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
						reg_purpose[0x02]=true;
						reg_purposes_indexes[0x00]=0x02;
						reg_purposes_indexes[0x01]=0x02;
					}
					else if(has_prefix_0xF3)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
						reg_purpose[0x02]=true;
						reg_purposes_indexes[0x00]=0x02;
						reg_purposes_indexes[0x01]=0x02;
					}
					else
					{
						op_sizes[1]=0x40;
						op_sizes[0]=0x40;
						reg_purpose[0x01]=true;
						reg_purposes_indexes[0x00]=0x01;
						reg_purposes_indexes[0x01]=0x01;
					}
				}
				else if(sec_op==0x80 && sec_op<0x90) //Rel address
				{
					number_of_ops=1;
					has_rel=true;
					has_memory_address=true;
					address_length=mode;
					rel_length=mode;
				}
				else if(sec_op==0x90 && sec_op<0xA0)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;

					number_of_ops=1;
					op_sizes[0]=0x08;
				}
				else if(sec_op==0xa0)
				{
					is_push=true;
				}
				else if(sec_op==0xa1)
				{
					is_pop=true;
				}
				else if(sec_op==0xa2)
				{
					number_of_ops=3;
					op_sizes[2]=0x20;
					op_sizes[1]=0x20;
					op_sizes[0]=0x20;
				}
				else if(sec_op==0xa3)
				{
					has_modr_m=true;
					number_of_ops=2;
				}
				else if(sec_op==0xa4)
				{
					has_modr_m=true;
					has_imm=true;

					imm_size[0]=0x08;
					number_of_ops=3;
				}
				else if(sec_op==0xa5)
				{
					has_modr_m=true;
					number_of_ops=3;
					op_sizes[2]=0x08;
				}
				else if(sec_op==0xa6)
				{
					//Nothing
				}
				else if(sec_op==0xa7)
				{
					//Nothing
				}
				else if(sec_op==0xa8)
				{
					is_push=true;
				}
				else if(sec_op==0xa9)
				{
					is_pop=true;
				}
				else if(sec_op==0xaa)
				{
					number_of_ops=1;
					has_flag=true;
				}
				else if(sec_op==0xab)
				{
					has_modr_m=true;
					number_of_ops=2;
				}
				else if(sec_op==0xac)
				{
					has_modr_m=true;
					has_imm=true;
					imm_size[0]=0x08;
					number_of_ops=3;
				}
				else if(sec_op==0xad)
				{
					has_modr_m=true;
					number_of_ops=3;
					op_sizes[2]=0x08;
				}
				else if(input_byte==0xae)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					if(ext_no==0x00)
					{
						has_memory_address=true;
						address_length=512;
						number_of_ops=3;
						op_sizes[2]=mode;
						op_sizes[1]=mode;
						op_sizes[0]=0x200;
					}
					else if(ext_no==0x01)
					{
						has_memory_address=true;
						number_of_ops=3;
						op_sizes[2]=mode;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
					else if(ext_no==0x02)
					{
						has_memory_address=true;
						address_length=32;
						number_of_ops=1;
						op_sizes[0]=0x2;
					}
					else if(ext_no==0x03)
					{

						has_memory_address=true;
						address_length=32;
						number_of_ops=1;
						op_sizes[0]=0x2;
					}
					else if(ext_no==0x04)
					{
						has_memory_address=true;
						address_length=mode;
						number_of_ops=3;
						op_sizes[2]=0x20;
						op_sizes[1]=0x20;
						op_sizes[0]=mode;
					}
					else if(ext_no==0x05)
					{
						number_of_ops=3;
						op_sizes[2]=mode;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
					else if(ext_no==0x06)
					{
						number_of_ops=0;
					}
					else if(ext_no==0x07)
					{
						has_memory_address=true;
						address_length=8;
						number_of_ops=1;
						op_sizes[0]=0x08;
					}
				}
				else if(sec_op==0xaf)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(sec_op==0xb0)
				{
					has_modr_m=true;
					number_of_ops=3;
					op_sizes[2]=0x08;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
				}
				else if(sec_op==0xb1)
				{
					has_modr_m=true;
					number_of_ops=3;
					op_sizes[2]=mode;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(sec_op==0xb2)
				{
					has_modr_m=true;
					address_length=mode;
					number_of_ops=3;
					op_sizes[2]=mode;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(sec_op==0xb3)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(sec_op==0xb4)
				{
					has_modr_m=true;
					address_length=mode;
					number_of_ops=3;
					op_sizes[2]=mode;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(sec_op==0xb5)
				{
					has_modr_m=true;
					address_length=mode;
					number_of_ops=3;
				}
				else if(sec_op==0xb6)
				{
					has_modr_m=true;
					is_mov=true;
					number_of_ops=2;
					op_sizes[1]=0x08;
					op_sizes[0]=mode;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(sec_op==0xb7)
				{
					has_modr_m=true;
					is_mov=true;
					number_of_ops=2;
					op_sizes[1]=0x10;
					op_sizes[0]=mode;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(sec_op==0xb8)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(sec_op==0xb9)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(sec_op==0xba) //Next to do()
				{
					has_ext=true;
					has_imm=true;

					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;

					imm_size[0]=0x08;
					number_of_ops=2;
					op_sizes[1]=0x08;
					op_sizes[0]=mode;
				}
				else if(sec_op>=0xbb && sec_op<0xBE)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(sec_op==0xBE)
				{
					has_modr_m=true;
					is_mov=true;
					number_of_ops=2;
					op_sizes[1]=0x08;
					op_sizes[0]=mode;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(sec_op==0xBF)
				{
					has_modr_m=true;
					is_mov=true;
					number_of_ops=2;
					op_sizes[1]=0x10;
					op_sizes[0]=mode;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(sec_op==0xC0)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
				}
				else if(sec_op==0xC1)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(sec_op==0xc2)
				{
					has_modr_m=true;
					has_imm=true;
					op_sizes[2]=0x08;
					imm_size[0]=0x08;
					number_of_ops=3;

					if(has_prefix_0x66)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
					}
					else if(has_prefix_0xF2)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
					}
					else if(has_prefix_0xF3)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
					}
					else
					{
						op_sizes[1]=0x40;
						op_sizes[0]=0x40;
					}
				}
				else if(sec_op==0xc3)
				{
					has_modr_m=true;
					//address_length=default_mode;
					number_of_ops=2;

					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(sec_op==0xc4)
				{
					has_modr_m=true;
					has_imm=true;
					number_of_ops=3;

					if(has_prefix_0x66)
					{
						op_sizes[1]=mode;
						op_sizes[0]=0x80;
					}
					else	
					{
						op_sizes[1]=mode;
						op_sizes[0]=0x40;

					}
					op_sizes[2]=0x08;
					imm_size[0]=0x08;
				}
				else if(sec_op==0xc5)
				{
					has_modr_m=true;
					has_imm=true;
					number_of_ops=3;

					if(has_prefix_0x66)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=mode;

					}
					else
					{
						op_sizes[1]=0x40;	
						op_sizes[0]=mode;
					}
					op_sizes[2]=0x08;
					imm_size[0]=0x08;
				}
				else if(sec_op==0xc5)
				{
					number_of_ops=3;
					imm_size[0]=0x08;
					op_sizes[2]=0x80;
					op_sizes[1]=0x80;
					op_sizes[0]=0x80;
				}
				else if(sec_op==0xc7)
				{
					has_ext=true;

					pri_op=input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					if(ext_no==0x01)
					{
						number_of_ops=3;
						if(default_mode==0x20)
						{
							op_sizes[2]=0x20;
							op_sizes[1]=0x20;
							op_sizes[0]=0x40;
						}
						else
						{
							op_sizes[2]=0x40;
							op_sizes[1]=0x40;
							op_sizes[0]=0x40;
						}
					}
					else if(ext_no==0x06 || ext_no==0x07)
					{
						//address_length=64;
						number_of_ops=1;
						op_sizes[0]=0x40;
					}
				}
				else if(sec_op>=0xc8 && sec_op<0xD0)
				{
					add_reg=true;
					number_of_ops=1;
					op_sizes[0]=mode;
				}
				else if(sec_op>=0xd0 && sec_op<0xD6)
				{
					has_modr_m=true;
					number_of_ops=2;

					if(has_prefix_0x66)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
					}
					else if(has_prefix_0xF2)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
					}
					else if(has_prefix_0xF3)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
					}
					else
					{
						op_sizes[1]=0x40;
						op_sizes[0]=0x40;
					}
				}
				else if(sec_op==0xD6 || sec_op==0xE7 || sec_op==0xF7)
				{
					is_mov=true;

					number_of_ops=2;

					if(has_prefix_0x66)
					{
						if(sec_op==0xF7)
						{
							number_of_ops=3;
							op_sizes[2]=0x80;
							reg_purposes_indexes[0x02]=0x02;
						}
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
						reg_purpose[0x02]=true;
						reg_purposes_indexes[0x00]=0x02;
						reg_purposes_indexes[0x01]=0x02;
					}
					else if(has_prefix_0xF2)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x40;
						reg_purpose[0x01]=true;
						reg_purpose[0x02]=true;
						reg_purposes_indexes[0x00]=0x01;
						reg_purposes_indexes[0x01]=0x02;
					}
					else if(has_prefix_0xF3)
					{
						op_sizes[1]=0x40;
						op_sizes[0]=0x80;
						reg_purpose[0x02]=true;
						reg_purpose[0x01]=true;
						reg_purposes_indexes[0x00]=0x02;
						reg_purposes_indexes[0x01]=0x01;
					}
					else
					{
						if(sec_op==0xF7)
						{
							number_of_ops=3;
							op_sizes[2]=0x40;
							reg_purposes_indexes[0x02]=0x01;
						}
						op_sizes[1]=0x40;
						op_sizes[0]=0x40;
						reg_purposes_indexes[0x00]=0x01;
						reg_purposes_indexes[0x01]=0x01;
					}
				}
				else if(sec_op==0xD7)
				{
					is_mov=true;
					number_of_ops=2;
					reg_purpose[0x00]=true;
					if(has_prefix_0x66)
					{
						op_sizes[1]=0x80;
						reg_purpose[0x02]=true;
						reg_purposes_indexes[0x01]=0x02;

					}
					else
					{
						op_sizes[1]=0x40;
						reg_purpose[0x01]=true;
						reg_purposes_indexes[0x01]=0x01;

					}
					reg_purposes_indexes[0x00]=0x00;

					op_sizes[0]=mode;
				}
				else if (sec_op>=0xD8 && sec_op<0xE7)
				{
					number_of_ops=2;
					if(has_prefix_0x66)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
					}
					else
					{
						op_sizes[1]=0x40;
						op_sizes[0]=0x40;
					}
				}
				else if (sec_op>=0xE8 && sec_op<0xF7)
				{
					number_of_ops=2;
					if(has_prefix_0x66)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
					}
					else
					{
						op_sizes[1]=0x40;
						op_sizes[0]=0x40;
					}
				}
				else if(sec_op==0xF7)
				{
					number_of_ops=3;
					has_memory_address=true;
					if(has_prefix_0x66)
					{
						address_length=0x80;
						address_type=0x06;
						op_sizes[2]=0x80;
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
					}
					else
					{
						address_length=0x40;
						address_type=0x06;
						op_sizes[2]=0x40;
						op_sizes[1]=0x40;
						op_sizes[0]=0x40;
					}
				}
				else if (sec_op>=0xF8 && sec_op<0xFF)
				{
					number_of_ops=2;
					if(has_prefix_0x66)
					{
						op_sizes[1]=0x80;
						op_sizes[0]=0x80;
					}
					else
					{
						op_sizes[1]=0x40;
						op_sizes[0]=0x40;
					}
				}
			}
			else
			{
				if(pri_op<0x40)
				{
					if(pri_op%8==0 || pri_op%8==2)
					{
						has_modr_m=true;
						number_of_ops=2;
						op_sizes[1]=0x08;
						op_sizes[0]=0x08;
					}
					else if(pri_op%8==1 || pri_op%8==3)
					{
						has_modr_m=true;
						number_of_ops=2;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
					else if(pri_op%8==4)
					{
						has_imm=true;
						imm_size[0]=0x08;
						number_of_ops=2;
						op_sizes[1]=0x08;
						op_sizes[0]=0x08;
					}
					else if(pri_op%8==5) //might be 0x08
					{
						has_imm=true;
						imm_size[0]=mode;
						number_of_ops=2;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
					else if (pri_op%8==6)
					{
						if(default_mode==0x40)
							is_invalid=true;
						else
						{

							if(pri_op==0x06)
							{
								is_push=true;
								number_of_ops=1;
								segment="ES";
								seg_index=0;
							}
							else if(pri_op==0x0E)
							{
								is_push=true;
								number_of_ops=1;
								segment="CS";
								seg_index=1;
							}
							else if(pri_op==0x16)
							{
								is_push=true;
								number_of_ops=1;
								segment="SS";
								seg_index=2;
							}
							else if(pri_op==0x1E)
							{
								is_push=true;
								number_of_ops=1;
								segment="DS";
								seg_index=3;	
							}				
						}
					}
					else if (pri_op%8==7) // Not used, because of choice: Flat memory model
					{
						if(default_mode==0x40)
							is_invalid=true;
						else
						{
							if(pri_op==0x07)
							{
								is_pop=true;
								number_of_ops=1;
								segment="ES";
								seg_index=0;
							}
							else if(pri_op==0x0F)
							{
								is_pop=true;
								number_of_ops=1;
								segment="CS";
								seg_index=0;
							}
							else if(pri_op==0x17)
							{
								is_pop=true;
								number_of_ops=1;
								segment="SS";
								seg_index=0;
							}
							else if(pri_op==0x1F)
							{
								is_pop=true;
								number_of_ops=1;
								segment="DS";
								seg_index=0;
							}
						}
					}
				}
				else if(pri_op>=0x40 && pri_op<0x48)
				{
					if(mode<0x40)
					{
						add_reg=true;
						number_of_ops=1;
					}
				}
				else if(pri_op==0x48 && pri_op<50)
				{
					if(mode<0x40)
					{
						add_reg=true;
						number_of_ops=1;
					}
				}
				else if(pri_op>=0x50 && pri_op<0x58) //register specified in opcode
				{
					is_push=true;
					add_reg=true;
					number_of_ops=1;
					if(mode==0x40)
						op_sizes[0]=0x40;
					else
						op_sizes[0]=0x10;
				}
				else if(pri_op>=0x58 && pri_op<0x60)
				{
					is_pop=true;
					add_reg=true;
					number_of_ops=1;
					if(mode==0x40)
						op_sizes[0]=0x40;
					else
						op_sizes[0]=0x10;
				}
				else if(pri_op==0x60)
				{
					if(default_mode==0x40)
					{
						is_invalid=true;
					}
					else
					{
						is_push=true;
						op_sizes[2]=0x20;
						op_sizes[1]=0x20;
						op_sizes[0]=0x20;
					}
				}
				else if(pri_op==0x61)
				{
					if(default_mode==0x40)
					{
						is_invalid=true;
					}
					else
					{
						is_pop=true;
						op_sizes[2]=0x20;
						op_sizes[1]=0x20;
						op_sizes[0]=0x20;
					}
				}
				else if(pri_op==0x62)
				{
					if(default_mode==0x40)
					{
						is_invalid=true;
					}
					else
					{
						number_of_ops=3;
						is_bound=true;
						has_flag=true;
						has_modr_m=true;
						op_sizes[2]=0x10;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
				}
				else if(pri_op==0x63)
				{

					if(default_mode==0x40)
					{
						is_mov=true;
						has_modr_m=true;
					}
					else
					{
					
						has_modr_m=true;
						op_sizes[1]=0x10;
						op_sizes[0]=0x10;
					}
				}
				else if(pri_op==0x68)
				{
					is_push=true;
					has_imm=true;
					imm_size[0]=mode;
					number_of_ops=1;
					op_sizes[0]=mode;
				}
				else if(pri_op==0x69)
				{
					has_modr_m=true;
					has_imm=true;
					imm_size[0]=mode;
					number_of_ops=3;
					op_sizes[2]=mode;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(pri_op==0x6a)
				{
					is_push=true;
					has_imm=true;
					imm_size[0]=0x08;
					number_of_ops=1;
					op_sizes[0]=0x08;
				}
				else if(pri_op==0x6b)
				{
					has_modr_m=true;
					has_imm=true;
					imm_size[0]=mode;
					number_of_ops=3;
					op_sizes[2]=mode;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(pri_op>=0x6c && pri_op<0x70)
				{
					number_of_ops=2;
					has_memory_address=true;
					if(pri_op==0x6C || pri_op==0x6E)
					{
						op_sizes[1]=0x10;
						op_sizes[0]=0x08;
						address_length=0x08;
					}
					else if(pri_op==0x6D || pri_op==0x6F)
					{
						op_sizes[1]=0x10;
						op_sizes[0]=mode;
						address_length=mode;
					}
				}
				else if(pri_op>=0x70 && pri_op<=0x80)
				{
					has_rel=true;
					rel_length=0x08;
					op_sizes[0]=rel_length;
				}
				else if(pri_op==0x80 || pri_op==0x82)
				{
					if(mode==0x40)
					{
						if(input_byte==0x82)
							is_invalid=true;
					}
					if(!is_invalid)
					{
						has_ext=true;
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
						has_modr_m=true;
						has_imm=true;
						imm_size[0]=0x08;
						op_sizes[1]=0x08;
						op_sizes[0]=0x08;
						number_of_ops=2;
					}
				}
				else if(input_byte==0x81)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					has_modr_m=true;;
					has_imm=true;
					number_of_ops=2;
					imm_size[0]=mode;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if( pri_op==0x83)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					has_modr_m=true;;
					has_imm=true;
					number_of_ops=2;
					imm_size[0]=0x08;
					op_sizes[1]=0x08;
					op_sizes[0]=mode;
				}
				else if(pri_op==0x84)
				{
					has_modr_m=true;
					number_of_ops=2;
					is_test=true;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
				}
				else if(pri_op==0x85)
				{
					has_modr_m=true;
					number_of_ops=2;
					is_test=true;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(pri_op==0x86)
				{
					has_modr_m=true;
					number_of_ops=2;
					is_xchg=true;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
				}
				else if(pri_op==0x87)
				{
					has_modr_m=true;
					number_of_ops=2;
					is_xchg=true;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(pri_op==0x88)
				{
					has_modr_m=true;
					number_of_ops=2;
					is_mov=true;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(pri_op==0x89)
				{
					has_modr_m=true;
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
					is_mov=true;
				}
				else if(pri_op==0x8A)
				{
					has_modr_m=true;
					number_of_ops=2;
					is_mov=true;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(pri_op==0x8B)
				{
					has_modr_m=true;
					number_of_ops=2;
					is_mov=true;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(pri_op==0x8C)
				{
					has_modr_m=true;
					number_of_ops=2;
					is_mov=true;
					reg_purpose[0x00]=true;
					reg_purpose[0x03]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x03;
				}
				else if(pri_op==0x8D)
				{
					has_modr_m=true;
					number_of_ops=2;
					is_lea=true;
					has_memory_address=true;
					address_length=mode;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(pri_op==0x8E)
				{
					has_modr_m=true;
					number_of_ops=2;
					is_mov=true;
					op_sizes[1]=0x10;
					op_sizes[0]=0x10;
					reg_purpose[0x00]=true;
					reg_purpose[0x03]=true;
					reg_purposes_indexes[0x00]=0x03;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(pri_op==0x8f)
				{
					has_ext=true;
					no_reg=true;
					is_pop=true;
					number_of_ops=1;
					op_sizes[0]=mode;
				}
				else if(pri_op>=0x90 && pri_op<0x98)
				{

					if(has_prefix)
					{
						if(has_prefix_0xF3)
							is_pause=true;
					}
					else
					{
						if(pri_op==0x90)
						{	
							is_no_op = true;	
							number_of_ops=0;
						}
						else
						{
							add_reg=true;
							is_xchg=true;
							number_of_ops=2;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
					}
				}
				else if(pri_op==0x98) 
				{
					number_of_ops=2;
					op_sizes[1]=0x10;
					op_sizes[0]=mode;
				}
				else if (pri_op==0x99)
				{
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(pri_op==0x9a)
				{
					if(default_mode==0x40)
					{
						is_invalid=true;
					}
					else
					{
						number_of_ops=1;
						is_call = true;
						has_pointer=true;
						pointer_length=mode;
						//ptr;
					}
				}
				else if(pri_op==0x9b)
				{
					is_prefix=true;
					is_wait=true;
				}
				else if(pri_op==0x9c && pri_op==0x9d)
				{
					is_push=true;
					has_flag=true;
					number_of_ops=1;
					op_sizes[0]=0x10;
				}
				else if(pri_op==0x9E && pri_op==0x9F)
				{
					number_of_ops=1;
					op_sizes[0]=0x08;
				}
				else if(pri_op==0xa0 || pri_op==0xa2 )
				{
					is_mov=true;
					has_offset=true;
					offset_length=0x08; //check clocser
					number_of_ops=2;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(pri_op==0xa1 || pri_op==0xa3)
				{
					is_mov=true;
					has_offset=true;
					offset_length=mode;
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(pri_op==0xa4)
				{
					is_mov=true;
					has_memory_address=true;
					address_length=0x08;
					number_of_ops=2;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(pri_op==0xa5)
				{
					is_mov=true;
					has_memory_address=true;
					address_length=0x08;
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(pri_op==0xa6)
				{
					address_length=0x08;
					number_of_ops=2;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
				}
				else if(pri_op==0xa7)
				{
					address_length=mode;
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(pri_op==0xa8)
				{
					number_of_ops=2;
					has_imm=true;
					imm_size[0]=0x08;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
				}
				else if(pri_op==0xa9)
				{
					number_of_ops=2;
					has_imm=true;
					imm_size[0]=mode;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(pri_op==0xaa || pri_op==0xac)
				{
					number_of_ops=2;
					address_length=0x08;
					number_of_ops=2;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
				}
				else if(pri_op==0xab || pri_op==0xad)
				{
					address_length=mode;
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(pri_op==0xae)
				{
					number_of_ops=2;
					address_length=0x08;
					number_of_ops=2;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
				}
				else if(pri_op==0xaf)
				{
					address_length=mode;
					number_of_ops=2;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(pri_op>=0xb0 && pri_op<0xb8)
				{
					add_reg=true;
					is_mov=true;
					has_imm=true;
					number_of_ops=2;
					imm_size[0]=0x08;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(pri_op>=0xb8 && pri_op<0xC0)
				{
					add_reg=true;
					is_mov=true;
					has_imm=true;
					number_of_ops=2;
					imm_size[0]=mode;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
					reg_purpose[0x00]=true;
					reg_purposes_indexes[0x00]=0x00;
					reg_purposes_indexes[0x01]=0x00;
				}
				else if(pri_op==0xc0)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					has_imm=true;
					number_of_ops=2;
					imm_size[0]=0x08;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
				}
				else if(pri_op==0xc1)
				{
					has_ext=true;
					has_imm=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					number_of_ops=2;
					imm_size[0]=mode;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(pri_op==0xc2)
				{
					has_imm=true;
					number_of_ops=1;
					op_sizes[0]=0x10;
					imm_size[0]=0x10;
				}
				else if(pri_op==0xc3)
				{
					number_of_ops=0;
				}
				else if(pri_op==0xc4)
				{
					if(default_mode==0x40)
					{
						is_invalid=true;
					}
					else
					{
						has_modr_m=true;
						number_of_ops=3;
						address_length=mode;
						op_sizes[2]=mode;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
				}
				else if(pri_op==0xc5)
				{
					if(default_mode==0x40)
					{
						is_invalid=true;
					}
					else
					{
						has_modr_m=true;
						number_of_ops=3;
						address_length=mode;
						op_sizes[2]=mode;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
				}
				else if(pri_op==0xc6)
				{
					has_ext=true;
					has_imm=true;
					pri_op=byte_al.get(current_index);
					current_index++;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					number_of_ops=2;
					imm_size[0]=0x08;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;

				}
				else if(pri_op==0xc7)
				{
					has_ext=true;
					has_imm=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					number_of_ops=2;
					imm_size[0]=mode;
					op_sizes[1]=mode;
					op_sizes[0]=mode;
				}
				else if(pri_op==0xc8)
				{
					has_imm=true;
					number_of_ops=3;
					imm_size[1]=0x08;
					imm_size[0]=0x10;
					op_sizes[2]=0x08;
					op_sizes[1]=0x10;
					op_sizes[0]=0x10;
				}
				else if(pri_op==0xc9)
				{
					number_of_ops=1;
					op_sizes[0]=0x10;
				}
				else if(pri_op==0xca)
				{
					has_imm=true;
					number_of_ops=1;
					imm_size[0]=0x10;
					op_sizes[0]=0x10;
				}
				else if(pri_op==0xcb)
				{
					number_of_ops=0;
				}
				else if(pri_op==0xcc)
				{
					has_flags=true;
					number_of_ops=2;
					op_sizes[1]=0x10;
					op_sizes[0]=0x03;
				}
				else if(pri_op==0xcd)
				{
					has_flags=true;
					has_imm=true;
					number_of_ops=2;
					imm_size[0]=0x08;
					op_sizes[1]=0x10;
					op_sizes[0]=0x08;
				}
				else if(pri_op==0xce)
				{
					has_flags=true;
					number_of_ops=1;
					op_sizes[0]=0x10;
				}
				else if(pri_op==0xcf)
				{
					has_flags=true;
					number_of_ops=1;
					op_sizes[0]=0x10;
				}
				else if(pri_op==0xD0 || pri_op==0xd2)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					number_of_ops=2;
					op_sizes[1]=0x01;
					op_sizes[0]=0x08;
				}
				else if(pri_op==0xD2)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					number_of_ops=2;
					op_sizes[1]=0x10;
					op_sizes[0]=0x08;
				}
				else if(pri_op==0xD1)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					number_of_ops=2;
					op_sizes[1]=0x01;
					op_sizes[0]=mode;
				}
				else if (pri_op==0xD3)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					number_of_ops=2;
					op_sizes[1]=0x10;
					op_sizes[0]=mode;
				}
				else if(pri_op==0xd4)
				{
					if(default_mode==0x40)
					{
						is_invalid=true;
					}
					else
					{
						input_byte=byte_al.get(current_index);
						current_index++;
						
						op_sizes[1]=0x08;
						op_sizes[0]=0x08;
						if(input_byte!=0x0A)
						{
							has_sec=false;
							current_position=8;
							number_of_ops=3;
							has_imm=true;
							imm_size[0]=0x08;
							op_sizes[2]=0x08;
						}
						else
						{
							has_sec=true;
							current_position=5;
							sec_op=input_byte;
							opcodes[1]=sec_op;
							number_of_ops=2;
						}
					}
				}
				else if(pri_op==0xd5)
				{
					if(default_mode==0x40)
					{
						is_invalid=true;
					}
					else
					{
						input_byte=byte_al.get(current_index);
						current_index++;
						has_imm=true;
						op_sizes[1]=0x08;
						op_sizes[0]=0x08;
						if(input_byte!=0x0A)
						{
							has_sec=false;
							number_of_ops=3;
							current_position=8;
							has_imm=true;
							imm_size[0]=0x08;
							op_sizes[2]=0x08;

						}
						else
						{
							has_sec=true;
							current_position=5;
							sec_op=input_byte;
							opcodes[1]=sec_op;
							number_of_ops=2;
						}
					}
				}
				else if(input_byte==0xd6)
				{
					if(default_mode==0x40)
					{
						is_invalid=true;
					}
					else
					{
						number_of_ops=2;
						op_sizes[0]=0x08;
					}
				}
				else if(input_byte==0xd7)
				{
					number_of_ops=2;
					has_memory_address=true;
					address_length=0x08;
					if(mode==0x40)
					{
						address_type=3;
					}
					else
					{
						address_type=2;
					}
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
				}
				else if(input_byte==0xd8)
				{
					has_sec=true;
					has_ext=true;
					has_sec=true;
					is_FPU_ins=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					if(input_byte == 0xD1 || input_byte == 0xD3)
					{
						op_sizes[1]=mode;
						op_sizes[0]=mode;
						sec_op=input_byte;
						opcodes[1]=sec_op;
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
					}
					else
					{
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
						has_real=true;
						reg_FPU_size=0x20;
						op_sizes[1]=0x20;
						op_sizes[0]=mode;
					}
					number_of_ops=2;

				}
				else if(input_byte==0xd9)
				{
					has_ext=true;
					has_sec=true;
					is_FPU_ins=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ins_opcodes[1]=input_byte;
					sec_op=ins_opcodes[1];
					current_position=5;
					if(input_byte>0x07)
					{
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
					}
					if(input_byte==0xC9)
					{
						number_of_ops=2;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
					else if(input_byte==0xD0)
					{
						number_of_ops=0;
					}
					else if(input_byte>=0xE0 && input_byte<=0xEE)
					{
						number_of_ops=1;
						op_sizes[0]=mode;
					}
					else if (input_byte == 0xF0 || input_byte == 0xF2 
							|| input_byte == 0xF4 || input_byte == 0xF6
							|| input_byte == 0xF7 || input_byte == 0xFA
							|| input_byte == 0xFB || input_byte == 0xFC
							|| input_byte == 0xFE || input_byte == 0xFF)  
					{
						number_of_ops=1;
						op_sizes[0]=mode;
					}
					else if (input_byte == 0xF1 || input_byte == 0xF3 
							|| input_byte == 0xF5 || input_byte == 0xF8
							|| input_byte == 0xF9 || input_byte == 0xFD  )
					{
						number_of_ops=2;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
					else
					{
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
						if(ext_no==0x00)
						{
							number_of_ops=2;
							has_real=true;
							reg_FPU_size=0x20;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if(ext_no== 0x01)
						{
							number_of_ops=2;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if(ext_no== 0x02)
						{
							number_of_ops=2;
							has_real=true;
							reg_FPU_size=0x20;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if(ext_no== 0x03)
						{
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if(ext_no== 0x04)
						{
							number_of_ops=1;	
							has_memory_address=true;
							address_length=fpu_mode;
							op_sizes[0]=address_length;
						}
						else if(ext_no== 0x05)
						{
							number_of_ops=1;
							has_memory_address=true;
							address_length=0x10;
							op_sizes[0]=address_length;
						}
						else if(ext_no== 0x06)
						{
							number_of_ops=1;
							has_memory_address=true;
							address_length=fpu_mode;
							op_sizes[0]=address_length;
						}
						else if(ext_no== 0x07)
						{
							number_of_ops=1;
							has_memory_address=true;
							address_length=0x10;
							op_sizes[0]=address_length;
						}
					}
				}
				else if(input_byte==0xda)
				{
					has_ext=true;
					has_sec=true;
					is_FPU_ins=true;

					input_byte=byte_al.get(current_index);
					current_index++;

					if(input_byte==0xE9)
					{
						number_of_ops=2;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
						ins_opcodes[1]=input_byte;
						sec_op=ins_opcodes[1];
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
					}
					else
					{
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
						if(ext_no>=0x00 && ext_no<=0x03)
						{
							number_of_ops=2;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
							is_cmov=true;
							reg_purpose[0x00]=true;
							reg_purposes_indexes[0x00]=0x00;
							reg_purposes_indexes[0x01]=0x00;
						}
						if(ext_no>=0x04 && ext_no<=0x07)
						{
							number_of_ops=2;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else is_invalid=true;
					}
				}
				else if(input_byte==0xdb)
				{
					is_FPU_ins=true;
					has_ext=true;
					has_sec=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					
					if(input_byte>=0xE0 && input_byte<=0xE4)
					{
						ins_opcodes[1]=input_byte;
						sec_op=ins_opcodes[1];
						number_of_ops=0;
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
					}
					else
					{
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
						if(ext_no>=0x00 && ext_no<=0x03)
						{	
							has_real=true;
							reg_FPU_size=0x20;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
							is_cmov=true;
							reg_purpose[0x00]=true;
							reg_purposes_indexes[0x00]=0x00;
							reg_purposes_indexes[0x01]=0x00;


						}
						else if(ext_no==0x04)
						{
							//do nothing
						}
						else if(ext_no==0x06)
						{
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if (ext_no==0x05 || ext_no==0x07)
						{
							number_of_ops=2;
							has_real=true;
							reg_FPU_size=0x50;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else is_invalid=true;
					}
				}
				else if(input_byte==0xdc)
				{
					has_ext=true;
					is_FPU_ins=true;
					input_byte=byte_al.get(current_index);
					current_index++;

					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					if(ext_no>=0x00 && ext_no<=0x07)
					{
						number_of_ops=2;
						has_real=true;
						reg_FPU_size=0x40;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
					else is_invalid=true;
				}
				else if(input_byte==0xdd)
				{
					has_ext=true;
					is_FPU_ins=true;

					input_byte=byte_al.get(current_index);
					current_index++;

					if(input_byte==0xE1 || input_byte==0xE9)
					{
						number_of_ops=2;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
						has_sec=true;
						ins_opcodes[1]=input_byte;
						sec_op=ins_opcodes[1];
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
					}
					else
					{	
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
						if(ext_no>=0x00)
						{
							number_of_ops=1;
						}
						else if(ext_no>=0x01) 
						{
							number_of_ops=2;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if(ext_no>=0x02)
						{
							number_of_ops=2;
							has_real=true;
							reg_FPU_size=0x40;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if(ext_no>=0x03)
						{
							number_of_ops=2;
							has_real=true;
							reg_FPU_size=0x40;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if(ext_no>=0x04)
						{
							number_of_ops=2;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if(ext_no>=0x05) 
						{
							number_of_ops=2;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if(ext_no>=0x06)
						{
							number_of_ops=3;
							has_real=true;
							reg_FPU_size=fpu_mode;
							op_sizes[1]=mode;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if(ext_no>=0x07)
						{
							number_of_ops=1;
						}
						else if(ext_no==0x07)
						{
							number_of_ops=1;
							has_memory_address=true;
							address_length=0x10;
							op_sizes[0]=mode=0x10;
						}
						else is_invalid=true;
					}
				}
				else if(input_byte==0xde)
				{
					is_FPU_ins=true;
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;

					if(input_byte==0xC1 || input_byte==0xC9 || input_byte==0xD9
							|| input_byte==0xF1 || input_byte==0xF9)
					{
						has_sec=true;
						sec_op=input_byte;
						ins_opcodes[1]=sec_op;
						number_of_ops=2;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
					}
					else
					{	
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
						if(ext_no>=0x00 && ext_no<=0x07)
						{
							number_of_ops=2;
							has_real=true;
							reg_FPU_size=0x10;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else is_invalid=true;
					}
				}
				else if(input_byte==0xdf)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					if(input_byte==0xE0)
					{
						number_of_ops=1;
						has_sec=true;
						sec_op=input_byte;
						ins_opcodes[1]=sec_op;
						number_of_ops=2;
						op_sizes[0]=0x10;
						input_byte=byte_al.get(current_index);
						current_index++;
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
					}
					else
					{	
						ext_no=instruction_x86.get_ext(input_byte);
						current_position=7;
						if(ext_no>=0x00 && ext_no<=0x03)
						{
							number_of_ops=2;
							has_real=true;
							reg_FPU_size=0x10;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if(ext_no==0x04)
						{
							number_of_ops=2;
							has_real=true;
							reg_FPU_size=0x50;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else if(ext_no>=0x05 && ext_no<=0x07)
						{
							number_of_ops=2;
							op_sizes[1]=mode;
							op_sizes[0]=mode;
						}
						else is_invalid=true;
					}
				}
				else if(input_byte==0xe0)
				{
					number_of_ops=2;
					has_rel=true;
					is_loop=true;
					rel_length=0x08;
					op_sizes[1]=rel_length;
					op_sizes[0]=mode;
				}
				else if(input_byte==0xe1)
				{
					number_of_ops=2;
					has_rel=true;
					is_loop=true;
					rel_length=0x08;
					op_sizes[1]=rel_length;
					op_sizes[0]=mode;
				}
				else if(input_byte==0xe2)
				{
					number_of_ops=2;
					has_rel=true;
					is_loop=true;
					rel_length=0x08;
					op_sizes[1]=rel_length;
					op_sizes[0]=mode;
				}
				else if(input_byte==0xe3)
				{
					number_of_ops=2;
					has_rel=true;
					rel_length=0x08;
					op_sizes[1]=mode;
					op_sizes[0]=rel_length;
				}
				else if(input_byte==0xe4)
				{
					has_imm=true;	
					imm_size[0]=0x08;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
				}
				else if(input_byte==0xe5)
				{
					number_of_ops=2;
					has_imm=true;
					imm_size[0]=0x08;
					op_sizes[1]=0x08;
					op_sizes[0]=mode;
				}
				else if(input_byte==0xe6)
				{
					number_of_ops=2;
					has_imm=true;
					imm_size[0]=0x08;
					op_sizes[1]=0x08;
					op_sizes[0]=0x08;
				}
				else if(input_byte==0xe7)
				{
					number_of_ops=2;
					has_imm=true;
					imm_size[0]=0x08;
					op_sizes[1]=mode;
					op_sizes[0]=0x08;
				}
				else if(input_byte==0xe8)
				{
					has_rel=true;
					rel_length=mode;
					op_sizes[0]=rel_length;
					is_call=true;
				}
				else if(input_byte==0xe9)
				{
					has_rel=true;
					rel_length=mode;
					op_sizes[0]=rel_length;
				}
				else if(input_byte==0xea)
				{
					if(default_mode==0x40)
						is_invalid=true;
					else
					{
						has_pointer=true;
						pointer_length=mode;
						op_sizes[0]=pointer_length;
					}
				}
				else if(input_byte==0xeb)
				{
					has_rel=true;
					rel_length=0x08;
					op_sizes[0]=rel_length;
				}
				else if(input_byte==0xec)
				{
					number_of_ops=2;
					op_sizes[1]=0x10;
					op_sizes[0]=0x08;
				}
				else if(input_byte==0xed)
				{
					number_of_ops=2;
					number_of_ops=2;
					op_sizes[1]=0x10;
					op_sizes[0]=0x20;
				}
				else if(input_byte==0xee)
				{
					number_of_ops=2;
					number_of_ops=2;
					op_sizes[1]=0x08;
					op_sizes[0]=0x10;
				}
				else if(input_byte==0xef)
				{
					number_of_ops=2;
					number_of_ops=2;
					op_sizes[1]=0x20;
					op_sizes[0]=0x10;
				}
				else if(input_byte==0xf0)
				{
					is_lock=true;
				}
				else if(input_byte==0xf1)
				{
					number_of_ops=1;
					has_flags=true;
					op_sizes[0]=0x10;
				}
				else if(input_byte==0xf2)
				{
					number_of_ops=1;
					op_sizes[0]=mode;
				}
				else if(input_byte==0xf3)
				{
					number_of_ops=1;
					op_sizes[0]=mode;
				}
				else if(input_byte==0xf4)
				{
					number_of_ops=0;
				}
				else if(input_byte==0xf5)
				{
					number_of_ops=0;
				}
				else if(input_byte==0xf6)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					if(ext_no==0x00)
					{
						number_of_ops=2;
						has_imm=true;
						imm_size[0]=0x08;
						op_sizes[1]=0x08;
						op_sizes[0]=0x08;
					}
					else if(ext_no== 0x01)
					{
						number_of_ops=2;
						has_imm=true;
						imm_size[0]=0x08;
						op_sizes[1]=0x08;
						op_sizes[0]=0x08;
					}
					else if(ext_no== 0x02)
					{
						number_of_ops=2;
						op_sizes[0]=0x08;
					}
					else if(ext_no== 0x03)
					{
						number_of_ops=2;
						op_sizes[0]=0x08;
					}
					else if(ext_no== 0x04)
					{
						number_of_ops=3;
						op_sizes[2]=0x08;
						op_sizes[1]=0x08;
						op_sizes[0]=0x08;
					}
					else if(ext_no== 0x05)
					{
						number_of_ops=3;
						op_sizes[2]=0x08;
						op_sizes[1]=0x08;
						op_sizes[0]=0x08;
					}
					else if(ext_no== 0x06)
					{
						number_of_ops=4;
						op_sizes[3]=0x08;
						op_sizes[2]=0x08;
						op_sizes[1]=0x08;
						op_sizes[0]=0x08;
					}
					else if(ext_no== 0x07)
					{
						number_of_ops=4;
						number_of_ops=4;
						op_sizes[3]=0x08;
						op_sizes[2]=0x08;
						op_sizes[1]=0x08;
						op_sizes[0]=0x08;
					}
					else is_invalid=true;
				}
				else if(input_byte==0xf7)
				{
					pri_op=(short)input_byte;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;

					if(input_byte==0x00)
					{
						number_of_ops=2;
						has_imm=true;
						imm_size[0]=mode;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
					else if(input_byte== 0x01)
					{
						number_of_ops=2;
						has_imm=true;
						imm_size[0]=mode;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
					else if(input_byte== 0x02)
					{
						number_of_ops=1;
						op_sizes[0]=mode;
					}
					else if(input_byte== 0x03)
					{
						number_of_ops=1;
						op_sizes[0]=mode;
					}
					else if(input_byte== 0x04)
					{
						number_of_ops=3;
						op_sizes[2]=mode;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}

					else if(input_byte== 0x05)
					{
						number_of_ops=3;
						op_sizes[2]=mode;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}

					else if(input_byte== 0x06)
					{
						number_of_ops=3;
						op_sizes[2]=mode;
						op_sizes[1]=mode;
						op_sizes[0]=mode;					
					}
					else if(input_byte== 0x07)
					{
						number_of_ops=3;
						op_sizes[2]=mode;
						op_sizes[1]=mode;
						op_sizes[0]=mode;
					}
					else is_invalid=true;
				}
				else if(input_byte==0xf8)
				{
					number_of_ops=0;
				}
				else if(input_byte==0xf9)
				{
					number_of_ops=0;
				}
				else if(input_byte==0xfa)
				{
					number_of_ops=0;
				}
				else if(input_byte==0xfb)
				{
					number_of_ops=0;
				}
				else if(input_byte==0xfc)
				{
					number_of_ops=0;
				}
				else if(input_byte==0xfd)
				{
					number_of_ops=0;
				}
				else if(input_byte==0xfe)
				{
					has_ext=true;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					number_of_ops=1;
					op_sizes[0]=0x08;

				}
				else if(input_byte==0xff)
				{
					has_ext=true;
					number_of_ops=1;
					input_byte=byte_al.get(current_index);
					current_index++;
					ext_no=instruction_x86.get_ext(input_byte);
					current_position=7;
					number_of_ops=1;
					if(ext_no==0x00)
					{
						op_sizes[0]=0x08;
					}
					else if(ext_no==0x01)
					{
						op_sizes[0]=0x08;
					}
					else if(ext_no==0x02)
					{
						op_sizes[0]=mode;
						is_call=true;
					}
					else if(ext_no==0x03)
					{
						has_memory_address=true;
						address_length=mode;
						op_sizes[0]=mode;
						is_call=true;

					}
					else if(ext_no==0x04)
					{
						op_sizes[0]=mode;
					}
					else if(ext_no==0x05)
					{
						has_memory_address=true;
						address_length=mode;
						op_sizes[0]=mode;
					}

					else if(ext_no==0x06)
					{
						is_push=true;
						op_sizes[0]=mode;
					}
					else if(ext_no==0x07)
					{
						is_pop=true;
						op_sizes[0]=mode;
					}
				}
			}
			boolean has_add_reg=false;
			short[] displacement_bytes=null;
			short disp_length=-1;
			for(int i=0;i<prefixes.length;i++)
			{
				if(prefixes[i]>-1)
					instruction_length++;
				else break;
			}
			for(int i=0;i<ins_opcodes.length;i++)
			{
				if(ins_opcodes[i]>-1)
					instruction_length++;
				else break;
			}
			
			if(add_reg)
			{
				has_add_reg=true;
			}
			if(is_cmov)
			{
				is_mov=random_flags();
			}
			if(has_ext || ext_no>-1)
			{
				has_modr_m=true;
			}
			if(has_modr_m)
			{
				instruction_length++;
				if(!(has_ext&&ext_no>-1))
				{
					input_byte=byte_al.get(current_index);
					current_index++;
				}
				modrm=input_byte;
				instruction_length++;
				
				if(instruction_x86.has_sib(modrm))
				{
					input_byte=byte_al.get(current_index);
					current_index++;
					sib=input_byte;
					instruction_length++;
				}
				if(instruction_x86.has_displacement(modrm))
				{

					disp_length=(short) (instruction_x86.get_displacement()/8);
					instruction_length+=disp_length;
					displacement_bytes=null;

					if(disp_length>0)
					{
						displacement_bytes=new short[disp_length];
						for(int i=0;i<disp_length;i++)
						{
							input_byte=byte_al.get(current_index);
							current_index++;
							displacement_bytes[i]=input_byte;
						}
					}
				}
			}
			if(has_imm && current_position<8)
			{
				if(!has_modr_m && !has_ext)
				{					
					input_byte=byte_al.get(current_index);
					current_index++;
				}
			}
			short[][] immediates=null;
			if(has_imm)
			{
				short imm_size_length=(short)imm_size.length;
				short imm_length=0;
				immediates=new short[imm_size_length][0x20/8];

				for(int i=0;i<imm_size_length;i++)
					for(int j=0;j<0x20/8;j++)
						immediates[i][j]=-1;

				for(int i=0;i<imm_size_length;i++)
				{
					imm_length=(short) (imm_size[i]/8);
					if(imm_length>0)
					{
						for(int j=0;j<imm_length;j++)
						{
							input_byte=byte_al.get(current_index);
							current_index++;
							immediates[i][j]=input_byte;
							instruction_length++;
						}
					}
				}
			}
			short[] relative_address = null;
			if(has_rel)
			{
				relative_address=new short[rel_length/8];
				for(int i=0;i<rel_length/8;i++)
				{
					input_byte=byte_al.get(current_index);
					current_index++;
					relative_address[i]=input_byte;
					instruction_length++;
				}
			}
			short[] pointer = null;
			if(has_pointer)
			{
				pointer=new short[pointer_length/8];
				for(int i=0;i<pointer_length/8;i++)
				{
					input_byte=byte_al.get(current_index);
					current_index++;
					pointer[i]=input_byte;
					instruction_length++;
				}
			}
			if(has_memory_address)
			{
				//Could add random element, but it is not needed
			}
			short[] offset = null;
			if(has_offset)
			{
				offset=new short[offset_length/8];
				for(int i=0;i<offset_length/8;i++)
				{
					input_byte=byte_al.get(current_index);
					current_index++;
					offset[i]=input_byte;
					instruction_length++;
				}
				if(ins_opcodes[0]%2==0)
				{
					modrm=0xC0;
				}
				else
				{
					modrm=0x00;
				}
			}
			//Instruction
			current_instruction = new ActualInstruction_x86(prefixes,has_rex,ins_opcodes,ext_no,modrm,sib,
					displacement_bytes, immediates,instruction_length, is_mov,is_pop, is_push, is_lea, 
					is_call,is_loop,has_memory_address,address_type, address_length,
					has_rel, relative_address, has_offset,offset, has_pointer,pointer,is_FPU_ins,reg_FPU_size,has_add_reg,mode,reg_purpose,reg_purposes_indexes,op_sizes,has_imm, number_of_ops);
		}
		catch(Exception e)
		{
			
			process.destroy();
		}

		return current_instruction;
	}
	public boolean random_flags()
	{
		Random random= new Random();
		return random.nextBoolean();
	}

	public boolean has_sib(int mod,int r_m)
	{
		if(mod<4 && r_m==4)
			return true;
		return false;
	}
	public int get_disp(int mod)
	{
		int disp=Opcodes.displacement[mod];
		return disp;
	}
	public boolean available_input()
	{
		try
		{
			if(input.available()>0)
				return true;
			else return false;
		}
		catch (Exception e){return false;}
	}
	
	public char[] transform_from_bytes_bit(int value)
	{
		String binary_rep=Integer.toBinaryString(value);
		int length=(8-binary_rep.length());

		for(int i=0;i<length;i++)
			binary_rep="0"+binary_rep;


		return binary_rep.toCharArray();

	}
	public String set_address(boolean[] address)
	{
		String addr="";
		for(int i=0;i<address.length;i++)
		{	
			if(address[i])
				addr= addr+"1";
			else addr= addr+"0";

		}
		return addr;
	}
}

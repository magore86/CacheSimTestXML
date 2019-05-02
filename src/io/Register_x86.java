package io;


public class Register_x86 //write in random values
{

	private Register[][] wo_rex_registers;
	private Register[][] w_rex_registers;
	private final String[][] wo_rex_names={{"AL", "CL","DL", "BL", "AH", "CH", "DH", "BH"}, //1B
			{"AX","CX","DX","BX", "SP", "BP", "SI", "DI"}, //2B
			{"EAX","ECX","EDX","EBX", "ESP", "EBP", "ESI", "EDI"},//4B
			{"RAX","RCX","RDX","RBX", "RSP", "RBP", "RSI", "RDI"}, //8B
			{"MM0","MM1","MM2","MM3", "MM4", "MM5", "MM6", "MM7"}, //8B, mmx
			{"XMM0","XMM1","XMM2","XMM3", "XMM4", "XMM5", "XMM6", "XMM7"}, //16B, sse
			{"ES","CS","SS","DS","FS", "GS","res.","res."}, //segment
			{"CR0","invalid","CR2","CR3","CR4", "invalid","invalid","invalid"}, //Control
			{"DR0","DR1","DR2","DR3","DR4", "DR5","DR6.","DR7"}}; //Debug

	private final String[][] w_rex_names={{"R8B","R9B","R10B","R11B","R12B","R13B","R14B","R15B"} ,//1B,
			{"R8W","R9W","R10W","R11W","R12W","R13W","R14W","R15W"}, //2B
			{"R8D","R9D","R10D","R11D","R12D","R13D","R14D","R15D"}, //4B
			{"R8","R9","R10","R11","R12","R13","R14","R15"}, //8B
			{"MM8","MM9","MM10","MM11", "MM12", "MM13", "MM14", "MM15"}, //8B, mmx
			{"XMM8","XMM9","XMM10","XMM11", "XMM12", "XMM13", "XMM14", "XMM15"}, //16B, sse
			{"ES","CS","SS","DS","FS", "GS","res.","res."}, //segment
			{"CR8","invalid","invalid","invalid","invalid","invalid","invalid","invalid"}, //Control
			{"invalid","invalid","invalid","invalid","invalid","invalid","invalid","invalid"}}; //Debug

	private final int[] sizes = {8,16,32,64,64,128,4,64,64};

	private final int[] fpu_sizes = {80};



	private static final short BYTE = 0x08;

	private Register[] fpu_register;

	private String[] fpu_register_names={"ST0","ST1","ST2","ST3","ST4","ST5","ST6","ST7"}; //index==0x04 with rm



	/*Methods for calculating which register to use*/
	public Register_x86()
	{
		wo_rex_registers=new Register[0x09][0x08];
		w_rex_registers=new Register[0x09][0x08];
		fpu_register=new Register[0x08];
		declare_registers();

	}
	public void declare_registers()
	{
		for(int i=0;i<0x09;i++)
		{
			for(int j=0;j<0x08;j++)
			{
				wo_rex_registers[i][j]=new Register(this.wo_rex_names[i][j],false,false,sizes[i]);
				w_rex_registers[i][j]=new Register(this.w_rex_names[i][j],true,false,sizes[i]);
			}

		}
		for(int i=0;i<0x08;i++)
			fpu_register[i]=new Register(this.fpu_register_names[i],false,true,fpu_sizes[0x00]);
	}
	public void insert_into_register(boolean has_rex,boolean is_fpu,Register register,int[] opsizes,short[] purposes)
	{
		if(has_rex)
			insert_into_w_rex_register(register,is_fpu,opsizes,purposes);
		else
			insert_into_wo_rex_register(register,is_fpu,opsizes,purposes);
	}
	public Register[][] get_w_rex_register()
	{
		return w_rex_registers;
	}
	public Register[][] get_wo_rex_register()
	{
		return wo_rex_registers;
	}
	public Register[] get_fpu_register()
	{
		return fpu_register;
	}
	public void insert_into_w_rex_register(Register register,boolean is_fpu,int[] opsizes,short[] purposes) //in case of modrm and restricted to 2 operands + immediate. Op1 always src, op2 dest
	{
		boolean dest_is_reg=false;
		short d_bit=register.get_d_bit();
		short reg=register.get_reg();
		short rm = register.get_r_m();
		int opsize=opsizes[0x01]; 
		short purpose=purposes[0x01];
		short index=get_reg_index(is_fpu,purpose,opsize);
		if(d_bit==0x01)
		{
			dest_is_reg=true;
		}
		if(dest_is_reg)
		{
			register.set_name(this.w_rex_names[index][reg]);
			w_rex_registers[index][reg]=register;
		}
		else
		{
			if(index<0x04)
			{
				register.set_name(this.w_rex_names[index][rm]);
				w_rex_registers[index][rm]=register;
			}
			else if(index==0x04)
				fpu_register[rm]=register;
			else if(index==0x05)
			{
				register.set_name(this.w_rex_names[index-1][rm]);
				w_rex_registers[index-1][rm]=register;
			}	
			else if(index==0x06)
			{
				register.set_name(this.w_rex_names[index-1][rm]);
				w_rex_registers[index-1][rm]=register;
			}

		}

	}
	public void insert_into_wo_rex_register(Register register,boolean is_fpu,int[] opsizes,short[] purposes)
	{
		boolean is_reg=false;
		short d_bit=register.get_d_bit();
		int opsize=opsizes[0x01]; 
		short purpose=purposes[0x01];
		boolean dest_is_reg=false;
		if(d_bit==0x01)
		{
			dest_is_reg=true;


		}

		//Account for number of ops and number of regs.

		short reg=register.get_reg();
		short rm = register.get_r_m();
		short index=get_reg_index(is_fpu,purpose,opsize); //look at the indexes, the are arranged from src to dst (op1 -> op2)
		if(dest_is_reg){
			register.set_name(this.wo_rex_names[index][reg]);
			wo_rex_registers[index][reg]=register;
		}
		else
		{
			if(index<0x04)
			{
				register.set_name(this.wo_rex_names[index][rm]);
				wo_rex_registers[index][rm]=register;
			}
			else if(index==0x04)
				fpu_register[rm]=register;
			else if(index==0x05)
			{
				register.set_name(this.wo_rex_names[index-1][rm]);
				wo_rex_registers[index-1][rm]=register;
			}	
			else if(index==0x06)
			{
				register.set_name(this.wo_rex_names[index-1][rm]);
				wo_rex_registers[index-1][rm]=register;
			}
		}
	}
	public void insert_into_register(boolean has_rex,Register register,boolean is_fpu,int[] opsizes,short[] purposes)
	{
		if(has_rex)
			insert_into_w_rex_register(register, is_fpu, opsizes, purposes);
		else
			insert_into_wo_rex_register(register, is_fpu, opsizes, purposes);
	}
	/*Calculates index from opsizes
	 * e.g. 0x00 is a 8-bit general purpose register*/
	public short get_reg_index(boolean is_fpu,short purpose,int opsize)
	{
		if(is_fpu)
		{
			return 9;
		}
		else
		{
			if(purpose==0x00)
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
		}

		return -1;

	}
	public short get_number_of_regs(Register register)
	{
		return register.get_number_of_registers();
	}
	public Register[][] get_registers(boolean rex) {
		if(rex)
			return get_w_rex_register();
		else return get_wo_rex_register();


	}
	/*Finds register by code, translates opsize to index*/
	public Register get_register(boolean rex,boolean src_is_reg,boolean fpu_ins,int opsize, short purpose,short mod, short rm, short reg)
	{
		short index=get_reg_index(fpu_ins,purpose,opsize); //translation

		if(index!=-1)
		{
			if(rex)
			{
				if(src_is_reg)
				{				
					if(!fpu_ins)
						return w_rex_registers[index][reg];
					else
						return fpu_register[reg];
				}
				else
				{
					if(index<0x04)
					{
						return w_rex_registers[index][rm];
					}
					else if(index==0x04)
					{
						return w_rex_registers[index][rm];
					}
					else if(index==0x05)
					{
						return w_rex_registers[index][rm];
					}
					else if(index==0x06)
					{
						return w_rex_registers[index][rm];
					}
					else if (index==0x09)
						return fpu_register[rm];

				}
			}
			else 
			{

				if(src_is_reg)
				{
					if(!fpu_ins)
						return wo_rex_registers[index][reg];
					else
						return fpu_register[reg];

				}
				else
				{
					if(index<0x04)
					{
						return wo_rex_registers[index][rm];
					}
					else if(index==0x04)
					{
						return wo_rex_registers[index][rm];
					}
					else if(index==0x05)
					{
						return wo_rex_registers[index][rm];
					}
					else if(index==0x06)
					{
						return wo_rex_registers[index][rm];
					}
					else if (index==0x09)
						return fpu_register[rm];

				}

			}
		}
		return null;


	}

	public void clear_register(boolean rex,boolean src_is_reg,boolean fpu_ins,int opsize, short purpose,short reg,short mod, short rm)
	{
		short index=get_reg_index(fpu_ins,purpose,opsize);
		if(rex)
		{
			if(src_is_reg)
				w_rex_registers[index][reg]=null;
			else
			{
				if(index<0x04)
				{
					w_rex_registers[index][rm]=null;
				}
				else if(index==0x04)
				{
					w_rex_registers[index][rm]=null;
				}
				else if(index==0x05)
				{
					w_rex_registers[index][rm]=null;
				}
				else if (index==0x09)
					fpu_register[rm]=null;

			}
		}
		else 
		{
			if(src_is_reg)
				wo_rex_registers[index][reg]=null;
			else
			{
				if(index<0x04)
				{
					wo_rex_registers[rm][index]=null;
				}
				else if(index==0x04)
				{
					wo_rex_registers[index][rm]=null;
				}
				else if(index==0x05)
				{
					wo_rex_registers[index][rm]=null;
				}
				else if (index==0x09)
					fpu_register[rm]=null;

			}
		}
	}







}





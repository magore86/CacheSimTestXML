package io;
import hardware.Opcodes;

public class Instruction_x86 {
	
	private static final short BYTE = 0x08;
	public short prefix_1;
	public short prefix_2;
	public short prefix_3;
	public short prefix_4;
	public short pri_opcode;
	public short sec_opcode;
	public short tert_opcode;
	public boolean has_sec;
	public boolean has_tert;
	public short mod_r_m;
	public boolean has_mod_r_m;
	public short ext;
	public boolean has_ext;
	public short[] imm_sizes;
	public short[][] imms;
	public boolean has_imm;
	
	public short reg_sizes;
	public short mem_sizes;
	
	String instruction="";
			
	
	public short instruction_length; // in bytes
	public boolean has_prefix;
	public int mode;
	public boolean rex_b;
	public int default_mode;
	public boolean d;
	public boolean[] pri_opcd_bin;
	private short displacement;
	private boolean has_sib;
	private int sib;
	private short disp_sib;
	private boolean has_fixed_register;
	private String fixed_register;
	private int reg_size;
	private boolean is_sreg;
	private boolean is_eee_1;
	private boolean is_eee_2;
	private boolean has_fixed_register_2;
	private String fixed_register_2;
	private int size;
	
	
	/*Methods for finding MODR/M, SIB and displacement */
	public Instruction_x86()
	{
		
	}
	public String get_instruction()
	{
		return instruction;
	}
	public short get_mod_code(short mod_byte)
	{
		short mod=0;
		boolean[] mod_r_m_bin=generate_binary_boolean(mod_byte,BYTE);
		for(int i=0;i<mod_r_m_bin.length;i++)
		{
			if(mod_r_m_bin[i])
			{
				if(i<2)
				{
					mod+=Math.pow(2,1-i);


				}
			}
		}
		return mod;
		
		
	}
	public short get_r_m_code(short mod_byte)
	{
		
		short r_m=0;
		boolean[] mod_r_m_bin=generate_binary_boolean(mod_byte,BYTE);
		short size_i;
				
		//little endian


		for(int i=0;i<mod_r_m_bin.length;i++)
		{
			if(mod_r_m_bin[i])
			{
				if(i>=5 && i<8)
				{
					r_m+=Math.pow(2,(7-i));	
				}
			}

		}
		return r_m;
	}
	public short get_reg_code(short mod_byte)
	{
		
		short reg =0;
		boolean[] mod_r_m_bin=generate_binary_boolean(mod_byte,BYTE);
		short size_i;
				
		//little endian


		for(int i=0;i<mod_r_m_bin.length;i++)
		{
			if(mod_r_m_bin[i])
			{
				if(i>=2 && i<5)
				{
					reg+=Math.pow(2,(4-i));	
				}
			}

		}
		return reg;
	}
	
	
	public short get_ext(short mod_byte)
	{
		short ext = 0;
		boolean[] temp=generate_binary_boolean(mod_byte,BYTE);
		for(int i=0x02;i<0x05;i++)
		{
			ext+=Math.pow(2,i-2);
		}
		return ext;
	}

	public String calc_no_mod_r_m(short opcode)
	{
		String register="";
		boolean[] opcd_bin=generate_binary_boolean(opcode,BYTE);
		short reg = 0;
		for(int i=5;i<opcd_bin.length;i++)
		{
			if(opcd_bin[i])
				reg+=Math.pow(2,i-5);	
		}
		if(mode==16)
			register=Opcodes.r16[reg];
		else if(mode==32)
			register=Opcodes.r32[reg];
		else if(mode==64)
			register=Opcodes.r64[reg];
		else
			register=Opcodes.r8[reg];
		
		return register;
	}

	public String get_prefix(short prefix) {
		return instruction;
		
	}
	public boolean[] generate_binary_boolean(short code,short length)
	{
		char[] binary=null;
		char[] temp=null;
		boolean[] binary_bool = null; 
		int k=0;
		try
		{
			binary=Integer.toBinaryString((int)code).toCharArray();
			temp=new char[length];
			k=binary.length-1;
			for(int i=length-1;i>-1;i--)
			{
				if(k>-1)
				{
					temp[i]=binary[k];
					k--;
				}
				else
					temp[i]='0';
				
			}
				
			binary=temp;
		}
		catch(Exception e)
		{
			System.out.print("Data type error");
		}
		binary_bool=new boolean[binary.length];
		for(int i=0;i<binary.length;i++)
		{
			if(binary[i]=='1')
				binary_bool[i]=true;
		}
		return binary_bool;
	}
	public boolean has_displacement(short modrm)
	{
		boolean[] modrm_bin=generate_binary_boolean(modrm,BYTE);
		if(!(modrm_bin[0x00] || modrm_bin[0x01]))
		{
			if(modrm_bin[0x05] && !modrm_bin[0x06] && !modrm_bin[0x07])
			{
				displacement=0x20;
				return true;
			}
			else return false;
		}
		else if(!modrm_bin[0x00] && modrm_bin[0x01])
		{
			displacement=0x08;
			return true;
		}
		else if(modrm_bin[0x00] && !modrm_bin[0x01])
		{
			displacement=0x20;
			return true;
		}
		else
		{
			displacement=0x00;
			return false;
		}
		
	}
	public short get_displacement() {
		
		return displacement;
	}
	
	public short get_scalar(short sib,short mode)
	{
		
		
		
		if(sib<=0xFF && sib>0xC0)
			return 8;
		if(sib<=0xBF && sib>0x80)
			return 4;
		if(sib<=0x7F && sib>0x40)
			return 2;
		if(sib<=0x3F && sib>0x00)
			return 0;
		
		 return -1;
		
		
	}
	public boolean has_sib()
	{
		return true;
	}
	public short get_SIB_disp()
	{
		return disp_sib;
	}
	public boolean has_sib(short modrm) {
		
		short r_m=get_r_m_code(modrm);
		
		if(r_m==0x03)
			return true;
		else return false;
	}
	
}

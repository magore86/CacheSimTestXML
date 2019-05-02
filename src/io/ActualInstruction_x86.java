package io;



public class ActualInstruction_x86 {
	
	private static final short BYTE = 0x08;
	private short[] prefixes;
	private boolean has_rex;
	private short[] opcodes;
	private short extension;
	private short modrm;
	private short mod;
	private short rm;
	private short reg;
	private short sib;
	private short scalar;
	private short scale;
	private short index;
	private short base;
	private short[] displacement;
	private short[][] immediates;
	private boolean has_immediate;
	private short length;
	private short d_bit;
	private boolean reg_is_in_opcode;
	private boolean is_mov;
	private boolean is_pop;
	private boolean is_push;
	private boolean is_lea;
	private boolean is_call;
	private boolean is_loop;
	private boolean has_memory_address;
	private short memory_address_type;
	private short memory_address_length;
	private boolean has_relative_address;
	private short[] relative_address;
	private boolean has_offset;
	private short[] offset;
	private boolean has_pointer;
	private short[] pointer;
	private boolean is_fpu;
	private short mode;
	private short size_FPU_reg;
	private boolean[] reg_purpose;
	private short[] reg_purpose_indexes;
	private int[] opsizes;
	private short number_of_ops;
	private boolean to_memory;
	private boolean from_memory;
	private ActualInstruction_x86 next;
	
	
	
	/*Transport class: Methods for calculating mod,R and M */
	public ActualInstruction_x86(short[] prefixes,boolean has_rex,short[] opcodes,short extension,
								short modrm,short sib, short[] displacement, short[][] immediates,
								short length, boolean is_mov, boolean is_pop, boolean is_push, boolean is_lea, boolean is_call,boolean is_loop,boolean has_memory_address,
								short memory_address_type,short memory_address_length,boolean has_relative_address,short[] relative_address,boolean has_offset,short[] offset,
								boolean has_pointer,short[] pointer,boolean is_fpu,short size_FPU_reg, boolean reg_is_in_opcode,short mode,boolean[] reg_purpose,
								short[] reg_purpose_indexes, int[] opsizes, boolean has_immediate, short number_of_ops)
	{
		this.prefixes=prefixes;
		this.has_rex=has_rex;
		this.opcodes=opcodes;
		this.extension=extension;
		this.modrm=modrm;
		this.sib=sib;
		this.displacement=displacement;
		this.immediates=immediates;
		this.has_immediate=has_immediate;
		this.length=length;
		
		this.reg_is_in_opcode=reg_is_in_opcode;
		this.is_mov=is_mov;
		this.is_pop=is_pop;
		this.is_push=is_push;
		this.is_lea=is_lea;
		this.is_call=is_call;
		this.is_loop=is_loop;
		this.has_memory_address=has_memory_address;
		this.memory_address_type=memory_address_type;
		this.memory_address_length=memory_address_length;
		this.has_relative_address=has_relative_address;
		this.relative_address=relative_address;
		this.has_offset=has_offset;
		this.offset=offset;
		this.has_pointer=has_pointer;
		this.pointer = pointer;
		this.is_fpu=is_fpu;
		this.size_FPU_reg=size_FPU_reg;
		this.mode=mode;
		
		this.opsizes=opsizes;
		this.reg_purpose_indexes=reg_purpose_indexes;
		this.number_of_ops=number_of_ops;
		
		
		if(opcodes[0x02]>-1)
		{
			d_bit=(short)(opcodes[0x02]%2);
		}
		else if (opcodes[0x01]>-1)
		{
			d_bit=(short)(opcodes[0x01]%2);
		}
		else
		{
			d_bit=(short)(opcodes[0x00]%2);
		}
		if(modrm>-1)
		{
			this.mod=get_mod_code(modrm);
			this.rm=get_r_m_code(modrm);
			this.reg=get_reg_code(modrm);
		}
		if(sib>-1)
		{
			this.scale=get_mod_code(sib);
			this.index=get_reg_code(sib);
			this.base=get_r_m_code(sib);
			this.scalar=(short)Math.pow(2,scale);
			
		}
		
		if(this.reg_is_in_opcode)
		{
			for(int i=0;i<opcodes.length;i++)
			{
				if(opcodes[i]==-1)
				{
					if(i>0)
						reg=get_reg_code(opcodes[i-1]);
					else
						reg=0;
					break;
				}
		
			}
		}
		boolean has_memory_access=false;
		if(this.has_relative_address || this.has_immediate || this.has_offset)
			has_memory_access=true;
		else if(this.mod<0x03)
		{
			has_memory_access=true;
		}
		if(has_memory_access)
		{
			if(d_bit==0x00)
			{	
				to_memory=true;
				from_memory=false;
			}
			else
			{
				from_memory=true;
				to_memory=false;
			}
		}
		else
		{
			from_memory=false;
			to_memory=false;
		}
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
	public short[] get_prefixes()
	{
		return prefixes;
	}
	public short[] get_opcodes()
	{
		return opcodes;
	}
	public short[] get_displacement()
	{
		return displacement;
	}
	public short[][] get_immediates()
	{
		return immediates;
	}
	public short get_extension()
	{
		return extension;
	}
	public short get_modrm()
	{
		return modrm;
	}
	public short get_mod()
	{
		return mod;
	}
	public short get_rm()
	{
		return rm;
	}
	public short get_reg()
	{
		return reg;
	}
	public short get_sib()
	{
		return sib;
	}
	public short get_scale()
	{
		return scale;
	}
	public short get_index()
	{
		return index;
	}
	public short get_base()
	{
		return base;
	}
	public short get_scalar()
	{
		return scalar;
	}
	public short get_length()
	{
		return length;
	}
	public short get_d_bit()
	{
		return d_bit;
	}
	public boolean has_rex()
	{
		return has_rex;
	}
	public boolean is_mov()
	{
		return is_mov;
	}
	public boolean is_pop()
	{
		return is_pop;
	}
	public boolean is_push()
	{
		return is_push;
	}
	public boolean is_lea()
	{
		return is_lea;
	}
	public boolean is_call()
	{
		return is_call;
	}
	public boolean is_loop()
	{
		return is_loop;
	}
	public boolean has_memory_address()
	{
		return has_memory_address;
	}
	public boolean has_relative_address()
	{
		return has_relative_address;
	}
	public boolean has_offset()
	{
		return has_offset;
	}
	public boolean has_pointer()
	{
		return has_pointer;
	}
	public short get_memory_address_type()
	{
		return memory_address_type;
	}
	public short get_memory_address_length()
	{
		return memory_address_length;
	}
	public short[] get_relative_address()
	{
		return relative_address;	
	}
	public short[] get_offset()
	{
		return offset;
	}
	public short[] get_pointer()
	{
		return pointer;
	}
	public boolean is_fpu()
	{
		return is_fpu;
	}
	public short get_size_FPU_reg()
	{
		return size_FPU_reg;
	}
	public short get_mode()
	{
		return mode;
	}
	public boolean get_reg_is_in_opcode()
	{
		return reg_is_in_opcode;
	}
	public short[] get_reg_purpose_indexes()
	{
		return reg_purpose_indexes;
	}
	public int[] get_opsizes()
	{
		return opsizes;
	}
	public boolean has_immediate()
	{
		return has_immediate;
	}
	public short get_number_of_ops() 
	{
		return number_of_ops;
	}
	public void add_instr(ActualInstruction_x86 instruction, boolean[] bin_address, short[] hex_address) 
	{
		next=instruction;	
	}
	public void set_next(ActualInstruction_x86 next)
	{
		this.next=next;
	}
	public ActualInstruction_x86 get_next()
	{
		return next;
	}
	public boolean write_to_memory() 
	{
		return to_memory;
	}
	public boolean read_from_memory() {
		return from_memory;
	}
	public int get_max_value_length() {
		return 128/8;
	}

	
	

}

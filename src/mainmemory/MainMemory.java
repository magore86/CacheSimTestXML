package mainmemory;


import java.util.Hashtable;
import java.util.Random;

import io.MemoryHandler;


//Class representing a MainMemory controller



public class MainMemory {
	// create an array of arrays to represent the main memory. Fixed size of 16-bit per array

	public boolean[] address;
	public boolean[] next_available_address;
	
	public int number_of_used_addresses;
	public int level;
	public int next_child;

	public int size;
	private boolean[] first_address;
	private boolean[] next_available_bin_address;
	private int hex_address_length;
	private int bin_address_length;
	private short[] next_available_hex_address;
	private MemoryHandler memory_handler;

	
	
	
	
	private Hashtable<String,Memory> mem_ht;

	

	/* Main memory: HashMap is memory with <String> and store an memory object*/
	public MainMemory(boolean space, int size)
	{
		if(space)
			mem_ht = new Hashtable<String,Memory>();
	

		bin_address_length=size;
		hex_address_length=size/8;
		next_available_bin_address=new boolean[size];
		next_available_hex_address=new short[size/8]; 
		first_address=new boolean[size];
	}
	public short[] read_random(int length)
	{
		short[] data=new short[length];
		for(int i=0;i<data.length;i++)
		{
			data[i]=random_data();
		}
		return data;
	}
	public short[] insert(short data)
	{
		String address="";
		short[] hex_address=new short[hex_address_length];
		boolean[] bin_address=new boolean[bin_address_length];
		for(int i=0;i<hex_address.length;i++)
			hex_address[i]=next_available_hex_address[i];
		
		for(int i=0;i<bin_address.length;i++)
			bin_address[i]=next_available_bin_address[i];
		
		increment_addresses(1);
		
		Memory mem_cell=new Memory(hex_address,bin_address, data);
		
		for(int i=0;i<hex_address_length;i++)
		{
			if(hex_address[i]<0x0A)
			{
				address+="0" + hex_address[i];
			}
			else
				address+=hex_address[i];
		}
		mem_ht.put(address, mem_cell);
		return hex_address;
	}
	public short[] insert(short data,short[] hex_address,boolean[] bin_address)
	{
		String address="";
		
		Memory mem_cell=new Memory(hex_address,bin_address, data);
		
		for(int i=0;i<hex_address_length;i++)
			address+=hex_address[i];
		
		mem_ht.put(address, mem_cell);
		return hex_address;
	}
	public void write(short data, short[] hex_address, boolean[] bin_address)
	{
		Memory mem_cell=get_cell(hex_address);
		if(mem_cell!=null)
			mem_cell.write(bin_address,data);
		else
			insert(data,hex_address,bin_address);
	}
	
	public Memory get_cell(short[] hex_address)
	{
		String address="";
		
		for(int i=0;i<hex_address.length;i++)
		{
			if(hex_address[i]<0x0A)
			{
				address+="0" + hex_address[i];
			}
			else
			{
				address+=""+hex_address[i];
			}
		}
				
		Memory mem_cell=mem_ht.get(address);
		if(mem_cell!=null)
		{
			short[] temp=mem_cell.get_hex_address();
			for(int i=0;i<hex_address.length;i++)
			{
				if(hex_address[i]!=temp[i])
					return null;
			}
			return mem_cell;
		}
		return null;
	}
	
	public short read(boolean[] bin_address,short[] hex_address)
	{
		Memory mem_cell=get_cell(hex_address);
		short data=-1;
		if(mem_cell==null)
		{
			String address=gen_key(hex_address);
			data=random_data();
			mem_cell=new Memory(hex_address,bin_address,data);
			mem_ht.put(address, mem_cell);
			return data;
			
		}
		else
		{
			data=mem_cell.read();
			return data;
		}
	}
	/*Read from memory: Random if not in memory. Since there is no guarantee that a element will be in main memory
	 * , there is random generation of memory block. This is due to the uncertainty of moving the program into memory */
	public short[] read_3(short[] hex_address,boolean[] bin_address, int length) {
		
		short[] data=new short[length];
		short[] nxt_h_addr=hex_address;
		boolean[] nxt_b_addr=bin_address;
		for(int i=0;i<length;i++)
		{
			Memory mem_cell=get_cell(nxt_h_addr);

			if(mem_cell==null)
			{
				String address=gen_key(nxt_h_addr);
				data[i]=random_data();
				mem_cell=new Memory(nxt_h_addr,nxt_b_addr,data[i]);
				mem_ht.put(address, mem_cell);
				

			}
			else
			{
				data[i]=mem_cell.read();
				
			}
			nxt_h_addr=inc_hex_address(nxt_h_addr);
			nxt_b_addr=inc_bin_address(nxt_b_addr);
		}
		return data;
	}
	
	private String gen_key(short[] hex_address)
	{
		String address="";
		for(int i=0;i<hex_address.length;i++)
		{
			if(hex_address[i]<0x0A)
			{
				address+="0" + hex_address[i];
			}
			else
			{
				address+=""+hex_address[i];
			}
		}
		return address;
	}
	private short random_data() {
		Random random=new Random();
		short data=-1;
		int bound=0x100;
		data=(short)(random.nextInt(bound) % bound);
		return data;
	}
	public boolean addr_compare(short[] hex_address, short[] hex_address2, short data_length,short data_length2) 
	{


		for(int i=0;i<hex_address_length;i++)
		{
			if(hex_address[i]!=hex_address2[i])
				return false;
		}
		if(data_length==data_length2)
			return true;
		else return false;

	}
	public int get_number_of_used_addresses() {

		return number_of_used_addresses;
	}
	public boolean[] get_first_address() {

		return first_address;
	}
	public boolean[] get_next_available_address()
	{
		boolean[] address =next_available_address;
		//increment_addresses();
		return address;
	}
	public boolean[] peek_next_bin_address()
	{
		return next_available_bin_address;
	}
	public short[] peek_next_hex_address()
	{
		return next_available_hex_address;
	}
	public void increment_addresses(int data_length)
	{
		inc_bin_address(data_length);
		inc_hex_address(data_length);
	}
	public void inc_bin_address(int data_length)
	{
		for(int j=0;j<data_length;j++)
		{
			for(int i=bin_address_length-1;i>-1;i--)

			{

				if(next_available_bin_address[i])
				{
					next_available_bin_address[i]=false;
				}
				else
				{
					next_available_bin_address[i]=true;
					break;
				}

			}
		}
	}
	public void inc_hex_address(int data_length)
	{
		for(int j=0;j<data_length;j++)
		{
			next_available_hex_address=inc_hex_address(next_available_hex_address);	
		}
	}
	public void write_to_memory(boolean[] bin_address,short[] hex_address,short data) //bin_data != hex_data error
	{
		short[] hex_addr=new short[hex_address_length];
		boolean[] bin_addr=new boolean[bin_address.length];
		for(int i=0;i<hex_address_length;i++)
		{
			hex_addr[i]=hex_address[i];
		}
		for(int i=0;i<bin_addr.length;i++)
		{
			bin_addr[i]=bin_address[i];
		}
		

		insert(data,hex_address,bin_address); //insert into main memory with address// 
	}
	public boolean[] inc_bin_address(boolean[] b) {

		for(int i=b.length-1;i>-1;i--)
		{
			if(b[i])
			{
				b[i]=false;
			}
			else
			{
				b[i]=true;
				return b;
			}
		}
		return b;

	}
	public short[] inc_hex_address(short[] h) {

		for(int i=h.length-1;i>-1;i--)
		{
			if(h[i]==255)
			{
				h[i]=0;
			}
			else
			{
				h[i]++;
				return h;
			}
		}
		return h;


	}
	public void write(short[] data, short[] start_hex_addr, boolean[] start_bin_addr) {
		
		short[] nxt_h_addr=start_hex_addr;
		boolean[] nxt_b_addr=start_bin_addr;
		
		for(int i=0;i<data.length;i++)
		{
			write(data[i],nxt_h_addr,nxt_b_addr);
			nxt_h_addr=inc_hex_address(nxt_h_addr);
			nxt_b_addr=inc_bin_address(nxt_b_addr);
		}
	}
	public void clear(int address_length, boolean space) {
		
		mem_ht=null;
		if(space)
			mem_ht=new Hashtable<String,Memory>();
		
		size=address_length;
		
		next_available_bin_address=new boolean[size];
		next_available_hex_address=new short[size/8]; 
		first_address=new boolean[size];
		
	}
	
	
	


}
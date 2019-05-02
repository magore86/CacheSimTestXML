package io;
import mainmemory.*;

public class MemoryHandler {
	
	private MainMemory main_memory;
	private boolean[] next_available_bin_address;
	private short[] next_available_hex_address;
	private int bin_address_length;
	private int hex_address_length;
	
	private final static short ONE_BYTE=8;
	
	public MemoryHandler(int bit_length, MainMemory main_memory)
	{
		
		this.main_memory=main_memory;// new MainMemory(bit_length);
		bin_address_length=bit_length;
		next_available_bin_address=new boolean[bin_address_length];
		hex_address_length=bit_length/ONE_BYTE;
		next_available_hex_address=new short[hex_address_length];
	}
	public boolean[] get_next_bin_address()
	{
		return next_available_bin_address;
	}
	public short[] get_next_hex_address()
	{
		return next_available_hex_address;
	}
	public void increment_addresses()
	{
		inc_bin_address();
		inc_hex_address();
	}
	public void inc_bin_address()
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
				return;
			}
				
		}
	}
	public void inc_hex_address()
	{
		for(int i=hex_address_length-1;i>-1;i--)
		{
			if(next_available_hex_address[i]==0xFF)
			{
				next_available_hex_address[i]=0x00;
			}
			else
			{
				next_available_hex_address[i]++;
				return;
			}
				
			
		}
	}
	public MainMemory get_memory()
	{
		return main_memory;
	}
}
	

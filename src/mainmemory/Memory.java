package mainmemory;


public class Memory {
	
	
	private Memory[] next_address_bits;
	
	private short data;


	//Memory memory;
	
	;
	private boolean is_instruction;
	private short[] hex_address;
	private short data_length; //byte-addressable
	private short[] first_hex_address;
	private short[] last_hex_address;
	private boolean[] bin_address;
	private int number_of_accesses;
	
	
	/*Memory cell*: mostly methods for setting and getting data*/
	public Memory(short[] hex_address,boolean[] bin_address, short data)
	{
		this.hex_address=hex_address;
		this.bin_address=bin_address;
		
		this.data=data;
		this.number_of_accesses=0;
	}
	public short read_hex_data()
	{
		number_of_accesses++;
		return data;
	}
	public long get_number_of_accesses()
	{
		return number_of_accesses;
	}
	public Memory[] get_next_address_bits() {
		return next_address_bits;
	}
	public void set_next_address_bits(Memory[] next_address_bits) {
		this.next_address_bits = next_address_bits;
	}
	public boolean is_instruction()
	{
		return is_instruction;
	}
	public short read() {
		number_of_accesses++;
		return data;
		
	}
	public short[] get_first_hex_address() 
	{
		
		return first_hex_address;
	}
	public short[] get_last_hex_address()
	{
		return last_hex_address;
	}
	
	public short get_data_length()
	{
		return data_length;
	}
	public Memory get_data() {
		return this;
	}
	public short[] get_hex_address() {
		return hex_address;
	}
	public boolean[] get_bin_address()
	{
		return bin_address;
	}
	public void write(boolean[] bin_address, short data) 
	{	
		this.bin_address=bin_address;
		this.data=data;	
	}
	

}

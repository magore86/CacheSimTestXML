package hardware;

public class Entry {
	
	
	
	
	private short[] hex_data;
	private short data;
	private short[] hex_address;
	private boolean[] bin_address;
	private boolean[] tag;

	private long sequence_order;
	private short entry_no;
	private int data_length;
	

	/*The data that is written to cache. Stores metadata */
	public Entry(short data,short[] hex_address,boolean[] bin_address,boolean[] tag)
	{
		this.hex_address=hex_address;
		this.data=data;
		this.bin_address=bin_address;
		this.tag=tag;
	}
	public void write(short[] hex_data)
	{
		if(hex_data!=null)
		{
			this.hex_data=new short[hex_data.length/8];
			for(int i=0;i<this.hex_data.length;i++)
			{
				this.hex_data[i]=hex_data[i];
			}
		}
	}
	public void write(short data)
	{
		this.data=data;
	}
	public short read()
	{
		return data;
	}
	public short get_data()
	{
		return data;
	}
	public short[] get_hex_address()
	{
		return hex_address;
	}
	public boolean[] get_bin_address()
	{
		return bin_address;
	}
	public void set_tag(boolean[] new_tag)
	{
		tag=new_tag;
	}
	public boolean[] get_tag()
	{
		return tag;
	}
	public void set_sequence_order(int sequence_order)
	{
		this.sequence_order=sequence_order;
	}
	public long get_sequence_order()
	{
		return this.sequence_order;
	}
	public int get_data_length()
	{
		return data_length;
	}
	public short get_entry_no()
	{
		return entry_no;
	}
	public short[] get_hex_data()
	{
		return hex_data;
	}
	
	
		
}

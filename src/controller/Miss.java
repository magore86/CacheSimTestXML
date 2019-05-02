package controller;

public class Miss {

	private String name;
	private String type;
	private String b_address="";
	private String h_address="";
	private boolean[] bin_address;
	private short[] hex_address;
	private short data;
	private int level;
	private int subtype; //0=Cold,1=Coherence,2=Capacity 3=Conflict
	
	/* Transport class */
	public Miss(String name, String type,int subtype,int level, boolean [] bin_address,short[] hex_address, short data)
	{
		this.name=name;
		this.type=type;
		this.subtype=subtype;
		this.bin_address=bin_address;
		this.hex_address=hex_address;
		this.data=data;
		this.level=level;
		
		for(int i=0; i<bin_address.length;i++)
		{
			if(bin_address[i])
				b_address+="1";
			else
				b_address+="0";
		}
		for(int i=0;i<hex_address.length;i++)
		{
			h_address+=hex_address[i] + " ";
		}
	}
	public String get_name()
	{
		return this.name;
	}
	public String get_type()
	{
		return this.type;
	}
	public int get_level()
	{
		return level;
	}
	public String get_b_address()
	{
		return this.b_address;
	}
	public String get_h_address()
	{
		return this.h_address;
	}
	public short get_data()
	{
		return data;
	}
	public int get_sub_type() {
		return subtype;
	}
	public void set_sub_type(int subtype) {
		this.subtype = subtype;
	}
}

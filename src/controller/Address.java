package controller;

import java.util.HashMap;

/*Address map associated with CPU number. Mainly methods for searching in a HashMap */
public class Address {

	private String txt_bin_addr;
	private String txt_hex_addr;
	private HashMap<Integer, String> cpu_numbers;
	
	public Address(boolean[] bin_addr,short[] hex_addr)
	{
		cpu_numbers=new HashMap<Integer,String>();
		set_txt_bin_address(bin_addr);
		set_txt_hex_address(hex_addr);
	}

	private void set_txt_hex_address(short[] hex_addr) {

		for(int i= 0;i<hex_addr.length;i++)
		{
			if(hex_addr[i]<0x10)
			{
				txt_hex_addr+="0" + hex_addr[i];
			}
			else
			{
				txt_hex_addr+= hex_addr[i];
			}
		}		
	}
	private void set_txt_bin_address(boolean[] bin_addr) {
		
		for(int i= 0;i<bin_addr.length;i++)
		{
			if(bin_addr[i])
				txt_bin_addr+="1";
			else
				txt_bin_addr+="0";
		}	
	}
	public void assign_cpu(int cpu_no)
	{
		cpu_numbers.put(cpu_no,"CPU number" + cpu_no);
	}
	public void remove_cpu(int cpu_no)
	{
		cpu_numbers.remove(cpu_no);
	}
	public boolean contains_cpu(int cpu_no)
	{
		if(cpu_numbers.containsKey(cpu_no))
			return true;
		else return false;
	}
	public String get_txt_bin_addr()
	{
		return txt_bin_addr;
	}
	public String get_txt_hex_addr()
	{
		return txt_hex_addr;
	}
}

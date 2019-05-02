package io;

public class ExeHeader {
	
	private int[] virtual_size;
	private int[] raw_data_size;
	private int[] r_pointer;
	private int number_of_sections;
	private int start;

	public ExeHeader(int[] virtual_size, int[] raw_data_size, int[] r_pointer, int number_of_sections, int start) {
		
		this.virtual_size=virtual_size;
		this.raw_data_size=raw_data_size;
		this.r_pointer=r_pointer;
		this.number_of_sections=number_of_sections;
		this.start=start;
		
	}
	public int[] get_virtual_size()
	{
		return this.virtual_size;
	}
	public int[] get_raw_data_size()
	{
		return this.raw_data_size;
	}
	public int[] get_r_pointer()
	{
		return this.r_pointer;
	}
	public int get_number_of_sections()
	{
		return this.number_of_sections;
	}
	public int get_start()
	{
		return start;
	}
	
	

}

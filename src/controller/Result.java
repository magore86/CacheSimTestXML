package controller;


public class Result{

	private CPUProcess proc;
	private short[] hex_addr;
	private boolean[] bin_addr;
	private short[] data;

	public Result(CPUProcess proc,short[] hex_addr,boolean[] bin_addr,short[] data)
	{
		this.proc=proc;
		this.hex_addr=hex_addr;
		this.bin_addr=bin_addr;
		this.data=data;

	}
	public CPUProcess get_proc()
	{
		return proc;
	}
	public short[] get_hex_addr()
	{
		return hex_addr;
	}
	public boolean[] get_bin_addr()
	{
		return bin_addr;
	}
	public short[] get_data()
	{
		return data;
	}
	
}
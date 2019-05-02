package controller;

import java.util.ArrayList;

import application.SimResult;

public class PerformanceMetric {
	
	
	private double speedup;
	private double parallel_fraction;
	private int number_of_instructions;
	private int number_of_instructions_parallel;
	private int serial_executions;
	private int parallel_executions;
	private int memory_access;
	private TypesOfMisses miss_types;
	private ArrayList<SimResult> results;
	//Collection of metrics
	public PerformanceMetric(double speedup, double parallel_fraction, int number_of_instructions,
			int serial_executions, int parallel_executions, int number_of_instructions_parallel, int memory_access) {
		
		this.number_of_instructions=number_of_instructions;
		this.number_of_instructions_parallel=number_of_instructions_parallel;
		this.serial_executions=serial_executions;
		this.parallel_executions=parallel_executions;
		this.speedup=speedup;
		this.parallel_fraction=parallel_fraction;
		this.memory_access=memory_access;
		
		
	}	
	public double get_speedup()
	{
		return this.speedup;
	}
	public double get_parallel_fraction()
	{
		return this.parallel_fraction;
	}
	public int get_number_instructions()
	{
		return this.number_of_instructions;
	}
	public int get_number_instructions_parallel()
	{
		return this.number_of_instructions_parallel;
	}
	public int get_serial_executions()
	{
		return serial_executions;
	}
	public int get_parallel_executions()
	{
		return parallel_executions;
	}
	public int get_memory_access()
	{
		return memory_access;
	}
	public double get_serial_fraction()
	{
		return (1-this.parallel_fraction);
	}
	public double get_average_cpu_usage_parallel()
	{
		return ((double)this.number_of_instructions_parallel/(double)parallel_executions);
	}
	public double get_share_memory_access()
	{
		return ((double)this.memory_access/(double)this.number_of_instructions);
	}
	public void add_miss_types(TypesOfMisses tom)
	{
		this.miss_types=tom;
	}
	public TypesOfMisses get_tom()
	{
		return miss_types;
	}
	public StringBuilder perf_toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\nNumber of instructions: " + this.number_of_instructions);
		sb.append("\nNumber of instructions run in parallel: " + this.number_of_instructions_parallel);
		sb.append("\nNumber of serial executions: " + this.serial_executions);
		sb.append("\nNumber of parallel executions: " + this.parallel_executions);
		sb.append("\nSpeed-up = " + this.speedup);
		sb.append("\np = " + this.parallel_fraction);
		sb.append("\n(1-p) = " + this.get_serial_fraction());
		sb.append("\nNumber of memory instruction: " + this.memory_access);
		sb.append("\nShare of memory instruction: " + this.get_share_memory_access());
		sb.append("\nAverage CPU usage in parallel execution: " + this.get_average_cpu_usage_parallel());
		sb.append("\n\nMisses, all included ");
		sb.append("\n\n"+miss_types.misses_toString());
		return sb;
	}
	public void add_results(SimResult sr)
	{
		results.add(sr);
	}
	public void set_results(ArrayList<SimResult> al)
	{
		this.results=al;
	}
	public ArrayList<SimResult> get_results()
	{
		return this.results;
	}
}

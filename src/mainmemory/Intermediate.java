package mainmemory;

public class Intermediate {
	
	private short data;
	private long number_of_accesses;
	
	public Intermediate(short data,long number_of_accesses)
	{
		this.data=data;
		this.number_of_accesses=number_of_accesses;
	}
	public short get_data()
	{
		return data;
	}
	public long get_number_of_accesses()
	{
		return number_of_accesses;
	}
}

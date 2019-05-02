package controller;

public class TypesOfMisses {
	
	/*Transport class for miss types to GUI*/
	private int[] miss_type_numbers;
	public TypesOfMisses(int[] miss_type_numbers)
	{
		this.miss_type_numbers=miss_type_numbers;
	}
	public int[] get_miss_type_numbers()
	{
		return this.miss_type_numbers;
	}
	public StringBuilder misses_toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Compulsory:" + miss_type_numbers[0] );
		sb.append("\nCoherence:" + miss_type_numbers[1] );
		sb.append("\nConflict/Capacity:" + miss_type_numbers[2] ); //one and two are the same, but can be used to distinguish between them,
		sb.append("\nConflict/Capacity:" + miss_type_numbers[3] +"\n"  ); //However, a fully associative cache has only cap misses an direct mapped 
																	//only conflict
		return sb;
	}
}

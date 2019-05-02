package hardware;

import java.util.HashMap;


public class Cache {


	private int capacity;
	private int address_length; //1,2,4,8,16,32- or 64-bit
	
	
	private int associativity;

	private int blocksize;
	
	private int length=0;
	

	private int offset_start;
	private int offset_stop;
	private int index_start;
	private int index_stop;
	private int tag_start;
	private int tag_stop;
	private int offset_length;
	private int index_length;
	
	private HashMap<Integer,Index> set;
	
	
	public Cache (int associativity, int capacity, int blocksize, int level, 
			boolean shared,int bits)
	{
		this.capacity=capacity;
		this.blocksize=blocksize;
	
		this.associativity=associativity;
		
		length =  this.capacity /(this.blocksize*this.associativity); 
		
		int length_2=(int) (Math.log(length)/Math.log(2));
		index_length=calc_index_length(length);
		
		length_2=this.capacity/(this.associativity*length);
		offset_length=calc_offset_length(this.blocksize);//(length_2);
		
		length_2=(int)(Math.log(length_2)/Math.log(2));
		
		set_parameters();
		
		set = new HashMap<Integer,Index>();
	}
	public int calc_index_length()
	{
		double log2 = Math.log(2);
		double length = Math.log((this.length))/log2;
		return (int)length;
	}
	public int calc_offset_length()
	{	
		double log2 = Math.log(2);
		double length = Math.log((blocksize))/log2;
		return (int)length;	
	}
	public int calc_tag_length(int addr_length, int index_length, int offset_length)
	{
		return  addr_length - index_length - offset_length; 
	}
	public int calc_index_length(int number_of_sets)
	{
		double log2 = Math.log(2);
		double length = Math.log((number_of_sets))/log2;
		return (int)length;
	}
	public int calc_offset_length(int blocksize)
	{
		double log2 = Math.log(2);
		double length = Math.log((blocksize))/log2;
		return (int)length;	
	}
	public void set_parameters() //32-bit address, 256KB cap, 64B blocksize
	{
		offset_start=address_length-1;

		offset_stop=offset_start-offset_length;
		index_start=offset_stop-1;

		index_stop=index_start-index_length;
		tag_start=index_stop-1;
		tag_stop=0; //Should be 0
	}


	public int get_number_of_blocks() {
		return length*associativity*blocksize;
	}
	public long prefetch(int addr, int way) {
		// TODO Auto-generated method stub
		return 0;
	}
	public HashMap<String,Slot> get_slots(int index) {
		if(set.size()>0)
			return set.get(index).ret_slots();
		else return null;
	
	}
	public Slot get_slot(boolean[] tag, int index) {
		if(set.size()>0)
		{
			Index in =set.get(index);
			if(in!=null)
				return in.get_slot(tag);
			else return null;
		}
		return null;
	}
	public Slot write(Slot new_slot,int index,boolean[] tag, boolean[] bin_addr) {
		
		Index in=set.get(index);
		if(in==null)
		{
			in=new Index(associativity,blocksize);
			set.put(index, in);					
			
		}
		return in.write(tag, new_slot);
		
	}
	public void nullify_slot(int index, boolean[] tag) {
		
		Index in=set.get(index);
		if(in!=null)
		{
			in.nullify_slot(tag);
			
		}
		
	}
	

	
	
}



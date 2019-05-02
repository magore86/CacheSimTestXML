package controller;

import java.awt.List;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;

import io.FileReader;
import hardware.CPU;
import hardware.Cache;
import hardware.Entry;

import hardware.Slot;
import mainmemory.Bus;
import mainmemory.IMC;
import mainmemory.Intermediate;
import mainmemory.MainMemory;

public class CacheController implements IBus, IMC{

	public Cache[] caches; //different levels of caches 
	public int number_of_levels; //no of caches
	public int number_of_CPUs; //no of CPUs
	public int address_length;
	private int[] offset_start;
	private int[] offset_stop;
	private int[] index_start;
	private int[] index_stop;
	private int[] tag_start;
	private int[] tag_stop;
	private int[] offset_length;
	private int[] index_length;
	public int[] capacity;
	public int[] blocksize;
	public int[] associativity;
	private LinkedList<Miss> misses;
	public int cpu_no;
	private int[] no_of_hits;
	private int[] no_of_misses;

	private int incl_policy;
	private MainController main_controller;
	private Bus bus;
	/* Cache coherency methods: MESIF */
	public CacheController(int cpu_no,int number_of_levels, int incl_policy, int[] capacity,int[] blocksize, int[] associativity,int address_length)
	{
		//super(number_of_levels, number_of_CPUs);
		main_controller=main_controllers.peekFirst();
		bus = buses.peekFirst();
		this.cpu_no=cpu_no;
		this.number_of_levels=number_of_levels;
		this.incl_policy=incl_policy;
		caches=new Cache[number_of_levels];
		this.address_length=address_length;
		this.capacity=capacity;
		this.blocksize=blocksize;
		this.associativity=associativity;
		misses=new LinkedList<Miss>();
		no_of_hits=new int[number_of_levels];
		no_of_misses=new int[number_of_levels];
		set_parameters();
		add_caches();
	}
	public void set_parameters()
	{
		//Invariant=Tag_l-(index_l+offset_l)=0
		int number_of_sets=0;

		offset_start=new int[number_of_levels];
		offset_stop=new int[number_of_levels];
		index_start=new int[number_of_levels];
		index_stop=new int[number_of_levels];
		tag_start=new int[number_of_levels];
		tag_stop=new int[number_of_levels];
		offset_length=new int[number_of_levels];
		index_length=new int[number_of_levels];

		for(int i=0;i<number_of_levels;i++)
		{
			number_of_sets=get_number_of_sets(capacity[i],blocksize[i],associativity[i]);

			offset_length[i]=set_offset_length(blocksize[i]);
			index_length[i]=set_index_length(number_of_sets);

			offset_start[i]=address_length-1;
			offset_stop[i]=offset_start[i]-offset_length[i];
			index_start[i]=offset_stop[i];
			index_stop[i]=index_start[i]-index_length[i];
			tag_start[i]=index_stop[i]-1;
			tag_stop[i]=0;//0; //Should be 0::
		}
	}

	public void add_caches()
	{
		for(int i=0;i<number_of_levels;i++)
		{
			caches[i]=new Cache(associativity[i], capacity[i], blocksize[i], i, false, 1);
		}
	}
	//Processor write. M-state
	public void write_slot(int cache_lvl,int index,boolean[] start_bin_addr,boolean[] tag,short[] start_hex_addr,Slot new_slot) //change for all caches, rewrite to slot
	{

		Slot slot;
		Miss miss;
		slot=caches[cache_lvl].write(new_slot,index, tag, start_bin_addr);

		if(slot!=null)
		{

			if(!slot.is_invalid())
			{	

				no_of_misses[cache_lvl]++;
				miss= new Miss("Write","Conflict/Capacity",2,(short)cache_lvl,new boolean[1],new short[1],(short) 0);
				misses.add(miss);

				if(cache_lvl+1<number_of_levels)
				{
					cache_lvl++;
					boolean[] old_tag = calc_tag(slot.get_bin_address(), cache_lvl);
					int old_index=calc_index_int(slot.get_bin_address(),cache_lvl);
					write_slot(cache_lvl,old_index,slot.get_bin_address(),old_tag,slot.get_hex_address(),slot);
				}
				else
				{
					short[] old_data= slot.get_all_data();

					if(slot.is_modified())
					{
						bus.write_to_memory(slot.get_hex_address(), slot.get_bin_address(),old_data);
					}

				}
			}
			else 
				no_of_hits[cache_lvl]++;
		}
		else
			no_of_hits[cache_lvl]++;
	}
	//No miss, write. On bus transactions
	public void write_slot_2(int cache_lvl,int index,boolean[] start_bin_addr,boolean[] tag,short[] start_hex_addr,Slot new_slot) //change for all caches, rewrite to slot
	{
		Slot slot;
		slot=caches[cache_lvl].write(new_slot,index, tag, start_bin_addr);

		if(slot!=null)
		{

			if(!slot.is_invalid())
			{	
				if(cache_lvl+1<number_of_levels)
				{
					cache_lvl++;
					boolean[] old_tag = calc_tag(slot.get_bin_address(), cache_lvl);
					int old_index=calc_index_int(slot.get_bin_address(),cache_lvl);
					write_slot_2(cache_lvl,old_index,slot.get_bin_address(),old_tag,slot.get_hex_address(),slot);
				}
				else
				{
					short[] old_data= slot.get_all_data();
					if(slot.is_modified())
					{
						bus.write_to_memory(slot.get_hex_address(), slot.get_bin_address(),old_data);
					}

				}
			}
		}
	}
	//Check if block is in cache and validity
	public LinkedList<Slot> read_slot(int cache_lvl,int length,boolean[] start_bin_addr,short[] start_hex_addr,boolean prefetch)
	{
		boolean[] tag;
		int index = 0;

		int i=0;
		Slot slot;
		short[] next_hex_addr=start_hex_addr;
		boolean[] next_bin_addr=start_bin_addr;
		LinkedList<Slot> slots= new LinkedList<Slot>();
		int start_offset;

		while(i<length)
		{
			start_offset=calc_offset_int(next_bin_addr,0);
			slot=get_slot(0,next_bin_addr);
			if(slot!=null)
			{
				if(!slot.is_invalid())
				{
					slots.add(slot);
					no_of_hits[0]++;
				}
				else
				{
					if(prefetch)
					{
						slot=update_slot_prefetch(length, next_bin_addr, next_hex_addr, next_hex_addr,true);
						slots.add(slot);
					}
					else{
						slot=update_slot(length, next_bin_addr, next_hex_addr, next_hex_addr,true);
						slots.add(slot);
					}
				}
			}
			else
			{

				if(prefetch)
				{

					slot=update_slot_prefetch(length, next_bin_addr, next_hex_addr, next_hex_addr,false);
					slots.add(slot);
				}
				else{


					slot=update_slot(length, next_bin_addr, next_hex_addr, next_hex_addr,false);
					slots.add(slot);
				}
			}
			for(int j=start_offset;j<blocksize[0];j++)
			{
				next_bin_addr=inc_bin_address(next_bin_addr);
				next_hex_addr=inc_hex_address(next_hex_addr);
				i++;
			}
		}
		return slots;

	}
	//On read miss in L1, checks n levels and/or peer caches
	public Slot update_slot(int length,boolean[] start_bin_addr,short[] start_hex_addr,short[] data, boolean invalid)
	{
		int level =-1;
		boolean[] tag;
		Slot slot=null;
		Slot valid_slot = null;
		LinkedList<Slot> slots=new LinkedList<Slot>();
		Entry entry;
		int offset;
		int index;
		Miss miss;
		if(invalid)
		{
			miss= new Miss("Read","Coherence",1,0,start_bin_addr,start_hex_addr,(short) 0);
			misses.add(miss);
			no_of_misses[0]++;
		}
		else
		{
			miss= new Miss("Read","Compulsory",0,0,start_bin_addr,start_hex_addr,(short) 0);
			misses.add(miss);
			no_of_misses[0]++;
		}
		if(number_of_levels>1)
		{
			if(incl_policy != 2)
			{
				for(int i=1;i<number_of_levels;i++)
				{
					//valid_slot=get_slot_check_entry(i,length,start_bin_addr);
					valid_slot=get_slot(i,start_bin_addr);
					if(valid_slot!=null)
					{
						if(!valid_slot.is_invalid())
						{	
							slots.add(valid_slot);

							no_of_hits[i]++;

							level=i;
							break;
						}

						else
						{
							miss= new Miss("Read","Coherence",1,(short)i,start_bin_addr,start_hex_addr,(short) 0);
							misses.add(miss);
							no_of_misses[i]++;
							break;

						}
					}
					else
					{
						miss= new Miss("Read","Compulsory",0,(short)i,start_bin_addr,start_hex_addr,(short) 0);
						misses.add(miss);
						no_of_misses[i]++;
					}
				}
			}
			else if(incl_policy==2 && !invalid)
			{
				for(int i=1;i<number_of_levels;i++)
				{
					valid_slot=get_slot(i,start_bin_addr);
					if(valid_slot!=null)
					{
						if(!valid_slot.is_invalid())
						{	
							slots.add(valid_slot);
							no_of_hits[i]++;
							level=i;
							break;
						}
						else
						{
							miss= new Miss("Read","Coherence",1,(short)i,start_bin_addr,start_hex_addr,(short) 0);
							misses.add(miss);
							no_of_misses[i]++;
							break;

						}

					}
					else
					{
						miss= new Miss("Read","Compulsory",0,(short)i,start_bin_addr,start_hex_addr,(short) 0);
						misses.add(miss);
						no_of_misses[i]++;
					}
				}
			}
		}
		if(slots.isEmpty()) // search other caches
		{	
			slots = bus.find_slots(start_bin_addr,length);
			
			for(Slot s:slots)
			{
				valid_slot=s;
				if(valid_slot.is_modified())
				{
					valid_slot.set_modified(false);
					valid_slot.set_dirty(false);
					valid_slot.set_shared(true);
					short[] temp= valid_slot.get_all_data();
					bus.write_to_memory(start_hex_addr, start_bin_addr,temp);

				}
				else if(valid_slot.is_exclusive())
				{
					valid_slot.set_exclusive(false);
					valid_slot.set_shared(true);

				}
				else if(valid_slot.is_forwarding())
				{
					valid_slot.set_forwarding(false);
					valid_slot.set_shared(true);
					bus.flush(valid_slot,start_bin_addr,start_hex_addr);
				}

			}
			if(slots.isEmpty()) //Slot not present at all in the system
			{
				short[] next_hex_addr;
				boolean[] next_bin_addr;
				int start_offset;
				int k=0;
				int l;

				if(incl_policy==0 || incl_policy==1)
				{
					for(int i=0;i<number_of_levels;i++)
					{
						start_offset = calc_offset_int(start_bin_addr,i);
						if((blocksize[i]-start_offset)>length) //gets the length of the data needed
						{
							l=length;
						}
						else
						{
							l=blocksize[i]-start_offset;
						}
						data=bus.read_from_memory_3(start_hex_addr,start_bin_addr,l);
						next_hex_addr=start_hex_addr;
						next_bin_addr=start_bin_addr;
						tag=calc_tag(start_bin_addr,i);
						index=calc_index_int(start_bin_addr,i);
						slot=new Slot(start_bin_addr,blocksize[i], tag,start_hex_addr);
						for(int j=0;j<data.length;j++)
						{

							entry=new Entry(data[j],next_hex_addr, next_bin_addr,tag);
							offset = calc_offset_int(next_bin_addr,i);
							slot.write(offset, entry);
							next_bin_addr=inc_bin_address(next_bin_addr);
							next_hex_addr=inc_hex_address(next_hex_addr);
						}
						slot.set_exclusive(true);

						write_slot_2(i, index, next_bin_addr, tag, next_hex_addr, slot);
					}
				}
				else
				{
					start_offset = calc_offset_int(start_bin_addr,0);
					if((blocksize[0]-start_offset)>length)
					{
						l=length;
					}
					else
					{
						l=blocksize[0]-start_offset;
					}

					data=bus.read_from_memory_3(start_hex_addr,start_bin_addr,l);
					next_hex_addr=start_hex_addr;
					next_bin_addr=start_bin_addr;
					tag=calc_tag(start_bin_addr,0);
					index=calc_index_int(start_bin_addr,0);
					slot=new Slot(start_bin_addr,blocksize[0], tag,start_hex_addr);
					for(int j=0;j<data.length;j++)
					{

						entry=new Entry(data[j],next_hex_addr, next_bin_addr,tag);
						offset = calc_offset_int(next_bin_addr,0);
						slot.write(offset, entry);
						next_bin_addr=inc_bin_address(next_bin_addr);
						next_hex_addr=inc_hex_address(next_hex_addr);
					}
					slot.set_exclusive(true);

					write_slot_2(0, index, next_bin_addr, tag, next_hex_addr, slot);
				}
			} //Block is in a peer cache
			else
			{
				short[] next_hex_addr=start_hex_addr;
				boolean[] next_bin_addr=start_bin_addr;

				if(incl_policy==0 || incl_policy==2)
				{
					for(int i =0;i<number_of_levels;i++)
						no_of_misses[i]--;

					tag=calc_tag(start_bin_addr,0);
					index=calc_index_int(start_bin_addr,0);
					slot=new Slot(start_bin_addr,blocksize[0], tag,start_hex_addr);
					for(int i=0;i<data.length;i++)
					{

						entry=new Entry(data[i],next_hex_addr, next_bin_addr,tag);
						offset = calc_offset_int(next_bin_addr,0);
						slot.write(offset, entry);
						next_bin_addr=inc_bin_address(next_bin_addr);
						next_hex_addr=inc_hex_address(next_hex_addr);
					}
					slot.set_forwarding(true);
					slot.set_shared(true);
					write_slot_2(0, index, next_bin_addr, tag, next_hex_addr, slot);
				}
				else
				{
					for(int j=0;j<number_of_levels;j++)
					{
						no_of_misses[j]--;
						tag=calc_tag(start_bin_addr,j);
						index=calc_index_int(start_bin_addr,j);
						slot=new Slot(start_bin_addr,blocksize[j], tag,start_hex_addr);
						for(int i=0;i<data.length;i++)
						{

							entry=new Entry(data[i],next_hex_addr, next_bin_addr,tag);
							offset = calc_offset_int(next_bin_addr,0);
							slot.write(offset, entry);
							next_bin_addr=inc_bin_address(next_bin_addr);
							next_hex_addr=inc_hex_address(next_hex_addr);
						}
						slot.set_forwarding(true);
						slot.set_shared(true);
						write_slot_2(j, index, next_bin_addr, tag, next_hex_addr, slot);
					}
				}
			}
		}
		else //Block is in a another level of the processor's cache
		{
			short[] next_hex_addr=start_hex_addr;
			boolean[] next_bin_addr=start_bin_addr;
			tag=calc_tag(start_bin_addr,0);
			index=calc_index_int(start_bin_addr,0);
			slot=new Slot(start_bin_addr,blocksize[0], tag,start_hex_addr);
			for(int i=0;i<data.length;i++)
			{

				entry=new Entry(data[i],next_hex_addr, next_bin_addr,tag);
				offset = calc_offset_int(next_bin_addr,0);
				slot.write(offset, entry);
				next_bin_addr=inc_bin_address(next_bin_addr);
				next_hex_addr=inc_hex_address(next_hex_addr);
			}
			for(Slot s:slots)
			{
				if(!s.is_invalid())
				{	
					valid_slot=s;
					if(valid_slot.is_modified())
					{
						slot.set_modified(true);
						slot.set_dirty(true);
					}
					else if(valid_slot.is_exclusive())
					{
						slot.set_exclusive(true);
					}
					else if(valid_slot.is_forwarding())
					{
						slot.set_forwarding(true);
						slot.set_shared(true);
					}
				}
			}
			if(incl_policy==0 || incl_policy==1)
			{
				write_slot_2(0, index, start_bin_addr, tag, start_hex_addr, slot);
			}
			else
			{
				swap(level,slot,index,start_bin_addr,tag, start_hex_addr);
			}
		}
		return slot;


	}
	//Initial read method
	public LinkedList<Entry> read(int cache_lvl,int length,boolean[] start_bin_addr,short[] start_hex_addr,boolean prefetch ) // add intermediate, read
	{
		LinkedList<Slot> slots=read_slot(cache_lvl, length, start_bin_addr, start_hex_addr,prefetch);
		LinkedList<Entry> list= new LinkedList <Entry>();
		Entry entry=null;
		short[] next_hex_addr=start_hex_addr;
		boolean[] next_bin_addr=start_bin_addr;
		int offset =calc_offset_int(next_bin_addr,cache_lvl);

		for(Slot s:slots)
		{
			offset =calc_offset_int(next_bin_addr,cache_lvl);

			for(int i=offset;i<blocksize[cache_lvl];i++) 
			{
				entry=s.get_entry(i);
				if(entry!=null)
					list.add(s.get_entry(i));
				else break;

				next_bin_addr=inc_bin_address(next_bin_addr);
				next_hex_addr=inc_hex_address(next_hex_addr);
				offset =calc_offset_int(next_bin_addr,cache_lvl);
			}

		}
		return list;
	}
	//Update on miss in L1, check n levels and other peer caches
	public Slot update_slot_prefetch(int length,boolean[] start_bin_addr,short[] start_hex_addr,short[] data, boolean invalid)
	{
		boolean[] tag;
		Slot slot=null;
		Slot valid_slot = null;
		LinkedList<Slot> slots=new LinkedList<Slot>();
		Entry entry;
		int offset;
		int index;
		int level =-1;
		Miss miss;
		if(invalid)
		{
			miss= new Miss("Read","Coherence",1,0,start_bin_addr,start_hex_addr,(short) 0);
			misses.add(miss);
			no_of_misses[0]++;
		}
		else
		{
			miss= new Miss("Read","Compulsory",0,0,start_bin_addr,start_hex_addr,(short) 0);
			misses.add(miss);
			no_of_misses[0]++;
		}
		if(number_of_levels>1)
		{
			if(incl_policy != 2)
			{
				for(int i=1;i<number_of_levels;i++)
				{
					//valid_slot=get_slot_check_entry(i,length,start_bin_addr);
					valid_slot=get_slot(i,start_bin_addr);
					if(valid_slot!=null)
					{
						if(!valid_slot.is_invalid())
						{	
							slots.add(valid_slot);
							no_of_hits[i]++;

							level=i;
							break;
						}
						else
						{
							miss= new Miss("Read","Coherency",1,(short)i,start_bin_addr,start_hex_addr,(short) 0);
							misses.add(miss);
							no_of_misses[i]++;
						}
					}
					else
					{
						miss= new Miss("Read","Compulsory",0,(short)i,start_bin_addr,start_hex_addr,(short) 0);
						misses.add(miss);
						no_of_misses[i]++;

					}
				}
			}
			else if(incl_policy==2 && !invalid)
			{
				for(int i=1;i<number_of_levels;i++)
				{
					//valid_slot=get_slot_check_entry(i,length,start_bin_addr);
					valid_slot=get_slot(i,start_bin_addr);
					if(valid_slot!=null)
					{
						if(!valid_slot.is_invalid())
						{	
							slots.add(valid_slot);
							no_of_hits[i]++;
							level=i;
							break;
						}
						else
						{
							miss= new Miss("Read","Coherence",1,(short)i,start_bin_addr,start_hex_addr,(short) 0);
							misses.add(miss);
							no_of_misses[i]++;
						}

					}
					else
					{
						miss= new Miss("Read","Compulsory",0,(short)i,start_bin_addr,start_hex_addr,(short) 0);
						misses.add(miss);
						no_of_misses[i]++;
					}
				}
			}
		}
		if(slots.isEmpty()) // search other peer caches if block not found in the processor's cache
		{	
			slots = bus.find_slots(start_bin_addr,length);


			for(Slot s:slots)
			{

				valid_slot=s;
				if(valid_slot.is_modified())
				{
					valid_slot.set_modified(false);
					valid_slot.set_dirty(false);
					valid_slot.set_shared(true);
					short[] temp= valid_slot.get_all_data();
					bus.write_to_memory(start_hex_addr, start_bin_addr,temp);

				}
				else if(valid_slot.is_exclusive())
				{
					valid_slot.set_exclusive(false);
					valid_slot.set_shared(true);

				}
				else if(valid_slot.is_forwarding())
				{
					valid_slot.set_forwarding(false);
					valid_slot.set_shared(true);
					bus.flush(valid_slot,start_bin_addr,start_hex_addr);

				}
			}
			if(slots.isEmpty()) //If not in a peer cache either
			{
				short[] next_hex_addr;
				boolean[] next_bin_addr;
				int start_offset;
				int k=0;

				if(incl_policy==0 || incl_policy==1)
				{
					for(int i=0;i<number_of_levels;i++)
					{
						start_offset = calc_offset_int(start_bin_addr,i);
						for(int j=offset_start[i];j>offset_stop[i];j--) //To maximize the block size, set address to be 0

						{
							if(start_bin_addr[j])
								start_bin_addr[j]=false;
						}
						for(int j=start_offset;j>-1;j--) //Same for hex rep of address
						{
							k=(start_hex_addr.length -1 ) - (j/256);
							start_hex_addr[k]--;
						}
						prefetch(i, blocksize[i], start_bin_addr, start_hex_addr); //Prefetch address corresponding to the next block
						data=bus.read_from_memory_3(start_hex_addr,start_bin_addr,blocksize[i]); //Fetch from main memory
						next_hex_addr=start_hex_addr;
						next_bin_addr=start_bin_addr;
						tag=calc_tag(start_bin_addr,i);
						index=calc_index_int(start_bin_addr,i);
						slot=new Slot(start_bin_addr,blocksize[i], tag,start_hex_addr);
						for(int j=0;j<data.length;j++)
						{

							entry=new Entry(data[j],next_hex_addr, next_bin_addr,tag);
							offset = calc_offset_int(next_bin_addr,i);
							slot.write(offset, entry);
							next_bin_addr=inc_bin_address(next_bin_addr);
							next_hex_addr=inc_hex_address(next_hex_addr);
						}
						slot.set_exclusive(true);

						write_slot_2(i, index, next_bin_addr, tag, next_hex_addr, slot);
					}
				}
				else
				{
					start_offset = calc_offset_int(start_bin_addr,0);
					for(int j=offset_start[0];j>offset_stop[0];j--)  //To maximize the block size, set address to be 0

					{
						if(start_bin_addr[j])
							start_bin_addr[j]=false;
					}
					for(int j=start_offset;j>-1;j--) //same as with bin_addr
					{
						k=(start_hex_addr.length -1 ) - (j/256);
						start_hex_addr[k]--;
					}
					prefetch(0, blocksize[0], start_bin_addr, start_hex_addr);
					data=bus.read_from_memory_3(start_hex_addr,start_bin_addr,blocksize[0]);
					next_hex_addr=start_hex_addr;
					next_bin_addr=start_bin_addr;
					tag=calc_tag(start_bin_addr,0);
					index=calc_index_int(start_bin_addr,0);
					slot=new Slot(start_bin_addr,blocksize[0], tag,start_hex_addr);
					for(int j=0;j<data.length;j++)
					{

						entry=new Entry(data[j],next_hex_addr, next_bin_addr,tag);
						offset = calc_offset_int(next_bin_addr,0);
						slot.write(offset, entry);
						next_bin_addr=inc_bin_address(next_bin_addr);
						next_hex_addr=inc_hex_address(next_hex_addr);
					}
					slot.set_exclusive(true);

					write_slot_2(0, index, next_bin_addr, tag, next_hex_addr, slot);
				}
			}
			else //Found in a peer cache, update State
			{
				short[] next_hex_addr=start_hex_addr;
				boolean[] next_bin_addr=start_bin_addr;

				if(incl_policy==0 || incl_policy==2)
				{
					no_of_misses[0]--;
					tag=calc_tag(start_bin_addr,0);
					index=calc_index_int(start_bin_addr,0);
					slot=new Slot(start_bin_addr,blocksize[0], tag,start_hex_addr);
					for(int i=0;i<data.length;i++)
					{

						entry=new Entry(data[i],next_hex_addr, next_bin_addr,tag);
						offset = calc_offset_int(next_bin_addr,0);
						slot.write(offset, entry);
						next_bin_addr=inc_bin_address(next_bin_addr);
						next_hex_addr=inc_hex_address(next_hex_addr);
					}
					slot.set_forwarding(true);
					slot.set_shared(true);
					write_slot_2(0, index, next_bin_addr, tag, next_hex_addr, slot);
				}
				else
				{
					for(int j=0;j<number_of_levels;j++)
					{
						no_of_misses[j]--;
						tag=calc_tag(start_bin_addr,j);
						index=calc_index_int(start_bin_addr,j);
						slot=new Slot(start_bin_addr,blocksize[j], tag,start_hex_addr);
						for(int i=0;i<data.length;i++)
						{

							entry=new Entry(data[i],next_hex_addr, next_bin_addr,tag);
							offset = calc_offset_int(next_bin_addr,0);
							slot.write(offset, entry);
							next_bin_addr=inc_bin_address(next_bin_addr);
							next_hex_addr=inc_hex_address(next_hex_addr);
						}
						slot.set_forwarding(true);
						slot.set_shared(true);
						write_slot_2(j, index, next_bin_addr, tag, next_hex_addr, slot);
					}
				}
			}
		}
		else //in another level of cache
		{
			short[] next_hex_addr=start_hex_addr;
			boolean[] next_bin_addr=start_bin_addr;
			tag=calc_tag(start_bin_addr,0);
			index=calc_index_int(start_bin_addr,0);
			slot=new Slot(start_bin_addr,blocksize[0], tag,start_hex_addr);
			for(int i=0;i<data.length;i++)
			{
				entry=new Entry(data[i],next_hex_addr, next_bin_addr,tag);
				offset = calc_offset_int(next_bin_addr,0);
				slot.write(offset, entry);
				next_bin_addr=inc_bin_address(next_bin_addr);
				next_hex_addr=inc_hex_address(next_hex_addr);
			}
			for(Slot s:slots)
			{
				if(!s.is_invalid())
				{	
					valid_slot=s;
					if(valid_slot.is_modified())
					{
						slot.set_modified(true);
						slot.set_dirty(true);
					}
					else if(valid_slot.is_exclusive())
					{
						slot.set_exclusive(true);
					}
					else if(valid_slot.is_forwarding())
					{
						slot.set_forwarding(true);
						slot.set_shared(true);
					}
				}
			}
			if(incl_policy==0 || incl_policy==1)
			{
				write_slot_2(0, index, start_bin_addr, tag, start_hex_addr, slot);
			}
			else
			{
				swap(level,slot,index,start_bin_addr,tag, start_hex_addr);
			}
		}
		return slot;
	}
	//prefetch next line/block
	private void prefetch(int cache_lvl,int length,boolean[] start_bin_addr,short[] start_hex_addr)
	{
		boolean[] bin_addr;
		short[] hex_addr;

		bin_addr=start_bin_addr;
		hex_addr=start_hex_addr;
		//move +1 block size
		for(int j=0;j<blocksize[cache_lvl];j++)
		{
			bin_addr=inc_bin_address(bin_addr);
			hex_addr=inc_hex_address(hex_addr);
		}
		short[] data=bus.read_from_memory_3(hex_addr,bin_addr,blocksize[cache_lvl]);
		boolean[] tag=calc_tag(bin_addr, cache_lvl);
		int index = calc_index_int(bin_addr,cache_lvl);
		Slot slot=new Slot(bin_addr,blocksize[cache_lvl], tag,hex_addr);
		Entry entry=null;
		for(int j=0;j<data.length;j++)
		{
			entry=new Entry(data[j],hex_addr, bin_addr,tag);
			slot.write(j, entry);
		}
		write_slot_2(cache_lvl, index, bin_addr, tag, data, slot);

	}
	//Exclusive swap
	private void swap(int level, Slot slot, int index, boolean[] start_bin_addr,boolean[] tag, short[] start_hex_addr) {

		Slot slot2 = caches[0].write(slot, index,tag, start_bin_addr);
		boolean[]tag2=calc_tag(start_bin_addr, level);
		int index2 = calc_index_int(start_bin_addr,level);

		if(slot2!=null)
		{

			caches[level].nullify_slot(index2,tag2);
			tag2 = calc_tag(slot2.get_bin_address(),level);
			index2= calc_index_int(slot2.get_bin_address(),level);
			slot2.set_tag(tag2);
			caches[level].write(slot2, index2,tag2, start_bin_addr);
		}
		else
		{
			caches[level].nullify_slot(index2,tag2);
		}
	}
	public Slot get_slot_check_entry(int cache_lvl,int data_length,boolean[] start_bin_addr) //returns slot if all entries are there
	{
		Entry entry=null;
		int start_offset=calc_offset_int(start_bin_addr,cache_lvl);
		int length=blocksize[cache_lvl]-start_offset;
		if(length>data_length)
		{
			length=data_length-1;
		}
		boolean[] next_bin_addr=start_bin_addr;
		Slot slot=get_slot(cache_lvl,start_bin_addr);
		if(slot!=null)
		{
			for(int i=start_offset;i<length;i++)
			{
				entry=slot.get_entry(i);
				if(entry==null){
					return null;
				}

				next_bin_addr=inc_bin_address(next_bin_addr);
			}
		}
		else return null;
		return slot;
	}
	public Slot get_slot(int cache_lvl, boolean[] start_bin_addr)
	{
		boolean[] tag = calc_tag(start_bin_addr, cache_lvl);
		int index=calc_index_int(start_bin_addr,cache_lvl);
		return caches[cache_lvl].get_slot(tag, index);
	}
	//Write methods for write modify
	public void write_slot_ME(boolean[] bin_addr, short[] hex_addr,short[] data)
	{
		Entry entry;
		LinkedList<Entry> list;

		int k=0;
		boolean[] next_bin_addr=bin_addr;
		short[] next_hex_addr=hex_addr;
		boolean[] tag=calc_tag( next_bin_addr,0);

		int	start_offset=calc_offset_int(next_bin_addr,0);
		int offset=0;

		for(int i=0;i<data.length;i=k)
		{
			list=new LinkedList<Entry>();
			bin_addr=next_bin_addr;
			hex_addr=next_hex_addr;
			for(int j=start_offset;j<blocksize[0];j++)
			{
				tag=calc_tag(next_bin_addr,0);
				entry=new Entry(data[i],next_hex_addr, next_bin_addr,tag);
				next_bin_addr=inc_bin_address(next_bin_addr);
				next_hex_addr=inc_hex_address(next_hex_addr);
				offset=calc_offset_int(next_bin_addr,0);
				list.add(entry);
				k++;
			}
			if(incl_policy==0 || incl_policy == 2)
				write_slot_M(bin_addr, hex_addr, list);
			else
				write_M_incl(bin_addr, hex_addr, list);

			next_bin_addr=inc_bin_address(next_bin_addr);
			next_hex_addr=inc_hex_address(next_hex_addr);
			start_offset=calc_offset_int(next_bin_addr,0);
		}
	}
	//Write modify
	public void write_slot_M(boolean[] bin_addr,short[] hex_addr,LinkedList<Entry> list)
	{
		Slot slot=null;

		int offset;
		boolean[] tag= calc_tag(bin_addr,0);
		int index=calc_index_int(bin_addr,0);
		slot= new Slot(bin_addr,blocksize[0],tag,hex_addr);
		slot.set_modified(true);
		slot.set_dirty(true);
		bus.invalidate_slot(bin_addr);
		for(Entry e:list)
		{
			offset=calc_offset_int(e.get_bin_address(),0);
			slot.write(offset,e);
		}
		write_slot(0, index, bin_addr, tag, hex_addr,slot);
	}
	//Inclusive cache L1 to Ln
	public void write_M_incl(boolean[] bin_addr,short[] hex_addr,LinkedList<Entry> list)
	{
		Slot slot=null;
		int index;
		int offset;
		boolean[] tag;
		bus.invalidate_slot(bin_addr);
		for(int i=0;i<number_of_levels;i++)
		{
			tag= calc_tag(bin_addr,i);
			index=calc_index_int(bin_addr,i);
			slot= new Slot(bin_addr,blocksize[i],tag,hex_addr);
			slot.set_modified(true);
			slot.set_dirty(true);

			for(Entry e:list)
			{
				offset=calc_offset_int(e.get_bin_address(),0);
				slot.write(offset,e);
			}
			write_slot(i, index, bin_addr, tag, hex_addr,slot);
		}
	}
	//Write bus-broadcasted slot to cache
	public void flush(boolean[] start_bin_addr, short[] start_hex_addr, Slot valid_slot) {

		Slot slot=null;
		Entry entry=null;
		short[] data = valid_slot.get_all_data();
		boolean[] tag;
		boolean[] bin_addr;
		short[] hex_addr;
		int index;
		int offset;
		if(incl_policy==1)
		{
			for(int i=0;i<number_of_levels;i++)
			{
				bin_addr=start_bin_addr;
				hex_addr=start_hex_addr;
				tag=calc_tag(bin_addr,i);
				index=calc_index_int(bin_addr,i);
				slot=new Slot(bin_addr,blocksize[i],tag,hex_addr);
				slot.set_shared(true);
				for(int j=0;j<data.length;j++)
				{
					offset=calc_offset_int(bin_addr,i);
					entry = new Entry(data[j], start_hex_addr, start_bin_addr, tag);
					slot.write(offset, entry);
				}
				write_slot_2(i, index, start_bin_addr, tag, start_hex_addr, slot);
			}
		}
		else
		{

			bin_addr=start_bin_addr;
			hex_addr=start_hex_addr;
			tag=calc_tag(bin_addr,0);
			index=calc_index_int(bin_addr,0);
			slot=new Slot(bin_addr,blocksize[0],tag,hex_addr);
			slot.set_shared(true);
			for(int j=0;j<data.length;j++)
			{
				offset=calc_offset_int(bin_addr,0);
				entry = new Entry(data[j], start_hex_addr, start_bin_addr, tag);
				slot.write(offset, entry);
			}
			write_slot_2(0, index, start_bin_addr, tag, start_hex_addr, slot);
		}
	}
	//Write exclusive
	public void write_slot_E(int cache_lvl,boolean[] bin_addr,short[] hex_addr,LinkedList<Entry> list)
	{
		boolean[] tag=calc_tag(bin_addr,cache_lvl);
		int index=calc_index_int(bin_addr,cache_lvl);
		int offset;
		Slot slot=null;

		//Entry entry=fetch_entry(cache_lvl, bin_addr);
		slot= new Slot(bin_addr,blocksize[cache_lvl],tag,hex_addr);
		slot.set_exclusive(true);
		for(Entry e:list)
		{
			offset=calc_offset_int(e.get_bin_address(),cache_lvl);
			slot.write(offset,e);
		}
		write_slot(cache_lvl,index,bin_addr,tag,hex_addr, slot);
	}
	
	private int get_number_of_sets(int capacity, int blocksize, int associativity) {
		int number_of_sets=capacity /(blocksize*associativity);
		return number_of_sets;
	}
	public int set_index_length(int number_of_sets) {

		double log2 = Math.log(2);
		double length = Math.log((number_of_sets))/log2;
		return (int)length;
	}
	private int set_offset_length(int blocksize) {

		double log2 = Math.log(2);
		double length = Math.log((blocksize))/log2;
		return (int)length;	
	}
	public int get_number_of_blocks(int cache_lvl, int CPU_no) {
		return caches[cache_lvl].get_number_of_blocks();

	}
	public long prefetch(int cache_lvl,int CPU_no,int addr,int way)
	{
		return caches[cache_lvl].prefetch(addr, way);
	}
	public void add_caches(int assoc, int c_size, int b_size, int level, boolean shared, int bits) {

		for(int i=0;i<number_of_CPUs;i++)
			caches[i]=new Cache(assoc, c_size, b_size, level, shared, bits);

	}	
	public HashMap<String, Slot> get_slots(int cache_lvl, int index)
	{
		return caches[cache_lvl].get_slots(index);
	}
	public int get_cpu_no()
	{
		return cpu_no;
	}
	public LinkedList<Miss> get_misses() {

		return misses;
	}
	public int[] get_no_of_hits()
	{
		return no_of_hits;
	}
	public int[] get_no_of_misses()
	{
		return no_of_misses;
	}
	public void invalidate_slot(boolean[] address) {

		if(incl_policy!=2)
		{
			for(int i=0;i<number_of_levels;i++)
			{
				boolean[] tag=calc_tag(address,i);
				int index=calc_index_int(address,i);
				Slot slot=caches[i].get_slot(tag, index);
				if(slot!=null)
					slot.set_invalid(true);
			}
		}
		else
		{
			for(int i=0;i<number_of_levels;i++)
			{
				boolean[] tag=calc_tag(address,i);
				int index=calc_index_int(address,i);
				Slot slot=caches[i].get_slot(tag, index);
				if(slot!=null)
				{
					slot.set_invalid(true);
					return;
				}
			}
		}

	}
	//Not used, since F can be F, but not S
	public LinkedList<Slot> change_F(boolean[] bin_addr,LinkedList<Slot> list) {

		int index;
		boolean[] tag;
		Slot slot;

		for(int i=0;i<number_of_levels;i++)
		{
			index=calc_index_int(bin_addr,i);
			tag=calc_tag(bin_addr,i);
			slot=caches[i].get_slot(tag, index);
			if(slot!=null)
			{
				if(incl_policy==2)
				{
					if(slot.is_shared())
					{
						list.add(slot);
						return list;
					}
				}
				else
				{
					if(slot.is_shared())
					{
						list.add(slot);

					}
				}
			}
		}
		return list;
	}
	public LinkedList<Slot> change_E(boolean[] bin_addr, LinkedList<Slot> list) {
		int index;
		boolean[] tag;
		Slot slot;
		for(int i=0;i<number_of_levels;i++)
		{
			index=calc_index_int(bin_addr,i);
			tag=calc_tag(bin_addr,i);
			slot=caches[i].get_slot(tag, index);
			if(slot!=null)
			{
				if(incl_policy==2)
				{
					if(slot.is_shared())
					{
						list.add(slot);
						return list;
					}
				}
				else
				{
					if(slot.is_shared())
					{
						list.add(slot);

					}
				}
			}
		}
		return list;
	}
	private short[] inc_hex_address(short[] next_hex_address) {

		int hex_address_length=next_hex_address.length;
		short[] address=new short[hex_address_length];
		for(int i=0;i<hex_address_length;i++)
		{
			address[i]=next_hex_address[i];
		}
		for(int i=hex_address_length-1;i>-1;i--)
		{
			if(address[i]==0xFF)
			{
				address[i]=0x00;
			}
			else
			{
				address[i]++;
				return address;
			}
		}
		return address;
	}
	private boolean[] inc_bin_address(boolean[] next_bin_address) {

		int bin_address_length=next_bin_address.length;
		boolean[] address=new boolean[bin_address_length];
		for(int i=0;i<bin_address_length;i++)
		{
			address[i]=next_bin_address[i];
		}
		for(int i=bin_address_length-1;i>-1;i--)
		{
			if(address[i])
			{
				address[i]=false;
			}
			else
			{
				address[i]=true;
				return address;
			}
		}
		return address;
	}
	public int calc_index(int cache_lvl,boolean[] address)
	{
		int start=index_start[cache_lvl];
		int stop=index_stop[cache_lvl];
		int index=0;
		for(int i=start;i>stop;i--)
		{
			if(address[i])
				index+=Math.pow(2,start-i);
		}
		return index;
	}
	public int calc_offset(int cache_lvl,boolean[] address)
	{
		int start=offset_start[cache_lvl];
		int stop=offset_stop[cache_lvl];
		int offset=0;
		for(int i=start;i>stop;i--)
		{
			if(address[i])
				offset+=Math.pow(2,start-i);
		}
		return offset;
	}
	public boolean[] calc_tag(boolean[] current_address,int level)
	{
		int k=tag_start[level];
		int l=tag_stop[level]; //Should be -1;
		boolean[] tag=new boolean[k-l] ; //k-l=k--1=k+1;
		for(int i=0;i<tag.length;i++)
		{
			tag[i]=current_address[i];
			k--;
		}
		return tag;

	}
	public boolean[] calc_index(boolean[] current_address,int level) {

		int k=index_start[level];
		int l=index_length[level];
		boolean[] index=new boolean[k-l];

		for(int i=0;i<index.length;i++)
		{
			index[i]=current_address[k];
			k--;
		}
		return index;

	}

	public boolean[] calc_offset(boolean[] current_address, int level) {

		boolean[] offset=new boolean[offset_start[level]-offset_length[level]] ;
		for(int i=0;i<offset.length;i++)
			offset[i]=current_address[i];
		return offset;
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
	private int calc_index_int(boolean[] bin_address, int cache_lvl) 
	{

		int stop=index_stop[cache_lvl];
		int start=index_start[cache_lvl];

		int index=0;
		for(int i=stop+1;i<start+1;i++)
		{
			if(bin_address[i])
			{
				index+=(Math.pow(2,start-i));
			}
		}
		return index;
	}
	private int calc_offset_int(boolean[] bin_address, int cache_lvl) {

		int stop=offset_stop[cache_lvl];
		int start=offset_start[cache_lvl];

		int offset=0;
		for(int i=stop+1;i<start+1;i++)
		{
			if(bin_address[i])
			{
				offset+=(Math.pow(2,start-i));
			}
		}
		return offset;
	}
	public void clear() {
		
		
		caches=null;
		
		
	}
}









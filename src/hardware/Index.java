package hardware;

import java.util.HashMap;
import java.util.LinkedList;

public class Index {

	private HashMap<String,Slot> slots;
	private LinkedList<Slot> lru_queue; //Doubly Linked List
	private int associativity;
	
	private int blocksize;

	
	public Index(int associativity,int blocksize)
	{

		this.associativity=associativity;
		this.blocksize=blocksize;
		slots=new HashMap<String,Slot>();
	
		lru_queue = new LinkedList<Slot>();

	}
	/* Index of a cache set. Contains methods for read, write, invalidate and LRU */
	public Index(boolean[] get_bin_address, int associativity, int blocksize) {
		
		this.associativity=associativity;
		this.blocksize=blocksize;
		slots=new HashMap<String,Slot>();
	
		lru_queue = new LinkedList<Slot>();
	}
	public HashMap<String,Slot> ret_slots()
	{
		return slots;
	}
	//Most recently used, added first in queue
	public Slot get_slot(boolean[] tag) {

		String txt_tag=tag_to_string(tag);
		Slot slot;
		if(slots.containsKey(txt_tag))
		{
			slot=slots.get(txt_tag);
			lru_queue.remove(slot);
			lru_queue.addFirst(slot);
			return slot;
		}
			return null;
	}
	//Most recently used, added first in queue
	public Slot write(boolean[] tag,Slot new_slot)
	{

		String txt_tag = new_slot.get_txt_tag();
		
		if(slots.containsKey(txt_tag)) //Contains key, hit
		{
			Slot old_slot=slots.get(txt_tag);
			slots.put(txt_tag,new_slot);
			lru_queue.remove(old_slot);
			lru_queue.addFirst(new_slot);
			return null;
		}
		else
		{
			if(slots.size()<associativity) //Null, space, hit
			{
				slots.put(txt_tag,new_slot);
				lru_queue.addFirst(new_slot);
				return null;
			
			}
			else 
				return lru(new_slot,txt_tag); //No space, miss
		}	
	}
	//New slot first in LRU-queue
	private Slot lru(Slot new_slot,String txt_tag) {

		Slot slot = null;
		slot = lru_queue.removeLast();
		slots.remove(slot.get_txt_tag());
		slots.put(txt_tag,new_slot);
		lru_queue.addFirst(new_slot);
		return slot;



	}
	public void nullify_slot(boolean tag[])
	{
		String t = tag_to_string(tag);
		Slot slot;
		if(slots.containsKey(t))
		{
			slot=slots.get(t);
			lru_queue.remove(slot);
			slots.remove(t);
		}
	}
	public String tag_to_string(boolean[] t)
	{
		String string="";
		for(int i=0;i<t.length;i++)
		{
			if(t[i])
				string+="1";
			else
				string+="0";
		}
		return string;
	}
	


}

package hardware;


public class Slot {

	private int associativity;
	
	private Slot[] next_slot;
	private boolean[] bin_address;
	
	private boolean dirty;
	private boolean[] tag;

	private Offset[] offsets;
	private boolean forwarding;
	private boolean owned;
	private boolean shared;
	private boolean invalid;
	private boolean exclusive;
	private boolean modified;

	private String txt_tag;
	private int blocksize;
	private short[] hex_addr;
	
	/*Represents a cache block, the block consist of n offset elements */
	public Slot(boolean[] bin_address, int blocksize, boolean[] tag, short[] hex_addr) 
	{
		this.bin_address=bin_address;
		this.hex_addr=hex_addr;
		this.blocksize=blocksize;
		this.offsets=new Offset[blocksize];
		this.tag=tag;
		txt_tag=tag_to_string(tag);
	}
	public Slot next_slot_bit(int i)
	{
		return next_slot[i];
	}

	public void write(int offset, Entry entry)
	{
		Offset off=offsets[offset];
		if(off==null)
		{
			offsets[offset]=new Offset();
			off=offsets[offset];
		}
		offsets[offset].write(entry);
		set_tag(entry.get_bin_address(),entry.get_tag().length);

	}
	public Entry get_entry(int offset)
	{
		Offset off=offsets[offset];
		if(off!=null)
			return off.read();
		else return null;
	}

	public void set_tag(boolean[] addr, int length) {

		tag=new boolean[length];
		for(int i=0;i<length;i++)
		{
			tag[i]=addr[i];
		}
		
	}
	public int get_number_of_entries() {
		return 0;
	}
	public boolean is_modified() {
		return modified;
	}

	public void set_modified(boolean modified) {
		this.modified = modified;
	}

	public boolean is_exclusive() {
		return exclusive;
	}

	public void set_exclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}

	public boolean is_invalid() {
		return invalid;
	}

	public void set_invalid(boolean invalid) {
		this.invalid = invalid;
	}

	public boolean is_shared() {
		return shared;
	}

	public void set_shared(boolean shared) {
		this.shared = shared;
	}

	public boolean is_owned() {
		return owned;
	}

	public void set_owned(boolean owned) {
		this.owned = owned;
	}

	public boolean is_dirty() {
		return dirty;
	}

	public void set_dirty(boolean dirty) {
		this.dirty = dirty;
	}
	public boolean[] get_bin_address()
	{
		return bin_address;
	}
	public boolean is_forwarding() {
		return forwarding;
	}
	public void set_forwarding(boolean forwarding)
	{
		this.forwarding=forwarding;
	}
	
	public boolean[] get_tag()
	{
		return tag;
	}
	public boolean tag_compare(boolean[] new_tag)
	{
		for(int i=0;i<tag.length;i++)
		{
			if(tag[i]!=new_tag[i])
				return false;
		}
		return true;
	}
	public short[] get_all_data() {

		short[] data=new short[offsets.length];
		Entry entry;
		for(int i=0;i<offsets.length;i++)
		{
			if(offsets[i]!=null)
			{
				entry=offsets[i].read();
				if(entry!=null)
					data[i]=entry.get_data();
			}
			else
			{
				data[i]=-1;			}
			
		}
		return data;
	}
	public int get_associativity() {
		return associativity;
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
	public void set_tag(boolean[] tag)
	{
		txt_tag=tag_to_string(tag);
	}
	public String get_txt_tag() {
		return txt_tag;
	}
	public short[] get_hex_address() {
		return hex_addr;
	}

}

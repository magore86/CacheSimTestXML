package application;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Configs {

	private IntegerProperty lvl;
	private IntegerProperty cap;
	private IntegerProperty blk;
	private IntegerProperty assoc;

	public Configs(int lvl,int cap,int blk, int assoc)
	{
		this.lvl = new SimpleIntegerProperty(lvl);
		this.cap =  new SimpleIntegerProperty(cap);
		this.blk =  new SimpleIntegerProperty(blk);
		this.assoc =  new SimpleIntegerProperty(assoc);

	}

	public int get_level() {
		return lvl.get();
	}

	public void set_level(int lvl) {
		this.lvl.set(lvl);
	}

	public IntegerProperty lvl_property() {
		return lvl;
	}

	public int get_capacity() {
		return cap.get();
	}

	public void set_capacity(int lvl) {
		this.cap.set(lvl);
	}

	public IntegerProperty cap_property() {
		return cap;
	}
	public int get_blocksize() {
		return blk.get();
	}

	public void set_blocksize(int lvl) {
		this.blk.set(lvl);
	}

	public IntegerProperty blk_property() {
		return blk;
	}
	public int get_associativity() {
		return assoc.get();
	}

	public void set_associativity(int lvl) {
		this.assoc.set(lvl);
	}

	public IntegerProperty assoc_property() {
		return assoc;
	}
}




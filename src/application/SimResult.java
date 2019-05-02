package application;

import java.util.LinkedList;

import controller.PerformanceMetric;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class SimResult {
	private IntegerProperty hits;
	private IntegerProperty misses;
	private DoubleProperty ratio;
	private LinkedList<PerformanceMetric> perfs;
	

	public SimResult(int hits,int misses,double ratio)
	{
		this.hits = new SimpleIntegerProperty(hits);
		this.misses =  new SimpleIntegerProperty(misses);
		this.ratio =  new SimpleDoubleProperty(ratio);
		
	}

	public int get_hits() {
		return hits.get();
	}

	public void set_hits(int hits) {
		this.hits.set(hits);
	}

	public IntegerProperty hits_property() {
		return hits;
	}

	public int get_misses() {
		return misses.get();
	}

	public void set_misses(int misses) {
		this.misses.set(misses);
	}

	public IntegerProperty misses_property() {
		return misses;
	}
	public double get_ratio() {
		return ratio.get();
	}

	public void set_ratio(int ratio) {
		this.ratio.set(ratio);
	}

	public DoubleProperty ratio_property() {
		return ratio;
	}

	
}



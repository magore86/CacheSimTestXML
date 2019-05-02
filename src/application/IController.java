package application;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public interface IController {
	
	public ObservableList<Configs> al_configs=FXCollections.observableArrayList();
	public ArrayList<Integer> number_of_CPUs=new ArrayList<Integer>();

}

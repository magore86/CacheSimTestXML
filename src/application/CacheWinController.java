package application;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CacheWinController extends Controller implements IController {
	private boolean fully_associative;
	
	@FXML
	private TitledPane cache_pane;
	@FXML
	private Button cont;
	@FXML
	private TextField cap;
	@FXML
	private TextField blk;
	@FXML
	private TextField assoc;
	@FXML
	private CheckBox fully_assoc;
	@FXML
	private TextArea instr;
	
	private int lvl;
	
	@FXML
	private void initialize()
	{
		instr.setEditable(true);
		instr.appendText("Please enter values for capacity: From 0 to 12");
		instr.appendText("\nPlease enter values for block size: From 0 to k=log_2(capacity)");
		instr.appendText("\nPlease enter values for assiativity: From 1. Associativity*Blocksize<=Capacity!");
		for(int i=0;i<12;i++)
			instr.appendText("\n2^" + i +" = " + (int)Math.pow(2,i) + "B: K="+i);
		
		instr.appendText("\n\n\nNote: Capacity is multiplied by 1024B, thus it is KB!");
		instr.setEditable(false);
		
		
	}
	
	@FXML
	protected void init_assoc(ActionEvent event) throws Exception
	{
		if((fully_assoc.isSelected()))
		{
			fully_associative=true;
			assoc.setEditable(false);
		}
		else
		{
			fully_associative=false;
			assoc.setEditable(true);
		}
		
	}
	@FXML
	protected void input_checker_digit(ActionEvent event) throws Exception
	{
		ObservableList<Configs> al=FXCollections.observableArrayList();
		boolean is_ok1=false;
		boolean is_ok2=false;
		boolean is_ok3=false;
		int capacity = 0;
		int block_size = 0;
		int associativity = 0;
		int k = 0;
		try 
		{
			capacity=Integer.parseInt(cap.getText());
			k=capacity;
			if(k>-1 && k < 12)
			{
				capacity=(int)Math.pow(2,capacity);
				capacity*=1024;
				is_ok1=true;
			}
			else
			{
				cap.setText("");
			}
			
			
			
		}
		catch(NumberFormatException nfe)
		{
			cap.setText("");
		}
		try 
		{
			block_size=Integer.parseInt(blk.getText());
			if(block_size<=(Math.log(capacity)/Math.log(2)) && block_size>-1)
			{
				block_size=(int) Math.pow(2, block_size);
				is_ok2=true;
			}
			else
			{
				blk.setText("");
			}
			
		}
		catch(NumberFormatException nfe)
		{
			blk.setText("");
		}
		try 
		{
			if(fully_associative==true)
			{
				associativity=capacity/block_size;
				is_ok3=true;
			}
			else
			{
				associativity=Integer.parseInt(assoc.getText());
				try
				{
					
					if(associativity*block_size<=capacity)
					{
						is_ok3=true;
					}
					else
					{
						assoc.setText("");
					}
				}
				catch(Exception e)
				{
					assoc.setText("");
				}
				
			}
		}
		catch(NumberFormatException nfe)
		{
			assoc.setText("");
		}
		if(is_ok1 && is_ok2 && is_ok3)
		{
			
			lvl=al_configs.size() +1;
			Configs c= new Configs(lvl,capacity,block_size,associativity);
			to_array(c);
			
			Stage stage = (Stage)cont.getScene().getWindow();
			stage.close();
			
		}
		else
		{
			Alert alert=new Alert(AlertType.ERROR,"Please insert symbols@[0-9]\n"
					+ "Capacity must be between 0 and 12 (2^K) and block size must be smaller or equal to K in capacity, but larger than -1"
					+ "\nAssociativity*blocksize cannot be greater than capacity");
			alert.show();
		}
		
		
	}
	private void to_array(Configs c) throws IOException 
	{
		al_configs.add(c);
		
		
		
		
	}
	
	
}

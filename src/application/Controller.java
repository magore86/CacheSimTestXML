package application;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import MSExcel.msex.XcelWriter;
import controller.BaseController;
import controller.PerformanceMetric;
import javafx.*;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Controller implements IController  {

	//Layout
	@FXML
	private MenuBar add_cpu;
	@FXML
	private MenuItem add;
	@FXML
	private Button start;
	@FXML
	private TextField path;
	@FXML
	private CheckBox mode;
	@FXML
	private ChoiceBox<String> incl_pol;
	@FXML
	private TextField n_sims;
	@FXML
	private TextArea perf;
	@FXML
	private CheckBox prefetch;
	@FXML
	private CheckBox enable_mm;
	@FXML
	private CheckBox reg_share;
	@FXML
	private TableView<SimResult> table_results;
	@FXML
	private TableColumn<SimResult,Number> miss_col;
	@FXML
	private TableColumn<SimResult,Number> hit_col;
	@FXML
	private TableColumn<SimResult,Number> ratio_col;
	@FXML
	private BarChart<String,Double> graph_results;
	@FXML
	private CategoryAxis x_axis;
	@FXML
	private NumberAxis y_axis;

	//helpers
	private TableView<Integer> tblv_cpu;
	private TableColumn<Integer, String> tc_level;
	private Pane pane;

	private int index;
	protected int no_of_CPUs;
	//protected ObservableList<Configs> al_configs;
	private boolean mode32enabled;
	private int[] associativity;
	private int[] capacity;
	private int[] blocksize;
	private int address_length;
	private ObservableList<ArrayList<SimResult>> sim_results;
	private int n_times;
	private int incl_policy;
	private ArrayList<PerformanceMetric> perf_metrics;
	private BaseController base_controller;


	@FXML
	private void initialize()
	{
		no_of_CPUs=0;
		incl_policy=0;
		sim_results=FXCollections.observableArrayList();
		ObservableList<String> cb =FXCollections.observableArrayList("NINE","Inclusive", "Exclusive");
		if(incl_pol!=null)
			incl_pol.setItems(cb);

		
		//init_graph();
	}

	@FXML
	protected void handle_add_cpu(ActionEvent event) throws Exception
	{
		Parent root = FXMLLoader.load(getClass().getResource("AddCPU.fxml"));
		Stage CPU_stage = new Stage();

		CPU_stage.initModality(Modality.APPLICATION_MODAL);
		CPU_stage.setScene(new Scene(root, 800, 225));
		CPU_stage.showAndWait();





	}

	@FXML
	protected void add_cache_element(ActionEvent event) throws Exception
	{
		Parent root = FXMLLoader.load(getClass().getResource("AddCache.fxml"));
		Stage cache_stage = new Stage();

		cache_stage.setScene(new Scene(root, 800, 225));
		cache_stage.show();

	}
	@FXML
	protected void open_file(ActionEvent event) throws IOException
	{

		FileChooser fc = new FileChooser();
		File file=null;
		fc.getExtensionFilters().add(new ExtensionFilter("Executable" ,"*.exe"));

		file = fc.showOpenDialog(null);
		if(file!=null)
			path.setText(file.getAbsolutePath());
		else
			path.setText("No path");


	}
	@FXML
	public void mode32enable()
	{
		if(mode.isSelected())
			mode32enabled=true;
		else mode32enabled=false;
	}
	@FXML
	public void start_sim()
	{
		sim_results.clear();
		int length=al_configs.size();
		String filepath=path.getText();
		if(length>0)
		{
			associativity=new int[length];
			capacity = new int[length];
			blocksize = new int[length];
			for(int i=0;i<length;i++)
			{
				associativity[i]=al_configs.get(i).get_associativity();
				capacity[i]=al_configs.get(i).get_capacity();
				blocksize[i]=al_configs.get(i).get_blocksize();
			}
		}
		if(mode32enabled)
		{
			address_length=32;
		}
		else
		{
			address_length=64;
		}


		try
		{
			n_times=Integer.parseInt(n_sims.getText());
			if(n_times>0 && n_times<11)
			{
				no_of_CPUs=number_of_CPUs.get(0);
				if(no_of_CPUs<1024)
				{
					ReadOnlyIntegerProperty temp=incl_pol.getSelectionModel().selectedIndexProperty();
					int value=temp.intValue();
					if(value > -1)
					{
						incl_policy=value;
					}

					if(filepath!="")
					{
						perf_metrics=new ArrayList<PerformanceMetric>();
					try 
						{	


							for(int i=0;i<n_times;i++)
							{
								base_controller=new BaseController();
								base_controller.init_controllers(!reg_share.isSelected(),enable_mm.isSelected(),prefetch.isSelected(),al_configs.size(),
										no_of_CPUs,capacity,blocksize,associativity,
										address_length,incl_policy,filepath);

								perf_metrics.add(base_controller.run());
								sim_results.add(perf_metrics.get(i).get_results());
								base_controller.clear();
								base_controller=null;
								System.gc();



							}
							
							write_perf();
							to_table();
							to_graph();
						
						}
						catch(Exception e)
						{
							Alert alert = new Alert(AlertType.ERROR, "Ooops,something went wrong!\nSimulation unable to finish");
							alert.show();

						}
					}
					else
					{
						Alert alert = new Alert(AlertType.INFORMATION, "Please fill out all parameters!\nNumber of simulations is not entered");
						alert.show();
					}
				}
				else
				{
					Alert alert = new Alert(AlertType.INFORMATION, "Number of CPUs must be less than 1024.\nDue to memory contraints!");
					alert.show();
				}
			}
			else
			{
				Alert alert = new Alert(AlertType.INFORMATION, "Number of simulations must be less than 11(but more than 0).\nDue to memory contraints!");
				alert.show();
			}

		}
		catch(NumberFormatException nfe)
		{
			Alert alert = new Alert(AlertType.ERROR, "Please enter symbols@[0-9]");
			alert.show();
		}
	}


	public void write_perf()
	{
		int k=0;
		perf.clear();
		for(PerformanceMetric pm: perf_metrics)
		{
			perf.appendText("" + k + ".\n"+ pm.perf_toString() + "\n");
			k++;

		}
	}
	private void to_table() {

		table_results.getItems().clear();
		ObservableList<SimResult> temp=FXCollections.observableArrayList();
		for(int i=0;i<sim_results.size();i++)
		{	
			temp.addAll(sim_results.get(i));

			if(!temp.isEmpty())
			{
				table_results.setEditable(true);

				hit_col.setCellValueFactory(hits -> hits.getValue().hits_property());
				miss_col.setCellValueFactory(misses -> misses.getValue().misses_property());
				ratio_col.setCellValueFactory(ratio -> ratio.getValue().ratio_property());

				table_results.setItems(temp);


			}

		}





	}

	private void to_graph() {

		graph_results.getData().clear();
		ArrayList<SimResult> temp = null;
		graph_results.setTitle("Ratio");
		ArrayList<Series<String,Double>> series = new ArrayList<Series<String,Double>>();
		ObservableList<String> categ=FXCollections.observableArrayList();
		Series<String,Double> serie=null;
		Double ratio=0.00;
		int level=0;
		y_axis.setAutoRanging(false);
		y_axis.setLowerBound(0);
		y_axis.setUpperBound(100);
		y_axis.setTickUnit(10);
		for(int i=0;i<al_configs.size();i++)
		{
			serie=new Series<String,Double>();
			level=i+1;
			serie.setName("L" + level);
			series.add(serie);


		}

		String cat="";
		for(int i=0;i<sim_results.size();i++)
		{
			temp=sim_results.get(i);
			cat="Sim" + i;
			categ.add(cat);
			for(int j= 0;j<temp.size();j++)
			{

				ratio=temp.get(j).get_ratio();
				serie=series.get(j);
				serie.getData().add(new Data<String,Double>(cat,ratio));

			}
		}
		x_axis.setCategories(categ);
		for(int i= 0; i<al_configs.size();i++)
		{
			serie=series.get(i);
			graph_results.getData().add(serie);
		}

		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		ArrayList<String> al=null;
		int j = 0;
		for(ArrayList<SimResult> ob_al_sr : sim_results)
		{

			for(int i=0;i<ob_al_sr.size();i++)
			{
				al = new ArrayList<String>();
				al.add("L" + al_configs.get(i).get_level());
				al.add("" + al_configs.get(i).get_capacity());
				al.add("" + al_configs.get(i).get_blocksize());
				al.add("" + al_configs.get(i).get_associativity());
				al.add("" +ob_al_sr.get(i).get_hits());
				al.add("" +ob_al_sr.get(i).get_misses());
				al.add("" +ob_al_sr.get(i).get_ratio());
				data.add(al);
			}

		}
		XcelWriter xw = new XcelWriter("Results");
		xw.add_data(data);
		xw.write("Results");

	}



}





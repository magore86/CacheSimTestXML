package application;

import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.Cell;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

public class CPUWinController extends Controller implements IController {


	@FXML
	private Pane table_pane;
	@FXML
	private TitledPane cpu_container;
	@FXML
	private TableView<Configs> tblv;
	@FXML
	private TableColumn<Configs,Number> lvl;
	@FXML
	private TableColumn<Configs,Number> cap;
	@FXML
	private TableColumn<Configs,Number> blk;
	@FXML
	private TableColumn<Configs,Number> assoc;
	@FXML
	private CheckBox edit_cache;
	@FXML
	private TextField cpu_number;
	@FXML
	private Button ok_cpu;

	@FXML
	private void initialize()
	{

		table_insert();
	}




	@FXML
	protected void handle_add_cache(ActionEvent event) throws Exception
	{
		if(al_configs.size()<2)
		{
			Parent root = FXMLLoader.load(getClass().getResource("AddCache.fxml"));
			Stage cache_stage = new Stage();
			cache_stage.initModality(Modality.APPLICATION_MODAL);
			cache_stage.setScene(new Scene(root, 900,500));

			cache_stage.showAndWait();
			table_insert();
		}
		else
		{
			Alert alert = new Alert(AlertType.INFORMATION,"Cannot add more caches. Maximum is 2!");
			alert.show();
		}

	}@FXML
	protected void remove_caches(ActionEvent event) throws Exception
	{
		al_configs.clear();
	}

	public void table_insert() 
	{
		ObservableList<Configs> temp=al_configs;
		if(!temp.isEmpty())
		{
			tblv.setEditable(true);

			lvl.setCellValueFactory(lvl -> lvl.getValue().lvl_property());
			cap.setCellValueFactory(cap -> cap.getValue().cap_property());
			blk.setCellValueFactory(blk -> blk.getValue().blk_property());
			assoc.setCellValueFactory(assoc -> assoc.getValue().assoc_property());
			tblv.setItems(temp);


		}

	}
	/*Due to time constraints, Edit is not included*/
	@FXML
	public void set_edit_table()
	{

		if(edit_cache.isSelected())
		{
			try {

				cap.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter() {
				}));
				blk.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter() {
				}));
				assoc.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter() {
				}));
				cap.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Configs,Number>>() {

					@Override
					public void handle(CellEditEvent<Configs, Number> t) {

						int max=(int)((Math.pow(2,11))*1024);
						int value=(int)Math.pow(2,t.getNewValue().intValue());
						int pos=t.getTablePosition().getRow();
						int k=(int)(Math.log(al_configs.get(pos).get_capacity()/Math.log(2)));
						if(value<=max)
						{
							((Configs) t.getTableView().getItems().get(
									t.getTablePosition().getRow())
									).set_capacity( Integer.parseInt("" +value ));
						}
						else
						{
							t.getTableView().refresh();
						}

					}
				});

				blk.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Configs,Number>>() {

					@Override
					public void handle(CellEditEvent<Configs, Number> t) {

						int value=t.getNewValue().intValue();
						int pos=t.getTablePosition().getRow();
						int k=(int)(Math.log(al_configs.get(pos).get_capacity()/Math.log(2)));
						if(value<=k)
						{
							value=(int) Math.pow(2,value);
							((Configs) t.getTableView().getItems().get(
									t.getTablePosition().getRow())
									).set_blocksize( Integer.parseInt("" + value));
						}
						else
						{
							t.getTableView().refresh();
						}


					}
				});

				assoc.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Configs,Number>>() {

					@Override
					public void handle(CellEditEvent<Configs, Number> t) {

						int value=t.getNewValue().intValue();
						int pos=t.getTablePosition().getRow();
						int prod=value*al_configs.get(pos).get_blocksize();
						if(prod<=al_configs.get(pos).get_capacity())
						{

							((Configs) t.getTableView().getItems().get(
									t.getTablePosition().getRow())
									).set_associativity( Integer.parseInt("" +value));
						}
						else
						{
							t.getTableView().refresh();
						}


					}
				});

			}
			catch(NumberFormatException nfe)
			{
				Alert alert = new Alert(AlertType.ERROR,"Please insert an integer");
				alert.show();
			}
			catch(Exception e)
			{
				Alert alert = new Alert(AlertType.ERROR,"Something went wrong, try again!");
				alert.show();
			}
		}
		else
		{
			tblv.setEditable(false);
		}
	}
	@FXML
	public void back_to_main()
	{
		try 
		{
			no_of_CPUs=Integer.parseInt(cpu_number.getText());

			if(!number_of_CPUs.isEmpty())
				number_of_CPUs.remove(0);

			number_of_CPUs.add(no_of_CPUs);
			Stage stage = (Stage)ok_cpu.getScene().getWindow();
			stage.close();

		}
		catch(NumberFormatException nfe)
		{
			Alert alert = new Alert(AlertType.ERROR,"Please specify number of processors");
			alert.show();
		}
		catch(NullPointerException npe)
		{
			Alert alert = new Alert(AlertType.ERROR,"Please specify number of processors");
			alert.show();
		}
		catch(Exception e)
		{
			Alert alert = new Alert(AlertType.ERROR,"Please specify number of processors");
			alert.show();
		}

	}



}

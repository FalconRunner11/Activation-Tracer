package nv.activation_tracer;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class ActivationTableModel extends AbstractTableModel {
	
	//-----------------------------------------------------------------//
	
	/** Declare and initialize final variables **/
	
	//-----------------------------------------------------------------//
	
	/** Declare global variables **/
	
	private String[][] tableData;
	private String[] headers;
	
	//-----------------------------------------------------------------//
	
	/** Constructors **/
	
	protected ActivationTableModel(String[][] tD, String[] h) {
		tableData = tD;
		headers = h;
	}
	
	//-----------------------------------------------------------------//
	
	/** Private Methods **/
	
	//-----------------------------------------------------------------//
	
	/** Mutator Methods **/
	
	//-----------------------------------------------------------------//
	
	/** Accessor Methods **/
	
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	public String getColumnName(int column) {
		return headers[column];
	}
	
	public int getColumnCount() {
		return headers.length;
	}

	public int getRowCount() {
		return tableData.length;
	}

	public Object getValueAt(int row, int column) {
		return tableData[row][column];
	}
	
	//-----------------------------------------------------------------//

}

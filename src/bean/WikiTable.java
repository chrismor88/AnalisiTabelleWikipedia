package bean;

import java.util.List;

import org.json.JSONArray;

public class WikiTable {

	private int numberOfLine;
	private String tableName;
	String id;
	private JSONArray tableData;
	private List<Integer> relevantColumns;
	private String jsonTable;
	private int numberOfRowsWithMultipleMentions;


	public WikiTable(String jsonTable, String tableName, String id, int numberOfLine,JSONArray jsonArray,List<Integer> targetColumns) {
		this.numberOfLine = numberOfLine;
		this.tableName=tableName;
		this.id = id;
		this.tableData = jsonArray;
		this.relevantColumns = targetColumns;
		this.jsonTable = jsonTable;
		this.numberOfRowsWithMultipleMentions = 0;
	}

	public JSONArray getTableData() {
		return tableData;
	}
	
	public int getRealNumberOfColumns(){
		return tableData.getJSONArray(0).length();
	}

	public List<Integer> getRelevantColumns() {
		return relevantColumns;
	}


	public int getNumberOfLine() {
		return numberOfLine;
	}

	public String getTableName() {
		return tableName;
	}

	public String getId() {
		return id;
	}
	

	public boolean isImportantThisColumn(int indexColumn){
		return relevantColumns.contains(indexColumn);
	}
	
	public String getJsonTable() {
		return jsonTable;
	}
	
	public int getNumberOfRowsWithMultipleMentions() {
		return numberOfRowsWithMultipleMentions;
	}
	
	public void setNumberOfRowsWithMultipleMentions(int numberOfRowsWithMultipleMentions) {
		this.numberOfRowsWithMultipleMentions = numberOfRowsWithMultipleMentions;
	}
}

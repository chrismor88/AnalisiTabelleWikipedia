package bestselection;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import util.ValidatorCellText;
import util.regex.VerifierRegEx;



public class AnalyzerJSONTable {

	public static final String HARD_SELECTION = "HARD_SELECTION";
	public static final String SOFT_SELECTION = "SOFT_SELECTION";


	private final String ERROR_INVALID_NUMBER = "(\\w*\\s*Error: This is not a valid number. Please refer to the documentation at for correct input.)";
	private final String ERROR = ERROR_INVALID_NUMBER+"|"+"((.)*Expression error: Unrecognised word mdhis. monthsand days(.)*) | (Cannot handle non-empty timestamp argument!)";

	private VerifierRegEx verifier;
	private Logger logger;
	private ValidatorCellText validatorCellText;

	public AnalyzerJSONTable() {
		this.verifier =  new VerifierRegEx();
	}



	public AnalyzerJSONTable(Logger logger) {
		this.verifier =  new VerifierRegEx();
		this.logger = logger;
		this.validatorCellText = new ValidatorCellText(this.logger);
	}


	public List<Integer> haveThisTableAKey(org.json.JSONObject jsonObject,String typeSelection){

		List<Integer> indexOfGoodColumns = new ArrayList<Integer>();

		long numColumns = jsonObject.getLong("numCols");
		long numDataRows = jsonObject.getLong("numDataRows");

		String tableName = jsonObject.getString("pgTitle");

		JSONArray tableHeaders = jsonObject.getJSONArray("tableHeaders");

		//contiene gli indici delle colonne con dato numerico
		JSONArray numericColumns= jsonObject.getJSONArray("numericColumns");
		List<Integer> colonneNumeriche = new ArrayList<Integer>();
		for(Object o : numericColumns){
			int k = Integer.parseInt(o.toString());
			colonneNumeriche.add(k);
		}

		Map<Integer, List<String>> columnsIndexToText = new HashMap<Integer,List<String>>();

		if((numColumns - numericColumns.length()) > 1){
			JSONArray tableData = jsonObject.getJSONArray("tableData");
			for(int i=0;i<numDataRows;i++){
				JSONArray currentRow = (JSONArray) tableData.get(i);
				for(int j=0;j<numColumns ;j++){
					List<String> columnsEntities = columnsIndexToText.get(j);
					if(columnsEntities == null){
						columnsEntities = new ArrayList<>();
						columnsIndexToText.put(j, columnsEntities);
					}

					JSONObject cellObj = currentRow.getJSONObject(j);

					String text = (String)cellObj.get("text");
					text = text.trim();
					text = text.replaceAll(ERROR, "");
					text = text.replaceAll("\"\\s*", "");
					text = text.replaceAll("\\s*\"", "");
					text = text.replaceAll("“","");
					text = text.replaceAll("”", "");
					text = text.replaceAll("'", "");
					text = text.replaceAll("\\[(.)*\\]","");
					text = text.replaceAll("\\((.)*\\)","");
					text = text.replaceAll("\\*", "");
					text = text.replaceAll("\\^", "");

					columnsEntities.add(text);

				}
			}
		}

		switch (typeSelection) {
		case HARD_SELECTION:
			indexOfGoodColumns = selectRelevantColumns_hardSelection(columnsIndexToText,tableName);
			break;
		case SOFT_SELECTION:
			indexOfGoodColumns = selectRelevantColumns_softSelection(columnsIndexToText,tableName);
			break;

		default:
			break;
		}

		columnsSelectionByTheHeader(indexOfGoodColumns,tableHeaders);

		return indexOfGoodColumns;

	}



	private List<Integer> selectRelevantColumns_hardSelection(Map<Integer,List<String>> columnsIndexToText,String tableName){

		List<Integer> indexesOfRelevantColumns = new ArrayList<Integer>();
		int threshold_charcaters_for_column = 4;
		for(Integer key : columnsIndexToText.keySet()){
			boolean badAverageCharacters = averageNumberOfCharactersPerColumn(key,columnsIndexToText.get(key), threshold_charcaters_for_column,tableName);
			boolean notRelevantColumn = false;
			boolean columnWithAllCellsUppercase = allCellsAreUppercase(key, columnsIndexToText.get(key), tableName);
			for(String text : columnsIndexToText.get(key)){
				notRelevantColumn = (validatorCellText.ignoresText2(verifier, text,tableName,key) || badAverageCharacters || columnWithAllCellsUppercase);
				if(notRelevantColumn){
					break;
				}
			}		
			if(!notRelevantColumn)
				indexesOfRelevantColumns.add(key);

		}

		return indexesOfRelevantColumns;
	}



	private List<Integer> selectRelevantColumns_softSelection(Map<Integer,List<String>> columnsIndexToText,String tableName){
		List<Integer> indexOfRelevantColumns = new ArrayList<Integer>();
		int threshold_charcaters_for_column = 4;
		for(Integer key : columnsIndexToText.keySet()){
			boolean badAverageCharacters = averageNumberOfCharactersPerColumn(key,columnsIndexToText.get(key), threshold_charcaters_for_column, tableName);
			boolean goodColumn = false; //ricerca esistenziale delle celle valide per una colonna
			for(String text : columnsIndexToText.get(key)){
				goodColumn = (!(validatorCellText.ignoresText(verifier, text)) && !badAverageCharacters);
				if(goodColumn){
					indexOfRelevantColumns.add(key);
					break;
				}
			}
		}		

		return indexOfRelevantColumns;

	}



	private void columnsSelectionByTheHeader(List<Integer> listColumns, JSONArray tableHeaders){
		JSONArray header = (JSONArray) tableHeaders.get(0);
		if(header != null){
			for(int i=0;i<header.length();i++){
				JSONObject obj = header.getJSONObject(i);
				String text = obj.getString("text");
				if(text.equals("Notes")|| text.equals("Description")||text.equals("Ref")||text.equals("Reference") || 
						text.equals("Route Name") || text.matches("Length(.)*") || text.matches("Size(.)*") ||text.matches("Height(.)*")|| 
						text.matches("Result")|| text.matches("Comment") || text.matches("Post-nominal")|| text.matches("Abbreviation(s)?")
						|| text.matches("Type") || text.matches("Successor") || text.matches("Acronym(s)?"))
					listColumns.removeIf(new MyPredicate<Integer>(i));

			}
		}
	}


	//the current column is discarded if the average of characters is minor than specified threshold
	public boolean averageNumberOfCharactersPerColumn(int columnId, List<String> columnsEntities, int threshold, String tableName){
		double sum = 0;
		int emptyCell = 0;
		for(String s : columnsEntities){
			s = s.trim();
			if(s.equals(""))
				emptyCell++;
			sum += s.length();
		}

		boolean result = (double)(sum/(columnsEntities.size()-emptyCell)) < (double)threshold;

		if(result)
			logger.info("Invalid column "+columnId+"\tfor table: "+tableName+"\tfor bad average characters");

		return result;
	}


	public boolean allCellsAreUppercase(int columnId, List<String> columnsEntities,String tableName){
		boolean result = true;
		Pattern pattern = Pattern.compile("^[A-Z]+$");
		for(String currentCell : columnsEntities){
			Matcher matcher = pattern.matcher(currentCell);
			if(!matcher.matches()){
				result = false;
				break;
			}
		}

		return result;
	}
}


class MyPredicate<Integer> implements Predicate<Integer>{
	Integer var;

	public MyPredicate(Integer i) {
		this.var = i;
	}

	@Override
	public boolean test(Integer t) {
		return var.equals(t);
	}
}

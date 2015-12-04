package bestselection.two_columns;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;


import util.regex.VerifierRegEx;
import util.ValidatorCellText;
import bean.*;

public class StatisticalTable{

	private final String PREFIX_URL = "en.wikipedia.org/wiki/";

	private BufferedWriter writerResultsFile;
	private BufferedWriter writerContentTable;
	private BufferedWriter fileWriterListOfFiltered;
	private ValidatorCellText validatorCellText;



	public StatisticalTable(BufferedWriter writerResultsFile, BufferedWriter wrtierContentTable) {
		this.writerResultsFile = writerResultsFile;
		this.writerContentTable = wrtierContentTable;
		this.validatorCellText = new ValidatorCellText();

	}


	public StatisticalTable(BufferedWriter writerResultsFile,
			BufferedWriter writerContentTable, BufferedWriter fileWriterListOfFiltered) {
		this.writerResultsFile = writerResultsFile;
		this.writerContentTable = writerContentTable;
		this.fileWriterListOfFiltered = fileWriterListOfFiltered;
		this.validatorCellText = new ValidatorCellText();

	}


	public void writeStaticalForThisTable(WikiTable wikiTable) {
		Set<List<String>> tableWithoutDuplicatedRows = new LinkedHashSet<List<String>>();

		int numberOfCompletedRows = 0; //righe con entrambi i campi pieni
		int numberOfRowsAtLeastOneMention = 0; //righe con almeno una mention in uno dei campi rilevanti
		int numberLineInDump = wikiTable.getNumberOfLine();
		int numberOfRowsWithMentions = 0;
		int numberOfRowsWithMultipleMentions = 0;

		if(wikiTable!=null){
			int numOfRows = wikiTable.getTableData().length();

			//iterazione per righe
			for(int i = 0;i<numOfRows;i++){
				List<String> row = new ArrayList<String>();

				int numberOfCellsWithMentions = 0;
				int numberOfCellsWithMultipleMentions = 0;
				int numberOfCellsWithText = 0;

				JSONArray currentRow = wikiTable.getTableData().getJSONArray(i);
				VerifierRegEx verifier = new VerifierRegEx();

				//iterazione per colonne
				for(int j=0;j<currentRow.length();j++){
					if(wikiTable.isImportantThisColumn(j)){
						JSONObject cellObject = currentRow.getJSONObject(j);
						String text = cellObject.getString("text");
						JSONArray surfaceLinks = cellObject.getJSONArray("surfaceLinks");
						String cleanedText = cleanText(text);
						if(!(validatorCellText.ignoresText(verifier, cleanedText))){
//								String cellWithMentions = "";
//								//considera solo quelle celle che hanno mentions
//								for(int k=0;k<surfaceLinks.size();k++){
//									JSONObject obj = (JSONObject)surfaceLinks.get(k);
//										JSONObject fieldTitle = ((JSONObject)obj.get("target"));
//										String wikid = (String)fieldTitle.get("title");
//										if(surfaceLinks.size() > 1){
//											cellWithMentions = cellWithMentions.concat(wikid+"@");
//										}
//										else
//											cellWithMentions = wikid;
//								}
//								row.add(cleanedText);
							



							if(surfaceLinks.length()>0){
								JSONObject obj = surfaceLinks.getJSONObject(0);
								JSONObject fieldTitle = obj.getJSONObject("target");
								String wikid = fieldTitle.getString("title");
								row.add(wikid);
								numberOfCellsWithMentions ++;
							}

							if(surfaceLinks.length() > 1){
								numberOfCellsWithMultipleMentions++;
							}
							//row.add(cleanedText);
							numberOfCellsWithText++;


						}
					}
				}

				tableWithoutDuplicatedRows.add(row);

				if(numberOfCellsWithMentions == wikiTable.getRelevantColumns().size())
					numberOfRowsWithMentions++;

				if(numberOfCellsWithMentions > 0)
					numberOfRowsAtLeastOneMention++;

				if(numberOfCellsWithText == wikiTable.getRelevantColumns().size())
					numberOfCompletedRows++;

				if(numberOfCellsWithMultipleMentions > 0)
					numberOfRowsWithMultipleMentions++;

				wikiTable.setNumberOfRowsWithMultipleMentions(numberOfRowsWithMultipleMentions);
			}


			synchronized (writerResultsFile) {

				synchronized (writerContentTable) {
					try {
						int numberOfOriginalColumns = wikiTable.getTableData().getJSONArray(0).length();
						int numberOfOriginalRows = wikiTable.getTableData().length();
						int numberOfRowsWithoutDuplicate = tableWithoutDuplicatedRows.size();
						boolean isAValidTable = this.printContentTable(wikiTable.getTableName(), wikiTable.getId(), wikiTable.getTableData().length(),numberOfOriginalColumns, wikiTable.getRelevantColumns().size(),wikiTable.getNumberOfRowsWithMultipleMentions(), tableWithoutDuplicatedRows, writerContentTable);
						if (isAValidTable){
							writerResultsFile.append(numberLineInDump+"\t"+wikiTable.getTableName()+"\t"+PREFIX_URL+wikiTable.getTableName()+"\t"+wikiTable.getRealNumberOfColumns()+"\t"+wikiTable.getRelevantColumns().size()+"\t"+numberOfOriginalRows+"\t"+numberOfRowsWithoutDuplicate+"\t"+numberOfCompletedRows+"\t"
									+numberOfRowsWithMentions+"\t"+numberOfRowsAtLeastOneMention+"\t"+numberOfRowsWithMultipleMentions+"\n");
						}	

					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}



	private String cleanText(String text) {
		final String ERROR_INVALID_NUMBER = "(\\w*\\s*Error: This is not a valid number. Please refer to the documentation at for correct input.)";
		final String ERROR = ERROR_INVALID_NUMBER+"|"+"((.)*Expression error: Unrecognised word mdhis. monthsand days(.)*)";

		text = text.trim();
		String result = text.replaceAll("Category:Articles with hCards", "");
		result = result.replaceAll("Category:Jct template transclusions with missing shields", "");
		result = result.replaceAll("Category:Articles with hAudio microformats", "");
		result = result.replaceAll("Category:Articles with OS grid coordinates", "");
		result = result.replaceAll("Category:Articles containing Hebrew-language text", "");
		result = result.replaceAll("Category:Pages with incorrect formatting templates use","");
		result = result.replaceAll("Category:Pages with bad rounding precision", "");
		result = result.replaceAll("Category:Articles containing potentially dated statements", "");
		result = result.replaceAll("Cannot handle non-empty timestamp argument!", "");
		result = result.replaceAll("\"\\s*", "");
		result = result.replaceAll("\\s*\"", "");
		result = result.replaceAll("“","");
		result = result.replaceAll("”", "");
		result = result.replaceAll("'", "");
		result = result.replaceAll("\\[(.)*\\]","");
		result = result.replaceAll("\\((.)*\\)","");
		result = result.replaceAll("\\*", "");
		result = result.replaceAll("\\^", "");
		result = result.replaceAll(ERROR, "");


		return result;
	}


	private boolean printContentTable(String tableName, String pageId, int numberOfRows,int originalColumns, int relevantColumns,int numberOfRowsWithMultipleMentions, Set<List<String>> matrix, BufferedWriter writer){
		boolean response = false;

		Iterator<List<String>> iterator = matrix.iterator();
		if(matrix.size() >1 && iterator.next().size() == relevantColumns){
			response = true;

			try {
				String[] pageIdParties = pageId.split("-");
				writer.append("<doc\tidPage="+pageIdParties[0]+"\tidTable="+pageIdParties[1]+"\twikid="+tableName+"\tnum_original_row="+numberOfRows+"\tnum_original_col="+originalColumns+"\tnum_significant_columns="+relevantColumns+"\tnrows_with_multiple_mentions="+numberOfRowsWithMultipleMentions+"\t>\n");
				for(List<String> currentRow : matrix){
					if(currentRow.size()==relevantColumns){
						int i = 0;
						while(i<currentRow.size()-1){
							writer.append(currentRow.get(i)+"\t");
							i++;
						}
						writer.append(currentRow.get(i)+"\n");
					}

				}
				writer.append("</doc>\n");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return response;
	}


	public void writeStaticalForThisTable2(WikiTable wikiTable) {
		Set<List<String>> tableWithoutDuplicatedRows = new LinkedHashSet<List<String>>();

		int numberOfCompletedRows = 0; //righe con entrambi i campi pieni
		int numberOfRowsWithMentions = 0; //righe con entrambe le colonne con mentions sia blu che rosse
		int numberOfRowsAtLeastOneMention = 0; //righe con almeno una mention in uno dei campi rilevanti
		int numberOfRowsWithMultipleMentions = 0;

		int numberLineInDump = wikiTable.getNumberOfLine();

		if(wikiTable!=null){
			int numOfRows = wikiTable.getTableData().length();

			//iterazione per righe
			for(int i = 0;i<numOfRows;i++){
				List<String> row = new ArrayList<String>();

				int numberOfCellsWithMentions = 0;
				int numberOfCellsWithText = 0;
				int numberOfCellsWithMultipleMentions = 0;

				JSONArray currentRow =  wikiTable.getTableData().getJSONArray(i);
				VerifierRegEx verifier = new VerifierRegEx();

				//iterazione per colonne
				for(int j=0;j< currentRow.length();j++){
					if(wikiTable.isImportantThisColumn(j)){
						JSONObject cellObject = currentRow.getJSONObject(j);
						String text = cellObject.getString("text");
						JSONArray surfaceLinks = cellObject.getJSONArray("surfaceLinks");
						String cleanedText = cleanText(text);
						if(!(validatorCellText.ignoresText(verifier, cleanedText))){
							row.add(cleanedText);
							numberOfCellsWithText++;
							if(surfaceLinks.length() > 0){
								numberOfCellsWithMentions ++;
							}
							if(surfaceLinks.length() > 1){
								numberOfCellsWithMultipleMentions++;
							}
						}
					}
				}
				tableWithoutDuplicatedRows.add(row);

				if(numberOfCellsWithMentions == wikiTable.getRelevantColumns().size())
					numberOfRowsWithMentions++;

				if(numberOfCellsWithMentions > 0)
					numberOfRowsAtLeastOneMention++;

				if(numberOfCellsWithText == wikiTable.getRelevantColumns().size())
					numberOfCompletedRows++;

				if(numberOfCellsWithMultipleMentions > 0)
					numberOfRowsWithMultipleMentions++;

			}


			synchronized (writerResultsFile) {

				synchronized (writerContentTable) {

					synchronized (fileWriterListOfFiltered) {

						try {
							int numberOfOriginalColumns = wikiTable.getTableData().getJSONArray(0).length();
							int numberOfOriginalRows = wikiTable.getTableData().length();
							int numberOfRowsWithoutDuplicate = tableWithoutDuplicatedRows.size();
							boolean isAValidTable = this.printContentTable(wikiTable.getTableName(), wikiTable.getId(), wikiTable.getTableData().length(),numberOfOriginalColumns, wikiTable.getRelevantColumns().size(),numberOfRowsWithMultipleMentions, tableWithoutDuplicatedRows, writerContentTable);
							if (isAValidTable){
								writerResultsFile.append(numberLineInDump+"\t"+wikiTable.getTableName()+"\t"+PREFIX_URL+wikiTable.getTableName()+"\t"+wikiTable.getRealNumberOfColumns()+"\t"+wikiTable.getRelevantColumns().size()+"\t"+numberOfOriginalRows+"\t"+numberOfRowsWithoutDuplicate+"\t"+numberOfCompletedRows+"\t"
										+numberOfRowsWithMentions+"\t"+numberOfRowsAtLeastOneMention+"\n");

								fileWriterListOfFiltered.append(wikiTable.getJsonTable()+"\n");
							}	

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}
	}

}

package bestselection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import bean.WikiTable;
import bestselection.two_columns.StatisticalTable;




public class Consumer extends Thread {

	private BlockingQueue<String> messageBuffer; //buffer in cui vengono trasmessi e prelevati le stringhe di tipo json
	private BlockingQueue<String> outputBuffer; //buffer per comunicare al produttore la terminazione dei consumatori
	private AnalyzerJSONTable analyzer; // analizzatore sintattico di supporto
	private BufferedWriter fileWriterListOfFiltered; // crea un file tsv per le statistiche sulle tabelle
	private Main main;


	private StatisticalTable statisticalTable;




	public Consumer(BlockingQueue<String> messageBuffer, BlockingQueue<String> responseBuffer,
			BufferedWriter fileWriterListOfFiltered, BufferedWriter writerStatisticalTablesWithRelevantColumns,
			BufferedWriter writerContentTablesWithTwoRelevantColumns, Logger logger, Main main) {
		this.analyzer = new AnalyzerJSONTable(logger);
		this.messageBuffer = messageBuffer;
		this.outputBuffer = responseBuffer;
		this.statisticalTable = new StatisticalTable(writerStatisticalTablesWithRelevantColumns, writerContentTablesWithTwoRelevantColumns,fileWriterListOfFiltered);
		this.main = main;


	}


	public Consumer(BlockingQueue<String> messageBuffer, BlockingQueue<String> responseBuffer,
			BufferedWriter writerStatisticalTablesWithTwoRelevantColumns,
			BufferedWriter writerContentTablesWithTwoRelevantColumns, Logger logger, Main main) {
		
		this.analyzer = new AnalyzerJSONTable(logger);
		this.messageBuffer = messageBuffer;
		this.outputBuffer = responseBuffer;
		this.statisticalTable = new StatisticalTable(writerStatisticalTablesWithTwoRelevantColumns,writerContentTablesWithTwoRelevantColumns);
		this.main = main;
	}


	@Override
	public void run() {
		super.run();
		int numberOfLine = 0;
		
		while(true){

			try {
				String message = messageBuffer.take();
				if(message.equals(Message.FINISHED_PRODUCER)){
					break;
				}
				else{
					String[] messageSplitted = message.split("#@#");
					numberOfLine = Integer.parseInt(messageSplitted[0]);
//					JSONParser jsonParser = new JSONParser();
//					JSONObject jsonObject = (JSONObject) jsonParser.parse(messageSplitted[1]);
					
					
					JSONObject jsonObject = new JSONObject(messageSplitted[1]);
					
					
					String id = jsonObject.getString("_id");
					String pgTitle = jsonObject.getString("pgTitle");
					String wikiId = pgTitle.replaceAll(" ","_");
					
//					if(!wikiId.contains("List_of_minor_planets")){
					
						List<Integer> relevantColumns = analyzer.haveThisTableAKey(jsonObject,AnalyzerJSONTable.HARD_SELECTION);
						
						if(relevantColumns.size() > 1){
							WikiTable wikiTable = new WikiTable(messageSplitted[1],wikiId,id, numberOfLine, jsonObject.getJSONArray("tableData"), relevantColumns);
							statisticalTable.writeStaticalForThisTable(wikiTable);
						}
						
						main.incrementCounter();
						System.out.println(main.getCounter());
						
//					}
					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}


		try {
			messageBuffer.put(Message.FINISHED_PRODUCER);
			outputBuffer.put(Message.FINISHED_CONSUMER);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		this.interrupt();

	}



}

package bestselection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import util.ReaderFileConfig;



public class Main {
	int counterCompletedTables;
	int good_tables;
	int bad_tables;

	public Main() {
		counterCompletedTables = 0;
		good_tables = 0;
		bad_tables = 0;
	}


	public synchronized void incrementCounter(){
		counterCompletedTables++;
	}

	public synchronized int getCounter(){
		return counterCompletedTables;
	}


	public synchronized void incrementGood(){
		good_tables++;
	}

	public synchronized void incrementBad(){
		bad_tables++;
	}

	public synchronized int getCounterGoodTables(){
		return good_tables;
	}

	public synchronized int getCounterBadTables(){
		return bad_tables;
	}



	public static void main(String[] args) {
//		executionForListOf();
		executionForDump();

	}


	private static void executionForListOf(){
		try{
			ReaderFileConfig readerFileConfig = new ReaderFileConfig();
			String PATH_FILE_LIST_OF = readerFileConfig.getValueOf("path_list_of");
			String LIST_OF_TIMESTAMP = readerFileConfig.getValueOf("path_list_of_timestamp");
			String LIST_OF_STATISTICAL_FILE_TABLE_RELEVANT_COLUMNS = readerFileConfig.getValueOf("path_list_of_statistics");
			String LIST_OF_FILE_CONTENT_TABLE_RELEVANT_COLUMNS=readerFileConfig.getValueOf("path_list_of_content_file");
			String LOG_FILE= readerFileConfig.getValueOf("path_list_of_log");

			Main main = new Main();

			long startInMillis = 0, endInMillist = 0, totalInMillis = 0;

			Calendar calendar = Calendar.getInstance();
			java.util.Date today = calendar.getTime();

			File fileListOf = new File(PATH_FILE_LIST_OF);
			File timestamp = new File(LIST_OF_TIMESTAMP);

			File fileStatisticalTablesWithTwoRelevantColumns = new File(LIST_OF_STATISTICAL_FILE_TABLE_RELEVANT_COLUMNS);
			File fileContentTablesWithTwoRelevantColumns = new File(LIST_OF_FILE_CONTENT_TABLE_RELEVANT_COLUMNS);


			Logger logger = Logger.getLogger("MyLog");  

			// This block configure the logger with handler and formatter  
			FileHandler fh = new FileHandler(LOG_FILE);  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);
			logger.setUseParentHandlers(false);

			PrintWriter writerTimestamp = null;
			BufferedWriter writerStatisticalTablesWithTwoRelevantColumns = null;
			BufferedWriter writerContentTablesWithTwoRelevantColumns = null;


			Producer producer = null;

			writerTimestamp = new PrintWriter(new BufferedWriter(new FileWriter(timestamp, true)));

			BlockingQueue<String> messageBuffer = new LinkedBlockingQueue<String>(1000);
			BlockingQueue<String> responseBuffer = new LinkedBlockingQueue<String>(10);

			producer = new Producer(fileListOf, messageBuffer, responseBuffer);

			producer.start();
			Thread.sleep(2000);

			startInMillis = System.currentTimeMillis();

			int cores = Runtime.getRuntime().availableProcessors();
//			int cores = 1;
			Consumer[] consumers = new Consumer[cores];

			writerStatisticalTablesWithTwoRelevantColumns = new BufferedWriter(new FileWriter(fileStatisticalTablesWithTwoRelevantColumns));
			writerContentTablesWithTwoRelevantColumns = new BufferedWriter(new FileWriter(fileContentTablesWithTwoRelevantColumns));

			writerStatisticalTablesWithTwoRelevantColumns.append("NUM. LINE IN DUMP\tTABLE NAME\tURL\tORIGINAL NUMBER OF COLUMNS\tSIGNIFICANT NUMBER OF COLUMNS\tORIGINAL NUMBER OF ROWS\tNUM. OF ROWS WITHOUT DUPLICATE\tNUM. COMPLETED ROWS\tNUM. ROWS WITH MENTIONS\tNUM. ROWS WITH AT LEAST ONE MENTION\tNUM. ROWS WITH MULTIPLE MENTIONS\n");


			for(int i=0; i<cores;i++){
				consumers[i] = new Consumer(messageBuffer, responseBuffer,writerStatisticalTablesWithTwoRelevantColumns, 
						writerContentTablesWithTwoRelevantColumns,logger,main);

				consumers[i].start();
			}


			producer.join();
			writerStatisticalTablesWithTwoRelevantColumns.close();
			writerContentTablesWithTwoRelevantColumns.close();

			System.out.println("Finished");
			endInMillist = System.currentTimeMillis();
			totalInMillis = endInMillist - startInMillis;
			System.out.println("Total time in millis: "+totalInMillis);
			double totalTimeInSeconds = (double)totalInMillis /1000;
			System.out.println("Total time in seconds: "+totalTimeInSeconds);
			writerTimestamp.append("Esecuzione del "+today.toString()+", TEMPO DI ESECUZIONE TOTALE:"+totalTimeInSeconds+" secs\n");
			writerTimestamp.append("Number of good tables: "+main.getCounterGoodTables()+"\n");
			writerTimestamp.append("Number of bad tables: "+main.getCounterBadTables()+"\n\n\n");
			writerTimestamp.close();	



		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}catch (InterruptedException e) {
			e.printStackTrace();
		} 

	}



	private static void executionForDump(){
		Main main = new Main();
		ReaderFileConfig readerFileConfig = new ReaderFileConfig();

		long startInMillis = 0, endInMillist = 0, totalInMillis = 0;

		Date startTime = new Date();

		int cores = Runtime.getRuntime().availableProcessors();
		Consumer[] consumers = new Consumer[cores];

		try {
			String path_dump_statistics = readerFileConfig.getValueOf("path_dump_statistics");
			String path_dump_content = readerFileConfig.getValueOf("path_dump_content_file");
			String path_dump_timestamp = readerFileConfig.getValueOf("path_dump_timestamp");
			String path_dump = readerFileConfig.getValueOf("path_dump");
			String path_dump_log = readerFileConfig.getValueOf("path_dump_log");

			File tables_dump = new File(path_dump);
			BufferedWriter writerTimestamp1 = new BufferedWriter(new FileWriter(path_dump_timestamp));
			BufferedWriter writerStatisticalTablesWithTwoRelevantColumns = new BufferedWriter(new FileWriter(path_dump_statistics));
			BufferedWriter writerContentTablesWithTwoRelevantColumns = new BufferedWriter(new FileWriter(path_dump_content));
			writerStatisticalTablesWithTwoRelevantColumns.append("NUM. LINE IN DUMP\tTABLE NAME\tURL\tORIGINAL NUMBER OF COLUMNS\tSIGNIFICANT NUMBER OF COLUMNS\tORIGINAL NUMBER OF ROWS\tNUM. OF ROWS WITHOUT DUPLICATE\tNUM. COMPLETED ROWS\tNUM. ROWS WITH MENTIONS\tNUM. ROWS WITH AT LEAST ONE MENTION\tNUM. ROWS WITH MULTIPLE MENTIONS\n");

			Producer producer = null;

			BlockingQueue<String> messageBuffer = new LinkedBlockingQueue<String>(10000);
			BlockingQueue<String> responseBuffer = new LinkedBlockingQueue<String>(10);

			producer = new Producer(tables_dump, messageBuffer, responseBuffer);
			producer.start();
			Thread.sleep(2000);
			
			startInMillis = System.currentTimeMillis();


			// This block configure the logger with handler and formatter  
			Logger logger = Logger.getLogger("MyLog");  
			FileHandler fh = new FileHandler(path_dump_log);  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);
			logger.setUseParentHandlers(false);


			for(int i=0; i<cores;i++){
				consumers[i] = new Consumer(messageBuffer, responseBuffer,writerStatisticalTablesWithTwoRelevantColumns, 
						writerContentTablesWithTwoRelevantColumns,logger,main);

				consumers[i].start();
			}



			producer.join();
			Date endTime = new Date();

			writerStatisticalTablesWithTwoRelevantColumns.close();
			writerContentTablesWithTwoRelevantColumns.close();
			System.out.println("Finished");

			endInMillist = System.currentTimeMillis();
			totalInMillis = endInMillist - startInMillis;

			System.out.println("Total time in millis: "+totalInMillis);
			double totalTimeInSeconds = (double)totalInMillis /1000;
			System.out.println("Total time in seconds: "+totalTimeInSeconds);
			writerTimestamp1.append("Inizio esecuzione: "+startTime.toString()+"\n Fine esecuzione:"+endTime.toString()+"\nTempo totale di esecuzione:"+totalTimeInSeconds+" secs\n");
			writerTimestamp1.close();
		}catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}




	}
}
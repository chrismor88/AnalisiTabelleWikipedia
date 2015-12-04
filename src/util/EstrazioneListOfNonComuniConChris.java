package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EstrazioneListOfNonComuniConChris {


	public static void main(String[] args) throws IOException {

		//		List<String> listOfNonInComune = new ArrayList<String>();
		Set<String> listOfNonInComune = new LinkedHashSet<String>();
		String line = "";

		BufferedReader br = new BufferedReader(new FileReader("/home/chris88/Scrivania/574_Elementi_non_in_comune_con_chris.txt"));


		while((line=br.readLine())!=null){
			if(line.contains("<wikid=")){
				String list_of_fullname = line.replaceAll("<wikid=", "");
				list_of_fullname = list_of_fullname.replaceAll(">", "");
				Pattern pattern = Pattern.compile("-\\d+$");
				Matcher matcher = pattern.matcher(list_of_fullname);
				if(matcher.find()){
					String finalPageId = (matcher.group()).replace('-', ' ');
					String wikid = list_of_fullname.replace(matcher.group(), "");
					finalPageId = finalPageId.trim();
					listOfNonInComune.add(finalPageId+"\t"+wikid);
				}
			}
		}




		br = new BufferedReader(new FileReader("/home/chris88/Documenti/Tesi/wiki_tables/list_of/List_of_2.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("/home/chris88/Scrivania/574_list_of_non_in_comune_con_chris.txt"));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter("/home/chris88/Scrivania/list_of_non_trovate_delle_574.txt"));
		int counter = 0;
		int counterListOf = 0;
		List<String> listOfTrovate = new ArrayList<String>();
		while((line=br.readLine())!=null && (counterListOf < listOfNonInComune.size())){
			counter ++;
			System.out.println(counter);
			JSONParser parser = new JSONParser();
			try {
				JSONObject jsonObject = (JSONObject) parser.parse(line);
				String id = (String)jsonObject.get("_id");
				String pgTitle = (String)jsonObject.get("pgTitle");
				String wikid = pgTitle.replaceAll(" ","_");
				String[] idParties = id.split("-");

				String currentEntity = idParties[1]+"\t"+wikid;
				if(listOfNonInComune.contains(currentEntity)){
					bw.append(line+"\n");
					counterListOf++;
					listOfTrovate.add(idParties[1]+"\t"+wikid);
				}


			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		
		listOfNonInComune.removeAll(listOfTrovate);
		for(String s: listOfNonInComune){
			String[] fields = s.split("\t");
			bw2.append(fields[1]+"-"+fields[0]+"\n");
			
		}
		
		br.close();
		bw.flush();
		bw.close();
		
		bw2.flush();
		bw2.close();






	}
}
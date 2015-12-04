package bestselection;

import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FiltroDuplicatiListOf {

	private Set<String> tabelleProcessate;

	public FiltroDuplicatiListOf() {
		tabelleProcessate = new LinkedHashSet<>();
	}

	public boolean valutaTabella(String table){
		boolean response = false;
		JSONParser parser = new JSONParser();
			JSONObject jsonObject = new JSONObject(table);
			String pgTitle = jsonObject.getString("pgTitle");
			if(!tabelleProcessate.contains(pgTitle)){
				tabelleProcessate.add(pgTitle);
				response = true;
			}


		return response;

	}

}

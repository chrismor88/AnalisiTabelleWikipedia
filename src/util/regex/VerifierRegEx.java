package util.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifierRegEx {

	private static final String WITHOUT_VALUE = "(-|—|None|none|N/A|n/a)";
	private static final String ERROR_INVALID_NUMBER = "(\\w*\\s*Error: This is not a valid number. Please refer to the documentation at for correct input.)";
	private static final String NUMERIC_REGEX = "(^\\d{1,3}(.)*)";
	private static final String MONTH = "(January|Jan(\\.)?|February|Feb(\\.)?|March|Mar(\\.)?|April|Apr(\\.)?|May|June|Jun(\\.)|July|Jul(\\.)?|August|Aug(\\.)?|September|Sept|Sep(\\.)?|October|Oct(\\.)?|November|Nov(\\.)?|December|Dec(\\.)?)";
	private static final String DATE = "("+MONTH+"\\s?\\d{1,2}(.)*)";
	private static final String BOOLEAN_VALUE = "(Yes|YES|No|NO|yes|no)";
	private static final String ERROR = "(.)*Expression error: Unrecognised word mdhis. monthsand days(.)*";
	private static final String START_WITH_SPECIAL_CHARACTER = "^\\W(.)*";
	private static final String SPECIAL_CHARACTER = "[ṢàèìòùẁǹÀÈÌÒÙẀǸáéíóúẃŕýṕśḱĺźćńḿÁÉÍÓÚẂŔÝṔŚḰĹŹĆŃḾőűŐŰäëïöüẅẗÿḧẍÄËÏÖÜẄŸḦẌãẽĩõũỹñÃẼĨÕŨỸÑâêîôûŵŷŝĝĥĵẑĉÂÊÎÔÛŴŶŜĜĤĴẐĈȩŗţşḑģḩķļçņŖŢŞḐĢḨĶĻÇŅąęįǫųĄĘĮǪŲāēīōūȳḡĀĒĪŌŪȲḠåůẘẙÅŮăĕĭŏŭğĂĔĬŎŬĞǎěǐǒǔǰřťšďǧȟǩžčňǍĚǏǑǓŘŤŠĎǦǨŽČŇȧėȯẏẇṙṫṗṡḋḟġḣżẋċḃṅṁȦĖȮİẎẆṘṪṖṠḊḞĠḢŻẊĊḂṄṀÆæØøÐ]";
	private static final String SPECIAL_CHARACTER_INSIDE = "(^(.)+\\s[\\\\/\\-\\–]+\\s(.)+$)|(^(.)+[\\$£\\|\\^§°#;:]+(.)+$)";
	public static final String END_SPECIAL_CHARACTER="(.)+[\\-\\/:\\–†]+$";  //carattere speciale finale tra /,-,–,†,:
	
	public boolean isANumericValue(String text){
		Pattern pattern = Pattern.compile(NUMERIC_REGEX);
		Matcher matcher = pattern.matcher(text);
		return matcher.matches();

	}

	public boolean isADate(String text){
		Pattern pattern = Pattern.compile(DATE);
		Matcher matcher = pattern.matcher(text);

		return matcher.matches();
	}



	public boolean withoutValue(String text){
		Pattern pattern = Pattern.compile(WITHOUT_VALUE);
		Matcher matcher = pattern.matcher(text);

		return matcher.matches();

	}


	public boolean hasBooleanValue(String text){
		Pattern pattern = Pattern.compile(BOOLEAN_VALUE);
		Matcher matcher = pattern.matcher(text);

		return matcher.matches();


	}


	public boolean endsWithSpecialCharacter(String text){
		Pattern pattern = Pattern.compile(END_SPECIAL_CHARACTER);
		Matcher matcher = pattern.matcher(text);

		return matcher.matches();

	}
	

	public boolean containsError(String text){
		Pattern pattern = Pattern.compile(ERROR+"|"+ERROR_INVALID_NUMBER);
		Matcher matcher = pattern.matcher(text);

		return matcher.matches();
	}

	public boolean startWithSpcecialCharacter(String text){
		Pattern pattern = Pattern.compile(START_WITH_SPECIAL_CHARACTER);
		Matcher matcher = pattern.matcher(text);

		return matcher.matches();
	}

	public boolean firstCharacterIsSpecial(String text){
		if(!text.equals("")){
			String s = String.valueOf(text.charAt(0));
			Pattern pattern = Pattern.compile(SPECIAL_CHARACTER);
			Matcher matcher = pattern.matcher(s);
			return matcher.matches();
		}
		return false;
	}



	public boolean specialCharacterInside(String text){
		Pattern pattern = Pattern.compile(SPECIAL_CHARACTER_INSIDE);
		Matcher matcher = pattern.matcher(text);

		return matcher.matches();
	}



	public static void main(String[] args) {
		VerifierRegEx verifier = new VerifierRegEx();
	
		
		String s1 = "AAG TV";
		String s2 = "AAG.TV";
		String s3 = "AAG-TV";
		String s4 = "AAG - TV";
		String s5 = "AAG/ TV";
		String s6 = "AAG TV/";
		String s7 = "AAG: TV";
		String s8 = "AAG TV-";
		String s9 ="American Football League All-Star Game Most Valuable Player Award";
		String s10 = "AAG – TV";
		
		
		System.out.println(s1+": "+verifier.specialCharacterInside(s1));
		System.out.println(s2+": "+verifier.specialCharacterInside(s2));
		System.out.println(s3+": "+verifier.specialCharacterInside(s3));
		System.out.println(s4+": "+verifier.specialCharacterInside(s4));
		System.out.println(s5+": "+verifier.specialCharacterInside(s5));
		System.out.println(s6+": "+verifier.specialCharacterInside(s6));
		System.out.println(s7+": "+verifier.specialCharacterInside(s7));
		System.out.println(s9+": "+verifier.specialCharacterInside(s9));
		System.out.println(s10+": "+verifier.specialCharacterInside(s10));
		System.out.println(s6+": "+verifier.endsWithSpecialCharacter(s6)+", END WITH SPECIAL CHARACTER");
		

		System.out.println(VerifierRegEx.SPECIAL_CHARACTER_INSIDE);
		
		
	}

}

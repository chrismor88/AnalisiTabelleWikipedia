package util;

import java.util.logging.Logger;

import util.regex.VerifierRegEx;

public class ValidatorCellText {
	private Logger logger;

	public ValidatorCellText() {}

	public ValidatorCellText(Logger logger){
		this.logger = logger;
	}

	public boolean ignoresText(VerifierRegEx verifier, String text) {
		text = text.trim();
		String[] words = text.split(" ");
		String[] wordsWithComma = text.split(",");
		if(verifier.firstCharacterIsSpecial(text)){
			return (text.equals("") || verifier.withoutValue(text) || verifier.isADate(text) || verifier.isANumericValue(text) || 
					verifier.hasBooleanValue(text) || words.length > 10 ||
					wordsWithComma.length > 2 || verifier.specialCharacterInside(text));
		}
		else{

			return (text.equals("") || verifier.withoutValue(text) || verifier.isADate(text) || verifier.isANumericValue(text) || 
					verifier.hasBooleanValue(text) || words.length > 10 || wordsWithComma.length > 2 ||
					verifier.specialCharacterInside(text) || verifier.startWithSpcecialCharacter(text));
		}
	}
	
	
	
	

	public boolean ignoresText2(VerifierRegEx verifier, String text, String tableName, int indexColumn) {
		text = text.trim();
		String[] words = text.split(" ");
		String[] wordsWithComma = text.split(",");
		if(verifier.firstCharacterIsSpecial(text)){
			
			if(text.equals("")){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\tscartata per: campo vuoto");
				}
				return true;
			}
			if(verifier.withoutValue(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: assenza di valore");
				}
				return true;

			}


			if(verifier.isADate(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: data");
				}
				return true;

			}

			if(verifier.isANumericValue(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: valore numerico iniziale");
				}
				return true;


			}

			if(verifier.hasBooleanValue(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: valore booleano");
				}
				return true;


			}
			if(words.length > 10){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: numero di parole > 10");
				}
				return true;


			}
			if(wordsWithComma.length > 2){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: numero di virgole > 1");
				}
				return true;

			}
			
			if(verifier.endsWithSpecialCharacter(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: trattino finale");
				}
				return true;
			}
			
			if(verifier.specialCharacterInside(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: carattere speciale interno alla stringa");
				}
				return true;
			}
		}
		else{
			
		
			if(text.equals("")){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: campo vuoto");
				}
				return true;
			}
			if(verifier.withoutValue(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: assenza di valore");
				}
				return true;

			}


			if(verifier.isADate(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: data");
				}
				return true;

			}

			if(verifier.isANumericValue(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: valore numerico iniziale");
				}
				return true;


			}

			if(verifier.hasBooleanValue(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: valore booleano");
				}
				return true;


			}
			if(words.length > 10){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: numero di parole > 10");
				}
				return true;


			}
			if(wordsWithComma.length > 2){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: numero di virgole > 1");
				}
				return true;

			}
			
			if(verifier.endsWithSpecialCharacter(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: trattino finale");
				}
				return true;
			}

			if(verifier.specialCharacterInside(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: carattere speciale interno alla stringa");
				}
				return true;
			}



			if(verifier.startWithSpcecialCharacter(text)){
				synchronized(logger){
					logger.info("Colonna:"+indexColumn+"\tdella tabella: "+tableName+"\ttext: "+text+"\tscartata per: carattere speciale iniziale sconosciuto");
				}
				return true;
			}
 
		}

		return false;

	}
	
	


}

package narod;

import java.util.Random;

import narod.Stanovnik.Pol;

public class StanovnikUtil {
	
	private static final int LEFT_CHAR_LIMIT = 97;
	private static final int RIGHT_CHAR_LIMIT = 122;
	private static final int NAME_LENGTH = 6;
	
	public static String generateString() {
		Random random = new Random();
		String generatedString = random.ints(LEFT_CHAR_LIMIT, RIGHT_CHAR_LIMIT)
				.limit(NAME_LENGTH)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		return generatedString;
	}
	
	public static Pol generatePol(){
		Random random = new Random();
		boolean polBoolean = random.nextBoolean();
		if(polBoolean)
			return Pol.MUSKI;
		else return Pol.ZENSKI;
	}
	
	public static double distanca(int x1, int x2, int y1, int y2) {
		int dx = (int)Math.pow((x1 - x2), 2);
		int dy = (int)Math.pow((y1 - y2), 2);
		return Math.sqrt((dx + dy));
	}
	
}

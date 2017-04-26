package vontus.magicbottle;

import java.text.NumberFormat;
import java.util.Locale;

public class Utils {

	public static String roundDouble(double number) {
		Double round = Math.round(number * 10) / 10d;
		return NumberFormat.getNumberInstance(Locale.US).format(round);
	}

	public static String roundInt(int number) {
		Double round = Math.round(number * 10) / 10d;
		return NumberFormat.getNumberInstance(Locale.US).format(round);
	}

	public static double stringToDouble(String toConvert) throws NumberFormatException {
		return Double.valueOf(toConvert.replace(",", ""));
	}
}
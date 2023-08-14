package uni.siegen.bgf.cardafit.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CommonUtil {
	
	public static boolean isNotNullOrEmpty(String value) {
		return value != null && !value.isBlank();
	}
	
	public static boolean isNullOrEmpty(String value) {
		return value == null || value.isBlank();
	}

	public static Map<String, String> getAlertPayloadData(int alertType) {
        Map<String, String> pushData = new HashMap<>();
        pushData.put("messageId", "" + LocalDateTime.now());
        pushData.put("text", "" + alertType);
        
        String message = "";
    	switch (alertType) {
		case 0:
			message = "Trinke jetzt ein Glas Wasser!";
			break;
		case 1:
			message = "Bleib nun zwei Minuten in Bewegung!";
			break;
		case 2:
			message = "Es ist Zeit für eine schnelle Übung!";
			break;
		case 3:
			message = "Lege nun eine zwei minütige Pause ein!";
			break;
		case 4:
			message = "Es ist Zeit für eine Teamübung!";
			break;
		case 5:
			message = "Machen Sie eine kurze Pause und trinken Sie auch ein Glas Wasser!";
			break;
		case 6:
			message = "Gehen Sie eine Weile spazieren und strecken Sie Ihre Hände ein wenig!";
			break;

		default:
			break;
		}
        
    	pushData.put("title", "'CardaFit Aufgabe'");
    	pushData.put("message", message);
    	
        return pushData;
    }
	
	/*
	 * Return 2 letter weekday name in German
	 */
	public static String getCurrentWeekDayName() {
    	String day = new SimpleDateFormat("EEE", Locale.GERMAN).format(new Date());
    	return isNotNullOrEmpty(day) && day.length() > 2 ? day.substring(0, 2) : "";
	}
}

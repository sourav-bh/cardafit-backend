package uni.siegen.bgf.cardafit.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CommonUtil {
	
	public static boolean isNotNullOrEmpty(String value) {
		return value != null && !value.isBlank();
	}

	public static Map<String, String> getAlertPayloadData(int alertType) {
        Map<String, String> pushData = new HashMap<>();
        pushData.put("messageId", "" + LocalDateTime.now());
        pushData.put("text", "" + alertType);
        return pushData;
    }
}

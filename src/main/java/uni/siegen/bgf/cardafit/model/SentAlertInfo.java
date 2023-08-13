package uni.siegen.bgf.cardafit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SentAlertInfo {
	private int alertType;
	private int sentCount;
	private long lastSentAt;
}

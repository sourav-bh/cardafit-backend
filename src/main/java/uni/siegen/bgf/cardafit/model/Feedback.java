package uni.siegen.bgf.cardafit.model;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Feedback {
    @Id 
    private String id;
	private String userId;
	private String feedbackText;

}

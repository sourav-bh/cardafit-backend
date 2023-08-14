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
public class Team {
	@Id 
	private String id;
	private String teamName;
	private boolean isActive;

}

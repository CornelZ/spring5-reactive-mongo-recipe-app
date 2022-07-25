package guru.springframework.commands;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class IngredientCommand {
  private String id;
  private String recipeId;
  private String description;
  private BigDecimal amount;
  private UnitOfMeasureCommand uom;
}

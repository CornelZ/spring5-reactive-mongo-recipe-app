package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

  private final RecipeReactiveRepository recipeReactiveRepository;
  private final RecipeCommandToRecipe recipeCommandToRecipe;
  private final RecipeToRecipeCommand recipeToRecipeCommand;

  public RecipeServiceImpl(
      RecipeReactiveRepository recipeReactiveRepository,
      RecipeCommandToRecipe recipeCommandToRecipe,
      RecipeToRecipeCommand recipeToRecipeCommand) {
    this.recipeReactiveRepository = recipeReactiveRepository;
    this.recipeCommandToRecipe = recipeCommandToRecipe;
    this.recipeToRecipeCommand = recipeToRecipeCommand;
  }

  @Override
  public Flux<Recipe> getRecipes() {
    log.debug("I'm in the service");
    return recipeReactiveRepository.findAll();
  }

  @Override
  public Mono<Recipe> findById(String id) {
    return recipeReactiveRepository.findById(id);
  }

  @Override
  public Mono<RecipeCommand> findCommandById(String id) {
    return findById(id) //
        .map(this::recipeCommandWithrecipeIdForIngredients);
  }

  private RecipeCommand recipeCommandWithrecipeIdForIngredients(Recipe recipe) {
    RecipeCommand recipeCommand = recipeToRecipeCommand.convert(recipe);
    recipeCommand.getIngredients().forEach(rc -> rc.setRecipeId(recipeCommand.getId()));
    return recipeCommand;
  }

  @Override
  public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
    Recipe detachedRecipe = recipeCommandToRecipe.convert(command);

    return recipeReactiveRepository.save(detachedRecipe).map(recipeToRecipeCommand::convert);
  }

  @Override
  public void deleteById(String idToDelete) {
    recipeReactiveRepository.deleteById(idToDelete).block();
  }
}

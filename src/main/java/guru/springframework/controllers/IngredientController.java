package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
public class IngredientController {

  private final IngredientService ingredientService;
  private final RecipeService recipeService;
  private final UnitOfMeasureService unitOfMeasureService;
  private WebDataBinder webDataBinder;

  public IngredientController(
      IngredientService ingredientService,
      RecipeService recipeService,
      UnitOfMeasureService unitOfMeasureService) {
    this.ingredientService = ingredientService;
    this.recipeService = recipeService;
    this.unitOfMeasureService = unitOfMeasureService;
  }

  @InitBinder("ingredient")
  public void initBinder(WebDataBinder webDataBinder) {
    this.webDataBinder = webDataBinder;
  }

  @GetMapping("/recipe/{recipeId}/ingredients")
  public String listIngredients(@PathVariable String recipeId, Model model) {
    log.debug("Getting ingredient list for recipe id: " + recipeId);

    model.addAttribute("recipe", recipeService.findCommandById(recipeId));

    return "recipe/ingredient/list";
  }

  @GetMapping("recipe/{recipeId}/ingredient/{id}/show")
  public String showRecipeIngredient(
      @PathVariable String recipeId, @PathVariable String id, Model model) {
    model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));
    return "recipe/ingredient/show";
  }

  @GetMapping("recipe/{recipeId}/ingredient/new")
  public String newRecipe(@PathVariable String recipeId, Model model) {

    // make sure we have a good id value
    // RecipeCommand recipeCommand =
    recipeService.findCommandById(recipeId).block();

    // need to return back parent id for hidden form property
    IngredientCommand ingredientCommand = new IngredientCommand();
    model.addAttribute("ingredient", ingredientCommand);

    // init uom
    ingredientCommand.setUom(new UnitOfMeasureCommand());

    return "recipe/ingredient/ingredientform";
  }

  @GetMapping("recipe/{recipeId}/ingredient/{id}/update")
  public String updateRecipeIngredient(
      @PathVariable String recipeId, @PathVariable String id, Model model) {
    model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));
    return "recipe/ingredient/ingredientform";
  }

  @PostMapping("recipe/{recipeId}/ingredient")
  public String saveOrUpdate(
      @ModelAttribute("ingredient") IngredientCommand command, @PathVariable String recipeId) {
    webDataBinder.validate();
    BindingResult bindingResult = webDataBinder.getBindingResult();

    if (bindingResult.hasErrors()) {

      bindingResult
          .getAllErrors()
          .forEach(objectError -> log.debug("objectError: {}", objectError));
      return "recipe/ingredient/ingredientform";
    }

    IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command).block();

    log.debug("saved ingredient id:" + savedCommand.getId());

    return "redirect:/recipe/"
        + savedCommand.getRecipeId()
        + "/ingredient/"
        + savedCommand.getId()
        + "/show";
  }

  @GetMapping("recipe/{recipeId}/ingredient/{id}/delete")
  public String deleteIngredient(@PathVariable String recipeId, @PathVariable String id) {

    log.debug("deleting ingredient id:" + id);
    ingredientService.deleteById(recipeId, id).block();

    return "redirect:/recipe/" + recipeId + "/ingredients";
  }

  @ModelAttribute("uomList")
  private Flux<UnitOfMeasureCommand> populateUomList() {
    return unitOfMeasureService.listAllUoms();
  }
}

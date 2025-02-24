package guru.springframework.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import reactor.core.publisher.Flux;

@Ignore
public class IndexControllerTest {

  @Mock RecipeService recipeService;

  @Mock Model model;

  IndexController controller;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    controller = new IndexController(recipeService);
  }

  @Test
  public void testMockMVC() throws Exception {
    MockMvc mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ControllerExceptionHandler())
            .build();

    when(recipeService.getRecipes()).thenReturn(Flux.empty());

    mockMvc //
        .perform(get("/")) //
        .andExpect(status().isOk()) //
        .andExpect(view().name("index"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void getIndexPage() throws Exception {

    // given
    Recipe recipe = new Recipe();
    recipe.setId("1");

    when(recipeService.getRecipes()).thenReturn(Flux.just(recipe, new Recipe()));

    ArgumentCaptor<List<Recipe>> argumentCaptor = ArgumentCaptor.forClass(List.class);

    // when
    String viewName = controller.getIndexPage(model);

    // then
    assertEquals("index", viewName);
    verify(recipeService, times(1)).getRecipes();
    verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());
    List<Recipe> setInController = argumentCaptor.getValue();
    assertEquals(2, setInController.size());
  }
}

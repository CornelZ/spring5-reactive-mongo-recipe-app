package guru.springframework.controllers;

import static org.mockito.Mockito.when;

import guru.springframework.config.WebConfig;
import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Flux;

public class RouterFunctionTest {

  WebTestClient webTestClient;

  @Mock RecipeService recipeService;

  @Before
  public void setUp() {

    MockitoAnnotations.initMocks(this);
    WebConfig webConfig = new WebConfig();
    RouterFunction<?> routerFunction = webConfig.routes(recipeService);
    webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
  }

  @Test
  public void testGetRecipes() {

    when(recipeService.getRecipes()).thenReturn(Flux.just());

    webTestClient
        .get()
        .uri("/api/recipes")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  public void testGetRecipesWithData() {

    when(recipeService.getRecipes()).thenReturn(Flux.just(new Recipe(), new Recipe()));

    webTestClient
        .get()
        .uri("/api/recipes")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Recipe.class);
  }
}

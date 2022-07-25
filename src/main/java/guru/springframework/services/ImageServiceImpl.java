package guru.springframework.services;

import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

/** Created by jt on 7/3/17. */
@Service
public class ImageServiceImpl implements ImageService {

  private final RecipeReactiveRepository recipeReactiveRepository;

  public ImageServiceImpl(RecipeReactiveRepository recipeReactiveRepository) {
    this.recipeReactiveRepository = recipeReactiveRepository;
  }

  @Override
  public Mono<Void> saveImageFile(String recipeId, MultipartFile file) {

    Mono<Recipe> recipeMono =
        recipeReactiveRepository //
            .findById(recipeId) //
            .map(
                recipe -> {
                  try {
                    Byte[] byteObjects;
                    byteObjects = new Byte[file.getBytes().length];
                    int i = 0;
                    for (byte b : file.getBytes()) {
                      byteObjects[i++] = b;
                    }

                    recipe.setImage(byteObjects);
                    return recipe;
                  } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                  }
                });
    recipeReactiveRepository.save(recipeMono.block());
    return Mono.empty();
  }
}

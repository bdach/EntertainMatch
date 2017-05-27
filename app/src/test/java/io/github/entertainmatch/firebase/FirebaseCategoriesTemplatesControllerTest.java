package io.github.entertainmatch.firebase;

import io.github.entertainmatch.model.Category;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the {@link FirebaseCategoriesTemplatesController} class.
 *
 * @author Bartlomiej Dach
 * @since 27.05.17
 */
public class FirebaseCategoriesTemplatesControllerTest extends AbstractFirebaseControllerTest {
    @Test
    public void getImageForCategory() {
        // given
        Category category = new Category(
                "category",
                "test"
        );
        FirebaseCategoriesTemplatesController.cachedMap.put("category", category);
        // when
        String image = FirebaseCategoriesTemplatesController.getImageForCategory("category");
        // then
        assertThat(image).isEqualTo("test");
    }
}

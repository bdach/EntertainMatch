package io.github.entertainmatch.firebase.model;

import io.github.entertainmatch.firebase.models.FirebaseCategoryTemplate;
import io.github.entertainmatch.model.Category;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Bartlomiej Dach
 * @since 28.05.17
 */
public class FirebaseCategoryTemplateTest {
    @Test
    public void toCategory() {
        // given
        FirebaseCategoryTemplate categoryTemplate = new FirebaseCategoryTemplate(
                "category",
                "url",
                "id"
        );
        // when
        Category category = categoryTemplate.toCategory();
        // then
        assertThat(category.getId()).isEqualTo("id");
        assertThat(category.getImageUrl()).isEqualTo("url");
        assertThat(category.getName()).isEqualTo("category");
        assertThat(category.getVoteCount()).isEqualTo(0);
        assertThat(category.isVotedFor()).isFalse();
    }
}

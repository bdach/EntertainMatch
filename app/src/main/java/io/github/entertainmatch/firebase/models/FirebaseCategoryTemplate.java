package io.github.entertainmatch.firebase.models;

import io.github.entertainmatch.model.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Provides predefined category types stored in Firebase.
 *
 * @author Adrian Bednarz
 * @since 5/5/17.
 */
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseCategoryTemplate {
    /**
     * User-friendly name of category.
     */
    @Getter
    public String name;

    /**
     * URL to category image.
     */
    @Getter
    public String imageUrl;

    /**
     * Category identifier.
     */
    @Getter
    public String id;

    /**
     * @return Constructs application Category from Firebase template.
     */
    public Category toCategory() {
        return new Category(name, 0, false, imageUrl, id);
    }
}

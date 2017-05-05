package io.github.entertainmatch.firebase.models;

import io.github.entertainmatch.model.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Adrian Bednarz on 5/5/17.
 *
 * Provides predefined category types stored in firebase.
 */
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseCategoryTemplate {
    /**
     * User-friendly name of category
     */
    @Getter
    public String name;

    /**
     * Url to theme image
     */
    @Getter
    public String imageUrl;

    /**
     * Identifier of a category
     */
    @Getter
    public int id;

    /**
     * @return Constructs application Category from firebase template
     */
    public Category toCategory() {
        return new Category(name, 0, false, imageUrl);
    }
}

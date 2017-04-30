package io.github.entertainmatch.firebase.models;

import java.util.List;

import io.github.entertainmatch.model.Person;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Adrian Bednarz on 4/30/17.
 *
 * Represents user information in Firebase.
 */
@NoArgsConstructor
public class FirebasePerson {
    @Getter
    private List<String> pollIds;
}

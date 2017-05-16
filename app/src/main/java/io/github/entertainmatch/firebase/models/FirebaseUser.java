package io.github.entertainmatch.firebase.models;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Adrian Bednarz on 4/30/17.
 *
 * Represents user information in Firebase.
 */
@NoArgsConstructor
public class FirebaseUser {
    @Getter
    private Map<String, Boolean> polls = new HashMap<>();
    @Getter
    private Map<String, Boolean> events = new HashMap<>();
}

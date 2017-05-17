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
@Getter
public class FirebaseUser {
    private Map<String, Boolean> polls = new HashMap<>();
    private Map<String, Boolean> events = new HashMap<>();
    private Map<String, Boolean> dates = new HashMap<>();
    private Map<String, Boolean> finished = new HashMap<>();
}

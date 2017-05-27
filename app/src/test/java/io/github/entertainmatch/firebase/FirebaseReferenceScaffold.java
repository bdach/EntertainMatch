package io.github.entertainmatch.firebase;

import com.google.firebase.database.DatabaseReference;
import lombok.RequiredArgsConstructor;
import org.mockito.Mockito;

import java.util.LinkedList;

/**
 * @author Bartlomiej Dach
 * @since 27.05.17
 */
@RequiredArgsConstructor
public class FirebaseReferenceScaffold {
    private final LinkedList<DatabaseReference> referenceList;

    public static FirebaseReferenceScaffold from(DatabaseReference databaseReference) {
        LinkedList<DatabaseReference> referenceList = new LinkedList<>();
        referenceList.add(databaseReference);
        return new FirebaseReferenceScaffold(referenceList);
    }

    public FirebaseReferenceScaffold child(String key) {
        DatabaseReference reference = Mockito.mock(DatabaseReference.class);
        DatabaseReference lastReference = referenceList.getLast();
        Mockito.when(lastReference.child(key))
                .thenReturn(reference);
        referenceList.addLast(reference);
        return this;
    }

    public DatabaseReference finish() {
        return referenceList.getLast();
    }
}

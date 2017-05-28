package io.github.entertainmatch.firebase;

import android.provider.ContactsContract;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import lombok.AccessLevel;
import lombok.Getter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Abstract base class for all controllers utilising {@link FirebaseDatabase}
 * and {@link DatabaseReference} instances.
 *
 * @author Bartlomiej Dach
 * @since 27.05.17
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FirebaseDatabase.class)
public abstract class AbstractFirebaseControllerTest {

    private static FirebaseDatabase database = Mockito.mock(FirebaseDatabase.class);
    @Getter(AccessLevel.MODULE)
    private static DatabaseReference reference;

    @BeforeClass
    public static void setUp() {
        reference = Mockito.mock(DatabaseReference.class);
        Mockito.when(database.getReference())
                .thenReturn(reference);
        Mockito.when(database.getReference(Mockito.anyString()))
                .thenReturn(reference);
        PowerMockito.mockStatic(FirebaseDatabase.class);
        Mockito.when(FirebaseDatabase.getInstance())
                .thenReturn(database);
    }
}

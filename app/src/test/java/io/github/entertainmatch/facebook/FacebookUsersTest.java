package io.github.entertainmatch.facebook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import io.github.entertainmatch.model.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the {@link FacebookUsers} class.
 *
 * @author Bartlomiej Dach
 * @since 27.05.17
 */
@RunWith(MockitoJUnitRunner.class)
public class FacebookUsersTest {
    @Mock
    private Context context;
    @Mock
    private SharedPreferences sharedPreferences;
    @Mock
    private SharedPreferences.Editor editor;
    private HashMap<String, String> backingMap;

    @SuppressLint({"CommitPrefEdits", "NewApi"})
    @Before
    public void setUp() {
        FacebookUsers.currentUser = null;
        // absolutely disgusting
        Mockito.when(context.getApplicationContext()).thenReturn(context);
        Mockito.when(context.getSharedPreferences(
                Mockito.anyString(), Mockito.anyInt()
        )).thenReturn(sharedPreferences);
        Mockito.when(sharedPreferences.edit()).thenReturn(editor);
        // setup editor to use backing map
        backingMap = new HashMap<>();
        Mockito.when(editor.putString(Mockito.anyString(), Mockito.anyString()))
                .then(invocation -> {
                    String key = invocation.getArgument(0);
                    String value = invocation.getArgument(1);
                    backingMap.put(key, value);
                    return editor;
                });
        Mockito.when(sharedPreferences.getString(Mockito.anyString(), Mockito.anyString()))
                .then(invocation -> {
                    String key = invocation.getArgument(0);
                    String defaultValue = invocation.getArgument(1);
                    return backingMap.getOrDefault(key, defaultValue);
                });
        Mockito.when(editor.remove(Mockito.anyString()))
                .then(invocation -> {
                    String key = invocation.getArgument(0);
                    backingMap.remove(key);
                    return editor;
                });
    }

    @Test
    public void getCurrentUser_returnsNullWhenNoUser() {
        // given
        // when
        Person currentUser = FacebookUsers.getCurrentUser(context);
        // then
        assertThat(currentUser).isNull();
    }

    @Test
    public void getCurrentUser_returnsNullIfStoredUserInvalid() {
        // given
        Person person = new Person();
        person.name = "John Smith";
        // when
        FacebookUsers.setCurrentUser(context, person);
        FacebookUsers.currentUser = null;
        Person currentUser = FacebookUsers.getCurrentUser(context);
        // then
        assertThat(currentUser).isNull();
    }

    @Test
    public void getCurrentUser_returnsCachedUser() {
        // given
        Person person = new Person();
        person.facebookId = "111122223333";
        FacebookUsers.currentUser = person;
        // when
        Person currentUser = FacebookUsers.getCurrentUser(context);
        // then
        assertThat(currentUser).isEqualTo(person);
    }

    @Test
    public void setCurrentUser_getterRetrievesUser() {
        // given
        Person person = new Person();
        person.facebookId = "123456789";
        person.name = "John Smith";
        // when
        FacebookUsers.setCurrentUser(context, person);
        Person currentUser = FacebookUsers.getCurrentUser(context);
        // then
        assertThat(currentUser).isEqualToComparingFieldByField(person);
    }

    @Test
    public void setCurrentUser_idempotentWithRemove() {
        // given
        Person person = new Person();
        person.facebookId = "123456789";
        person.name = "John Smith";
        // when
        FacebookUsers.setCurrentUser(context, person); // user was saved into shared prefs
        FacebookUsers.removeCurrentUser(context); // user should be removed here
        // then
        assertThat(backingMap).isEmpty();
    }
}

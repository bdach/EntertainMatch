package io.github.entertainmatch.facebook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import com.facebook.GraphRequest;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the {@link FriendsProvider} class.
 *
 * @author Bartlomiej Dach
 * @since 27.05.17
 */
public class FriendsProviderTest {
    /*
     * Note:
     * I'd love to actually _execute_ any of these tests, but I need a Context
     * to initialize the API. I'll do that in an instrumentation test.
     */

    @Test
    public void getFriendsList() {
        // given
        // when
        GraphRequest request = FriendsProvider.getFriendsList(null, null);
        // then
        Bundle parameters = request.getParameters();
        String fields = parameters.getString("fields");
        assertThat(fields).contains("name", "id", "picture");
    }

    @SuppressLint("NewApi")
    @Test
    public void getFriendsById() {
        // given
        List<String> ids = Arrays.asList("123", "456", "789");
        // when
        List<GraphRequest> friendsById = FriendsProvider.getFriendsById(ids, null, null);
        // then
        assertThat(friendsById.size()).isEqualTo(ids.size());
    }
}

package io.github.entertainmatch.view;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import io.github.entertainmatch.view.MainActivity;
import io.github.entertainmatch.view.NavigationHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static android.app.Activity.RESULT_OK;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Bartlomiej Dach
 * @since 29.05.17
 */
@RunWith(MockitoJUnitRunner.class)
public class NavigationHelperTest {
    @Mock
    private AppCompatActivity targetActivity;
    @Mock
    private ComponentName callingActivityName;
    @Captor
    private ArgumentCaptor<Intent> intentCaptor;

    @Test
    public void back_callingActivity() {
        // given
        Mockito.when(targetActivity.getCallingActivity())
                .thenReturn(callingActivityName);
        // when
        NavigationHelper.back(targetActivity, "pollId");
        // then
        Mockito.verify(targetActivity).setResult(
                Mockito.eq(RESULT_OK),
                intentCaptor.capture()
        );
        Mockito.verify(targetActivity, Mockito.times(0))
                .startActivity(Mockito.any(Intent.class));
        Intent intent = intentCaptor.getValue();
        String pollId = intent.getStringExtra(MainActivity.STAGE_FINISHED_POLL_ID_KEY);
        assertThat(pollId).isEqualTo("pollId");
    }

    @Test
    public void back_noCallingActivity() {
        // given
        Mockito.when(targetActivity.getCallingActivity())
                .thenReturn(null);
        // when
        NavigationHelper.back(targetActivity, "anotherPollId");
        // then
        Mockito.verify(targetActivity).startActivity(intentCaptor.capture());
        Intent intent = intentCaptor.getValue();
        ComponentName component = intent.getComponent();
        assertThat(component.getPackageName()).isEqualTo(targetActivity.getPackageName());
        assertThat(component.getClassName()).isEqualTo(MainActivity.class.getName());
        Mockito.verify(targetActivity, Mockito.times(0))
                .setResult(Mockito.anyInt(), Mockito.any(Intent.class));
    }
}

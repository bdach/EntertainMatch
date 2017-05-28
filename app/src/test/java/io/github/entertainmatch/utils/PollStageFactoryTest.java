package io.github.entertainmatch.utils;

import io.github.entertainmatch.model.PollStage;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.model.VoteDateStage;
import io.github.entertainmatch.model.VoteEventStage;
import io.github.entertainmatch.model.VoteResultStage;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Bartlomiej Dach
 * @since 28.05.17
 */
@RunWith(Parameterized.class)
@RequiredArgsConstructor
public class PollStageFactoryTest {
    @Parameterized.Parameters
    public static Collection<Object[]> get() {
        return Arrays.asList(new Object[][] {
                {"categories", VoteCategoryStage.class},
                {"events", VoteEventStage.class},
                {"dates", VoteDateStage.class},
                {"results", VoteResultStage.class}
        });
    }

    private final String pollId;
    private final Class<? extends PollStage> targetClass;

    @Test
    public void test() {
        // given
        // when
        PollStage pollStage = PollStageFactory.get(targetClass.toString(), pollId);
        // then
        assertThat(pollStage).isInstanceOf(targetClass);
    }
}

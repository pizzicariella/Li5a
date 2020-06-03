package de.htw.ai.ema;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import de.htw.ai.ema.gui.PlayGameActivity;
import de.htw.ai.ema.model.Card;
import de.htw.ai.ema.model.Rank;
import de.htw.ai.ema.model.Suit;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.core.internal.deps.dagger.internal.Preconditions.checkNotNull;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PlayGameActivityTest {

    @Rule
    public ActivityTestRule<PlayGameActivity> activityRule = new ActivityTestRule<>(PlayGameActivity.class);

    @Test
    public void elementsAreDisplayed(){
        onView(withId(R.id.activity_play_game)).check(matches(isDisplayed()));
        onView(withId(R.id.recycler_view_hand_cards)).check(matches(isDisplayed()));
        //onView(withId(R.id.image_view_stack)).check(matches(isDisplayed()));
        //TODO check also for other players, cancel button etc
    }

    @Test
    public void testRecyclerViewHandCards(){
        PlayGameActivity.addImage(new Card(Suit.CLUBS, Rank.ACE));
        onView(withId(R.id.recycler_view_hand_cards)).check(matches(atPosition(0, isDisplayed())));
        onView(withId(R.id.recycler_view_hand_cards)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.recycler_view_hand_cards)).check(matches(atPosition(0,
                new DrawableMatcher(R.drawable.highlight_card_frame))));
    }


    //TODO
    @Test
    public void testStackImageView(){
        fail("Not yet implemented");
    }

    //TODO
    @Test
    public void testCancelButton(){
        fail("Not yet implemented");
    }

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    public class DrawableMatcher extends TypeSafeMatcher<View> {

        private final int expectedId;
        String resourceName;

        public DrawableMatcher(int expectedId) {
            super(View.class);
            this.expectedId = expectedId;
        }

        @Override
        protected boolean matchesSafely(View target) {
            if (!(target instanceof ImageView)) {
                return false;
            }
            ImageView imageView = (ImageView) target;
            if (expectedId < 0) {
                return imageView.getDrawable() == null;
            }
            Resources resources = target.getContext().getResources();
            Drawable expectedDrawable = resources.getDrawable(expectedId);
            resourceName = resources.getResourceEntryName(expectedId);

            if (expectedDrawable == null) {
                return false;
            }

            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            Bitmap otherBitmap = ((BitmapDrawable) expectedDrawable).getBitmap();
            return bitmap.sameAs(otherBitmap);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("with drawable from resource id: ");
            description.appendValue(expectedId);
            if (resourceName != null) {
                description.appendText("[");
                description.appendText(resourceName);
                description.appendText("]");
            }
        }
    }
}

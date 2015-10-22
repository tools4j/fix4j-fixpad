package org.fix4j.pad;

import com.google.common.base.Preconditions;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextInputControl;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.loadui.testfx.GuiTest;

/**
 * User: ben
 * Date: 21/10/15
 * Time: 5:17 PM
 */
public class ContainsStringMatcher extends TypeSafeMatcher<Object> {
    private final String substring;

    @Factory
    public static Matcher<Object> containsText(String text) {
        return new ContainsStringMatcher(text);
    }

    public ContainsStringMatcher(String substring) {
        this.substring = substring;
    }

    @Override
    public void describeTo(Description desc) {
        desc.appendText("Node should have substring " + this.substring);
    }

    @Override
    public boolean matchesSafely(Object target) {
        if(target instanceof String) {
            return this.nodeHasLabel(GuiTest.find((String) target));
        } else if(target instanceof Labeled){
            return this.nodeHasLabel((Labeled)target);
        } else {
            return false;
        }
    }

    private boolean nodeHasLabel(Node node) {
        Preconditions.checkArgument(node instanceof Labeled || node instanceof TextInputControl, "Target node must be Labeled or TextInputControl.");
        if(node instanceof Labeled) {
            Labeled textInput1 = (Labeled) node;
            return textInput1.getText().contains(this.substring);
        } else {
            TextInputControl textInput = (TextInputControl)node;
            return textInput.getText().contains(substring);
        }
    }
}
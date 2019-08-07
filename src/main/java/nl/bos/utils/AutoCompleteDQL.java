package nl.bos.utils;

import impl.org.controlsfx.skin.AutoCompletePopup;
import impl.org.controlsfx.skin.AutoCompletePopupSkin;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiFunction;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.stage.PopupWindow;

import org.fxmisc.richtext.CodeArea;
import org.reactfx.EventStream;

import static org.reactfx.EventStreams.nonNullValuesOf;

public class AutoCompleteDQL {

	CodeArea codeArea;
	AutoCompletePopup<String> popup;

	/**
	 * 
	 * @param codeArea When a suggestion entered, which range of text should be
	 *                 replaced.
	 */
	public AutoCompleteDQL(CodeArea codeArea) {
		this.codeArea = codeArea;
		this.popup = new AutoCompletePopup<>();

		codeArea.textProperty().addListener(textChangeListener);
		codeArea.focusedProperty().addListener(focusChangedListener);

		popup.setOnSuggestion(sce -> {
			completeUserInput(sce.getSuggestion());
			hidePopup();
		});
	}

	public void showPopup() {
		Collection<String> suggestions = getSuggestion(codeArea.getText(), codeArea.getCaretPosition());
		if (suggestions.isEmpty()) {
			hidePopup();
		} else {
			popup.hide();
			EventStream<Optional<Bounds>> caretBounds = nonNullValuesOf(codeArea.caretBoundsProperty());
			caretBounds.subscribe(b -> {
				Optional<Bounds> bounds = b;
				popup.setX(bounds.get().getMaxX());
				popup.setX(bounds.get().getMaxY());
			});
		}
		popup.getSuggestions().setAll(suggestions);
		selectFirstSuggestion(popup);
		if (!popup.isShowing()) {
			popup.show(codeArea.getScene().getWindow());
		}
	}

	public void hidePopup() {
		popup.hide();
	}

	private void completeUserInput(String suggestion) {
		IndexRange range = getReplaceRange(codeArea.getText(), codeArea.getCaretPosition());
		codeArea.deleteText(range);
		codeArea.insertText(range.getStart(), suggestion);
		codeArea.moveTo(range.getStart() + suggestion.length());
	}

	private final ChangeListener<String> textChangeListener = (obs, oldText, newText) -> {
		if (codeArea.isFocused() && popup.isShowing()) {
			showPopup();
		}
	};

	private final ChangeListener<Boolean> focusChangedListener = (obs, oldFocused, newFocused) -> {
		if (newFocused == false) {
			hidePopup();
		}
	};

	private static void selectFirstSuggestion(AutoCompletePopup<?> autoCompletionPopup) {
		Skin<?> skin = autoCompletionPopup.getSkin();
		if (skin instanceof AutoCompletePopupSkin) {
			AutoCompletePopupSkin<?> au = (AutoCompletePopupSkin<?>) skin;
			ListView<?> li = (ListView<?>) au.getNode();
			if (li.getItems() != null && !li.getItems().isEmpty()) {
				li.getSelectionModel().select(0);
			}
		}
	}

	private static Collection<String> getSuggestion(String text, int caretPos) {
		//return getSuggestion(text, caretPos);
		return Collections.emptyList();
	}
	
	  public static IndexRange getReplaceRange(String text, int caretPos) {
		    return new IndexRange(0, caretPos);
		  }
}
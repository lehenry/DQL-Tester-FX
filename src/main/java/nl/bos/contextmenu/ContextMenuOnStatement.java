package nl.bos.contextmenu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import nl.bos.contextmenu.menuitem.action.MenuItemInsertTemplateFieldAction;
import org.fxmisc.richtext.CodeArea;

public class ContextMenuOnStatement {
    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem insertTemplateField;
    private final CodeArea statement;

    public ContextMenuOnStatement(CodeArea statement) {
        this.statement = statement;
        insertTemplateField = new MenuItem("Insert template field");
        insertTemplateField.setDisable(true);
        new MenuItemInsertTemplateFieldAction(insertTemplateField, statement);

        contextMenu.getItems().addAll(insertTemplateField);
    }

    public void onRightMouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            contextMenu.hide();
        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            validateMenuItems();
            contextMenu.show(statement, mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    }

    private void validateMenuItems() {
        insertTemplateField.setDisable(false);
    }
}

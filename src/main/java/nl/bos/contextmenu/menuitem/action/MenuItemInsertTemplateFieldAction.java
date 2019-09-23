package nl.bos.contextmenu.menuitem.action;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import nl.bos.actions.InsertTemplateFieldAction;
import org.fxmisc.richtext.CodeArea;

import java.util.logging.Logger;

public class MenuItemInsertTemplateFieldAction implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(MenuItemInsertTemplateFieldAction.class.getName());

    private final CodeArea statement;

    public MenuItemInsertTemplateFieldAction(MenuItem insertTemplateField, CodeArea statement) {
        this.statement = statement;
        insertTemplateField.setOnAction(this);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        LOGGER.info(actionEvent.getSource().toString());
        LOGGER.info(statement.getText());

        new InsertTemplateFieldAction(statement.getText());
    }
}

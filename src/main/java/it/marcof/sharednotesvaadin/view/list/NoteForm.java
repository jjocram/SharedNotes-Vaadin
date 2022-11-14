package it.marcof.sharednotesvaadin.view.list;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import it.marcof.sharednotesvaadin.data.entity.Note;
import it.marcof.sharednotesvaadin.data.entity.UserEntity;
import it.marcof.sharednotesvaadin.data.service.NoteService;
import it.marcof.sharednotesvaadin.security.SecurityService;

import java.util.List;

public class NoteForm extends FormLayout {
    TextField name = new TextField("Title");
    TextArea content = new TextArea("Note's content");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Close");

    Dialog addEditorDialog = new Dialog();
    Button addEditorButton = new Button("Add editor", e -> addEditorDialog.open());

    Binder<Note> binder = new BeanValidationBinder<>(Note.class);
    private Note note;
    private NoteService noteService;
    private SecurityService securityService;


    public NoteForm(List<UserEntity> editors, NoteService noteService, SecurityService securityService) {
        this.noteService = noteService;
        this.securityService = securityService;

        binder.bindInstanceFields(this);

        addClassName("note-form");

        configureDialog();

        add(addEditorDialog, name, content, getButtonsLayout());
    }

    private void configureDialog() {
        addEditorDialog.setHeaderTitle("Add new editor");

        TextField editorUsername = new TextField("Username");
        VerticalLayout dialogLayout = new VerticalLayout(editorUsername);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        addEditorDialog.add(dialogLayout);

        Button addButton = new Button("Add", e -> {
            noteService.addEditor(note, editorUsername.getValue());
            addEditorDialog.close();
        });
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> addEditorDialog.close());

        addEditorDialog.getFooter().add(cancelButton);
        addEditorDialog.getFooter().add(addButton);
    }

    private HorizontalLayout getButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addEditorButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, note)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, addEditorButton, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(note);
            fireEvent(new SaveEvent(this, note));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public void setNote(Note note) {
        this.note = note;
        //FIXME: change into something more clear
        if (note != null) {
            if (note.getOwner() == null) {
                save.setEnabled(true);
                save.setVisible(true);

                delete.setEnabled(false);
                delete.setVisible(false);

                addEditorButton.setEnabled(false);
                addEditorButton.setVisible(false);

                close.setEnabled(true);
                close.setVisible(true);
            } else if (note.getOwner().getUsername().equals(securityService.getAuthenticatedUser().getUsername())) {
                save.setEnabled(true);
                save.setVisible(true);

                delete.setEnabled(true);
                delete.setVisible(true);

                addEditorButton.setEnabled(true);
                addEditorButton.setVisible(true);

                close.setEnabled(true);
                close.setVisible(true);
            } else {
                save.setEnabled(true);
                save.setVisible(true);

                delete.setEnabled(false);
                delete.setVisible(false);

                addEditorButton.setEnabled(false);
                addEditorButton.setVisible(false);

                close.setEnabled(true);
                close.setVisible(true);
            }
        } else {
            save.setEnabled(false);
            save.setVisible(false);

            delete.setEnabled(false);
            delete.setVisible(false);

            addEditorButton.setEnabled(false);
            addEditorButton.setVisible(false);

            close.setEnabled(true);
            close.setVisible(true);
        }
        binder.readBean(note);
    }


    public static abstract class NoteFormEvent extends ComponentEvent<NoteForm> {
        private Note note;

        protected NoteFormEvent(NoteForm source, Note note) {
            super(source, false);
            this.note = note;
        }

        public Note getNote() {
            return note;
        }
    }

    public static class SaveEvent extends NoteFormEvent {
        SaveEvent(NoteForm source, Note note) {
            super(source, note);
        }
    }

    public static class DeleteEvent extends NoteFormEvent {
        DeleteEvent(NoteForm source, Note note) {
            super(source, note);
        }
    }

    public static class CloseEvent extends NoteFormEvent {
        CloseEvent(NoteForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}

package it.marcof.sharednotesvaadin.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.marcof.sharednotesvaadin.data.entity.Note;
import it.marcof.sharednotesvaadin.data.service.NoteService;
import it.marcof.sharednotesvaadin.security.SecurityService;
import it.marcof.sharednotesvaadin.view.list.NoteForm;

import javax.annotation.security.PermitAll;
import java.util.Collections;


@PermitAll // Permit to all AUTHENTICATED users
@PageTitle("Notes | SharedNotes")
@Route(value = "", layout = MainLayout.class)
public class ListView extends VerticalLayout {
    Grid<Note> grid = new Grid<>(Note.class);
    TextField filterText = new TextField();
    NoteForm form;

    NoteService noteService;
    SecurityService securityService;

    public ListView(NoteService noteService, SecurityService securityService) {
        this.noteService = noteService;
        this.securityService = securityService;

        addClassName("list-view");
        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());

        updateList();

        closeEditor();
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        form = new NoteForm(Collections.emptyList(), noteService, securityService);
        form.setWidth("50%");

        form.addListener(NoteForm.SaveEvent.class, this::saveNote);
        form.addListener(NoteForm.DeleteEvent.class, this::deleteNote);
        form.addListener(NoteForm.CloseEvent.class, e -> closeEditor());

    }

    private void configureGrid() {
        grid.addClassName("notes-grid");
        grid.setSizeFull();

        grid.setColumns("name"); // automatically gets the right properties
        grid.addColumn(note -> {
            if (note.getOwner().getUsername().equals(securityService.getAuthenticatedUser().getUsername())){
                return "You";
            } else {
                return note.getOwner().getUsername();
            }
        }).setHeader("Owner"); // get nested attributes and set header for them

        grid.getColumns().forEach(noteColumn -> noteColumn.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> editNote(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addNoteButton = new Button("Create note");
        addNoteButton.addClickListener(click -> addNote());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addNoteButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    public void updateList() {
        grid.setItems(noteService.findAllNotes(filterText.getValue(), securityService.getAuthenticatedUser().getUsername()));
    }

    public void editNote(Note note) {
        if (note == null) {
            closeEditor();
        } else {
            form.setNote(note);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setNote(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addNote() {
        grid.asSingleSelect().clear();
        editNote(new Note());
    }

    private void saveNote(NoteForm.SaveEvent event) {
        if (event.getNote().getOwner() == null) {
            noteService.saveNewNote(event.getNote(), securityService.getAuthenticatedUser().getUsername());
        } else {
            noteService.saveNote(event.getNote());
        }
        updateList();
        closeEditor();
    }

    private void deleteNote(NoteForm.DeleteEvent event) {
        noteService.deleteNote(event.getNote());
        updateList();
        closeEditor();
    }
}

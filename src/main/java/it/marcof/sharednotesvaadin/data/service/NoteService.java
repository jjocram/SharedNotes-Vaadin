package it.marcof.sharednotesvaadin.data.service;

import it.marcof.sharednotesvaadin.data.entity.Note;
import it.marcof.sharednotesvaadin.data.entity.UserEntity;
import it.marcof.sharednotesvaadin.data.repository.NoteRepository;
import it.marcof.sharednotesvaadin.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public List<Note> findAllNotes(String stringFilter, String username) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return noteRepository.findAll().stream()
                    .filter(note -> note.userCanAccess(username))
                    .collect(Collectors.toList());
        } else {
            return noteRepository.findAllByName(stringFilter).stream()
                    .filter(note -> note.userCanAccess(username))
                    .collect(Collectors.toList());
        }
    }

    public void deleteNote(Note note) {
        noteRepository.delete(note);
    }

    public void saveNewNote(Note note, String ownerUsername) {
        if (note == null) {
            log.error("Note is null. Check the connection between form and application");
            return;
        }

        UserEntity owner = userRepository.findByUsername(ownerUsername);
        note.setOwner(owner);

        noteRepository.save(note);
    }

    public void saveNote(Note note) {
        noteRepository.save(note);
    }

    public void addEditor(Note note, String editorUsername) {
        if (note == null) {
            log.error("Note is null. Check the connection between form and application");
            return;
        }

        UserEntity newEditor = userRepository.findByUsername(editorUsername);
        //TODO: check if editor exists
        newEditor.addNoteAsEditor(note);
        userRepository.save(newEditor);
        noteRepository.save(note);
    }
}

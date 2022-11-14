package it.marcof.sharednotesvaadin.data.entity;

import lombok.*;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends AbstractEntity {
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "owner")
    @ToString.Exclude
    private List<Note> ownedNotes = new LinkedList<>();

    @ManyToMany(targetEntity = Note.class, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Note> editorNotes = new LinkedList<>();

    public void addNoteAsEditor(Note note) {
        editorNotes.add(note);
    }

}

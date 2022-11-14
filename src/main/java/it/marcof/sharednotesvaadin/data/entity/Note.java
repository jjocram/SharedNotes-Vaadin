package it.marcof.sharednotesvaadin.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Note extends AbstractEntity {
    @Column(nullable = false)
    private String name;

    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    @JsonIgnoreProperties({"owned_notes"})
    private UserEntity owner;

    @ManyToMany(targetEntity = UserEntity.class, mappedBy = "editorNotes", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<UserEntity> editors = new ArrayList<>();

    public boolean userCanAccess(String username) {
        return owner.getUsername().equals(username) || editors.stream().anyMatch(user -> user.getUsername().equals(username));
    }
}

package it.marcof.sharednotesvaadin.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
public class AbstractEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity)) {
            return false;
        }

        AbstractEntity other = (AbstractEntity) obj;

        if (id != null) {
            return id.equals(other.id);
        }

        return super.equals(other);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }

        return super.hashCode();
    }
}

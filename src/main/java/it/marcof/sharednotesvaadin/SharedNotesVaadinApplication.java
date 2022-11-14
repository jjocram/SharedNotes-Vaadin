package it.marcof.sharednotesvaadin;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import it.marcof.sharednotesvaadin.data.entity.Note;
import it.marcof.sharednotesvaadin.data.entity.UserEntity;
import it.marcof.sharednotesvaadin.data.service.NoteService;
import it.marcof.sharednotesvaadin.data.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
@PWA(name = "Shared Notes", shortName = "SN", offlineResources = {})
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class SharedNotesVaadinApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(SharedNotesVaadinApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserService userService, NoteService noteService) {
        return args -> {
            File db = new File("./data.mv.db");
            if (!db.exists()) {
                UserEntity marcof = new UserEntity("marcof", "1234", null, null);
                UserEntity testUser = new UserEntity("test", "1234", null, null);

                userService.save(marcof);
                userService.save(testUser);

                Note note = new Note("Test", "Prova", null, null);
                noteService.saveNewNote(note, "marcof");
                noteService.addEditor(note, "test");
            } else {
                System.out.println("DB already exists");
            }

        };
    }

}

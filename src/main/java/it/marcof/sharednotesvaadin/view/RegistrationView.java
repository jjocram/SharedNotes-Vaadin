package it.marcof.sharednotesvaadin.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import it.marcof.sharednotesvaadin.data.entity.UserEntity;
import it.marcof.sharednotesvaadin.data.service.UserService;

@AnonymousAllowed
@Route("register")
@PageTitle("Register | Shared Notes")
public class RegistrationView extends FormLayout {

    private TextField username = new TextField("Username");
    private PasswordField password = new PasswordField("Password");
    private Button submit = new Button("Signup", e -> register());

    private UserService userService;

    public RegistrationView(UserService userService) {
        this.userService = userService;

        addClassName("login-view");
        setSizeFull();

        add(new VerticalLayout(new H1("Register"), username, password, submit));
    }

    public void register() {
        userService.save(new UserEntity(username.getValue(), password.getValue(), null, null));
        UI.getCurrent().navigate("login");
    }
}

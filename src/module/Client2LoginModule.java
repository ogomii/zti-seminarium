package src.module;

import java.util.*;
import java.io.IOException;
import javax.security.auth.*;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import javax.security.auth.spi.*;
import java.security.Principal;
import com.sun.security.auth.UserPrincipal;

public class Client2LoginModule implements LoginModule {
    
    private static final String PASSWORD = "testPassword";

    private String username;
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;

    private boolean loginSucceeded = false;
    private Principal userPrincipal;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
                           Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
    }

    @Override
    public boolean login() throws LoginException {
        PasswordCallback passwordCallback = new PasswordCallback("password: ", false);
        username = "unknownUser";

        try {
            callbackHandler.handle(new Callback[]{passwordCallback});
            String password = new String(passwordCallback.getPassword());
            if (PASSWORD.equals(password)) {
                loginSucceeded = true;
            }
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException("Can't login");
        }

        return loginSucceeded;
    }

    @Override
    public boolean commit() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }
        userPrincipal = new UserPrincipal(username);
        if (!subject.getPrincipals().contains(userPrincipal))
            subject.getPrincipals().add(userPrincipal);
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        logout();
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        subject.getPrincipals().remove(userPrincipal);
        return false;
    }
}

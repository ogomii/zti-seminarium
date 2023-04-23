package src;

import java.io.*;
import java.util.*;
import java.security.AccessControlException;
import java.security.Principal;
import java.security.PrivilegedAction;
import javax.security.auth.*;
import javax.security.auth.callback.*;
import javax.security.auth.login.*;
import javax.security.auth.spi.*;
import com.sun.security.auth.*;
import src.actions.*;

public class Jaas{
    public static void main(String[] args){

        LoginContext lc = null;
        try {
            lc = new LoginContext("JaasLogin", new LoginCallbackHandler());
        } catch (LoginException le) {
            System.err.println("Cannot create LoginContext. " + le.getMessage());
            System.exit(-1);
        } catch (Exception e){
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        // the user has 3 attempts to authenticate successfully
        int authenticationAttempts = 0;
        for (; authenticationAttempts< 3; authenticationAttempts++) {
            try {

                // attempt authentication
                lc.login();

                // if we return with no exception, authentication succeeded
                break;

            } catch (LoginException le) {

                  System.err.println("Authentication failed: " + le.getMessage());
                  try {
                      Thread.currentThread().sleep(3000);
                  } catch (Exception e) {
                      // ignore
                  }
            }
        }

        if(authenticationAttempts == 3){
            System.out.println("Authentication failed 3 times, stopping application...");
            System.exit(-1);
        }
        System.out.println("Authentication successful!");

        Subject mySubject = lc.getSubject();
        Iterator principalIterator = mySubject.getPrincipals().iterator();
        System.out.println("Authenticated user has the following Principals:");
        while (principalIterator.hasNext()) {
            Principal p = (Principal)principalIterator.next();
            System.out.println("\t" + p.toString());
        }

        PrivilegedAction action = new ReadFilesAction();
        try{
            Subject.doAsPrivileged(mySubject, action, null);
        } catch(AccessControlException ace){
            System.out.println("Subject doesn't have access to ReadFilesAction!");
            System.exit(-1);
        }

        System.exit(0);
    }
}

class LoginCallbackHandler implements CallbackHandler{

    public void handle(Callback[] callbacks)
    throws IOException, UnsupportedCallbackException {

        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {

                // prompt the user for a username
                NameCallback nc = (NameCallback)callbacks[i];

                System.err.print(nc.getPrompt());
                System.err.flush();
                nc.setName((new BufferedReader
                        (new InputStreamReader(System.in))).readLine());

            } else if (callbacks[i] instanceof PasswordCallback) {

                // prompt the user for sensitive information
                PasswordCallback pc = (PasswordCallback)callbacks[i];
                System.err.print(pc.getPrompt());
                System.err.flush();
                pc.setPassword((new BufferedReader
                        (new InputStreamReader(System.in))).readLine().toCharArray());

            } else {
                throw new UnsupportedCallbackException
                        (callbacks[i], "Unrecognized Callback");
            }
        }
    }
}
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

    private static LoginContext lc = null;
    private static Subject mySubject = null;
    public static void main(String[] args){

        createLoginContext();
        authentiacteUser();
        mySubject = lc.getSubject();
        printUserPricipals();
        execureReadFilesAction();

        System.exit(0);
    }

    private static void createLoginContext(){
        try {
            lc = new LoginContext("JaasLogin", new LoginCallbackHandler());
        } catch (LoginException le) {
            System.err.println("Cannot create LoginContext. " + le.getMessage());
            System.exit(-1);
        } catch (Exception e){
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void authentiacteUser(){
        int authenticationAttempts = 0;
        for (; authenticationAttempts < 3; authenticationAttempts++) {
            try {
                lc.login();
                break;
            } catch (LoginException le) {
                System.err.println("Authentication failed: " + le.getMessage());
                try {
                    Thread.currentThread().sleep(3000);
                } catch (Exception e) {}
            }
        }

        if(authenticationAttempts == 3){
            System.out.println("Authentication failed 3 times, stopping application...");
            System.exit(-1);
        }
        System.out.println("Authentication successful!");
    }

    private static void printUserPricipals(){
        Iterator principalIterator = mySubject.getPrincipals().iterator();
        System.out.println("Authenticated user has the following Principals:");
        while (principalIterator.hasNext()) {
            Principal p = (Principal)principalIterator.next();
            System.out.println("\t" + p.toString());
        }
    }

    private static void execureReadFilesAction(){
        PrivilegedAction action = new ReadFilesAction();
        try{
            Subject.doAsPrivileged(mySubject, action, null);
        } catch(AccessControlException ace){
            System.out.println("Subject doesn't have access to ReadFilesAction!");
            System.exit(-1);
        }
    }
}

class LoginCallbackHandler implements CallbackHandler{

    public void handle(Callback[] callbacks)
    throws IOException, UnsupportedCallbackException {

        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                NameCallback nc = (NameCallback)callbacks[i];
                System.err.print(nc.getPrompt());
                System.err.flush();
                nc.setName((new BufferedReader
                        (new InputStreamReader(System.in))).readLine());

            } else if (callbacks[i] instanceof PasswordCallback) {
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
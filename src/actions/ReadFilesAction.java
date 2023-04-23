package src.actions;

import java.io.File;
import java.security.PrivilegedAction;

public class ReadFilesAction implements PrivilegedAction{
    
    public Object run() {

        File f = new File("foo.txt");
        if (!f.exists())
            System.out.print("\nfoo.txt does not exist in the current working directory.");
        else
            System.out.println("foo.txt does exist in the current working directory.");
        return null;
    }
}

javac src/Jaas.java src/module/Client1LoginModule.java src/module/Client2LoginModule.java src/actions/ReadFilesAction.java

jar -cvf Jaas.jar src/Jaas.class src/LoginCallbackHandler.class

jar -cvf ReadFilesAction.jar src/actions/ReadFilesAction.class

jar -cvf Client1LoginModule.jar src/module/Client1LoginModule.class

jar -cvf Client2LoginModule.jar src/module/Client2LoginModule.class
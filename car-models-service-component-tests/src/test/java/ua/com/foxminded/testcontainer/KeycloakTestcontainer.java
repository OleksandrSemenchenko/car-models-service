package ua.com.foxminded.testcontainer;

import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ua.com.foxminded.controller.ComponentTestContext;

public class KeycloakTestcontainer extends ExtendableKeycloakContainer<KeycloakTestcontainer> {
    
    @Override
    protected void configure() {
        super.configure();
        String[] commandParts = getCommandParts();
        List<String> commands = new ArrayList<String>(Arrays.asList(commandParts));
        commands.add("--hostname=" + ComponentTestContext.AUTHORIZATION_SERVER_ALIAS);
        commands.add("--hostname-port=8080");
        commands.add("--hostname-strict-backchannel=true");
        setCommandParts(commands.toArray(new String[0]));
    }
}

package ua.foxminded.cars.testcontainer;

import dasniko.testcontainers.keycloak.ExtendableKeycloakContainer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeycloakTestcontainer extends ExtendableKeycloakContainer<KeycloakTestcontainer> {

  private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak";
  private static final String KEYCLOAK_VERSION = "24.0.3";
  private static final String AUTHORIZATION_SERVER_ALIAS = "keycloak";

  public KeycloakTestcontainer() {
    super(KEYCLOAK_IMAGE + ":" + KEYCLOAK_VERSION);
  }

  @Override
  protected void configure() {
    super.configure();
    String[] commandParts = getCommandParts();
    List<String> commands = new ArrayList<String>(Arrays.asList(commandParts));
    commands.add("--hostname=" + AUTHORIZATION_SERVER_ALIAS);
    commands.add("--hostname-port=8080");
    commands.add("--hostname-strict-backchannel=true");
    setCommandParts(commands.toArray(new String[0]));
  }
}

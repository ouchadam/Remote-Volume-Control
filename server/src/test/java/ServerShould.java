import com.rvc.server.Server;
import com.rvc.server.ServerSettings;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class ServerShould {

    ServerSettings settings = mock(ServerSettings.class);

    @Test
    public void initialise() {
        Server server = new Server(settings);
        assertNotNull(server);
    }

}

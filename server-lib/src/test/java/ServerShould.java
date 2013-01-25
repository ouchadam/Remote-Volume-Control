import com.server.server.Server;
import com.server.server.ServerSettings;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class ServerShould {

    ServerSettings settings = mock(ServerSettings.class);

    @Test
    public void initialise() throws IOException {
        Server server = new Server(settings);
        assertNotNull(server);
    }

}

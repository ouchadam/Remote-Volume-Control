package com.server;

import java.io.IOException;

public interface ServerController {

    void startServer() throws IOException;

    void stopServer() throws IOException;

}

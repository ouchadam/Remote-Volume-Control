package com.rvc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public interface IOController {

    void openIO() throws IOException;

    void closeIO() throws IOException;

    BufferedReader bufferReader();

    PrintWriter printWriter();

}

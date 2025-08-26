package server;

import java.io.OutputStream;
import java.io.PrintStream;
import panel.PanelManager;

public class LogPrintStream extends PrintStream {

    public LogPrintStream(OutputStream out) {
        super(out);
    }

    @Override
    public void println(String x) {
        super.println(x);   
        // Gửi log đến DynamicPanel
        PanelManager.gI().log(x);
    }

    @Override
    public void print(String s) {
        super.print(s);
        // Gửi log đến DynamicPanel
        PanelManager.gI().log(s);
    }
}

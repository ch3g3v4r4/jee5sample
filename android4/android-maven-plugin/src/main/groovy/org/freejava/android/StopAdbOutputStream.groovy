package org.freejava.android

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;


public class StopAdbOutputStream extends FilterOutputStream {
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private CommandLine adbCmdLine;
    private DefaultExecutor adbExecutor;

	public StopAdbOutputStream(OutputStream out, CommandLine adbCmdLine, DefaultExecutor adbExecutor) {
		super(out);
		this.adbCmdLine = adbCmdLine;
		this.adbExecutor = adbExecutor;
	}

	@Override
	public void write(int cc) throws IOException {
		super.write(cc);

		final byte c = (byte) cc;
        if ((c == '\n') || (c == '\r')) {
            processBuffer();
        } else {
            buffer.write(cc);
        }

	}
    protected void processBuffer() {
        String line = buffer.toString();
        if (line.trim().startsWith("Done.") && line.trim().endsWith("installed.")) {
        	try {
        		//http://code.google.com/p/android/issues/detail?id=18868
        		int result = adbExecutor.execute(adbCmdLine);
        	} catch (Exception e) {
        		e.printStackTrace();
			}
        }
        buffer.reset();
    }
}

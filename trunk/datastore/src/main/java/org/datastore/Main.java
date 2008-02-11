package org.datastore;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class Main {
public static void main(String[] args){
Display display = new Display();
Shell shell = new Shell(display);
shell.setText("Hello World");
shell.setSize(200,100);
shell.open ();
while (!shell.isDisposed()) {
if (!display.readAndDispatch())
display.sleep ();
}
display.dispose ();
}
}
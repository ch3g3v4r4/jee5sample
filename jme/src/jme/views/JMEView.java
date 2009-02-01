package jme.views;


import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.SimpleCanvasImpl;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.TextureManager;
import com.jmex.swt.input.SWTKeyInput;
import com.jmex.swt.input.SWTMouseInput;
import com.jmex.swt.lwjgl.LWJGLSWTCanvas;
import com.jmex.swt.lwjgl.LWJGLSWTCanvasConstructor;
import com.jmex.swt.lwjgl.LWJGLSWTConstants;

public class JMEView extends ViewPart {
    private Display display;
    private DisplaySystem ds;
    private LWJGLSWTCanvas canvas;

    static int width = 100, height = 100;

	/**
	 * The constructor.
	 */
	public JMEView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
        this.display = parent.getDisplay();
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new FillLayout());


        ds = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
        ds.registerCanvasConstructor("SWT", LWJGLSWTCanvasConstructor.class);

        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put(LWJGLSWTConstants.PARENT, comp);
        props.put(LWJGLSWTConstants.STYLE, SWT.NONE);
        props.put(LWJGLSWTConstants.DEPTH_BITS, 8);

        canvas = (LWJGLSWTCanvas)ds.createCanvas(width, height, "SWT", props);
        canvas.setUpdateInput(true);
        canvas.setTargetRate(60);

        KeyInput.setProvider(SWTKeyInput.class.getCanonicalName());
        canvas.addKeyListener((SWTKeyInput) KeyInput.get());

        SWTMouseInput.setup(canvas, true);

          // Important! Here is where we add the guts to the panel:
        MyImplementor impl = new MyImplementor(width, height);
        canvas.setImplementor(impl);

        display.timerExec(200, new Runnable() {
			@Override
			public void run() {
		        canvas.init();
		        canvas.render();

			}});

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}

	@Override
	public void dispose() {
		SWTMouseInput.destroyIfInitalized();
		KeyInput.destroyIfInitalized();
		canvas.dispose();
		ds.close();

		super.dispose();
	}


    static class MyImplementor extends SimpleCanvasImpl {

        private Quaternion rotQuat;
        private float angle = 0;
        private Vector3f axis;
        private Box box;
        long startTime = 0;
        long fps = 0;
        private InputHandler input;

        public MyImplementor(int width, int height) {
            super(width, height);
        }

        public void simpleSetup() {

            // Normal Scene setup stuff...
            rotQuat = new Quaternion();
            axis = new Vector3f(1, 1, 0.5f);
            axis.normalizeLocal();

            Vector3f max = new Vector3f(5, 5, 5);
            Vector3f min = new Vector3f(-5, -5, -5);

            box = new Box("Box", min, max);
            box.setModelBound(new BoundingBox());
            box.updateModelBound();
            box.setLocalTranslation(new Vector3f(0, 0, -10));
            box.setRenderQueueMode(Renderer.QUEUE_SKIP);
            rootNode.attachChild(box);

            box.setRandomColors();

            TextureState ts = renderer.createTextureState();
            ts.setEnabled(true);
            ts.setTexture(TextureManager.loadTexture(JMEView.class
                    .getClassLoader().getResource(
                            "jme/Monkey.jpg"),
                    Texture.MinificationFilter.BilinearNearestMipMap,
                    Texture.MagnificationFilter.Bilinear));

            rootNode.setRenderState(ts);
            startTime = System.currentTimeMillis() + 5000;

            input = new FirstPersonHandler(cam, 50, 1);
        }

        public void simpleUpdate() {
            input.update(tpf);

            // Code for rotating the box... no surprises here.
            if (tpf < 1) {
                angle = angle + (tpf * 25);
                if (angle > 360) {
                    angle = 0;
                }
            }
            rotQuat.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, axis);
            box.setLocalRotation(rotQuat);

            if (startTime > System.currentTimeMillis()) {
                fps++;
            } else {
                long timeUsed = 5000 + (startTime - System.currentTimeMillis());
                startTime = System.currentTimeMillis() + 5000;
//                System.out.println(fps + " frames in " + (timeUsed / 1000f)
//                        + " seconds = " + (fps / (timeUsed / 1000f))
//                        + " FPS (average)");
                fps = 0;
            }
        }
    }
}
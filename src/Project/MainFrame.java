package Project;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;

public class MainFrame extends GLCanvas implements GLEventListener, KeyListener {
	private static final float SUN_RADIUS = 12f;
	private GLU glu;
	private Texture stars;
	private ArrayList<Planet> planets;
	private Sun sun;
	float cameraAzimuth = 0.0f, cameraSpeed = 0.0f, cameraElevation = 0.0f;
	float cameraX = 0.0f, cameraY = 0.0f, cameraZ = -20.0f;
	float cameraUpx = 0.0f, cameraUpy = 1.0f, cameraUpz = 0.0f;
	public MainFrame(int width, int height, GLCapabilities capabilities) {
		super(capabilities);
		setSize(width, height);
		addGLEventListener(this);
	}
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		glu = new GLU();
		planets = new ArrayList<>();
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glClearColor(0f, 0f, 0f, 0f);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		this.addKeyListener(this);												// get key binds
		FPSAnimator animator = new FPSAnimator(this, 60);            // get animator
		animator.start();
		String MercuryTex = "src/Textures/mercurymap.jpg";							// get textures
		String VenusTex = "src/Textures/venusmap.jpg";
		String EarthTex = "src/Textures/EarthTex.png";
		String MarsTex = "src/Textures/mars_1k_color.jpg";
		String JupiterTex = "src/Textures/Jupiter.jpg";
		String SaturnTex = "src/Textures/saturn.jpg";
		String UranusTex = "src/Textures/uranuscyl1.jpg";
		String NeptuneTex = "src/Textures/neptune_current.jpg";

			// create the planets
			// speed and radius were taken from https://nssdc.gsfc.nasa.gov/planetary/factsheet/
			// considering the speed of Earth is 29.8 km/s, did ratio conversion for the rest

		Planet Mercury = new Planet(gl, glu, getObjectTexture(gl, MercuryTex), 0.588f, SUN_RADIUS + 2f, 2.56f, "Mercury");
		Planet Venus = new Planet(gl, glu, getObjectTexture(gl, VenusTex), 0.435f, SUN_RADIUS + 12f, 3.56f, "Venus");
		Planet Earth = new Planet(gl, glu, getObjectTexture(gl, EarthTex), 0.37f, SUN_RADIUS + 32f, 6.335f, "Earth");
		Planet Mars = new Planet(gl, glu, getObjectTexture(gl, MarsTex), 0.3f, SUN_RADIUS + 50f, 3.56f, "Mars");
		Planet Jupiter = new Planet(gl, glu, getObjectTexture(gl, JupiterTex), 0.162f, SUN_RADIUS + 65f, 8.56f, "Jupiter");
		Planet Saturn = new Planet(gl, glu, getObjectTexture(gl, SaturnTex), 0.3f, SUN_RADIUS + 90f, 7.56f, "Saturn");
		Planet Uranus = new Planet(gl, glu, getObjectTexture(gl, UranusTex), 0.25f, SUN_RADIUS + 105f, 6.56f, "Uranus");
		Planet Neptune = new Planet(gl, glu, getObjectTexture(gl, NeptuneTex), 0.275f, SUN_RADIUS + 120f, 5.56f, "Neptune");

		planets.add(Mercury);
		planets.add(Venus);
		planets.add(Earth);
		planets.add(Mars);
		planets.add(Jupiter);
		planets.add(Saturn);
		planets.add(Uranus);
		planets.add(Neptune);

		String StarlightTex = "src/Textures/starfield.png";
		stars = getObjectTexture(gl, StarlightTex);

		String SunTex = "src/Textures/preview_sun.jpg";
		this.sun = new Sun(gl, glu, getObjectTexture(gl, SunTex), "Sun");
	}

	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		final GL2 gl = glAutoDrawable.getGL().getGL2();
		setCamera(gl, 300);
		aimCamera(gl, glu);
		moveCamera();
		setLights(gl);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		stars.bind(gl);
		stars.enable(gl);
		drawStarSky(gl);
		sun.display();
		for (Planet p : planets)
			p.display();
	}
	private Texture getObjectTexture(GL2 gl, String fileName) {
		InputStream stream;
		Texture tex;
		String extension = fileName.substring(fileName.lastIndexOf('.'));
		try {
			stream = new FileInputStream(new File(fileName));
			TextureData data = TextureIO.newTextureData(gl.getGLProfile(), stream, false, extension);
			tex = TextureIO.newTexture(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return tex;
	}
	private void setLights(GL2 gl) {
		float SHINE_ALL_DIRECTIONS = 1;
		float[] lightPos = { 0, 0, 0, SHINE_ALL_DIRECTIONS };
		float[] lightColorAmbient = { 0.5f, 0.5f, 0.5f, 1f };
		float[] lightColorSpecular = { 0.8f, 0.8f, 0.8f, 1f };
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);
		gl.glEnable(GL2.GL_LIGHT1);
		gl.glEnable(GL2.GL_LIGHTING);

	}
	private void setCamera(GL2 gl, float distance) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		float widthHeightRatio = (float) getWidth() / (float) getHeight();
		glu.gluPerspective(45, widthHeightRatio, 1, 1000);
		glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	private void drawStarSky(GL gl) {
		stars.enable(gl);
		stars.bind(gl);
		((GLPointerFunc) gl).glDisableClientState(GL2.GL_VERTEX_ARRAY);
		final float radius = 150f;
		final int slices = 16;
		final int stacks = 16;
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_COLOR, GL.GL_DST_ALPHA);
		GLUquadric sky = glu.gluNewQuadric();
		glu.gluQuadricTexture(sky, true);
		glu.gluQuadricDrawStyle(sky, GLU.GLU_FILL);
		glu.gluQuadricNormals(sky, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(sky, GLU.GLU_INSIDE);
		glu.gluSphere(sky, radius, slices, stacks);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_COLOR, GL.GL_DST_ALPHA);
	}
	// ciordeala de la Lab7 ¯\_(ツ)_/¯
	// am schimbat doar keybinds
	public void moveCamera() {
		float[] tmp = polarToCartesian(cameraAzimuth, cameraSpeed, cameraElevation);

		cameraX += tmp[0];
		cameraY += tmp[1];
		cameraZ += tmp[2];
	}
	public void aimCamera(GL2 gl, GLU glu) {
		gl.glLoadIdentity();
		float[] tmp = polarToCartesian(cameraAzimuth, 100.0f, cameraElevation);
		float[] camUp = polarToCartesian(cameraAzimuth, 100.0f, cameraElevation + 90);
		cameraUpx = camUp[0];
		cameraUpy = camUp[1];
		cameraUpz = camUp[2];
		glu.gluLookAt(cameraX, cameraY, cameraZ, cameraX + tmp[0],
				cameraY + tmp[1], cameraZ + tmp[2], cameraUpx, cameraUpy, cameraUpz);
	}
	private float[] polarToCartesian(float azimuth, float length, float altitude) {
		float[] result = new float[3];
		float x, y, z;
		float theta = (float) Math.toRadians(90 - azimuth);
		float tantheta = (float) Math.tan(theta);
		float radian_alt = (float) Math.toRadians(altitude);
		float cospsi = (float) Math.cos(radian_alt);
		x = (float) Math.sqrt((length * length) / (tantheta * tantheta + 1));
		z = tantheta * x;
		x = -x;
		if ((azimuth >= 180.0 && azimuth <= 360.0) || azimuth == 0.0f) {
			x = -x;
			z = -z;
		}
		// Calculate y, and adjust x and z
		y = (float) (Math.sqrt(z * z + x * x) * Math.sin(radian_alt));
		if (length < 0) {
			x = -x;
			z = -z;
			y = -y;
		}
		x = x * cospsi;
		z = z * cospsi;
		result[0] = x;
		result[1] = y;
		result[2] = z;
		return result;
	}
	public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_W) {
			cameraElevation -= 2;
		}
		if (event.getKeyCode() == KeyEvent.VK_S) {
			cameraElevation += 2;
		}
		if (event.getKeyCode() == KeyEvent.VK_D) {
			cameraAzimuth -= 2;
		}
		if (event.getKeyCode() == KeyEvent.VK_A) {
			cameraAzimuth += 2;
		}
		if (event.getKeyCode() == KeyEvent.VK_UP) {
			cameraSpeed += 0.05;
		}
		if (event.getKeyCode() == KeyEvent.VK_DOWN) {
			cameraSpeed -= 0.05;
		}
		if (event.getKeyCode() == KeyEvent.VK_SPACE) {
			cameraSpeed = 0;
		}
		if (event.getKeyCode() < 250)
			keys[event.getKeyCode()] = true;
		if (cameraAzimuth > 359)
			cameraAzimuth = 1;

		if (cameraAzimuth < 1)
			cameraAzimuth = 359;
	}
	private final boolean[] keys = new boolean[250];
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

	}
	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {

	}
}

package Project;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;

public class Sun {
	private final GL2 gl;
	private final GLU glu;
	private final Texture sunTexture;
	private float angle = 0;
	private final GLUT glut;
	private final String name;

	// constructor with values
	public Sun(GL2 gl, GLU glu, Texture sunTexture, String name) {
		this.gl = gl;
		this.glu = glu;
		this.sunTexture = sunTexture;
		this.name = name;
		this.glut = new GLUT();
	}

	// display of object
	public void display() {
		final float radius = 10f;										// size of sun
		final int slices = 32;											// divide sun into slices/stack parts, more parts make the sun look better
		final int stacks = 32;
		sunTexture.enable(gl);
		sunTexture.bind(gl);
		gl.glPushName(6);											// push id 6 into stack
		angle = (angle + 0.7f) % 360f;									// constant update of angle
		gl.glPushMatrix();												// push current matrix info in stack
		gl.glRotatef(angle, 0.8f, 0.1f, 0);					// make rotation
		GLUquadric sun = glu.gluNewQuadric();							// create sun object
		glu.gluQuadricTexture(sun, true);							// get coords of sun object
		glu.gluQuadricDrawStyle(sun, GLU.GLU_FILL);						// fill the sun object
		glu.gluSphere(sun, radius, slices, stacks);						// now make the sun into a sphere
		glu.gluDeleteQuadric(sun);										// free memory
		gl.glPopMatrix();
		gl.glPopName();

		gl.glRasterPos3f(0, 10, -20); 						// position of the text message
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, name); 			// render the text message using GLUT
	}
}
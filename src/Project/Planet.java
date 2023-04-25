package Project;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;

public class Planet {
	private final GL2 gl;
	private final GLU glu;
	private final Texture planetTexture;
	private float angle;
	private final float distance;
	private float rotationAngle = 0;
	private final float speed;
	private final float radius;
	private final String name;
	private final GLUT glut;

	public Planet(GL2 gl, GLU glu, Texture planetTexture, float speed, float distance, float radius, String name) {
		this.gl = gl;
		this.glu = glu;
		this.planetTexture = planetTexture;
		this.speed = speed;
		this.distance = distance;
		this.radius = radius;
		this.name = name;
		this.glut = new GLUT();
	}
	public void display() {
		gl.glPushMatrix();
		angle = (angle + speed) % 360f;										// get spinning angle for each planet using speed
			// get coordinates for each planet
		final float x = (float) Math.sin(Math.toRadians(angle)) * distance;
		final float y = (float) Math.cos(Math.toRadians(angle)) * distance;
		final float z = 0;
		gl.glTranslatef(x, y, z);	//move pointers at the coords
		draw();						//draw the object
		gl.glPopMatrix();

	}
	private void draw() {
		float[] rgba = { 1f, 1f, 1f };
		final int slices = 32;												//same explanation as Sun
		final int stacks = 32;
			// apply some lights to the object
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);			// apply ambient light
		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 10f);				// apply shining 0-128f value,
																			// bigger means smaller potent shine
		planetTexture.enable(gl);
		planetTexture.bind(gl);
		rotationAngle = (rotationAngle + 0.1f) % 360f;						// get rotation angle for each planet
		gl.glPushMatrix();
		gl.glRotatef(rotationAngle, 0.2f, 0.1f, 0);
		GLUquadric planet = glu.gluNewQuadric();							// same as Sun
		glu.gluQuadricTexture(planet, true);
		glu.gluQuadricDrawStyle(planet, GLU.GLU_FILL);
		glu.gluSphere(planet, radius, slices, stacks);
		glu.gluDeleteQuadric(planet);
		gl.glPopMatrix();


		gl.glRasterPos3f(0, radius, 0);								// observe the position will move with the planet
																			// unlike the sun which is stationary
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, name);				// Render the text message using GLUT


	}

}
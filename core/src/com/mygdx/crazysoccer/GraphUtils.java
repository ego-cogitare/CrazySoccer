package com.mygdx.crazysoccer;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GraphUtils {
	
	public static Color getPixelAt(float x, float y) {
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		ByteBuffer pixels = pixmap.getPixels();
		Gdx.gl.glReadPixels((int)x, (int)y, 1, 1, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);
		Color color = new Color();
		Color.rgba8888ToColor(color, pixmap.getPixel(0,0));
		pixmap.dispose();
		
		return color;
	}
	
	public static void dottedVertLine(int x, int y, int length, int space, int dotSize) {
		Gdx.gl20.glLineWidth(1);
		int xEnd = x + length;
		CrazySoccer.shapeRenderer.begin(ShapeType.Filled);
		while (x < xEnd) {
			CrazySoccer.shapeRenderer.rect(x, y, dotSize, dotSize);
			x += space + dotSize;
		}
		CrazySoccer.shapeRenderer.end();
	}
}

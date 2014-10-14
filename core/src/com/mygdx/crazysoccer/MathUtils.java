package com.mygdx.crazysoccer;

class MathUtils {
	
	public static class Box {
		public float boxX1; 
		public float boxY1; 
		public float boxX2; 
		public float boxY2;
		
		public Box(float boxX1, float boxY1, float boxX2, float boxY2) {
			this.boxX1 = boxX1;
			this.boxX2 = boxX2;
			this.boxY1 = boxY1;
			this.boxY2 = boxY2;
		}
	}
	
//	public static Box box(float boxX1, float boxY1, float boxX2, float boxY2) {
//		
//		return new Box(boxX1, boxY1, boxX2, boxY2);
//	}
	
	// Попадает ли точка в прямоугольник
	public static boolean isPointInBox(float pointX, float pointY, Box box) {
		
		return (pointX > box.boxX1) && (pointX < box.boxX2) && (pointY > box.boxY1) && (pointY < box.boxY2);
	}
	
	/**
	 * Пересекаются ли два отрезка
	 * @param ax1
	 * @param ay1
	 * @param ax2
	 * @param ay2
	 * @param bx1
	 * @param by1
	 * @param bx2
	 * @param by2
	 * @return true если пересекаются и false в противном случае
	 */
	public static boolean Intersection(float ax1, float ay1, float ax2, float ay2, float bx1, float by1, float bx2, float by2) {
	
	   float v1 = (bx2-bx1)*(ay1-by1) - (by2-by1)*(ax1-bx1);
	   float v2 = (bx2-bx1)*(ay2-by1) - (by2-by1)*(ax2-bx1);
	   float v3 = (ax2-ax1)*(by1-ay1) - (ay2-ay1)*(bx1-ax1);
	   float v4 = (ax2-ax1)*(by2-ay1) - (ay2-ay1)*(bx2-ax1);
	
	   return (v1 * v2 < 0) && (v3 * v4 < 0);
	}
	
	/**
	 * Подсчет количества пересечений точки с координатами pointX и pointY с гранями многоугольника с 
	 * вершинами verticles. Если число равно 1 - точка находится внутри многоугольника, иначе если
	 * 2 - справа от него
	 * @param verticles
	 * @return
	 */
	public static int intersectCount(float pointX, float pointY, float[][] verticles) {
		
		// Количество пересечений луча с гранями многоугольника
		int intersect = 0;
		
		for (int i = 0; i < verticles.length - 1; i++) {
			if (Intersection(0, pointY, pointX, pointY, verticles[i][0], verticles[i][1], verticles[i+1][0], verticles[i+1][1])) {
				intersect++;
			}
		}
		
		if (Intersection(0, pointY, pointX, pointY, verticles[0][0], verticles[0][1], verticles[verticles.length - 1][0], verticles[verticles.length - 1][1])) {
			intersect++;
		}
		
		return intersect;
	}
}

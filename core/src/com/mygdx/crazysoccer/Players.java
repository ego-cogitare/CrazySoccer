package com.mygdx.crazysoccer;

public class Players {

	public static enum Names {
		KUNIO,
		RIKKI
	}
	
	private static enum Ampluas {
		GK,
		DF,
		MF,
		FW
	}
	
	public static class FaceCell {
		public int x = -1;
		public int y = -1;
		
		public FaceCell(int y, int x) {
			this.x = x;
			this.y = y;
		}
	}
	
	public static class Params {
		public int ID;
		public Names name;
		public String stringName;
		public Ampluas amplua;
		
		public float walkingSpeed;
		public float runSpeed;
		public float topSpeed;
		public float strength;
		public float kickStrength;
		public float mass;
		public float maxHealth;
		
		public FaceCell faceFrontId;
		public FaceCell faceProfileId;
		public FaceCell faceLayId;
		
		
		public Params(int ID, String stringName, Names name, Ampluas amplua, float walkingSpeed, float runSpeed, float topSpeed, 
				float strength, float kickStrength, float mass, float maxHealth, FaceCell faceFrontId, FaceCell faceProfileId, FaceCell faceLayId) {
			
			this.ID = ID;
			this.stringName = stringName;
			this.name = name;
			this.amplua = amplua;
			this.walkingSpeed = walkingSpeed;
			this.runSpeed = runSpeed;
			this.topSpeed = topSpeed;
			this.strength = strength;
			this.kickStrength = kickStrength;
			this.mass = mass;
			this.maxHealth = maxHealth;
			
			this.faceFrontId = faceFrontId;
			this.faceProfileId = faceProfileId;
			this.faceLayId = faceLayId;
		}
	}
	
	private static Params[] playerParams = {
		new Params(
			0, "KUNIO", Names.KUNIO, Ampluas.FW, 3.0f, 6.0f, 9.0f, 210.0f, 50.0f, 62.0f, 200, 
			new FaceCell(0,2), new FaceCell(0,3), new FaceCell(0,1)
		),
		
		new Params(
			1, "RIKKI", Names.RIKKI, Ampluas.FW, 3.0f, 6.0f, 9.0f, 210.0f, 50.0f, 62.0f, 200,
			new FaceCell(0,5), new FaceCell(0,6), new FaceCell(0,4)
		),
		
		new Params(
			2, "BATON", Names.RIKKI, Ampluas.FW, 3.0f, 6.0f, 9.0f, 210.0f, 50.0f, 62.0f, 200,
			new FaceCell(0,8), new FaceCell(0,9), new FaceCell(0,7)
		),
		
		new Params(
			3, "KOSYAK", Names.RIKKI, Ampluas.FW, 3.0f, 6.0f, 9.0f, 210.0f, 50.0f, 62.0f, 200,
			new FaceCell(0,11), new FaceCell(0,12), new FaceCell(0,10)
		),
		
		new Params(
			4, "PATLA", Names.RIKKI, Ampluas.FW, 3.0f, 6.0f, 9.0f, 210.0f, 50.0f, 62.0f, 200,
			new FaceCell(0,14), new FaceCell(0,15), new FaceCell(0,13)
		),
		
		new Params(
			5, "KEEPER", Names.RIKKI, Ampluas.FW, 3.0f, 6.0f, 9.0f, 210.0f, 50.0f, 62.0f, 200,
			new FaceCell(1,1), new FaceCell(1,2), new FaceCell(1,0)
		),
		
		new Params(
			6, "SVERDLO", Names.RIKKI, Ampluas.FW, 3.0f, 6.0f, 9.0f, 210.0f, 50.0f, 62.0f, 200,
			new FaceCell(1,4), new FaceCell(1,5), new FaceCell(1,3)
		),
		
		new Params(
			7, "BANAN", Names.RIKKI, Ampluas.FW, 3.0f, 6.0f, 9.0f, 210.0f, 50.0f, 62.0f, 200,
			new FaceCell(1,7), new FaceCell(1,8), new FaceCell(1,6)
		),
		
		new Params(
			8, "RIBKA", Names.RIKKI, Ampluas.FW, 3.0f, 6.0f, 9.0f, 210.0f, 50.0f, 62.0f, 200,
			new FaceCell(1,10), new FaceCell(1,11), new FaceCell(1,9)
		),
		
		new Params(
			9, "OCHKARIK", Names.RIKKI, Ampluas.FW, 3.0f, 6.0f, 9.0f, 210.0f, 50.0f, 62.0f, 200,
			new FaceCell(1,13), new FaceCell(1,14), new FaceCell(1,12)
		),
		
		new Params(
			10, "DAVIDS", Names.RIKKI, Ampluas.FW, 3.0f, 6.0f, 9.0f, 210.0f, 50.0f, 62.0f, 200,
			new FaceCell(2,0), new FaceCell(2,1), new FaceCell(1,15)
		),
		
		new Params(
			11, "TRUSI", Names.RIKKI, Ampluas.FW, 3.0f, 6.0f, 9.0f, 210.0f, 50.0f, 62.0f, 200,
			new FaceCell(2,3), new FaceCell(2,4), new FaceCell(2,2)
		),
	};
	
	public static Params getParams(int playerId) {
		try {
			return playerParams[playerId];
		} 
		catch(ArrayIndexOutOfBoundsException e) {
			return playerParams[0];
		}
	}
}

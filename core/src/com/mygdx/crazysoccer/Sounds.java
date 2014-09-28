package com.mygdx.crazysoccer;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Sounds {
	
	private class Sample {
		public long ID = -1L;
		private String name;
		private String fileName;
		private boolean loop = false;
		private Sound sound;
		
		public Sample(String fileName) {
			this.sound = Gdx.audio.newSound(Gdx.files.internal(fileName));
		}
	}
	
	private Map<String, Sample> sound;
	
	public Sounds() {
		sound = new HashMap<String, Sample>();
	}
	
	// Загрузка звукового файла
	public void load(String name, String fileName) {
		sound.put(name, new Sample(fileName));
	}
	
	public void volume(String name, float vol) {
		sound.get(name).sound.setVolume(sound.get(name).ID, vol);
	}
	
	public void play(String name) {
		if (sound.get(name).ID < 0 || !sound.get(name).loop) {
			sound.get(name).ID = sound.get(name).sound.play();
		}
	}
	
	public void play(String name, boolean restart) {
		if (restart) sound.get(name).sound.stop();
		this.play(name);
	}
	
	public void stop(String name) {
		sound.get(name).sound.stop();
	}
	
	public void pause(String name) {
		sound.get(name).sound.pause();
	}
	
	public void loop(String name, boolean looping) {
		sound.get(name).sound.setLooping(sound.get(name).ID, looping);
		sound.get(name).loop = looping;
	}
}

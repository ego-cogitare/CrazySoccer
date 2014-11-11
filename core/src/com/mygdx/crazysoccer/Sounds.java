package com.mygdx.crazysoccer;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Sounds {
	
	private class Sample {
		public long ID = -1L;
		//private String name;
		//private String fileName;
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
	
	public void resume(String name) {
		sound.get(name).sound.resume();
	}
	
	public void loop(String name, boolean looping) {
		sound.get(name).sound.setLooping(sound.get(name).ID, looping);
		sound.get(name).loop = looping;
	}
	
	public void stopAll(ArrayList<String> exceptList) {
		List<String> keyList = new ArrayList<String>(sound.keySet());
		
		for (int i = 0; i < keyList.size(); i++) {
			if (exceptList.indexOf(keyList.get(i)) == -1) {
				this.stop(keyList.get(i));
			}
		}
	}
	
	public void stopAll(String exceptOne) {
		List<String> keyList = new ArrayList<String>(sound.keySet());
		
		for (int i = 0; i < keyList.size(); i++) {
			if (exceptOne != keyList.get(i)) {
				this.stop(keyList.get(i));
			}
		}
	}
}

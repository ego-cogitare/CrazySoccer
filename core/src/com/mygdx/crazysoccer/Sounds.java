package com.mygdx.crazysoccer;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

class Sounds {
	
	private static class Sample {
		public long ID = -1L;
		private boolean loop = false;
		private Sound sound;
		
		public Sample(String fileName) {
			this.sound = Gdx.audio.newSound(Gdx.files.internal(fileName));
		}
	}
	
	private static Map<String, Sample> sound;
	
	public Sounds() {
		sound = new HashMap<String, Sample>();
	}
	
	// Загрузка звукового файла
	public static void load(String name, String fileName) {
		sound.put(name, new Sample(fileName));
	}
	
	public static void volume(String name, float vol) {
		sound.get(name).sound.setVolume(sound.get(name).ID, vol);
	}
	
	public static void play(String name) {
		if (sound.get(name).ID < 0 || !sound.get(name).loop) {
			sound.get(name).ID = sound.get(name).sound.play();
		}
	}
	
	public static boolean isLoaded(String name) {
		return (sound.get(name) != null && sound.get(name).ID >= 0) ? true : false;
	}
	
	public static void play(String name, boolean restart) {
		if (restart) sound.get(name).sound.stop();
		play(name);
	}
	
	public static void stop(String name) {
		sound.get(name).sound.stop();
	}
	
	public static void pause(String name) {
		sound.get(name).sound.pause();
	}
	
	public static void resume(String name) {
		sound.get(name).sound.resume();
	}
	
	public static void unload(String name) {
		if (isLoaded(name)) {
			stop(name);
			sound.get(name).sound.dispose();
			sound.remove(name);
		}
	}
	
	public static void unloadAll(String exceptOne) {
		List<String> keyList = new ArrayList<String>(sound.keySet());
		
		for (int i = 0; i < keyList.size(); i++) {
			if (exceptOne != keyList.get(i) && isLoaded(keyList.get(i))) { 
				stop(keyList.get(i));
				sound.get(keyList.get(i)).sound.dispose();
				sound.remove(keyList.get(i));
			}
		}
	}
	
	public static void loop(String name, boolean looping) {
		sound.get(name).sound.setLooping(sound.get(name).ID, looping);
		sound.get(name).loop = looping;
	}
	
	public static void stopAll(ArrayList<String> exceptList) {
		List<String> keyList = new ArrayList<String>(sound.keySet());
		
		for (int i = 0; i < keyList.size(); i++) {
			if (exceptList.indexOf(keyList.get(i)) == -1) {
				stop(keyList.get(i));
			}
		}
	}
	
	public static void stopAll(String exceptOne) {
		List<String> keyList = new ArrayList<String>(sound.keySet());
		
		for (int i = 0; i < keyList.size(); i++) {
			if (exceptOne != keyList.get(i)) {
				stop(keyList.get(i));
			}
		}
	}
}

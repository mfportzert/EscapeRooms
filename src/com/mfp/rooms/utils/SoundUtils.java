package com.mfp.rooms.utils;

import org.andengine.audio.sound.Sound;

public class SoundUtils {
	
	public static void playSound(Sound sound) {
		
		if (sound != null) {
			sound.play();
		}
	}

	public static void playSound(Sound sound, float volume) {
		
		if (sound != null) {
			
			sound.setVolume(volume);
			SoundUtils.playSound(sound);
		}
	}
}

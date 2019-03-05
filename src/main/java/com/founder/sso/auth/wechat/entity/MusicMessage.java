package com.founder.sso.auth.wechat.entity;

/**
 * 音乐消息
 * 
 * @author hanpt
 */
public class MusicMessage extends BaseMessage {
	// 音乐
	private Music Music;

	public Music getMusic() {
		return Music;
	}

	public void setMusic(Music music) {
		Music = music;
	}
}

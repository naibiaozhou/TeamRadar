package com.nut.teamradar.webclient;

public interface TRConnectionListener {
	public void OnLogIn(int flag,int Id, String Name);
	public void OnLogOut(int flag);
	public void OnRregtistration(int Flag);
}

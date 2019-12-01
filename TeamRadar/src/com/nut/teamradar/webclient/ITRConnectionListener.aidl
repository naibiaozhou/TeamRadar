package com.nut.teamradar.webclient;
interface ITRConnectionListener{
	void OnLogIn(int flag,int Id, String Name);
	void OnLogOut(int flag);
	void OnRregtistration(int Flag,int id);
}
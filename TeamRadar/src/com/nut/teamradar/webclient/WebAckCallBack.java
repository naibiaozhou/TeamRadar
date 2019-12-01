package com.nut.teamradar.webclient;

public interface WebAckCallBack<T,T1> {
	public void eventCallBack(int flag, T arg0, T1 arg1);
}

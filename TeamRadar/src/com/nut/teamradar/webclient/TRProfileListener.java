package com.nut.teamradar.webclient;

import com.nut.teamradar.model.User;

public interface TRProfileListener {
	public void OnObtainProfile(User usr);
	public void OnProfileUpdate(int flag);
}

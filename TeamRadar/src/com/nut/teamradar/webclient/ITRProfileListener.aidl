package com.nut.teamradar.webclient;
import com.nut.teamradar.model.User;
interface ITRProfileListener{
	void OnObtainProfile(in User usr);
	void OnProfileUpdate(int flag);
	void OnPasswordUpdate(int flag);
	void OnHXUserPasswordUpdate(int flag);
}
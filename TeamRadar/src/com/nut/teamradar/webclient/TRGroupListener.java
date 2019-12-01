package com.nut.teamradar.webclient;

import java.util.List;

import com.nut.teamradar.model.Member;

public interface TRGroupListener {
	public void OnMemberUpdate(int flag, List<Member> Members);
	public void OnDeleteMember(int Flag);
	public void OnDeleteActivity(int Flag);
	public void OnCreateActivity(int Flag);
	public void OnAddUserToActivity(int Flag);
	public void OnHXUserCreated(int Flag);
	public void OnSessionStart();
	public void OnSessionStop();
}

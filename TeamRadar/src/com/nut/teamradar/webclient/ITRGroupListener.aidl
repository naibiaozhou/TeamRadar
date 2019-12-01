package com.nut.teamradar.webclient;
import com.nut.teamradar.model.Member;
import com.nut.teamradar.model.Group;
interface ITRGroupListener {
	void OnMemberUpdate(int flag, in List<Member> Members);
	void OnDeleteMember(int Flag);
	void OnDeleteMemberFromActivity(int Flag);
	void OnDeleteActivity(int Flag);
	void OnCreateActivity(int Flag);
	void OnAddUserToActivity(int Flag);
	void OnHXUserCreated(int Flag);
	void OnObtainActivity(int flag, in List<Group> groups);
	void OnObtainActivityBySubscription(int flag, in List<Group> groups);
	void OnSessionStart();
	void OnSessionStop();
}
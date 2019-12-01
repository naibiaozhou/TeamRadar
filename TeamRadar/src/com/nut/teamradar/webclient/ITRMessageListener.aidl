package com.nut.teamradar.webclient;
import com.nut.teamradar.model.ShortMessage;

interface ITRMessageListener{
	void OnPushMessage(int Flag);
	void OnPullMessage(int Flag, in List<ShortMessage> msgs );
	void OnProcessMessage(int Flag, in ShortMessage msg );
}

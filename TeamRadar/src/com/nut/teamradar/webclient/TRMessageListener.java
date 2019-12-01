package com.nut.teamradar.webclient;

import java.util.List;

import com.nut.teamradar.model.ShortMessage;

public interface TRMessageListener {
	public void OnPushMessage(int Flag);
	public void OnPullMessage(int Flag, List<ShortMessage> msgs );
}

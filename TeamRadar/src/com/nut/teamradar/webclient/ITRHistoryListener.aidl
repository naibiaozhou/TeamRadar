package com.nut.teamradar.webclient;
import java.util.List;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.model.Location;
import com.nut.teamradar.model.ActivityHistory;

interface ITRHistoryListener {
	void OnGetActivity(int Flag , in List<Group> Groups);
	void OnGetActivityHistory(int Flag ,in List<ActivityHistory> Historys);
	void OnGetHistoryLocations(int Flag, in List<com.nut.teamradar.model.Location> Locations);
}

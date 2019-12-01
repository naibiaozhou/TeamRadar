package com.nut.teamradar.webclient;
import java.util.List;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.model.Location;
import com.nut.teamradar.model.ActivityHistory;

public interface TRHistoryListener {
	public void OnGetActivity(int Flag , List<Group> Groups);
	public void OnGetActivityHistory(int Flag ,List<ActivityHistory> Historys);
	public void OnGetHistoryLocations(int Flag, List<com.nut.teamradar.model.Location> Locations);
}

package com.nut.teamradar.webclient;

import java.util.List;
import com.nut.teamradar.model.MarkerInfo;
import com.nut.teamradar.model.Location;

public interface TRLocationListener {
	public void OnLocationUpdate(int Flag, List<com.nut.teamradar.model.Location> locs);
	public void OnRandezvousUpdate(int Flag, MarkerInfo Info);
}

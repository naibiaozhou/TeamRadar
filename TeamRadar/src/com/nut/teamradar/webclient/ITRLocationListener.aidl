package com.nut.teamradar.webclient;
import com.nut.teamradar.model.Location;
import com.nut.teamradar.model.MarkerInfo;
interface ITRLocationListener {
	void OnLocationUpdate(int Flag, in List<com.nut.teamradar.model.Location> locs);
	void OnLocationUploaded(int Flag);
	void OnRandezvousUpdate(int Flag);
	void OnObtainRandezvous(int Flag, in MarkerInfo Info);
}

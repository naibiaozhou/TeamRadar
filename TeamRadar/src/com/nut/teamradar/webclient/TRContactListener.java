package com.nut.teamradar.webclient;

import java.util.List;

import com.nut.teamradar.model.Contact;

public interface TRContactListener {
	public void OnDownloadContacts(int Flag, List<Contact> contacts);
	public void OnDeleteContact(int Flag);
	public void OnAddContact(int Flag);
}

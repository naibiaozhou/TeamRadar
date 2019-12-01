package com.nut.teamradar.webclient;

import com.nut.teamradar.model.Contact;

interface ITRContactListener{
	void OnDownloadContacts(int Flag, in List<Contact> contacts);
	void OnDeleteContact(int Flag);
	void OnAddContact(int Flag);
}

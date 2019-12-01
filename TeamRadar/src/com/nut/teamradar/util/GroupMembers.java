package com.nut.teamradar.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.nut.teamradar.model.Member;

public class GroupMembers {
	private long GroupId;
	private List<Member> mMembers=null;
	
	public GroupMembers(long id)
	{
		GroupId = id;
		mMembers=new ArrayList<Member>();
	}
	public List<Member> getAllMembers()
	{
		return mMembers;
	}
	public long GetGroupId()
	{
		return GroupId;
	}
	public Member GetAMember(long UserId)
	{
		int i;
		Member mb=null;
		for(i=0;i< mMembers.size();i++)
		{
			mb = mMembers.get(i);
			if(mb.getUserId() == UserId)
			{
				return mb;
			}
		}
		return null;
	}
	public Member GetMember(int index)
	{
		if(index > mMembers.size())
		{
			return null;
		}		
		return mMembers.get(index);
	}
	public int getSize()
	{
		return mMembers.size();
	}
	public void ClearAllMembers()
	{
		mMembers.removeAll(mMembers);
	}
	public void AddAllMembers(List<Member> mbs)
	{
		mMembers.addAll(mbs);
	}
	
	public void InsertMember(Member mbr)
	{
		mMembers.add(mbr);
	}
	
	public void DeleteMember(Member mbr)
	{
		mMembers.remove(mbr);
	}
	
	public void DeleteMember(int index)
	{
		mMembers.remove(index);
	}
	
	public Iterator<Member> GetIterator()
	{
		return mMembers.iterator();
	}
	
}

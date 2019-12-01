package com.nut.teamradar.util;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.widget.ImageView;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.nut.teamradar.R;
import com.nut.teamradar.TRServiceConnection;
import com.nut.teamradar.TeamRadarApplication;
import com.nut.teamradar.domain.HXUser;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.model.Member;
import com.squareup.picasso.Picasso;

public class UserUtils {
    /**
     * 鏍规嵁username鑾峰彇鐩稿簲user锛岀敱浜巇emo娌℃湁鐪熷疄鐨勭敤鎴锋暟鎹紝杩欓噷缁欑殑妯℃嫙鐨勬暟鎹紱
     * @param username
     * @return
     */
    public static HXUser getUserInfo(String username){
    	HXUser user = TeamRadarApplication.getInstance().getContactList().get(username);
        if(user == null){
            user = new HXUser(username);
        }
            
        if(user != null){
            user.setNick(username);
        }
        return user;
    }
    
    /**
     * 璁剧疆鐢ㄦ埛澶村儚
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	HXUser user = getUserInfo(username);
        if(user != null){
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }
    public static String GetGroupName( int GroupIndex)
    {
    	String name = null;
    	Group grp =  TRServiceConnection.getInstance().GetGroup(GroupIndex);
    	if(grp != null)
    	{
    		name  = grp.getName();
    	}
    	return name;
    }
    public static String GetGroupNameByGroupName( String Name)
    {
    	String retname = null;
    	String info[] = Name.split(":");
    	Group grp =  TRServiceConnection.getInstance().getGroupByNameAndSub(info[1],info[0]);
    	if(grp != null)
    	{
    		retname  = grp.getName();
    	}
    	return retname;
    }
    public static String GetUserName(int GroupIndex,int MemberIndex)
    {
    	String name = null;
    	Member mbr = TRServiceConnection.getInstance().GetMember(GroupIndex, MemberIndex);
    	if(mbr != null)
    	{
    		name = mbr.getUsername();
    	}
    	return name;
    }
    public static String GetUserNameBySub(String sub)
    {
    	String name = null;
    	Member mbr = TRServiceConnection.getInstance().GetMemberBySub(sub);
    	if(mbr != null)
    	{
    		name = mbr.getUsername();
    	}
    	return name;
    }
    public static String MakeGroupName(int index)
    {
    	String Out;
    	Group grp =  TRServiceConnection.getInstance().GetGroup(index);
    	Out = grp.getSubscription()+":"+grp.getName();
    	return Out;
    }
    public static String MakeGroupName(String name,String sub)
    {
    	String Out;
    	Out = sub+":"+name;
    	return Out;
    }
    public static String GetGroupId(String GroupName)
    {
    	List<EMGroup> grouplist = EMGroupManager.getInstance().getAllGroups();
		Iterator<EMGroup> it = grouplist.iterator();
		while(it.hasNext())
		{
			EMGroup grp = it.next();
			if(grp.getGroupName().equals(GroupName))
			{
				return grp.getGroupId();
			}
		}
		return null;
    }
}

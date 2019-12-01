/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nut.teamradar.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMConversation.EMConversationType;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.nut.teamradar.Constant;
import com.nut.teamradar.R;
import com.nut.teamradar.TeamRadarHXSDKHelper;
import com.nut.teamradar.controller.HXSDKHelper;
import com.nut.teamradar.domain.RobotUser;
import com.nut.teamradar.model.Group;
import com.nut.teamradar.model.Member;
import com.nut.teamradar.util.Encrypt;
import com.nut.teamradar.util.SmileUtils;
import com.nut.teamradar.util.UserUtils;

/**
 * 鏄剧ず鎵�鏈夎亰澶╄褰昦dpater
 * 
 */
public class ChatAllHistoryAdapter extends ArrayAdapter<EMConversation> {
	private static final String TAG = "ChatAllHistoryAdapter";
	private LayoutInflater inflater;
	private List<EMConversation> conversationList;
	private List<EMConversation> copyConversationList;
	private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;

	public ChatAllHistoryAdapter(Context context, int textViewResourceId, List<EMConversation> objects) {
		super(context, textViewResourceId, objects);
		this.conversationList = objects;
		copyConversationList = new ArrayList<EMConversation>();
		copyConversationList.addAll(objects);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String ActivityName="dumy";
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_chat_history, parent, false);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.msgState = convertView.findViewById(R.id.msg_state);
			holder.list_item_layout = (RelativeLayout) convertView.findViewById(R.id.list_item_layout);
			convertView.setTag(holder);
		}
		if (position % 2 == 0) {
			holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem);
		} else {
			holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem_grey);
		}

		// 鑾峰彇涓庢鐢ㄦ埛/缇ょ粍鐨勪細璇�
		EMConversation conversation = getItem(position);
		// 鑾峰彇鐢ㄦ埛username鎴栬�呯兢缁刧roupid
		String username = conversation.getUserName();
		if (conversation.getType() == EMConversationType.GroupChat) {
			// 群聊消息，显示群聊头像
			holder.avatar.setImageResource(R.drawable.group_icon);
			EMGroup group = EMGroupManager.getInstance().getGroup(username);
			if(group != null)
			{
				ActivityName = UserUtils.GetGroupNameByGroupName(Encrypt.GetDecryptString(group.getGroupName()));
				if(ActivityName == null)
				{
					if(username.length()%2 ==1)
					{
						ActivityName = username;
						Log.e(TAG, "Error String :"+username);
					}
					else
						ActivityName = Encrypt.GetDecryptString(username);
				}
			}
			holder.name.setText(group != null ? ActivityName : username);
		} else if(conversation.getType() == EMConversationType.ChatRoom){
		    holder.avatar.setImageResource(R.drawable.group_icon);
            EMChatRoom room = EMChatManager.getInstance().getChatRoom(username);
            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
		}else {
		    UserUtils.setUserAvatar(getContext(), username, holder.avatar);	
			if (username.equals(Constant.GROUP_USERNAME)) {
				holder.name.setText("群聊");
			} else if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
				holder.name.setText("申请与通知");
			}
			Map<String,RobotUser> robotMap=((TeamRadarHXSDKHelper)HXSDKHelper.getInstance()).getRobotList();
			if(robotMap!=null&&robotMap.containsKey(username)){
				String nick = robotMap.get(username).getNick();
				ActivityName = UserUtils.GetUserNameBySub(Encrypt.GetDecryptString(username));
				if(ActivityName == null)
					ActivityName = Encrypt.GetDecryptString(username);
				if(!TextUtils.isEmpty(nick)){
					holder.name.setText(ActivityName);
				}else{
					holder.name.setText(ActivityName);
				}
			}else{
				ActivityName = UserUtils.GetUserNameBySub(Encrypt.GetDecryptString(username));
				if(ActivityName == null)
					ActivityName = Encrypt.GetDecryptString(username);
				holder.name.setText(ActivityName);
			}
		}



		if (conversation.getUnreadMsgCount() > 0) {
			// 鏄剧ず涓庢鐢ㄦ埛鐨勬秷鎭湭璇绘暟
			holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
			holder.unreadLabel.setVisibility(View.VISIBLE);
		} else {
			holder.unreadLabel.setVisibility(View.INVISIBLE);
		}

		if (conversation.getMsgCount() != 0) {
			// 鎶婃渶鍚庝竴鏉℃秷鎭殑鍐呭浣滀负item鐨刴essage鍐呭
			EMMessage lastMessage = conversation.getLastMessage();
			holder.message.setText(SmileUtils.getSmiledText(getContext(), getMessageDigest(lastMessage, (this.getContext()))),
					BufferType.SPANNABLE);

			holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
			if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
				holder.msgState.setVisibility(View.VISIBLE);
			} else {
				holder.msgState.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

	/**
	 * 鏍规嵁娑堟伅鍐呭鍜屾秷鎭被鍨嬭幏鍙栨秷鎭唴瀹规彁绀�
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case IMAGE: // 图片消息
			ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
			digest = getStrng(context, R.string.picture) + imageBody.getFileName();
			break;
		case VOICE:// 璇煶娑堟伅
			digest = getStrng(context, R.string.voice);
			break;
		case VIDEO: // 瑙嗛娑堟伅
			digest = getStrng(context, R.string.video);
			break;
		case TXT: // 文本消息
			
			if(((TeamRadarHXSDKHelper)HXSDKHelper.getInstance()).isRobotMenuMessage(message)){
				digest = ((TeamRadarHXSDKHelper)HXSDKHelper.getInstance()).getRobotMenuMessageDigest(message);
			}else if(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)){
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
			}else{
				TextMessageBody txtBody = (TextMessageBody) message.getBody();
				digest = txtBody.getMessage();
			}
			break;
		case FILE: // 鏅�氭枃浠舵秷鎭�
			digest = getStrng(context, R.string.file);
			break;
		default:
			EMLog.e(TAG, "unknow type");
			return "";
		}

		return digest;
	}

	private static class ViewHolder {
		/** 鍜岃皝鐨勮亰澶╄褰� */
		TextView name;
		/** 娑堟伅鏈鏁� */
		TextView unreadLabel;
		/** 鏈�鍚庝竴鏉℃秷鎭殑鍐呭 */
		TextView message;
		/** 鏈�鍚庝竴鏉℃秷鎭殑鏃堕棿 */
		TextView time;
		/** 鐢ㄦ埛澶村儚 */
		ImageView avatar;
		/** 鏈�鍚庝竴鏉℃秷鎭殑鍙戦�佺姸鎬� */
		View msgState;
		/** 鏁翠釜list涓瘡涓�琛屾�诲竷灞� */
		RelativeLayout list_item_layout;

	}

	String getStrng(Context context, int resId) {
		return context.getResources().getString(resId);
	}
	
	

	@Override
	public Filter getFilter() {
		if (conversationFilter == null) {
			conversationFilter = new ConversationFilter(conversationList);
		}
		return conversationFilter;
	}
	
	private class ConversationFilter extends Filter {
		List<EMConversation> mOriginalValues = null;

		public ConversationFilter(List<EMConversation> mList) {
			mOriginalValues = mList;
		}

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				mOriginalValues = new ArrayList<EMConversation>();
			}
			if (prefix == null || prefix.length() == 0) {
				results.values = copyConversationList;
				results.count = copyConversationList.size();
			} else {
				String prefixString = prefix.toString();
				final int count = mOriginalValues.size();
				final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

				for (int i = 0; i < count; i++) {
					final EMConversation value = mOriginalValues.get(i);
					String username = value.getUserName();
					
					EMGroup group = EMGroupManager.getInstance().getGroup(username);
					if(group != null){
						username = group.getGroupName();
					}

					// First match against the whole ,non-splitted value
					if (username.startsWith(prefixString)) {
						newValues.add(value);
					} else{
						  final String[] words = username.split(" ");
	                        final int wordCount = words.length;

	                        // Start at index 0, in case valueText starts with space(s)
	                        for (int k = 0; k < wordCount; k++) {
	                            if (words[k].startsWith(prefixString)) {
	                                newValues.add(value);
	                                break;
	                            }
	                        }
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			conversationList.clear();
			conversationList.addAll((List<EMConversation>) results.values);
			if (results.count > 0) {
			    notiyfyByFilter = true;
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}

		}

	}
	
	@Override
	public void notifyDataSetChanged() {
	    super.notifyDataSetChanged();
	    if(!notiyfyByFilter){
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
	}
}

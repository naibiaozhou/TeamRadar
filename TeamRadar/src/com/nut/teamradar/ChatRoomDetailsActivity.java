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
package com.nut.teamradar;

import java.util.List;

import android.R.raw;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatRoom;
import com.easemob.chat.EMGroup;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.nut.teamradar.util.UserUtils;
import com.nut.teamradar.widget.ExpandGridView;

public class ChatRoomDetailsActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "ChatRoomDetailsActivity";
	private static final int REQUEST_CODE_EXIT = 1;
	private static final int REQUEST_CODE_EXIT_DELETE = 2;
	private static final int REQUEST_CODE_CLEAR_ALL_HISTORY = 3;

	String longClickUsername = null;

	private ExpandGridView userGridview;
	private String roomId;
	private ProgressBar loadingPB;
	private Button exitBtn;
	private Button deleteBtn;
	private EMChatRoom room;
	private GridAdapter adapter;
	private int referenceWidth;
	private int referenceHeight;
	private ProgressDialog progressDialog;

	public static ChatRoomDetailsActivity instance;
	
	String st = "";
	// 娓呯┖鎵�鏈夎亰澶╄褰�
	private RelativeLayout clearAllHistory;
	private RelativeLayout blacklistLayout;
	private RelativeLayout changeGroupNameLayout;
	
	private RelativeLayout blockGroupMsgLayout;
	private RelativeLayout showChatRoomIdLayout;
	private TextView chatRoomIdTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_details);
		instance = this;
		st = getResources().getString(R.string.people);
		clearAllHistory = (RelativeLayout) findViewById(R.id.clear_all_history);
		userGridview = (ExpandGridView) findViewById(R.id.gridview);
		userGridview.setVisibility(View.GONE);
		loadingPB = (ProgressBar) findViewById(R.id.progressBar);

		blockGroupMsgLayout = (RelativeLayout)findViewById(R.id.rl_switch_block_groupmsg);

		Drawable referenceDrawable = getResources().getDrawable(R.drawable.smiley_add_btn);
		referenceWidth = referenceDrawable.getIntrinsicWidth();
		referenceHeight = referenceDrawable.getIntrinsicHeight();

		 // 鑾峰彇浼犺繃鏉ョ殑groupid
		 roomId = getIntent().getStringExtra("roomId");
		 
		 showChatRoomIdLayout.setVisibility(View.VISIBLE);
		 chatRoomIdTextView.setText("鑱婂ぉ瀹D锛�"+roomId);
		 
		 room = EMChatManager.getInstance().getChatRoom(roomId);

		exitBtn.setVisibility(View.GONE);
		deleteBtn.setVisibility(View.GONE);
		blacklistLayout.setVisibility(View.GONE);
		changeGroupNameLayout.setVisibility(View.GONE);
		blockGroupMsgLayout.setVisibility(View.GONE);
		
		// 濡傛灉鑷繁鏄兢涓伙紝鏄剧ず瑙ｆ暎鎸夐挳
		if (EMChatManager.getInstance().getCurrentUser().equals(room.getOwner())) {
			exitBtn.setVisibility(View.GONE);
			deleteBtn.setVisibility(View.GONE);
		}
		
		((TextView) findViewById(R.id.group_name)).setText(room.getName() + "(" + room.getAffiliationsCount() + st);
		adapter = new GridAdapter(this, R.layout.grid, room.getMembers());
		userGridview.setAdapter(adapter);
		
		updateRoom();


		// 璁剧疆OnTouchListener
		userGridview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (adapter.isInDeleteMode) {
						adapter.isInDeleteMode = false;
						adapter.notifyDataSetChanged();
						return true;
					}
					break;
				default:
					break;
				}
				return false;
			}
		});

		clearAllHistory.setOnClickListener(this);
		blacklistLayout.setOnClickListener(this);
		changeGroupNameLayout.setOnClickListener(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String st1 = getResources().getString(R.string.being_added);
		String st2 = getResources().getString(R.string.is_quit_the_group_chat);
		String st3 = getResources().getString(R.string.chatting_is_dissolution);
		String st4 = getResources().getString(R.string.are_empty_group_of_news);
		String st5 = getResources().getString(R.string.is_modify_the_group_name);
		final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
		final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);
		String st8 = getResources().getString(R.string.Are_moving_to_blacklist);
		final String st9 = getResources().getString(R.string.failed_to_move_into);
		
		final String stsuccess = getResources().getString(R.string.Move_into_blacklist_success);
		if (resultCode == RESULT_OK) {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(ChatRoomDetailsActivity.this);
				progressDialog.setMessage(st1);
				progressDialog.setCanceledOnTouchOutside(false);
			}
			switch (requestCode) {
			case REQUEST_CODE_EXIT: // 閫�鍑虹兢
				progressDialog.setMessage(st2);
				progressDialog.show();
				exitGrop();
				break;
			case REQUEST_CODE_CLEAR_ALL_HISTORY:
				// 娓呯┖姝ょ兢鑱婄殑鑱婂ぉ璁板綍
				progressDialog.setMessage(st4);
				progressDialog.show();
				clearGroupHistory();
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 鐐瑰嚮閫�鍑虹兢缁勬寜閽�
	 * 
	 * @param view
	 */
	public void exitGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);

	}

	/**
	 * 鐐瑰嚮瑙ｆ暎缇ょ粍鎸夐挳
	 * 
	 * @param view
	 */
	public void exitDeleteGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast", getString(R.string.dissolution_group_hint)),
				REQUEST_CODE_EXIT_DELETE);

	}

	/**
	 * 娓呯┖缇よ亰澶╄褰�
	 */
	public void clearGroupHistory() {
		EMChatManager.getInstance().clearConversation(room.getId());
		progressDialog.dismiss();
	}

	/**
	 * 閫�鍑虹兢缁�
	 * 
	 * @param groupId
	 */
	private void exitGrop() {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMChatManager.getInstance().leaveChatRoom(roomId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
							if(ChatActivity.activityInstance != null)
							    ChatActivity.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "閫�鍑鸿亰澶╁澶辫触: " + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}
	
	protected void updateRoom() {
		new Thread(new Runnable() {
			public void run() {
				try {
					final EMChatRoom returnRoom = EMChatManager.getInstance().fetchChatRoomFromServer(roomId);

					runOnUiThread(new Runnable() {
						public void run() {
							((TextView) findViewById(R.id.group_name)).setText(returnRoom.getName() + "(" + returnRoom.getAffiliationsCount()
									+ "浜�)");
							loadingPB.setVisibility(View.INVISIBLE);
							adapter.notifyDataSetChanged();
							if (EMChatManager.getInstance().getCurrentUser().equals(returnRoom.getOwner())) {
								// 鏄剧ず瑙ｆ暎鎸夐挳
								exitBtn.setVisibility(View.GONE);
								deleteBtn.setVisibility(View.GONE);
							} else {
								// 鏄剧ず閫�鍑烘寜閽�
								exitBtn.setVisibility(View.GONE);
								deleteBtn.setVisibility(View.GONE);

							}
						}
					});

				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							loadingPB.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		}).start();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.clear_all_history: // 娓呯┖鑱婂ぉ璁板綍
			String st9 = getResources().getString(R.string.sure_to_empty_this);
			Intent intent = new Intent(ChatRoomDetailsActivity.this, AlertDialog.class);
			intent.putExtra("cancel", true);
			intent.putExtra("titleIsCancel", true);
			intent.putExtra("msg", st9);
			startActivityForResult(intent, REQUEST_CODE_CLEAR_ALL_HISTORY);
			break;

		default:
			break;
		}

	}

	/**
	 * 缇ょ粍鎴愬憳gridadapter
	 * 
	 * @author admin_new
	 * 
	 */
	private class GridAdapter extends ArrayAdapter<String> {

		private int res;
		public boolean isInDeleteMode;
		private List<String> objects;

		public GridAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
			res = textViewResourceId;
			isInDeleteMode = false;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
		    ViewHolder holder = null;
			if (convertView == null) {
			    holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(res, null);
				holder.imageView = (ImageView) convertView.findViewById(R.id.iv_avatar);
				holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
				holder.badgeDeleteView = (ImageView) convertView.findViewById(R.id.badge_delete);
				convertView.setTag(holder);
			}else{
			    holder = (ViewHolder) convertView.getTag();
			}
			final LinearLayout button = (LinearLayout) convertView.findViewById(R.id.button_avatar);
			// 鏈�鍚庝竴涓猧tem锛屽噺浜烘寜閽�
			if (position == getCount() - 1) {
			    holder.textView.setText("");
				// 璁剧疆鎴愬垹闄ゆ寜閽�
			    holder.imageView.setImageResource(R.drawable.smiley_minus_btn);
//				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_minus_btn, 0, 0);
				// 濡傛灉涓嶆槸鍒涘缓鑰呮垨鑰呮病鏈夌浉搴旀潈闄愶紝涓嶆彁渚涘姞鍑忎汉鎸夐挳
				if (!room.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
					// if current user is not room admin, hide add/remove btn
					convertView.setVisibility(View.INVISIBLE);
				} else { // 鏄剧ず鍒犻櫎鎸夐挳
					if (isInDeleteMode) {
						// 姝ｅ浜庡垹闄ゆā寮忎笅锛岄殣钘忓垹闄ゆ寜閽�
						convertView.setVisibility(View.INVISIBLE);
					} else {
						// 姝ｅ父妯″紡
						convertView.setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
					}
					final String st10 = getResources().getString(R.string.The_delete_button_is_clicked);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EMLog.d(TAG, st10);
							isInDeleteMode = true;
							notifyDataSetChanged();
						}
					});
				}
			} else if (position == getCount() - 2) { // 娣诲姞缇ょ粍鎴愬憳鎸夐挳
			    holder.textView.setText("");
			    holder.imageView.setImageResource(R.drawable.smiley_add_btn);
//				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_add_btn, 0, 0);
				// 濡傛灉涓嶆槸鍒涘缓鑰呮垨鑰呮病鏈夌浉搴旀潈闄�
				if (!room.getOwner().equals(EMChatManager.getInstance().getCurrentUser())) {
					// if current user is not room admin, hide add/remove btn
					convertView.setVisibility(View.INVISIBLE);
				} else {
					// 姝ｅ浜庡垹闄ゆā寮忎笅,闅愯棌娣诲姞鎸夐挳
					if (isInDeleteMode) {
						convertView.setVisibility(View.INVISIBLE);
					} else {
						convertView.setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
					}
					final String st11 = getResources().getString(R.string.Add_a_button_was_clicked);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EMLog.d(TAG, st11);
							// 杩涘叆閫変汉椤甸潰
//							startActivityForResult(
//									(new Intent(ChatRoomDetailsActivity.this, GroupPickContactsActivity.class).putExtra("groupId", groupId)),
//									REQUEST_CODE_ADD_USER);
						}
					});
				}
			} else { // 鏅�歩tem锛屾樉绀虹兢缁勬垚鍛�
				final String username = getItem(position);
				convertView.setVisibility(View.VISIBLE);
				button.setVisibility(View.VISIBLE);
//				Drawable avatar = getResources().getDrawable(R.drawable.default_avatar);
//				avatar.setBounds(0, 0, referenceWidth, referenceHeight);
//				button.setCompoundDrawables(null, avatar, null, null);
				holder.textView.setText(username);
				UserUtils.setUserAvatar(getContext(), username, holder.imageView);
				// demo缇ょ粍鎴愬憳鐨勫ご鍍忛兘鐢ㄩ粯璁ゅご鍍忥紝闇�鐢卞紑鍙戣�呰嚜宸卞幓璁剧疆澶村儚
				if (isInDeleteMode) {
					// 濡傛灉鏄垹闄ゆā寮忎笅锛屾樉绀哄噺浜哄浘鏍�
					convertView.findViewById(R.id.badge_delete).setVisibility(View.VISIBLE);
				} else {
					convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
				}
				final String st12 = getResources().getString(R.string.not_delete_myself);
				final String st13 = getResources().getString(R.string.Are_removed);
				final String st14 = getResources().getString(R.string.Delete_failed);
				final String st15 = getResources().getString(R.string.confirm_the_members);
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (isInDeleteMode) {
							// 濡傛灉鏄垹闄よ嚜宸憋紝return
							if (EMChatManager.getInstance().getCurrentUser().equals(username)) {
								startActivity(new Intent(ChatRoomDetailsActivity.this, AlertDialog.class).putExtra("msg", st12));
								return;
							}
							if (!NetUtils.hasNetwork(getApplicationContext())) {
								Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), 0).show();
								return;
							}
							EMLog.d("room", "remove user from room:" + username);
						} else {
							// 姝ｅ父鎯呭喌涓嬬偣鍑籾ser锛屽彲浠ヨ繘鍏ョ敤鎴疯鎯呮垨鑰呰亰澶╅〉闈㈢瓑绛�
							// startActivity(new
							// Intent(GroupDetailsActivity.this,
							// ChatActivity.class).putExtra("userId",
							// user.getUsername()));

						}
					}
				});

			}
			return convertView;
		}

		@Override
		public int getCount() {
			return super.getCount() + 2;
		}
	}


	public void back(View view) {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}
	
	private static class ViewHolder{
	    ImageView imageView;
	    TextView textView;
	    ImageView badgeDeleteView;
	}

}

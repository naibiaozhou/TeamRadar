package com.nut.teamradar.Fragments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.GroupReomveListener;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.LocationMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VideoMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.EasyUtils;
import com.easemob.util.PathUtil;
import com.easemob.util.VoiceRecorder;
import com.nut.teamradar.ChatActivity;
import com.nut.teamradar.Constant;
import com.nut.teamradar.GroupDetailsActivity;
import com.nut.teamradar.ImageGridActivity;
import com.nut.teamradar.MainActivity;
import com.nut.teamradar.OfflineMapActivity;
import com.nut.teamradar.R;
import com.nut.teamradar.TeamRadarApplication;
import com.nut.teamradar.VideoCallActivity;
import com.nut.teamradar.VoiceCallActivity;
import com.nut.teamradar.adapter.ChatAllHistoryAdapter;
import com.nut.teamradar.adapter.ExpressionAdapter;
import com.nut.teamradar.adapter.ExpressionPagerAdapter;
import com.nut.teamradar.adapter.MessageAdapter;
import com.nut.teamradar.adapter.VoicePlayClickListener;
import com.nut.teamradar.base.BaseUi;
import com.nut.teamradar.db.InviteMessgeDao;
import com.nut.teamradar.model.Location;
import com.nut.teamradar.model.User;
import com.nut.teamradar.util.Code;
import com.nut.teamradar.util.CommonUtils;
import com.nut.teamradar.util.ImageUtils;
import com.nut.teamradar.util.LocationRecordFileNameProcessor;
import com.nut.teamradar.util.LocationRecordReader;
import com.nut.teamradar.util.SmileUtils;
import com.nut.teamradar.webclient.WebAckCallBack;
import com.nut.teamradar.webclient.WebConnection;
import com.nut.teamradar.widget.ExpandGridView;
import com.nut.teamradar.widget.PasteEditText;

public class FragmentChat extends BaseUi {

	private InputMethodManager inputMethodManager;
	private ListView listView;
	private ChatAllHistoryAdapter adapter;
	private EditText query;
	private ImageButton clearSearch;
	public RelativeLayout errorItem;
	public TextView errorText;
	private boolean hidden;
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_chat, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
		inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
		
		conversationList.addAll(loadConversationsWithRecentChat());
		listView = (ListView) getView().findViewById(R.id.list);
		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
		// 鐠佸墽鐤哸dapter
		listView.setAdapter(adapter);
				
		
		final String st2 = getResources().getString(R.string.Cant_chat_with_yourself);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EMConversation conversation = adapter.getItem(position);
				String username = conversation.getUserName();
				if (username.equals(TeamRadarApplication.getInstance().getUserName()))
					Toast.makeText(getActivity(), st2, 0).show();
				else {
				    // 鏉╂稑鍙嗛懕濠傘亯妞ょ敻娼�
				    Intent intent = new Intent(getActivity(), ChatActivity.class);
				    if(conversation.isGroup()){
				        // it is group chat
                        intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                        intent.putExtra("groupId", username);
				    }else{
				        // it is single chat
                        intent.putExtra("userId", username);
				    }
				    startActivity(intent);
				}
			}
		});
		// 濞夈劌鍞芥稉濠佺瑓閺傚洩褰嶉崡锟�
		registerForContextMenu(listView);

		listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 闂呮劘妫屾潪顖炴暛閻╋拷
				hideSoftKeyboard();
				return false;
			}

		});
		// 閹兼粎鍌ㄥ锟�
		query = (EditText) getView().findViewById(R.id.query);
		String strSearch = getResources().getString(R.string.search);
		query.setHint(strSearch);
		// 閹兼粎鍌ㄥ鍡曡厬濞撳懘娅巄utton
		clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
		query.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				adapter.getFilter().filter(s);
				if (s.length() > 0) {
					clearSearch.setVisibility(View.VISIBLE);
				} else {
					clearSearch.setVisibility(View.INVISIBLE);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		clearSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				query.getText().clear();
				hideSoftKeyboard();
			}
		});

	}

	void hideSoftKeyboard() {
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
		getActivity().getMenuInflater().inflate(R.menu.delete_message, menu); 
		// }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean handled = false;
		boolean deleteMessage = false;
		if (item.getItemId() == R.id.delete_message) {
			deleteMessage = true;
			handled = true;
		} else if (item.getItemId() == R.id.delete_conversation) {
			deleteMessage = false;
			handled = true;
		}
		EMConversation tobeDeleteCons = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
		// 閸掔娀娅庡銈勭窗鐠�
		EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), deleteMessage);
		InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
		inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
		adapter.remove(tobeDeleteCons);
		adapter.notifyDataSetChanged();

		// 閺囧瓨鏌婂☉鍫熶紖閺堫亣顕伴弫
		((MainActivity) getActivity()).updateUnreadLabel();
		
		return handled ? true : super.onContextItemSelected(item);
	}

	/**
	 * 閸掗攱鏌婃い鐢告桨
	 */
	public void refresh() {
		conversationList.clear();
		conversationList.addAll(loadConversationsWithRecentChat());
		if(adapter != null)
		    adapter.notifyDataSetChanged();
	}

	/**
	 * 閼惧嘲褰囬幍锟介張澶夌窗鐠囷拷
	 * 
	 * @param context
	 * @return
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        +	 */
	private List<EMConversation> loadConversationsWithRecentChat() {
		// 閼惧嘲褰囬幍鈧張澶夌窗鐠囨繐绱濋崠鍛闂勫瞼鏁撴禍
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		// 鏉╁洦鎶ら幒濉礶ssages size娑�?閻ㄥ垻onversation
		/**
		 * 婵″倹鐏夐崷銊﹀笓鎼村繗绻冪粙瀣╄厬閺堝鏌婂☉鍫熶紖閺�璺哄煂閿涘astMsgTime娴兼艾褰傞悽鐔峰綁閸�
		 * 瑜板崬鎼烽幒鎺戠碍鏉╁洨鈻奸敍瀛媜llection.sort娴兼矮楠囬悽鐔风磽鐢�
		 * 娣囨繆鐦塁onversation閸︹娍ort鏉╁洨鈻兼稉顓熸付閸氬簼绔撮弶鈩冪Х閹垳娈戦弮鍫曟？娑撳秴褰� 
		 * 闁灝鍘ら獮璺哄絺闂傤噣顣�
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			for (EMConversation conversation : conversations.values()) {
				if (conversation.getAllMessages().size() != 0) {
					sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
				}
			}
		}
		try {
			// Internal is TimSort algorithm, has bug
			sortConversationByLastChatTime(sortList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EMConversation> list = new ArrayList<EMConversation>();
		for (Pair<Long, EMConversation> sortItem : sortList) {
			list.add(sortItem.second);
		}
		return list;
	}

	/**
	 * 閺嶈宓侀張锟介崥搴濈閺夆剝绉烽幁顖滄畱閺冨爼妫块幒鎺戠碍
	 * 
	 * @param usernames
	 */
	private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
			@Override
			public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

				if (con1.first == con2.first) {
					return 0;
				} else if (con2.first > con1.first) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden && ! ((MainActivity)getActivity()).isConflict) {
			refresh();
		}
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        if(((MainActivity)getActivity()).isConflict){
        	outState.putBoolean("isConflict", true);
        }else if(((MainActivity)getActivity()).getCurrentAccountRemoved()){
        	outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }

}

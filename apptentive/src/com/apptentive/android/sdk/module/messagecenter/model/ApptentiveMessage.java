/*
 * Copyright (c) 2015, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.messagecenter.model;

import com.apptentive.android.sdk.Log;
import com.apptentive.android.sdk.model.ConversationItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Sky Kelsey
 */
public abstract class ApptentiveMessage extends ConversationItem implements MessageCenterUtil.MessageCenterListItem {

	public static final String KEY_ID = "id";
	public static final String KEY_CREATED_AT = "created_at";
	public static final String KEY_TYPE = "type";
	public static final String KEY_HIDDEN = "hidden";
	public static final String KEY_CUSTOM_DATA = "custom_data";
	public static final String KEY_AUTOMATED = "automated";
	public static final String KEY_SENDER = "sender";
	public static final String KEY_SENDER_ID = "id";

	// State and Read are not stored in JSON, only in DB.
	private State state = State.unknown;
	private boolean read = false;

	// datestamp is only stored in memory, due to how we selectively apply date labeling in the view.
	private String datestamp;

	private static final String KEY_SENDER_NAME = "name";
	private static final String KEY_SENDER_PROFILE_PHOTO = "profile_photo";


	protected ApptentiveMessage() {
		super();
		state = State.sending;
		read = true; // This message originated here.
		setBaseType(BaseType.message);
		initType();
	}

	protected ApptentiveMessage(String json) throws JSONException {
		super(json);
	}

	protected void initBaseType() {
		setBaseType(BaseType.message);
	}

	protected abstract void initType();

	public void setId(String id) {
		try {
			put(KEY_ID, id);
		} catch (JSONException e) {
			Log.e("Exception setting ApptentiveMessage's %s field.", e, KEY_ID);
		}
	}

	public String getId() {
		try {
			if (!isNull((KEY_ID))) {
				return getString(KEY_ID);
			}
		} catch (JSONException e) {
			// Ignore
		}
		return null;
	}

	public Double getCreatedAt() {
		try {
			return getDouble(KEY_CREATED_AT);
		} catch (JSONException e) {
			// Ignore
		}
		return null;
	}

	public void setCreatedAt(Double createdAt) {
		try {
			put(KEY_CREATED_AT, createdAt);
		} catch (JSONException e) {
			Log.e("Exception setting ApptentiveMessage's %s field.", e, KEY_CREATED_AT);
		}
	}

	public Type getType() {
		try {
			if (isNull((KEY_TYPE))) {
				return Type.CompoundMessage;
			}
			return Type.parse(getString(KEY_TYPE));
		} catch (JSONException e) {
			// Ignore
		}
		return Type.unknown;
	}

	protected void setType(Type type) {
		try {
			put(KEY_TYPE, type.name());
		} catch (JSONException e) {
			Log.e("Exception setting ApptentiveMessage's %s field.", e, KEY_TYPE);
		}
	}

	public boolean isHidden() {
		try {
			return getBoolean(KEY_HIDDEN);
		} catch (JSONException e) {
			// Ignore
		}
		return false;
	}

	public void setHidden(boolean hidden) {
		try {
			put(KEY_HIDDEN, hidden);
		} catch (JSONException e) {
			Log.e("Exception setting ApptentiveMessage's %s field.", e, KEY_HIDDEN);
		}
	}

	public void setCustomData(Map<String, Object> customData) {
		if (customData == null || customData.size() == 0) {
			if (!isNull(KEY_CUSTOM_DATA)) {
				remove(KEY_CUSTOM_DATA);
			}
			return;
		}
		try {
			JSONObject customDataJson = new JSONObject();
			for (String key : customData.keySet()) {
				customDataJson.put(key, customData.get(key));
			}
			put(KEY_CUSTOM_DATA, customDataJson);
		} catch (JSONException e) {
			Log.e("Exception setting ApptentiveMessage's %s field.", e, KEY_CUSTOM_DATA);
		}
	}

	public State getState() {
		if (state == null) {
			return State.unknown;
		}
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getSenderId() {
		try {
			if (!isNull((KEY_SENDER))) {
				JSONObject sender = getJSONObject(KEY_SENDER);
				if (!sender.isNull((KEY_SENDER_ID))) {
					return sender.getString(KEY_SENDER_ID);
				}
			}
		} catch (JSONException e) {
			// Ignore
		}
		return null;
	}

	// For debugging only.
	public void setSenderId(String senderId) {
		try {
			JSONObject sender;
			if (!isNull((KEY_SENDER))) {
				sender = getJSONObject(KEY_SENDER);
			} else {
				sender = new JSONObject();
				put(KEY_SENDER, sender);
			}
			sender.put(KEY_SENDER_ID, senderId);
		} catch (JSONException e) {
			Log.e("Exception setting ApptentiveMessage's %s field.", e, KEY_SENDER_ID);
		}
	}

	public String getSenderUsername() {
		try {
			if (!isNull((KEY_SENDER))) {
				JSONObject sender = getJSONObject(KEY_SENDER);
				if (!sender.isNull((KEY_SENDER_NAME))) {
					return sender.getString(KEY_SENDER_NAME);
				}
			}
		} catch (JSONException e) {
			// Ignore
		}
		return null;
	}

	public String getSenderProfilePhoto() {
		try {
			if (!isNull((KEY_SENDER))) {
				JSONObject sender = getJSONObject(KEY_SENDER);
				if (!sender.isNull((KEY_SENDER_PROFILE_PHOTO))) {
					return sender.getString(KEY_SENDER_PROFILE_PHOTO);
				}
			}
		} catch (JSONException e) {
			// Should not happen.
		}
		return null;
	}

	public boolean getAutomated() {
		try {
			if (!isNull((KEY_AUTOMATED))) {
				return getBoolean(KEY_AUTOMATED);
			}
		} catch (JSONException e) {
			// Ignore
		}
		return false;
	}

	public void setAutomated(boolean isAutomated) {
		try {
			put(KEY_AUTOMATED, isAutomated);
		} catch (JSONException e) {
			Log.e("Exception setting ApptentiveMessage's %s field.", e, KEY_AUTOMATED);
		}
	}

	public String getDatestamp() {
		return datestamp;
	}

	public void setDatestamp(String datestamp) {
		this.datestamp = datestamp;
	}

	public void clearDatestamp() {
		this.datestamp = null;
	}

	public abstract boolean isOutgoingMessage();

	public boolean isAutomatedMessage() {
		return getAutomated();
	}


	public enum Type {
		TextMessage,
		FileMessage,
		AutomatedMessage,
		CompoundMessage,
		// Unknown
		unknown;

		public static Type parse(String rawType) {
			try {
				return Type.valueOf(rawType);
			} catch (IllegalArgumentException e) {
				Log.v("Error parsing unknown ApptentiveMessage.Type: " + rawType);
			}
			return unknown;
		}
	}

	public static enum State {
		sending, // The item is either being sent, or is queued for sending.
		sent,    // The item has been posted to the server successfully.
		saved,   // The item has been returned from the server during a fetch.
		unknown;

		public static State parse(String state) {
			try {
				return State.valueOf(state);
			} catch (IllegalArgumentException e) {
				Log.v("Error parsing unknown ApptentiveMessage.State: " + state);
			}
			return unknown;
		}
	}
}

package com.monits.agilefant.parse.adapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.monits.agilefant.model.FilterableTeam;
import com.monits.agilefant.model.FilterableUser;
import com.monits.agilefant.model.User;
import com.monits.agilefant.model.UserChooser;

public class UserChooserDeserializer implements JsonDeserializer<UserChooser> {

	private final static String USER_BASE_CLASS_NAME = "fi.hut.soberit.agilefant.model.User";

	@Override
	public UserChooser deserialize(final JsonElement jElement, final Type type,
			final JsonDeserializationContext jContext) throws JsonParseException {
		final JsonObject jsonObject = jElement.getAsJsonObject();

		final String baseClassName = jsonObject.get("baseClassName").getAsString();

		if (baseClassName.equalsIgnoreCase(USER_BASE_CLASS_NAME)) {
			final long id = jsonObject.get("id").getAsLong();

			final User user = jContext.deserialize(jsonObject.get("originalObject"), User.class);

			return new FilterableUser(
					id,
					jsonObject.get("enabled").getAsBoolean(),
					jsonObject.get("matchedString").getAsString(),
					jsonObject.get("name").getAsString(),
					user);

		} else {
			final JsonArray idList = jsonObject.get("idList").getAsJsonArray();
			final List<Long> usersId = new ArrayList<Long>(idList.size());
			for (int i = 0; i < idList.size(); ++i) {
				usersId.add(idList.get(i).getAsLong());
			}

			final JsonElement descriptionJsonElement =
					jsonObject.getAsJsonObject("originalObject").get("description");

			return new FilterableTeam(
					jsonObject.get("id").getAsLong(),
					jsonObject.get("enabled").getAsBoolean(),
					jsonObject.get("matchedString").getAsString(),
					jsonObject.get("name").getAsString(),
					usersId,
					descriptionJsonElement.isJsonNull() ? "" : descriptionJsonElement.getAsString());
		}
	}

}
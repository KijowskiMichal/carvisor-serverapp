package utilities.jsonparser;

import entities.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import entities.builders.UserBuilder;

public class UserJsonParser {

    private static final String DEF_IMAGE = null;

    public static JSONObject parse(User user) {
        return new JSONObject()
                .put("id", user.getId())
                .put("name", user.getName())
                .put("surname", user.getSurname())
                .put("rate", user.getEcoPointsAvg() / 2.0)
                .put("tracks", user.getTracksNumber())
                .put("combustion", user.getCombustionAVG())
                .put("revolutions", user.getRevolutionsAVG())
                .put("speed", user.getSpeedAVG());
    }

    public static User parseFromRegistrationFrom(JSONObject jsonObject) {
        UserBuilder userBuilder = new UserBuilder()
                .setName(jsonObject.getString("name"))
                .setSurname(jsonObject.getString("surname"))
                .setNick(jsonObject.getString("nick"))
                .setPassword(DigestUtils.sha256Hex(jsonObject.getString("password")))
                .setPhoneNumber(jsonObject.getInt("phoneNumber"));

        if (jsonObject.has("image")) {
            userBuilder.setImage(jsonObject.getString("image"));
        } else {
            userBuilder.setImage(DEF_IMAGE);
        }
        return userBuilder.build();
    }
}

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class ParseHelper {

    public Map<String, Object> jsonStringToMap(String jsonString) throws ParseException {
        Map<String, Object> returnMap = new HashMap<>();
        JSONObject jsonObj = (JSONObject) new JSONParser().parse(jsonString);
        Iterator<Map.Entry<String, Object>> entrySetIterator = jsonObj.entrySet().iterator();

        while(entrySetIterator.hasNext()) {
            Map.Entry<String, Object> next = entrySetIterator.next();
            String key = next.getKey();
            String value = next.getValue().toString();

            if( value.contains("[") &&
                    (value.indexOf("[") < value.indexOf("{")) ) {   //jsonArray일 경우
                List<Map<String, Object>> list = new ArrayList<>();
                JSONArray jsonAry = (JSONArray)new JSONParser().parse(value);

                int count = value.length() - value.replace("{", "").length();
                for(int j = 0; j < count; j++) {
                    list.add(jsonStringToMap(jsonAry.get(j).toString()));
                }
                returnMap.put(key, list);
            } else if (value.contains("{")) {
                returnMap.put(key, jsonStringToMap(value));
            } else {
                returnMap.put(key, value);
            }
        }

        return returnMap;
    }

}
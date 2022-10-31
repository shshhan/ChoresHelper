import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class ParseHelperTest {

    ParseHelper parseHelper;

    public ParseHelperTest() {
        parseHelper = new ParseHelper();
    }

    @Test
    public void jsonStringToMap() throws ParseException {
        String jsonString = "{\"" +
                "id\":1," +
                "\"first_name\":\"Windham\"," +
                "\"last_name\":\"Sherrocks\"," +
                "\"email\":\"wsherrocks0@elpais.com\"," +
                "\"gender\":\"Genderqueer\"," +
                "\"ip_address\":\"106.135.59.71\"" +
                "}";

        Map<String, Object> parsedMap = parseHelper.jsonStringToMap(jsonString);

        assertThat(parsedMap.size()).isEqualTo(6);
        assertThat(parsedMap.get("first_name")).isEqualTo("Windham");

    }
}
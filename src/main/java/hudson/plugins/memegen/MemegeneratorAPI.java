/*
 * The MIT License
 *
 * Copyright 2012 Jon Cairns <jon.cairns@22blue.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.memegen;

import java.util.*;
import java.net.URL;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class MemegeneratorResponseException extends Exception {
	MemegeneratorResponseException(JSONObject obj) {
		super(obj.get("errorMessage")+", full response: "+obj.toJSONString());
	}
}
class MemegeneratorJSONException extends Exception {
	MemegeneratorJSONException(JSONObject obj, String key) {
		super("Missing key in JSON response: "+key+", full response: "+obj.toJSONString());
	}
}
/**
 *
 * @author Jon Cairns <jon.cairns@22blue.co.uk>
 */
public class MemegeneratorAPI {
	private static final String APIURL = "http://version1.api.memegenerator.net";

	private String username;
	private String password;

	MemegeneratorAPI(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public boolean instanceCreate(Meme meme) {
		boolean ret = false;
		HashMap<String,String> vars = new HashMap();
		vars.put("username", username);
		vars.put("password", password);
		vars.put("text0", meme.getUpperText());
		vars.put("text1", meme.getLowerText());
		vars.put("generatorID", ""+meme.getGeneratorID());
		vars.put("imageID", ""+meme.getImageID());

		try {
			URL url = buildURL("Instance_Create",vars);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			conn.setDoOutput(true);
			JSONObject obj = parseResponse(conn);
			JSONObject result = (JSONObject)obj.get("result");
			String memeUrl = (String)result.get("instanceImageUrl");
			if (memeUrl.matches("^http(.*)") == false) {
				memeUrl = APIURL + memeUrl;
			}
			//Debug the JSON to logs
			//System.err.println(obj.toJSONString()+", AND "+result.toJSONString());
			meme.setImageURL(memeUrl);
			ret = true;

		} catch (MalformedURLException me) {
			String log_warn_prefix = "Memegenerator API malformed URL: ";
			System.err.println(log_warn_prefix.concat(me.getMessage()));
		} catch (IOException ie) {
			String log_warn_prefix = "Memegenerator API IO exception: ";
			System.err.println(log_warn_prefix.concat(ie.getMessage()));
		} catch (ParseException pe) {
			String log_warn_prefix = "Memegenerator API malformed response: ";
			System.err.println(log_warn_prefix.concat(pe.getMessage()));
		} catch (MemegeneratorResponseException mre) {
			String log_warn_prefix = "Memegenerator API failure: ";
			System.err.println(log_warn_prefix.concat(mre.getMessage()));
		} catch (MemegeneratorJSONException mje) {
			String log_warn_prefix = "Memegenerator API malformed JSON: ";
			System.err.println(log_warn_prefix.concat(mje.getMessage()));
		}

		return ret;
	}

	protected URL buildURL(String action, HashMap<String, String> map)
		throws MalformedURLException, IOException {

		String getVars = "";
		Set s = map.entrySet();
		Iterator i = s.iterator();
		while (i.hasNext()) {
			Map.Entry var = (Map.Entry)i.next();
			getVars += URLEncoder.encode((String)var.getKey(),"UTF-8")+"="+URLEncoder.encode((String)var.getValue(),"UTF-8")+"&";
		}
		URL url = new URL(APIURL+"/"+action+"?"+getVars);
		return url;
	}

	protected JSONObject parseResponse(HttpURLConnection conn)
		throws ParseException, IOException, MemegeneratorResponseException,MemegeneratorJSONException {

		Reader rd = new InputStreamReader(conn.getInputStream());
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(rd);

		JSONObject jsonObject = (JSONObject) obj;
		if (!jsonObject.containsKey("success")) {
			throw new MemegeneratorJSONException(jsonObject,"success");
		}
		if (!jsonObject.get("success").equals(true)) {
			throw new MemegeneratorResponseException(jsonObject);
		}

		if (!jsonObject.containsKey("result")) {
			throw new MemegeneratorJSONException(jsonObject,"result");
		}

		return jsonObject;
	}
}

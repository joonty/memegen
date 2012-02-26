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

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.User;
import java.util.Set;
import java.util.Iterator;
import java.util.Calendar;
import java.util.Random;
/**
 *
 * @author Jon Cairns <jon.cairns@22blue.co.uk>
 */
public class MemeFactory {

	public static Meme getMeme(AbstractBuild build) {
		String resultString = build.getResult().toString();
		String[] sel = selectConfig(build.getResult());
		String buildName = build.getDisplayName();
		String projectName = build.getProject().getDisplayName();
		String users = userSetToString(build.getCulprits());
		String text0 = textReplace(sel[2],buildName,projectName,users);
		String text1 = textReplace(sel[3],buildName,projectName,users);
		return new Meme(Integer.parseInt(sel[0]),Integer.parseInt(sel[1]),text0,text1);
	}

	protected static String[] selectConfig(Result type) {
		String[][] config;
		if (type == Result.FAILURE) {
			config = new String[][] {
				{"74","2485","I don\'t usually commit", "but when I do, I break the build"},
				{"74","2485","%projectName%?", "I didn\'t like it anyway"},
				{"74","2485","I don\'t usually break the build", "but when I do, I do it on a %day%"},
				{"74","2485","Testing?", "I prefer to leave that until production"}
			};
		} else {
			config = new String[][] {
				{"74","2485","Build %buildNumber%","Have a drink"}
			};
		}

		Random rand = new Random();
		int key = rand.nextInt(config.length);
		return config[key];
	}

	protected static String textReplace(String input, String buildNumber, String projectName) {
		String text = new String(input);
		if (text.matches("day")) {
			Calendar now = Calendar.getInstance();

			//create an array of days
			String[] strDays = new String[]{
				"Sunday",
				"Monday",
				"Tuesday",
				"Wednesday",
				"Thusday",
				"Friday",
				"Saturday"
			};

			String day = strDays[now.get(Calendar.DAY_OF_WEEK) - 1];
			text = text.replace("%day%",day);
		}
		text = text.replace("%buildNumber%", buildNumber);
		text = text.replace("%projectName%", projectName);
		return text;
	}

	protected static String textReplace(String input, String buildNumber, String projectName, String user) {
		String text = textReplace(input,buildNumber,projectName);
		return text.replace("%user%", user);
	}

	protected static String userSetToString(Set userSet) {
		Iterator i = userSet.iterator();
		String ret = "";
		int idx = 0;
		while (i.hasNext()) {
			User user = (User)i.next();
			if (idx > 0) {
				ret += ", ";
			}
			ret += user.getDisplayName();
			idx++;
		}
		if (ret.isEmpty()) {
			ret = "Nobody";
		}
		return ret;
	}
}

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

import java.io.Serializable;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * @author Jon Cairns <jon.cairns@22blue.co.uk>
 */
public class Meme implements Serializable {
	protected int generatorID;
	protected int imageID;
	protected boolean parsed = false;
	public String identifier;
	public String upperText;
	public String lowerText;

	protected String imageURL;

	public Meme() {}

	@DataBoundConstructor
	public Meme(String identifier, String lowerText, String upperText) {
		this.identifier = identifier;
		this.upperText = upperText;
		this.lowerText = lowerText;
	}

	protected void parseIdentifier() {
		if (!parsed) {
			String[] id_r = identifier.split("-");
			generatorID = Integer.parseInt(id_r[0]);
			imageID = Integer.parseInt(id_r[1]);
			parsed = true;
		}
	}

	public int getGeneratorID() {
		parseIdentifier();
		return generatorID;
	}

	public int getImageID() {
		parseIdentifier();
		return imageID;
	}

	public String getUpperText() {
		return upperText;
	}

	public String getLowerText() {
		return lowerText;
	}

	public void setImageURL(String URL) {
		imageURL = URL;
	}

	public String getImageURL() {
		return imageURL;
	}

	public Meme clone() {
		return new Meme(identifier,lowerText,upperText);
	} 
}

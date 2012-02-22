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

/**
 *
 * @author Jon Cairns <jon.cairns@22blue.co.uk>
 */
public class Meme {
	protected int generatorID;
	protected int imageID;
	protected String upperText;
	protected String lowerText;

	protected String imageURL;

	Meme(int generatorID, int imageID, String upperText, String lowerText) {
		this.generatorID = generatorID;
		this.imageID = imageID;
		this.upperText = upperText;
		this.lowerText = lowerText;
	}

	public int getGeneratorID() {
		return generatorID;
	}

	public int getImageID() {
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
}

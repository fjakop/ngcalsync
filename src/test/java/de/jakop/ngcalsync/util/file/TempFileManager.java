/**
 * Copyright © 2012, Frank Jakop
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.jakop.ngcalsync.util.file;

import java.io.File;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author fjakop
 *
 */
public class TempFileManager {

	private File tempdir;

	/**
	 * 
	 * @return a temporary directory which is not yet created
	 */
	public File getTempDir() {
		if (tempdir == null || !tempdir.isDirectory()) {

			final long time = System.currentTimeMillis();
			final String dirname = "temp" + time;

			final String settingsDir = System.getProperty("java.io.tmpdir") + File.separator + dirname;
			tempdir = new File(settingsDir);
		}

		return tempdir;
	}

	/**
	 * deletes the temporary directory recursively and quietly
	 */
	public void deleteTempDir() {
		FileUtils.deleteQuietly(tempdir);
	}

	/**
	 * Sets the user's home to the temporary directory
	 */
	public void setUserHomeToTempDir() {
		// we do not want to create temp files in the user's home, so let's set user.home to java.io.tmpdir
		System.setProperty("user.home", getTempDir().getAbsolutePath());
	}
}

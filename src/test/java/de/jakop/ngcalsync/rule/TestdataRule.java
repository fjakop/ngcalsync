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
package de.jakop.ngcalsync.rule;

import java.io.File;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * {@link TestRule} to access files under src/test/resources according tothe package and class name 
 * of the curent test class.
 *
 * @author fjakop
 */
public class TestdataRule implements TestRule {

	private String classpath;
	private String methodpath;

	/**
	 * Given that a file named 'foo/bar.txt' is to be accessed by a test method <code>testSomething()</code>in a
	 * class named <code>de.jakop.my.CoolTest</code> the file 'src/test/resources/de/jakop/my/CoolTest/testSomething/foo/bar.txt'
	 * is returned.
	 * 
	 * @param pathname the relative path to the file
	 * @return the file
	 */
	public final File getFile(final String pathname) {
		final String testdata_pattern = "/src/test/resources/%s/%s/%s";
		return new File(new File("."), String.format(testdata_pattern, classpath, methodpath, pathname));
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				classpath = description.getTestClass().getName();
				methodpath = description.getMethodName();
				base.evaluate();
			}
		};
	}

}
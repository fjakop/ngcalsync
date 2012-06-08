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
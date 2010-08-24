
package com.xpn.xwiki.wiked.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.xpn.xwiki.wiked.test.ui.ImageRegistryTest;
import com.xpn.xwiki.wiked.test.ui.WikedPropertyPageTest;
import com.xpn.xwiki.wiked.test.wikip.WikiParserTest;
import com.xpn.xwiki.wiked.test.xwt.ButtonFactoryTest;
import com.xpn.xwiki.wiked.test.xwt.CompositeBuilderTest;
import com.xpn.xwiki.wiked.test.xwt.CompositeFactoryTest;
import com.xpn.xwiki.wiked.test.xwt.InitFactoryTest;
import com.xpn.xwiki.wiked.test.xwt.LabelFactoryTest;
import com.xpn.xwiki.wiked.test.xwt.ModifyListenerTest;
import com.xpn.xwiki.wiked.test.xwt.SWTPropertyReaderTest;
import com.xpn.xwiki.wiked.test.xwt.TextFactoryTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.xpn.xwiki.wiked.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(ImageRegistryTest.class);
		suite.addTestSuite(WikedPropertyPageTest.class);
		suite.addTestSuite(WikiParserTest.class);
		suite.addTestSuite(SWTPropertyReaderTest.class);
		suite.addTestSuite(LabelFactoryTest.class);
		suite.addTestSuite(ModifyListenerTest.class);
		suite.addTestSuite(CompositeBuilderTest.class);
		suite.addTestSuite(CompositeFactoryTest.class);
		suite.addTestSuite(ButtonFactoryTest.class);
		suite.addTestSuite(TextFactoryTest.class);
		suite.addTestSuite(InitFactoryTest.class);
		//$JUnit-END$
		return suite;
	}

}

package eu.choreos;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import eu.choreos.vv.Item;
import eu.choreos.vv.WSClient;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() throws Exception
    {
	 WSClient ws = new WSClient("http://localhost:1234/SimpleStore?wsdl");
	 Item cd = ws.request("searchByArtist", "Floyd");
		assertEquals("The dark side of the moon;", cd.getChild("return").getContent());	

    }
}

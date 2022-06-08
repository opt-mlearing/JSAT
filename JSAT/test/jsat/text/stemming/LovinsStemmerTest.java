package jsat.text.stemming;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff
 */
public class LovinsStemmerTest {

    public LovinsStemmerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of stem method, of class LovinsStemmer.
     */
    @Test
    public void testStem() {
        System.out.println("stem");
        String[] origSent = ("such an analysis can reveal features that are not easily visible "
                + "from the variations in the individual genes and can lead to a picture of "
                + "expression that is more biologically transparent and accessible to "
                + "interpretation").split(" ");
        LovinsStemmer instance = new LovinsStemmer();
        String[] expResult = ("such an analys can reve featur that ar not eas vis from th "
                + "vari in th individu gen and can lead to a pictur of expres that is mor "
                + "biolog transpar and acces to interpres").split(" ");

        for (int i = 0; i < origSent.length; i++)
            assertEquals(expResult[i], instance.stem(origSent[i]));
    }

}

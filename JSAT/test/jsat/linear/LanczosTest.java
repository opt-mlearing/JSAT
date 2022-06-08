/*
 * This implementation has been contributed under the Public Domain.
 */
package jsat.linear;

import java.util.Random;

import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author edwardraff
 */
public class LanczosTest {

    public LanczosTest() {
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

    @Test
    public void testImplicitSymmetric() {
        System.out.println("test Implicit Symmetric case");
        Random rand = new Random(123);

        Matrix A = DenseMatrix.random(70, 40, rand);
        int k = 4;

        Matrix A_AT = A.multiplyTranspose(A);
        Matrix AT_A = A.transposeMultiply(A);

        Lanczos AAT_explicit = new Lanczos(A_AT, k, true, true);
        Lanczos AAT_implicit = new Lanczos(A, k, true, false);

        Vec expected = AAT_explicit.getEigenValues();
        Vec result = AAT_implicit.getEigenValues();

        Vec relative_diffs = expected.subtract(result).pairwiseDivide(expected);

        for (int i = 0; i < k; i++)
            assertEquals(0, relative_diffs.get(i), 0.1);

        Lanczos ATA_explicit = new Lanczos(AT_A, k, false, true);
        Lanczos ATA_implicit = new Lanczos(A, k, false, false);

        expected = ATA_explicit.getEigenValues();
        result = ATA_implicit.getEigenValues();

        relative_diffs = expected.subtract(result).pairwiseDivide(expected);

        for (int i = 0; i < k; i++)
            assertEquals(0, relative_diffs.get(i), 0.1);

    }

    @Test
    public void testSymmetric() {
        System.out.println("test Symmetric case");
        //Generate a eigen value decomposition backwards, randomly creating V and D and then working to A, then decompositing A and checking the results
        Random rand = new Random(123);


        Matrix D = Matrix.diag(DenseVector.random(70, rand).multiply(10));

        D.set(0, 0, 900);
        D.set(1, 1, 700);
        D.set(2, 2, 400);
        D.set(3, 3, 300);

        int k = 4;

        //Lazy construction of orthonormal matrix to be eigen vectors
        Matrix V = Matrix.random(70, 70, rand);
        SingularValueDecomposition svd = new SingularValueDecomposition(V.clone());
        V = svd.getU();

        Matrix A = V.multiply(D).multiply(V.transpose());

        Lanczos lanczos = new Lanczos(A, k, false, true);

        Vec eigen_values = lanczos.getEigenValues();
        for (int i = 0; i < k; i++)
            assertEquals(D.get(i, i), eigen_values.get(i), 0.05);

        //Do the eigen values match up too? 
        for (int j = 0; j < V.rows(); j++) {
            Vec expected = new SubVector(0, k, V.getRow(j));
            Vec result = lanczos.getEigenVectors().getRow(j);

            expected.applyFunction(Math::abs);
            result.applyFunction(Math::abs);

            Vec relative_diffs = expected.subtract(result).pairwiseDivide(expected);
            for (int i = 0; i < k; i++)
                assertEquals(0, relative_diffs.get(i), 0.1);
        }


        //Test full decomposition for sanity
        lanczos = new Lanczos(A, A.rows(), false, true);
        Matrix A_rec = lanczos.getEigenVectors().clone();
        Matrix.diagMult(A_rec, lanczos.getEigenValues());
        A_rec = A_rec.multiplyTranspose(lanczos.getEigenVectors());
        // diff in norms should be relatively near zero, adjusted for magnitud of input
        assertEquals(0.0, A.subtract(A_rec).frobenius() / A.frobenius(), 0.01);

    }

}

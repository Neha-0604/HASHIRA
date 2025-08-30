import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    
    static BigInteger[] solvePolynomial(List<long[]> shares, int degree) {
        int n = shares.size();
        BigInteger[][] A = new BigInteger[n][degree + 1];
        BigInteger[] b = new BigInteger[n];

        for (int i = 0; i < n; i++) {
            long x = shares.get(i)[0];
            BigInteger y = BigInteger.valueOf(shares.get(i)[1]);
            b[i] = y;
            for (int j = 0; j <= degree; j++) {
                A[i][j] = BigInteger.valueOf(x).pow(j);
            }
        }
        return gaussianSolve(A, b, degree + 1);
    }

    
    static BigInteger[] gaussianSolve(BigInteger[][] A, BigInteger[] b, int vars) {
        int n = b.length;
        BigInteger[][] aug = new BigInteger[n][vars + 1];

        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, vars);
            aug[i][vars] = b[i];
        }

        int row = 0;
        for (int col = 0; col < vars && row < n; col++) {
            int sel = row;
            while (sel < n && aug[sel][col].equals(BigInteger.ZERO)) sel++;
            if (sel == n) continue;

            BigInteger[] tmp = aug[sel]; aug[sel] = aug[row]; aug[row] = tmp;

            BigInteger div = aug[row][col];
            if (!div.equals(BigInteger.ZERO)) {
                for (int j = col; j <= vars; j++) {
                    if (!aug[row][j].equals(BigInteger.ZERO))
                        aug[row][j] = aug[row][j].divide(div);
                }
            }

            for (int i = 0; i < n; i++) {
                if (i != row) {
                    BigInteger factor = aug[i][col];
                    if (!factor.equals(BigInteger.ZERO)) {
                        for (int j = col; j <= vars; j++) {
                            aug[i][j] = aug[i][j].subtract(factor.multiply(aug[row][j]));
                        }
                    }
                }
            }
            row++;
        }

        BigInteger[] x = new BigInteger[vars];
        for (int i = 0; i < vars; i++) {
            x[i] = aug[i][vars];
        }
        return x;
    }

    
    static String readFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    public static void main(String[] args) {
        try {
            
            String json = readFile("testcase1.json");
            JSONObject obj = new JSONObject(json);

            int n = obj.getInt("n");
            int k = obj.getInt("k");
            int degree = k - 1;

            List<long[]> shares = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                int base = obj.getInt(i + "base");
                String valStr = obj.getString(i + "value");
                long x = i;
                BigInteger y = new BigInteger(valStr, base);
                shares.add(new long[]{x, y.longValue()});
            }

            BigInteger[] coeffs = solvePolynomial(shares.subList(0, k), degree);

            System.out.println("Polynomial coefficients: " + Arrays.toString(coeffs));
            System.out.println("Secret (c): " + coeffs[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

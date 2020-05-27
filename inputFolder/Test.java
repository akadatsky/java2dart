// filler filler filler filler filler filler filler filler filler filler
package test;

public class Test {

  /**
   * test
   * @param i
   */
    public Test(int i) {
    }

    public boolean foo() {
        return false;
    }

    public static main() {
        Test v = new Test(42) {
            public boolean foo() {
                return true;
            }
        };
    }

}
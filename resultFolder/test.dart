class Test {
  /**
   * test
   * @param i
   */
  Test(int i);
  bool foo() => false;
  static main() {
    Test v = new Test_main(42);
  }
}
class Test_main extends Test {
  Test_main(int arg0) : super(arg0);
  bool foo() => true;
}
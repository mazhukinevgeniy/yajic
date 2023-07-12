public class Unused {

    //inner class with anonymous class inside. we must reach the anonymous class when analyzing bytecode
    private class Internal {
        public void doNothing() {
            IProvider provider = new IProvider() {
                public int foo() {
                    var temporary = new Library().getInfo();
                    return 1;
                }
            };
        }
    }
}

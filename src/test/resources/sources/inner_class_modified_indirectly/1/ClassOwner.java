public class ClassOwner {
    public String getInfo() {
        return new InnerClass().getInfo();
    }

    public class InnerClass {
        public String getInfo() {
            StringBuilder builder = new StringBuilder();
            builder.append(new Library().getInfo());
            return builder.toString();
        }
    }
}

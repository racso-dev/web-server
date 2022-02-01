import src.MimeParser;

class WebServer {
  public static void main(String[] args) {
    MimeParser mimeParser = new MimeParser("salut");
    mimeParser.testMethod();
  }
}

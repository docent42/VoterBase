import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler
{
    int limit = 500_000;
    int number = 0;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        try
        {
            if (qName.equals("voter") && number < limit)
            {
                String birthDay = attributes.getValue("birthDay");
                String name = attributes.getValue("name");
                DBconnection.countVoter(name,birthDay);
                number++;
                if (number % 10000 == 0) {
                    System.out.printf("<-------------- %d /%.3f sec/ -------------->%n"
                            ,number,(double)(System.currentTimeMillis()-Loader.timer)/1000);
                    Loader.timer = System.currentTimeMillis();
                }
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

}

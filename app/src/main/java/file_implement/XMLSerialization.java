package file_implement;

import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;
import static android.content.Context.MODE_PRIVATE;

import com.example.lab_1.MyObject;
import com.example.lab_1.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

public class XMLSerialization {
    private static final String xml_objects_tag = "objects";
    private static final String xml_object_tag = "object";
    private static final String xml_name_tag = "name";
    private static final String xml_number_tag = "number";
    private static final String xml_logic_tag = "logic";
    private static final String xml_id_attribute = "_id";

    public static void saveData(Context context, String filename, ArrayList<MyObject> myObjects) {
        if (context == null || TextUtils.isEmpty(filename) || myObjects == null) return;
        try ( BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(context.openFileOutput("listData.xml", MODE_PRIVATE)))){
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, xml_objects_tag);
            for (int i = 0; i < myObjects.size(); i++) {
                xmlSerializer.startTag(null, xml_object_tag);
                xmlSerializer.startTag(null, xml_name_tag);
                xmlSerializer.text(myObjects.get(i).getName());
                xmlSerializer.endTag(null, xml_name_tag);
                xmlSerializer.startTag(null, xml_number_tag);
                xmlSerializer.text(String.valueOf(myObjects.get(i).getNumber()));
                xmlSerializer.endTag(null, xml_number_tag);
                xmlSerializer.startTag(null, xml_logic_tag);
                xmlSerializer.text(String.valueOf(myObjects.get(i).isLogic()));
                xmlSerializer.endTag(null, xml_logic_tag);
                xmlSerializer.endTag(null, xml_object_tag);
            }
            xmlSerializer.endTag(null, xml_objects_tag);
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            bufferedWriter.write(writer.toString());
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<MyObject> loadData(Context context, String filename) {
        if (context == null || TextUtils.isEmpty(filename)) return null;
        String data = null;
        try (FileInputStream fis = context.openFileInput(filename);
             InputStreamReader isr = new InputStreamReader(fis)) {
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            data = new String(inputBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<MyObject> myObjects = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            // включаем поддержку namespace (по умолчанию выключена)
            factory.setNamespaceAware(true);
            // создаем парсер
            XmlPullParser xpp = factory.newPullParser();
            // даем парсеру на вход Reader
            if (!TextUtils.isEmpty(data)) {
                xpp.setInput(new StringReader(data));

                MyObject object = null;
                String value = null;
                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    switch (xpp.getEventType()) {
                        case XmlPullParser.START_TAG:
                            switch (xpp.getName()) {
                                case xml_objects_tag: {
                                    myObjects = new ArrayList<>();
                                    break;
                                }
                                case xml_object_tag: {
                                    object = new MyObject();
                                    myObjects.add(object);
                                    break;
                                }
                                default:
                                    break;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            switch (xpp.getName()) {
                                case xml_name_tag: {
                                    if (object != null && value != null) object.setName(value);
                                    break;
                                }
                                case xml_number_tag: {
                                    if (object != null && value != null)
                                        object.setNumber(Double.parseDouble(value));
                                    break;
                                }
                                case xml_logic_tag:l: {
                                    if (object != null && value != null)
                                        object.setLogic(Boolean.parseBoolean(value));
                                    break;
                                }
                            }
                            break;
                        case XmlPullParser.TEXT:
                            value = xpp.getText();
                            break;
                        default:
                            break;
                    }
                    // следующий элемент
                    xpp.next();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return myObjects;
    }
}

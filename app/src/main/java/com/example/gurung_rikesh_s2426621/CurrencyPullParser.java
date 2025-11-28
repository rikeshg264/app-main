package com.example.gurung_rikesh_s2426621;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class CurrencyPullParser {

    public ArrayList<CurrencyFx> parse(String xmlData) {
        ArrayList<CurrencyFx> list = new ArrayList<>();
        CurrencyFx item = null;

        try {
            XmlPullParserFactory factory =
                    XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    String tag = xpp.getName();

                    if (tag.equalsIgnoreCase("item")) {
                        item = new CurrencyFx(); // NEW ITEM
                    }
                    else if (item != null) {
                        if (tag.equalsIgnoreCase("title")) {
                            item.title = xpp.nextText();
                        }
                        else if (tag.equalsIgnoreCase("description")) {
                            item.description = xpp.nextText();
                            item.rateValue = extractRate(item.description);
                        }
                        else if (tag.equalsIgnoreCase("pubDate")) {
                            item.pubDate = xpp.nextText();
                        }
                    }
                }
                else if (eventType == XmlPullParser.END_TAG) {
                    String tag = xpp.getName();

                    if (tag.equalsIgnoreCase("item") && item != null) {
                        list.add(item); // ADD FINISHED ITEM
                    }
                }

                eventType = xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private double extractRate(String text) {
        try {
            // Example: "1 GBP = 1.23456 USD"
            String[] parts = text.split("=");
            String right = parts[1].trim();
            String number = right.split(" ")[0];
            return Double.parseDouble(number);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
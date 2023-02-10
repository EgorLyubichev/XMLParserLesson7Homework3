package by.lev;

import org.junit.Assert;
import org.junit.Test;
import parser.XMLParser;
import parser.XMLTag;
import utilities.Iterator;
import utilities.ListADT;
import utilities.MyArrayList;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class XMLParserTest {

    @Test
    public void showMeYourLines() {

        List<String> example = new ArrayList<>();
        example.add("<?Instruction?>");
        example.add("<bookstore>");
        example.add("    <book>Book</book>");
        example.add("</bookstore>");

        List<String> toCompare = new ArrayList<>();

        XMLParser parser = new XMLParser("src/main/java/xmlfiles/example1.xml");

        MyArrayList<String> destination;

        try {
            Field field = parser.getClass().getDeclaredField("lines");
            field.setAccessible(true);
            destination = (MyArrayList<String>) field.get(parser);

            Iterator<String> iterator = destination.iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (!line.isEmpty())
                    toCompare.add(line);
            }


        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        Assert.assertArrayEquals(example.toArray(), toCompare.toArray());
    }

    @Test
    public void getOneLongLineFromFile() {
        String expected = "<?Instruction?>\n" +
                "<bookstore>\n" +
                "    <book>Book</book>\n" +
                "</bookstore>";

        String result;

        XMLParser parser = new XMLParser("src/main/java/xmlfiles/example1.xml");

        try {
            Method method = parser.getClass().getDeclaredMethod("transformLinesToOne");
            method.setAccessible(true);
            result = (String) method.invoke(parser);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testSkipInstructionTag() {
        XMLParser parser = new XMLParser("src/main/java/xmlfiles/example1.xml");
        int expected = 15;
        int resultCurrentPosition;

        try {

            Method transformLinesToOne = parser.getClass().getDeclaredMethod("transformLinesToOne");
            transformLinesToOne.setAccessible(true);
            String result = (String) transformLinesToOne.invoke(parser);
            Method skipInstructionTag = parser.getClass().getDeclaredMethod("skipInstructionTag");
            skipInstructionTag.setAccessible(true);
            skipInstructionTag.invoke(parser);
            Field currentParsingPosition = parser.getClass().getDeclaredField("currentParsingPosition");
            currentParsingPosition.setAccessible(true);
            resultCurrentPosition = currentParsingPosition.getInt(parser);
        } catch (NoSuchMethodException
                 | IllegalAccessException
                 | InvocationTargetException
                 | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(expected, resultCurrentPosition);
    }

    @Test
    public void testGetCorrectTagName(){
        XMLParser parser = new XMLParser("src/main/java/xmlfiles/example1.xml");

        try {
            Field  currentParsingPosition = parser.getClass().getDeclaredField("currentParsingPosition");
            currentParsingPosition.setAccessible(true);
            currentParsingPosition.setInt(parser, 16);

            Field document = parser.getClass().getDeclaredField("document");
            document.setAccessible(true);
            String documentLine =
                    "<?Instruction?>\n" +
                            "<bookstore>\n" +
                            "    <book>Book</book>\n" +
                            "</bookstore>";
            document.set(parser, documentLine);

            Method getTagName = parser.getClass().getDeclaredMethod("getTagName");
            getTagName.setAccessible(true);
            String firstTagInTheLine = (String) getTagName.invoke(parser);
            System.out.println(firstTagInTheLine);

        } catch (NoSuchFieldException
                 | IllegalAccessException
                 | NoSuchMethodException
                 | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testShowMeTags(){
        XMLParser parser = new XMLParser("src/main/java/xmlfiles/example3.xml");
        parser.parseDocument();

        try {
            Field root = parser.getClass().getDeclaredField("root");
            root.setAccessible(true);
            XMLTag xmlTag = (XMLTag) root.get(parser);
            System.out.println("name: " + xmlTag.getName());
            ListADT<XMLTag> nested = xmlTag.getNestedTags();
            Iterator<XMLTag> iterator = nested.iterator();
            while (iterator.hasNext()){
                System.out.println("nested tag: " + iterator.next().getName());
            }

            ListADT<XMLTag.TagProperty> property = xmlTag.getProperties();
            Iterator<XMLTag.TagProperty> propertyIterator = property.iterator();
            while (propertyIterator.hasNext()){
                System.out.println("property tag: " + propertyIterator.next().getName()
                        + "value " + propertyIterator.next().getValue());
            }




        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }



    }

}

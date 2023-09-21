
import java.io.*;
import java.util.Iterator;

import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class OsmParser {
        public static void osmToBlocks(String osm,String blocks) {

            try {
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader = factory.createXMLStreamReader(new FileReader(osm));

                boolean insideNode = false;
                String nodeId = null;
                String lat = null;
                String lon = null;
                String name = null;

                int currentBytes = 999999999;
                int currentBlock = 1;
                int totalBytes = 0;
                int records = 0;
                String blocksData = "";

                while (reader.hasNext()) {
                    int event = reader.next();

                    switch (event) {
                        case XMLStreamConstants.START_ELEMENT:
                            String elementName = reader.getLocalName();
                            if ("node".equals(elementName)) {
                                insideNode = true;
                                nodeId = reader.getAttributeValue(null, "id");
                                lat = reader.getAttributeValue(null, "lat");
                                lon = reader.getAttributeValue(null, "lon");
                            } else if (insideNode && "tag".equals(elementName)) {
                                String key = reader.getAttributeValue(null, "k");
                                String value = reader.getAttributeValue(null, "v");
                                if ("name".equals(key)) {
                                    name = value;
                                }
                            }
                            break;

                        case XMLStreamConstants.END_ELEMENT:
                            if ("node".equals(reader.getLocalName())) {
                                insideNode = false;
                                String nodeData = "{"+ nodeId + ","+ (name == null ? nodeId : name) + ","+ lat + ","+ lon + "}"; // $$C{Node ID}{Name}{Latitude}{Longitude}
                                byte[] nodeBinary = nodeData.getBytes();
                                //try (FileOutputStream fos = new FileOutputStream(blocks,true)) {
                                    if (currentBytes + nodeBinary.length > 32000){
                                        String block = "$$"+ currentBlock++ ;
                                        //fos.write(block.getBytes());
                                        blocksData = blocksData.concat(block);
                                        currentBytes = 0;
                                    }
                                    //fos.write(nodeBinary);
                                    blocksData = blocksData.concat(nodeData);
                                    records++;
                                    currentBytes = currentBytes + nodeBinary.length;
                                    totalBytes = totalBytes + nodeBinary.length;
                                //} catch (IOException e) {
                                //    e.printStackTrace();
                                //}
                                // Reset the name for the next node
                                name = null;
                            }
                            break;
                    }
                }
                try (FileOutputStream fos = new FileOutputStream(blocks)) {
                    fos.write((("$$0{"+ currentBlock +"}{"+records+"}").concat(blocksData)).getBytes()); // $$0{Number of blocks}{Number of records}
                }catch(IOException e){
                    e.printStackTrace();
                }
                reader.close();
            } catch (FileNotFoundException | XMLStreamException e) {
                e.printStackTrace();
            }
        }
}
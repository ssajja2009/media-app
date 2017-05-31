package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*; 
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.net.URL;

import utils.LogUtils;

/**
 * This is the meat of the application. MediaService is a Singleton class that is used to retrieve all the video objects.
 * A number of public and private methods are implemented to showcase the functionality of the MediaService application.
 * Key methods that return Videos with HD flag set to true and false are implemented in two modes.
 * 1. Cache all the video objects in this class. This is useful to show all video object data in a screen and in case need to more processing etc.
 *    Draw back of this: All data is cached, so takes a lot of memory
 * 2. Process one page at a time and count the number of objects in each page and returns total count for both modes.
 * 
 * PS: This program is developed keeping amount of time I need to spend in mind. A number of optimizations can be made based on real requirements.
 * I hope this demonstrates enough proficiency to take the conversations forward.
 * 
 * This class is using google's gson library for parsing JSON objects that are returned from server. 
 */
public class MediaService{

    private static MediaService service;
    private List<JsonObject> media = new ArrayList<JsonObject>(11);
    private boolean mediaSaved = false;

 

    private static final String SERVICE_URL = "http://api.viki.io/v4/videos.json?app=100250a";
    private static final String ATTR_MORE= "more";
    private static final String ATTR_RESPONSE= "response";
    private static final String ATTR_FLAGS= "flags";
    private static final String ATTR_HD= "hd";
    private static final String ATTR_ID = "id";
    
    
    /**
     * Private constructor to implement singleton patern of MediaService.
     * This takes a flag based on which this service caches the data or not.
     */
    private MediaService(boolean saveMedia){
      if(saveMedia){
        fetchAllMedia();
        mediaSaved = true;
      } else{
          mediaSaved = false;
      }
    }
    
    /**
     * Return the singleton instance of this MediaService. If the service object is not there it will create.
     */
    public static MediaService getInstance(boolean saveMedia){ 
        if(service == null ) {
            service = new MediaService(saveMedia);
        }
        return service;
    }

    /**
     * Return the count of objects for which hd flag is set to true.
     * This method implements two mechanisms a) Saving all media objects in cache and b) processing one page at a time.
     */
    public int getHDMediaCount(){
        if(mediaSaved){
         return getHDMedia().size();
        } else{
            return getMediaCount(true);
        }
    }

    /**
     * Return the count of objects for which hd flag is set to false.
     * This method implements two mechanisms a) Saving all media objects in cache and b) processing one page at a time.
     */
    public int getNonHDMediaCount(){
     if(mediaSaved){
           return getNonHDMedia().size();
     } else{
         
      return getMediaCount(false);
     }
    }


   /**
     * Return all video objects for which hd flag is set to true.
     * This method provides implementation for the case where media objects are stored locally. 
     * As demonstrated in the Count method similar approach can be used to process and return objects on the fly.
     * However if there is an use case to access all the media objects it make sense to cache these objects as all the objects need to saved to return.
     */
    public List<JsonObject> getHDMedia(){
        //TODO: Currently this method assumes Media is stored locally. Similar to the count method this can be extended to fetch content on fly, filter and send
        List<JsonObject> hdMedia = media.stream().filter( m -> m.getAsJsonObject(ATTR_FLAGS).get(ATTR_HD).getAsBoolean() == true).collect(Collectors.toList());
        return hdMedia; 
    }

    /**
     * Similar to @getHDMedia method returns all Non HD media
     */
    public List<JsonObject> getNonHDMedia(){
         //TODO: Currently this method assumes Media is stored locally. Similar to the count method this can be extended to fetch content on fly, filter and send
        List<JsonObject> nonHdMedia = media.stream().filter( m -> m.getAsJsonObject(ATTR_FLAGS).get(ATTR_HD).getAsBoolean() == false).collect(Collectors.toList());
        return nonHdMedia;
    }


 /**
  * A private method used by @getHDMediaCount and @getNonHDMediaCount methods to filter and count media objects based on the hd flag sent to this method.
  */
  private int getMediaCount(boolean hd){
      int selectedCount = 0;
         int page = 1;
         int perPage = 10;
         boolean hasMore = false;
        do{
            JsonElement payloadElen = fetchMedia(perPage, page);
            if(payloadElen.isJsonObject()){
                JsonObject payload = (JsonObject) payloadElen;
                hasMore = payload.get(ATTR_MORE).getAsBoolean();
                JsonArray mediaObjects = payload.get(ATTR_RESPONSE).getAsJsonArray();
                List<JsonElement> selectedMedia = 
                   StreamSupport.stream(mediaObjects.spliterator(), false)
                         .filter( m -> m.getAsJsonObject().getAsJsonObject(ATTR_FLAGS).get(ATTR_HD).getAsBoolean() == hd).collect(Collectors.toList());
                selectedCount += selectedMedia.size();
                LogUtils.logDebug("Completed processing response for page: " + page);
            }
        page++;
       } while(hasMore);
    return selectedCount;
  }
  

   /**
    * Retreieves all media objects and saves as member variable for further processing.
    * This is used when local caching is enabled.
    */
    private void fetchAllMedia(){
      
      int page = 1;
      int perPage = 10;
      boolean hasMore = false;
        do{
            JsonElement payloadElen = fetchMedia(perPage, page);
            if(payloadElen.isJsonObject()){
                JsonObject payload = (JsonObject) payloadElen;
                hasMore = payload.get(ATTR_MORE).getAsBoolean();
                JsonArray mediaObjects = payload.get(ATTR_RESPONSE).getAsJsonArray();
                    for (int i = 0; i < mediaObjects.size(); i++) {
                        JsonObject mediaObject = mediaObjects.get(i).getAsJsonObject();
                        LogUtils.logDebug("Retrieved media with id: " + (mediaObject.get(ATTR_ID).getAsString()));
                        media.add(mediaObject);
                    }
                page++;
            }
         } while(hasMore);
    }

   /**
    * Retreieves  media objects of a given page. 
    * 
    */
    private JsonElement fetchMedia(int pageSize, int pageNum){
      try{
        String url = new StringBuffer(SERVICE_URL).append("&").append("per_page=").append(pageSize)
                                             .append("&").append("page=").append(pageNum).toString();
        //Make sure proper encoding is set while converting as data seems to use characters from different languages                                        
        InputStreamReader reader = new InputStreamReader(new URL(url).openStream(), "UTF-8");
        JsonParser parser = new JsonParser();
        // The JsonElement is the root node. It can be an object, array, null or
        // java primitive.
        JsonElement element = parser.parse(reader);
        return element;
      } catch(Exception e){ //possible exceptions MalformedURLException, IOException, UnsupportedEncoding exception
          //TODO: Error Handling, for now logging and continueing
          LogUtils.logError(e.getMessage());
        return new JsonObject();
      }
    }

}

package main;

import service.MediaService;
/**
 * Main class that demonstrates the application.
 * This class  instantiates MediaService object and call the count methods on the service and prints those values.
 * MediaService 
 */
public class MediaApplication{

    public static void main(String[] args){

    //Create instance of MediaService that does not save all the media(video) objects in the cache.
    // This flag that is passed as false, governs that. In case memory is not issue this flag can be passed as true.
    // When cached other methods like listing of each media object etc can be used in efficient manner.
    MediaService service = MediaService.getInstance(false);

    //Retriece all video objects that has hd flag set to true. When I run all 10,000 objects have this flag true
    int hdMediaCount = service.getHDMediaCount();

   // Retriece all video objects that has hd flag set to false. None of the objects has hd flag set to true when I ran
    int nonHdMediaCount = service.getNonHDMediaCount();

    //This prints 10,000
    System.out.println(" HD Media Count=" + hdMediaCount);

    //This prints 0
    System.out.println("Non HD Media Count=" + nonHdMediaCount);

   //If required similarly print actual information of HD Media Titles and Non HD Media Titles etc.

    }
}
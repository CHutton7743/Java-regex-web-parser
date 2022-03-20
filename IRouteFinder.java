import java.util.*;

public interface IRouteFinder {
    String TRANSIT_WEB_URL = "https://www.communitytransit.org/busservice/schedules/";

    /**
     * The function returns the route URLs for a specific destination initial using the URL text
     * @param destInitial This represents a destination (e.g. b/B is initial for Bellevue, Bothell, ...)
     * @return key/value map of the routes with key is destination and
     *       value is an inner map with a pair of route ID and the route page URL
     *       (e.g. of a map element <Brier, <111, https://www.communitytransit.org/busservice/schedules/route/111>>)
     * @throws Exception
     */
    Map<String, Map<String, String>> getBusRoutesUrls(final char destInitial) throws Exception;

    /**
     * The function returns list of trip lengths in minutes, grouped by bus route and destination To/From
     * @param destinationBusesMap: key/value map of the routes with key is bus route ID and
     *                           value is the route page URL
     *                           (e.g. of a map element <111, https://www.communitytransit.org/busservice/schedules/route/111>>)
     * @return key/value map of the trips lengths in minutes with key is the route ID - destination (e.g. To Bellevue)
     *        and value is the trips lengths in minutes
     *        (e.g. of a map element <111 - To Brier, [60, 50, 40, ...]>)
     */
    Map<String, List<Long>> getBusRouteTripsLengthsInMinutesToAndFromDestination(final Map<String, String> destinationBusesMap);

}




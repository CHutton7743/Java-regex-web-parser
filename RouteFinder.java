import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalTime;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;

public class RouteFinder implements IRouteFinder {
    Scanner input = new Scanner(System.in);
    /**
     * The function returns the route URLs for a specific destination initial using the URL websiteText
     * @param destInitial This represents a destination (e.g. b/B is initial for Bellevue, Bothell, ...)
     * @return key/value map of the routes with key is destination and
     *       value is an inner map with a pair of route ID and the route page URL
     *       (e.g. of a map element <Brier, <111, https://www.communitytransit.org/busservice/schedules/route/111>>)
     */
    @Override
    public Map<String, Map<String, String>> getBusRoutesUrls(final char destInitial) throws Exception{
        char c = destInitial;
        while(isLetter(c) == false) {
            System.out.println("Please enter a valid Character.");
            c = Character.toUpperCase(input.next().charAt(0));
         }
        // String regex = "<h3>(" + Character.toUpperCase(destInitial) + ".*)</h3>";
        String regex = "<hr id=[^>]*>\\s*<h3>([^<]*)</h3>(\\s*?<div class=\"row Community\">([\\s\\S]*?</div>){3})++";

        URLConnection TRANSIT_WEB_URL = new URL("https://www.communitytransit.org/busservice/schedules/").openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(TRANSIT_WEB_URL.getInputStream())); // scrapes the whole webpage into one string
        String inputLine = "";
        String websiteText = "";
        while ((inputLine = in.readLine()) != null) { //this loop performs the scrape of the website
            websiteText += inputLine + "\n";

        }
        in.close();
        // we use pattern and matcher to run the regex statement over the website 
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(websiteText);
        matcher.groupCount();
        
        HashMap<String, Map<String, String>> map = new HashMap<>();
        
        String regexTemp = "<strong><a href=\"([^\"]*)\"[^>]*>(.*)</a></strong>";
        
        while (matcher.find()) { // here we fill in our created map with the destination and the inner map with the bus numbers and their URL links
            Pattern rowPattern = Pattern.compile(regexTemp);
            Matcher rowMatcher = rowPattern.matcher(matcher.group(0));
            HashMap<String, String> innerMap = new HashMap<>();
            if(matcher.group(1).charAt(0) == destInitial) {
                map.put(matcher.group(1), innerMap);

                while(rowMatcher.find()) {
                    innerMap.put(rowMatcher.group(2), rowMatcher.group(1));
                }
            }
        }
    
        return map;
    }

    /**
     * The function returns list of trip lengths in minutes, grouped by bus route and destination To/From
     * @param destinationBusesMap: key/value map of the routes with key is bus route ID and
     *                           value is the route page URL
     *                           (e.g. of a map element <111, https://www.communitytransit.org/busservice/schedules/route/111>>)
     * @return key/value map of the trips lengths in minutes with key is the route ID - destination (e.g. To Bellevue)
     *        and value is the trips lengths in minutes
     *        (e.g. of a map element <111 - To Brier, [60, 50, 40, ...]>)
     * @throws IOException
     * @throws MalformedURLException
     */
    @Override
    public Map<String, List<Long>> getBusRouteTripsLengthsInMinutesToAndFromDestination(final Map<String, String> destinationBusesMap) {
        HashMap<String, List<Long>> busTime = new HashMap<>();
        String grabTable = "<h2>Weekday<small>([\\s\\S]*?)</small>[\\s\\S]*?</thead>([\\s\\S]*?)</tbody>";
        String grabRow = "<tr>[\\s\\S]*?</tr>";
        String grabTime = "\\d\\d?:\\d\\d (AM|PM)";
        String inputLine = "";
        String websiteText = "";
        
        for(Map.Entry<String, String> map : destinationBusesMap.entrySet()) {
            try {
                URLConnection TRANSIT_WEB_URL = new URL("https://www.communitytransit.org/busservice" + map.getValue()).openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(TRANSIT_WEB_URL.getInputStream()));
                while ((inputLine = in.readLine()) != null) {
                    websiteText += inputLine + "\n";
                }
            } catch (Exception e) {
                System.out.println("Somethings gone wrong with the URL Connection");
                System.out.println(e);
            }
            Pattern tablePattern = Pattern.compile(grabTable);
            Matcher tableMatcher = tablePattern.matcher(websiteText);
            // find all the tables
            while(tableMatcher.find()) {
                List<Long> busTimes = new ArrayList<Long>();
                Pattern rowPattern = Pattern.compile(grabRow);
                Matcher rowMatcher = rowPattern.matcher(tableMatcher.group(2));
                String desto = map.getKey() + " " + tableMatcher.group(1);
                // find all the rows
                while(rowMatcher.find()) {
                    ArrayList<LocalTime> timeList = new ArrayList<>();
                    Pattern timePattern = Pattern.compile(grabTime);
                    Matcher timeMatcher = timePattern.matcher(rowMatcher.group(0));
                    // find all the times
                    while(timeMatcher.find()) {
                        timeList.add(LocalTime.parse(timeMatcher.group(0), DateTimeFormatter.ofPattern("h:mm a", Locale.US)));
                    }
                    LocalTime firstStop = timeList.get(0);
                    LocalTime lastStop = timeList.get(timeList.size()-1);
                    Long minutes = ChronoUnit.MINUTES.between(firstStop, lastStop);
                    busTimes.add(minutes);
                }
                
                busTime.put(desto, busTimes);
                }
                
        }
        return busTime;
    }
    // main menu
    public void printMainMenu() {
        System.out.println("_____________________________________________________________________________\n");
        System.out.println("                             ____________                                    \n");
        System.out.println("                      Welcome to my bus route program                        \n");
        System.out.println("              What letter does your destination begin with?                  \n");
        System.out.println("                             ____________                                    \n");
        System.out.println("_____________________________________________________________________________\n");
    } 
    // asking for another destination menu
    public void anotherDestination() {
        System.out.println("_____________________________________________________________________________\n");
        System.out.println("                             ____________                                    \n");
        System.out.println("          Would you like to search for another destination?                  \n");
        System.out.println("           What letter does your destination begin with?                     \n");
        System.out.println("          Type Y to continue or any other character to exit                  \n");
        System.out.println("                             ____________                                    \n");
        System.out.println("_____________________________________________________________________________\n");
    }
    // this method is designed to try and handle all the test cases that you will use to break my program
    public Boolean checkIfValidInput(String input, char destinitial) { 
        List<String> busStops = destinations(input, destinitial);
        boolean trueOrFalse = false;
        for(int i =0; i < input.length()-1;i++) {
            if (isLetter(input.charAt(i))) {
                trueOrFalse = true;
            } else {
                trueOrFalse = false;
            }
        }
        if(Character.isUpperCase(input.charAt(0))) {
            trueOrFalse = true;
        } else {
            trueOrFalse = false;
        }
        // check to see if the destination input matches any of the destinations
        for(int i = 0; i < busStops.size(); i ++) {
            if (input.equals(busStops.get(i))) {
                trueOrFalse = true;
                return trueOrFalse;
            } else {
                trueOrFalse = false;
            }
        }
        return trueOrFalse;
    }
    //checks to make sure the given char is an alphabetical letter
    public boolean isLetter(char input) {
        boolean bool = false;
        char[] alphabet = new char[26];
        alphabet[0] = 'a';
        alphabet[1] = 'b';
        alphabet[2] = 'c';
        alphabet[3] = 'd';
        alphabet[4] = 'e';
        alphabet[5] = 'f';
        alphabet[6] = 'g';
        alphabet[7] = 'h';
        alphabet[8] = 'i';
        alphabet[9] = 'j';
        alphabet[10] = 'k';
        alphabet[11] = 'l';
        alphabet[12] = 'm';
        alphabet[13] = 'n';
        alphabet[14] = 'o';
        alphabet[15] = 'p';
        alphabet[16] = 'q';
        alphabet[17] = 'r';
        alphabet[18] = 's';
        alphabet[19] = 't';
        alphabet[20] = 'u';
        alphabet[21] = 'v';
        alphabet[22] = 'w';
        alphabet[23] = 'x';
        alphabet[24] = 'y';
        alphabet[25] = 'z';
        for(int i = 0; i < alphabet.length; i ++) {
            if (input == Character.toUpperCase(alphabet[i]) || input == alphabet[i]) {
                bool = true;
            }
        }
        return bool;
    }
    // This method should check to see if the given destination is a valid destination by scraping the website for valid bus stop destionations using regex expressions
    public List<String> destinations(String input, char destinitial) {
        String websiteText = "";
        String inputLine = "";
        String grabCommunities = "<ul id=\"Communities\" class=\"row\">[\\s\\S]*?</ul>";
        String grabCities = "<a href=\"#[\\s\\S]*?>("+destinitial+".*)</a></li>";
        List<String> busStops = new ArrayList<>();
        try {
            URLConnection TRANSIT_WEB_URL = new URL("https://www.communitytransit.org/busservice/schedules/").openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(TRANSIT_WEB_URL.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                websiteText += inputLine + "\n";
            }
        } catch (Exception e) {
            System.out.println("Somethings gone wrong with the URL Connection");
            System.out.println(e);
            Pattern communityPattern = Pattern.compile(grabCommunities);
            Matcher communityMatcher = communityPattern.matcher(websiteText);
            
            while(communityMatcher.find()) {
                Pattern citiesPattern = Pattern.compile(grabCities);
                Matcher citiesMatcher = citiesPattern.matcher(communityMatcher.group(0));
                for(int i = 0; i <busStops.size();i++) {
                    busStops.add(citiesMatcher.group(1));
                }
            }
        }
        return busStops;
    }
}

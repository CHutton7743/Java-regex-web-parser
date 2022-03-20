import java.io.IOException;
import java.util.*;

public class Client {
   public static void main(String[] args) throws Exception, IOException {
       final RouteFinder route = new RouteFinder();
       Scanner input = new Scanner(System.in);
       boolean ans = true;
       route.printMainMenu();
       while(ans) {
           String c = input.next();
           char destinitial = c.charAt(0);

           // check to see if a valid initial character
           while(route.isLetter(destinitial) == false) {
               System.out.println("Please enter a valid Character.");
               destinitial = input.next().charAt(0);
            }
           final Map<String, Map<String, String>> busRoutesMap = route.getBusRoutesUrls(Character.toUpperCase(destinitial));
           // this loop will sort out and print the bus destinations and the bus numbers
           for(Map.Entry<String, Map<String, String>> busRoute : busRoutesMap.entrySet()) {
               System.out.println("Destination: " + busRoute.getKey());
               for(Map.Entry<String, String> bus : busRoute.getValue().entrySet()) {
                   System.out.println("Bus: " + bus.getKey());
                }
                System.out.println("++++++++++++++++++++++++++++++++++++++");
           }
           System.out.println();
           System.out.println("Please input your destination: ");
           String destination = input.next();
           // checking to see if the input is a valid destination
           while (route.checkIfValidInput(destination, destinitial) == false) {
               System.out.println("Please enter a valid destination!");
               destination = input.next();
           }
           // the purpose of this loop is to retrieve the proper map to pass into the second function
           for (Map.Entry<String, Map<String, String>> entry : busRoutesMap.entrySet()) {  
                String desto = entry.getKey();
                if (desto.equals(destination)) {
                    Map<String, List<Long>> busMap = route.getBusRouteTripsLengthsInMinutesToAndFromDestination(entry.getValue());
                    for(String busString : busMap.keySet()) {
                        System.out.print(busString);
                        System.out.println(busMap.get(busString));
                        System.out.println();
                    }
                } else {
                    continue;
                }    
           }    
           System.out.println();
           route.anotherDestination();
           String reply = input.next();
           if (reply.length() > 1) {
               break;
           }
           if (Character.toUpperCase(reply.charAt(0)) == 'Y') {
               ans = true;
               route.printMainMenu();
           } else {
               ans = false;
           }
       }
       input.close();
    }
}

package is.hi.controller;

import is.hi.model.*;
import is.hi.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;

import static com.sun.deploy.config.JREInfo.getAll;

/**
 * TODO-Dilja einn eða fleiri controller? Think about it
 * @author Diljá, Ólöf, Sandra og Kristín
 * @date september 2017
 * HBV501G Hugbúnaðarverkefni 1
 * Háskóli Íslands
 *
 * Tekur við skipunum frá vefviðmóti til að notandi geti loggað sig inn
 * búið til nýtt travel plan og bætt í travel planið
 */
@Controller
public class showCampController {

    @Autowired
    campSiteService CampsiteService;
    @Autowired
    UserService userService;
    @Autowired
    TravelPlanService travelplanService;
    @Autowired
    AlternativeService alternativeService;

    // @Autowired
    //campsiteRepository campRep;
    // @Autowired
    // travelPlanRepository travelPlanRep;
    // @Autowired
    // userRepository travellerRep;
    // @Autowired
    // travelPlanItemRepository travelPlanItemRep;

    ArrayList<TravelPlanItem> tpiList;
    ArrayList<TravelPlan> tpList;
    ArrayList<Camp> cList;
    String user;

    /**
     * @return skilar forsíðu
     */
    @RequestMapping("/forsida")
    public String forsida(){
        return "forsida";
    }

    /**
     * TODO setja í service
     * @param model
     * @return vefsíðu sem sýnir lista af öllum tjaldsvæðum
     */
    @RequestMapping(value = "/listofcamps", method = RequestMethod.GET)
    public String listCamps(Model model) {
        ArrayList<Camp> cList;
        cList = CampsiteService.getCampsites();
        model.addAttribute("camps", cList);
        return "allCampsites";
    }


    /**
     *
     * @return vefsíðu sem gerir notenda kleift að stofa nýjan aðgang
     */
    @RequestMapping("/newAccountSite")
    public String newAccountSite(){
        return "newAccountSite";
    }

    /**
     *
     * @return
     */

    @RequestMapping("/accountInfo")
    public String accountInfo(){
        return "accountInfo";
    }

    /**
     *
     * vefsíða þar sem notandi getur stofnað nýjan aðgang
     * @param username
     * @param email
     * @param pw1
     * @param pw2
     * @return býr til nýjan aðgang
     */
    @RequestMapping(value = "/newAccount", method = RequestMethod.POST)
    public String newAccount(@RequestParam(value="username")
                                     String username, @RequestParam(value="email") String email,
                             @RequestParam(value="pw1") String pw1,
                             @RequestParam(value="pw2") String pw2)
    {   boolean doesExist = userService.doesUserExist(username, email);
        boolean PWidentical = userService.arePWidentical(pw1, pw2);
        if (doesExist)
            return "newAccountSite";
        if (PWidentical){
            userService.newLoginUser(username, email, pw1);
            return "/accountInfo";

        }
        return "newAccount";
    }
    /*
        /**
         *
         * Vefsíða sem byður notanda að logga sig inn og tjékkar hvort notandi sé til
         * @param uname
         * @param psw
         * @param model
         * @return annaðhvort forsíðu eða notendasíðu eftir því hvort aðgangurinn er til
         */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String synaNotanda(@RequestParam(value = "uname") String nafn, @RequestParam(value = "psw") String lykilorð, Model model ) {
        if (userService.isPwCorr(nafn, lykilorð)) {
            //TODO tengja við service til að birta Camp og TravelPlan
            ArrayList<Camp> cList;


            cList = CampsiteService.getCampsites();


            model.addAttribute("camps", cList);

            ArrayList<TravelPlan> tpList;

            tpList = travelplanService.getTravelplans();


            model.addAttribute("travelplans", tpList);
            if(userService.hasAdminAuthority(nafn, lykilorð)){
                return "adminLoginSite";
            }

            return "notendasida";
        } else {
            return "forsida";
        }
    }

    /**
     *
     * @return
     */
    @RequestMapping(value="newTravelPlan", method = RequestMethod.GET)
    public String newTravelPlan(){
        return "newTravelPlan";
    }

    /**
     * TODO setja lógík í service
     * @param model
     * @return
     */
    @RequestMapping(value = "/addTravel", method = RequestMethod.POST)
    public String addTravel(Model model){
        try{
            tpList = travelplanService.getTravelplans();
        }catch (Exception e){
            System.out.println(e + " getTravelplans in /addTravel");
        }


        model.addAttribute("travelplans", tpList);
        return "notendasida";
    }

    /**
     *
     * vefsíða sem gerir notenda kleift að búa til nýtt travelplan
     * @param username
     * @param planName
     * @param model
     * @return fer aftur á notendasíðu
     */
    @RequestMapping(value = "/newTravel", method = RequestMethod.POST)
    public String newTravel(@RequestParam(value="username")
                                    String username, @RequestParam(value="planName") String planName, Model model)
    {   try {
        travelplanService.createTravelplan(planName, username);
        tpList = travelplanService.getTravelplans();
        model.addAttribute("travelplans", tpList);
    } catch (Exception e){
        System.out.println("newTravel error");
    }

        return "notendasida";
    }

    /**
     *
     * TODO laga svo virki
     * Vefsíða þar sem notandi getur bætt travelitem við travel planið sitt
     * //@param campname
     *
     * //@param date
     * @param nights
     * @param model
     * @return
     */
    @RequestMapping(value = "/addTravelitem", method = RequestMethod.POST)
    public String addTravelItem(@RequestParam(value="date") String date,
                            @RequestParam(value="nights") int nights,
                            @RequestParam(value="travel") String travel, Model model
    )
    {
        //TODO vantar planname til að tengja við
        Date realDate;
        realDate = alternativeService.dateMaker(date);
        TravelPlanItem travelplanItem = new TravelPlanItem(5, realDate, realDate, 1000, nights);
        travelplanService.addItemtoPlan(travel, travelplanItem);
        tpiList = new ArrayList<TravelPlanItem>();
        try{

            tpiList.add(travelplanItem);

        }catch (Exception e){
            System.out.println("addTravelItem error" +" "+e);
        }
        ArrayList<Camp> cList;
        cList = CampsiteService.getCampsites();
        model.addAttribute("camps", cList);

        //model.addAttribute("travelplanItems", tpiList);
        tpList = travelplanService.getTravelplans();

        model.addAttribute("travelplans", tpList);

        return "notendasida";
    }

    /**
     *
     * @return
     */
    @RequestMapping(value="/UserReviews", method = RequestMethod.GET)
    public String checkNavi(){

        return "UserReviews";
    }
    /**
     * @return Vefsíða með upplýsingum um tjaldsvæði
     */
    @RequestMapping("/campInfo")
    public String campInfo() {
        return "campInfo";
    }
    /**
     * Sækir öll tjalddsvæði og flokkar eftir landshluta.
     *
     * @param model
     * @param area
     * @return skilar síðu þar sem hægt er að sjá tjaldsvæðin.
     */
    @RequestMapping(value = "/showCamps", method = RequestMethod.POST)
    public String showCamps(Model model,
                            @RequestParam(value = "area") String area) {

        cList = CampsiteService.getCampsites();
        ArrayList<Camp> cList2 = new ArrayList<Camp>();

        for (Camp c : cList) {
            if (c.getCamparea().equals(area)) {
                cList2.add(c);
            }
            model.addAttribute("camps", cList2);
        }
        if (area.equals("All"))
            model.addAttribute("camps", cList);

        return "allCampsites";
    }

    /**
     * Sækir upplýsingar um tjaldsvæði
     *
     * @param campName
     * @param model
     * @return skilar síðu þar sem hægt er að skoða upplýsingar um tjaldsvæði
     */

    @RequestMapping(value = "/getInfo", method = RequestMethod.POST)
    public String getInfo(@RequestParam(value = "campName") String campName, Model model) {
        //TODO thurfum ekki oll campsites
        Camp camp = CampsiteService.getOneCamp(campName);
        ArrayList<Review> rList = userService.getReviews(campName);

        // ArrayList<AverageRating> aList = new ArrayList<AverageRating>();
        double rate = userService.getRating(campName);
        model.addAttribute("camp", camp);
        model.addAttribute("reviews", rList);
        model.addAttribute("rate", rate);



        return "campInfo";
    }

    /**
     * @return skilar síðu þar sem hægt er að skrifa ummæli.
     */

    @RequestMapping(value = "giveReview")
    public String giveReview() {
        return "giveReview";
    }

    /**
     * finnur út hvaða tjaldsvæði notandi vill gefa ummæli.
     *
     * @param campName
     * @param model
     * @return skilar síðu þar sem hægt er að skrifa ummæli
     */

    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public String review(@RequestParam(value = "campName") String campName, Model model) {


        for (Camp c : cList) {
            if (c.getCampname().equals(campName))
                model.addAttribute("camp", c);
        }

        return "giveReview";
    }

    /**
     * Vefsíða sem sýnir upplýsingar um tjaldsvæði
     *
     * @param myReview
     * @param campName
     * @param model
     * @return skilar síðu með upplýsingu
     */
/*
    @RequestMapping(value = "/postReview", method = RequestMethod.POST)
    public String postReview(@RequestParam(value = "myReview") String myReview,
                             @RequestParam(value = "campName") String campName, Model model) {
        ArrayList<Review> rList = new ArrayList<Review>();
        Review review = new Review(myReview, user);
        for (Camp c : cList) {
            if (c.getCampmame().equals(campName))
                rList = c.getReviews();
            rList.add(review);
            c.setReviews(rList);
            rList = c.getReviews();
            model.addAttribute("camp", c);
            model.addAttribute("reviews", rList);
        }

        return "campInfo";
    }

    @RequestMapping(value = "giveRating", method = RequestMethod.POST)
    public String giveRating(@RequestParam(value = "rating") int rating,
                             @RequestParam(value = "campName2") String campName, Model model) {

        userService.setRating(rating, campName);
        //ArrayList<Review> rev = userService.getReviews();
        double rate;
        for (Camp c : cList) {
            if (c.getCampname().equals(campName)) {
                aList = c.getRatings();
                aList.add(rating1);
                c.setRatings(aList);
                int i = 0;
                for (AverageRating a : aList) {
                    i = i + a.getVoted();
                }

                rate = i / aList.size();
                model.addAttribute("camp", c);
                model.addAttribute("rate", rate);
                model.addAttribute("reviews", c.getReviews());
            }
        }
        return "campInfo";
    }*/

}

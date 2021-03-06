package is.hi.repository;

import is.hi.model.TravelPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Diljá, Ólöf, Sandra og Kristín
 * @date september 2017
 * HBV501G Hugbúnaðarverkefni 1
 * Háskóli Íslands
 *
 * Repository for travelplanitems
 */
//TODO ætti þetta að vera Long??

public interface travelPlanItemRepository extends JpaRepository<TravelPlanItem, Long>{
    /**
     * gets all travelplanitems
     * @return a list of travelplanitems
     */
    @Query(value ="SELECT a FROM TravelPlanItem a")
    List<TravelPlanItem> getAll();

    /**
     * Adss travelplanitems
     * @param travelplanItem
     */
    //void add(TravelPlanItem travelplanItem);
}

@Dao
public interface UserDao {
	@Query("SELECT setDataValues FROM setDataTable WHERE setID = ")
	//ArrayList<Int> getSetData(int workoutID);

	@Query("SELECT setID FROM setDataTable ORDER BY setID DESC")
	int getHighestID();
}

import java.lang.Object

@Entity(tableName="WorkoutDataTable")
public class WorkoutData{
	//unique incrementing
	@primaryKey
	private int workoutID;

	//Kind of hackish solution, just to allow us to put our names down
	private String userName;

	//this class is a wrapper for timestamp SQL recognizes
	private Timestamp workoutTimeStamp;

	public workoutData(int currWorkoutID,int currSet, int ){
		//set Timestamp to curr time
		//set id to be the increment of last ID in table
	}
}

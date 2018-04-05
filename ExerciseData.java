import java.lang.Object

@Entity(tableName="ExerciseDataTable")
public class ExerciseData{
	//unique incrementing
	@primaryKey
	private int exerciseID;

	//ID of the exercise this is a component of, e.g. dumbbell flys
	@foreignKey
	private int workoutID;

	//this class is a wrapper for timestamp SQL recognizes
	private Timestamp exerciseTimeStamp;

	//1st set, 2nd set, etc.
	private String muscleGroup;

	//name of exercise
	private String exerciseName;

	public exerciseData(int currWorkoutID,int currSet, int ){
		//set Timestamp to curr time
		//set id to be the increment of last ID in table
	}
}

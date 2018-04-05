import java.lang.Object

@Entity(tableName="SetDataTable")
public class SetData{
	//unique incrementing
	@primaryKey
	private int setID;

	//ID of the exercise this is a component of, e.g. dumbbell flys
	@foreignKey
	private int exerciseID;

	//this class is a wrapper for timestamp SQL recognizes
	private Timestamp setTimeStamp;

	//1st set, 2nd set, etc.
	private int setNumber;

	//amount of weight lifted
	private int weight;

	//holds the data values
	private ArrayList<Integer> setDataValue;

	public setData(int currExerciseID,int currSet, int ){
		//set Timestamp to curr time
		//set id to be the increment of last ID in table
	}
}
